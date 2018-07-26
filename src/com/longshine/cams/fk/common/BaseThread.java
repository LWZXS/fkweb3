package com.longshine.cams.fk.common;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class BaseThread extends Thread {
	// 系统日志对象
	protected Log logger;
	protected Log getLogger(){return logger;}
	// 线程控制退出方法
	protected boolean bexit = false;
	// 线程名称
	protected String thread_name;
	protected void setThreadName(String v_name){
		this.thread_name = v_name;
	}
	protected String getThreadName(){
		return this.thread_name;
	}
	public static void sleep(long millis){
		try{ Thread.sleep(millis);}
		catch(Exception e){}
	}
	// 为了每隔一段时间检查健康程度，设置以下参数用于检查线程运行的安全程度，线程需要执行健康检查工作时，重载checkup()方法即可。
	private final long mgr_check_timeout = 10000;
	public void setExit(){
		bexit = true;
	}
	// 线程空闲等待时长
	protected long idle_sleep;
	// 线程初始化方法
	public BaseThread(long v_idle_sleep){
		idle_sleep = v_idle_sleep < 10 ? 10 : v_idle_sleep;
		idle_sleep = idle_sleep > 1000 ? 1000 : idle_sleep;
		this.logger = LogFactory.getLog(this.getClass());
	}
	public void run(){
		this.logger.info(this.thread_name + " is running.....");
		boolean bckeckup;
		boolean bdowork;
		long mgr_check_point = System.currentTimeMillis();
		while(true){
			bdowork = false;
			if(bexit)
				break;
			try{
				// do something
				if(checkup_enabled){
//					bckeckup = false;	// bcheckup只用于控制是否需要继续执行下次检查，因此无需每次都设置该值，如果有任务需要继续检查，也需要等待主循环的sleep后继续执行
					if(System.currentTimeMillis() - mgr_check_point > mgr_check_timeout){
						bckeckup = checkup();	// 没有任务需要继续检查返回false，否则返回true
						if(!bckeckup)
							mgr_check_point = System.currentTimeMillis();
					}
				}
				bdowork = dowork();
				if(!bdowork){
					BaseThread.sleep(idle_sleep);
				}
			}catch(Exception e){
				this.logger.warn(thread_name + " Exception:" + e);
			}
		}
		this.logger.info(this.thread_name + " begin exit.....");
	}
	// 健康检查方法控制调度
	protected boolean checkup_enabled = false;
	protected void enableCheckup(){ checkup_enabled = true; }
	// 线程对象健康检查方法
	protected boolean checkup(){ return false; }
	// 线程对象的工作任务
	protected abstract boolean dowork();
}
