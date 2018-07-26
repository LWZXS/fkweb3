package com.longshine.cams.fk.interfaces.common;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.apache.cxf.annotations.WSDLDocumentation;

@SuppressWarnings("restriction")
@WebService(targetNamespace="http://mk.soa.csg.cn")
@SOAPBinding(parameterStyle=SOAPBinding.ParameterStyle.BARE)
public interface I_FK_JLZDH_FHFKSJXFZXJG_Schema {
	@WSDLDocumentation("本地费控用户远程充值")
	@WebMethod(operationName="I_FK_JLZDH_FHFKSJXFZXJG",action="I_FK_JLZDH_FHFKSJXFZXJG")
	@WebResult(name="I_FK_JLZDH_FHFKSJXFZXJGResponse",targetNamespace="http://mk.soa.csg.cn")
	public VO_FK_JLZDH_FHFKSJXFZXJG_RES I_FK_JLZDH_FHFKSJXFZXJG(@WebParam(name="I_FK_JLZDH_FHFKSJXFZXJGRequest",targetNamespace="http://mk.soa.csg.cn")VO_FK_JLZDH_FHFKSJXFZXJG_REQ req);
}
