package com.longshine.cams.fk.server.mmj;

import com.longshine.cams.fk.server.FK_JLMMJ.VO_FK_MMJ_JLYCSFRZ_REQ_BD;
import com.longshine.cams.fk.server.FK_JLMMJ.VO_FK_MMJ_JLYCSFRZ_RES_BD;
import com.longshine.cams.fk.server.FK_JLMMJ.VO_FK_MMJ_YCKZ_REQ_BD;
import com.longshine.cams.fk.server.FK_JLMMJ.VO_FK_MMJ_YCKZ_RES_BD;
import com.longshine.cams.fk.structs.FKTask;

import net.sf.json.JSONObject;

public class MMJTest {

	public static void main(String[] args) throws Exception{
		
		int num=2;
		VO_FK_MMJ_JLYCSFRZ_RES_BD jlycsfrz_res=new VO_FK_MMJ_JLYCSFRZ_RES_BD();
		VO_FK_MMJ_JLYCSFRZ_REQ_BD jlycsfrz_req=new VO_FK_MMJ_JLYCSFRZ_REQ_BD();
		jlycsfrz_req.setFSYZ("abcd12345");
		jlycsfrz_req.setMYZT(1L);
		FKTask v_task=new FKTask();
		v_task.TASKID="fk1-1234-abcd";
		
		JSONObject jsonObject = JSONObject.fromObject(jlycsfrz_req);
//		System.out.println(jsonObject.toString());
		long startTime=System.currentTimeMillis();
		for (int i=0;i<num ;i++) {
			jlycsfrz_res = MMJFkUtil.getMMJ_JLYCSFRZ(jlycsfrz_req, v_task);
		}
		System.out.println("秒："+( System.currentTimeMillis() - startTime )/1000);
		
		//////////////////////////////////////////////////////////////
		
		///////////////////////////////////////////////////
		VO_FK_MMJ_YCKZ_RES_BD yckz_res=new VO_FK_MMJ_YCKZ_RES_BD();
		VO_FK_MMJ_YCKZ_REQ_BD yckz_req=new VO_FK_MMJ_YCKZ_REQ_BD();
		yckz_req.setAQMKXLH("aQMKXLH");
		yckz_req.setFSYZ("FSYZ");
		yckz_req.setKZMLSJ("981D8EE88ACFA013");
		yckz_req.setMMJSJS("MMMMMMMMMMMMM");
		yckz_req.setMYZT(1L);
		
		jsonObject = JSONObject.fromObject(yckz_req);
		
		startTime=System.currentTimeMillis();
		for (int i=0;i<num ;i++) {
			yckz_res=MMJFkUtil.getMMJ_YCKZ(yckz_req, v_task);
		}
		System.out.println("秒："+( System.currentTimeMillis() - startTime )/1000);
		
		System.out.println(jlycsfrz_res);
	}
}
