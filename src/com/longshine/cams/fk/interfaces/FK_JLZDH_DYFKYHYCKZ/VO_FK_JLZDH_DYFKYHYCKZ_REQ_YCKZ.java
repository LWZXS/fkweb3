package com.longshine.cams.fk.interfaces.FK_JLZDH_DYFKYHYCKZ;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@SuppressWarnings("restriction")
@XmlType(name="VO_FK_JLZDH_DYFKYHYCKZ_REQ_YCKZ")
public class VO_FK_JLZDH_DYFKYHYCKZ_REQ_YCKZ {
	/**本地费控用户参数更新    输入
	 * Varchar(20)	JYLSH	交易流水号
	 * Varchar(20)	GDDWBM	供电单位编码
	 * Varchar(20)	JLDBH	计量点编号
	 * Varchar2(32) DBZCBH	电表资产编号
	 * Varchar2(32)	YHBH	用户编号
	 * Varchar2(20)	KZLX	控制类型
							1A:拉闸
							1B:合闸允许
							1C:直接合闸
							2A:报警
							2B:报警解除
							3A:保电
							3B:保电解除
	 * Varchar2(20)	ZXCS		执行次数：计量自动化失败自动下发指令的最多次数
	 */
	private String JYLSH;
	private String GDDWBM;
	private String JLDBH;
	private String DBZCBH;
	private String YHBH;
	private String KZLX;
	private BigDecimal ZXCS;
	
	@XmlElement(name="JYLSH",namespace="http://soa.csg.cn")
	public String getJYLSH() {
		return JYLSH;
	}
	@XmlElement(name="GDDWBM",namespace="http://soa.csg.cn")
	public String getGDDWBM() {
		return GDDWBM;
	}
	@XmlElement(name="JLDBH",namespace="http://soa.csg.cn")
	public String getJLDBH() {
		return JLDBH;
	}
	@XmlElement(name="DBZCBH",namespace="http://soa.csg.cn")
	public String getDBZCBH() {
		return DBZCBH;
	}
	@XmlElement(name="YHBH",namespace="http://soa.csg.cn")
	public String getYHBH() {
		return YHBH;
	}
	@XmlElement(name="KZLX",namespace="http://soa.csg.cn")
	public String getKZLX() {
		return KZLX;
	}	
	@XmlElement(name="ZXCS",namespace="http://soa.csg.cn")
	public BigDecimal getZXCS() {
		return ZXCS;
	}
	
	public void setJYLSH(String jYLSH) {
		JYLSH = jYLSH;
	}
	
	public void setGDDWBM(String gDDWBM) {
		GDDWBM = gDDWBM;
	}
	
	public void setJLDBH(String jLDBH) {
		JLDBH = jLDBH;
	}
	
	public void setDBZCBH(String dBZCBH) {
		DBZCBH = dBZCBH;
	}
	
	public void setYHBH(String yHBH) {
		YHBH = yHBH;
	}
	
	public void setKZLX(String kZLX) {
		KZLX = kZLX;
	}
	public void setZXCS(BigDecimal zXCS) {
		ZXCS = zXCS;
	}
}
