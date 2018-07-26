package com.longshine.cams.service;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import org.apache.cxf.helpers.IOUtils;
import org.apache.cxf.io.CachedOutputStream;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.log4j.Logger;
import org.apache.log4j.LogManager;

public class OutputIntercepter extends AbstractPhaseInterceptor<Message>{
	private static final Logger log = LogManager.getLogger("log4j.properties");
	
    public OutputIntercepter() {
        super(Phase.PRE_STREAM);
        //addBefore(SoapPreProtocolOutInterceptor.class.getName());
    }
    public void handleMessage(Message message) {
        //TODO
    	String xml = null;
    	try{
    		log.debug("HTTP_REQUEST_METHOD:" + message.get(Message.HTTP_REQUEST_METHOD));
    		log.debug("RESPONSE_CODE:" + message.get(Message.RESPONSE_CODE));
    		if(message.get(Message.HTTP_REQUEST_METHOD) == null){	// 如果是应答则拦截，客户端请求则不处理
    			OutputStream os = message.getContent(OutputStream.class);
    			CachedStream cos = new CachedStream();
//				WSS4JOutInterceptor wssOut = new WSS4JOutInterceptor(outProps);
				message.setContent(OutputStream.class, cos);
				message.getInterceptorChain().doIntercept(message);
				CachedOutputStream csnew = (CachedOutputStream)message.getContent(OutputStream.class);
				InputStream in = csnew.getInputStream();
				xml = convertStreamToString(in);
				//这里对xml做处理，处理完后同理，写回流中
				IOUtils.copy(new ByteArrayInputStream(xml.getBytes("utf-8")), os);
				cos.close();
				os.flush();
				message.setContent(OutputStream.class, os);
				if(log != null)
					log.debug("respose or call:" + xml);
				else
					System.out.println("respose or call:" + xml);
    		}
    	} catch (Exception e) {
    		if(log != null)
    			log.error("Error when split original inputStream. CausedBy : " + "\n" + e);
    		else
    			System.out.println("Error when split original inputStream. CausedBy : " + "\n" + e);
        }
    }
	class CachedStream extends CachedOutputStream {
		public CachedStream() {
			super();
		}
		protected void doFlush() throws IOException {
			currentStream.flush();
		}
		protected void doClose() throws IOException {
		}
		protected void onWrite() throws IOException {
		}
	}
    public String convertStreamToString(InputStream is) {
    	BufferedReader reader;
    	StringBuilder sb = new StringBuilder();
    	String line = null;
    	boolean bfirst = true;
    	try {
    		reader = new BufferedReader(new InputStreamReader(is,"utf-8"));
    		while ((line = reader.readLine()) != null) {
    			if(!bfirst){ sb.append("\n"); bfirst = false;}
    			sb.append(line);
    		}
    	} catch (IOException e) {
    		log.warn("[OutputIntercepter]convertStreamToString Exception:" + e);
    	} finally {
    		try {
    			is.close();
    		} catch (IOException e1) {
        		log.warn("[OutputIntercepter]convertStreamToString InputStream.close() IOException:" + e1);
    		}
    	}
    	return sb.toString();
	}
}
