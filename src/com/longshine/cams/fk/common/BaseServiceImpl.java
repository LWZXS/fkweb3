package com.longshine.cams.fk.common;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.WebServiceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.transport.http.AbstractHTTPDestination;
import com.longshine.cams.fk.server.FKServer;
import com.longshine.cams.fk.structs.FKTask;

@SuppressWarnings("restriction")
public abstract class BaseServiceImpl {
	// 系统日志对象
	protected Log logger;
	protected Log getLogger(){return logger;}
	// 获取FKServer对象
	private FKServer fkserver;
	public BaseServiceImpl(){
		this.fkserver = FKServer.getInstance();
		this.logger = LogFactory.getLog(this.getClass());
	}
	// 获取客户端调用信息，如URL等
	@Resource
	private WebServiceContext wsc;
	
	protected HttpServletRequest getHttpServletRequest(){
		return (HttpServletRequest)wsc.getMessageContext().get(AbstractHTTPDestination.HTTP_REQUEST);
	}
	protected FKServer getFkserver() {
		if(this.fkserver == null)
			this.fkserver = FKServer.getInstance();
		return this.fkserver;
	}

	protected WebServiceContext getWebServiceContext() {
		return wsc;
	}
	protected Object RequestParse(String v_function, Object v_req){
		// 下面开始进行任务处理，流程是构建TaskParseBase对象、创建任务TaskBuilder、检查任务TaskCheck、执行任务TaskPerform、构造返回结果TaskResponseBuilder
		BaseTaskParse parser = getFkserver().getTaskParser(v_function);
		// 如果请求的参数不是这种类似POJO的结构，用户任务解析类可以在Base类的基础上增加自定义TaskBuilder方法来生成任务对象
//		Parse_FK_JLZDH_FKMSBDQHYC parser = (Parse_FK_JLZDH_FKMSBDQHYC)parser_base;
		FKTask task = parser.TaskBuilder(v_req); //创建任务
		Object ret = null;
		long temp_int = 0;
		if(task != null){
			logger.debug("BaseServiceImpl.RequestParse begin call parser.TaskCheck(" + task + ")");
			temp_int = parser.TaskCheck(task);  //任务检查
			logger.debug("BaseServiceImpl.RequestParse call parser.TaskCheck(" + task.TASKID + "), return:" + temp_int);
			if(temp_int == 0){
				logger.debug("BaseServiceImpl.RequestParse begin call parser.TaskParse(" + task + ")");
				temp_int = parser.TaskParse(task);  //任务解析
				logger.debug("BaseServiceImpl.RequestParse call parser.TaskParse(" + task.TASKID + "), return:" + temp_int);
				if(temp_int != 0){	// 任务解析错误处理
					parser.ParseError(task, temp_int);
				}
			}else{	// 任务检查错误处理
				parser.CheckError(task, temp_int);
			}
		}
		logger.debug("BaseServiceImpl.RequestParse begin call parser.TaskResponseBuilder(" + task + ")");
		ret = parser.TaskResponseBuilder(task);  //任务应答
		logger.debug("BaseServiceImpl.RequestParse call parser.TaskResponseBuilder(" + task.TASKID + "), return:" + ret);
		return ret;
	}
}
