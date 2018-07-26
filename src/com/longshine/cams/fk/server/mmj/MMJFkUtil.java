package com.longshine.cams.fk.server.mmj;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.longshine.cams.fk.server.FK_JLMMJ.VO_FK_MMJ_JLYCSFRZ_REQ_BD;
import com.longshine.cams.fk.server.FK_JLMMJ.VO_FK_MMJ_JLYCSFRZ_RES_BD;
import com.longshine.cams.fk.server.FK_JLMMJ.VO_FK_MMJ_YCKZ_REQ_BD;
import com.longshine.cams.fk.server.FK_JLMMJ.VO_FK_MMJ_YCKZ_RES_BD;
import com.longshine.cams.fk.structs.FKTask;

public class MMJFkUtil {
	private static Log logger = LogFactory.getLog(MMJFkUtil.class);
	//请求WebService输入参数的文件目录 MMJConstant.uploadPath

	//请求计量远程身份认证--生成请求WebServicel输入参数JSON文件
	public static VO_FK_MMJ_JLYCSFRZ_RES_BD getMMJ_JLYCSFRZ(
		VO_FK_MMJ_JLYCSFRZ_REQ_BD jlycsfrz_req, FKTask v_task) {
		
		VO_FK_MMJ_JLYCSFRZ_RES_BD vo=null;
		TxJkJlycsfrzReq mmjReq=new TxJkJlycsfrzReq();
		mmjReq.setFSYZ(jlycsfrz_req.getFSYZ());
		mmjReq.setMYZT(jlycsfrz_req.getMYZT());
		mmjReq.setTaskId(MMJConstant.jlycsfrzpre+v_task.TASKID);
		
		//Web请求参数-->JSON文件-->目录 
		JSONObject jsonObject = JSONObject.fromObject(mmjReq);
		String mmjKey=MMJConstant.jlycsfrzpre+v_task.TASKID;
		String fileName=MMJConstant.jlycsfrzpre+v_task.TASKID+"_"+System.currentTimeMillis()+".json";
		////////////////////////////////////////////////////
//		TxJkJlycsfrzRes mmjRes=new TxJkJlycsfrzRes();
//		mmjRes.setJSZT(1L);
//		mmjRes.setMMJSJS("AAAAA");
//		mmjRes.setSJSMW("JSMW");
//		mmjRes.setYCXX("YCXX");
//		mmjRes.setTaskId(MMJConstant.jlycsfrzpre+v_task.TASKID);
//		JSONObject jsonObject = JSONObject.fromObject(mmjRes);
		/////////////////////////////////////////////////////////////
		logger.debug("RET mmjKey:"+mmjKey+" fileName:"+fileName);
		boolean ret=createUploadFile(jsonObject.toString(),MMJConstant.uploadPath,fileName,  v_task.TASKID);
		if(ret==false){
				return null;
		}
		
		//定时任务-->文件-->入队列		
		
		//循环查询队列 -->（超时/异常-->退出）-->取返回参数-->退出
		boolean isExit=true;
		long timeout=System.currentTimeMillis() + Long.parseLong(MMJConstant.timeout);
		String msg=null;
		while(isExit){	// 等待超时 
			msg=MMJStorageUtil.getMsg(mmjKey);
			if(msg!=null && !"".equals(msg)){
				vo=getJlsfrzResVoByJson(msg);
//				logger.info("getMMJ_JLYCSFRZ-->MMJStorageUtil.getSize:"+MMJStorageUtil.getSize());
//				logger.info("JLYCSFRZ匹配请求成功:"+mmjKey +":"+vo.toString()+" MMJStorageUtil.getSize:"+MMJStorageUtil.getSize());
				isExit=false;
			}
			if(System.currentTimeMillis() > timeout){
				logger.info("JLYCSFRZ匹配请求超时:"+mmjKey+" | :"+String.valueOf(System.currentTimeMillis() - timeout));
				isExit=false;
			}
		}
		logger.info("getMMJ_JLYCSFRZ ("+((System.currentTimeMillis() - timeout)>0?"==OVERTIME==":"==OK==")+"):"+String.valueOf(System.currentTimeMillis() - timeout)+" | mmjKey:"+mmjKey+" |MMJStorageUtil.getSize:"+MMJStorageUtil.getSize()+" | vo:"+vo.toString());
//		logger.info("JLYCSFRZ匹配请求成功:"+mmjKey +":"+vo.toString()+" MMJStorageUtil.getSize:"+MMJStorageUtil.getSize());
		
		return vo;
	}
	
	//转换返回参数对象
	public static VO_FK_MMJ_JLYCSFRZ_RES_BD getJlsfrzResVoByJson(String msg){
		VO_FK_MMJ_JLYCSFRZ_RES_BD bd=new VO_FK_MMJ_JLYCSFRZ_RES_BD();
		JSONObject jsonObject = JSONObject.fromObject(msg);
		bd.setJSZT(jsonObject.getLong("JSZT"));
		bd.setMMJSJS(jsonObject.getString("MMJSJS"));
		bd.setSJSMW(jsonObject.getString("SJSMW"));
		bd.setYCXX(jsonObject.getString("YCXX"));
		return bd;
	}
	
	//远程控制--生成请求WebServicel输入参数JSON文件
	public static VO_FK_MMJ_YCKZ_RES_BD getMMJ_YCKZ(VO_FK_MMJ_YCKZ_REQ_BD yckz_req, FKTask v_task){
		
		VO_FK_MMJ_YCKZ_RES_BD vo=null;
		TxJkYckzReq mmjReq=new TxJkYckzReq();
		mmjReq.setFSYZ(yckz_req.getFSYZ());
		mmjReq.setMYZT(yckz_req.getMYZT());
		mmjReq.setAQMKXLH(yckz_req.getAQMKXLH());
		mmjReq.setKZMLSJ(yckz_req.getKZMLSJ());
		mmjReq.setMMJSJS(yckz_req.getMMJSJS());
		mmjReq.setTaskId(MMJConstant.yckzpre+v_task.TASKID);
		JSONObject jsonObject = JSONObject.fromObject(mmjReq);
		
		//Web请求参数-->JSON文件-->目录 
//		JSONObject jsonObject = JSONObject.fromObject(mmjReq);
		String mmjKey=MMJConstant.yckzpre+v_task.TASKID;
		String fileName=MMJConstant.yckzpre+v_task.TASKID+"_"+System.currentTimeMillis()+".json";
		
		////////////////////////////////////////////
//		TxJkYckzRes mmjRes=new TxJkYckzRes();
//		mmjRes.setJSZT(1L);
//		mmjRes.setKZMLMW("AAAAA");
//		mmjRes.setYCXX("YCXX");
//		mmjRes.setTaskId("YCKZ_"+v_task.TASKID);		
//		JSONObject jsonObject = JSONObject.fromObject(mmjRes);
		///////////////////////////////////////////////////
		logger.debug("RET mmjKey:"+mmjKey+" fileName:"+fileName);
		boolean ret=createUploadFile(jsonObject.toString(),MMJConstant.uploadPath,fileName,  v_task.TASKID);
		if(ret==false){
				return null;
		}
		
		//定时任务-->文件-->入队列		
		
		//循环查询队列 -->（超时/异常-->退出）-->取返回参数-->退出
		boolean isExit=true;
		long timeout=System.currentTimeMillis() + Long.parseLong(MMJConstant.timeout);
		String msg=null;
		while(isExit){	// 等待超时 
			msg=MMJStorageUtil.getMsg(mmjKey);
			
			if(msg!=null && !"".equals(msg)){
					vo=getYckzResVoByJson(msg);
//				logger.info("getMMJ_YCKZ-->MMJStorageUtil.getSize:"+MMJStorageUtil.getSize());
//				logger.info("YCKZ匹配请求成功:"+mmjKey +":"+vo.toString()+" MMJStorageUtil.getSize:"+MMJStorageUtil.getSize());
				isExit=false;
			}
			if(System.currentTimeMillis() > timeout){
//				logger.info("YCKZ匹配请求超时:"+mmjKey +" | "+String.valueOf(System.currentTimeMillis() - timeout));
				isExit=false;
			}
		}
//		logger.info("getMMJ_YCKZ (>0超时):"+String.valueOf(System.currentTimeMillis() - timeout)+" | mmjKey:"+mmjKey+" |MMJStorageUtil.getSize:"+MMJStorageUtil.getSize());
		logger.info("getMMJ_YCKZ ("+((System.currentTimeMillis() - timeout)>0?"==OVERTIME==":"==OK==")+"):"+String.valueOf(System.currentTimeMillis() - timeout)+" | mmjKey:"+mmjKey+" |MMJStorageUtil.getSize:"+MMJStorageUtil.getSize()+" | vo:"+vo.toString());

		return vo;
	}
	
	//转换返回参数对象
	private static VO_FK_MMJ_YCKZ_RES_BD getYckzResVoByJson(String msg) {
		VO_FK_MMJ_YCKZ_RES_BD bd=new VO_FK_MMJ_YCKZ_RES_BD();
		JSONObject jsonObject = JSONObject.fromObject(msg);
		bd.setJSZT(jsonObject.getLong("JSZT"));
		bd.setKZMLMW(jsonObject.getString("KZMLMW"));
		bd.setYCXX(jsonObject.getString("YCXX"));
		return bd;
	}

	//生成上传文件 
		public static boolean createUploadFile(String rows, String uploadPath, String fileName,String key) {
			 // 标记文件生成是否成功
	        boolean flag = true;
	        
	        if(key==null || "".equals(key)){
	        	logger.error("与密码机通讯的生成文件Key为空。");
	        	return false;
	        }
	        // 含文件名的全路径
	        String fullPathFileName =null;
	        try {
	        	//获取文件夹路径
	    	     File tmpFile = new File(uploadPath);	    	     
	    	   //判断文件夹是否创建，没有创建则创建新文件夹
	    	     if(!tmpFile.exists()){
	    	            tmpFile.mkdirs();
	    	      }
	    	     fullPathFileName= uploadPath + File.separator + fileName ;

	            File file = new File(fullPathFileName);
	         // 如果已存在,删除旧文件
	            if (file.exists()) { 
	                file.delete();
	                logger.debug("与密码机通讯删除已存在的请求文件。"+fullPathFileName);
	            }
	            file = new File(fullPathFileName);
	            file.createNewFile();
			
	            // 遍历输出每行
	            //设置输出文件的编码为utf-8
	            PrintWriter pfp = new PrintWriter(file, "UTF-8"); 
	            pfp.print(rows+ "\n");	            
	            pfp.close();

	        } catch (Exception e) {
	        	logger.error("与密码机通讯生成请求文件异常错误："+fullPathFileName +" "+e.getMessage()+" ERR:"+getExceptionInfo(e));
	            flag = false;
	            //e.printStackTrace();
	        }
	        return flag;
		}
		
		//输出异常
		public static String getExceptionInfo(Exception e) { 
			try { 
				StringWriter sw = new StringWriter(); 
				PrintWriter printWriter = new PrintWriter(sw);
				e.printStackTrace(printWriter); 
				System.out.println(sw.toString());
				String a=sw.toString();
				printWriter.close();
				sw.close();
				return  a; 
			} catch (Exception e2) { 
				return "bad getErrorInfoFromException"; 
			} 
		} 
		
		/**
		 * 判断某个文件是否存在
		 * @param FileName 文件名称,包含路径
		 * @return 是否存在的布尔型变量
		 */
		public static  boolean isFileExists(String FileName) {
			File file = new File(FileName.trim());
			return file.exists();
		}

		public static  boolean isFileExists(File file)
		{
			return file.exists();
		}
		    
		    
}
