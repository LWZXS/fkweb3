package com.longshine.cams.fk.server.FK_JLMMJ;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@SuppressWarnings("restriction")
@XmlType(name="VO_FK_MMJ_YCKZ_RES")
public class VO_FK_MMJ_YCKZ_RES {
	private VO_FK_MMJ_YCKZ_RES_BD bd;
	private String replyCode;

	@XmlElement(name="FK_MMJ_YCKZ_OUT",namespace="http://soa.csg.cn")
	public VO_FK_MMJ_YCKZ_RES_BD getBd() {
		return bd;
	}
	@XmlElement(name="replyCode",namespace="http://soa.csg.cn")
	public String getReplyCode() {
		return replyCode;
	}
	public void setReplyCode(String replyCode) {
		this.replyCode = replyCode;
	}
	public void setBd(VO_FK_MMJ_YCKZ_RES_BD bd) {
		this.bd = bd;
	}
	@Override
	public String toString(){
		String ret = null;
		if(this.bd != null)
			return this.bd.toString();
		else
			ret = "no body.";
		return ret;
	}
}
