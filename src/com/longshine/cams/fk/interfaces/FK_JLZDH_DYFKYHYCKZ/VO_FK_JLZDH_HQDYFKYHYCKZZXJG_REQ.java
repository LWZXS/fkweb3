package com.longshine.cams.fk.interfaces.FK_JLZDH_DYFKYHYCKZ;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@SuppressWarnings("restriction")
@XmlType(name="VO_FK_JLZDH_HQDYFKYHYCKZZXJG_REQ")
public class VO_FK_JLZDH_HQDYFKYHYCKZZXJG_REQ {
	private VO_FK_JLZDH_HQDYFKYHYCKZZXJG_REQ_YCKZZXJG YCKZZXJG;

	@XmlElement(name="FK_JLZDH_DYFKYHYCKZZXJG",namespace="http://mk.soa.csg.cn")
	public VO_FK_JLZDH_HQDYFKYHYCKZZXJG_REQ_YCKZZXJG getYCKZZXJG() {
		return YCKZZXJG;
	}

	public void setYCKZZXJG(VO_FK_JLZDH_HQDYFKYHYCKZZXJG_REQ_YCKZZXJG yCKZZXJG) {
		this.YCKZZXJG = yCKZZXJG;
	}
}
