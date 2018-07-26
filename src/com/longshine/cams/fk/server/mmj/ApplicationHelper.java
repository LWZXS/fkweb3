package com.longshine.cams.fk.server.mmj;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class ApplicationHelper implements ApplicationContextAware{
	private static ApplicationContext applicationContext;
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		if(null == applicationContext.getParent()) {//父容器的parent为null
			this.applicationContext = applicationContext;
		}
	}
	
	public static ApplicationContext getApplicationContext() {
		return ApplicationHelper.applicationContext;
	}

}
