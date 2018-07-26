package com.longshine.cams.fk.server;

import java.net.InetSocketAddress;
import java.util.Hashtable;
import java.util.Map;

import com.longshine.cams.fk.common.BaseThread;
import com.longshine.cams.fk.structs.StringWrapper;

public class PROC_TaskServer extends BaseThread {
	// 保留FKTaskPerformManager的引用
//	private FKTaskPerformManager perform_mgr;
	// 保留FKConfiguration的引用
	private FKConfiguration config;
	// 保留后台任务服务的服务地址信息
	private InetSocketAddress taskserver_addr = null;
	// 使用队列管理所有请求TaskServer的费控服务Socket对象
	private Map<String,TaskServerSocket> taskmap;

	public PROC_TaskServer(long v_idle_sleep) {
		super(v_idle_sleep);
		this.setThreadName("TaskServer Thread:" + this.getId());
		// TODO Auto-generated constructor stub
	}
	@Override
	protected boolean dowork() {
		// TODO Auto-generated method stub
		return this.recvTaskServeData();
	}
	@Override
	protected boolean checkup() {
		// TODO Auto-generated method stub
		return super.checkup();
	}
	public void setManagers(FKConfiguration v_config,FKTaskPerformManager v_perform_mgr){
		this.config = v_config;
//		this.perform_mgr = v_perform_mgr;
		this.enableCheckup();
		this.taskmap = new Hashtable<String,TaskServerSocket>();
		try{
			String taskserver = this.config.getProperty(FKConfigureKeys.CAMS_FK_TASK_TASKSERVER_ADDR_KEY);
			String host = taskserver.split(":")[0];
			int port = Integer.parseInt(taskserver.split(":")[1]);
			this.taskserver_addr = new InetSocketAddress(host, port);
		}catch(Exception e){}
	}
	public InetSocketAddress getTaskserver_addr() {
		return taskserver_addr;
	}
	public void setTaskserver_addr(InetSocketAddress taskserver_addr) {
		this.taskserver_addr = taskserver_addr;
	}
	public boolean setTaskServerAddr(String v_addr){
		try{
			String host = v_addr.split(":")[0];
			int port = Integer.parseInt(v_addr.split(":")[1]);
			this.taskserver_addr = new InetSocketAddress(host, port);
		}catch(Exception e){
			return false;
		}
		return true;
	}
	/**创建一个新的连接将费控指令发送给任务服务转发到终端
	 * @param v_xml	待发送的任务指令XML报文字符串
	 * @param v_cmd_id 本次指令发送的指令标识，要求唯一
	 * @return -1 如果连接失败等服务不可用
	 *         -2 其他异常返回
	 *         -3 重复的任务标识返回
	 *          0 发送成功返回
	 */
	public int sendTaskCommand(String v_cmd_id, String v_xml){
		int ret = 0;
		int temp_int;
		TaskServerSocket client = null;
		if(this.taskmap.containsKey(v_cmd_id)){
			ret = -3;
			return ret;
		}
		try{
			client = new TaskServerSocket(v_cmd_id);
			temp_int = client.connectServer(this.taskserver_addr);
			if(temp_int < 0){
				ret = -1;
				return ret;
			}
			temp_int = client.sendData(v_xml);
			if(temp_int == -1)
				ret = -1;
			else if(temp_int < 0)
				ret = -2;
			else{	// 发送成功
				this.taskmap.put(v_cmd_id, client);		// 加入到接收监控MAP队列
				ret = 0;
			}
		}catch(Exception e){
			logger.warn("sendTaskCommand Exception:" + e);
			ret = -2;
			this.releaseResource(v_cmd_id);
		}
		return ret;
	}
	/**根据提交给TaskServer的任务标识，查找任务是否已经从TaskServer执行完成，返回了结果XML内容体
	 * @param v_cmd_id 待检查的任务标识
	 * @param v_result TaskServer返回的执行结果XML报文封套，返回给调用方
	 * @return 0 执行完成，返回的结果在v_result封套中
	 *         1 没有执行完成，没有有效的XML完整数据报文
	 *         -1 异常原因，导致资源释放，本次任务执行失败
	 *         -2 待检查的任务标识不存在后台服务执行对象
	 */
	public int checkTaskResult(String v_cmd_id, StringWrapper v_result){
		int ret = 0;
		try{
			if(this.taskmap.containsKey(v_cmd_id)){	// 查找到任务执行对象
				TaskServerSocket client = this.taskmap.get(v_cmd_id);
				if(client.isHasCompleteResp()){	// 有收到返回完成的XML应答报文体
					v_result.str = client.pickupRecievePackage();
					if(v_result.str != null)
						ret = 0;
					else
						ret = 1;	// 没有执行完成，没有有效的XML完整数据报文
				}else{
					if(!client.isSocketLived()){	// socket资源已经释放，返回资源释放的错误
						ret = -1;
					}else{
						ret = 1;	// 没有执行完成，没有有效的XML完整数据报文
					}
				}
			}else
				ret = -2;	// 待检查的任务标识不存在
		}catch(Exception e){
			logger.warn("checkTaskResult Exception:" + e);
			ret = -1;
		}
		return ret;
	}
	private boolean recvTaskServeData(){
		boolean ret = false;
		int temp_int;
		try{
			for(String task_key:this.taskmap.keySet()){
				TaskServerSocket client = this.taskmap.get(task_key);
				temp_int = client.recvData();
				if(temp_int < 0){	// 接收异常，需要关闭连接资源
					System.out.println("Socket Recieve Error(" + temp_int + ") begin release Resource.");
					client.releaseResouce();
				}
				else if((temp_int == 0) || (temp_int == 1))	// 接收正常
					ret = true;
			}
		}catch(Exception e){
			logger.warn("recvTaskServeData Exception:" + e);
		}
		return ret;
	}
	/**根据发送给TaskServer的命令标识，将命令承载的TaskServerSocket进行释放处理，逻辑如下：
	 * 1、在taskmap中查找TaskServerSocket对象
	 * 2、找到后，执行该对象的releaseResouce()操作
	 * 3、将该对象从taskmap中移除
	 * @param v_cmd_id 待查找的TaskServer命令标识
	 */
	public void releaseResource(String v_cmd_id){
		try{
			if((v_cmd_id != null) && (!"".equals(v_cmd_id))){
				TaskServerSocket client = this.taskmap.get(v_cmd_id);
				if(client != null){
					client.releaseResouce();
					this.taskmap.remove(v_cmd_id);
				}
			}
		}catch(Exception e){
			logger.warn("Release Resource Exception:" + e);
		}
	}
}
