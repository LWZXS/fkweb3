package com.longshine.cams.fk.server.mmj;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MMJDataImpQutz {
	private static Log logger = LogFactory.getLog(MMJDataImpQutz.class);
	private MMJConsumer mmjConsumer;
	
	public MMJConsumer getMmjConsumer() {
		return mmjConsumer;
	}

	public void setMmjConsumer(MMJConsumer mmjConsumer) {
		this.mmjConsumer = mmjConsumer;
	}

	public MMJDataImpQutz() {
	}
	
	public void dataImp_start(){
		//刷新任务的时间
//		this.logger.info("定时调用读取MMJ接口返回调用结果数据文件 ");
		//+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())
		ExecutorService service = Executors.newCachedThreadPool();
		service.submit(mmjConsumer);
		
	}
	
}
