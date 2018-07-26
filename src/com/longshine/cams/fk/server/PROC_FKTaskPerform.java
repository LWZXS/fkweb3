package com.longshine.cams.fk.server;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.longshine.cams.fk.common.BaseTaskPerform;
import com.longshine.cams.fk.common.BaseThread;
import com.longshine.cams.fk.common.TaskPerformMultiMode;
import com.longshine.cams.fk.structs.FKTask;
import com.longshine.cams.fk.structs.FKTaskMX;
import com.longshine.cams.fk.structs.ServiceError;
import com.longshine.cams.fk.structs.StringWrapper;
import com.longshine.cams.fk.structs.TaskAttribute;
import com.longshine.cams.fk.structs.TaskServerJob;
import com.longshine.cams.fk.structs.TaskServerJobs;

public class PROC_FKTaskPerform extends BaseThread {
	// 任务管理对象引用
	private FKTaskManager task_mgr = null;
	// 任务解析管理对象
	private FKTaskPerformManager perform_mgr = null;
	// 配置对象
	private FKConfiguration config;
	// 后台TaskServer通信管理线程
	private PROC_TaskServer proc_taskserver;
	// 系统通用接口超时时间同步延时，单位：秒
	protected Long oversecsdelay;
	// 保存本线程处理的FKTask任务清单
	private List<FKTask> tasklist;

	public PROC_FKTaskPerform(long v_idle_sleep,PROC_TaskServer v_taskserver) {
		super(v_idle_sleep);
		this.setThreadName("FKTaskPerform Thread:" + this.getId());
		// TODO Auto-generated constructor stub
		this.proc_taskserver = v_taskserver;
		this.tasklist = new LinkedList<FKTask>();
	}
	@Override
	protected boolean dowork() {
		// TODO Auto-generated method stub
		boolean ret = false;
		boolean ret1;
		// 获取新任务
		ret1 = this.getNewTask();
		if(ret1)			ret = true;
		// 循环所有任务发送后台JOB
		ret1 = this.TaskServerJobSend();
		if(ret1)			ret = true;
		// 循环所有任务设置后台JOB返回内容
		ret1 = this.TaskServerResultCheck();
		if(ret1)			ret = true;
		// 循环所有任务回调业务执行类
		ret1 = this.TaskPerformerCall();
		if(ret1)			ret = true;
		// 循环所有任务，将完成的任务进行完成处理
		ret1 = this.TaskComplete();
		if(ret1)			ret = true;
		return ret;
	}
	public void setManagers(FKConfiguration v_config,FKTaskPerformManager v_perform_mgr,FKTaskManager v_task_mgr){
		this.config = v_config;
		this.task_mgr = v_task_mgr;
		this.perform_mgr = v_perform_mgr;
		this.oversecsdelay = config.getPropertyLong(FKConfigureKeys.CAMS_FK_SYSTEM_OVERSECSDELAY_KEY);
	}
	// 获取新任务
	private boolean getNewTask(){
		boolean ret = false;
		FKTask task = null;
		int temp_int;
		BaseTaskPerform performer = null;
		int num_max = this.config.getPropertyInteger(FKConfigureKeys.CAMS_FK_SYSTEM_PERFORM_TASKS_MAX_KEY);
		int tasks_num = this.tasklist.size();
		if(tasks_num < num_max){	// 需要获取任务
			task = this.task_mgr.TaskDispatch();
		}
		if(task != null){	// 获取到一个新任务
			ret = true;
			logger.debug(this.getThreadName() + "[getNewTask]get a new FKTask:" + task);
			performer = perform_mgr.getPerform_factory().getTaskPerformer(task.function);
			if(performer == null){
				logger.warn("[getNewTask]Call interface(" + task.function + ") has no class_perform, Task is " + task);
				// 将任务设置为结束，提示返回错误，设置所有明细为：SYS_NOPERFORMER
				for(FKTaskMX taskmx:task.mxlist){
					taskmx.setErrorCodeMsg(ServiceError.SYS_NOPERFORMER, ServiceError.SYS_NOPERFORMER_MSG);
				}
				this.task_mgr.TaskComplete(task.TASKID, TaskAttribute.TaskStatus.perform_error);
				task = null;
				return ret;
			}else{
				if(TaskPerformMultiMode.class.isAssignableFrom(performer.getClass())){
					task.setPerformMode(TaskAttribute.TaskPerformMode.multimode);
				}else{
					task.setPerformMode(TaskAttribute.TaskPerformMode.singlemode);
				}
				performer.setProcTaskperform(this);
				task.setPerformer(performer);
				task.PERFTIME = new Date();
			}
		}else{
			return ret;
		}
		// 调用任务执行类的TaskPrepare操作
		long task_oversecs;
		try{
			task_oversecs = task.OVERSECS - this.oversecsdelay;
			if(task_oversecs <= 0)
				task_oversecs = this.config.getPropertyLong(FKConfigureKeys.CAMS_FK_TASK_TASKSERVER_PERFORM_TIMEOUT_KEY);
			temp_int = performer.TaskPrepare(task, task_oversecs);
		}catch(Exception e1){	// 任务数据准备过程异常退出，设置任务为完成状态
			logger.warn("[getNewTask]TASK(" + task + ") performer.TaskPrepare Exception:" + e1);
			this.task_mgr.TaskComplete(task.TASKID, TaskAttribute.TaskStatus.perform_error);
			return ret;
		}
		if(temp_int >= 1){	// 任务处理完成，无需进行后续处理，可设置任务完成状态
			this.task_mgr.TaskComplete(task.TASKID, TaskAttribute.TaskStatus.perform_complete);
			return ret;
		}else if(temp_int < 0){	// -1 所有明细密码机通讯失败
			this.task_mgr.TaskComplete(task.TASKID, TaskAttribute.TaskStatus.perform_error);
			return ret;
		}
		logger.debug("[getNewTask]TASK(" + task + ") performer.TaskPrepare return code:" + temp_int + ",task.getPerformMode():" + task.getPerformMode());
		// 调用任务执行类的后台任务启动工作
		if(task.getPerformMode() == TaskAttribute.TaskPerformMode.multimode){
			logger.debug("[getNewTask]begin Call BeginJobs.....");
			temp_int = ((TaskPerformMultiMode)performer).BeginJobs(task, task_oversecs);
			logger.debug("[getNewTask]Call BeginJobs return:" + temp_int + ",Jobs:" + task.tsjobs.getJobList());
		}
		else{	// 单指令模式时，这里进行后台JOB组装
			logger.debug("[getNewTask]begin Call buildTaskServerJob.....");
			if(!this.buildTaskServerJob(task, task_oversecs)){	// 发起后台任务失败
				logger.debug("[getNewTask]begin Call TaskComplete.....");
				this.task_mgr.TaskComplete(task.TASKID, TaskAttribute.TaskStatus.perform_error);
				return ret;
			}
		}
		// 将任务添加到任务管理队列中，进行后续流程监控
		synchronized(this){
			logger.debug(this.getThreadName() + " put into tasklist, Task:" + task);
			this.tasklist.add(task);
		}
		return ret;
	}
	// 循环所有任务发送后台JOB,将任务状态为initial的任务发送到后台服务
	private boolean TaskServerJobSend(){
		boolean ret = false;
		int temp_int;
		TaskServerJobs jobs;
		TaskServerJob job;
		try{
			for(FKTask task:this.tasklist){
				jobs = task.getTSJobs();
				for(String jobid : jobs.getJobList().keySet()){
					job = task.getTSJobs().getJobList().get(jobid);
					if(job.getStatus() == TaskAttribute.TSJobStatus.initial){
						ret = true;
						job.setBeginDate();
						temp_int = this.proc_taskserver.sendTaskCommand(job.getTaskServerID(), job.getRequestXml());
						logger.debug("TaskServerJobSend sendTaskCommand(Task:" + task.TASKID + ") return code(" + temp_int + ")");
						if(temp_int == 0){	// 发送成功
							jobs.setJobStatus(job, TaskAttribute.TSJobStatus.ts_sended);
						}else{	// 发送失败
							jobs.setJobStatus(job, TaskAttribute.TSJobStatus.ts_send_error);
						}
					}
				}
			}
		}catch(Exception e1){
			logger.warn("TaskServerJobSend Exception:" + e1);
		}
		return ret;
	}
	// 循环所有任务设置后台JOB返回内容,检查所有任务状态为ts_sended的任务的返回状态
	private boolean TaskServerResultCheck(){
		boolean ret = false;
		int temp_int;
		long job_limit;
		TaskServerJobs jobs;
		TaskServerJob job;
		StringWrapper result = new StringWrapper();
		try{
			for(FKTask task:this.tasklist){
				jobs = task.getTSJobs();
				for(String jobid : jobs.getJobList().keySet()){
					job = task.getTSJobs().getJobList().get(jobid);
					if(job.getStatus() == TaskAttribute.TSJobStatus.ts_sended){
						ret = true;
						job_limit = task.INTIME.getTime() + job.getOversecs() * 1000;
						// 0 执行完成，返回的结果在v_result封套中;1 没有执行完成，没有有效的XML完整数据报文;
						// -1 异常原因，导致资源释放，本次任务执行失败;-2 待检查的任务标识不存在后台服务执行对象
						temp_int = this.proc_taskserver.checkTaskResult(job.getTaskServerID(), result);
						if(temp_int == 0){	// 后台返回成功
//							this.log.debug("TaskServerResultCheck filledTSJobResponseResult:" + result);
							jobs.filledTSJobResponseResult(job, result.str);
						}else if(temp_int == 1){	// 没有执行完成,检查是否超时，没有超时，状态不改变
							if(System.currentTimeMillis() > job_limit){	// 后台作业执行超时
								logger.info("TaskServerResultCheck setJobStatus ts_recieve_timeout");
								jobs.setJobStatus(job, TaskAttribute.TSJobStatus.ts_recieve_timeout);
								job.setEndDate();
							}
						}else{	// 后台任务执行异常
							logger.warn("TaskServerResultCheck setJobStatus ts_recieve_error");
							jobs.setJobStatus(job, TaskAttribute.TSJobStatus.ts_recieve_error);
							job.setEndDate();
						}
					}
				}
			}
		}catch(Exception e1){
			logger.warn("TaskServerResultCheck Exception:" + e1);
		}
		return ret;
	}
	// 循环所有任务回调业务执行类，single后台任务模式，逐条明细匹配后调用，
	private boolean TaskPerformerCall(){
		boolean ret = false;
		boolean needcallback;
		TaskServerJobs jobs;
		TaskServerJob job;
		long callback_min_status = TaskAttribute.TSJobStatus.ts_recieve_ok.getValue();
		long callback_max_status = TaskAttribute.TSJobStatus.job_complete_ok.getValue();
		try{
			for(FKTask task:this.tasklist){
				jobs = task.getTSJobs();
				needcallback = false;
				for(String jobid : jobs.getJobList().keySet()){
					job = task.getTSJobs().getJobList().get(jobid);
					// 任务的所有作业中只要有一个作业是：ts_send_error、ts_recieve_timeout、ts_recieve_error、ts_recieve_ok四种状态中的一种
					long job_status = job.getStatus().getValue();
					if(job_status >= callback_min_status && job_status < callback_max_status){
						ret = true;
						needcallback = true;
						if(task.getPerformMode() == TaskAttribute.TaskPerformMode.singlemode){	// 如果是single后台任务模式
							for(FKTaskMX taskmx : task.mxlist){
								if(taskmx.TASKMXID.equals(job.getTaskmxid())){	// 匹配到某任务明细记录
									switch(job.getStatus()){
									case ts_recieve_ok:
										task.getPerformer().parseTaskServerResult(task, taskmx, job.getResponseXml());
										taskmx.PERFORMSTEP = TaskAttribute.PerformStep.jobcomp_ok.getValue();
										jobs.setJobStatus(job, TaskAttribute.TSJobStatus.job_complete_ok);
										break;
									case ts_send_error:
									case ts_recieve_error:
										taskmx.PERFORMSTEP = TaskAttribute.PerformStep.jobcomp_error.getValue();
										task.getPerformer().setTaskMXErrorCode(taskmx, ServiceError.SYS_ERRTASKSERVER);
										taskmx.setErrorCodeMsg(ServiceError.SYS_ERRTASKSERVER, ServiceError.SYS_ERRTASKSERVER_MSG);
										taskmx.setPerformContinue(false);
										jobs.setJobStatus(job, TaskAttribute.TSJobStatus.job_complete_error);
										break;
									case ts_recieve_timeout:
										taskmx.PERFORMSTEP = TaskAttribute.PerformStep.jobcomp_overtime.getValue();
										task.getPerformer().setTaskMXErrorCode(taskmx, ServiceError.TASK_PERFOVERTIME);
										taskmx.setErrorCodeMsg(ServiceError.TASK_PERFOVERTIME, ServiceError.TASK_PERFOVERTIME_MSG);
										taskmx.setPerformContinue(false);
										jobs.setJobStatus(job, TaskAttribute.TSJobStatus.job_complete_timeout);
										break;
									default:	// 其他情况不处理
										break;
									}
								}
							}
						}
					}
				}
				if(needcallback){	// 有执行结果需要业务执行类处理
					if(task.getPerformMode() == TaskAttribute.TaskPerformMode.multimode){	// 观察者模式
						((TaskPerformMultiMode)task.getPerformer()).completeSomeJobs(task);
					}
				}
			}
		}catch(Exception e1){
			logger.warn("TaskPerformerCall Exception:" + e1);
		}
		return ret;
	}
	// 循环所有任务，将完成的任务进行完成处理
	private boolean TaskComplete(){
		boolean ret = false;
		try{
			List<String> complete_tasks = new ArrayList<String>();
			for(FKTask task:this.tasklist){
				ret = this.CompleteCheckAndDoOneTask(task);
				if(ret){	// 任务成功执行了完成操作，可以将任务从执行队列中清除掉
					complete_tasks.add(task.TASKID);
				}
			}
			// 根据完成任务清单从tasklist中清除任务
			for(String taskid : complete_tasks){
				for(FKTask task:this.tasklist){
					if(taskid.equals(task.TASKID)){
						this.tasklist.remove(task);
						break;
					}
				}
			}
			complete_tasks = null;
		}catch(Exception e1){
			logger.warn("TaskComplete Exception:" + e1);
		}
		return ret;
	}
	/**结束任务处理，针对单个任务
	 * 标识任务结束的三种状态（perform_complete、perform_oversec、perform_error）规则如下：
	 * 只要存在一个job成功完成job_complete_ok，则设置任务为成功状态，perform_complete
	 * 没有一条job_complete_ok,存在一条job_complete_timeout，则设置任务为超时perform_oversec，
	 * 其他情况则设置任务为执行错误perform_error
	 * @author wolf 2016-7-17
	 * @param v_task
	 * @return true - 任务成功执行了完成操作，可以将任务从执行队列中清除掉
	 *         false - 任务没有做任何操作，等待下一次完成检查
	 */
	private boolean CompleteCheckAndDoOneTask(FKTask v_task){
		boolean ret = false;
		boolean is_overtime = false;
		boolean must_complete = false;
		boolean can_complete = true;
		long task_time_limit;
		TaskServerJobs jobs = v_task.getTSJobs();
		TaskServerJob job;
		@SuppressWarnings("unused")
		int num_complete_ok = 0, num_complete_error = 0, num_complete_timeout = 0;
		task_time_limit = v_task.INTIME.getTime() + v_task.OVERSECS * 1000;
		if(System.currentTimeMillis() > task_time_limit)
			is_overtime = true;
		// 如果阻塞任务已经阻塞超时，则后续的后台任务执行也无意义，需要将任务结束
		if(v_task.STATUS == TaskAttribute.TaskStatus.block_oversec.getValue()){
			must_complete = true;
			for(FKTaskMX taskmx : v_task.mxlist){
				taskmx.PERFORMSTEP = TaskAttribute.PerformStep.jobcomp_blockovertime.getValue();
				v_task.getPerformer().setTaskMXErrorCode(taskmx, ServiceError.TASK_PERFOVERTIME);
				taskmx.setErrorCodeMsg(ServiceError.TASK_BLOCKOVERTIME, ServiceError.TASK_BLOCKOVERTIME_MSG);
				taskmx.setPerformContinue(false);
			}
		}
		for(String jobid : jobs.getJobList().keySet()){
			job = v_task.getTSJobs().getJobList().get(jobid);
			switch(job.getStatus()){
			case job_complete_ok:
				num_complete_ok += 1;
				break;
			case job_complete_error:
				num_complete_error += 1;
				break;
			case job_complete_timeout:
				num_complete_timeout += 1;
				break;
			default:
				if(must_complete){
					jobs.setJobStatus(job, TaskAttribute.TSJobStatus.job_complete_timeout);
					num_complete_timeout += 1;
				}else{
					if(is_overtime){
						for(FKTaskMX taskmx : v_task.mxlist){
							if(job.getTaskmxid().equals(taskmx.TASKMXID)){
								taskmx.PERFORMSTEP = TaskAttribute.PerformStep.jobcomp_overtime.getValue();
								v_task.getPerformer().setTaskMXErrorCode(taskmx, ServiceError.TASK_PERFOVERTIME);
								taskmx.setErrorCodeMsg(ServiceError.TASK_PERFOVERTIME, ServiceError.TASK_PERFOVERTIME_MSG);
								taskmx.setPerformContinue(false);
								jobs.setJobStatus(job, TaskAttribute.TSJobStatus.job_complete_timeout);
								num_complete_timeout += 1;
								break;
							}
						}
					}else{	// 存在未完成的后台服务任务，并且任务的超时时间没有到达
						can_complete = false;
					}
				}
				break;
			}
		}
		if(must_complete){
			ret = true;
			// 完成任务
			logger.debug("[CompleteOneTask]TASK(" + v_task + ") must complete, begin call task_mgr.TaskComplete to complete the TASK.");
			this.task_mgr.TaskComplete(v_task.TASKID, TaskAttribute.TaskStatus.block_oversec);
			// 释放资源
			logger.debug("TASK(" + v_task + ") task_mgr.TaskComplete complete, begin releaseTaskResource.");
			this.releaseTaskResource(v_task);
		}
		if(can_complete){	// 后台任务执行后完成任务
			ret = true;
			if(v_task.getPerformMode() == TaskAttribute.TaskPerformMode.multimode){	// 观察擦者模式
				if(is_overtime || v_task.isCanComplete()){	// 观察者模式标识任务可以完成任务或任务超时，则直接完成任务，否则继续等待，直至任务超时
					logger.debug("[CompleteOneTask] TaskPerformMode(" + v_task.getPerformMode() + ") begin call TaskComplete.......");
					if(num_complete_ok > 0)
						this.task_mgr.TaskComplete(v_task.TASKID, TaskAttribute.TaskStatus.perform_complete);
					else if(num_complete_timeout > 0)
						this.task_mgr.TaskComplete(v_task.TASKID, TaskAttribute.TaskStatus.perform_oversec);
					else
						this.task_mgr.TaskComplete(v_task.TASKID, TaskAttribute.TaskStatus.perform_error);
					// 最后进行该任务下的所有任务的资源释放处理
					this.releaseTaskResource(v_task);
				}
			}else{	// 明细单后台作业模式
				logger.debug("[CompleteOneTask] TaskPerformMode(" + v_task.getPerformMode() + ") begin call TaskComplete.......");
				if(num_complete_ok > 0)
					this.task_mgr.TaskComplete(v_task.TASKID, TaskAttribute.TaskStatus.perform_complete);
				else if(num_complete_timeout > 0)
					this.task_mgr.TaskComplete(v_task.TASKID, TaskAttribute.TaskStatus.perform_oversec);
				else
					this.task_mgr.TaskComplete(v_task.TASKID, TaskAttribute.TaskStatus.perform_error);
				// 最后进行该任务下的所有任务的资源释放处理
				this.releaseTaskResource(v_task);
			}
		}
		return ret;
	}
	/**将任务使用的各项资源执行释放处理
	 * @author wolf 2016-7-18
	 * @param v_task 需要释放资源的任务
	 */
	private void releaseTaskResource(FKTask v_task){
		try{
			if(v_task == null)
				return;
			TaskServerJobs jobs = v_task.getTSJobs();
			TaskServerJob job = null;
			for(String jobid : jobs.getJobList().keySet()){
				job = v_task.getTSJobs().getJobList().get(jobid);
				this.proc_taskserver.releaseResource(job.getTaskServerID());
			}
		}catch(Exception e){
			logger.warn("releaseTaskResource Call interface(" + v_task.function + ") Exception:" + e);
		}
	}
	// 单指令模式时，每条明细将组装一个后台JOB作业，只要有一条组装成功，则成功，否则失败
	private boolean buildTaskServerJob(FKTask v_task, long v_oversecs){
		boolean ret = false;
		int temp_int;
		String tsJobID;
		TaskServerJob job = null;
		for(FKTaskMX taskmx:v_task.mxlist){
			if(taskmx.isPerformContinue()){	// 对应明细记录数据库匹配成功，或者通过密码机获取到了相关通信密码参数，可以继续的继续执行
				StringWrapper xml = new StringWrapper();
				// 0 处理成功，1 其他异常，-1 调用参数错误
				temp_int = v_task.getPerformer().combineTaskServerRequest(v_task, taskmx, v_oversecs, xml);
				if(temp_int == 0){	// 处理成功
					tsJobID = this.perform_mgr.createTaskServerID(taskmx.TASKMXID);
					taskmx.setTaskServerID(tsJobID);
					job = new TaskServerJob(tsJobID, taskmx.TASKMXID, xml.str,v_oversecs);
					v_task.addTaskServerJob(job);
					ret = true;
				}else{	// 组装后台Job失败
					v_task.getPerformer().setTaskMXErrorCode(taskmx, ServiceError.SYS_ERRTSJOBPACKAGE);
					taskmx.setErrorCodeMsg(ServiceError.SYS_ERRTSJOBPACKAGE, ServiceError.SYS_ERRTSJOBPACKAGE_MSG);
				}
			}
		}
		return ret;
	}
	/**下发任务到TaskServer并在超时时间范围内等待TaskServer的返回结果
	 * @param v_tsjob 在超时时间范围内由TaskServer应答的结果报文保存在TaskServerJob实例对应属性中
	 * @return  0 - 处理成功
	 *          1 - 超时未返回
	 *         -1 - 其他异常或失败发生
	 */
	public int getTaskServerResult(TaskServerJob v_tsjob){
		int ret = -1;
		int temp_int = 1;
		logger.debug("getTaskServerResult begin TaskServerJob(" + v_tsjob + ")....................");
		if(v_tsjob == null || ("".equals(v_tsjob.getTaskServerID())))
			return ret;
		v_tsjob.setBeginDate();
		v_tsjob.setOversecs(this.config.getPropertyLong(FKConfigureKeys.CAMS_FK_TASK_TASKSERVER_PERFORM_TIMEOUT_KEY));
		logger.debug("getTaskServerResult begin sendTaskCommand (" + v_tsjob.getOversecs() + ")....................");
		ret = this.proc_taskserver.sendTaskCommand(v_tsjob.getTaskServerID(), v_tsjob.getRequestXml());
		logger.debug("getTaskServerResult sendTaskCommand return code(" + ret + ")....................");
		if(ret != 0){
			logger.warn("getTaskServerResult Error(" + ret + ")....................");
			ret = -1;
			return ret;
		}
		long timeout = v_tsjob.getBeginDate().getTime() + v_tsjob.getOversecs() * 1000;
		StringWrapper result = new StringWrapper();
		logger.debug("getTaskServerResult begin checkTaskResult ....................");
		while(System.currentTimeMillis() < timeout){	// 等待超时
			// 0 执行完成，返回的结果在v_result封套中;1 没有执行完成，没有有效的XML完整数据报文;
			// -1 异常原因，导致资源释放，本次任务执行失败;-2 待检查的任务标识不存在后台服务执行对象
			temp_int = this.proc_taskserver.checkTaskResult(v_tsjob.getTaskServerID(), result);
			if(temp_int == 1){	// 继续等待，其他返回
				BaseThread.sleep(this.idle_sleep);
				continue;
			}else
				break;
		}
		logger.debug("getTaskServerResult checkTaskResult return code(" + temp_int + ")....................");
		switch(temp_int){
		case 1:	// 超时未返回
			ret = 1;
			break;
		case 0:	// 处理成功
			v_tsjob.setResponseXml(result.str);
			ret = 0;
			break;
		default:
			ret = -1;
			break;
		}
		// 资源释放
		this.proc_taskserver.releaseResource(v_tsjob.getTaskServerID());
		logger.debug("getTaskServerResult releaseResource....................");
		return ret;
	}
}
