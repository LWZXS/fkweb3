package com.longshine.cams.fk.server.FK_JLMMJ;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@SuppressWarnings("restriction")
@XmlType(name="VO_FK_MMJ_JLYCSFRZ_RES_BD")
public class VO_FK_MMJ_JLYCSFRZ_RES_BD {
	// 密码机随机数,字符型,8字节
	private String MMJSJS;
	// 随机数密文,字符型,8字节。
	private String SJSMW;
	// 接收状态，成功返回0，失败返回-1。
	private Long JSZT;
	// 异常信息，成功返回空，错误返回系统处理异常信息，错误码详见附件-密码机异常代码表
	private String YCXX;

	@XmlElement(name="MMJSJS",namespace="http://soa.csg.cn")
	public String getMMJSJS() {
		return MMJSJS;
	}
	@XmlElement(name="SJSMW",namespace="http://soa.csg.cn")
	public String getSJSMW() {
		return SJSMW;
	}
	@XmlElement(name="JSZT",namespace="http://soa.csg.cn")
	public Long getJSZT() {
		return JSZT;
	}
	@XmlElement(name="YCXX",namespace="http://soa.csg.cn")
	public String getYCXX() {
		return YCXX;
	}
	public void setMMJSJS(String mMJSJS) {
		MMJSJS = mMJSJS;
	}
	public void setSJSMW(String sJSMW) {
		SJSMW = sJSMW;
	}
	public void setJSZT(Long jSZT) {
		JSZT = jSZT;
	}
	public void setYCXX(String yCXX) {
		YCXX = yCXX;
	}
	@Override
	public String toString(){
		return "[FK_MMJ_JLYCSFRZ_RES]MMJSJS:" + MMJSJS + ";SJSMW:" + SJSMW + ";JSZT:" + JSZT + ";YCXX:" + YCXX;
	}
}
