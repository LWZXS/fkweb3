package com.longshine.cams.fk.interfaces.FK_JLZDH_DYFKYHYCKZ;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.apache.cxf.annotations.WSDLDocumentation;

@SuppressWarnings("restriction")
@WebService(targetNamespace="http://soa.csg.cn")
@SOAPBinding(parameterStyle=SOAPBinding.ParameterStyle.BARE)
public interface I_FK_JLZDH_DYFKYHYCKZ_Schema {
	@WSDLDocumentation("营销系统发起低压用户远程控制请求，计量自动化系统接收营销系统远程控制请求并下发给费控电能表")
	@WebMethod(operationName="I_FK_JLZDH_DYFKYHYCKZ",action="I_FK_JLZDH_DYFKYHYCKZ")
	@WebResult(name="I_FK_JLZDH_DYFKYHYCKZResponse",targetNamespace="http://soa.csg.cn")
	public VO_FK_JLZDH_DYFKYHYCKZ_RES I_FK_JLZDH_DYFKYHYCKZ(@WebParam(name="I_FK_JLZDH_DYFKYHYCKZRequest",targetNamespace="http://soa.csg.cn") VO_FK_JLZDH_DYFKYHYCKZ_REQ req);
}
