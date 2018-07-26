package com.longshine.cams.fk.interfaces.FK_JLZDH_DYFKYHYCKZ;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.longshine.cams.fk.interfaces.common.VO_FK_COMMON_RESP;
import com.longshine.cams.fk.server.mmj.MMJThread;
import com.longshine.cams.fk.server.mmj.SpringContextUtil;


public class Impl_FK_JLZDH_HQDYFKYHYCKZZXJG implements
		I_FK_JLZDH_HQDYFKYHYCKZZXJG_Schema {
	private int num=2;
	private ApplicationContext appContext;
	
	 @Autowired
	private HttpServletRequest request;
	 
	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public ApplicationContext getAppContext() {
		return appContext;
	}

	public void setAppContext(ApplicationContext appContext) {
		this.appContext = appContext;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	public DataSource getDatasource() {
		return datasource;
	}

	public void setDatasource(DataSource datasource) {
		this.datasource = datasource;
	}

		// 数据源定义
		private DataSource datasource;

	@Override
	public VO_FK_JLZDH_HQDYFKYHYCKZZXJG_RES I_FK_JLZDH_HQDYFKYHYCKZZXJG(
			VO_FK_JLZDH_HQDYFKYHYCKZZXJG_REQ req) {
		VO_FK_JLZDH_HQDYFKYHYCKZZXJG_RES vo=new VO_FK_JLZDH_HQDYFKYHYCKZZXJG_RES();
		System.out.println(request);
		System.out.println(appContext);
//		HttpServletRequest re = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
		
		this.appContext =SpringContextUtil.getApplicationContext();
		SpringContextUtil.getServletContext();
		//SpringContextUtil.getRequest();
		//SpringContextUtil.getBean("datasource_gdcams2");
		ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
//		HttpServletRequest request = attr.getRequest();
		
//		this.appContext = WebApplicationContextUtils.getWebApplicationContext(request.getServletContext());
//		this.datasource = this.appContext.getBean("datasource_gdcams2",DataSource.class);
//		try {
//			System.out.println(this.datasource.getConnection().getMetaData());
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		VO_FK_COMMON_RESP resp = new VO_FK_COMMON_RESP();
		resp.setJSZT(0L);
		resp.setYCXX("处理成功");		
		vo.setReplyCode("OK");
		vo.setResp(resp);
		
		
		return vo;
	}

	public void c(){
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
	}
	
	protected int queryUpdateSQL(String v_sql) throws SQLException{
		Connection conn = null;
		Statement st = null;
		int i = 0;;
		try{
			conn = this.datasource.getConnection();
			if(conn != null){
				st = conn.createStatement();
				i = st.executeUpdate(v_sql);
			}
		}catch(SQLException e1){
			throw e1;
		}
		catch(Exception e){
			System.out.println("Select Exception(" + v_sql + "),Exception:" + e);
		}finally{
			this.closeConnectionResource(st,conn);
			st = null;
			conn = null;
		}
		return i;
	}
	
	protected void closeConnectionResource(Object ...v_resources){
		for(int i = 0; i < v_resources.length; i++){
			try{
				if(v_resources[i] != null){
					Class<?> res_class = v_resources[i].getClass();
					Method method = res_class.getMethod("close");
					if(method != null)
						method.invoke(v_resources[i]);
				}
			}catch(Exception e){
				System.out.println("closeConnectionResource function failed. Exception:" + e);
			}
		}
	}
	
}
