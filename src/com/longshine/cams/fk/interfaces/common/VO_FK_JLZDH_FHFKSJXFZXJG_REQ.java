package com.longshine.cams.fk.interfaces.common;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@SuppressWarnings("restriction")
@XmlType(name="VO_FK_JLZDH_FHFKSJXFZXJG_REQ")
public class VO_FK_JLZDH_FHFKSJXFZXJG_REQ {
	private VO_FK_JLZDH_FHFKSJXFZXJG_REQ_SJXFZXJG SJXFZXJG;

	@XmlElement(name="FK_JLZDH_SJXFZXJG",namespace="http://mk.soa.csg.cn")
	public VO_FK_JLZDH_FHFKSJXFZXJG_REQ_SJXFZXJG getSJXFZXJG() {
		return SJXFZXJG;
	}

	public void setSJXFZXJG(VO_FK_JLZDH_FHFKSJXFZXJG_REQ_SJXFZXJG SJXFZXJG) {
		this.SJXFZXJG = SJXFZXJG;
	}
}
