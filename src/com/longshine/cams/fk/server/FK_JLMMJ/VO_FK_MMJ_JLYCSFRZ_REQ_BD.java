package com.longshine.cams.fk.server.FK_JLMMJ;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@SuppressWarnings("restriction")
@XmlType(name="VO_FK_MMJ_JLYCSFRZ_REQ_BD")
public class VO_FK_MMJ_JLYCSFRZ_REQ_BD {
	// 密钥状态；整型，0: 测试密钥状态；1: 正式密钥状态；
	private Long MYZT;
	// 分散因子；字符型,8字节，“0000”+表号；
	private String FSYZ;

	@XmlElement(name="MYZT",namespace="http://soa.csg.cn")
	public Long getMYZT() {
		return MYZT;
	}
	@XmlElement(name="FSYZ",namespace="http://soa.csg.cn")
	public String getFSYZ() {
		return FSYZ;
	}
	public void setMYZT(Long mYZT) {
		MYZT = mYZT;
	}
	public void setFSYZ(String fSYZ) {
		FSYZ = fSYZ;
	}
	@Override
	public String toString(){
		return "[FK_MMJ_JLYCSFRZ_REQ]MYZT:" + MYZT + ";FSYZ:" + FSYZ;
	}
}
