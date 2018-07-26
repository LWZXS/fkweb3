package com.longshine.cams.service;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.log4j.Logger;
import org.apache.log4j.LogManager;

public class InputIntercepter extends AbstractPhaseInterceptor<Message>{
	private static final Logger log = LogManager.getLogger("log4j.properties");
	
    public InputIntercepter() {
        super(Phase.RECEIVE);
        //addBefore(SoapPreProtocolOutInterceptor.class.getName());
    }
    public void handleMessage(Message message) {
        //TODO
    	try{
    		InputStream is = message.getContent(InputStream.class);
    		String xml = convertStreamToString(is);
    		if(xml != null && !"".equals(xml)){
    			is = new ByteArrayInputStream(xml.getBytes("utf-8"));
    			if(is != null)
    				message.setContent(InputStream.class, is);
    			log.info("request:" + xml);
    		}
    	} catch (Exception e) {
            log.error("Error when split original inputStream. CausedBy : "+"\n"+e);
        }
    }
    public String convertStreamToString(InputStream is) {
    	BufferedReader reader = null;
    	StringBuilder sb = null;
    	String line = null;
    	boolean bfirst = true;
    	try {
    		reader = new BufferedReader(new InputStreamReader(is, "utf-8"));
    		sb = new StringBuilder();
    		while ((line = reader.readLine()) != null) {
    			if(!bfirst){ sb.append("\n"); bfirst = false;}
    			sb.append(line);
    		}
    	} catch (IOException e) {
    		e.printStackTrace();
    	} finally {
    		try {
    			is.close();
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    	}
    	return sb.toString();
	}
}
