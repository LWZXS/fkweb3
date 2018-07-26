package com.longshine.cams.fk.server.FK_JLMMJ;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@SuppressWarnings("restriction")
@XmlType(name="VO_FK_MMJ_YCKZ_RES_BD")
public class VO_FK_MMJ_YCKZ_RES_BD {
	// 控制命令密文；输出的数据，字符型，20字节。
	private String KZMLMW;
	// 接收状态，成功返回0，失败返回-1
	private Long JSZT;
	// 异常信息，成功返回空，错误返回系统处理异常信息。 
	private String YCXX;

	@XmlElement(name="KZMLMW",namespace="http://soa.csg.cn")
	public String getKZMLMW() {
		return KZMLMW;
	}
	@XmlElement(name="JSZT",namespace="http://soa.csg.cn")
	public Long getJSZT() {
		return JSZT;
	}
	@XmlElement(name="YCXX",namespace="http://soa.csg.cn")
	public String getYCXX() {
		return YCXX;
	}
	public void setKZMLMW(String kZMLMW) {
		KZMLMW = kZMLMW;
	}
	public void setJSZT(Long jSZT) {
		JSZT = jSZT;
	}
	public void setYCXX(String yCXX) {
		YCXX = yCXX;
	}
	@Override
	public String toString(){
		return "[VO_FK_MMJ_YCKZ_RES_BD]KZMLMW:" + this.getKZMLMW() + ";JSZT:" + this.getJSZT() + ";YCXX:" + this.getYCXX();
	}
}
