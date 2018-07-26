package com.longshine.cams.fk.server;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.longshine.cams.fk.common.BaseTaskParse;
import com.longshine.cams.fk.structs.BusinessResourceInitial;
import com.longshine.cams.fk.structs.FKTask;
import com.longshine.cams.fk.structs.TaskAttribute;

public class FKServer {
	private static Log logger = LogFactory.getLog(FKServer.class);
	private static void debug(Object v){if(logger.isDebugEnabled())	logger.debug(v);}
	private static void info(Object v){if(logger.isInfoEnabled())	logger.info(v);}
	private static void warn(Object v){if(logger.isWarnEnabled())	logger.warn(v);}
	// 定义单例对象的实例变量和方法
	private static FKServer instance;
	// 对外共享的方法
	private ServletContext sc;
	private ApplicationContext appContext;
	private FKConfiguration config;
	// 定义任务管理器对象
	private FKTaskManager task_mgr;
	// 定义任务检查管理器对象
	private FKTaskParseManager parse_mgr;
	// 定义任务执行管理器对象
	private FKTaskPerformManager perform_mgr;
	// 定义一个业务层初始化对象清单
	private static List<BusinessResourceInitial> br_list;
	// 运行模式，gd为广东模式，sw为省外模式
	private static TaskAttribute.SystemMode run_mode;
	// 系统初始化状态
	private static boolean run_status = false;
	// 获取系统初始化运行模式
	public static TaskAttribute.SystemMode getRunMode() {
		return run_mode;
	}
	public ServletContext getServletContext() {
		return sc;
	}
	public FKTaskManager getTaskManager() {
		return task_mgr;
	}
	public FKTaskParseManager getParseManager() {
		return parse_mgr;
	}
	public FKTaskPerformManager getPerformManager() {
		return perform_mgr;
	}
	private FKServer(){}
	public static FKServer getInstance(ServletContext vsc){
		if(instance == null){
			synchronized(FKServer.class){
				if(instance == null){
					instance = new FKServer();
					instance.sc = vsc;
					instance.Initialized();
				}
			}
		}
		return instance;
	}
	public Log getLog() {
		return logger;
	}
	public static FKServer getInstance(){
		return instance;
	}
	public FKConfiguration getConfig() {
		return config;
	}
	// 装载配置参数，并初始化任务数据校验管理器、任务管理器、任务执行管理器。
	private void Initialized(){
		info("begin call FKConfiguration.getInstance....");
		this.appContext = WebApplicationContextUtils.getWebApplicationContext(sc);
		this.config = FKConfiguration.getInstance(sc);
		// 内部对象初始化完成后初始化注册业务对象
		run_mode = TaskAttribute.SystemMode.gd;
		if("sw".equals(this.config.getProperty(FKConfigureKeys.CAMS_FK_SYSTEM_MODE_KEY).toLowerCase()))
			run_mode = TaskAttribute.SystemMode.sw;

		this.task_mgr = FKTaskManager.getInstance(this.config);
		this.parse_mgr = FKTaskParseManager.getInstance(this.config);
		this.perform_mgr = FKTaskPerformManager.getInstance(this.config);
		
		info(this.config.getProperty(FKConfigureKeys.CAMS_FK_TASK_TABLENAME_KEY));
		info(this.config.getProperty(FKConfigureKeys.CAMS_FK_TASK_MX_TABLENAME_KEY));
		info("==========================================================");
		String str = "cities=";
		for(String key:this.config.getCitylist().keySet()){
			str += key + ",";
		}
		if(str.lastIndexOf(',') > 0)
			str = str.substring(0, str.length() - 1);
		info(str);
		info("==========================================================");
		this.task_mgr.initializeManager();
		this.parse_mgr.initializeManager();
		this.perform_mgr.initializeManager();
		// 设置系统初始化工作基本完成
		setRunStatus(true);
		// 系统注册外部资源进行初始化
		this.BusinessResouceInitial();
		info("FKComponent Initialized....");
	}
	
	public ApplicationContext getApplicationContext() {
		return this.appContext;
	}
	// 等待所有内部线程结束
	public void Destroy(){
		info("FKComponent begin destroyed....");
		// 设置所有线程的退出状态
		perform_mgr.setProcExit();
		parse_mgr.setProcExit();
		task_mgr.setProcExit();
		// 等待所有线程退出
		perform_mgr.joinProc();
		parse_mgr.joinProc();
		task_mgr.joinProc();
		info("FKComponent exit complete....");
	}
	// 启动所有服务线程
	public void startServer(){
		task_mgr.startProc();
		parse_mgr.startProc();
		perform_mgr.startProc();
		info("FKComponent begin work....");
	}
	/**代理任务管理器的任务标识生成方法
	 * @return 返回生成的任务标识
	 */
	public String CreateTaskID(){
		return task_mgr.CreateTaskID();
	}
	/**代理任务管理器的任务追加方法
	 * @param task 添加到任务管理器中的任务对象
	 * @return 只有当队列满时才返回失败，此时应用层需要返回调用方，属系统内部错误
	 */
	public boolean TaskAdd(FKTask task){
		return task_mgr.TaskAdd(task);
	}
	/**代理任务管理器的阻塞任务检查和超时处理方法
	 * @param v_taskid 待检查的任务标识
	 * @return 返回执行完成的任务对象
	 */
	public FKTask TaskBlockCheckOrOversec(final String v_taskid){
		return task_mgr.TaskBlockCheckOrOversec(v_taskid);
	}
	/**代理任务管理器的阻塞任务检查和超时处理方法
	 * @param v_taskid 待检查的任务标识
	 * @param v_acc_errcode 设置阻塞超时错误码
	 * @param v_acc_errstr 设置阻塞超时错误信息
	 * @return 阻塞任务执行超时检查成功后，返回任务对象实例
	 */
	public FKTask TaskBlockCheckOrOversec(final String v_taskid,final long v_acc_errcode,final String v_acc_errstr){
		return task_mgr.TaskBlockCheckOrOversec(v_taskid, v_acc_errcode, v_acc_errstr);
	}
	/**代理任务解析对象的任务解析工厂对象，根据接口生成任务解析对象
	 * @param v_func 根据接口方法名获取接口对应解析对象
	 * @return 返回请求接口对应的任务解析对象实例
	 */
	public BaseTaskParse getTaskParser(final String v_func){
		return this.parse_mgr.getParsefactory().getTaskParser(v_func);
	}
	
	/**在服务器中注册业务资源初始化对象
	 * @param v_res 待注册的业务资源，该对象中描述了类名称，方法名称（目前只支持静态方法初始化），同时指定初始化的类型，
	 * 类型是TaskAttribute.InitialType枚举变量，目前只支持“system_mode”模式，即通过系统运行模式初始化业务资源
	 */
	public synchronized static void RegisterInitialResource(BusinessResourceInitial v_res){
		if(v_res != null){
			if(isRunStatus()){
				debug("begin initial direct....");
				InitialOneResource(v_res);
			}
			else{
				debug("add to the initial resources list....");
				if(br_list == null)
					br_list = new ArrayList<BusinessResourceInitial>();
				br_list.add(v_res);
			}
		}
	}
	/**根据业务注册的业务对象初始化业务对象*/
	private void BusinessResouceInitial(){
		if(br_list == null)
			return;
		boolean b_ok;
		for(BusinessResourceInitial res : br_list){
			b_ok = InitialOneResource(res);
			if(b_ok){
				info("Business Resource(" + res.getType() + ":" + res.getClassName() + "#" + res.getFunctionName()+ ") Initial Succeed.");
			}else{
				warn("Business Resource(" + res.getType() + ":" + res.getClassName() + "#" + res.getFunctionName()+ ") Initial Failed.");
			}
		}
	}
	private static synchronized boolean InitialOneResource(BusinessResourceInitial v_res){
		boolean ret = false;
		if(v_res == null)
			return ret;
		Class<?> cls;
		Method method;
		try{
			cls = Class.forName(v_res.getClassName());
			switch(v_res.getType()){
			case system_mode:
				method = cls.getDeclaredMethod(v_res.getFunctionName(), TaskAttribute.SystemMode.class);
				method.invoke(null, FKServer.run_mode);
				ret = true;
				break;
			default:
				break;
			}
		}catch(Exception e1){
			warn("Business Resource Initial Exception:" + e1);
		}
		return ret;
	}
	public static synchronized boolean isRunStatus() {
		return run_status;
	}
	public static synchronized void setRunStatus(boolean run_status) {
		FKServer.run_status = run_status;
	}
}
