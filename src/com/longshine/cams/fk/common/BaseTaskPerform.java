package com.longshine.cams.fk.common;

import javax.sql.DataSource;

import com.longshine.cams.fk.server.FKConfiguration;
import com.longshine.cams.fk.server.FKConfigureKeys;
import com.longshine.cams.fk.server.FKServer;
import com.longshine.cams.fk.server.FKTaskPerformManager;
import com.longshine.cams.fk.server.PROC_FKTaskPerform;
import com.longshine.cams.fk.structs.FKTask;
import com.longshine.cams.fk.structs.FKTaskMX;
import com.longshine.cams.fk.structs.InterfaceProperty;
import com.longshine.cams.fk.structs.StringWrapper;
import com.longshine.cams.fk.structs.TaskAttribute;

public abstract class BaseTaskPerform extends BaseDao{
	// 系统配置对象
	protected FKConfiguration config;
	// 保存对任务管理器的引用
	protected FKTaskPerformManager task_perform_mgr;
	// 任务解析对象的方法名
	protected String function_name;
	// 保存接口配置项引用
	protected InterfaceProperty property;
	// 当前数据库的数据库类型，如oracle,mysql等
	protected String db_type;
	// 保存当前执行该任务指定对象的执行线程对象
	protected PROC_FKTaskPerform proc_taskperform;
	/**费控通信中表号规则，取值含义如下：0-涉及表号的位置使用12个0代替，1-使用表地址代替，不足12位时，前补0*/
	protected TaskAttribute.BHRule bh_rule;
	// 运行模式，gd为广东模式，sw为省外模式
	protected TaskAttribute.SystemMode run_mode;
	// 密码机运行状态，1-release状态，0-debug状态
	protected TaskAttribute.MMJRunStatus mmj_runstatus;
	
	public PROC_FKTaskPerform getProcTaskperform() {
		return proc_taskperform;
	}
	public void setProcTaskperform(PROC_FKTaskPerform v_proc) {
		this.proc_taskperform = v_proc;
	}
	// 初始化任务解析对象
	public void Initialized(FKConfiguration v_config,InterfaceProperty v_prop,DataSource v_ds,String v_func){
		this.config = v_config;
		this.property = v_prop;
		this.datasource = v_ds;
		this.function_name = v_func;
		this.task_perform_mgr = FKTaskPerformManager.getInstance();
		this.max_cached_rows = (int)this.config.getPropertyLong(FKConfigureKeys.CAMS_FK_TASK_DB_CACHEDROWS_MAX_KEY);
		this.db_type = this.config.getProperty(FKConfigureKeys.CAMS_FK_TASK_DB_TYPE_KEY).toLowerCase();
		if(this.config.getPropertyInteger(FKConfigureKeys.CAMS_FK_TASK_GYCS_BH_RULE_KEY) == 0)
			this.bh_rule = TaskAttribute.BHRule.default0;
		else
			this.bh_rule = TaskAttribute.BHRule.bdz;
		this.run_mode = FKServer.getRunMode();
		if("debug".equals(this.config.getProperty(FKConfigureKeys.CAMS_FK_TASK_MMJ_RUNSTATUS_KEY).toLowerCase()))
			this.mmj_runstatus = TaskAttribute.MMJRunStatus.debug;
		else
			this.mmj_runstatus = TaskAttribute.MMJRunStatus.release;
	}
	public void setConfig(FKConfiguration config) {
		this.config = config;
	}
	/** 任务执行准备，主要功能有两点，1-根据任务明细内容从数据库中扩展相关属性，2-根据任务明细记录，判断是否需要通过从密码机获取加密数据项
	 * 该虚方法需要实现，以实现任务数据的请求处理，如果只是获取数据库中的相关属性返回给调用方，则该方法即可完成任务
	 * @param v_task 待处理的任务
	 * @param v_oversecs 处理任务的超时时间限制
	 * @return 0 执行成功，
	 *         -1 所有明细密码机通讯失败，
	 *         -2 所有明细数据库通讯失败，
	 *         -3 所有明细系统中无符合记录，
	 *         -4 无明细记录
	 *         -5 任务为null异常
	 *         1 任务处理完成，无需进行后续处理，可设置任务完成状态
	 */
	public abstract int TaskPrepare(FKTask v_task, long v_oversecs);
	/**从数据库中获取信息填充任务中的某些属性，如终端规约等属性
	 * 该方法不是必须实现，执行实现类可以继承该类实现数据库属性变更，无需从数据库扩展属性时，无需继承，缺省实现时，直接放回执行成功
	 * @param v_task 待处理的任务
	 * @return 0 处理成功，-1 数据库获取异常
	 */
	protected int selectTaskInfor(FKTask v_task){int ret = 0; return ret;}
	/**根据明细记录生成MMJ请求报文结构体
	 * 该方法不是必须实现，执行类根据明细记录需要发起密码机参数加密请求，则需要覆盖该方法进行密码机加密参数设置，缺省实现时，直接放回执行成功
	 * @param v_task 待处理的任务对象
	 * @param v_taskmx 待处理的任务明细对象
	 * @param v_req	返回组装好的Request结构体
	 * @return 0 处理成功，1 其他异常
	 * 该方法不是必须实现，根据接口的需求来提供实现，无需请求密码机参数时，无需继承，缺省实现时，直接放回执行成功
	 */
	protected int combineMMJRequest(FKTask v_task, FKTaskMX v_taskmx, Object v_req){int ret = 0; return ret;}
	/**根据密码机返回的结果设置任务结构体中的相关参数
	 * 该方法不是必须实现，执行类根据明细记录发起了密码机参数加密请求，则需要覆盖该方法进行密码机加密参数设置，缺省实现时，直接放回执行成功
	 * @param v_task 待处理的任务对象
	 * @param v_taskmx 待处理的任务明细对象
	 * @param v_res	密码机返回的对象实例
	 * @return 0 处理成功，1 其他异常
	 */
	protected int parseMMJResult(FKTask v_task, FKTaskMX v_taskmx, Object v_res){int ret = 0; return ret;}
	/**根据明细记录生成TaskServer的请求XML报文结构
	 * 该方法不是必须实现，执行类需要发起向TaskServer请求时，才调用该方法，进行请求结构封装，缺省实现时，直接放回执行成功
	 * @param v_task 待处理的任务对象
	 * @param v_taskmx 待处理的任务明细对象
	 * @param v_oversecs 设置请求后台服务的超时时间，单位：秒
	 * @param v_xml 返回组装好的XML字符串封装对象
	 * @return 0 处理成功，1 其他异常
	 */
	public int combineTaskServerRequest(FKTask v_task, FKTaskMX v_taskmx, Long v_oversecs, StringWrapper v_xml){int ret = 0; return ret;}
	/**根据TaskServer返回的XML结构数据解析到任务结构体，以返回给调用方
	 * 该方法不是必须实现，执行类发起了TaskServer请求后，根据TaskServer返回的XML字符串填充FKTask/FKTaskMX结构体
	 * @param v_task 待处理的任务对象
	 * @param v_taskmx 待处理的任务明细对象
	 * @param v_xml TaskServer返回的XML字符串
	 * @return 0 处理成功，-1 其他异常
	 */
	public int parseTaskServerResult(FKTask v_task, FKTaskMX v_taskmx, String v_xml){int ret = 0; return ret;}
	/**根据记录处理错误信息设置任务明细记录的结果状态，不同类型的接口设置的错误状态属性不一致，因此由用户层来实现错误码设置
	 * 该方法不是必须实现，缺省实现时，将该记录的perform_continue控制属性设置为false，用户可以继承该方法，最后调用父类的该方法设置明细记录的perform_continue属性
	 * @param v_taskmx 待设置任务明细错误代码的任务明细记录
	 * @param v_errcode 待设置的错误代码值
	 */
	public void setTaskMXErrorCode(FKTaskMX v_taskmx, Long v_errcode){}
	/**采用后台任务服务多JOB交互模式执行任务时，当后台服务完成部分任务后，通过回调业务执行类中的该方法实现业务结果处理.<br>
	 * <p>当业务执行类中完成了所有的工作后，可以在该回调方法中调用tsjobs
	 * @param v_task 将完成任务的任务作为参数调用业务执行类，在该类中的tsjobs对象中包含了所有的后台服务交互工作对象List，业务执行类可以根据业务需要进行结果处理及后续过程驱动
	 */
	public void completeSomeJobs(FKTask v_task){};
	public int BeginJobs(FKTask v_task, long v_oversecs){return 0;};
	/**根据传入的表地址参数和表号规则生成代码中使用的表号参数，目前有两种配置，<br>
	 * 当CAMS_FK_TASK_GYCS_BH_RULE_KEY配置为0时-涉及表号的位置使用12个0代替，配置为1时-使用表地址代替，不足12位时，前补0
	 * @param v_bdz 传入电表的表地址，该方法根据费控指令中生成表号的规则返回表号
	 * @return 根据配置规则返回表号字符串
	 * */
	protected String CreateParamBH(String v_bdz){
		if(this.bh_rule == TaskAttribute.BHRule.default0)
			return CAMSConstant.BH_DEFAULT_0;
	else{
			String ret = CAMSConstant.BH_DEFAULT_0 + v_bdz;
			return ret.substring(ret.length() - 12, ret.length());
		}
//		if (v_bdz == null){
//			
//			return CAMSConstant.BH_DEFAULT_0;
//		}
//		else {
//			
//		return v_bdz;
//		
//		}
//		
	}
}
