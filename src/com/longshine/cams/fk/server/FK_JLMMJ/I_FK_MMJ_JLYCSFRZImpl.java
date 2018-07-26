package com.longshine.cams.fk.server.FK_JLMMJ;

import java.text.SimpleDateFormat;
import java.util.Date;

public class I_FK_MMJ_JLYCSFRZImpl implements I_FK_MMJ_JLYCSFRZ {

	//日期属性格式
		private static final ThreadLocal<SimpleDateFormat> sd = new ThreadLocal<SimpleDateFormat>(){
			@Override
			protected SimpleDateFormat initialValue() {
				return  new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			}
		};
		
	@Override
	public VO_FK_MMJ_JLYCSFRZ_RES I_FK_MMJ_JLYCSFRZ(VO_FK_MMJ_JLYCSFRZ_REQ req) {
		VO_FK_MMJ_JLYCSFRZ_RES vo=new VO_FK_MMJ_JLYCSFRZ_RES();
		VO_FK_MMJ_JLYCSFRZ_RES_BD bd=new VO_FK_MMJ_JLYCSFRZ_RES_BD();
		bd.setJSZT(0L);
		bd.setYCXX(req.getBd().getMYZT().toString());
		bd.setMMJSJS("c");
		bd.setSJSMW(req.getBd().getFSYZ());
		vo.setReplyCode("OK");
		vo.setBd(bd);
		System.out.println(sd.get().format(new Date())+" I_FK_MMJ_JLYCSFRZ req-->"+req.getBd().toString());
		System.out.println(sd.get().format(new Date())+"I_FK_MMJ_JLYCSFRZ res-->"+bd.toString());
		return vo;
	}

}
