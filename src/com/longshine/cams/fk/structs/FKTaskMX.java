/**任务明细记录对象，每个任务包含0条或N条明细数据，每条数据包含一个费控用户操作最小单位对象，对应数据库表FK_TASK_MX
 * 其中的TASKMXID为本表中的主键，该键由归属任务的任务标识TASKID+'-'+N组成，其中N是第几条明细，没有实际含义，用于唯一区分明细记录
 * 对应数据库表的字段定义说明如下：
 **************************************************************************
 * Code           Data Type       Name                 Comment
 * TASKMXID       VARCHAR2(32)    任务明细标识         任务请求明细标识，格式：TASKID-N，N是1,2,3等序列
 * TASKID         VARCHAR2(32)    任务标识             对应的用户请求任务的任务标识，FK_TASK.TASKID
 * QXDWBM         VARCHAR2(16)    权限单位编码         用电客户对应的管理组织单位编码
 * YHBH           VARCHAR2(32)    用户编号             用电客户的用户编号
 * YHLX           VARCHAR2(4)     用户类型             10公线专变用户，11：专线专变用户，20：低压用户
 * CBFS           VARCHAR2(4)     抄表方式             41：专变费控用户表码，42：低压费控用户表码
 * FKMSDM         VARCHAR2(2)     费控模式             0本地 1远程 2 其他
 * BGLX           VARCHAR2(2)     变更类型             变更类型代码，代码含义另行定义
 * JLDBH          VARCHAR2(32)    计量点号             计量点编号，按照规范为户号+NN的序号表示
 * DBZCBH         VARCHAR2(32)    电表资产编号         电表资产编号
 * BDZ            VARCHAR2(32)    表地址               表地址，存储电表与终端的通信地址
 * ZDLJDZ         VARCHAR2(16)    终端逻辑地址         采集终端、集中器的逻辑地址
 * ZDZCBH         VARCHAR2(32)    终端资产编号         采集终端、集中器的资产编号
 * LXBZ           SMALLINT        类型标志             0为月冻结表码，1为实时表码，2为日冻结表码，3：费控电能表(费控终端)停复电状态，4：费控电能表(费控终端)保电电状态，5：费控模式状态字，F:以上全部抄读
 * YCLX           SMALLINT        异常类型             异常统计类型：1：终端在线率异常 2：自动抄表率异常
 * YCSJ           NUMBER(5,4)     异常数据值           异常数据值，采用小于1的小数形式表示，保留四位小数
 * FSYZ           VARCHAR2(20)    分散因子             分散因子；8字节(16个HEX字符)（0000+表号），用于费控远程身份认证业务
 * SJS1           VARCHAR2(20)    随机数1              随机数1，用于费控远程身份认证业务
 * MW             VARCHAR2(100)   密文                 随机数密文、密文、参数密文，用于费控远程身份认证、本地费控用户远程钱包退费、本地费控用户参数更新业务
 * AQMKXLH        VARCHAR2(20)    安全模块序列号       费控电能表安全模块（ESAM）序列号，八字节，用于获取远程身份认证结果业务
 * ZXJG           VARCHAR2(8)     执行结果             对应抄读结果、执行结果、远程身份认证结果、回抄结果，0-成功，其他为错误码
 * GDJE           NUMBER(10,2)    购电金额             购电金额，用于本地费控用于远程开户、本地费控用户远程充值业务
 * GDCS           NUMBER(8)       购电次数             购电次数，用于本地费控用户远程开户、本地费控用于远程充值
 * MAC1           VARCHAR2(20)    MAC1                 MAC1、MAC，用于业务中需要使用到MAC、MAC1地址的业务
 * MAC2           VARCHAR2(20)    MAC2                 MAC2，用于业务中需要用用到MAC2地址的业务
 * KGLC           VARCHAR2(2)     开关轮次             开关轮次，用于专变费控用于远程控制业务
 * KZLX           VARCHAR2(4)     控制类型             1A:拉闸，1B:合闸允许，1C:直接合闸，2A:报警，2B:报警解除，3A:保电，3B:保电解除
 * ZXCS           NUMBER(2)       执行次数             控制命令执行没有成功时的最大允许执行次数，默认为1，空或小于1的当1处理，用于专变费控用于远程控制业务
 * FKSJBS         VARCHAR2(2)     回抄数据标识         回抄数据标识、电价参数套标识，用于本地费控用户电价参数更新、获取电能表安全数据回抄结果业务
 * FKSJ           VARCHAR2(500)   回抄数据             回抄数据、电价参数，用于本地费控用户电价参数更新、获取电能表安全数据回抄结果业务
 * KGZT           VARCHAR2(2)     开关状态             开关状态，用于获取实时用电信息返回结果业务
 * BDZT           VARCHAR2(2)     保电状态             开关状态，用于获取实时用电信息返回结果业务
 * SJSJ           DATE            数据时间             数据时间
 * ZXYGZ          NUMBER(20,4)    正向有功总
 * ZXYGF          NUMBER(20,4)    正向有功峰
 * ZXYGP          NUMBER(20,4)    正向有功平
 * ZXYGG          NUMBER(20,4)    正向有功谷
 * ZXYGJ          NUMBER(20,4)    正向有功尖
 * ZXWGZ          NUMBER(20,4)    正向无功总
 * ZXWGF          NUMBER(20,4)    正向无功峰
 * ZXWGP          NUMBER(20,4)    正向无功平
 * ZXWGG          NUMBER(20,4)    正向无功谷
 * ZXWGJ          NUMBER(20,4)    正向无功尖
 * FXYGZ          NUMBER(20,4)    反向有功总
 * FXYGF          NUMBER(20,4)    反向有功峰
 * FXYGP          NUMBER(20,4)    反向有功平
 * FXYGG          NUMBER(20,4)    反向有功谷
 * FXYGJ          NUMBER(20,4)    反向有功尖
 * FXWGZ          NUMBER(20,4)    反向无功总
 * FXWGF          NUMBER(20,4)    反向无功峰
 * FXWGP          NUMBER(20,4)    反向无功平
 * FXWGG          NUMBER(20,4)    反向无功谷
 * FXWGJ          NUMBER(20,4)    反向无功尖
 * ZDXL           NUMBER(20,4)    最大需量
 * ZXYGA          NUMBER(20,4)    正向有功A相
 * ZXYGB          NUMBER(20,4)    正向有功B相
 * ZXYGC          NUMBER(20,4)    正向有功C相
 **************************************************************************
 */
package com.longshine.cams.fk.structs;

import java.math.BigDecimal;
import java.util.Date;

import com.longshine.cams.fk.server.FKConfiguration;
import com.longshine.cams.fk.server.FKConfigureKeys;

public class FKTaskMX {
	// 数据库字段定义
	public String     TASKMXID;   // 任务请求明细标识，格式：TASKID-N，N是1,2,3等序列
	public String     TASKID;     // 对应的用户请求任务的任务标识，FK_TASK.TASKID
	public String     ZLBM;       // 根据接口配置，设置任务的指令编码
	public String	  DSBM;		  // 供电单位编码，用于未来的分区字段
	public String     QXDWBM;     // 用电客户对应的管理组织单位编码
	public Date		  JLSJ;		  // 记录时间
	public String     YHBH;       // 用电客户的用户编号
	public String     YHLX;       // 10公线专变用户，11：专线专变用户，20：低压用户
	public String     CBFS;       // 41：专变费控用户表码，42：低压费控用户表码
	public String     FKMSDM;     // 0本地 1远程 2 其他
	public String     BGLX;       // 变更类型代码，代码含义另行定义
	public String     JLDBH;      // 计量点编号，按照规范为户号+NN的序号表示
	public String     DBZCBH;     // 电表资产编号
	public String     BDZ;        // 表地址，存储电表与终端的通信地址
	public String	  COMMPORT;	  // 电表通信端口号，数据库电表档案（CJ_CS_JLDCS.CS_8904）中没有配置时，使用缺省参数
	public String	  BAUDRATE;	  // 电表通信波特率，数据库电表档案（CJ_CS_JLDCS.CS_8905）中没有配置时，使用缺省参数
	public Long		  PERFORMSTEP;// 该记录处理的步骤，0-初始状态，10-数据库匹配到记录，20-密码机通讯完成，30-发送给后台服务，100-记录成功处理
	public Long		  ERRCODE;	  // 该记录的处理错误码，0-成功
	public String     ERRMSG;     // 该记录的错误描述信息
	public Long		  CBSXH;	  // 抄表顺序号
	public String     ZDLJDZ;     // 采集终端、集中器的逻辑地址
	public String	  GYBM;		  // 对应终端的终端规约
	public String     ZDZCBH;     // 采集终端、集中器的资产编号
	public String     LXBZ;       // 0为月冻结表码，1为实时表码，2为日冻结表码，3：费控电能表(费控终端)停复电状态，4：费控电能表(费控终端)保电电状态，5：费控模式状态字，F:以上全部抄读
	public Long       YCLX;       // 异常统计类型：1：终端在线率异常 2：自动抄表率异常
	public BigDecimal YCSJ;       // 异常数据值，采用小于1的小数形式表示，保留四位小数
	public String     FSYZ;       // 分散因子；8字节(16个HEX字符)（0000+表号），用于费控远程身份认证业务
	public String     SJS1;       // 费控远程身份认证的“随机数1”,费控模式本地切换远程的“钱包文件”
	public String     SJS2;       // 随机数2，用于费控远程控制的电表返回
	public String     MW1;        // 随机数密文、密文、参数密文，用于费控远程身份认证、本地费控用户远程钱包退费、本地费控用户参数更新业务
	public String     MW2;        // 随机数密文2，用于远程控制指令计量密码机加密返回
	public String     AQMKXLH;    // 费控电能表安全模块（ESAM）序列号，八字节，用于获取远程身份认证结果业务
	public String     ZXJG;       // 对应抄读结果、执行结果、远程身份认证结果、回抄结果，0-成功，其他为错误码
	public BigDecimal GDJE;       // 购电金额，用于本地费控用于远程开户、本地费控用户远程充值业务
//	public String 		GDJE;       // 购电金额，用于本地费控用于远程开户、本地费控用户远程充值业务
//	public String       GDCS;       // 购电次数，用于本地费控用户远程开户、本地费控用于远程充值
	public Long       GDCS;       // 购电次数，用于本地费控用户远程开户、本地费控用于远程充值
	public String     MAC1;       // MAC1、MAC，用于业务中需要使用到MAC、MAC1地址的业务
	public String     MAC2;       // MAC2，用于业务中需要用用到MAC2地址的业务
	public String     KGLC;       // 开关轮次，用于专变费控用于远程控制业务
	public String     KZLX;       // 1A:拉闸，1B:合闸允许，1C:直接合闸，2A:报警，2B:报警解除，3A:保电，3B:保电解除
	public String	  KZLX2;	  // 控制类型组合数据
	public Long       ZXCS;       // 控制命令执行没有成功时的最大允许执行次数，默认为1，空或小于1的当1处理，用于专变费控用于远程控制业务
	public String     FKSJBS;     // 回抄数据标识、电价参数套标识，用于本地费控用户电价参数更新、获取电能表安全数据回抄结果业务
	public String     FKSJ;       // 回抄数据、电价参数，用于本地费控用户电价参数更新、获取电能表安全数据回抄结果业务
	public String     KGZT;       // 开关状态，用于获取实时用电信息返回结果业务
	public String     BDZT;       // 开关状态，用于获取实时用电信息返回结果业务
	public Date       SJSJ;       // 数据时间
	public Date		  JLSJSJ;	  // 计量系统返回的数据时间，一般取系统中符合请求要求的具体的数据发生时间
	public BigDecimal ZXYGZ;
	public BigDecimal ZXYGF;
	public BigDecimal ZXYGP;
	public BigDecimal ZXYGG;
	public BigDecimal ZXYGJ;
	public BigDecimal ZXWGZ;
	public BigDecimal ZXWGF;
	public BigDecimal ZXWGP;
	public BigDecimal ZXWGG;
	public BigDecimal ZXWGJ;
	public BigDecimal FXYGZ;
	public BigDecimal FXYGF;
	public BigDecimal FXYGP;
	public BigDecimal FXYGG;
	public BigDecimal FXYGJ;
	public BigDecimal FXWGZ;
	public BigDecimal FXWGF;
	public BigDecimal FXWGP;
	public BigDecimal FXWGG;
	public BigDecimal FXWGJ;
	public BigDecimal ZDXL;
	public BigDecimal ZXYGA;
	public BigDecimal ZXYGB;
	public BigDecimal ZXYGC;
	// 以下属性为任务内存属性，无需持久化到数据库
	private String taskserverid;
	private boolean perform_continue;
	public String getTaskServerID() {
		return taskserverid;
	}
	public void setTaskServerID(String v_taskserverid) {
		this.taskserverid = v_taskserverid;
	}
	public boolean isPerformContinue() {
		return perform_continue;
	}
	public void setPerformContinue(boolean v_perform_continue) {
		this.perform_continue = v_perform_continue;
	}
	public void setErrorCodeMsg(Long v_errcode, String v_errmsg){
		this.ERRCODE = v_errcode;
		this.ERRMSG = v_errmsg;
	}
	public void setCommPortAndBaudRate(FKConfiguration v_config,String v_commport, String v_baudrate){
		if(v_commport != null && (!"".equals(v_commport)) && (!"-1".equals(v_commport)))	// 端口号合理性校验
			this.COMMPORT = v_commport;
		else	// 端口号不合理，使用系统缺省配置
			this.COMMPORT = v_config.getProperty(FKConfigureKeys.CAMS_FK_TASK_GYCS_COMMPORT_KEY);
		if(v_baudrate != null && (!"".equals(v_baudrate)) && (!"-1".equals(v_baudrate)))	// 端口号合理性校验
			this.BAUDRATE = v_baudrate;
		else	// 波特率不合理，使用系统缺省配置
			this.BAUDRATE = v_config.getProperty(FKConfigureKeys.CAMS_FK_TASK_GYCS_BAUDRATE_KEY);
	}
	// 初始化方法，用于从数据库装载任务恢复任务调度时生成任务
	public FKTaskMX(){
	}
	public FKTaskMX(FKTask v_task, long v_rec){
		this.TASKMXID = v_task.TASKID + "-" + v_rec;
		this.TASKID = v_task.TASKID;
		this.ZLBM = v_task.ZLBM;
		this.DSBM = v_task.DSBM;
		this.QXDWBM = v_task.QXDWBM;
		this.PERFORMSTEP = TaskAttribute.PerformStep.initial.getValue();
		this.perform_continue = true;
		this.taskserverid = this.TASKMXID;
		this.JLSJ = new Date();
	}
	@Override
	public String toString(){
		return "TASKMXID:" + TASKMXID + ";YHBH:" + YHBH + ";JLDBH:" + JLDBH + ";DBZCBH:" + DBZCBH;
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
		if(TASKMXID == null || "".equals(TASKMXID))
			return ret;
		String columns = "";
		String columnvalues = "";
		if(v_type == TaskAttribute.OperatorType.insert){
			if(TASKMXID != null && (!"".equals(TASKMXID))){ columns += ",TASKMXID"; columnvalues += ",'" + TASKMXID + "'";}
		}
		if(TASKID != null && (!"".equals(TASKID))){ columns += ",TASKID"; columnvalues += ",'" + TASKID + "'";}
		if(DSBM != null && (!"".equals(DSBM))){ columns += ",DSBM"; columnvalues += ",'" + DSBM + "'";}
		if(QXDWBM != null && (!"".equals(QXDWBM))){ columns += ",QXDWBM"; columnvalues += ",'" + QXDWBM + "'";}
		if(JLSJ != null){ columns += ",JLSJ"; columnvalues += "," + FKTask.DBDateTimeColumn(JLSJ) + "";}
		if(ZLBM != null && (!"".equals(ZLBM))){ columns += ",ZLBM"; columnvalues += ",'" + ZLBM + "'";}
		if(YHBH != null && (!"".equals(YHBH))){ columns += ",YHBH"; columnvalues += ",'" + YHBH + "'";}
		if(YHLX != null && (!"".equals(YHLX))){ columns += ",YHLX"; columnvalues += ",'" + YHLX + "'";}
		if(CBFS != null && (!"".equals(CBFS))){ columns += ",CBFS"; columnvalues += ",'" + CBFS + "'";}
		if(FKMSDM != null && (!"".equals(FKMSDM))){ columns += ",FKMSDM"; columnvalues += ",'" + FKMSDM + "'";}
		if(BGLX != null && (!"".equals(BGLX))){ columns += ",BGLX"; columnvalues += ",'" + BGLX + "'";}
		if(JLDBH != null && (!"".equals(JLDBH))){ columns += ",JLDBH"; columnvalues += ",'" + JLDBH + "'";}
		if(DBZCBH != null && (!"".equals(DBZCBH))){ columns += ",DBZCBH"; columnvalues += ",'" + DBZCBH + "'";}
		if(BDZ != null && (!"".equals(BDZ))){ columns += ",BDZ"; columnvalues += ",'" + BDZ + "'";}
		if(COMMPORT != null && (!"".equals(COMMPORT))){ columns += ",COMMPORT"; columnvalues += ",'" + COMMPORT + "'";}
		if(BAUDRATE != null && (!"".equals(BAUDRATE))){ columns += ",BAUDRATE"; columnvalues += ",'" + BAUDRATE + "'";}
		if(PERFORMSTEP != null){ columns += ",PERFORMSTEP"; columnvalues += "," + PERFORMSTEP + "";}
		if(ERRCODE != null){ columns += ",ERRCODE"; columnvalues += "," + ERRCODE + "";}
		if(ERRMSG != null && (!"".equals(ERRMSG))){ columns += ",ERRMSG"; columnvalues += ",'" + ERRMSG + "'";}
		if(CBSXH != null){ columns += ",CBSXH"; columnvalues += "," + CBSXH + "";}
		if(ZDLJDZ != null && (!"".equals(ZDLJDZ))){ columns += ",ZDLJDZ"; columnvalues += ",'" + ZDLJDZ + "'";}
		if(GYBM != null && (!"".equals(GYBM))){ columns += ",GYBM"; columnvalues += ",'" + GYBM + "'";}
		if(ZDZCBH != null && (!"".equals(ZDZCBH))){ columns += ",ZDZCBH"; columnvalues += ",'" + ZDZCBH + "'";}
		if(LXBZ != null && (!"".equals(ZDZCBH))){ columns += ",LXBZ"; columnvalues += ",'" + LXBZ + "'";}
		if(YCLX != null){ columns += ",YCLX"; columnvalues += "," + YCLX + "";}
		if(YCSJ != null){ columns += ",YCSJ"; columnvalues += "," + this.scaleAdjust(YCSJ,4) + "";}
		if(FSYZ != null && (!"".equals(FSYZ))){ columns += ",FSYZ"; columnvalues += ",'" + FSYZ + "'";}
		if(SJS1 != null && (!"".equals(SJS1))){ columns += ",SJS1"; columnvalues += ",'" + SJS1 + "'";}
		if(SJS2 != null && (!"".equals(SJS2))){ columns += ",SJS2"; columnvalues += ",'" + SJS1 + "'";}
		if(MW1 != null && (!"".equals(MW1))){ columns += ",MW1"; columnvalues += ",'" + MW1 + "'";}
		if(MW2 != null && (!"".equals(MW2))){ columns += ",MW2"; columnvalues += ",'" + MW2 + "'";}
		if(AQMKXLH != null && (!"".equals(AQMKXLH))){ columns += ",AQMKXLH"; columnvalues += ",'" + AQMKXLH + "'";}
		if(ZXJG != null && (!"".equals(ZXJG))){ columns += ",ZXJG"; columnvalues += ",'" + ZXJG + "'";}
    	if(GDJE != null){ columns += ",GDJE"; columnvalues += "," + this.scaleAdjust(GDJE,2) + "";}
		if(GDCS != null){ columns += ",GDCS"; columnvalues += "," + GDCS + "";}
		if(MAC1 != null && (!"".equals(MAC1))){ columns += ",MAC1"; columnvalues += ",'" + MAC1 + "'";}
		if(MAC2 != null && (!"".equals(MAC2))){ columns += ",MAC2"; columnvalues += ",'" + MAC2 + "'";}
		if(KGLC != null && (!"".equals(KGLC))){ columns += ",KGLC"; columnvalues += ",'" + KGLC + "'";}
		if(KZLX != null && (!"".equals(KZLX))){ columns += ",KZLX"; columnvalues += ",'" + KZLX + "'";}
		if(KZLX2 != null && (!"".equals(KZLX2))){ columns += ",KZLX2"; columnvalues += ",'" + KZLX2 + "'";}
		if(ZXCS != null){ columns += ",ZXCS"; columnvalues += "," + ZXCS + "";}
		if(FKSJBS != null && (!"".equals(FKSJBS))){ columns += ",FKSJBS"; columnvalues += ",'" + FKSJBS + "'";}
		if(FKSJ != null && (!"".equals(FKSJ))){ columns += ",FKSJ"; columnvalues += ",'" + FKSJ + "'";}
		if(KGZT != null && (!"".equals(KGZT))){ columns += ",KGZT"; columnvalues += ",'" + KGZT + "'";}
		if(BDZT != null && (!"".equals(BDZT))){ columns += ",BDZT"; columnvalues += ",'" + BDZT + "'";}
		if(SJSJ != null){ columns += ",SJSJ"; columnvalues += "," + FKTask.DBDateTimeColumn(SJSJ) + "";}
		if(JLSJSJ != null){ columns += ",JLSJSJ"; columnvalues += "," + FKTask.DBDateTimeColumn(JLSJSJ) + "";}
		if(ZXYGZ != null){ columns += ",ZXYGZ"; columnvalues += "," + this.scaleAdjust(ZXYGZ,4) + "";}
		if(ZXYGF != null){ columns += ",ZXYGF"; columnvalues += "," + this.scaleAdjust(ZXYGF,4) + "";}
		if(ZXYGP != null){ columns += ",ZXYGP"; columnvalues += "," + this.scaleAdjust(ZXYGP,4) + "";}
		if(ZXYGG != null){ columns += ",ZXYGG"; columnvalues += "," + this.scaleAdjust(ZXYGG,4) + "";}
		if(ZXYGJ != null){ columns += ",ZXYGJ"; columnvalues += "," + this.scaleAdjust(ZXYGJ,4) + "";}
		if(ZXWGZ != null){ columns += ",ZXWGZ"; columnvalues += "," + this.scaleAdjust(ZXWGZ,4) + "";}
		if(ZXWGF != null){ columns += ",ZXWGF"; columnvalues += "," + this.scaleAdjust(ZXWGF,4) + "";}
		if(ZXWGP != null){ columns += ",ZXWGP"; columnvalues += "," + this.scaleAdjust(ZXWGP,4) + "";}
		if(ZXWGG != null){ columns += ",ZXWGG"; columnvalues += "," + this.scaleAdjust(ZXWGG,4) + "";}
		if(ZXWGJ != null){ columns += ",ZXWGJ"; columnvalues += "," + this.scaleAdjust(ZXWGJ,4) + "";}
		if(FXYGZ != null){ columns += ",FXYGZ"; columnvalues += "," + this.scaleAdjust(FXYGZ,4) + "";}
		if(FXYGF != null){ columns += ",FXYGF"; columnvalues += "," + this.scaleAdjust(FXYGF,4) + "";}
		if(FXYGP != null){ columns += ",FXYGP"; columnvalues += "," + this.scaleAdjust(FXYGP,4) + "";}
		if(FXYGG != null){ columns += ",FXYGG"; columnvalues += "," + this.scaleAdjust(FXYGG,4) + "";}
		if(FXYGJ != null){ columns += ",FXYGJ"; columnvalues += "," + this.scaleAdjust(FXYGJ,4) + "";}
		if(FXWGZ != null){ columns += ",FXWGZ"; columnvalues += "," + this.scaleAdjust(FXWGZ,4) + "";}
		if(FXWGF != null){ columns += ",FXWGF"; columnvalues += "," + this.scaleAdjust(FXWGF,4) + "";}
		if(FXWGP != null){ columns += ",FXWGP"; columnvalues += "," + this.scaleAdjust(FXWGP,4) + "";}
		if(FXWGG != null){ columns += ",FXWGG"; columnvalues += "," + this.scaleAdjust(FXWGG,4) + "";}
		if(FXWGJ != null){ columns += ",FXWGJ"; columnvalues += "," + this.scaleAdjust(FXWGJ,4) + "";}
		if(ZDXL != null){ columns += ",ZDXL"; columnvalues += "," + this.scaleAdjust(ZDXL,4) + "";}
		if(ZXYGA != null){ columns += ",ZXYGA"; columnvalues += "," + this.scaleAdjust(ZXYGA,4) + "";}
		if(ZXYGB != null){ columns += ",ZXYGB"; columnvalues += "," + this.scaleAdjust(ZXYGB,4) + "";}
		if(ZXYGC != null){ columns += ",ZXYGC"; columnvalues += "," + this.scaleAdjust(ZXYGC,4) + "";}
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
	private BigDecimal scaleAdjust(BigDecimal v_bd, long v_scale){
		BigDecimal ret;
		if(v_scale < 0)	v_scale = 0;
		if(v_bd.scale() > v_scale)	// 如果小数位数大于4位，则为4位，忽略后续位数
			ret = v_bd.setScale((int)v_scale, BigDecimal.ROUND_DOWN);
		else
			ret = v_bd;
		return ret;
	}
}
