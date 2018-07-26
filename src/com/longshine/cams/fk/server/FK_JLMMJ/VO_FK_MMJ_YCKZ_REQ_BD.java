package com.longshine.cams.fk.server.FK_JLMMJ;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@SuppressWarnings("restriction")
@XmlType(name="VO_FK_MMJ_YCKZ_REQ_BD")
public class VO_FK_MMJ_YCKZ_REQ_BD {
	// 密钥状态，整型，0: 测试密钥状态；1: 正式密钥状态；
	private Long MYZT;
	// 密码机随机数,字符型,4字节，电表身份认证成功后返回；
	private String MMJSJS;
	// 分散因子,字符型,8字节，“0000”+表号；
	private String FSYZ;
	// 安全模块序列号, 字符型, 8字节；
	private String AQMKXLH;
	// 控制命令数据；表示拉闸、合闸、报警等控制命令明文,字符型,8字节；
	private String KZMLSJ;

	@XmlElement(name="MYZT",namespace="http://soa.csg.cn")
	public Long getMYZT() {
		return MYZT;
	}
	@XmlElement(name="MMJSJS",namespace="http://soa.csg.cn")
	public String getMMJSJS() {
		return MMJSJS;
	}
	@XmlElement(name="FSYZ",namespace="http://soa.csg.cn")
	public String getFSYZ() {
		return FSYZ;
	}
	@XmlElement(name="AQMKXLH",namespace="http://soa.csg.cn")
	public String getAQMKXLH() {
		return AQMKXLH;
	}
	@XmlElement(name="KZMLSJ",namespace="http://soa.csg.cn")
	public String getKZMLSJ() {
		return KZMLSJ;
	}
	public void setMYZT(Long mYZT) {
		MYZT = mYZT;
	}
	public void setMMJSJS(String mMJSJS) {
		MMJSJS = mMJSJS;
	}
	public void setFSYZ(String fSYZ) {
		FSYZ = fSYZ;
	}
	public void setAQMKXLH(String aQMKXLH) {
		AQMKXLH = aQMKXLH;
	}
	public void setKZMLSJ(String kZMLSJ) {
		KZMLSJ = kZMLSJ;
	}
	@Override
	public String toString(){
		return "[FK_MMJ_YCKZ_REQ]MYZT:" + MYZT + ";MMJSJS:" + MMJSJS + ";FSYZ:" + FSYZ + ";AQMKXLH:" + AQMKXLH + ";KZMLSJ:" + KZMLSJ;
	}
}
