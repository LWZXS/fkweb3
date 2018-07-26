package com.longshine.cams.fk.interfaces.FK_JLZDH_DYFKYHYCKZ;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.longshine.cams.fk.interfaces.common.VO_FK_COMMON_RESP_YX;

@SuppressWarnings("restriction")
@XmlType(name="VO_FK_JLZDH_DYFKYHYCKZ_RES")
public class VO_FK_JLZDH_DYFKYHYCKZ_RES {

	private String replyCode;	// SOAP name:replyCode
	private VO_FK_COMMON_RESP_YX resp;

	public VO_FK_JLZDH_DYFKYHYCKZ_RES(){
		replyCode = "OK";
		resp = new VO_FK_COMMON_RESP_YX();
	}
	
	@XmlElement(name="replyCode",namespace="http://soa.csg.cn")
	public String getReplyCode() {
		return replyCode;
	}
	@XmlElement(name="FK_JLZDH_DYFKYHYCKZ_OUT",namespace="http://soa.csg.cn")
	public VO_FK_COMMON_RESP_YX getResp() {
		return resp;
	}
	public void setReplyCode(String replyCode) {
		this.replyCode = replyCode;
	}
	public void setResp(VO_FK_COMMON_RESP_YX resp) {
		this.resp = resp;
	}
	
}
