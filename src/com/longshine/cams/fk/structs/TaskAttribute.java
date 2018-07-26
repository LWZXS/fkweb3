package com.longshine.cams.fk.structs;

public class TaskAttribute {
	/**用于管理FKTask.STATUS字段，任务状态对应值，0-未分配，1-执行中，2-阻塞超时，3-执行超时，4-已完成
	 * @author wolf 2016-7-5
	 */
	public enum TaskStatus{
		initial(0),
		performing(10),
		perform_complete(20),
		block_oversec(21),
		perform_oversec(22),
		perform_error(23);
		private final long value;
		public long getValue(){return value;}
		public static TaskStatus getVariable(long v){for(TaskStatus var:values()){if(var.getValue() == v) return var;}return null;}
		TaskStatus(long v){this.value = v;}
	}
	/**用于管理FKTask.TASKTYPE字段，任务类型字段对应值，1-阻塞型，2-非阻塞型
	 * @author wolf 2016-7-5
	 */
	public enum TaskType{
		block(1),
		unblock(2);
		private final long value;
		public long getValue(){return value;}
		public static TaskType getVariable(long v){for(TaskType var:values()){if(var.getValue() == v) return var;}return null;}
		TaskType(long v){this.value = v;}
	}
	/**用于管理FKTask.RESP_STATUS字段，异步任务应答发送状态定义，0-未发送，1-发送中，2-已发送，3-发送失败
	 * @author wolf 2016-7-5
	 */
	public enum SendingStatus{
		unsend(0),
		sending(1),
		sended(2),
		senderror(3);
		private final long value;
		public long getValue(){return value;}
		public static SendingStatus getVariable(long v){for(SendingStatus var:values()){if(var.getValue() == v) return var;}return null;}
		SendingStatus(long v){this.value = v;}
	}
	/**用于管理任务的模式，任务模式：priority-优先级任务，block-阻塞任务，unblock-非阻塞任务
	 * @author wolf 2016-7-5
	 */
	public enum TaskMode{
		priority("priority"),
		block("block"),
		unblock("unblock");
		private final String value;
		public String getValue(){return value;}
		public static TaskMode getVariable(String v){for(TaskMode var:values()){if(var.getValue().equals(v)) return var;}return null;}
		TaskMode(String v){this.value = v;}
	}
	/**用于管理标识调用外部系统时的调用源头，1-结果回送，2-数据提交
	 * @author wolf 2016-7-5
	 */
	public enum TaskCallMode{
		callback(1),
		datasend(2);
		private final long value;
		public long getValue(){return value;}
		public static TaskCallMode getVariable(long v){for(TaskCallMode var:values()){if(var.getValue() == v) return var;}return null;}
		TaskCallMode(long v){this.value = v;}
	}
	/**任务明细记录执行步骤定义
	 * @author wolf 2016-7-5
	 */
	public enum PerformStep{
		initial(0),
		dbmatched(10),
		mmjcomp_ok(20),
		mmjcomp_error(21),
		sendtaskserver(30),
		jobcomp_ok(40),
		jobcomp_error(41),
		jobcomp_overtime(42),
		jobcomp_blockovertime(43),
		sendover(100);
		private final long value;
		public long getValue(){return value;}
		public static PerformStep getVariable(long v){for(PerformStep var:values()){if(var.getValue() == v) return var;}return null;}
		PerformStep(long v){this.value = v;}
	}
	/**数据库操作类型定义
	 * @author wolf 2016-7-5
	 */
	public enum OperatorType{
		insert(1),
		update(2),
		select(3),
		any(4);
		private final long value;
		public long getValue(){return value;}
		public static OperatorType getVariable(long v){for(OperatorType var:values()){if(var.getValue() == v) return var;}return null;}
		OperatorType(long v){this.value = v;}
	}
	/**请求密码机的类型，1-计量密码机，2-营销密码机
	 * @author wolf 2016-7-9
	 */
	public enum MMJType{
		jl(1),
		yx(2);
		private final long value;
		public long getValue(){return value;}
		public static MMJType getVariable(long v){for(MMJType var:values()){if(var.getValue() == v) return var;}return null;}
		MMJType(long v){this.value = v;}
	}
	/**密码机运行状态，分为两种，1-release发布状态，0-debug调试状态
	 * @author wolf 2016-11-21
	 * */
	public enum MMJRunStatus{
		debug(0),
		release(1);
		private final long value;
		public long getValue(){return value;}
		public static MMJRunStatus getVariable(long v){for(MMJRunStatus var:values()){if(var.getValue() == v) return var;}return null;}
		MMJRunStatus(long v){this.value = v;}
	}
	/**表号生成规则，0-涉及表号的位置使用12个0代替，1-使用表地址代替，不足12位时，前补0
	 * @author wolf 2016-11-21
	 * */
	public enum BHRule{
		default0(0),
		bdz(1);
		private final long value;
		public long getValue(){return value;}
		public static BHRule getVariable(long v){for(BHRule var:values()){if(var.getValue() == v) return var;}return null;}
		BHRule(long v){this.value = v;}
	}
	/**用户类型标识，1-低压居民，2-高压用户
	 * @author wolf 2016-7-9
	 */
	public enum YDKHType{
		dyjm(1),
		gy(2);
		private final long value;
		public long getValue(){return value;}
		public static YDKHType getVariable(long v){for(YDKHType var:values()){if(var.getValue() == v) return var;}return null;}
		YDKHType(long v){this.value = v;}
	}
	/**用电信息类型标志：0为月冻结表码，1为实时表码，2为日冻结表码，3：费控电能表(费控终端)停复电状态，
	 * 4：费控电能表(费控终端)保电电状态，5：费控模式状态字，F:以上全部抄读
	 * @author wolf 2016-7-13
	 */
	public enum YDXX_LXBZ{
		monthfreeze("0"),
		currentydxx("1"),
		dayfreeze("2"),
		tfdstatus("3"),
		bdstatus("4"),
		fkmsdm("5"),
		all("F");
		private final String value;
		public String getValue(){return value;}
		public static YDXX_LXBZ getVariable(String v){for(YDXX_LXBZ var:values()){if(var.getValue().equals(v)) return var;}return null;}
		YDXX_LXBZ(String v){this.value = v;}
	}
	/**发送给后台执行的作业的状态
	 * @author wolf 2016-7-15
	 * 0-初始，10-send，20-接收完成，21-接收错误，22-接收超时，23-发送错误，30-处理完成
	 */
	public enum TSJobStatus{
		initial(0),
		ts_sended(10),
		ts_recieve_ok(20),
		ts_recieve_error(21),
		ts_recieve_timeout(22),
		ts_send_error(23),
		job_complete_ok(30),
		job_complete_timeout(31),
		job_complete_error(32);
		private final long value;
		public long getValue(){return value;}
		public static TSJobStatus getVariable(long v){for(TSJobStatus var:values()){if(var.getValue() == v) return var;}return null;}
		TSJobStatus(long v){this.value = v;}
	}
	/**任务执行模式，分为两种类型，一种是单任务模式，一种是观察者模式
	 * 单任务模式主要是为了兼容初期的任务执行模式，后期改进为观察者模式
	 * 单任务模式：任务的每条明细执行一次后台任务即可结束，任务结束由执行线程控制，进行后续任务驱动
	 * 观察者模式：支持一次或多次后台任务交互，任务结束由用户执行类确定，用户执行类确认任务结束后，任务执行线程设置任务结束标志，进行后续任务驱动
	 */
	public enum TaskPerformMode{
		singlemode("single"),
		multimode("multi");
		private final String value;
		public String getValue(){return value;}
		public static TaskPerformMode getVariable(String v){for(TaskPerformMode var:values()){if(var.getValue().equals(v)) return var;}return null;}
		TaskPerformMode(String v){this.value = v;}
	}
	/**系统运行模式，运行与广东省集中模式，还是省外模式
	 * gd：广东省集中模式，对应的资源获取方式，档案信息获取的数据库资源的以广东省集中的数据库表结构为准
	 * sw：按照省外地市主站系统的模式获取档案的信息资源
	 */
	public enum SystemMode{
		gd("gd"),
		sw("sw");
		private final String value;
		public String getValue(){return value;}
		public static SystemMode getVariable(String v){for(SystemMode var:values()){if(var.getValue().equals(v)) return var;}return null;}
		SystemMode(String v){this.value = v;}
	}
	/**用于表示业务资源初始化方式，目前只有一种初始化方式，系统将对不同的初始化方式，使用不同的参数进行业务资源初始化
	 * system_mode初始化方式，将使用“SystemMode”的具体取值进行业务资源初始化
	 */
	public enum InitialType{
		system_mode("system_mode");
		private final String value;
		public String getValue(){return value;}
		public static InitialType getVariable(String v){for(InitialType var:values()){if(var.getValue().equals(v)) return var;}return null;}
		InitialType(String v){this.value = v;}
	}
}
