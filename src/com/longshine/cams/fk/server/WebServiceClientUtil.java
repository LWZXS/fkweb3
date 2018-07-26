package com.longshine.cams.fk.server;

import java.lang.reflect.Method;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;

public class WebServiceClientUtil {
	private static Log logger = LogFactory.getLog(WebServiceClientUtil.class);
	
	public static Object callCxfService(final String v_server_url,
			final String v_call_function,
			Class<?>[] v_param_types,
			Class<?> v_interface_class,
			Object...v_parms) throws Exception{
		return callCxfService(v_server_url,v_call_function,v_param_types,v_interface_class,null,null,v_parms);
	}
	/**完整的Service调用方法
	 * @param v_server_url 服务地址
	 * @param v_call_function 接口方法名
	 * @param v_param_types 参数类型列表
	 * @param v_interface_class 接口类，该接口是服务端实现，本地是接口类的方法定义描述
	 * @param v_call_interceptor 安装调用出去的拦截器，用于保存调用生成的XML结构化数据，以便调试跟踪
	 * @param v_resp_interceptor 安装调用出去后服务端应答的返回内容拦截器，用于保存调用生成的XML结构化数据，以便调试跟踪
	 * @param v_parms 调用服务的接口参数列表
	 * @return 返回接口的对象类型
	 * @throws Exception 远程异常直接抛出
	 */
	public static Object callCxfService(final String v_server_url,
			final String v_call_function,
			Class<?>[] v_param_types,
			Class<?> v_interface_class,
			Interceptor<?> v_call_interceptor,
			Interceptor<?> v_resp_interceptor,
			Object...v_parms) throws Exception{
		JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
		if(v_call_interceptor != null)
			factory.getOutInterceptors().add(v_call_interceptor);
		if(logger.isDebugEnabled())
			factory.getOutInterceptors().add(new LoggingOutInterceptor());
		if(v_resp_interceptor != null)
			factory.getInInterceptors().add(v_resp_interceptor);
		if(logger.isDebugEnabled())
			factory.getInInterceptors().add(new LoggingInInterceptor());
		factory.setServiceClass(v_interface_class);
		factory.setAddress(v_server_url);

		Object serviceObj = factory.create();
		Method m = serviceObj.getClass().getMethod(v_call_function,v_param_types);
		
		// 修改调出请求体中的命令空间前缀
		Client client = ClientProxy.getClient(serviceObj);
		HashMap<String, String> hmap = new HashMap<String, String>();
		hmap.put("SOAP-ENV", "http://schemas.xmlsoap.org/soap/envelope/");
		client.getRequestContext().put("soap.env.ns.map", hmap);
		client.getRequestContext().put("disable.outputstream.optimization", "true");
		
		return m.invoke(serviceObj, v_parms);
	}
}
