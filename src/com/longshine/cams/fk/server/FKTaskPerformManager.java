package com.longshine.cams.fk.server;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.springframework.context.ApplicationContext;

import com.longshine.cams.fk.common.BaseManager;
import com.longshine.cams.fk.server.FK_JLMMJ.I_FK_MMJ_JLYCSFRZ_Schema;
import com.longshine.cams.fk.server.FK_JLMMJ.I_FK_MMJ_YCKZ_Schema;
import com.longshine.cams.fk.server.FK_JLMMJ.VO_FK_MMJ_JLYCSFRZ_REQ;
import com.longshine.cams.fk.server.FK_JLMMJ.VO_FK_MMJ_JLYCSFRZ_REQ_BD;
import com.longshine.cams.fk.server.FK_JLMMJ.VO_FK_MMJ_JLYCSFRZ_RES;
import com.longshine.cams.fk.server.FK_JLMMJ.VO_FK_MMJ_JLYCSFRZ_RES_BD;
import com.longshine.cams.fk.server.FK_JLMMJ.VO_FK_MMJ_YCKZ_REQ;
import com.longshine.cams.fk.server.FK_JLMMJ.VO_FK_MMJ_YCKZ_REQ_BD;
import com.longshine.cams.fk.server.FK_JLMMJ.VO_FK_MMJ_YCKZ_RES;
import com.longshine.cams.fk.server.FK_JLMMJ.VO_FK_MMJ_YCKZ_RES_BD;
import com.longshine.cams.fk.structs.TaskAttribute;

@SuppressWarnings("unused")
public class FKTaskPerformManager extends BaseManager{
	private static Log logger = LogFactory.getLog(FKTaskPerformManager.class);
	// 定义单例对象的实例变量和方法
	private static FKTaskPerformManager instance;
	// 配置对象
	private FKConfiguration config;
	// 保持对FKServer的引用
	private FKServer fkserver;
	private ApplicationContext appContext;
	// 任务管理对象引用
	private FKTaskManager task_mgr;
	// 任务执行线程池
	private List<PROC_FKTaskPerform> proc_performs;
	// 管理任务执行工厂
	private TaskPerformFactory perform_factory;
	// 后台任务服务通讯服务对象
	private PROC_TaskServer proc_taskserver;
	// 保存数据库连接池对象
	private DataSource datasource;
	
	public TaskPerformFactory getPerform_factory() {
		return perform_factory;
	}
	private FKTaskPerformManager(){}
	public static FKTaskPerformManager getInstance(FKConfiguration v_config){
		if(instance == null){
			synchronized(FKTaskPerformManager.class){
				if(instance == null){
					instance = new FKTaskPerformManager();
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
	public static FKTaskPerformManager getInstance(){
		return instance;
	}
	public FKConfiguration getConfig() {
		return config;
	}
	public ApplicationContext getApplicationContext() {
		return appContext;
	}
	// 装载配置参数，并初始化任务数据校验管理器、任务管理器、任务执行管理器。
	private void Initialized(){
		this.perform_factory = TaskPerformFactory.getInstance(config);
		this.proc_performs = new ArrayList<PROC_FKTaskPerform>();
	}
	/**任务解析管理器工作准备
	 * 
	 */
	public void initializeManager(){
		this.fkserver = FKServer.getInstance();
		this.task_mgr = FKTaskManager.getInstance();
		this.appContext = this.fkserver.getApplicationContext();
		this.datasource = this.appContext.getBean("datasource_gdcams2",DataSource.class);
		this.perform_factory.setDatasource(this.datasource);
		// 初始化后台任务服务的处理线程
		this.proc_taskserver = new PROC_TaskServer(config.getPropertyLong(FKConfigureKeys.CAMS_FK_SYSTEM_IDLE_KEY));
		this.proc_taskserver.setManagers(this.config, this);
		// 初始化执行任务的线程池
		initializeTaskPerformProc();
	}
	/**初始化执行线程池
	 * 
	 */
	public void initializeTaskPerformProc(){
		// 初始化执行任务的线程池对象
		long proc_num = config.getPropertyLong(FKConfigureKeys.CAMS_FK_SYSTEM_PERFORMPROCESS_KEY);
		PROC_FKTaskPerform proc;
		if(proc_num < 1)
			proc_num = 1;
		for(long i = 0; i < proc_num; i++){
			proc = new PROC_FKTaskPerform(config.getPropertyLong(FKConfigureKeys.CAMS_FK_SYSTEM_IDLE_KEY), this.proc_taskserver);
			proc.setManagers(config, this, task_mgr);
			proc_performs.add(proc);
		}
	}
	@Override
	public void startProc() {
		// TODO Auto-generated method stub
		// 启动所有线程;
		this.proc_taskserver.start();
		for(PROC_FKTaskPerform proc:proc_performs){
			proc.start();
		}
	}
	// 设置所有线程退出条件
	@Override
	public void setProcExit(){
		this.proc_taskserver.setExit();
		for(PROC_FKTaskPerform proc:proc_performs){
			proc.setExit();
		}
	}
	// 等待管理线程退出
	@Override
	public void joinProc() {
		// 等待所有线程结束
		try{
			this.proc_taskserver.join();
			for(PROC_FKTaskPerform proc:proc_performs){
				proc.join();
			}
		}catch(InterruptedException e){
			logger.warn("proc_task_archieve stop exception:" + e);
		}
	}
	/**请求密码机获取远程控制密文信息
	 * @param v_yckz_bd 通过远程WebService调用获取密码机上的远程控制密文信息
	 * @return 密码机返回的信息内容，当调用失败时，返回null
	 */
	public VO_FK_MMJ_YCKZ_RES_BD getMMJ_YCKZ(VO_FK_MMJ_YCKZ_REQ_BD v_yckz_bd){
		return this.getMMJ_YCKZ(v_yckz_bd, null, null, null);
	}
	public VO_FK_MMJ_YCKZ_RES_BD getMMJ_YCKZ(VO_FK_MMJ_YCKZ_REQ_BD v_yckz_bd, TaskAttribute.MMJType v_mmjtype){
		return this.getMMJ_YCKZ(v_yckz_bd, v_mmjtype, null, null);
	}
	public VO_FK_MMJ_YCKZ_RES_BD getMMJ_YCKZ(VO_FK_MMJ_YCKZ_REQ_BD v_yckz_bd, TaskAttribute.MMJType v_mmjtype, Interceptor<?> v_call_intercept, Interceptor<?> v_resp_intercept){
		VO_FK_MMJ_YCKZ_RES_BD ret = null;
		String mmjserver_url = null;
		// 密码机方法名初始化
		String func_yckz = this.config.getProperty(FKConfigureKeys.CAMS_FK_TASK_MMJ_YCKZ_FUNCTION_KEY);
		if(v_mmjtype == null || v_mmjtype == TaskAttribute.MMJType.jl)
			mmjserver_url = this.config.getProperty(FKConfigureKeys.CAMS_FK_TASK_MMJSERVER_JL_ADDR_KEY);
		else
			return null;
		mmjserver_url = this.combineServerURL(mmjserver_url, func_yckz);
//		LoggingOutInterceptor call_intercept = new LoggingOutInterceptor();
//		LoggingInInterceptor resp_intercept = new LoggingInInterceptor();
		logger.debug("MMJ Service(getMMJ_YCKZ):" + mmjserver_url);
		try{
			VO_FK_MMJ_YCKZ_REQ req = new VO_FK_MMJ_YCKZ_REQ();
			VO_FK_MMJ_YCKZ_RES resp = null;
			req.setBd(v_yckz_bd);
			resp = (VO_FK_MMJ_YCKZ_RES)WebServiceClientUtil.callCxfService(
					mmjserver_url,
					func_yckz,
					new Class[]{VO_FK_MMJ_YCKZ_REQ.class},
					I_FK_MMJ_YCKZ_Schema.class,
					v_call_intercept,
					v_resp_intercept,
					req);
			if(resp != null)
				ret = resp.getBd();
		}catch(Exception e){
			logger.warn("Call MMJ(" + mmjserver_url + ") invoke I_FK_MMJ_YCKZ Exception:" + e);
		}
		return ret;
	}
	/**请求密码机获取计量远程身份认证的密文信息
	 * @param v_jlycsfrz_bd 通过远程WebService调用获取密码机上的远程身份认证密文信息
	 * @return 密码机返回的信息内容，当调用失败时，返回null
	 */
	public VO_FK_MMJ_JLYCSFRZ_RES_BD getMMJ_JLYCSFRZ(VO_FK_MMJ_JLYCSFRZ_REQ_BD v_jlycsfrz_bd){
		return this.getMMJ_JLYCSFRZ(v_jlycsfrz_bd, null, null, null);
	}
	public VO_FK_MMJ_JLYCSFRZ_RES_BD getMMJ_JLYCSFRZ(VO_FK_MMJ_JLYCSFRZ_REQ_BD v_jlycsfrz_bd, TaskAttribute.MMJType v_mmjtype){
		return this.getMMJ_JLYCSFRZ(v_jlycsfrz_bd, v_mmjtype, null, null);
	}
	public VO_FK_MMJ_JLYCSFRZ_RES_BD getMMJ_JLYCSFRZ(VO_FK_MMJ_JLYCSFRZ_REQ_BD v_jlycsfrz_bd, TaskAttribute.MMJType v_mmjtype, Interceptor<?> v_call_intercept, Interceptor<?> v_resp_intercept){
		VO_FK_MMJ_JLYCSFRZ_RES_BD ret = null;
		String mmjserver_url = null;
		String func_ycsfrz = this.config.getProperty(FKConfigureKeys.CAMS_FK_TASK_MMJ_YCSFRZ_FUNCTION_KEY);
		if(v_mmjtype == null || v_mmjtype == TaskAttribute.MMJType.jl)
			mmjserver_url = this.config.getProperty(FKConfigureKeys.CAMS_FK_TASK_MMJSERVER_JL_ADDR_KEY);
		else
			return null;
		mmjserver_url = this.combineServerURL(mmjserver_url, func_ycsfrz);
//		LoggingOutInterceptor call_intercept = new LoggingOutInterceptor();
//		LoggingInInterceptor resp_intercept = new LoggingInInterceptor();
		logger.debug("MMJ Service(getMMJ_JLYCSFRZ):" + mmjserver_url);
		try{
			VO_FK_MMJ_JLYCSFRZ_REQ req = new VO_FK_MMJ_JLYCSFRZ_REQ();
			VO_FK_MMJ_JLYCSFRZ_RES resp = null;
			req.setBd(v_jlycsfrz_bd);
			resp = (VO_FK_MMJ_JLYCSFRZ_RES)WebServiceClientUtil.callCxfService(
					mmjserver_url,
					func_ycsfrz,
					new Class[]{VO_FK_MMJ_JLYCSFRZ_REQ.class},
					I_FK_MMJ_JLYCSFRZ_Schema.class,
					v_call_intercept,
					v_resp_intercept,
					req);
			if(resp != null)
				ret = resp.getBd();
		}catch(Exception e){
			logger.warn("Call MMJ(" + mmjserver_url + ") invoke I_FK_MMJ_JLYCSFRZ Exception:" + e);
		}
		return ret;
	}
	/**生成后台TaskServer调用是的任务请求标识
	 * 当前规则是根据FKTaskMX.TASKMXID来生成，未来可能根据需要进行调整
	 * @param v_taskmx_id 待生成TaskServer后台服务通信的标识的任务明细标识
	 * @return 根据任务明细标识生成的TaskServer后台服务通信，目前直接使用明细标识作为后台服务通信标识，为了保证唯一性，如果一个明细对应多次后台请求时，需要变更算法，目前采用了业务层指定的方式实现。老的任务模式继续兼容
	 */
	public String createTaskServerID(String v_taskmx_id){
		return v_taskmx_id;
	}
	private String combineServerURL(String v_preurl, String v_function){
		String str = v_preurl;
		while(str.endsWith("/"))
			str = str.substring(0, str.length() - 1);
		while(str.endsWith("\\"))
			str = str.substring(0, str.length() - 1);
		return str + "/" + v_function;
	}
}
