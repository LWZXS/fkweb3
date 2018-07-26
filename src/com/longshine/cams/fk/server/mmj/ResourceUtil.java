package com.longshine.cams.fk.server.mmj;

import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.TreeMap;

import org.apache.commons.io.IOUtils;


public class ResourceUtil {

	//属性
	private static Properties properties=null; 
	
	private static boolean debug=false;
	
	private static String [] configs={
		 "config.properties","mmjcfg.properties"
	};

	public static boolean isDebug() {
		return debug;
	}

	public static void setDebug(boolean debug) {
		ResourceUtil.debug = debug;
	}

	private static void init(){
		try {
			if(debug){
				properties=loadAllProperties(configs);
			}else{ //通过spring 方式获取属性
				properties=(Properties) SpringContextUtil.getBean("configproperties");
			}
		} catch (Throwable e) { //spring 容器为初始化( main 方法启动  通过属性文件直接获取属性)
			properties=loadAllProperties(configs);
		}

	}

	public static String getKeyValue(String key){
		return getKeyValue(key,null);
	}

	public static String getKeyValue(String key,String defaultVal){
		if(properties==null || isDebug()){
			init();
		}
		if(key==null)
			return defaultVal;
		return properties.getProperty(key,defaultVal);
	}
	
	public static Properties loadAllProperties(String []  paths){
		ClassLoader cl=org.springframework.util.ClassUtils.getDefaultClassLoader();
		
		Properties pros=new Properties();
		for(String path : paths){
			if(path!=null  && !"".equals(path)){
				InputStream in=null;
				try {
					Properties pro=new Properties();
					in=cl.getResourceAsStream(path);
					if(path.endsWith(".xml")){
						pro.loadFromXML(in);
					}else if(in!=null){						
						pro.load(in);
					}
						pros.putAll(pro);
				} catch (Exception e) {
					e.printStackTrace();
				}finally{
					IOUtils.closeQuietly(in);
				}
			}
		}
		return pros;
	}
	
	public static void main(String[] args) {
		Properties prop=ResourceUtil.loadAllProperties(ResourceUtil.configs);
		
		@SuppressWarnings("unchecked")
		Map<String,String> orderedProp = new TreeMap(prop);
        Iterator<Entry<String,String>> itr = orderedProp.entrySet().iterator();
        String template="<entry key=\"?\">?</entry>";
        while (itr.hasNext()) {
            Entry<String,String> entry = (Entry<String,String>) itr.next();
            String key=entry.getKey();
            String value=entry.getValue();
            if(key.endsWith(".sql")){
            	value="<![CDATA[ \n"+value+" \n]]>";
            }
//            System.out.println(SimpleTemplate.getResult(template,key,value));
            if(key.endsWith(".key") ){
            	System.out.println();
            	System.out.println();
            }
            System.out.println(key +":"+ value);
        }
		
		
	}
}
