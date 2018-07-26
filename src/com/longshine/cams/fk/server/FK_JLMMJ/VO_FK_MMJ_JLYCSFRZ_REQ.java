package com.longshine.cams.fk.server.FK_JLMMJ;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@SuppressWarnings("restriction")
@XmlType(name="VO_FK_MMJ_JLYCSFRZ_REQ")
public class VO_FK_MMJ_JLYCSFRZ_REQ {
	private VO_FK_MMJ_JLYCSFRZ_REQ_BD bd;

	@XmlElement(name="FK_MMJ_JLYCSFRZ",namespace="http://soa.csg.cn")
	public VO_FK_MMJ_JLYCSFRZ_REQ_BD getBd() {
		return bd;
	}

	public void setBd(VO_FK_MMJ_JLYCSFRZ_REQ_BD bd) {
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
