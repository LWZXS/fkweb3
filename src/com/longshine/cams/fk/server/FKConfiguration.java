package com.longshine.cams.fk.server;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import com.longshine.cams.fk.structs.InterfaceProperty;

public class FKConfiguration {
	// 定义单例对象的实例变量和方法
	private static FKConfiguration instance;
	// 日志对象
	private static Log log = LogFactory.getLog(FKConfiguration.class);;
	// 系统对象
	private ServletContext sc;
	// 用于保存properties标签下的property名值对，使用HashMap对象保存，获取时使用getProperty方法
	private Properties properties = null;
	// 用于保存interfaces下的接口配置
	private Map<String,InterfaceProperty> interfaces = null;
	// 地市编码校验
	private Map<String,String> citylist;

	private FKConfiguration(){}
	public static FKConfiguration getInstance(ServletContext vsc){
		if(instance == null){
			synchronized(FKConfiguration.class){
				if(instance == null){
					instance = new FKConfiguration();
//					instance.log = 
					instance.sc = vsc;
					instance.LoadConfiguration();
				}
			}
		}
		return instance;
	}
	public static FKConfiguration getInstance(){
		return instance;
	}
	public Map<String, String> getCitylist() {
		return citylist;
	}
	public String getProperty(final String key){
		return this.properties.getProperty(key);
	}
	public long getPropertyLong(final String key){
		long ret = 0;
		try{
			ret = Long.parseLong(this.properties.getProperty(key));
		}catch(Exception e){
			ret = 0;
		}
		return ret;
	}
	public int getPropertyInteger(final String key){
		int ret = 0;
		try{
			ret = Integer.parseInt(this.properties.getProperty(key));
		}catch(Exception e){
			ret = 0;
		}
		return ret;
	}
	public Properties getProperties(){
		return this.properties;
	}
	public Map<String,InterfaceProperty> getInterfaceProps(){
		return this.interfaces;
	}
	public InterfaceProperty getInterfaceProperty(final String interfacekey){
		return this.interfaces.get(interfacekey);
	}
	private void LoadConfiguration(){
		this.properties = new Properties();
		this.interfaces = new Hashtable<String,InterfaceProperty>();
		this.propertiesinitialized();
		this.interfacesinitialized();
		if(this.sc == null)		// 如果不是在容器中生成配置文件对象，系统只装载缺省参数
			return;
		
		// 从配置中读取系统资源文件位置信息
		log.info("Current Directory:" + System.getProperty("user.dir") + "........");
		String config_file = this.sc.getInitParameter("fkConfigLocation");
		if(config_file == null || "".equals(config_file))
			config_file = FKConfigureKeys.CAMS_FK_CONFIG_FILE;
		log.info("FK Configuration File:" + config_file);
		try{
			Document xml_doc = this.LoadXMLConfigResource(config_file);
			if(xml_doc != null){
				this.ParseXMLResouce(xml_doc,"properties","property");
				this.ParseXMLResouce(xml_doc,"interfaces","interface");
			}
		}catch(Exception e){
			log.warn("Load Configure(" + config_file + ") Exception:" + e);
		}
		// 读取本地超越配置属性信息
		config_file = this.sc.getInitParameter("fkSiteConfigLocation");
		if(config_file == null || "".equals(config_file))	// 没有配置本地个性化的配置信息，直接退出
			return;
		log.info("FK Site Configuration(" + config_file + ") File:" + config_file);
		try{
			Document xml_doc = this.LoadXMLConfigResource(config_file);
			if(xml_doc != null){
				this.ParseXMLResouce(xml_doc,"properties","property");
				this.ParseXMLResouce(xml_doc,"interfaces","interface");
			}
		}catch(Exception e1){
			log.warn("Load Site Configure(" + config_file + ") Exception:" + e1);
		}
		log.info("Load Scene CALLSERVER:" + this.getProperty(FKConfigureKeys.CAMS_FK_TASK_CALLSERVER_ADDR_KEY));
		log.info("Load Scene TASKSERVER:" + this.getProperty(FKConfigureKeys.CAMS_FK_TASK_TASKSERVER_ADDR_KEY));
		String cities = getProperty(FKConfigureKeys.CAMS_FK_TASK_CITYLIST_KEY);
		if(cities != null){
			this.citylist = new Hashtable<String,String>();
			String[] cities_arr = cities.split(",");
			for(String city:cities_arr){
				this.citylist.put(city, city);
			}
		}
	}
	private void propertiesinitialized(){
		this.properties.setProperty(FKConfigureKeys.CAMS_FK_SYSTEM_CELLID_KEY, FKConfigureKeys.CAMS_FK_SYSTEM_CELLID_DEFAULT);
		this.properties.setProperty(FKConfigureKeys.CAMS_FK_SYSTEM_OVERSECS_KEY, FKConfigureKeys.CAMS_FK_SYSTEM_OVERSECS_DEFAULT);
		this.properties.setProperty(FKConfigureKeys.CAMS_FK_SYSTEM_MODE_KEY, FKConfigureKeys.CAMS_FK_SYSTEM_MODE_DEFAULT);
		this.properties.setProperty(FKConfigureKeys.CAMS_FK_SYSTEM_OVERSECSDELAY_KEY, FKConfigureKeys.CAMS_FK_SYSTEM_OVERSECSDELAY_DEFAULT);
		this.properties.setProperty(FKConfigureKeys.CAMS_FK_SYSTEM_LOGWSFILE_KEY, FKConfigureKeys.CAMS_FK_SYSTEM_LOGWSFILE_DEFAULT);
		this.properties.setProperty(FKConfigureKeys.CAMS_FK_SYSTEM_CALLBACKPROCESS_KEY, FKConfigureKeys.CAMS_FK_SYSTEM_CALLBACKPROCESS_DEFAULT);
		this.properties.setProperty(FKConfigureKeys.CAMS_FK_SYSTEM_PERFORMPROCESS_KEY, FKConfigureKeys.CAMS_FK_SYSTEM_PERFORMPROCESS_DEFAULT);
		this.properties.setProperty(FKConfigureKeys.CAMS_FK_SYSTEM_PERFORM_TASKS_MIN_KEY, FKConfigureKeys.CAMS_FK_SYSTEM_PERFORM_TASKS_MIN_DEFAULT);
		this.properties.setProperty(FKConfigureKeys.CAMS_FK_SYSTEM_PERFORM_TASKS_MAX_KEY, FKConfigureKeys.CAMS_FK_SYSTEM_PERFORM_TASKS_MAX_DEFAULT);
		this.properties.setProperty(FKConfigureKeys.CAMS_FK_TASK_DISPATCH_STRATEGY_KEY, FKConfigureKeys.CAMS_FK_TASK_DISPATCH_STRATEGY_DEFAULT);
		this.properties.setProperty(FKConfigureKeys.CAMS_FK_TASK_CALLSERVER_ADDR_KEY,"");
		this.properties.setProperty(FKConfigureKeys.CAMS_FK_TASK_TASKSERVER_ADDR_KEY,"");
		this.properties.setProperty(FKConfigureKeys.CAMS_FK_TASK_TASKSERVER_ENCODING_KEY,FKConfigureKeys.CAMS_FK_TASK_TASKSERVER_ENCODING_DEFAULT);
		this.properties.setProperty(FKConfigureKeys.CAMS_FK_TASK_TASKSERVER_SENDTAIL_KEY,FKConfigureKeys.CAMS_FK_TASK_TASKSERVER_SENDTAIL_DEFAULT);
		this.properties.setProperty(FKConfigureKeys.CAMS_FK_TASK_TASKSERVER_RECVBUFF_KEY, FKConfigureKeys.CAMS_FK_TASK_TASKSERVER_RECVBUFF_DEFAULT);
		this.properties.setProperty(FKConfigureKeys.CAMS_FK_TASK_TASKSERVER_CONNECT_TIMEOUT_KEY, FKConfigureKeys.CAMS_FK_TASK_TASKSERVER_CONNECT_TIMEOUT_DEFAULT);
		this.properties.setProperty(FKConfigureKeys.CAMS_FK_TASK_TASKSERVER_SEND_TIMEOUT_KEY, FKConfigureKeys.CAMS_FK_TASK_TASKSERVER_SEND_TIMEOUT_DEFAULT);
		this.properties.setProperty(FKConfigureKeys.CAMS_FK_TASK_TASKSERVER_PERFORM_TIMEOUT_KEY, FKConfigureKeys.CAMS_FK_TASK_TASKSERVER_PERFORM_TIMEOUT_DEFAULT);
		this.properties.setProperty(FKConfigureKeys.CAMS_FK_TASK_MMJSERVER_YX_ADDR_KEY,FKConfigureKeys.CAMS_FK_TASK_MMJSERVER_YX_ADDR_DEFAULT);
		this.properties.setProperty(FKConfigureKeys.CAMS_FK_TASK_MMJSERVER_JL_ADDR_KEY,FKConfigureKeys.CAMS_FK_TASK_MMJSERVER_JL_ADDR_DEFAULT);
		this.properties.setProperty(FKConfigureKeys.CAMS_FK_TASK_MMJ_YCKZ_FUNCTION_KEY,FKConfigureKeys.CAMS_FK_TASK_MMJ_YCKZ_FUNCTION_DEFAULT);
		this.properties.setProperty(FKConfigureKeys.CAMS_FK_TASK_MMJ_YCSFRZ_FUNCTION_KEY,FKConfigureKeys.CAMS_FK_TASK_MMJ_YCSFRZ_FUNCTION_DEFAULT);
		this.properties.setProperty(FKConfigureKeys.CAMS_FK_TASK_MMJ_RUNSTATUS_KEY,FKConfigureKeys.CAMS_FK_TASK_MMJ_RUNSTATUS_DEFAULT);
		this.properties.setProperty(FKConfigureKeys.CAMS_FK_TASK_TABLENAME_KEY,FKConfigureKeys.CAMS_FK_TASK_TABLENAME_DEFAULT);
		this.properties.setProperty(FKConfigureKeys.CAMS_FK_TASK_MX_TABLENAME_KEY,FKConfigureKeys.CAMS_FK_TASK_MX_TABLENAME_DEFAULT);
		this.properties.setProperty(FKConfigureKeys.CAMS_FK_TASK_DB_TYPE_KEY,FKConfigureKeys.CAMS_FK_TASK_DB_TYPE_DEFAULT);
		this.properties.setProperty(FKConfigureKeys.CAMS_FK_TASK_DB_CACHEDROWS_MAX_KEY,FKConfigureKeys.CAMS_FK_TASK_DB_CACHEDROWS_MAX_DEFAULT);
		this.properties.setProperty(FKConfigureKeys.CAMS_FK_SYSTEM_IDLE_KEY,FKConfigureKeys.CAMS_FK_SYSTEM_IDLE_DEFAULT);
		this.properties.setProperty(FKConfigureKeys.CAMS_FK_TASK_CITYLIST_KEY,FKConfigureKeys.CAMS_FK_TASK_CITYLIST_DEFAULT);
		// 通信规约参数设置
		this.properties.setProperty(FKConfigureKeys.CAMS_FK_TASK_GYCS_BH_RULE_KEY,FKConfigureKeys.CAMS_FK_TASK_GYCS_BH_RULE_DEFAULT);
		this.properties.setProperty(FKConfigureKeys.CAMS_FK_TASK_GYCS_PROTOCOL_KEY,FKConfigureKeys.CAMS_FK_TASK_GYCS_PROTOCOL_DEFAULT);
		this.properties.setProperty(FKConfigureKeys.CAMS_FK_TASK_GYCS_ITEMTYPE_KEY,FKConfigureKeys.CAMS_FK_TASK_GYCS_ITEMTYPE_DEFAULT);
		this.properties.setProperty(FKConfigureKeys.CAMS_FK_TASK_GYCS_COMMPORT_KEY,FKConfigureKeys.CAMS_FK_TASK_GYCS_COMMPORT_DEFAULT);
		this.properties.setProperty(FKConfigureKeys.CAMS_FK_TASK_GYCS_BAUDRATE_KEY,FKConfigureKeys.CAMS_FK_TASK_GYCS_BAUDRATE_DEFAULT);
		this.properties.setProperty(FKConfigureKeys.CAMS_FK_TASK_GYCS_RELAYTIMEOUT_KEY,FKConfigureKeys.CAMS_FK_TASK_GYCS_RELAYTIMEOUT_DEFAULT);
		this.properties.setProperty(FKConfigureKeys.CAMS_FK_TASK_GYCS_CHECKTYPE_KEY,FKConfigureKeys.CAMS_FK_TASK_GYCS_CHECKTYPE_DEFAULT);
		this.properties.setProperty(FKConfigureKeys.CAMS_FK_TASK_GYCS_DATABIT_KEY,FKConfigureKeys.CAMS_FK_TASK_GYCS_DATABIT_DEFAULT);
		this.properties.setProperty(FKConfigureKeys.CAMS_FK_TASK_GYCS_STOPBIT_KEY,FKConfigureKeys.CAMS_FK_TASK_GYCS_STOPBIT_DEFAULT);
		this.properties.setProperty(FKConfigureKeys.CAMS_FK_TASK_GYCS_OPERCODE_KEY,FKConfigureKeys.CAMS_FK_TASK_GYCS_OPERCODE_DEFAULT);
		// 营销系统通过FTP方式提交给计量系统的任务数据相关参数定义
		this.properties.setProperty(FKConfigureKeys.CAMS_FK_TASK_YXDATA_FTPADDR_KEY,FKConfigureKeys.CAMS_FK_TASK_YXDATA_FTPADDR_DEFAULT);
		this.properties.setProperty(FKConfigureKeys.CAMS_FK_TASK_YXDATA_FTPPATH_KEY,FKConfigureKeys.CAMS_FK_TASK_YXDATA_FTPPATH_DEFAULT);
		this.properties.setProperty(FKConfigureKeys.CAMS_FK_TASK_YXDATA_FTPUSER_KEY,FKConfigureKeys.CAMS_FK_TASK_YXDATA_FTPUSER_DEFAULT);
		this.properties.setProperty(FKConfigureKeys.CAMS_FK_TASK_YXDATA_FTPPASS_KEY,FKConfigureKeys.CAMS_FK_TASK_YXDATA_FTPPASS_DEFAULT);
		this.properties.setProperty(FKConfigureKeys.CAMS_FK_TASK_YXDATA_TEMPPATH_KEY,FKConfigureKeys.CAMS_FK_TASK_YXDATA_TEMPPATH_DEFAULT);
		this.properties.setProperty(FKConfigureKeys.CAMS_FK_TASK_YXDATA_MIDPATH_KEY,FKConfigureKeys.CAMS_FK_TASK_YXDATA_MIDPATH_DEFAULT);
		this.properties.setProperty(FKConfigureKeys.CAMS_FK_TASK_YXDATA_BACKUPPATH_KEY,FKConfigureKeys.CAMS_FK_TASK_YXDATA_BACKUPPATH_DEFAULT);
		// 计量系统通过FTP方式提交给营销系统的任务数据相关参数定义
		this.properties.setProperty(FKConfigureKeys.CAMS_FK_TASK_JLDATA_FTPADDR_KEY,FKConfigureKeys.CAMS_FK_TASK_JLDATA_FTPADDR_DEFAULT);
		this.properties.setProperty(FKConfigureKeys.CAMS_FK_TASK_JLDATA_FTPPATH_KEY,FKConfigureKeys.CAMS_FK_TASK_JLDATA_FTPPATH_DEFAULT);
		this.properties.setProperty(FKConfigureKeys.CAMS_FK_TASK_JLDATA_FTPUSER_KEY,FKConfigureKeys.CAMS_FK_TASK_JLDATA_FTPUSER_DEFAULT);
		this.properties.setProperty(FKConfigureKeys.CAMS_FK_TASK_JLDATA_FTPPASS_KEY,FKConfigureKeys.CAMS_FK_TASK_JLDATA_FTPPASS_DEFAULT);
		this.properties.setProperty(FKConfigureKeys.CAMS_FK_TASK_JLDATA_TEMPPATH_KEY,FKConfigureKeys.CAMS_FK_TASK_JLDATA_TEMPPATH_DEFAULT);
		this.properties.setProperty(FKConfigureKeys.CAMS_FK_TASK_JLDATA_MIDPATH_KEY,FKConfigureKeys.CAMS_FK_TASK_JLDATA_MIDPATH_DEFAULT);
		this.properties.setProperty(FKConfigureKeys.CAMS_FK_TASK_JLDATA_BACKUPPATH_KEY,FKConfigureKeys.CAMS_FK_TASK_JLDATA_BACKUPPATH_DEFAULT);
	}
	/**定义缺省的接口配置信息
	 * 以后可以考虑编写缺省的接口定义配置
	 */
	private void interfacesinitialized(){
		InterfaceProperty prop;
		// 配置“4.1 I-16-001-01.增量费控用户发送(I_FK_JLZDH_ZLFKYHFS)”
		// 配置“4.14 I-16-018-01 电能表安全数据回抄(I_FK_JLZDH_SJHC)”
		prop = new InterfaceProperty();
		prop.function		= "I_FK_JLZDH_SJHC";
		prop.call_func		= "I_FK_JLZDH_HQSJHCJG";
		prop.zlbm			= "SJHC";
		prop.name			= "电能表安全数据回抄";
		prop.mode			= "unblock";
		prop.oversecs		= this.getPropertyLong(FKConfigureKeys.CAMS_FK_SYSTEM_OVERSECS_KEY);
		prop.class_parse	= "com.longshine.cams.fk.interfaces.FK_JLZDH_SJHC.Parse_FK_JLZDH_SJHC";
		prop.class_perform	= "com.longshine.cams.fk.interfaces.FK_JLZDH_SJHC.Perform_FK_JLZDH_SJHC";
		this.interfaces.put(prop.function, prop);
	}

	private Document parse(DocumentBuilder builder, InputStream is, String systemId) throws IOException, SAXException {
		if (is == null) {
			return null;
		}
		try {
			return (systemId == null) ? builder.parse(is) : builder.parse(is, systemId);
		} finally {
			is.close();
		}
	}
	// 读取XML配置文件对象
	private Document LoadXMLConfigResource(String res){
		Document doc = null;
		try {
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			//ignore all comments inside the xml file
			docBuilderFactory.setIgnoringComments(true);

			//allow includes in the xml file
			docBuilderFactory.setNamespaceAware(true);
			try {
				docBuilderFactory.setXIncludeAware(true);
			} catch (UnsupportedOperationException e) {
				log.warn("Failed to set setXIncludeAware(true) for parser " + docBuilderFactory + ":" + e);
				return doc;
			}
			DocumentBuilder builder = docBuilderFactory.newDocumentBuilder();
			
			ApplicationContext springapp = WebApplicationContextUtils.getRequiredWebApplicationContext(sc);
			Resource resouce = springapp.getResource(res);
			File file;
			try{
				file = resouce.getFile();
				doc = parse(builder, new BufferedInputStream(new FileInputStream(file)), null);
				log.info("FK Configure File:" + file.getAbsolutePath());
			}catch(IOException e){
				log.warn("File: " + res + " not found. Exception:" + e);
				return doc;
			}
		} catch (DOMException e) {
			log.warn("error parsing conf " + res + ",Exception:\n" + e.getMessage());
			//throw new RuntimeException(e);
		} catch (SAXException e) {
			log.warn("error parsing conf " + res + ",Exception:\n" + e.getMessage());
			//throw new RuntimeException(e);
		} catch (ParserConfigurationException e) {
			log.warn("error parsing conf " + res + ",Exception:\n" + e.getMessage());
			//throw new RuntimeException(e);
		}
		return doc;
	}
	// 读取配置文件，获取三级几点的Element，并调用Element的不同的处理方法
	private void ParseXMLResouce(Document doc,String config_root,String config_item){
		try {
			Element root = doc.getDocumentElement();
			if (root == null) {
				log.warn("FKConfigure failed.");
				return;
			}
			if (!"configuration".equals(root.getTagName())){
				log.warn("bad conf file: top-level element not <configuration>");
				return;
			}
			NodeList props = root.getChildNodes();
			boolean bload = false;
			for (int i = 0; i < props.getLength(); i++){
				Node propNode = props.item(i);
		        if (!(propNode instanceof Element))
		        	continue;
		        Element prop = (Element)propNode;
		        if(!config_root.equals(prop.getTagName()))
		        	continue;
		        bload = true;
		        props = prop.getChildNodes();
			}
			if(bload){
				for (int i = 0; i < props.getLength(); i++) {
					Node propNode = props.item(i);
			        if (!(propNode instanceof Element))
			        	continue;
			        Element prop = (Element)propNode;
			        if (!config_item.equals(prop.getTagName()))
			        	log.warn("bad conf file: element not <property>");
			        NodeList fields = prop.getChildNodes();
			        // 判断如果是解析properties，则调用ParseProperty()方法
			        //   如果是解析interfaces，则调用ParseInterface()方法
			        if("properties".equals(config_root))
			        	this.ParseProperty(fields);
			        else if("interfaces".equals(config_root))
			        	this.ParseInterface(fields);
				}
			}
		} catch (DOMException e) {
			log.warn("error parsing conf,Exception:\n" + e.getMessage());
			// throw new RuntimeException(e);
		}
	}
	private void ParseProperty(NodeList fields){
        String attr = null;
        String value = null;
        for (int j = 0; j < fields.getLength(); j++) {
        	Node fieldNode = fields.item(j);
        	if (!(fieldNode instanceof Element))
        		continue;
        	Element field = (Element)fieldNode;
        	if ("name".equals(field.getTagName()) && field.hasChildNodes())
        		attr = ((Text)field.getFirstChild()).getData().trim();
        	if ("value".equals(field.getTagName()) && field.hasChildNodes())
        		value = ((Text)field.getFirstChild()).getData();
        }
        // Ignore this parameter if it has already been marked as 'final'
        if (attr != null) {
        	this.loadProperty(attr, value);
        }
	}
	private void ParseInterface(NodeList fields){
		String interfacekey = null;
		InterfaceProperty interfaceproperty = new InterfaceProperty();
		String temp_mode = null,temp_call_func = null,temp_name = null,temp_oversecs = null,temp_class_parse = null,temp_class_perform = null;
		String temp_zlbm = null;
        for (int j = 0; j < fields.getLength(); j++) {
        	Node fieldNode = fields.item(j);
        	if (!(fieldNode instanceof Element))
        		continue;
        	Element field = (Element)fieldNode;
        	if ("function".equals(field.getTagName()) && field.hasChildNodes())
        		interfacekey = ((Text)field.getFirstChild()).getData().trim();
        	if ("call_func".equals(field.getTagName()) && field.hasChildNodes())
        		temp_call_func = ((Text)field.getFirstChild()).getData().trim();
        	if ("zlbm".equals(field.getTagName()) && field.hasChildNodes())
        		temp_zlbm = ((Text)field.getFirstChild()).getData().trim();
        	if ("name".equals(field.getTagName()) && field.hasChildNodes())
        		temp_name = ((Text)field.getFirstChild()).getData().trim();
        	if ("mode".equals(field.getTagName()) && field.hasChildNodes())
        		temp_mode = ((Text)field.getFirstChild()).getData().trim();
        	if ("oversecs".equals(field.getTagName()) && field.hasChildNodes())
        		temp_oversecs = ((Text)field.getFirstChild()).getData().trim();
        	if ("class_parse".equals(field.getTagName()) && field.hasChildNodes())
        		temp_class_parse = ((Text)field.getFirstChild()).getData().trim();
        	if ("class_perform".equals(field.getTagName()) && field.hasChildNodes())
        		temp_class_perform = ((Text)field.getFirstChild()).getData();
        }
        log.debug("begin load interface property:" + interfacekey);
        // Ignore this parameter if it has already been marked as 'final'
        if (interfacekey != null){
        	if("".equals(interfacekey.trim()))
        		interfacekey = null;
        	if((temp_zlbm == null) || ("".equals(temp_zlbm.trim())))
        		interfacekey = null;
        	if((temp_class_parse == null) || ("".equals(temp_class_parse.trim()))){
        		temp_class_parse = null;
        		log.debug("interface(" + interfacekey + ") do not configure class_parse.");
        	}
        	if((temp_class_perform == null) || ("".equals(temp_class_perform.trim()))){
        		temp_class_perform = null;
        		log.debug("interface(" + interfacekey + ") do not configure class_perform.");
        	}
        }
        if(interfacekey != null){
   			interfaceproperty.function = interfacekey.trim();
   			if(temp_call_func == null)
   				interfaceproperty.call_func = "";
   			else
   				interfaceproperty.call_func = temp_call_func.trim();
   			interfaceproperty.zlbm = temp_zlbm.trim();
   			if(temp_name == null)
   				interfaceproperty.name = "";
   			else
   				interfaceproperty.name = temp_name.trim();
   			if(temp_mode == null)
   				interfaceproperty.mode = "unblock";
   			else if("priority".equals(temp_mode.trim()))
    			interfaceproperty.mode = "priority";
    		else if("block".equals(temp_mode.trim()))
    			interfaceproperty.mode = "block";
    		else
    			interfaceproperty.mode = "unblock";
   			if((temp_oversecs != null) && (!"".equals(temp_oversecs.trim())))
   				interfaceproperty.oversecs = new Long(temp_oversecs.trim());
   			else
   	   			interfaceproperty.oversecs = this.getPropertyLong(FKConfigureKeys.CAMS_FK_SYSTEM_OVERSECS_KEY);
   			if(temp_class_parse != null) interfaceproperty.class_parse = temp_class_parse.trim();
   			if(temp_class_perform != null)	interfaceproperty.class_perform = temp_class_perform.trim();
        }
    	if(interfacekey != null){
    		// 将接口对象放到配置Map对象中，使用function作为Key值
        	this.interfaces.put(interfacekey, interfaceproperty);
        }
	}
	
	private void loadProperty(String attr, String value) {
		if (value != null) {
			if(this.properties.getProperty(attr) != null)
				this.properties.setProperty(attr, value);
		}
	}
}
