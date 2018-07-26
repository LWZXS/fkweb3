package com.longshine.cams.fk.server;

import java.util.Hashtable;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.longshine.cams.fk.common.BaseTaskPerform;
import com.longshine.cams.fk.structs.InterfaceProperty;

public class TaskPerformFactory {
	private static Log logger = LogFactory.getLog(TaskPerformFactory.class);
	// 定义单例对象的实例变量和方法
	private static TaskPerformFactory instance;
	// 对外共享的方法
	private FKConfiguration config;
	// 保存数据库连接池对象
	private DataSource datasource;
	// 保存接口配置的信息
	private Map<String,InterfaceProperty> interfaces;
	// 保存任务解析类对象，方便未来的直接调用
	private Map<String,Class<?>> taskperforms;
	private TaskPerformFactory(){}
	public static TaskPerformFactory getInstance(FKConfiguration v_config){
		if(instance == null){
			synchronized(FKServer.class){
				if(instance == null){
					instance = new TaskPerformFactory();
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
	public static TaskPerformFactory getInstance(){
		return instance;
	}
	private void Initialized(){
		this.interfaces = config.getInterfaceProps();
		this.taskperforms = new Hashtable<String,Class<?>>();
		// 设置数据源
		this.LoadTaskPerformClass();
	}
	/**获取容器的数据源
	 * 
	 * @return 返回数据源引用
	 */
	public DataSource getDatasource() {
		return datasource;
	}
	public void setDatasource(DataSource datasource) {
		this.datasource = datasource;
	}
	/**初始化定义的所有接口的任务解析类对象，将所有的任务解析类对象保存在taskperforms中，供接口调用时快速实例化具体类对象
	 */
	private void LoadTaskPerformClass(){
		InterfaceProperty prop;
		String class_name;
		Class<?> class_obj;
		for(String propkey:this.interfaces.keySet()){
			prop = this.interfaces.get(propkey);
			class_name = prop.class_perform;
			try{
				if(class_name == null || "".equals(class_name.trim())){
					logger.debug("interface(" + prop.function + ") do not configure class_perform.");
					continue;
				}
				class_obj = Class.forName(class_name);
				if(BaseTaskPerform.class.isAssignableFrom(class_obj)){
					taskperforms.put(class_name, class_obj);
					logger.debug("interface(" + prop.function + ") configure TaskPerform Class(" + class_name + ") is add to taskperforms.");
				}
				else
					logger.info("interface(" + prop.function + ") configure TaskPerform Class(" + class_name + ") is not extends TaskPerformBase,It is invalid.");
			}catch(Exception e){
				logger.warn("interface(" + prop.function + ") Load TaskPerform Class(" + class_name + ") Exception:" + e);
			}
		}
	}
	/**生成接口对应的任务解析类对象新实例
	 * @param v_func 输入接口方法名
	 * @return 返回对应接口配置的任务解析类对象实例，如果类不存在，记录日志后，返回null
	 */
	public BaseTaskPerform getTaskPerformer(final String v_func){
		InterfaceProperty prop = this.interfaces.get(v_func);
		if (prop == null){
			logger.warn("interface (" + v_func + ")'s property is not exists,please check configure file.");
			return null;
		}
		String class_name = prop.class_perform;
		if((class_name == null) || "".equals(class_name)){
			logger.warn("interface (" + v_func + ") do not configure class_parse.");
			return null;
		}
		logger.debug("get Performer(" + class_name + "). There are Performers:" + this.taskperforms.keySet());
		Class<?> class_obj = this.taskperforms.get(class_name);
		if(class_obj == null){
			logger.warn("interface (" + v_func + ")'s class_perform(" + class_name + ") is invalid.");
			return null;
		}
		BaseTaskPerform performer = null;
		try{
			performer = (BaseTaskPerform)class_obj.newInstance();
			performer.Initialized(config, prop, datasource, v_func);
		}catch(Exception e){
			logger.warn("interface (" + v_func + ")'s class_perform(" + class_name + ") instance Except:" + e);
			return null;
		}
		return performer;
	}
}
