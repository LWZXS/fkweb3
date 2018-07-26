package com.longshine.cams.fk.server.mmj;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 消费
 */
public class MMJConsumer implements Runnable {
	
	private final static Log logger = LogFactory.getLog(MMJConsumer.class);
	private MMJServer mmjServer;
	
	public MMJServer getMmjServer() {
		return mmjServer;
	}

	public void setMmjServer(MMJServer mmjServer) {
		this.mmjServer = mmjServer;
	}

	public MMJConsumer() {
	}

	@Override
	public void run() {
		if(mmjServer==null) {
			logger.error("MMJ服务末注入。");
			return;
		}

		try {
			Thread.sleep(100L);
			mmjServer.consumer();
		} catch (Exception e) {
			//e.printStackTrace();
			logger.error("MMJconsumer异常 - "+MMJFkUtil.getExceptionInfo(e));
		}
	}

}
