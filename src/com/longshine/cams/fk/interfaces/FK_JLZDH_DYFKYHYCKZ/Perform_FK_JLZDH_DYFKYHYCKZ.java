package com.longshine.cams.fk.interfaces.FK_JLZDH_DYFKYHYCKZ;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

import javax.sql.rowset.CachedRowSet;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.longshine.cams.fk.common.BaseTaskPerform;
import com.longshine.cams.fk.common.CAMSConstant;
import com.longshine.cams.fk.interfaces.common.FunUtils;
import com.longshine.cams.fk.interfaces.common.SQLConstant;
import com.longshine.cams.fk.server.FKConfigureKeys;
import com.longshine.cams.fk.server.FK_JLMMJ.VO_FK_MMJ_JLYCSFRZ_REQ_BD;
import com.longshine.cams.fk.server.FK_JLMMJ.VO_FK_MMJ_JLYCSFRZ_RES_BD;
import com.longshine.cams.fk.server.FK_JLMMJ.VO_FK_MMJ_YCKZ_REQ_BD;
import com.longshine.cams.fk.server.FK_JLMMJ.VO_FK_MMJ_YCKZ_RES_BD;
import com.longshine.cams.fk.server.mmj.MMJConstant;
import com.longshine.cams.fk.server.mmj.MMJFkUtil;
import com.longshine.cams.fk.structs.FKTask;
import com.longshine.cams.fk.structs.FKTaskMX;
import com.longshine.cams.fk.structs.ServiceError;
import com.longshine.cams.fk.structs.StringWrapper;
import com.longshine.cams.fk.structs.TaskAttribute;
import com.longshine.cams.fk.structs.TaskServerJob;

public class Perform_FK_JLZDH_DYFKYHYCKZ extends BaseTaskPerform {
	private String fkmmjws=MMJConstant.fkmmjws;
	// 设置与TaskServer通讯执行命令时的命令编码
	private static final String taskserv_cmd = "56";
	/** 任务执行准备，主要功能有两点，1-根据任务明细内容从数据库中扩展相关属性，2-根据任务明细记录，判断是否需要通过从密码机获取加密数据项
	 * 该虚方法需要实现，以实现任务数据的请求处理，如果只是获取数据库中的相关属性返回给调用方，则该方法即可完成任务
	 * @param v_task 待处理的任务
	 * @param v_oversecs 处理任务的超时时间限制
	 * @return  0 执行成功，
	 *          1 任务处理完成，无需进行后续处理，可设置任务完成状态
	 *         -1 所有明细密码机通讯失败，
	 *         -2 所有明细数据库通讯失败，
	 *         -3 所有明细系统中无符合记录，
	 *         -4 无明细记录
	 *         -5 任务为null异常
	 *         -6 计量密码机：从密码机获取远程身份认证加密信息失败
	 *         -7 TaskServer:计量远程身份认证指令下发失败
	 *         -8 计量密码机：停复电指令加密失败
	 *         -9 其他异常原因
	 */
	@Override
	public int TaskPrepare(FKTask v_task, long v_oversecs) {
		// TODO Auto-generated method stub
		int ret;
		int temp_int;
		if(v_task == null){ret = -5; return ret;}
		if(v_task.mxlist == null || v_task.mxlist.size() == 0){ret = -4; return ret;}
		// 从数据库中获取参数
		temp_int = this.selectTaskInfor(v_task);
		this.logger.debug("TaskPrepare: DBParameterExceptDeal:" + v_task +" temp_int:"+temp_int);
		ret = this.DBParameterExceptDeal(v_task, temp_int);
		if(ret == 0){	// 数据库处理成功后才执行后续步骤
			
			ret = this.TaskMW2Prepare(v_task, v_oversecs);
			this.logger.debug("TaskPrepare: TaskMW2Prepare:" + v_oversecs +" ;ret:"+ret);
		}
		return ret;
	}
	/**从密码机获取远程身份认证并下发到后台服务获取身份认证结果，并将控制指令进行加密，具体过程如下：
	 * 1、组织计量密码机远程身份认证报文,需要参数:TaskMX(BDZ)，返回请求VO对象
	 * 2、调用计量密码机获取远程身份认证随机数及密文,返回填充TaskMX(SJS1,MW1)
	 * 3、组织电表远程身份认证报文，需要参数：TaskMX(BDZ,SJS1,MW1)，返回请求XML报文
	 * 4、调用后台服务下发远程身份认证报文并获取认证随机数，返回成功后填充TaskMX(SJS2,AQMKXLH)
	 * 5、组织计量密码机远程控制，需要参数：TaskMX(SJS2,BDZ,AQMKXLH,KZLX),返回请求VO对象
	 * 6、调用计量密码机获取远程控制参数，返回填充TaskMX(MW2)
	 * @return  0：成功
	 *         -6：计量密码机：从密码机获取远程身份认证加密信息失败
	 *         -7：TaskServer:计量远程身份认证指令下发失败
	 *         -8：计量密码机：停复电指令加密失败
	 *         -9：其他异常原因
	 */
	private int TaskMW2Prepare(FKTask v_task, long v_oversecs){
		int ret = 0;
		FKTaskMX taskmx = v_task.mxlist.get(0);
		try{
			// 1、组织计量密码机远程身份认证报文,需要参数:TaskMX(BDZ)，返回请求VO对象
			this.logger.debug("TaskMW2Prepare: combine_MMJ_JLYCSFRZ_Request:" + taskmx);
			VO_FK_MMJ_JLYCSFRZ_REQ_BD jlycsfrz_req = this.combine_MMJ_JLYCSFRZ_Request(taskmx);
			if(jlycsfrz_req == null){
				this.logger.debug("TaskMWPrepare: combine_MMJ_JLYCSFRZ_Request Failed.");
				ret = -9;
				return ret;
			}
			//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
			// 2、调用计量密码机获取远程身份认证随机数及密文,返回填充TaskMX(SJS1,MW1)
			this.logger.debug("TaskMW2Prepare: getMMJ_JLYCSFRZ:" + jlycsfrz_req);
			VO_FK_MMJ_JLYCSFRZ_RES_BD jlycsfrz_res=null;
			if("1".equals(fkmmjws)){
				jlycsfrz_res = MMJFkUtil.getMMJ_JLYCSFRZ(jlycsfrz_req, v_task);
			}else{
				jlycsfrz_res = this.task_perform_mgr.getMMJ_JLYCSFRZ(jlycsfrz_req, TaskAttribute.MMJType.jl);
			}
			if(jlycsfrz_res == null){
				this.logger.debug("TaskMWPrepare: this.task_perform_mgr.getMMJ_JLYCSFRZ Failed.");
				ret = -6;
				return ret;
			}
			this.logger.debug("TaskMW2Prepare: filled_MMJ_JLYCSFRZ_Result:" + jlycsfrz_res);
			ret = this.filled_MMJ_JLYCSFRZ_Result(taskmx, jlycsfrz_res);
			if(ret != 0){
				this.logger.debug("TaskMWPrepare: this.filled_MMJ_JLYCSFRZ_Result Failed(" + ret + ").");
				return ret;
			}
			StringWrapper xml = new StringWrapper();
			String tsID = taskmx.TASKMXID + "-1";
			// 3、组织电表远程身份认证报文，需要参数：TaskMX(BDZ,SJS1,MW1)，返回请求XML报文
			this.logger.debug("TaskMW2Prepare: combine_TaskServer_YCSFRZ_Request:" + tsID);
			ret = this.combine_TaskServer_YCSFRZ_Request(tsID, taskmx, xml);
			if(ret != 0){
				this.logger.debug("TaskMW2Prepare: this.combine_TaskServer_YCSFRZ_Request Failed(" + ret + ").");
				return ret;
			}
			TaskServerJob tsjob = new TaskServerJob(tsID, taskmx.TASKMXID, xml.str,this.config.getPropertyLong(FKConfigureKeys.CAMS_FK_TASK_TASKSERVER_PERFORM_TIMEOUT_KEY));
			// 4、调用后台服务下发远程身份认证报文并获取认证随机数，返回成功后填充TaskMX(SJS2,AQMKXLH)
			this.logger.debug("TaskMW2Prepare: getTaskServerResult:" + xml);
			ret = this.getProcTaskperform().getTaskServerResult(tsjob);	// 0 - 处理成功;1 - 超时未返回;-1 - 其他异常或失败发生
			//////////////////////////////////ret =1超时
			if(ret != 0){
				this.logger.debug("TaskMW2Prepare: this.getProcTaskperform().getTaskServerResult Failed(" + ret + ").");
				ret = -7;
				return ret;
			}
			//this.logger.debug("TaskMW2Prepare: filled_TaskServer_YCSFRZ_Result:" + tsjob.getResponseXml());
			this.logger.debug("TaskMW2Prepare: filled_TaskServer_YCSFRZ_Result:" + tsjob.getResponseXml()+" | "+ tsjob);
			ret = this.filled_TaskServer_YCSFRZ_Result(taskmx, tsjob.getResponseXml());
			if(ret != 0){
				this.logger.debug("TaskMW2Prepare: this.filled_TaskServer_YCSFRZ_Result Failed(" + ret + ").");
				ret = -7;
				return ret;
			}
			// 5、组织计量密码机远程控制，需要参数：TaskMX(SJS2,BDZ,AQMKXLH,KZLX),返回请求VO对象
			this.logger.debug("TaskMW2Prepare: combine_MMJ_YCKZ_Request.............");
			VO_FK_MMJ_YCKZ_REQ_BD yckz_req = this.combine_MMJ_YCKZ_Request(taskmx);
			if(yckz_req == null){
				this.logger.debug("TaskMW2Prepare: combine_MMJ_YCKZ_Request Failed.");
				ret = -9;
				return ret;
			}
			// 6、调用计量密码机获取远程控制参数，返回填充TaskMX(MW2)
			this.logger.debug("TaskMW2Prepare: getMMJ_YCKZ:" + yckz_req);
			//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
			VO_FK_MMJ_YCKZ_RES_BD yckz_res=null;
			if("1".equals(fkmmjws)){
					yckz_res = MMJFkUtil.getMMJ_YCKZ(yckz_req,v_task);
			}else{
					yckz_res = this.task_perform_mgr.getMMJ_YCKZ(yckz_req);
			}
			if(yckz_res == null){
				this.logger.debug("TaskMW2Prepare: this.task_perform_mgr.getMMJ_YCKZ Failed.");
				ret = -8;
				return ret;
			}
			this.logger.debug("TaskMW2Prepare: filled_MMJ_YCKZ_Result:" + yckz_res);
			ret = this.filled_MMJ_YCKZ_Result(taskmx, yckz_res);
			if(ret != 0){
				this.logger.debug("TaskMW2Prepare: this.filled_MMJ_YCKZ_Result Failed(" + ret + ").");
				return ret;
			}
		}catch(Exception e1){
			this.logger.warn("TaskMW2Prepare Exception:" + e1);
			ret = -9;
		}
		return ret;
	}
	// 1、组织计量密码机远程身份认证报文,需要参数:TaskMX(BDZ)，返回请求VO对象
	private VO_FK_MMJ_JLYCSFRZ_REQ_BD combine_MMJ_JLYCSFRZ_Request(FKTaskMX v_taskmx){
		VO_FK_MMJ_JLYCSFRZ_REQ_BD jlycsfrz = new VO_FK_MMJ_JLYCSFRZ_REQ_BD();
		jlycsfrz.setMYZT(this.mmj_runstatus.getValue());
		jlycsfrz.setFSYZ("0000" + this.CreateParamBH(v_taskmx.BDZ));
		this.logger.debug("BDZ"+v_taskmx.BDZ);
		return jlycsfrz;
	}
	/**2、调用计量密码机获取远程身份认证随机数及密文,返回填充TaskMX(SJS1,MW1)
	 * @param v_taskmx
	 * @param v_res
	 * @return  0：成功
	 *         -6：计量密码机：从密码机获取远程身份认证加密信息失败
	 */
	private int filled_MMJ_JLYCSFRZ_Result(FKTaskMX v_taskmx, VO_FK_MMJ_JLYCSFRZ_RES_BD v_res){
		if(v_res == null || v_res.getJSZT() == null)
			return -6;
		if(v_res.getJSZT() == 0 || v_res.getJSZT() == -1){
			v_taskmx.SJS1 = v_res.getMMJSJS();
			v_taskmx.MW1 = v_res.getSJSMW();
			return 0;
		}
		// 不是0 或 -1 则返回密码机返回结果失败
		return -6;
	}
	/**3、组织电表远程身份认证报文，需要参数：TaskMX(BDZ,SJS1,MW1)，返回请求XML报文
	 * 下发TaskServer的指令ID:TaskMX.TASKMXID + "-1"
	 * cmd.cmdtype=48
	 * @return 0 - 处理成功
	 *         -1 处理失败
	 */
	private int combine_TaskServer_YCSFRZ_Request(String v_tsID, FKTaskMX v_taskmx, StringWrapper v_xml){
		int ret = 0;
		try{
			Document document = DocumentHelper.createDocument();
			Element root = document.addElement("tasks");
			Element task = root.addElement("task");
			task.addAttribute("id", v_tsID);
			task.addElement("termaddr").setText(v_taskmx.ZDLJDZ);;	// 终端逻辑地址
			task.addElement("mpaddr").setText(v_taskmx.CBSXH + ":" + v_taskmx.BDZ);	// 抄表顺序号:表地址
			task.addElement("protocol").setText(this.config.getProperty(FKConfigureKeys.CAMS_FK_TASK_GYCS_PROTOCOL_KEY));	// 使用固定配置
			task.addElement("city").setText(FunUtils.zzjgbm(v_taskmx.QXDWBM));
			task.addElement("oper").setText("0");
//			this.logger.debug("QXDWBM:"+zzjgbm(v_taskmx.QXDWBM));
			task.addElement("cmd").addAttribute("cmdtype","sys").setText("48");
			task.addElement("ttl").setText(this.config.getProperty(FKConfigureKeys.CAMS_FK_TASK_TASKSERVER_PERFORM_TIMEOUT_KEY));
			Element items = task.addElement("items");
			items.addAttribute("itemtype", this.config.getProperty(FKConfigureKeys.CAMS_FK_TASK_GYCS_ITEMTYPE_KEY));
			items.addElement("item").addAttribute("id", "07000001").addText(v_taskmx.MW1);
			items.addElement("item").addAttribute("id", "07000002").addText(v_taskmx.SJS1);
//			items.addElement("item").addAttribute("id", "07000003").addText("0000000000000000");
			items.addElement("item").addAttribute("id", "07000003").addText("0000" + this.CreateParamBH(v_taskmx.BDZ));
			this.logger.debug("BDZ2:" + v_taskmx.BDZ + ";CreateParamBH:" + this.CreateParamBH(v_taskmx.BDZ));
			Element aux = task.addElement("aux");
			aux.addElement("info").addAttribute("name", "BaudRate").addText(v_taskmx.BAUDRATE);
			aux.addElement("info").addAttribute("name", "CommPort").addText(v_taskmx.COMMPORT);
			aux.addElement("info").addAttribute("name", "RelayTimeOut").addText(this.config.getProperty(FKConfigureKeys.CAMS_FK_TASK_GYCS_RELAYTIMEOUT_KEY));
			aux.addElement("info").addAttribute("name", "CheckType").addText(this.config.getProperty(FKConfigureKeys.CAMS_FK_TASK_GYCS_CHECKTYPE_KEY));
			aux.addElement("info").addAttribute("name", "DataBit").addText(this.config.getProperty(FKConfigureKeys.CAMS_FK_TASK_GYCS_DATABIT_KEY));
			aux.addElement("info").addAttribute("name", "StopBit").addText(this.config.getProperty(FKConfigureKeys.CAMS_FK_TASK_GYCS_STOPBIT_KEY));
			aux.addElement("info").addAttribute("name", "OperCode").addText(this.config.getProperty(FKConfigureKeys.CAMS_FK_TASK_GYCS_OPERCODE_KEY));
			
			v_xml.str = document.asXML();
			ret = 0;
		}catch(Exception e1){
			this.logger.warn("combine_TaskServer_YCSFRZ_Request Exception:" + e1);
			ret = -1;
		}
		return ret;
	}
	
//	//取0304XXX，第三四位值
//			public  String zzjgbm(String qxdwbm) {
//				if(qxdwbm!=null && qxdwbm.length()>2){
//					if(qxdwbm.length()>3){
//						return qxdwbm.substring(2, 4);
//					}else{
//						return qxdwbm.substring(2, 3);
//					}
//				}else{
//					return qxdwbm;
//				}
//				
//			}
			
	/**4、调用后台服务下发远程身份认证报文并获取认证随机数，返回成功后填充TaskMX(SJS2,AQMKXLH)
	 * 调用并返回，按照this.config.getProperty(FKConfigureKeys.CAMS_FK_TASK_TASKSERVER_PERFORM_TIMEOUT_KEY)配置秒数等待返回值
	 * @return  0:处理成功
	 *         -1:TaskServer:计量远程身份认证指令下发失败
	 *         其他值为后台返回电表的指令编码
	 */
	private int filled_TaskServer_YCSFRZ_Result(FKTaskMX v_taskmx, String v_xml){
		int ret = 0;
		try{
			Document document = DocumentHelper.parseText(v_xml);

			String xpath_taskid		= "/tasks/task[@id]";						// task节点路径，以便获取taskid
			String xpath_zxjg	    = "/tasks/task/status";						// 执行结果
			String xpath_errmsg		= "/tasks/task/message";					// 错误信息
			String xpath_sjs2		= "/tasks/task/items/item/data[@id='7000002']";		// 随机数2
			String xpath_aqmkxlh	= "/tasks/task/items/item/data[@id='70000F1']";		// 安全模块序列号
			
			Element ele_taskid		= (Element)document.selectSingleNode(xpath_taskid);
			Element ele_zxjg	    = (Element)document.selectSingleNode(xpath_zxjg);
			Element ele_errmsg		= (Element)document.selectSingleNode(xpath_errmsg);
			Element ele_sjs2		= (Element)document.selectSingleNode(xpath_sjs2);
			Element ele_aqmkxlh		= (Element)document.selectSingleNode(xpath_aqmkxlh);
			
			String taskid = null, zxjg = null, errmsg = null, sjs2 = null, aqmkxlh = null;
			if(ele_taskid != null)		taskid		= ele_taskid.attributeValue("id");
			if(ele_zxjg != null)	    zxjg	    = ele_zxjg.getText();
			if(ele_errmsg != null)		errmsg		= ele_errmsg.getText();
			if(ele_sjs2 != null)		sjs2		= ele_sjs2.getText();
			if(ele_aqmkxlh != null)		aqmkxlh		= ele_aqmkxlh.getText();
			if("0".equals(zxjg)){
				if(ele_sjs2 == null && ele_aqmkxlh ==null ){
					zxjg="-1";
					errmsg="终端返回异常";
					this.logger.debug( "终端状态返回0、但是没有返回值："+errmsg);
				}
			}
			this.logger.debug("parseTaskServerResult:taskid:" + taskid + ";zxjg:" + zxjg + ";errmsg:" + errmsg);
			v_taskmx.ZXJG = zxjg;
			v_taskmx.SJS2 = sjs2;
			v_taskmx.AQMKXLH = aqmkxlh;
			v_taskmx.setErrorCodeMsg(Long.parseLong(zxjg), errmsg);
			ret = Integer.parseInt(zxjg);
		}catch(Exception e){
			this.logger.warn("combineXMLRequest Exception:" + e);
			ret = -1;
		}
		return ret;
	}
	// 5、组织计量密码机远程控制，需要参数：TaskMX(SJS2,BDZ,AQMKXLH,KZLX),返回请求VO对象
	private VO_FK_MMJ_YCKZ_REQ_BD combine_MMJ_YCKZ_Request(FKTaskMX v_taskmx){
		VO_FK_MMJ_YCKZ_REQ_BD yckz = new VO_FK_MMJ_YCKZ_REQ_BD();
		yckz.setMYZT(this.mmj_runstatus.getValue());
		yckz.setMMJSJS(v_taskmx.SJS2);
		yckz.setFSYZ("0000" + this.CreateParamBH(v_taskmx.BDZ));
		yckz.setAQMKXLH(v_taskmx.AQMKXLH);
		Date now = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(now);
		calendar.add(Calendar.MINUTE, 10);
		Date limit = calendar.getTime();
		v_taskmx.KZLX2 = v_taskmx.KZLX + "00" + CAMSConstant.DF_YYMMDDHH24MISS.format(limit);
		yckz.setKZMLSJ(v_taskmx.KZLX2);
		return yckz;
	}
	/**6、调用计量密码机获取远程控制参数，返回填充TaskMX(MW2)
	 * @return  0：成功
	 *         -8：计量密码机：停复电指令加密失败
	 */
	private int filled_MMJ_YCKZ_Result(FKTaskMX v_taskmx, VO_FK_MMJ_YCKZ_RES_BD v_res){
		if(v_res == null)
			return -8;
		if(v_res.getJSZT() != 0)
			return -8;
		v_taskmx.MW2 = v_res.getKZMLMW();
		return 0;
	}
	/**根据selectTaskInfor错误信息进行错误处理
	 * @param v_task
	 * @param v_errcode
	 * @return 0 : 处理成功；-2 : 所有明细数据库通讯失败；-3 : 所有明细系统中无符合记录
	 */
	private int DBParameterExceptDeal(FKTask v_task, int v_errcode){
		int ret = 0;
		if(v_errcode < 0){	// 数据库查询异常
			for(FKTaskMX taskmx:v_task.mxlist){
				taskmx.ZXJG = ServiceError.SYS_FAULTDB.toString();
				taskmx.setErrorCodeMsg(ServiceError.SYS_FAULTDB, ServiceError.SYS_FAULTDB_MSG);
				taskmx.setPerformContinue(false);
			}
			ret = -2;
		}
		else{	// 对于数据库没有找到记录进行参数填充的记录设置为YW_NORECORD
			for(FKTaskMX taskmx:v_task.mxlist){
				if(taskmx.PERFORMSTEP == TaskAttribute.PerformStep.initial.getValue()){
					taskmx.ZXJG = ServiceError.YW_NORECORD.toString();
					taskmx.setErrorCodeMsg(ServiceError.YW_NORECORD, ServiceError.YW_NORECORD_MSG);
					taskmx.setPerformContinue(false);
				}
			}
			if(v_errcode == 0)
				ret = -3;
		}
		return ret;
	}
	/**@return >= 0 : 返回填充成功的记录数，-1 : 数据库语句执行错误
	 */
	@Override
	protected int selectTaskInfor(FKTask v_task) {
		// TODO Auto-generated method stub
		int ret = 0;
		String sql = null;
		FKTaskMX taskmx = v_task.mxlist.get(0);
		String cond_yhbh = taskmx.YHBH;
		String cond_zcbh = taskmx.DBZCBH;
		CachedRowSet rows = null;
		try{
			// 首先检查低压用户，如果有行集返回，则不再检查高压用户
			sql = this.getTaskInforSQL(v_task, TaskAttribute.YDKHType.dyjm, cond_yhbh, cond_zcbh);
			this.logger.debug("[selectTaskInfor]begin get db infor:" + sql);
			rows = this.querySQL(sql);
			if(rows.size() == 0){	// 不是低压用户
				this.closeConnectionResource(rows);
				// 如果没有低压用户，则检查高压用户
				sql = this.getTaskInforSQL(v_task, TaskAttribute.YDKHType.gy, cond_yhbh, cond_zcbh);
				this.logger.debug("[selectTaskInfor]begin get db infor:" + sql);
				rows = this.querySQL(sql);
			}
			if(rows.size() > 0){	// 有查询到返回记录，需要进行明细记录匹配填充
				ret = this.filledFKTaskMX(v_task, rows);
				if(ret == 0){	// 没有填充一条明细记录
				}
			}
		}catch(SQLException e1){
			this.logger.warn("SQL(" + sql + ") Exception:" + e1);
			ret = -1;
		}finally{
			this.closeConnectionResource(rows);
		}
		return ret;
	}
	private String getTaskInforSQL(FKTask v_task, TaskAttribute.YDKHType v_yhlx, String ...v_params){
		String ret = null;
		if(v_params.length == 1){	// 只拼接YHBH条件
			ret = (v_yhlx == TaskAttribute.YDKHType.dyjm) ? SQLConstant.sql_infor_dyjm_yhbh : SQLConstant.sql_infor_gy_yhbh;
			ret = ret.replace("${YHBH}", v_params[0]);	// 拼接YHBH条件
		}else if(v_params.length >= 2){	// 只拼接YHBH和ZCBH条件
			ret = (v_yhlx == TaskAttribute.YDKHType.dyjm) ? SQLConstant.sql_infor_dyjm_yhbh_zcbh : SQLConstant.sql_infor_gy_yhbh_zcbh;
			ret = ret.replace("${YHBH}", v_params[0]);	// 拼接YHBH条件
			ret = ret.replace("${ZCBH}", v_params[1]);	// 拼接ZCBH条件
		}else{
			ret = (v_yhlx == TaskAttribute.YDKHType.dyjm) ? SQLConstant.sql_infor_dyjm : SQLConstant.sql_infor_gy;
		}
		ret = ret.replace("${DSBM}", v_task.DSBM);	// 更新DSBM参数，省外没有改参数，运行无影响
		ret = ret.replace("${MAX_CACHE_ROWS}", String.valueOf(this.max_cached_rows));	// rownum特性限制行数
		return ret;
	}
	/**根据数据库结果进行结构参数填充
	 * @param v_task
	 * @param v_rows
	 * @return 填充成功的记录数
	 */
	private int filledFKTaskMX(FKTask v_task, CachedRowSet v_rows){
		int ret = 0;
		String yhbh, zcbh;
		boolean bfetch;
		try{
			while(v_rows.next()){
				yhbh = v_rows.getString("YHBH");
				zcbh = v_rows.getString("ZCBH");
//				jldh = v_rows.getString(3);
				bfetch = false;
				FKTaskMX taskmx = v_task.mxlist.get(0);
				if(zcbh != null){
					if(zcbh.equals(taskmx.DBZCBH)){
						if(yhbh != null){
							if(yhbh.equals(taskmx.YHBH)){
								bfetch = true;
							}
						}
					}
				}
				if(bfetch){	// 匹配到明细记录
					if(taskmx.PERFORMSTEP == TaskAttribute.PerformStep.initial.getValue()){
						taskmx.ZDLJDZ = v_rows.getString("ZDLJDZ");
						taskmx.CBSXH = v_rows.getLong("CBSXH");
						taskmx.BDZ = v_rows.getString("BDZ");
						taskmx.GYBM = v_rows.getString("GYBM");
						taskmx.setCommPortAndBaudRate(this.config, v_rows.getString("COMMPORT"), v_rows.getString("BAUDRATE"));
						taskmx.PERFORMSTEP = TaskAttribute.PerformStep.dbmatched.getValue();
						ret += 1;
					}
				}
			}
		}catch(Exception e1){
			this.logger.warn("filledFKTaskMX Exception:" + e1 + ";Message:" + e1.getStackTrace());
		}
		return ret;
	}
	@Override
	public int combineTaskServerRequest(FKTask v_task, FKTaskMX v_taskmx, Long v_oversecs, StringWrapper v_xml) {
		// TODO Auto-generated method stub
		int ret = 1;	// 设置为异常
		if(v_taskmx == null){
			ret = -1;
			return ret;
		}
		try{
			Document document = DocumentHelper.createDocument();
			Element root = document.addElement("tasks");
			Element task = root.addElement("task");
			task.addAttribute("id", v_taskmx.getTaskServerID());
			task.addElement("termaddr").setText(v_taskmx.ZDLJDZ);	// 终端逻辑地址
			task.addElement("mpaddr").setText(v_taskmx.CBSXH + ":" + v_taskmx.BDZ);	// 抄表顺序号:表地址
			task.addElement("protocol").setText(this.config.getProperty(FKConfigureKeys.CAMS_FK_TASK_GYCS_PROTOCOL_KEY));	// 使用固定配置
			task.addElement("city").setText(FunUtils.zzjgbm(v_taskmx.QXDWBM));
			task.addElement("oper").setText("0");
			task.addElement("cmd").addAttribute("cmdtype","sys").setText(taskserv_cmd);
			task.addElement("ttl").setText(v_oversecs.toString());
			Element items = task.addElement("items");
			items.addAttribute("itemtype", this.config.getProperty(FKConfigureKeys.CAMS_FK_TASK_GYCS_ITEMTYPE_KEY));

			Element aux = task.addElement("aux");
			aux.addElement("info").addAttribute("name", "BaudRate").addText(v_taskmx.BAUDRATE);
			aux.addElement("info").addAttribute("name", "CommPort").addText(v_taskmx.COMMPORT);
			aux.addElement("info").addAttribute("name", "RelayTimeOut").addText(this.config.getProperty(FKConfigureKeys.CAMS_FK_TASK_GYCS_RELAYTIMEOUT_KEY));
			aux.addElement("info").addAttribute("name", "CheckType").addText(this.config.getProperty(FKConfigureKeys.CAMS_FK_TASK_GYCS_CHECKTYPE_KEY));
			aux.addElement("info").addAttribute("name", "DataBit").addText(this.config.getProperty(FKConfigureKeys.CAMS_FK_TASK_GYCS_DATABIT_KEY));
			aux.addElement("info").addAttribute("name", "StopBit").addText(this.config.getProperty(FKConfigureKeys.CAMS_FK_TASK_GYCS_STOPBIT_KEY));
			aux.addElement("info").addAttribute("name", "AmmCtrlPasswd").addText("96000000"); /*电表控制密码，密文拉闸时填0即可，4个字节PAP0P1P2*/
			aux.addElement("info").addAttribute("name", "OperCode").addText(this.config.getProperty(FKConfigureKeys.CAMS_FK_TASK_GYCS_OPERCODE_KEY));
			aux.addElement("info").addAttribute("name", "kzlx").addText(v_taskmx.KZLX2);  /*控制类型，1A:拉闸；1B:合闸允许；1C:直接合闸；2A:报警；2B:报警解除；3A:保电；3B:保电解除*/
			aux.addElement("info").addAttribute("name", "mac").addText(v_taskmx.MW2); /*密文*/
			
			v_xml.str = document.asXML();
			this.logger.debug("combineTaskServerRequest:" + v_xml.str);
			ret = 0;
		}catch(Exception e){
			this.logger.warn("combineTaskServerRequest Exception:" + e);
			v_xml.str = null;
			ret = 1;
		}
		return ret;
	}
	@Override
	public int parseTaskServerResult(FKTask v_task, FKTaskMX v_taskmx, String v_xml) {
		// TODO Auto-generated method stub
		int ret = 0;
		this.logger.debug("parseTaskServerResult:" + v_xml);
		try{
			Document document = DocumentHelper.parseText(v_xml);

			String xpath_taskid		= "/tasks/task[@id]";						// task节点路径，以便获取taskid
			String xpath_zxjg	    = "/tasks/task/status";						// 电价更新结果
			String xpath_errmsg		= "/tasks/task/message";					// 错误信息
			
			Element ele_taskid		= (Element)document.selectSingleNode(xpath_taskid);
			Element ele_zxjg	    = (Element)document.selectSingleNode(xpath_zxjg);
			Element ele_errmsg		= (Element)document.selectSingleNode(xpath_errmsg);
			
			String taskid = null, zxjg = null, errmsg = null;
			if(ele_taskid != null)		taskid		= ele_taskid.attributeValue("id");
			if(ele_zxjg != null)	    zxjg	    = ele_zxjg.getText();
			if(ele_errmsg != null)		errmsg		= ele_errmsg.getText();
			
			this.logger.debug("parseTaskServerResult:taskid:" + taskid + ";zxjg:" + zxjg + ";errmsg:" + errmsg);
			v_taskmx.ZXJG	= zxjg;
			v_taskmx.setErrorCodeMsg(Long.parseLong(zxjg), errmsg);
		}catch(Exception e){
			this.logger.warn("parseTaskServerResult Exception:" + e);
			ret = -1;
		}
		return ret;
	}
	@Override
	public void setTaskMXErrorCode(FKTaskMX v_taskmx, Long v_errcode) {
		// TODO Auto-generated method stub
		if(v_taskmx != null)
			v_taskmx.ZXJG = v_errcode.toString();
	}
}
