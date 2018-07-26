package com.longshine.cams.fk.server.mmj;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.longshine.cams.fk.server.FK_JLMMJ.VO_FK_MMJ_JLYCSFRZ_REQ_BD;
import com.longshine.cams.fk.server.FK_JLMMJ.VO_FK_MMJ_JLYCSFRZ_RES_BD;
import com.longshine.cams.fk.server.FK_JLMMJ.VO_FK_MMJ_YCKZ_REQ_BD;
import com.longshine.cams.fk.server.FK_JLMMJ.VO_FK_MMJ_YCKZ_RES_BD;
import com.longshine.cams.fk.structs.FKTask;

public class MMJThread extends Thread {
	private static Log logger = LogFactory.getLog(MMJThread.class);
   private int num=1;
   private int type=0;

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public int getType() {
		return type;
	}
	
	public void setType(int type) {
		this.type = type;
	}

	public MMJThread(){
		
	}
	public MMJThread(int type) {
		super();
		this.type = type;
	}

	@Override
	public void run() {
		if(type==0){
			testJlycsfrz();
		}else{
			testyckz();
		}
	}
	public void testJlycsfrz(){
		long z=IncrementNumber.getInstance().getIncNumLength8();
		for(int i=0;i<num;i++){
		///////////////////////////////////////////////////
			VO_FK_MMJ_JLYCSFRZ_RES_BD jlycsfrz_res=new VO_FK_MMJ_JLYCSFRZ_RES_BD();
			VO_FK_MMJ_JLYCSFRZ_REQ_BD jlycsfrz_req=new VO_FK_MMJ_JLYCSFRZ_REQ_BD();
			jlycsfrz_req.setMYZT(z);
			FKTask v_task=new FKTask();
			v_task.TASKID="fk"+String.valueOf(z)+"-1-jl-"+System.currentTimeMillis();
//			v_task.TASKID="fk"+IncrementNumber.getInstance().getIncNumLength8()+"-1-jl-"+System.currentTimeMillis();
			jlycsfrz_req.setFSYZ(v_task.TASKID);
			
			logger.info("====task====:"+v_task.TASKID+ " | type:" + type +" | "+Thread.currentThread().getName());
			jlycsfrz_res = MMJFkUtil.getMMJ_JLYCSFRZ(jlycsfrz_req, v_task);
			logger.info("====jlycsfrz_res====:"+( (jlycsfrz_res==null)? "==FAILE==":"=="+getTaskPre(v_task.TASKID,String.valueOf(jlycsfrz_res.getYCXX())) +"==")+" | "+((jlycsfrz_res==null)?"##失败##":jlycsfrz_res) +" |taskId :"+v_task.TASKID+ " | type:" + type +" | "+Thread.currentThread().getName());
		}
	}
	
	public void testyckz(){
		long z=IncrementNumber.getInstance().getIncNumLength8();
		for(int i=0;i<num;i++){
			///////////////////////////////////////////////////
			VO_FK_MMJ_YCKZ_RES_BD yckz_res=new VO_FK_MMJ_YCKZ_RES_BD();
			VO_FK_MMJ_YCKZ_REQ_BD yckz_req=new VO_FK_MMJ_YCKZ_REQ_BD();
			yckz_req.setAQMKXLH("a");
			yckz_req.setKZMLSJ("9");
			yckz_req.setMMJSJS("b");
			yckz_req.setMYZT(z);
			FKTask v_task=new FKTask();
			v_task.TASKID="fk"+String.valueOf(z)+"-9-yc-"+System.currentTimeMillis();
//			v_task.TASKID="fk"+IncrementNumber.getInstance().getIncNumLength8()+"-9-yc-"+System.currentTimeMillis();
			yckz_req.setFSYZ(v_task.TASKID);
			
			logger.info("task:"+v_task.TASKID+ " | type:" + type +" | "+Thread.currentThread().getName());
			yckz_res=MMJFkUtil.getMMJ_YCKZ(yckz_req, v_task);
			logger.info("====yckz_res====:"+( (yckz_res==null)? "==FAILE==":"=="+getTaskPre(v_task.TASKID,String.valueOf(yckz_res.getYCXX())) +"==")+" | "+((yckz_res==null)?"##失败##":yckz_res) +" |taskId :"+v_task.TASKID+ " | type:" + type +" | "+Thread.currentThread().getName());
		}
	}
	
	private String getTaskPre(String v_task,String jszt) {
		String ret=v_task;
		int p=ret.indexOf("-");
		 if(jszt.equals(ret.substring(2,p))){
			 return "#T#";
		 }else{
			 return "#F#";
		 }
	}
}
