package com.longshine.cams.fk.server;

public class FKConfigureKeys {
	/**定义配置文件名，缺省文件名：classpath:chargecontrol.xml，程序中发布时发布的配置文件名，
	 * 该文件可以在web.xml中配置&#60;context-param&#62;
	 * 配置&#60;param-name&#62;fkConfigLocation来修改。<br>
	 * 另外，也可以配置&#60;context-param&#62;配置&#60;param-name&#62;
	 * fkSiteConfigLocation参数配置外部路径配置参数超越系统定义的配置参数值。*/
	public static final String CAMS_FK_CONFIG_FILE = "classpath:chargecontrol.xml";
	/**设置费控服务的运行节点标识,配置标识为：property:cams.fk.system.cellid<br>
	 * 缺省值：FKS1
	 * 配置多个费控接口服务时，不同节点需要配置不同的值*/
	public static final String CAMS_FK_SYSTEM_CELLID_KEY = "cams.fk.system.cellid";
	public static final String CAMS_FK_SYSTEM_CELLID_DEFAULT = "FKS1";
	/**配置系统运行模式，配置标识为：property:cams.fk.system.mode<br>
	 * 缺省值：GD<br>
	 * 取值：GD-运行与广东模式，SW-运行与省外地市主站系统模式*/
	public static final String CAMS_FK_SYSTEM_MODE_KEY = "cams.fk.system.mode";
	public static final String CAMS_FK_SYSTEM_MODE_DEFAULT = "gd";
	// 配置系统通用的接口超时时间，单位：秒
	public static final String CAMS_FK_SYSTEM_OVERSECS_KEY = "cams.fk.system.oversecs";
	public static final String CAMS_FK_SYSTEM_OVERSECS_DEFAULT = "600";
	// 配置系统通用接口超时时间同步延时，单位：秒
	public static final String CAMS_FK_SYSTEM_OVERSECSDELAY_KEY = "cams.fk.system.oversecsdelay";
	public static final String CAMS_FK_SYSTEM_OVERSECSDELAY_DEFAULT = "10";
	// 配置是否需要将WebService交互内容生成为本地文件，保存用于调试查看，文件名规则是“任务标识.send/recv.xml
	public static final String CAMS_FK_SYSTEM_LOGWSFILE_KEY = "cams.fk.system.logwsfile";
	public static final String CAMS_FK_SYSTEM_LOGWSFILE_DEFAULT = "0";
	// 配置结果回送/数据提交的线程池数量
	public static final String CAMS_FK_SYSTEM_CALLBACKPROCESS_KEY = "cams.fk.system.callbackprocess";
	public static final String CAMS_FK_SYSTEM_CALLBACKPROCESS_DEFAULT = "10";
	// 配置任务执行的线程池数量
	public static final String CAMS_FK_SYSTEM_PERFORMPROCESS_KEY = "cams.fk.system.performprocess";
	public static final String CAMS_FK_SYSTEM_PERFORMPROCESS_DEFAULT = "10";
	// 配置任务执行的线程池数量
	public static final String CAMS_FK_SYSTEM_PERFORM_TASKS_MIN_KEY = "cams.fk.system.perform.tasks.min";
	public static final String CAMS_FK_SYSTEM_PERFORM_TASKS_MIN_DEFAULT = "2";
	// 配置任务执行的线程池数量
	public static final String CAMS_FK_SYSTEM_PERFORM_TASKS_MAX_KEY = "cams.fk.system.perform.tasks.max";
	public static final String CAMS_FK_SYSTEM_PERFORM_TASKS_MAX_DEFAULT = "10";
	// 任务执行优先级调度分配策略
	public static final String CAMS_FK_TASK_DISPATCH_STRATEGY_KEY = "cams.fk.task.dispatch.strategy";
	public static final String CAMS_FK_TASK_DISPATCH_STRATEGY_DEFAULT = "2:1:1";
	/**费控服务异步请求回调接口地址、主动调用营销服务的接口服务地址配置，配置标识为：property:cams.fk.task.callserver.addr<br>
	 * 格式为：http://ip:port/pmis/fkservice
	 * */
	public static final String CAMS_FK_TASK_CALLSERVER_ADDR_KEY = "cams.fk.task.callserver.addr";
	/**费控服务调用TaskServer的服务配置，配置标识为：property:cams.fk.task.taskserver.addr<br>
	 * 格式为：IP:PORT*/
	public static final String CAMS_FK_TASK_TASKSERVER_ADDR_KEY = "cams.fk.task.taskserver.addr";
	// 费控服务字符集编码，取值：GBK,UTF-8
	public static final String CAMS_FK_TASK_TASKSERVER_ENCODING_KEY = "cams.fk.task.taskserver.encoding";
	public static final String CAMS_FK_TASK_TASKSERVER_ENCODING_DEFAULT = "gbk";
	// 后台任务服务器XML报文使用\0作为发送本帧的结束标志，缺省值为0，不发送，配置为1表示发送XML字符串结束符。
	public static final String CAMS_FK_TASK_TASKSERVER_SENDTAIL_KEY = "cams.fk.task.taskserver.sendtail";
	public static final String CAMS_FK_TASK_TASKSERVER_SENDTAIL_DEFAULT = "0";
	// 费控服务调用TaskServer的服务器的超时时间，单位：秒
	public static final String CAMS_FK_TASK_TASKSERVER_RECVBUFF_KEY = "cams.fk.task.taskserver.recvbuff";
	public static final String CAMS_FK_TASK_TASKSERVER_RECVBUFF_DEFAULT = "4096";
	// 费控服务调用TaskServer的服务器的超时时间，单位：秒
	public static final String CAMS_FK_TASK_TASKSERVER_CONNECT_TIMEOUT_KEY = "cams.fk.task.taskserver.connect.timeout";
	public static final String CAMS_FK_TASK_TASKSERVER_CONNECT_TIMEOUT_DEFAULT = "5";
	// 费控服务调用TaskServer的服务器的超时时间，单位：秒
	public static final String CAMS_FK_TASK_TASKSERVER_SEND_TIMEOUT_KEY = "cams.fk.task.taskserver.connect.timeout";
	public static final String CAMS_FK_TASK_TASKSERVER_SEND_TIMEOUT_DEFAULT = "10";
	// 费控服务调用TaskServer的服务器的任务执行超时时间，单位：秒
	public static final String CAMS_FK_TASK_TASKSERVER_PERFORM_TIMEOUT_KEY = "cams.fk.task.taskserver.connect.timeout";
	public static final String CAMS_FK_TASK_TASKSERVER_PERFORM_TIMEOUT_DEFAULT = "120";
	// 费控服务调用密码机的服务配置，格式为：http://ip:port/pmis/fkservice
	public static final String CAMS_FK_TASK_MMJSERVER_YX_ADDR_KEY = "cams.fk.task.mmjserver.yx.addr";
	public static final String CAMS_FK_TASK_MMJSERVER_YX_ADDR_DEFAULT = "http://localhost:9002/";
	/**费控服务调用密码机的服务配置，配置标识为：property:cams.fk.task.mmjserver.jl.addr<br>
	 * 格式为：http://ip:port/pmis/fkservice
	 * */
	public static final String CAMS_FK_TASK_MMJSERVER_JL_ADDR_KEY = "cams.fk.task.mmjserver.jl.addr";
	//public static final String CAMS_FK_TASK_MMJSERVER_JL_ADDR_DEFAULT = "http://192.168.1.211:8080/MasterStation_HSM/services/";
	public static final String CAMS_FK_TASK_MMJSERVER_JL_ADDR_DEFAULT = "http://192.168.1.211:8080/MeteringService/services/";

	// 费控服务调用密码机的服务配置，格式为：http://ip:port/pmis/fkservice
	public static final String CAMS_FK_TASK_MMJ_YCKZ_FUNCTION_KEY = "cams.fk.task.mmj.yckz.function";
	public static final String CAMS_FK_TASK_MMJ_YCKZ_FUNCTION_DEFAULT = "I_FK_MMJ_YCKZ";
	// 费控服务调用密码机的服务配置，格式为：http://ip:port/pmis/fkservice
	public static final String CAMS_FK_TASK_MMJ_YCSFRZ_FUNCTION_KEY = "cams.fk.task.mmj.ycsfrz.function";
	public static final String CAMS_FK_TASK_MMJ_YCSFRZ_FUNCTION_DEFAULT = "I_FK_MMJ_JLYCSFRZ";
	// 费控服务调用密码机的运行状态，格式为：debug:release
	public static final String CAMS_FK_TASK_MMJ_RUNSTATUS_KEY = "cams.fk.task.mmj.runstatus";
	public static final String CAMS_FK_TASK_MMJ_RUNSTATUS_DEFAULT = "release";
	// 线程空闲休眠时间设置，单位：毫秒，缺省为100毫秒
	public static final String CAMS_FK_SYSTEM_IDLE_KEY = "cams.fk.system.idle";
	public static final String CAMS_FK_SYSTEM_IDLE_DEFAULT = "100";
	/**配置费控任务表表名，配置标识为：property:cams.fk.task.tablename<br>
	 * 缺省值：GDCAMS2.FK_TASK
	 * 用户名不同时，可以配置该参数*/
	public static final String CAMS_FK_TASK_TABLENAME_KEY = "cams.fk.task.tablename";
	public static final String CAMS_FK_TASK_TABLENAME_DEFAULT = "GDCAMS2.FK_TASK";
	/**配置费控任务明细表表名，配置标识为：property:cams.fk.taskmx.tablename<br>
	 * 缺省值：GDCAMS2.FK_TASK_MX
	 * 用户名不同时，可以配置该参数*/
	public static final String CAMS_FK_TASK_MX_TABLENAME_KEY = "cams.fk.taskmx.tablename";
	public static final String CAMS_FK_TASK_MX_TABLENAME_DEFAULT = "GDCAMS2.FK_TASK_MX";
	// 定义一次查询语句从数据库中最大可以缓存到内存中的行数限制
	public static final String CAMS_FK_TASK_DB_TYPE_KEY = "cams.fk.task.db.type";
	public static final String CAMS_FK_TASK_DB_TYPE_DEFAULT = "oracle";
	// 定义一次查询语句从数据库中最大可以缓存到内存中的行数限制
	public static final String CAMS_FK_TASK_DB_CACHEDROWS_MAX_KEY = "cams.fk.task.db.cachedrows.max";
	public static final String CAMS_FK_TASK_DB_CACHEDROWS_MAX_DEFAULT = "1000";
	/**配置地市局编码校验列表，配置标识为：property:cams.fk.task.citylist<br>
	 * 格式为：四位的地市编码，如东莞0319，多个地市之间使用半角逗号分隔。<br>
	 * 缺省值：0301,0302,... ...等广东的地市局编码*/
	public static final String CAMS_FK_TASK_CITYLIST_KEY = "cams.fk.task.citylist";
	public static final String CAMS_FK_TASK_CITYLIST_DEFAULT = "0301,0302,0304,0305,0306,0307,0308,0309,0312,0313,0314,0317,0318,0319,0320,0351,0352,0353";
	// 定义电表通信时的缺省规约参数项
	/**用于费控指令下发的表号设置规则，配置标识为：property:cams.fk.task.gycs.bh.rule<br>
	 * 缺省值：0
	 * 取值含义如下：0-涉及表号的位置使用12个0代替，1-使用表地址代替，不足12位时，前补0*/
	public static final String CAMS_FK_TASK_GYCS_BH_RULE_KEY = "cams.fk.task.gycs.bh.rule";
	public static final String CAMS_FK_TASK_GYCS_BH_RULE_DEFAULT = "0";
	// 用于费控指令下发的统一规约名称配置
	public static final String CAMS_FK_TASK_GYCS_PROTOCOL_KEY = "cams.fk.task.gycs.protocol";
	public static final String CAMS_FK_TASK_GYCS_PROTOCOL_DEFAULT = "nwsg.dlt2007";
	// 用于费控指令下发时的items的类型编码
	public static final String CAMS_FK_TASK_GYCS_ITEMTYPE_KEY = "cams.fk.task.gycs.itemtype";
	public static final String CAMS_FK_TASK_GYCS_ITEMTYPE_DEFAULT = "inner";
	// 端口号,取参数表CJ_CS_JLDCS中的字段CS_8904,测量点端口号：00H~1EH表示485端口1~31；1FH表示载波通道；20H表示无线；30H表示其他。缺省设置为0
	public static final String CAMS_FK_TASK_GYCS_COMMPORT_KEY = "cams.fk.task.gycs.commport";
	public static final String CAMS_FK_TASK_GYCS_COMMPORT_DEFAULT = "0";
	// 波特率，取参数表CJ_CS_JLDCS中的字段CS_8905，档案未配置时，使用缺省值为9600
	public static final String CAMS_FK_TASK_GYCS_BAUDRATE_KEY = "cams.fk.task.gycs.baudrate";
	public static final String CAMS_FK_TASK_GYCS_BAUDRATE_DEFAULT = "6";
	// 中继超时时间，单位：秒，缺省配置30秒
	public static final String CAMS_FK_TASK_GYCS_RELAYTIMEOUT_KEY = "cams.fk.task.gycs.relaytimeout";
	public static final String CAMS_FK_TASK_GYCS_RELAYTIMEOUT_DEFAULT = "30";
	// 校验方式，0：无校验；1：偶校验；2：奇校验；缺省为偶校验
	public static final String CAMS_FK_TASK_GYCS_CHECKTYPE_KEY = "cams.fk.task.gycs.checktype";
	public static final String CAMS_FK_TASK_GYCS_CHECKTYPE_DEFAULT = "1";
	// 数据位，5,6,7,8；缺省8位；
	public static final String CAMS_FK_TASK_GYCS_DATABIT_KEY = "cams.fk.task.gycs.databit";
	public static final String CAMS_FK_TASK_GYCS_DATABIT_DEFAULT = "8";
	// 停止位，0：1位；1：1.5位；2：2位；缺省1位
	public static final String CAMS_FK_TASK_GYCS_STOPBIT_KEY = "cams.fk.task.gycs.stopbit";
	public static final String CAMS_FK_TASK_GYCS_STOPBIT_DEFAULT = "0";
	// 操作者代码；缺省设置为cams
	public static final String CAMS_FK_TASK_GYCS_OPERCODE_KEY = "cams.fk.task.gycs.opercode";
	public static final String CAMS_FK_TASK_GYCS_OPERCODE_DEFAULT = "1122";
	// 定义营销通过FTP方式提交给计量系统的数据文件，系统下载后，进行文件处理，以下定义的配置适用于该过程。
	// 下载营销文件的FTP服务地址，格式：ftp://IP:PORT
	public static final String CAMS_FK_TASK_YXDATA_FTPADDR_KEY = "cams.fk.task.yxdata.ftppath";
	public static final String CAMS_FK_TASK_YXDATA_FTPADDR_DEFAULT = "ftp://192.168.1.231:21";
	// 下载营销文件的FTP数据根路径
	public static final String CAMS_FK_TASK_YXDATA_FTPPATH_KEY = "cams.fk.task.yxdata.ftppath";
	public static final String CAMS_FK_TASK_YXDATA_FTPPATH_DEFAULT = "/FK";
	// FTP下载用户名
	public static final String CAMS_FK_TASK_YXDATA_FTPUSER_KEY = "cams.fk.task.yxdata.ftpuser";
	public static final String CAMS_FK_TASK_YXDATA_FTPUSER_DEFAULT = "cams";
	// FTP下载用户密码
	public static final String CAMS_FK_TASK_YXDATA_FTPPASS_KEY = "cams.fk.task.yxdata.ftppass";
	public static final String CAMS_FK_TASK_YXDATA_FTPPASS_DEFAULT = "cams";
	// 下载到处理服务器后的临时路径
	public static final String CAMS_FK_TASK_YXDATA_TEMPPATH_KEY = "cams.fk.task.yxdata.temppath";
	public static final String CAMS_FK_TASK_YXDATA_TEMPPATH_DEFAULT = "/data/yx/fk/source";
	// 下载到处理服务器后的中间路径，用于管理正在处理的文件
	public static final String CAMS_FK_TASK_YXDATA_MIDPATH_KEY = "cams.fk.task.yxdata.midpath";
	public static final String CAMS_FK_TASK_YXDATA_MIDPATH_DEFAULT = "/data/yx/fk/mid";
	// 下载到处理服务器后的备份路径
	public static final String CAMS_FK_TASK_YXDATA_BACKUPPATH_KEY = "cams.fk.task.yxdata.backuppath";
	public static final String CAMS_FK_TASK_YXDATA_BACKUPPATH_DEFAULT = "/data/yx/fk/backup";
	// 计量系统通过FTP方式提交给营销系统的任务数据相关参数定义
	// 提交营销文件的FTP服务地址，格式：ftp://IP:PORT
	public static final String CAMS_FK_TASK_JLDATA_FTPADDR_KEY = "cams.fk.task.jldata.ftpaddr";
	public static final String CAMS_FK_TASK_JLDATA_FTPADDR_DEFAULT = "ftp://192.168.1.231:21";
	// 提交营销文件的FTP数据根路径
	public static final String CAMS_FK_TASK_JLDATA_FTPPATH_KEY = "cams.fk.task.jldata.ftppath";
	public static final String CAMS_FK_TASK_JLDATA_FTPPATH_DEFAULT = "/FKCB";
	// FTP上传用户名
	public static final String CAMS_FK_TASK_JLDATA_FTPUSER_KEY = "cams.fk.task.jldata.ftpuser";
	public static final String CAMS_FK_TASK_JLDATA_FTPUSER_DEFAULT = "cams";
	// FTP上传用户密码
	public static final String CAMS_FK_TASK_JLDATA_FTPPASS_KEY = "cams.fk.task.jldata.ftppass";
	public static final String CAMS_FK_TASK_JLDATA_FTPPASS_DEFAULT = "cams";
	// 上传前服务器的临时路径
	public static final String CAMS_FK_TASK_JLDATA_TEMPPATH_KEY = "cams.fk.task.jldata.temppath";
	public static final String CAMS_FK_TASK_JLDATA_TEMPPATH_DEFAULT = "/data/jl/fk/source";
	// 上传前服务器的中间路径，用于管理正在上传的文件
	public static final String CAMS_FK_TASK_JLDATA_MIDPATH_KEY = "cams.fk.task.jldata.midpath";
	public static final String CAMS_FK_TASK_JLDATA_MIDPATH_DEFAULT = "/data/jl/fk/mid";
	// 上传到服务器后的备份路径
	public static final String CAMS_FK_TASK_JLDATA_BACKUPPATH_KEY = "cams.fk.task.jldata.backuppath";
	public static final String CAMS_FK_TASK_JLDATA_BACKUPPATH_DEFAULT = "/data/jl/fk/backup";
}
