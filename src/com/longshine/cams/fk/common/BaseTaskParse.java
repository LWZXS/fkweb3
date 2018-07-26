package com.longshine.cams.fk.common;

import java.util.Map;

import javax.sql.DataSource;

import com.longshine.cams.fk.server.FKConfiguration;
import com.longshine.cams.fk.server.FKConfigureKeys;
import com.longshine.cams.fk.server.FKServer;
import com.longshine.cams.fk.server.FKTaskParseManager;
import com.longshine.cams.fk.structs.FKTask;
import com.longshine.cams.fk.structs.InterfaceProperty;
import com.longshine.cams.fk.structs.ServiceError;
import com.longshine.cams.fk.structs.TaskAttribute;

public abstract class BaseTaskParse extends BaseDao{
	// 系统配置对象
	protected FKConfiguration config;
	// 保存对任务管理器的引用
	protected FKTaskParseManager task_parse_mgr;
	// 任务解析对象的方法名
	protected String function_name;
	// 保存接口配置项引用
	protected InterfaceProperty property;
	// 配置地市校验列表
	protected Map<String, String> citylist;
	// 费控通信中表号规则，取值含义如下：0-涉及表号的位置使用12个0代替，1-使用表地址代替，不足12位时，前补0
	protected Integer bh_rule;
	// 运行模式，gd为广东模式，sw为省外模式
	protected TaskAttribute.SystemMode run_mode;
	// 初始化任务解析对象
	public void Initialized(FKConfiguration v_config,InterfaceProperty v_prop,DataSource v_ds,String v_func){
		this.config = v_config;
		this.property = v_prop;
		this.datasource = v_ds;
		this.function_name = v_func;
		this.max_cached_rows = (int)this.config.getPropertyLong(FKConfigureKeys.CAMS_FK_TASK_DB_CACHEDROWS_MAX_KEY);
		this.task_parse_mgr = FKTaskParseManager.getInstance();
		this.citylist = this.config.getCitylist();
		this.bh_rule = this.config.getPropertyInteger(FKConfigureKeys.CAMS_FK_TASK_GYCS_BH_RULE_KEY);
		this.run_mode = FKServer.getRunMode();
	}
	public boolean cityCheck(String v_gddwbm){
		if(citylist == null)	// 无需进行供电单位编码校验
			return true;
		if((v_gddwbm == null) || "".equals(v_gddwbm))
			return false;
		String str_temp = v_gddwbm.length() > 4 ? v_gddwbm.substring(0, 4) : v_gddwbm;
		if(citylist.containsKey(str_temp))
			return true;
		else
			return false;
	}
	// 任务处理方法，接口实现方法中调用该方法，将用户调用参数转化的POJO传入，开始进行任务处理
	public abstract FKTask TaskBuilder(Object v_pojo);
	// 任务参数检查，按照业务设计的检查规则进行参数合法性校验，返回int类型错误码,0 : 校验成功，其他错误码校验失败
	public abstract long TaskCheck(FKTask v_task);
	/**任务执行，检查通过的任务将提交给任务管理器执行
	 * @param v_task 待执行任务解析的任务对象
	 * @return 0 : 执行成功，-1 : 任务添加失败，-2 : 任务执行程序异常，3 : 执行超时，4 : 执行完成
	 */
	public long TaskParse(FKTask v_task){
		long ret = 0;
		if(v_task == null){
			logger.debug("BaseTaskParse.TaskParse Recieve an invalid task.");
			ret = -1;
			return ret;
		}
		logger.debug("BaseTaskParse.TaskParse begin parse task(TASKTYPE:" + v_task.TASKTYPE + ")(" + v_task + ").");
		// 如果是阻塞型任务，需要指定当阻塞时间超时后，设置的任务应答错误码和错误信息，如果是非阻塞型任务，则接收错误码和错误信息无意义
		try{
			if(v_task.TASKTYPE == TaskAttribute.TaskType.block.getValue()){
				long v_acc_errcode = ServiceError.TASK_PERFOVERTIME;
				String v_acc_errstr = ServiceError.TASK_PERFOVERTIME_MSG;
				logger.debug("begin call task_parse_mgr.TaskParse for block task.");
				ret = task_parse_mgr.TaskParse(v_task, v_acc_errcode, v_acc_errstr);
			}else{
				logger.debug("begin call task_parse_mgr.TaskParse for normal task.");
				ret = task_parse_mgr.TaskParse(v_task);
			}
		}catch(Exception e1){
			logger.warn("call task_parse_mgr.TaskParse Exception:" + e1);
			ret = -2;
		}
		return ret;
	}
	/**缺省的检查错误处理方法
	 * @param v_task 待设置检查错误代码的任务实例
	 * @param v_errcode 具体的业务错误代码
	 */
	public void CheckError(FKTask v_task, long v_errcode){
		if(v_task != null){
			if(v_task.ACC_ERRCODE == null || v_task.ACC_ERRCODE != 0)
				v_task.ACC_ERRCODE = ServiceError.SYS_CHECKERR;
			if(v_task.ACC_ERRMSG == null || "".equals(v_task.ACC_ERRMSG.trim()))
				v_task.ACC_ERRMSG = ServiceError.SYS_CHECKERR_MSG;
		}
	}
	/**缺省的解析错误处理方法
	 * @param v_task 待设置解析错误代码的任务实例
	 * @param v_errcode 目前已知错误值，0 : 执行成功，-1 : 任务添加失败。3 : 执行超时，4 : 执行完成
	 */
	public void ParseError(FKTask v_task, long v_errcode){
		if(v_task != null){
			if(v_errcode >= 0){
				if(v_task.ACC_ERRCODE == null || v_task.ACC_ERRCODE != 0)
					v_task.ACC_ERRCODE = ServiceError.SYS_PARSEERR;
				if(v_task.ACC_ERRMSG == null || "".equals(v_task.ACC_ERRMSG.trim()))
					v_task.ACC_ERRMSG = ServiceError.SYS_PARSEERR_MSG;
			}
			else{
				v_task.ACC_ERRCODE = ServiceError.SYS_PARSEERR;
				v_task.ACC_ERRMSG = ServiceError.SYS_PARSEERR_MSG;
			}
		}
	}
	// 任务返回POJO组装
	public abstract Object TaskResponseBuilder(FKTask v_task);
	// 数据发送或调用方异步请求回调时，调用该方法，实现服务端方法调用
	public abstract boolean Callservice(String v_server_url,FKTask v_task);	
}
