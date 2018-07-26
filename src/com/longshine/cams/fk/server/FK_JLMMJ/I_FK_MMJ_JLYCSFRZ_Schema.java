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
public interface I_FK_MMJ_JLYCSFRZ_Schema {
	@WSDLDocumentation("计量系统向密码机请求，计量远程身份认证")
	@WebMethod(operationName="I_FK_MMJ_JLYCSFRZ",action="I_FK_MMJ_JLYCSFRZ")
	@WebResult(name="I_FK_MMJ_JLYCSFRZResponse",targetNamespace="http://soa.csg.cn")
	public VO_FK_MMJ_JLYCSFRZ_RES I_FK_MMJ_JLYCSFRZ(@WebParam(name="I_FK_MMJ_JLYCSFRZRequest",targetNamespace="http://soa.csg.cn") VO_FK_MMJ_JLYCSFRZ_REQ req);
}
