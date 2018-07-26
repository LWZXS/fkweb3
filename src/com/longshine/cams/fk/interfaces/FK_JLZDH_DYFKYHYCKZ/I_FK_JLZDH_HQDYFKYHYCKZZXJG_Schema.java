package com.longshine.cams.fk.interfaces.FK_JLZDH_DYFKYHYCKZ;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.apache.cxf.annotations.WSDLDocumentation;

	@SuppressWarnings("restriction")
	@WebService(targetNamespace="http://mk.soa.csg.cn")
	@SOAPBinding(parameterStyle=SOAPBinding.ParameterStyle.BARE)
public interface I_FK_JLZDH_HQDYFKYHYCKZZXJG_Schema {
		@WSDLDocumentation("获取低压费控用户远程控制执行结果")
		@WebMethod(operationName="I_FK_JLZDH_HQDYFKYHYCKZZXJG",action="I_FK_JLZDH_HQDYFKYHYCKZZXJG")
		@WebResult(name="I_FK_JLZDH_HQDYFKYHYCKZZXJGResponse",targetNamespace="http://mk.soa.csg.cn")
		public VO_FK_JLZDH_HQDYFKYHYCKZZXJG_RES I_FK_JLZDH_HQDYFKYHYCKZZXJG(@WebParam(name="I_FK_JLZDH_HQDYFKYHYCKZZXJGRequest",targetNamespace="http://mk.soa.csg.cn")VO_FK_JLZDH_HQDYFKYHYCKZZXJG_REQ req);
}
