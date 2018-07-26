package com;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.longshine.cams.fk.interfaces.FK_JLZDH_DYFKYHYCKZ.I_FK_JLZDH_DYFKYHYCKZ_Schema;
import com.longshine.cams.fk.interfaces.FK_JLZDH_DYFKYHYCKZ.VO_FK_JLZDH_DYFKYHYCKZ_REQ;
import com.longshine.cams.fk.interfaces.FK_JLZDH_DYFKYHYCKZ.VO_FK_JLZDH_DYFKYHYCKZ_REQ_YCKZ;
import com.longshine.cams.fk.interfaces.FK_JLZDH_DYFKYHYCKZ.VO_FK_JLZDH_DYFKYHYCKZ_RES;
import com.longshine.cams.fk.server.WebServiceClientUtil;
import com.longshine.cams.fk.server.FK_JLMMJ.VO_FK_MMJ_YCKZ_REQ;
import com.longshine.cams.fk.server.FK_JLMMJ.VO_FK_MMJ_YCKZ_REQ_BD;
import com.longshine.cams.fk.server.mmj.IncrementNumber;
import com.longshine.cams.fk.server.mmj.MMJStorageUtil;
import com.longshine.cams.fk.server.mmj.MMJThread;
import com.longshine.cams.fk.server.mmj.MMJThread2;

public class TestServlet extends HttpServlet {
	private static Log logger = LogFactory.getLog(TestServlet.class);
	private String mmjserver_url="http://127.0.0.1:8082/fkweb3/services/";

	/**
	 * Constructor of the object.
	 */
	public TestServlet() {
		super();
	}

	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}

	/**
	 * The doGet method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		int num=2;
		if(request.getParameter("num")!=null){
			num=Integer.parseInt(request.getParameter("num"));
		}
		String ok=request.getParameter("ok");
		if(ok!=null){
			MMJThread m1=new MMJThread(0);
			MMJThread2 m2=new MMJThread2(2);
			
	//		Thread t1=new Thread(m1);
	//		Thread t2=new Thread(m2);
	//		t1.start();
	//		t2.start();
			
			ExecutorService service = Executors.newCachedThreadPool();
			for(int i=0;i<num;i++){
				service.submit(m1);
				service.submit(m2);
			}
		
		}
		
		b();
		
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
		out.println("<HTML>");
		out.println("  <HEAD><TITLE>A Servlet</TITLE></HEAD>");
		out.println("  <BODY>");
		out.print("    This is ");
		out.print(this.getClass());
		out.println(", using the GET method");
		
		out.println("<BR/>SIZE:"+MMJStorageUtil.getSize());
		for(Map.Entry<String, String> entry : MMJStorageUtil.mmjMap.entrySet()){
		       out.println("<BR/>key= "+entry.getKey()+" and value= "+entry.getValue());
		   }
		out.println("  </BODY>");
		out.println("</HTML>");
		out.flush();
		out.close();
	}

	/**
	 * The doPost method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to post.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		int num=2;
		if(request.getParameter("num")!=null){
			num=Integer.parseInt(request.getParameter("num"));
		}
		MMJThread m1=new MMJThread(0);
		MMJThread m2=new MMJThread(2);
		
//		Thread t1=new Thread(m1);
//		Thread t2=new Thread(m2);
//		t1.start();
//		t2.start();
		
		ExecutorService service = Executors.newCachedThreadPool();
		for(int i=0;i<num;i++){
			service.submit(m1);
			service.submit(m2);
		}
		
		
		
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
		out.println("<HTML>");
		out.println("  <HEAD><TITLE>A Servlet</TITLE></HEAD>");
		out.println("  <BODY>");
		out.print("    This is ");
		out.print(this.getClass());
		out.println(", using the POST method");
		out.println("  </BODY>");
		out.println("</HTML>");
		out.flush();
		out.close();
	}

	/**
	 * Initialization of the servlet. <br>
	 *
	 * @throws ServletException if an error occurs
	 */
	public void init() throws ServletException {
		// Put your code here
	}
	
	public void a(){
		String msg = "{\"bd\":{\"AQMKXLH\":\"aQMKXLH\",\"FSYZ\":\"fSYZ\",\"KZMLSJ\":\"kZMLSJ\",\"MMJSJS\":\"mMJSJS\",\"MYZT\":1}}";
		JSONObject jsonObject = JSONObject.fromObject(msg);
		VO_FK_MMJ_YCKZ_REQ v=(VO_FK_MMJ_YCKZ_REQ) JSONObject.toBean(jsonObject, VO_FK_MMJ_YCKZ_REQ.class);
		System.out.println(v.getBd().getFSYZ());
		
		VO_FK_MMJ_YCKZ_REQ obj = new VO_FK_MMJ_YCKZ_REQ();
		VO_FK_MMJ_YCKZ_REQ_BD bd=new VO_FK_MMJ_YCKZ_REQ_BD();
		bd.setAQMKXLH("aQMKXLH");
		bd.setFSYZ("fSYZ");
		bd.setKZMLSJ("kZMLSJ");
		bd.setMMJSJS("mMJSJS");
		bd.setMYZT(1L);
		obj.setBd(bd);
		//1、使用JSONObject
        JSONObject json = JSONObject.fromObject(obj);
        //2、使用JSONArray
        JSONArray array=JSONArray.fromObject(obj);
        
        String strJson=json.toString();
        String strArray=array.toString();
        
        System.out.println("strJson:"+strJson);
        System.out.println("strArray:"+strArray);
        
		String a=String.valueOf(System.currentTimeMillis())+"."+IncrementNumber.getInstance().getIncrementNumber();
		
	}
	
	private void b(){
		///////////////////////////////////////
		VO_FK_JLZDH_DYFKYHYCKZ_REQ_YCKZ yckz=new VO_FK_JLZDH_DYFKYHYCKZ_REQ_YCKZ();
		String dbzcbh="123";
		String jldbh="222";
		String jylsh="111";
		String kzlx="1A";
		String yhbh="2222";
		yckz.setDBZCBH(dbzcbh);
		yckz.setGDDWBM("0301");
		yckz.setJLDBH(jldbh);
		yckz.setJYLSH(jylsh);
		yckz.setKZLX(kzlx);
		yckz.setYHBH(yhbh);
		yckz.setZXCS(new BigDecimal(1));
		try {
		int k=callFkServer_DYFKYHYCKZ(yckz,mmjserver_url);
		System.out.println(k);
		} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		}

/////////////////////////
	}
	
	public int callFkServer_DYFKYHYCKZ(VO_FK_JLZDH_DYFKYHYCKZ_REQ_YCKZ yckz,String mmjserver_url ) throws Exception{
		String func_ycsfrz ="I_FK_JLZDH_DYFKYHYCKZ";
		String ret;
		try{
			VO_FK_JLZDH_DYFKYHYCKZ_REQ req = new VO_FK_JLZDH_DYFKYHYCKZ_REQ();
			VO_FK_JLZDH_DYFKYHYCKZ_RES resp = null;
			req.setYCKZ(yckz);
			mmjserver_url = combineServerURL(mmjserver_url, func_ycsfrz);
			resp = (VO_FK_JLZDH_DYFKYHYCKZ_RES)WebServiceClientUtil.callCxfService(
					mmjserver_url,
					func_ycsfrz,
					new Class[]{VO_FK_JLZDH_DYFKYHYCKZ_REQ.class},
					I_FK_JLZDH_DYFKYHYCKZ_Schema.class,
					null,
					null,
					req);
			if(resp != null)
				ret = resp.getReplyCode();
		}catch(Exception e){
			System.out.print("callFkServer (" + mmjserver_url + ") invoke I_FK_JLZDH_DYFKYHYCKZ Exception:" + e);
		}
		
		return 0;
	}
	
	private String combineServerURL(String v_preurl, String v_function){
		String str = v_preurl;
		while(str.endsWith("/"))
			str = str.substring(0, str.length() - 1);
		while(str.endsWith("\\"))
			str = str.substring(0, str.length() - 1);
		return str + "/" + v_function;
	}

}
