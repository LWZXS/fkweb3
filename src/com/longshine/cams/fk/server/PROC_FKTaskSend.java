package com.longshine.cams.fk.server;

import com.longshine.cams.fk.common.BaseTaskParse;
import com.longshine.cams.fk.common.BaseThread;
import com.longshine.cams.fk.structs.FKTask;
import com.longshine.cams.fk.structs.TaskAttribute;

public class PROC_FKTaskSend extends BaseThread {
	// 配置对象
	private FKConfiguration config;
	// 任务管理对象引用
	private FKTaskManager task_mgr = null;
	// 任务解析管理对象
	private FKTaskParseManager parse_mgr = null;
	// 接口交互的任务解析对象
//	private BaseTaskParse parser;
	// 费控服务异步请求回调接口地址、主动调用对端服务的接口服务地址配置
	private String server_url;

	public PROC_FKTaskSend(long v_idle_sleep) {
		super(v_idle_sleep);
		this.setThreadName("FKTaskSend Thread:" + this.getId());
		// TODO Auto-generated constructor stub
	}
	/**每次从完成队列中获取一个任务执行
	 * @return 获取到任务后，执行完成后，返回true，否则返回false
	 */
	@Override
	protected boolean dowork() {
		// TODO Auto-generated method stub
		boolean ret = true;
		try{
			FKTask task = this.task_mgr.TaskSendDispatch();
			if(task == null){
				ret = false;
				return ret;
			}
			logger.debug("[PROC_FKTaskSend]get one task:" + task);
			ret = this.CallService(task);
		}catch(Exception e){
			logger.warn("PROC_FKTaskSend dowork Exception:" + e);
		}
		return ret;
	}
	public void setManagers(FKConfiguration v_config,FKTaskParseManager v_parsemgr,FKTaskManager v_taskmgr){
		this.config = v_config;
		this.task_mgr = v_taskmgr;
		this.parse_mgr = v_parsemgr;
		this.server_url = this.config.getProperty(FKConfigureKeys.CAMS_FK_TASK_CALLSERVER_ADDR_KEY);
	}
	private boolean CallService(FKTask v_task){
		boolean ret = true;
		BaseTaskParse parser;
		logger.debug("[PROC_FKTaskSend]begin CallService of FKTask:" + v_task);
		parser = this.parse_mgr.getParsefactory().getTaskParser(v_task.function);
		if(parser == null){
			logger.warn("Call interface(" + v_task.function + ") has no class_parser");
			ret = false;
			return ret;
		}
		logger.debug("[PROC_FKTaskSend]begin CallService(" + this.server_url + ") of FKTask:" + v_task);
		ret = parser.Callservice(this.combineServerURL(this.server_url, v_task), v_task);
		logger.debug("[PROC_FKTaskSend]begin CallService(" + this.server_url + ") Result(" + ret + ") of FKTask:" + v_task);
		if(ret)
			v_task.RESP_STATUS = TaskAttribute.SendingStatus.sended.getValue();
		else
			v_task.RESP_STATUS = TaskAttribute.SendingStatus.senderror.getValue();
		logger.debug("[PROC_FKTaskSend]begin Archieve of FKTask(response code:" + v_task.RESP_ERRCODE + "):" + v_task);
		task_mgr.TaskArchieve(v_task.TASKID);
		return ret;
	}
	public String combineServerURL(String v_preurl, FKTask v_task){
		String str = v_preurl;
		while(str.endsWith("/"))
			str = str.substring(0, str.length() - 1);
		while(str.endsWith("\\"))
			str = str.substring(0, str.length() - 1);
		return str + "/" + v_task.CALL_FUNC;
	}
}
