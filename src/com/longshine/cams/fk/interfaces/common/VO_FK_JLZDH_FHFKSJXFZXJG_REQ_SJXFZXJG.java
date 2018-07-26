package com.longshine.cams.fk.interfaces.common;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@SuppressWarnings("restriction")
@XmlType(name="VO_FK_JLZDH_FHFKSJXFZXJG_REQ_SJXFZXJG")
public class VO_FK_JLZDH_FHFKSJXFZXJG_REQ_SJXFZXJG {
	/**字段列表
	 * Varchar(20)	JYLSH	交易流水号
	 * Varchar(20)	GDDWBM	供电单位编码
	 * Varchar(20)	JLDBH	计量点编号
	 * Varchar2(32)	DBZCBH	电表资产编号
	 * Varchar2(32)	YHBH	用户编号
	 * Varchar2(8)	YCSFRZJG	远程身份认证结果；0成功；其他为错误码，参考附件错误码列表
	 * Varchar(20)	SJS1	随机数2，4字节
	 * Varchar(20)	AQMKXLH	安全模块(ESAM)序列号，8字节
	 */
	private String JYLSH;
	private String GDDWBM;
	private String JLDBH;
	private String DBZCBH;
	private String YHBH;
	private String ZXJG;
	
	@XmlElement(name="JYLSH" ,namespace="http://mk.soa.csg.cn")
	public String getJYLSH() {
		return JYLSH;
	}
	@XmlElement(name="GDDWBM" ,namespace="http://mk.soa.csg.cn")
	public String getGDDWBM() {
		return GDDWBM;
	}
	@XmlElement(name="JLDBH" ,namespace="http://mk.soa.csg.cn")
	public String getJLDBH() {
		return JLDBH;
	}
	@XmlElement(name="DBZCBH" ,namespace="http://mk.soa.csg.cn")
	public String getDBZCBH() {
		return DBZCBH;
	}
	@XmlElement(name="YHBH" ,namespace="http://mk.soa.csg.cn")
	public String getYHBH() {
		return YHBH;
	}
	@XmlElement(name="ZXJG" ,namespace="http://mk.soa.csg.cn")
	public String getZXJG() {
		return ZXJG;
	}
	
	public void setJYLSH(String jYLSH) {
		this.JYLSH = jYLSH;
	}
	public void setGDDWBM(String gDDWBM) {
		this.GDDWBM = gDDWBM;
	}
	public void setJLDBH(String jLDBH) {
		this.JLDBH = jLDBH;
	}
	public void setDBZCBH(String dBZCBH) {
		this.DBZCBH = dBZCBH;
	}
	public void setYHBH(String yHBH) {
		this.YHBH = yHBH;
	}
	public void setZXJG(String zXJG) {
		this.ZXJG = zXJG;
	}
}
