package com.longshine.cams.fk.server;

import java.lang.reflect.Method;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;

import com.longshine.cams.fk.common.BaseManager;
import com.longshine.cams.fk.structs.DispatchStrategy;
import com.longshine.cams.fk.structs.FKTask;
import com.longshine.cams.fk.structs.FKTaskMX;
import com.longshine.cams.fk.structs.InterfaceProperty;
import com.longshine.cams.fk.structs.TaskAttribute;

public class FKTaskManager extends BaseManager{
	private static Log logger = LogFactory.getLog(FKTaskManager.class);
	private static void debug(Object v){if(logger.isDebugEnabled())	logger.debug(v);}
	@SuppressWarnings("unused")
	private static void info(Object v){if(logger.isInfoEnabled())	logger.info(v);}
	private static void warn(Object v){if(logger.isWarnEnabled())	logger.warn(v);}
	// 定义单例对象的实例变量和方法
	private static FKTaskManager instance;
	// 对外共享的方法
	private FKConfiguration config;
	// 保持对FKServer的引用
	private FKServer fkserver;
	private ApplicationContext appContext;
	// 数据源定义
	private DataSource datasource;
	// 为了生成唯一的任务标识，定义一个顺序循环变量
	private static long seq_task = 1;
	private static final DecimalFormat seq_time_format = new DecimalFormat("0000000000");
	private static final DecimalFormat seq_task_format = new DecimalFormat("0000");
	private static final SimpleDateFormat table_history_suffix = new SimpleDateFormat("_yyyyMM");
	// 定义一个队列优先级任务执行控制数组
	private static DispatchStrategy[] task_dispatch_strategy;
	private static int task_dispatch_curr_pos;
	// 任务归档线程
	private PROC_FKTaskArchieve proc_task_archieve;
	// 任务归档线程启动
	public void startProc(){
		if(proc_task_archieve != null)
			proc_task_archieve.start();
	}
	// 设置所有线程退出条件
	public void setProcExit(){
		proc_task_archieve.setExit();
	}
	// 等待管理线程退出
	public void joinProc(){
		try{
			if(proc_task_archieve != null)
				proc_task_archieve.join();
		}catch(InterruptedException e){
			warn("proc_task_archieve stop exception:" + e);
		}
	}
	/**根据规则生成任务标识：{CELLID}-{CurrentTime}-{NNN}，生成任务标识的方法需要实现多线程安全
	 * {CELLID}：当前节点的节点标识，在CC_Configure中获得。
	 * {CurrentTime}：取系统当前时间，即1970年1月1日距当前的秒数。
	 * {NNN}：本系统实例中的顺序循环数，每生成一个任务即递增1。
	 * 多线程安全性是通过getSeq_tak()方法保证，获取任务顺序数是线程安全的。
	 * @return 符合规则的任务标识
	 */
	public String CreateTaskID(){
		String taskid = this.config.getProperty(FKConfigureKeys.CAMS_FK_SYSTEM_CELLID_KEY);
		// 取当前系统时间，计算从1970年1月1日以来的秒数
		taskid += "-" + seq_time_format.format(System.currentTimeMillis() / 1000);
		taskid += "-" + seq_task_format.format(getSeq_task());
		return taskid;
	}
	public static synchronized long getSeq_task() {
		seq_task = (seq_task >= 9999)?1:(seq_task + 1);
		return seq_task;
	}
	private FKTaskManager(){}
	public static FKTaskManager getInstance(FKConfiguration v_config){
		if(instance == null){
			synchronized(FKServer.class){
				if(instance == null){
					instance = new FKTaskManager();
					instance.config = v_config;
					instance.Initialized();
				}
			}
		}
		return instance;
	}
	public Log getLog() {
		return logger;
	}
	public static FKTaskManager getInstance(){
		return instance;
	}
	public FKConfiguration getConfig() {
		return config;
	}
	public ApplicationContext getApplicationContext() {
		return appContext;
	}
	// 执行任务队列，一共有三个，一个阻塞任务队列，一个非阻塞任务队列和一个高优先级任务队列
	private LinkedList<FKTask> task_input_block;
	private LinkedList<FKTask> task_input_unblock;
	private LinkedList<FKTask> task_input_priority;
	// 任务被认领之后，将该任务从任务input队列中取出，存放到任务执行队列中进行状态管理
	private Map<String,FKTask> task_running;
	// 任务完成队列，该队列保存的对象供交互接口管理对象发结果给调用方
	private Map<String,FKTask> task_complete_block;
	private LinkedList<FKTask> task_complete_unblock;
	// 接口交互模块中的回调/发送线程领取非阻塞的异步任务后，将结果回送给调用方，任务在本队列中管理
	private Map<String,FKTask> task_complete_sending;
	// 任务历史队列，该队列保存的对象等待数据持久化线程将执行任务迁移到任务历史表和任务明细表中
	private LinkedList<FKTask> task_archieve;
	// 装载配置参数，并初始化任务数据校验管理器、任务管理器、任务执行管理器。
	private void Initialized(){
		task_input_block = new LinkedList<FKTask>();
		task_input_unblock = new LinkedList<FKTask>();
		task_input_priority = new LinkedList<FKTask>();
		task_running = new Hashtable<String,FKTask>();
		task_complete_block = new Hashtable<String,FKTask>();
		task_complete_unblock = new LinkedList<FKTask>();
		task_complete_sending = new Hashtable<String,FKTask>();
		task_archieve = new LinkedList<FKTask>();
		createTaskDispatchArray(config.getProperty(FKConfigureKeys.CAMS_FK_TASK_DISPATCH_STRATEGY_KEY));
		task_dispatch_curr_pos = 0;
		proc_task_archieve = new PROC_FKTaskArchieve(config.getPropertyLong(FKConfigureKeys.CAMS_FK_SYSTEM_IDLE_KEY),this);
	}
	/**任务解析管理器工作准备
	 * 
	 */
	public void initializeManager(){
		this.fkserver = FKServer.getInstance();
		this.appContext = this.fkserver.getApplicationContext();
		this.datasource = this.appContext.getBean("datasource_gdcams2",DataSource.class);
	}
	/**根据调度配置策略生成调度数组
	 * 调度策略配置，如：2:1:1等，对应调度队列顺序为priority:block:unblock
	 * 数组内容为DispatchQueue枚举对象
	 * @param dispatch_str 调度策略配置，如：2:1:1等，对应调度队列顺序为priority:block:unblock
	 */
	public static void createTaskDispatchArray(String dispatch_str){
		// 调度策略配置，如：2:1:1等，对应调度队列顺序为priority:block:unblock
		byte dispatch_priority,dispatch_block,dispatch_unblock;
		dispatch_priority = (byte)Long.parseLong(dispatch_str.substring(0,dispatch_str.indexOf(':')));
		dispatch_block = (byte)Long.parseLong(dispatch_str.substring(dispatch_str.indexOf(':') + 1,dispatch_str.lastIndexOf(':')));
		dispatch_unblock = (byte)Long.parseLong(dispatch_str.substring(dispatch_str.lastIndexOf(':') + 1));
		if(dispatch_priority <= 1) dispatch_priority = 1;
		if(dispatch_block <= 1) dispatch_block = 1;
		if(dispatch_unblock <= 1) dispatch_unblock = 1;
		byte total_len = (byte)(dispatch_priority + dispatch_block + dispatch_unblock);
		task_dispatch_strategy = new DispatchStrategy[total_len];
		for(byte i = 0; i < dispatch_priority; i++) task_dispatch_strategy[i] = DispatchStrategy.dispatch_priority;
		for(byte i = 0; i < dispatch_block; i++) task_dispatch_strategy[dispatch_priority + i] = DispatchStrategy.dispatch_block;
		for(byte i = 0; i < dispatch_unblock; i++) task_dispatch_strategy[dispatch_priority + dispatch_block + i] = DispatchStrategy.dispatch_unblock;
	}
	public static synchronized DispatchStrategy[] getTaskDispatchStrategy() {
		return task_dispatch_strategy;
	}
	public synchronized FKTask getTaskArchieve(){
		return task_archieve.poll();
	}
	/**添加费控任务，外部系统需要使用计量自动化系统资源，通过接口交互模块调用后，通过该接口加入到任务管理器中
	 * 逻辑步骤如下：
	 * 1、根据任务的接口方法，获取接口配置信息
	 * 2、根据接口配置，设置TASKTYPE
	 * 3、设置任务的初始状态：STATUS、RESP_STATUS等
	 * @param task 待加入任务管理的用户任务实例
	 * @return 队列添加异常时返回false，否则返回true
	 */
	public boolean TaskAdd(FKTask task){
		boolean ret = true;
		debug("End TaskAdd FK_TASK(" + task + ")");
		// 目前系统不提供流控功能，未来可以添加支持
		// 根据接口的调用方法，获取任务的相关配置信息
		InterfaceProperty interprop = this.config.getInterfaceProperty(task.function);
		// 设置任务的执行节点为本服务节点标识
		task.CELLID = this.config.getProperty(FKConfigureKeys.CAMS_FK_SYSTEM_CELLID_KEY);
		task.STATUS = TaskAttribute.TaskStatus.initial.getValue();
		task.RESP_STATUS = TaskAttribute.SendingStatus.unsend.getValue();
		// 【同步】将任务插入到任务管理表中，FK_TASK和FK_TASK_MX表中
		debug("Begin insert FK_TASK(TASKTYPE:" + task.TASKTYPE + ") and FKTASK_MX..............");
		this.db_updateTask(task);
		// 线程阻塞所有的本对象中的队列对象
		synchronized(this){
			try{
				if(task.TASKTYPE == TaskAttribute.TaskType.block.getValue())
					task_input_block.add(task);
				else if(TaskAttribute.TaskMode.priority.getValue().equals(interprop.mode))
					task_input_priority.add(task);
				else
					task_input_unblock.add(task);
			}catch(Exception e){
				warn("Task add to task running failed.Exception:" + e);
				ret = false;
			}
		}
		debug("TaskAdd complete of FKTask(" + task + ")");
		return ret;
	}
	/**按照调度策略从优先级、阻塞、非阻塞队列中获取一个任务，并将该任务从队列中移除，本方法调用要确保线程安全
	 * 获取下一个任务的规则如下
	 * 1、按照task_dispatch调度策略获取指定任务队列的任务；
	 * 2、获取任务成功，将task_dispatch调度策略指向下一个任务获取策略，然后返回调用方；
	 * 3、获取任务失败，将从task_dispatch当前调度策略的下一个策略开始，顺序循环所有策略；
	 * 4、获取到第一个任务后，则将调度策略指向该策略的下一个策略，然后返回调用方；
	 * 5、全部调度策略循环后，也没有取到一个任务，则返回null给调用方
	 * 6、成功获取调度任务后，将该任务添加到任务执行Map中管理
	 * @return 返回获取到的任务，当返回参数为null时，表示当前没有可要执行的任务
	 */
	public FKTask TaskDispatch(){
		FKTask task = null;
		long task_priority_num = task_input_priority.size();
		long task_block_num = task_input_block.size();
		long task_unblock_num = task_input_unblock.size();
		if(task_priority_num + task_block_num + task_unblock_num <= 0){
			return task;
		}
		synchronized(this){
			debug("Thread(" + Thread.currentThread().getName()+ ")There has Task in the List(" + task_input_priority.size() + "," + task_input_block.size() + "," + task_input_unblock.size() + ")");
			if(task_dispatch_curr_pos >= task_dispatch_strategy.length) task_dispatch_curr_pos = 0;
			switch(task_dispatch_strategy[task_dispatch_curr_pos]){
			case dispatch_priority:
				task = task_input_priority.poll();
				break;
			case dispatch_block:
				task = task_input_block.poll();
				break;
			case dispatch_unblock:
				task = task_input_unblock.poll();
				break;
			}
			// 如果按照规则从对应队列中没有可执行的任务，则按照顺序获取下一个类型的任务，同时改变调度策略
			if(task == null){
				int curr_pos;
				for(int i = 0; i < task_dispatch_strategy.length; i++){
					curr_pos = task_dispatch_curr_pos + i + 1 >= task_dispatch_strategy.length ?
							task_dispatch_curr_pos + i + 1 - task_dispatch_strategy.length
							: task_dispatch_curr_pos + i + 1;
					switch(task_dispatch_strategy[curr_pos]){
					case dispatch_priority:
						task = task_input_priority.poll();
						break;
					case dispatch_block:
						task = task_input_block.poll();
						break;
					case dispatch_unblock:
						task = task_input_unblock.poll();
						break;
					}
					if (task != null){
						task_dispatch_curr_pos = curr_pos + 1;
						if(task_dispatch_curr_pos >= task_dispatch_strategy.length)
							task_dispatch_curr_pos = 0;
						break;
					}
				}
			}else{	// 获取成功后，当前指针后移一位
				task_dispatch_curr_pos += 1;
				if(task_dispatch_curr_pos >= task_dispatch_strategy.length)
					task_dispatch_curr_pos = 0;
			}
			debug("Thread(" + Thread.currentThread().getName()+ ") get a Task(" + task + ")");
		}
		// 将任务添加到任务执行Map中管理
		if(task != null){
			// 设置任务状态为执行中
			task.STATUS = TaskAttribute.TaskStatus.performing.getValue();
			// 【同步】更新FK_TASK表中对应的任务的状态，更新字段：FK_TASK.STATUS
			this.db_updateTaskStatus(task.TASKID, task.STATUS);
		}
		synchronized(this){
			if(task != null){
				task_running.put(task.TASKID, task);
				debug("task_running.size():"+task_running.size());
			}
		}
		return task;
	}
	/**完成任务，供“任务执行模块”调用，当执行线程完成工作后，调用该接口完成任务，详细逻辑如下：
	 * 1、根据任务标识从“执行任务队列”中获取任务，并从队列中移除，当发现“执行任务队列”中查找不到该任务时，后续过程无需执行，直接退出，记录日志即可；
	 * 2、将任务状态设置为“已完成”或“执行超时”；
	 * 3、【同步】将任务结果更新到数据库表中；
	 * 4、将任务插入到“任务完成队列”，等待“接口交互模块”将任务结果发送给调用方。
	 * @param v_taskid 将执行线程的的任务作为参数引用调用，方法中获取TASKID进行各类操作
	 * @param v_status 是TaskAttribute.TaskStatus的Enum变量，设置任务完成状态
	 * @return 执行成功返回true，失败返回false，并记录日志
	 */
	public boolean TaskComplete(final String v_taskid, TaskAttribute.TaskStatus v_status){
		boolean ret = true;
		FKTask task = null;
		synchronized(this){
			boolean bexist = task_running.containsKey(v_taskid);
			if(!bexist){
				warn("Task " + v_taskid + " is not in running queue, the job may be complete by others, please search the task from FK_TASK_YYYYMM table.");
				ret = false;
			}
			if(ret){
				task = task_running.get(v_taskid);
				if(task == null)
					ret = false;
				else{
					task_running.remove(v_taskid);
					if(v_status == TaskAttribute.TaskStatus.perform_oversec)
						task.STATUS = TaskAttribute.TaskStatus.perform_oversec.getValue();
					else if(v_status == TaskAttribute.TaskStatus.perform_error)
						task.STATUS = TaskAttribute.TaskStatus.perform_error.getValue();
					else
						task.STATUS = TaskAttribute.TaskStatus.perform_complete.getValue();
					task.COMPTIME = new Date();
					task.RESP_STATUS = TaskAttribute.SendingStatus.unsend.getValue();
				}
			}
		}
		if(ret){
			// 更新FK_TASK表字段(STATUS,RESP_STATUS,NUM_MX,COMPTIME,WJLJ,ACC_ERRCODE,ACC_ERRMSG,RESP_ERRCODE,RESP_ERRMSG)，更新其中不为null的字段，没有整行记录插入
			// 更新FK_TASK_MX表字段，根据TASKMXID，更新所有不为null的字段到数据，没有的记录插入
			debug("begin update TAKS(" + task + ") to FKTaks and FKTaskMX tables.");
			db_updateTask(task);
			debug("complete update TAKS(" + task + ") to FKTaks and FKTaskMX tables.");
		}
		synchronized(this){
			if(task.TASKTYPE == TaskAttribute.TaskType.block.getValue()){
				debug("Blocked Task is begin.(" + task + ")");
				task_complete_block.put(task.TASKID, task);
				debug("Blocked Task is complete.(" + task + ")");
			}
			else{
				debug("Unblocked Task is begin.(" + task + ")");
				task_complete_unblock.add(task);
				debug("Unblocked Task is complete.(" + task + ")");
			}
		}
		return ret;
	}
	/**获取执行完成的异步任务或需要发送给服务方的任务，程序逻辑如下：
	 * 1、从task_complete_unblock队列中获取队头任务
	 * 2、将任务追加到task_complete_sending队列中
	 * 没有任务时，直接返回null，本方法无需进行发送状态管理，也不需要更新到数据库
	 * @return 返回分配到的待回送调用方的任务
	 */
	public FKTask TaskSendDispatch(){
		FKTask task = null;
		synchronized(this){
			task = task_complete_unblock.poll();
			if(task != null){
				task.RESP_STATUS = TaskAttribute.SendingStatus.sending.getValue();
				task_complete_sending.put(task.TASKID, task);
			}
		}
		return task;
	}
	/**异步任务发送给调用方后，进行归档处理，调用该方法完成，逻辑过程如下：
	 * 1、查找task_unblock_complete队列，失败后，记录日志后退出即可
	 * 2、查找成功，将任务从队列中移除
	 * 3、更新已发送的状态到数据库FK_TASK表
	 * 4、插入到task_history队列中，等待任务迁移到历史表
	 * @param v_taskid 待归档的任务标识
	 */
	public void TaskArchieve(final String v_taskid){
		FKTask task = null;
		synchronized(this){
			task = task_complete_sending.get(v_taskid);
			if(task != null)
				task_complete_sending.remove(v_taskid);
		}
		if(task == null){
			warn("FKTask ID is " + v_taskid + " not exists,please check FK_TASK or FK_TASK_YYYYMM table.");
		}else{
			// 根据任务ID更新数据库表FK_TASK中的状态，只需要更新RESP_STATUS字段即可
			db_updateTaskRespStatus(task.TASKID,task.RESP_STATUS,task.RESP_ERRCODE,task.RESP_ERRMSG);
			// 任务插入到归档队列
			synchronized(this){
				task_archieve.add(task);
			}
		}
	}
	/**对于没有加入到处理队列的任务就判断为其他原因不合法，直接返回调用方后，进行归档处理，调用该方法完成，逻辑过程如下：
	 * 1、插入到task_history队列中，等待任务迁移到历史表
	 * 未来可以考虑检查其他队列中该任务是否存在，并进行相应的处理
	 * @param v_task 待归档的任务引用
	 */
	public void TaskArchieve(final FKTask v_task){
		synchronized(this){
			if(v_task != null){
				if(v_task.TASKID != null && v_task.INTIME != null)
					task_archieve.add(v_task);
			}
		}
	}
	/**阻塞任务检查及超时处理，该接口分两种实现，当超时时间未到达时，进行完成检查，当超时到达时，设置应答错误码及错误信息后应答
	 * 阻塞任务分配后，将定期检查是否完成，
	 * 1、如果完成，则进行任务归档
	 * 2、如果超时未完成，也进行归档，同时将任务超时状态设置为阻塞超时
	 * 业务逻辑如下：
	 * 1、当“阻塞型任务”执行过程中，阻塞线程超时还没有完成时，“接口交互模块”阻塞线程将调用该方法，实现任务的超时处理；
	 * 2、将任务从“任务执行队列”中移除，将状态设置为“阻塞超时”，并将任务插入“任务历史队列”中进行归档；
	 * 3、同时将阻塞超时结果发送给调用方，结束该任务。
	 * 程序逻辑：
	 * 1、检查task_block_complete存在，则返回true，执行成功，将执行结果返回调用方
	 * 2、第1步失败，检查task_running存在，则返回true，同时设置任务状态为阻塞超时，返回调用方
	 * 3、第2步失败，检查task_input_block存在，则返回true，同时设置任务状态为阻塞超时，返回调用方
	 * 4、以上1、2、3步检查成功，均将任务移除到task_history，更新到数据库
	 * @param v_taskid 将执行线程的的任务ID作为参数引用调用
	 * @param v_acc_errcode 当阻塞任务执行超时时，使用的超时错误代码
	 * @param v_acc_errstr 当阻塞任务执行超时时，使用的超时错误信息
	 * @return 执行成功返回true，失败返回false，并记录日志
	 */
	public FKTask TaskBlockCheckOrOversec(final String v_taskid,final long v_acc_errcode,final String v_acc_errstr){
		FKTask task = null;
		synchronized(this){
			task = task_complete_block.get(v_taskid);
			if(task != null){
				task_complete_block.remove(v_taskid);
			}
			if(task == null){
				task = task_running.get(v_taskid);
				if (task != null){
					task_running.remove(v_taskid);
					task.STATUS = TaskAttribute.TaskStatus.block_oversec.getValue();
					task.ACC_ERRCODE = v_acc_errcode;
					task.ACC_ERRMSG = v_acc_errstr;
				}
			}
			if(task == null){
				FKTask tmp_task;
				for(int i = 0; i < task_input_block.size(); i++){
					tmp_task = task_input_block.get(i);
					if(v_taskid.equals(tmp_task.TASKID)){
						// 已经查找到任务还没有分配到资源执行
						debug("find not dispatch resource, begin remove the task(" + tmp_task + ")");
						task_input_block.remove(i);
						task = tmp_task;
						task.STATUS = TaskAttribute.TaskStatus.block_oversec.getValue();
						task.ACC_ERRCODE = v_acc_errcode;
						task.ACC_ERRMSG = v_acc_errstr;
					}
				}
			}
		}
		if(task != null){
			// 将任务属性更新到数据库，更新时，将根据FKTask和FKTaskMX的非空字段更新到数据库相应记录中
			debug("complete check blocked task, begin set task information.");
			db_updateTask(task);
			// 迁移到task_history队列
			debug("complete check blocked task, add to task archieve list.");
			synchronized(this){
				task_archieve.add(task);
			}
		}
		return task;
	}
	/**阻塞任务检查及超时处理
	 * 检查task_block_complete存在，则取出返回给调用方，同时将该对象移除到task_history队列中，等待数据库更新
	 * @param v_taskid 查询指定任务ID的任务完成状态
	 * @return 如果没有完成或执行超时，则直接返回null，否则返回指定任务ID的任务引用
	 */
	public synchronized FKTask TaskBlockCheckOrOversec(final String v_taskid){
		FKTask task = null;
		synchronized(this){
			task = task_complete_block.get(v_taskid);
			if(task != null){
				if(task.STATUS == TaskAttribute.TaskStatus.perform_complete.getValue())
					task.STATUS = TaskAttribute.TaskStatus.perform_complete.getValue();
				else if(task.STATUS == TaskAttribute.TaskStatus.perform_error.getValue())
					task.STATUS = TaskAttribute.TaskStatus.perform_error.getValue();
				else
					task.STATUS = TaskAttribute.TaskStatus.perform_oversec.getValue();
			}
		}
		if(task != null){
			// 将任务属性更新到数据库，更新时，将根据FKTask和FKTaskMX的非空字段更新到数据库相应记录中
			debug("complete check blocked task, begin set task information on .(TaskBlockCheckOrOversec(final String v_taskid))");
			db_updateTask(task);
			// 将任务添加到归档队列，等待归档线程处理
			synchronized(this){
				debug("complete check blocked task, add to task archieve list.(TaskBlockCheckOrOversec(final String v_taskid))");
				task_complete_block.remove(v_taskid);
				task_archieve.add(task);
			}
		}
		return task;
	}
	/**根据任务标识更新任务状态
	 * 只更新FK_TASK表中的STATUS字段
	 */
	private boolean db_updateTaskStatus(final String v_taskid,long v_status){
		boolean ret = false;
		String update_sql = "UPDATE " + this.config.getProperty(FKConfigureKeys.CAMS_FK_TASK_TABLENAME_KEY);
		update_sql += " SET STATUS=" + v_status;
		update_sql += " WHERE TASKID='" + v_taskid + "'";
		int result = this.updateStatement(update_sql);
		if(result > 0){	// 更新成功
			ret = true;
		}else{	// 更新失败
			ret = false;
		}
		return ret;
	}
	/**根据任务标识更新任务状态
	 * 只更新FK_TASK表中的RESP_STATUS字段
	 */
	private boolean db_updateTaskRespStatus(final String v_taskid,long v_resp_status,long v_resp_errcode,String v_resp_errmsg){
		boolean ret = false;
		String update_sql = "UPDATE " + this.config.getProperty(FKConfigureKeys.CAMS_FK_TASK_TABLENAME_KEY);
		update_sql += " SET RESP_STATUS=" + v_resp_status;
		update_sql += " ,RESP_ERRCODE=" + v_resp_errcode;
		update_sql += " ,RESP_ERRMSG='" + v_resp_errmsg + "'";
		update_sql += " WHERE TASKID='" + v_taskid + "'";
		int result = this.updateStatement(update_sql);
		if(result > 0){	// 更新成功
			ret = true;
		}else{	// 更新失败
			ret = false;
		}
		return ret;
	}
	/**根据任务，将任务对象和任务明细对象所有不为null的属性更新到数据库相应的字段中，如果记录根据主键不存在，则插入
	 * 记录更新确保在一个数据库事务中执行
	 */
	private boolean db_updateTask(final FKTask v_task){
		boolean ret = false;
		int result = 0;
		String taskid = null;
		String taskmxid = null;
		if(v_task == null)
			return ret;
		taskid = v_task.TASKID;
		if(taskid == null || "".equals(taskid))
			return ret;
		// 以下代码开始需在一个事务中完成，事务开始
		Connection conn = null;
		try{
			conn = this.datasource.getConnection();
			boolean autoCommit = conn.getAutoCommit();
			if(autoCommit)
				conn.setAutoCommit(false);
			debug("begin update FK_TASK(" + taskid + ").......");
			// 启动事务后，执行后续操作
			if(this.existFKTask(taskid) >= 1){	// 执行FK_TASK更新操作
				result = this.updateFKTask(conn, v_task);
				debug("update FK_TASK(" + taskid + ")......." + result);
			}else{	// 执行FK_TASK插入操作
				result = this.insertFKTask(conn, v_task);
				debug("insert FK_TASK(" + taskid + ")......." + result);
			}
			
			int sjhcSum=0; ///数据回抄记录总数

			if(result > 0){
				for(FKTaskMX taskmx:v_task.mxlist){	// 开始执行任务明细更新
					if(taskmx == null)
						continue;
					taskmxid = taskmx.TASKMXID;
					if(this.existFKTaskMX(taskmxid) >= 1){	// 执行FK_TASK_MX更新操作
						////数据回抄记录总数++
						if(taskmx.ZLBM!=null && taskmx.ZLBM.equals("SJHC")){ //................
							sjhcSum++;
						}
						result = this.updateFKTaskMX(conn, taskmx);
						debug("update FK_TASK_MX(" + taskmxid + ")......." + result);
					}else{	// 执行FK_TASK_MX插入操作
						result = this.insertFKTaskMX(conn, taskmx);
						debug("insert FK_TASK_MX(" + taskmxid + ")......." + result);
					}
					if(result <= 0)		// 如果执行失败，则事务失败
						break;
				}
				///---------------
				//数据回抄记录总数>1时执行
				if(sjhcSum>0){
//				//插入解析电价
					this.updateHcjg(conn);					
				}
				//----------------------
				
			}
			if(result > 0){	// 执行成功
				conn.commit();
				ret = true;
			}
			else{
				conn.rollback();
				ret = false;
			}
		}catch(Exception e1){
			try{
			if(conn != null)
				conn.rollback();
			}catch(Exception e3){
				warn("rollback(" + v_task + ") exception:" + e3);
			}
			warn("update task and task_mx failed(" + v_task + "). Exception:" + e1);
		}finally{
			try{
				if(conn != null){
					conn.setAutoCommit(true);
				}
			}catch(Exception e2){
				warn("set autoTransaction to true exception:" + e2);
			}
			// 事务结束
			this.closeConnectionResource(conn);
		}
		return ret;
	}
	
	//进入电价解析、插入解析电价数据//--------------------
	private int updateHcjg(Connection conn) {
		debug("Proc_Fk_Sjhc Begin");
		CallableStatement cstmt=null;
		try{
			cstmt=conn.prepareCall("{ call Proc_Fk_Sjhc(?)}");
//			java.sql.Date sqlDate=new java.sql.Date(new Date().getTime());
			cstmt.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            boolean execute = cstmt.execute();
			debug("Proc_Fk_Sjhc End");
		}catch(Exception e){
			warn("Proc_Fk_Sjhc Exception:" + e);
		}finally{
			this.closeConnectionResource(cstmt);
			cstmt = null;
		}
		return 0;
	} //-----------------
	/**历史任务归档，归档逻辑步骤如下：<br>
	 * 1、根据任务的INTIME属性，生成目标任务历史表FK_TASK_YYYYMM和目标任务明细历史表FK_TASK_MX_YYYYMM<br>
	 * 2、将记录对象中的所有非空字段插入到目标表<br>
	 * 3、根据任务标识TASKID，从FK_TASK表中删除记录<br>
	 * 4、根据TASKMXID，从FK_TASK_MX表中删除记录<br>
	 * 5、YYYYMM的依据取自FKTask.INTIME字段<br>
	 * 以上2、3、4、5，需在一个事务中完成<br>
	 * 当数据库因为非语法错误时，返回失败，如数据库不可用等，当应用得到返回失败时，需重新将任务插入到队列头，等待较长时间后，再次运行<br>
	 * 当由于数据库语法错误导致出错时，记录日志，返回执行成功
	 * @param v_task 待归档的任务引用
	 * @return 任务为null，TASKID为null或为空字符串时返回false，否则返回true；
	 */
	public boolean db_archieveTask(final FKTask v_task){
		boolean ret = true;
		
		int result = 0 ;
		String taskid = null ;
		String delete_sql = null;
		String delete_mx_sql = null;
		String table_fk_task = null ;
		String table_fk_task_mx = null ;
		String table_fk_task_ls = null ;
		String table_fk_task_mx_ls = null ;
		
		if(v_task == null || v_task.TASKID == null || "".equals(v_task.TASKID) )
			return false ;
		taskid = v_task.TASKID ;
		//获取历史任务表名  比如：FK_TASK_201607 , FK_TASKMX_201607
		table_fk_task = this.config.getProperty(FKConfigureKeys.CAMS_FK_TASK_TABLENAME_KEY);
		table_fk_task_mx = this.config.getProperty(FKConfigureKeys.CAMS_FK_TASK_MX_TABLENAME_KEY) ;
		table_fk_task_ls = table_fk_task;
		table_fk_task_mx_ls = table_fk_task_mx;
		table_fk_task_ls += table_history_suffix.format(v_task.INTIME);
		table_fk_task_mx_ls += table_history_suffix.format(v_task.INTIME);
		
		//以下操作在一个事务中完成，事务开始
		Connection conn = null ;
		try{
			//使用数据源建立连接 
			conn = this.datasource.getConnection();
			//setAutoCommit保证操作在同一个事务内完成，同生同死
			boolean autoCommit = conn.getAutoCommit() ;
			if(autoCommit)
				conn.setAutoCommit(false);
			//根据组装好的表名插入数据
			//首先对历史任务表进行操作
			if((this.existFKTable(taskid,table_fk_task_ls)) >= 1){
				result = this.updateFKTable(conn, v_task, table_fk_task_ls);
			}
			else{
				result = this.insertFKTable(conn, v_task, table_fk_task_ls);
			}
			/**确认历史任务表有新数据插入，进行以下操作
			 * 1，根据任务标识TASKID，从FK_TASK表中删除记录
			 * 2，更新历史任务明细表
			 * 3, 根据任务标识TASKID, 从FK_TASKMX中删除记录
			 */
			if(result > 0){
				//从FK_TASK表中删除记录
				delete_sql = "DELETE FROM " + table_fk_task + " WHERE TASKID = '" + taskid + "'";
				this.deleteRecords(conn, delete_sql);
				 //更新历史任务明细表
				for(FKTaskMX taskmx : v_task.mxlist){
					if(taskmx == null)
						continue ;
					if((this.existFKTable(taskmx.TASKMXID, table_fk_task_mx_ls)) >= 1){
						result = this.updateFKTable(conn, taskmx, table_fk_task_mx_ls);
					}
					else{
						result = this.insertFKTable(conn, taskmx, table_fk_task_mx_ls);
					}
					if(result <= 0)
						break ;
				}
				//如果历史任务明细表已经更新，则根据TASKID，从FK_TASKMX表中删除记录
				if(result > 0 ){
					delete_mx_sql = "DELETE FROM " + table_fk_task_mx +" WHERE TASKID = '" + taskid + "'";
					this.deleteRecords(conn, delete_mx_sql);
				}
			}
			if(result > 0)
				conn.commit();
			else
				conn.rollback();
		}
		catch(Exception e1){
			try{
				if(conn != null)
					conn.rollback();
			}catch(Exception e3){
				warn("rollback(" + v_task + ") exception:" + e3);
			}
			warn("update task and task_mx failed(" + v_task + "). Exception:" + e1);
		}finally{
			try{
				if(conn != null){
					conn.setAutoCommit(true);
				}
			}catch(Exception e2){
				warn("set autoTransaction to true exception:" + e2);
			}
		}
		// 事务结束
		this.closeConnectionResource(conn);
		return ret;
	}
	/**调用时，调用方将ResultSet,Statement,Connection对象作为可变个数参数传入，本方法逐个对象调用close方法进行关闭操作
	 * 执行顺序以传入的顺序执行close操作
	 * @param v_resources 可变个数的sql资源对象
	 */
	private void closeConnectionResource(Object ...v_resources){
		for(int i = 0; i < v_resources.length; i++){
			try{
				if(v_resources[i] != null){
					Class<?> res_class = v_resources[i].getClass();
					Method method = res_class.getMethod("close");
					if(method != null)
						method.invoke(v_resources[i]);
				}
			}catch(Exception e){
				warn("Call close function failed. Exception:" + e);
			}
		}
	}
	/**根据传入的SQL语句执行update、insert、delete操作，并将执行结果返回给调用方
	 * 本方法可以执行update、insert的SQL单个语句，无事务管理功能
	 * @param v_sql 待执行的SQL语句
	 * @return 返回执行结果
	 */
	private int updateStatement(String v_sql){
		int result = 0;
		Connection conn = null;
		Statement st = null;
		try{
			conn = this.datasource.getConnection();
			if(conn != null){
				conn.setAutoCommit(true);
				st = conn.createStatement();
				result = st.executeUpdate(v_sql);
			}
		}catch(Exception e){
			warn("update failed(" + v_sql + ")");
		}finally{
			closeConnectionResource(st, conn);
			st = null;
			conn = null;
		}
		return result;
	}
	/**根据传入的Connection、SQL语句执行update、insert、delete操作，并将执行结果返回给调用方
	 * 本方法可以执行update、insert的SQL单个语句，无事务管理功能
	 * @param v_sql 待执行的SQL语句
	 * @return 返回执行结果
	 */
	private int deleteRecords(Connection v_conn, String v_sql){
		int result = 0;
		Statement st = null;
		try{
			if(v_conn != null){
				st = v_conn.createStatement();
				result = st.executeUpdate(v_sql);
			}
		}catch(Exception e){
			warn("delete failed(" + v_sql + ")");
		}finally{
			closeConnectionResource(st);
			st = null;
		}
		return result;
	}
	/**根据费控任务标识判断任务是否在FK_TASK表中的存在性
	 * @param v_taskid 待检查的任务标识记录是否存在
	 * @return 存在则返回记录条数，不存在则返回0，执行异常时返回-1
	 */
	private int existFKTask(String v_taskid){
		String select_sql = "SELECT COUNT(1) FROM " + this.config.getProperty(FKConfigureKeys.CAMS_FK_TASK_TABLENAME_KEY);
		select_sql += " WHERE TASKID='" + v_taskid + "'";
		return this.countSQLRecords(select_sql);
	}
	/**根据费控任务标识判断任务是否在FK_TASK_MX表中的存在性
	 * @param v_taskmxid 待检查的任务明细标识记录是否存在
	 * @return 存在则返回记录条数，不存在则返回0，执行异常时返回-1
	 */
	private int existFKTaskMX(String v_taskmxid){
		String select_sql = "SELECT COUNT(1) FROM " + this.config.getProperty(FKConfigureKeys.CAMS_FK_TASK_MX_TABLENAME_KEY);
		select_sql += " WHERE TASKMXID='" + v_taskmxid + "'";
		return this.countSQLRecords(select_sql);
	}
	/**提交的数据库语句中第一个查询字段必须是count(1)等，也只有一个查询量，统计符合条件的记录数
	 * @param 待执行的统计SQL
	 * @return 返回记录条数，不存在则返回0，执行异常时返回-1
	 */
	private int countSQLRecords(String v_sql){
		Connection conn = null;
		Statement st = null;
		ResultSet result = null;
		int rec_num = 0;
		try{
			conn = this.datasource.getConnection();
			if(conn != null){
				st = conn.createStatement();
				result = st.executeQuery(v_sql);
				while(result.next()){
					rec_num = result.getInt(1);
				}
			}
		}catch(Exception e){
			warn("Select Exception(" + v_sql + "),Exception:" + e);
			rec_num = 0;
		}finally{
			this.closeConnectionResource(result,st,conn);
			result = null;
			st = null;
			conn = null;
		}
		return rec_num;
	}
	/**将记录插入到FK_TASK表中，插入时，不为空的字段才插入，为空的字段不插入
	 * @param v_conn
	 * @param v_task
	 * @return 组装字段失败返回-1
	 */
	private int insertFKTask(Connection v_conn, FKTask v_task){
		int result = 0;
		String columns;
		String column_values;
		String insert_sql = "";
		String[] str_array = new String[2];
		boolean bvalid = v_task.buildColumnsAndValues(TaskAttribute.OperatorType.insert, str_array);
		if(!bvalid){	// 如果组装更新字段和内容失败，则返回失败-1；
			result = -1;
			warn("[insertFKTask]buildColumnsAndValues failed(" + v_task + ")....");
			return result;
		}
		columns = str_array[0];
		column_values = str_array[1];
		insert_sql = "INSERT INTO " + this.config.getProperty(FKConfigureKeys.CAMS_FK_TASK_TABLENAME_KEY);
		insert_sql += "(" + columns + ")";
		insert_sql += " VALUES(" + column_values + ")";
		debug("SQL:" + insert_sql);
		Statement st = null;
		try{
			st = v_conn.createStatement();
			result = st.executeUpdate(insert_sql);
			debug("SQL Result:" + result);
		}catch(Exception e){
			warn("Insert(" + insert_sql + ") Exception:" + e);
		}finally{
			this.closeConnectionResource(st);
			st = null;
		}
		return result;
	}
	/**将记录更新FK_TASK表，不为空的字段才更新
	 * @param v_conn
	 * @param v_task
	 * @return
	 */
	private int updateFKTask(Connection v_conn, FKTask v_task){
		int result = 0;
		String columns = "";
		String column_values = "";
		String update_sql = "";
		String[] str_array = new String[2];
		boolean bvalid = v_task.buildColumnsAndValues(str_array);
		if(!bvalid){	// 如果组装更新字段和内容失败，则返回失败-1；
			result = -1;
			warn("[updateFKTask]buildColumnsAndValues failed(" + v_task + ")....");
			return result;
		}
		columns = str_array[0];
		column_values = str_array[1];
		update_sql = "UPDATE " + this.config.getProperty(FKConfigureKeys.CAMS_FK_TASK_TABLENAME_KEY);
		update_sql += " SET(" + columns + ")";
		update_sql += " = (SELECT " + column_values + " FROM DUAL)";
		update_sql += " WHERE TASKID='" + v_task.TASKID + "'";
		debug("SQL:" + update_sql);
		Statement st = null;
		try{
			st = v_conn.createStatement();
			result = st.executeUpdate(update_sql);
			debug("SQL Result:" + result);
		}catch(Exception e){
			warn("Insert(" + update_sql + ") Exception:" + e);
		}finally{
			this.closeConnectionResource(st);
			st = null;
		}
		return result;
	}
	/**将记录插入到FK_TASK_MX表中，插入时，不为空的字段才插入，为空的字段不插入
	 * @param v_conn
	 * @param v_taskmx
	 * @return
	 */
	private int insertFKTaskMX(Connection v_conn, FKTaskMX v_taskmx){
		int result = 0;
		String columns = "";
		String column_values = "";
		String insert_sql = "";
		String[] str_array = new String[2];
		boolean bvalid = v_taskmx.buildColumnsAndValues(TaskAttribute.OperatorType.insert,str_array);
		if(!bvalid){	// 如果组装更新字段和内容失败，则返回失败-1；
			result = -1;
			warn("[insertFKTaskMX]buildColumnsAndValues failed(" + v_taskmx + ")....");
			return result;
		}
		columns = str_array[0];
		column_values = str_array[1];
		insert_sql = "INSERT INTO " + this.config.getProperty(FKConfigureKeys.CAMS_FK_TASK_MX_TABLENAME_KEY);
		insert_sql += "(" + columns + ")";
		insert_sql += " VALUES(" + column_values + ")";
		debug("SQL:" + insert_sql);
		Statement st = null;
		try{
			st = v_conn.createStatement();
			result = st.executeUpdate(insert_sql);
			debug("SQL Result:" + result);
		}catch(Exception e){
			warn("Insert(" + insert_sql + ") Exception:" + e);
		}finally{
			this.closeConnectionResource(st);
			st = null;
		}
		return result;
	}
	/**将记录更新FK_TASK_MX表，不为空的字段才更新
	 * @param v_conn
	 * @param v_taskmx
	 * @return
	 */
	private int updateFKTaskMX(Connection v_conn, FKTaskMX v_taskmx){
		int result = 0;
		String columns = "";
		String column_values = "";
		String update_sql = "";
		String[] str_array = new String[2];
		boolean bvalid = v_taskmx.buildColumnsAndValues(str_array);
		if(!bvalid){	// 如果组装更新字段和内容失败，则返回失败-1；
			result = -1;
			warn("[updateFKTaskMX]buildColumnsAndValues failed(" + v_taskmx + ")....");
			return result;
		}
		columns = str_array[0];
		column_values = str_array[1];
		update_sql = "UPDATE " + this.config.getProperty(FKConfigureKeys.CAMS_FK_TASK_MX_TABLENAME_KEY);
		update_sql += " SET(" + columns + ")";
		update_sql += " = (SELECT " + column_values + " FROM DUAL)";
		update_sql += " WHERE TASKMXID='" + v_taskmx.TASKMXID + "'";
		debug("SQL:" + update_sql);
		Statement st = null;
		try{
			st = v_conn.createStatement();
			result = st.executeUpdate(update_sql);
			debug("SQL Result:" + result);
		}catch(Exception e){
			warn("Insert(" + update_sql + ") Exception:" + e);
		}finally{
			this.closeConnectionResource(st);
			st = null;
		}
		return result;
	}
	
	private int existFKTable(String v_taskid,String v_tablename){
		String select_sql = "SELECT COUNT(1) FROM " + v_tablename;
		select_sql += " WHERE TASKID='" + v_taskid + "'";
		debug("SQL:" + select_sql);
		return this.countSQLRecords(select_sql);
	}
	
	private int updateFKTable(Connection v_conn, Object v_obj , String v_tablename){
		int result = 0;
		String columns = "";
		String column_values = "";
		String update_sql = "";
		String[] str_array = new String[2];
		int obj_type = 0;		// 1 - FKTask对象，FKTaskMX对象
		FKTask task = null;
		FKTaskMX taskmx = null;
		boolean bvalid = false;
		if(v_obj instanceof FKTask){
			task = (FKTask)v_obj;
			bvalid = task.buildColumnsAndValues(str_array);
			obj_type = 1;
		}
		else if(v_obj instanceof FKTaskMX){
			taskmx = (FKTaskMX)v_obj;
			bvalid = taskmx.buildColumnsAndValues(str_array);
			obj_type = 2;
		}
		if(!bvalid){	// 如果组装更新字段和内容失败，则返回失败-1；
			result = -1;
			return result;
		}
		columns = str_array[0];
		column_values = str_array[1];
		update_sql = "UPDATE " + v_tablename;
		update_sql += " SET(" + columns + ")";
		update_sql += " = (SELECT " + column_values + " FROM DUAL)";
		if(obj_type == 1)
			update_sql += " WHERE TASKID='" + task.TASKID + "'";
		else
			update_sql += " WHERE TASKMXID='" + taskmx.TASKMXID + "'";
		Statement st = null;
		try{
			st = v_conn.createStatement();
			result = st.executeUpdate(update_sql);
			debug("SQL:" + update_sql);
		}catch(Exception e){
			warn("Insert(" + update_sql + ") Exception:" + e);
		}finally{
			this.closeConnectionResource(st);
			st = null;
		}
		return result;
	}
	
	private int insertFKTable(Connection v_conn, Object v_obj, final String v_tablename){
		int result = 0;
		String columns;
		String column_values;
		String insert_sql = "";
		String[] str_array = new String[2];
		boolean bvalid = false;
		if(v_obj instanceof FKTask){
			bvalid = ((FKTask)v_obj).buildColumnsAndValues(TaskAttribute.OperatorType.insert, str_array);
		}
		else if(v_obj instanceof FKTaskMX){
			bvalid = ((FKTaskMX)v_obj).buildColumnsAndValues(TaskAttribute.OperatorType.insert, str_array);
		}
		if(!bvalid){	// 如果组装更新字段和内容失败，则返回失败-1；
			result = -1;
			return result;
		}
		columns = str_array[0];
		column_values = str_array[1];
		
		insert_sql = "INSERT INTO " + v_tablename;
		insert_sql += "(" + columns + ")";
		insert_sql += " VALUES(" + column_values + ")";
		Statement st = null;
		try{
			st = v_conn.createStatement();
			result = st.executeUpdate(insert_sql);
			debug("SQL:" + insert_sql);
		}catch(Exception e){
			warn("Insert(" + insert_sql + ") Exception:" + e);
		}finally{
			this.closeConnectionResource(st);
			st = null;
		}
		return result;
	}
}
