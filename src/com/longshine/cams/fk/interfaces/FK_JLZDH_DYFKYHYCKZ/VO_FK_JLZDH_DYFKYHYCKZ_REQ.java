package com.longshine.cams.fk.interfaces.FK_JLZDH_DYFKYHYCKZ;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@SuppressWarnings("restriction")
@XmlType(name="VO_FK_JLZDH_DYFKYHYCKZ_REQ")
public class VO_FK_JLZDH_DYFKYHYCKZ_REQ {
	
	private VO_FK_JLZDH_DYFKYHYCKZ_REQ_YCKZ YCKZ;

	@XmlElement(name="FK_JLZDH_DYFKYHYCKZ",namespace="http://soa.csg.cn")
	public VO_FK_JLZDH_DYFKYHYCKZ_REQ_YCKZ getYCKZ() {
		return YCKZ;
	}

	public void setYCKZ(VO_FK_JLZDH_DYFKYHYCKZ_REQ_YCKZ yCKZ) {
		this.YCKZ = yCKZ;
	}
}
