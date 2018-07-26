package com.longshine.cams.fk.interfaces.FK_JLZDH_DYFKYHYCKZ;

import com.longshine.cams.fk.common.BaseServiceImpl;
import com.longshine.cams.fk.interfaces.common.VO_FK_COMMON_RESP_YX;

public class Impl_FK_JLZDH_DYFKYHYCKZ extends BaseServiceImpl implements I_FK_JLZDH_DYFKYHYCKZ_Schema{

	public VO_FK_JLZDH_DYFKYHYCKZ_RES I_FK_JLZDH_DYFKYHYCKZ(VO_FK_JLZDH_DYFKYHYCKZ_REQ req) {	
		this.getLogger().info("I_FK_JLZDH_DYFKYHYCKZ be Called");
		
		System.out.println("I_FK_JLZDH_DYFKYHYCKZ"+ req);
		VO_FK_JLZDH_DYFKYHYCKZ_RES vo=new VO_FK_JLZDH_DYFKYHYCKZ_RES();
		VO_FK_COMMON_RESP_YX resp=new VO_FK_COMMON_RESP_YX();
		resp.setJSZT(1L);
		resp.setYCXX("YCXX");
		vo.setReplyCode("OK");
		vo.setResp(resp);
		return vo;
	}
}
