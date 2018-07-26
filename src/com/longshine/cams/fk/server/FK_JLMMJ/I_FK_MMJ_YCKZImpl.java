package com.longshine.cams.fk.server.FK_JLMMJ;

import java.text.SimpleDateFormat;
import java.util.Date;

public class I_FK_MMJ_YCKZImpl implements I_FK_MMJ_YCKZ {

	//日期属性格式
		private static final ThreadLocal<SimpleDateFormat> sd = new ThreadLocal<SimpleDateFormat>(){
			@Override
			protected SimpleDateFormat initialValue() {
				return  new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			}
		};
		
	@Override
	public VO_FK_MMJ_YCKZ_RES I_FK_MMJ_YCKZ(VO_FK_MMJ_YCKZ_REQ req) {
		VO_FK_MMJ_YCKZ_RES vo=new VO_FK_MMJ_YCKZ_RES();
		VO_FK_MMJ_YCKZ_RES_BD bd=new VO_FK_MMJ_YCKZ_RES_BD();
		bd.setJSZT(0L);
		bd.setYCXX(req.getBd().getMYZT().toString());
		bd.setKZMLMW(req.getBd().getFSYZ());
		vo.setBd(bd);
		vo.setReplyCode("OK");
		
		System.out.println(sd.get().format(new Date()) +" I_FK_MMJ_YCKZ req-->"+req.getBd().toString());
		System.out.println(sd.get().format(new Date())+"I_FK_MMJ_YCKZ res-->"+bd.toString());
		return vo;
	}

}
