package com.longshine.cams.fk.server;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;

import com.longshine.cams.fk.common.BaseManager;
import com.longshine.cams.fk.common.BaseThread;
import com.longshine.cams.fk.structs.FKTask;
import com.longshine.cams.fk.structs.InterfaceProperty;
import com.longshine.cams.fk.structs.TaskAttribute;

public class FKTaskParseManager extends BaseManager{
	private static Log logger = LogFactory.getLog(PROC_FKTaskArchieve.class);
	private static void debug(Object v){if(logger.isDebugEnabled())	logger.debug(v);}
	@SuppressWarnings("unused")
	private static void info(Object v){if(logger.isInfoEnabled())	logger.info(v);}
	private static void warn(Object v){if(logger.isWarnEnabled())	logger.warn(v);}
	// 定义单例对象的实例变量和方法
	private static FKTaskParseManager instance;
	// 对外共享的方法
	private FKConfiguration config;
	// 管理任务解析工厂
	private TaskParseFactory parse_factory;
	// 保存对FKServer的对象引用
	private FKServer fkserver;
	private ApplicationContext appContext;
	// 保存对FKTaskManager的对象引用
	private FKTaskManager task_mgr;
	// 任务回送/数据发送线程池
	private List<PROC_FKTaskSend> proc_sends;
	// 数据源定义
	private DataSource datasource;

	private FKTaskParseManager(){}
	public static FKTaskParseManager getInstance(FKConfiguration v_config){
		if(instance == null){
			synchronized(FKTaskParseManager.class){
				if(instance == null){
					instance = new FKTaskParseManager();
					instance.config = v_config;
					instance.Initialized();
				}
			}
		}
		return instance;
	}
	public TaskParseFactory getParsefactory() {
		return parse_factory;
	}
	public Log getLog() {
		return logger;
	}
	public static FKTaskParseManager getInstance(){
		return instance;
	}
	public FKConfiguration getConfig() {
		return config;
	}
	public ApplicationContext getApplicationContext() {
		return appContext;
	}
	// 装载配置参数，并初始化任务数据校验管理器、任务管理器、任务执行管理器。
	private void Initialized(){
		parse_factory = TaskParseFactory.getInstance(config);
		proc_sends = new ArrayList<PROC_FKTaskSend>();
	}
	/**任务解析管理器工作准备
	 * 
	 */
	public void initializeManager(){
		fkserver = FKServer.getInstance();
		this.appContext = fkserver.getApplicationContext();
		task_mgr = FKTaskManager.getInstance();
		datasource = this.appContext.getBean("datasource_gdcams2",DataSource.class);
		parse_factory.setDatasource(datasource);
		initializeTaskSendProc();
	}
	private void initializeTaskSendProc(){
		long proc_num = config.getPropertyLong(FKConfigureKeys.CAMS_FK_SYSTEM_CALLBACKPROCESS_KEY);
		PROC_FKTaskSend proc;
		if(proc_num < 1)
			proc_num = 1;
		for(long i = 0; i < proc_num; i++){
			proc = new PROC_FKTaskSend(config.getPropertyLong(FKConfigureKeys.CAMS_FK_SYSTEM_IDLE_KEY));
			proc.setManagers(this.config, this, this.task_mgr);
			proc_sends.add(proc);
		}
	}
	// 任务归档线程启动
	public void startProc(){
		// 启动所有线程;
		for(PROC_FKTaskSend proc:proc_sends){
			proc.start();
		}
	}
	// 设置所有线程退出条件
	public void setProcExit(){
		for(PROC_FKTaskSend proc:proc_sends){
			proc.setExit();
		}
	}
	// 等待管理线程退出
	public void joinProc(){
		// 等待所有线程结束
		try{
			for(PROC_FKTaskSend proc:proc_sends){
				proc.join();
			}
		}catch(InterruptedException e){
			warn("proc_task_archieve stop exception:" + e);
		}
	}
	public long TaskParse(FKTask v_task){
		return this.TaskParse(v_task, 0, null);
	}
	/**执行任务，该过程主要是供接口交互模块调用，开始执行任务，逻辑如下：
	 * 1、根据任务的类型，TASKTYPE为阻塞型还是非阻塞型任务，是否启用阻塞超时检查
	 * 2、将任务添加到任务管理器中，供任务执行管理器执行任务过程
	 * 3、根据任务执行的结果返回错误状态给调用方，0-代表执行成功
	 * @param v_task 待解析的任务引用
	 * @param v_acc_errcode 当解析的任务超时时，使用的超时错误代码
	 * @param v_acc_errstr 当解析的任务超时时，使用的超时错误信息
	 * @return 0 : 执行成功，-1 : 任务添加失败。3 : 执行超时，4 : 执行完成
	 */
	public long TaskParse(FKTask v_task,final long v_acc_errcode,final String v_acc_errstr){
		long ret = 0;
		FKTask task = null;
		debug("Begin TaskAdd. Task:" + v_task);
		if(!(task_mgr.TaskAdd(v_task))){
			warn("TaskAdd Failed.Task:" + v_task);
			ret = -1;
		}
		if(ret == 0){
			if(v_task.TASKTYPE == TaskAttribute.TaskType.block.getValue()){
				InterfaceProperty prop = this.config.getInterfaceProperty(v_task.function);
				long oversecs_delay = this.config.getPropertyLong(FKConfigureKeys.CAMS_FK_SYSTEM_OVERSECSDELAY_KEY);
				long date_oversecs = v_task.INTIME.getTime() + prop.oversecs * 1000 + oversecs_delay * 1000;
				long idle_millis = this.config.getPropertyLong(FKConfigureKeys.CAMS_FK_SYSTEM_IDLE_KEY);
				try{
					while(date_oversecs > System.currentTimeMillis()){
						task = task_mgr.TaskBlockCheckOrOversec(v_task.TASKID);
						if(task != null){	// 已经执行完成返回
							ret = task.STATUS;
							break;
						}
						// 如果未执行完成，等待一会儿后，继续检查执行任务的状态
						BaseThread.sleep(idle_millis);
					}
				}catch(Exception e){
					warn("Task Parse Perform Exception:" + e);
				}
				if(task == null){	// 说明任务超时未完成，因此需要进行阻塞超时未完成处理
					logger.info("说明任务超时未完成，因此需要进行阻塞超时未完成处理...gsn");
					task = task_mgr.TaskBlockCheckOrOversec(v_task.TASKID,v_acc_errcode,v_acc_errstr);
					if(task != null){
						ret = task.STATUS;
					}
				}
			}else{	// 非阻塞任务，添加到执行任务队列后，返回接收成功信息给调用方
				// 以后可以添加附加代码进行其他处理，暂时无需处理
			}
		}
		return ret;
	}
}
