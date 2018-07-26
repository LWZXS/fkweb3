package com.longshine.cams.fk.interfaces.common;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@SuppressWarnings("restriction")
@XmlType(name="VO_FK_COMMON_RESP")
public class VO_FK_COMMON_RESP {
	// 接收状态，成功返回0，失败返回-1 JSZT Number(8)
	private Long JSZT;
	// 异常信息，成功返回空，错误返回系统处理异常信息 YCXX Varchar2(64)
	private String YCXX;
	
	@XmlElement(name="JSZT",namespace="http://mk.soa.csg.cn")
	public Long getJSZT() {
		return JSZT;
	}
	public void setJSZT(Long jSZT) {
		JSZT = jSZT;
	}
	@XmlElement(name="YCXX",namespace="http://mk.soa.csg.cn")
	public String getYCXX() {
		if(JSZT == 0)
			return "OK";
		else
			return YCXX;
	}
	public void setYCXX(String yCXX) {
		YCXX = yCXX;
	}
}
