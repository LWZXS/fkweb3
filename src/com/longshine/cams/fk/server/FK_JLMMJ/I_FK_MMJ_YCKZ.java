package com.longshine.cams.fk.server.FK_JLMMJ;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.apache.cxf.annotations.WSDLDocumentation;

	@SuppressWarnings("restriction")
	@WebService(targetNamespace="http://soa.csg.cn")
	@SOAPBinding(parameterStyle=SOAPBinding.ParameterStyle.BARE)
	public interface I_FK_MMJ_YCKZ {
		@WSDLDocumentation("计量系统向密码机请求，获取远程控制密文")
		@WebMethod(operationName="I_FK_MMJ_YCKZ",action="I_FK_MMJ_YCKZ")
		@WebResult(name="I_FK_MMJ_YCKZResponse",targetNamespace="http://soa.csg.cn")
		public VO_FK_MMJ_YCKZ_RES I_FK_MMJ_YCKZ(@WebParam(name="I_FK_MMJ_YCKZRequest",targetNamespace="http://soa.csg.cn") VO_FK_MMJ_YCKZ_REQ req);
}
