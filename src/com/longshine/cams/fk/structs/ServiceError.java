package com.longshine.cams.fk.structs;

public class ServiceError {
	// 错误码定义
	public static Long SYS_SUCCESS     		= 0L;		public static String SYS_SUCCESS_MSG 		= "执行成功";
	public static Long SYS_ERRUNKNOWN		= 10000L;	public static String SYS_ERRUNKNOWN_MSG		= "一般错误";
	public static Long SYS_FAULTDB			= 10001L;	public static String SYS_FAULTDB_MSG		= "数据库通信失败";
	public static Long SYS_APPEXCEPTION		= 10002L;	public static String SYS_APPEXCEPTION_MSG	= "应用程序执行异常";
	public static Long SYS_CALLEXCEPTION	= 10003L;	public static String SYS_CALLEXCEPTION_MSG	= "调用WebService远程方法异常";
	public static Long SYS_NOTIMPLEMENT		= 10004L;	public static String SYS_NOTIMPLEMENT_MSG	= "该方法暂未实现";
	public static Long SYS_CHECKERR			= 10005L;	public static String SYS_CHECKERR_MSG		= "未知的检查错误";
	public static Long SYS_PARSEERR			= 10006L;	public static String SYS_PARSEERR_MSG		= "未知的解析错误";
	public static Long SYS_NOPERFORMER		= 10007L;	public static String SYS_NOPERFORMER_MSG	= "系统未配置任务执行类";
	
	public static Long SYS_MMJ_COMM			= 11000L;	public static String SYS_MMJ_COMM_MSG		= "密码机通讯失败";
	public static Long SYS_ERRTASKSERVER	= 11001L;	public static String SYS_ERRTASKSERVER_MSG	= "与后台任务服务器通讯失败";
	public static Long SYS_ERRTSJOBPACKAGE	= 11001L;	public static String SYS_ERRTSJOBPACKAGE_MSG= "组装后台服务通信报文失败";
	
	public static Long TASK_PERFOVERTIME	= 20000L;	public static String TASK_PERFOVERTIME_MSG	= "任务执行超时无返回";
	public static Long TASK_NOMXSJ			= 20001L;	public static String TASK_NOMXSJ_MSG		= "任务无明细记录";
	public static Long TASK_BLOCKOVERTIME	= 20002L;	public static String TASK_BLOCKOVERTIME_MSG	= "任务阻塞超时失败";
	
	public static Long YW_NOYDKH			= 21000L;	public static String YW_NOYDKH_MSG			= "对应供电局下的用户编号不存在";
	public static Long YW_NODSBM			= 21001L;	public static String YW_NODSBM_MSG			= "供电单位编码校验错误";
	public static Long YW_NORECORD			= 21002L;	public static String YW_NORECORD_MSG		= "对应查询的用户电表不存在";
	public static Long YW_ERRBGLX			= 21003L;	public static String YW_ERRBGLX_MSG			= "请求结构中的变更类型异常";
	public static Long YW_ERRFKMSDM			= 21004L;	public static String YW_ERRFKMSDM_MSG		= "请求结构中的费控模式代码异常";
	public static Long YW_ERRPARAMETER		= 21005L;	public static String YW_ERRPARAMETER_MSG	= "请求参数不可识别";
}
