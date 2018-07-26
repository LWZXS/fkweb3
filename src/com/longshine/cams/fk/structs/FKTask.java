/**该对象包含了任务的基本管理信息，该对象有一个明细对象列表，该列表对应的任务记录的明细数据
 * 任务对象主要属性均需要持久化到数据库表中，对应的数据库表为：FK_TASK
 * 该表中的属性如下，需要在本对象中体现
 ***********************************************************************************************
 * 任务标识		TASKID          VARCHAR2(32)    {CELLID}-{CurrentTime}-{NNN}
 * 接口ID			INT_FUNC        VARCHAR2(32)    对应接口规范中的方法名
 * 回调接口ID		CALL_FUNC		VARCHAR2(32)	回调接口的接口方法名
 * 交易流水号		JYLSH           VARCHAR2(32)    调用方的任务式任务均包含调用方的交易流水号，在任务执行完成后，该交易流水号作为应答的参数回调调用方
 * 节点标识		CELLID          VARCHAR2(8)     标识该任务是在哪个费控服务节点上管理和执行，每个不同的费控服务必须配置集群唯一的节点标识
 * 地区编码		DSBM            VARCHAR2(8)     细化到地市级，目前是四位编码
 * 任务类型		TASKTYPE        SMALLINT        任务类型，1-阻塞型，2-非阻塞型，缺省值为2
 * 任务状态		STATUS          SMALLINT        任务的状态，如未分派，执行中等，对应数据字典项：
 * 发送状态		RESP_STATUS     SMALLINT        完成执行的任务的发送状态，0-未发送，1-发送中，2-已发送
 * 明细记录条数		NUM_MX			INT				明细记录条数
 * 接收时间		INTIME          DATE            接收到数据的时间，取自接收服务器时间，系统发起的任务，这里为接收到该任务的时间
 * 开始执行时间		PERFTIME        DATE            开始执行时间，取自执行任务的服务器时间，系统发起的任务，该字段为空
 * 完成时间		COMPTIME        DATE            完成任务的时间，取自执行任务的服务器时间，系统发起的任务，该字段为空
 * 超时秒数		OVERSECS        NUMBER(10)      自接收任务后，超时时间内必须返回，否则返回超时
 * 文件路径		WJLJ            VARCHAR2(256)   保存需提交给对端的FTP文件相对路径
 * 接收错误码		ACC_ERRCODE     VARCHAR2(8)     接收请求时应答的错误码
 * 接收错误信息		ACC_ERRMSG      VARCHAR2(128)   接收请求时应答的错误信息
 * 应答错误码		RESP_ERRCODE    VARCHAR2(8)     作为客户端回调对端时对端应答的错误码
 * 应答错误信息		RESP_ERRMSG     VARCHAR2(128)   作为客户端回调对端时对端应答的错误信息
 ***********************************************************************************************
 */
package com.longshine.cams.fk.structs;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.longshine.cams.fk.common.BaseTaskPerform;
import com.longshine.cams.fk.server.FKTaskManager;

public class FKTask {
	// 管理属性方法
	public String function;	// 生成对象时，外部系统调用时，设置为INT_FUNC，当系统生成的数据发送指令，设置的是CALL_FUNC
	// 以下属性对应FK_TASK表中的相关属性
	public String TASKID;		// 接口实现代码设置
	public String INT_FUNC;		// 外部系统调用时设置为接口方法名
	public String CALL_FUNC;	// 系统生成的调用外部服务时的外部接口方法名
	public String ZLBM;			// 根据接口配置，设置任务的指令编码
	public String JYLSH;		// 交易流水号
	public String CELLID;		// 设置费控服务的节点标识
	public String DSBM;			// 根据请求的供电单位编码前四位，设置为DSBM，不足四位的按照实际位数设置本属性
	public String QXDWBM;		// 设置请求时的权限单位编码字段，原生数据发送时，设置单个用户的QXDWBM字段或多用户时，发布最上级的QXDWBM，直至DSBM
	public Long TASKTYPE;		// 根据接口配置设置
	public Long STATUS;			// 任务的状态，如未分派，执行中等，任务状态对应值，0-未分配，1-执行中，2-阻塞超时，3-执行超时，4-已完成
	public Long RESP_STATUS;
	public Long NUM_MX;
	public Date INTIME;
	public Date PERFTIME;
	public Date COMPTIME;
	public Long OVERSECS;
	public String WJLJ;
	public Long ACC_ERRCODE;
	public String ACC_ERRMSG;
	public Long RESP_ERRCODE;
	public String RESP_ERRMSG;
	// 定义任务明细的对象列表
	public List<FKTaskMX> mxlist = null;
	// 定义发送TaskServer的任务列表
	public TaskServerJobs tsjobs = null;
	// 用于控制该任务是否还有任务需要执行，是否可以完成
	private boolean can_complete;
	// 保存该任务执行的执行类引用
	private BaseTaskPerform performer = null;
	// 任务的执行模式
	private TaskAttribute.TaskPerformMode perform_mode;
	// 管理型属性和方法
	private static final SimpleDateFormat oracle_date_sdf=new SimpleDateFormat("yyyyMMddHHmmss");
	static String DBDateTimeColumn(final Date v_date){
		String oracle_cloumn = "TO_DATE('" + oracle_date_sdf.format(v_date) + "','YYYYMMDDHH24MISS')";
		return oracle_cloumn;
	}
	// 初始化方法，用于从数据库装载任务恢复任务调度时生成任务
	public FKTask(){
		this.tsjobs = new TaskServerJobs();
		this.setPerformMode(TaskAttribute.TaskPerformMode.singlemode);
		this.can_complete = false;
	}
	// 初始化方法
	public FKTask(InterfaceProperty v_prop,TaskAttribute.TaskCallMode v_mode){
		this.TASKID = FKTaskManager.getInstance().CreateTaskID();
		this.tsjobs = new TaskServerJobs();
		this.setPerformMode(TaskAttribute.TaskPerformMode.singlemode);
		this.can_complete = false;
		if(v_prop != null){
			this.function = v_prop.function;
			if(v_mode == TaskAttribute.TaskCallMode.callback)
				this.INT_FUNC = v_prop.function;
			this.CALL_FUNC = v_prop.call_func;
			this.ZLBM = v_prop.zlbm;
			this.OVERSECS = v_prop.oversecs;
			// 任务类型设置
			if(TaskAttribute.TaskMode.block.getValue().equals(v_prop.mode))	// 阻塞型任务
				this.TASKTYPE = TaskAttribute.TaskType.block.getValue();
			else
				this.TASKTYPE = TaskAttribute.TaskType.unblock.getValue();
		}
	}
	public void setQXDWBM(String v_qxdwbm){
		if(v_qxdwbm != null){
			this.QXDWBM = v_qxdwbm;
			this.DSBM = v_qxdwbm.length() > 4 ? v_qxdwbm.substring(0, 4) : v_qxdwbm;
		}
	}
	public void addFKTaskMX(FKTaskMX v_taskmx){
		if(this.mxlist == null)
			this.mxlist = new ArrayList<FKTaskMX>();
		this.mxlist.add(v_taskmx);
		this.NUM_MX = new Long(this.mxlist.size());
	}
	public boolean addTaskServerJob(TaskServerJob v_job){
		if(v_job == null)
			return false;
		if(this.tsjobs == null)
			this.tsjobs = new TaskServerJobs();
		return this.tsjobs.addJob(v_job);
	}
	public boolean buildColumnsAndValues(String[] params){
		return this.buildColumnsAndValues(TaskAttribute.OperatorType.any, params);
	}
	/**返回除了主键外的所有非空字段名称及对应更新数据值数组
	 * @param v_type 生成SQL字符串的类型，有insert和update两种可选
	 * @param params，第一个字符串为字段列表，第二个字符串为值列表
	 * @return 如果主键不存在则返回false，否则返回true
	 */
	public boolean buildColumnsAndValues(TaskAttribute.OperatorType v_type, String[] params){
		boolean ret = false;
		if(TASKID == null || "".equals(TASKID))
			return ret;
		String columns = "";
		String columnvalues = "";
		if(v_type == TaskAttribute.OperatorType.insert){
			if(TASKID != null && (!"".equals(TASKID))){ columns += ",TASKID"; columnvalues += ",'" + TASKID + "'";}
		}
		if(INT_FUNC != null && (!"".equals(INT_FUNC))){ columns += ",INT_FUNC"; columnvalues += ",'" + INT_FUNC + "'";}
		if(CALL_FUNC != null && (!"".equals(CALL_FUNC))){ columns += ",CALL_FUNC"; columnvalues += ",'" + CALL_FUNC + "'";}
		if(ZLBM != null && (!"".equals(ZLBM))){ columns += ",ZLBM"; columnvalues += ",'" + ZLBM + "'";}
		if(JYLSH != null && (!"".equals(JYLSH))){ columns += ",JYLSH"; columnvalues += ",'" + JYLSH + "'";}
		if(CELLID != null && (!"".equals(CELLID))){ columns += ",CELLID"; columnvalues += ",'" + CELLID + "'";}
		if(DSBM != null && (!"".equals(DSBM))){ columns += ",DSBM"; columnvalues += ",'" + DSBM + "'";}
		if(QXDWBM != null && (!"".equals(QXDWBM))){ columns += ",QXDWBM"; columnvalues += ",'" + QXDWBM + "'";}
		if(TASKTYPE != null){ columns += ",TASKTYPE"; columnvalues += "," + TASKTYPE + "";}
		if(STATUS != null){ columns += ",STATUS"; columnvalues += "," + STATUS + "";}
		if(RESP_STATUS != null){ columns += ",RESP_STATUS"; columnvalues += "," + RESP_STATUS + "";}
		if(NUM_MX != null){ columns += ",NUM_MX"; columnvalues += "," + NUM_MX + "";}
		if(INTIME != null){ columns += ",INTIME"; columnvalues += "," + DBDateTimeColumn(INTIME) + "";}
		if(PERFTIME != null){ columns += ",PERFTIME"; columnvalues += "," + DBDateTimeColumn(PERFTIME) + "";}
		if(COMPTIME != null){ columns += ",COMPTIME"; columnvalues += "," + DBDateTimeColumn(COMPTIME) + "";}
		if(OVERSECS != null){ columns += ",OVERSECS"; columnvalues += "," + OVERSECS + "";}
		if(WJLJ != null && (!"".equals(WJLJ))){ columns += ",WJLJ"; columnvalues += ",'" + WJLJ + "'";}
		if(ACC_ERRCODE != null){ columns += ",ACC_ERRCODE"; columnvalues += "," + ACC_ERRCODE + "";}
		if(ACC_ERRMSG != null && (!"".equals(ACC_ERRMSG))){ columns += ",ACC_ERRMSG"; columnvalues += ",'" + ACC_ERRMSG + "'";}
		if(RESP_ERRCODE != null){ columns += ",RESP_ERRCODE"; columnvalues += "," + RESP_ERRCODE + "";}
		if(RESP_ERRMSG != null && (!"".equals(RESP_ERRMSG))){ columns += ",RESP_ERRMSG"; columnvalues += ",'" + RESP_ERRMSG + "'";}
		if(columns.startsWith(","))
			params[0] = columns.substring(1, columns.length());
		else
			params[0] = columns;
		if(columnvalues.startsWith(","))
			params[1] = columnvalues.substring(1, columnvalues.length());
		else
			params[1] = columnvalues;
		ret = true;
		return ret;
	}
	public BaseTaskPerform getPerformer() {
		return performer;
	}
	public TaskAttribute.TaskPerformMode getPerformMode() {
		return perform_mode;
	}
	public TaskServerJobs getTSJobs() {
		return tsjobs;
	}
	public void setPerformer(BaseTaskPerform performer) {
		this.performer = performer;
	}
	public void setPerformMode(TaskAttribute.TaskPerformMode perform_mode) {
		this.perform_mode = perform_mode;
	}
	public boolean isCanComplete() {
		return can_complete;
	}
	public void setCanJomplete(boolean can_complete) {
		this.can_complete = can_complete;
	}
	@Override
	public String toString(){
		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd HH:mm:ss");
		return "TaskID:" + TASKID + "function:" + function + ";INT_FUNC:" + INT_FUNC + ";CALL_FUNC:" + CALL_FUNC + ";INTIME:" + sdf.format(INTIME) + ";MXLIST NUM:" + mxlist.size() + ";MXLIST:" + mxlist;
	}
}
