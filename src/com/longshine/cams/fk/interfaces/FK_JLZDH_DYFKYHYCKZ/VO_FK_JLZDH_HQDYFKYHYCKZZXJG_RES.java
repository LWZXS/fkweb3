package com.longshine.cams.fk.interfaces.FK_JLZDH_DYFKYHYCKZ;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.longshine.cams.fk.interfaces.common.VO_FK_COMMON_RESP;

@SuppressWarnings("restriction")
@XmlType(name="VO_FK_JLZDH_HQDYFKYHYCKZZXJG_RES")
public class VO_FK_JLZDH_HQDYFKYHYCKZZXJG_RES {

	private String replyCode;	// SOAP name:replyCode
	private VO_FK_COMMON_RESP resp;

	@XmlElement(name="replyCode",namespace="http://mk.soa.csg.cn")
	public String getReplyCode() {
		return replyCode;
	}
	@XmlElement(name="FK_JLZDH_DYFKYHYCKZZXJG_OUT",namespace="http://mk.soa.csg.cn")
	public VO_FK_COMMON_RESP getResp() {
		return resp;
	}
	public void setReplyCode(String replyCode) {
		this.replyCode = replyCode;
	}
	public void setResp(VO_FK_COMMON_RESP resp) {
		this.resp = resp;
	}
}
