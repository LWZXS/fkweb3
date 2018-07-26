package com.longshine.cams.service;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import org.apache.cxf.binding.soap.interceptor.SoapPreProtocolOutInterceptor;
import org.apache.cxf.helpers.IOUtils;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.io.CachedOutputStream;
import org.apache.cxf.io.CachedOutputStreamCallback;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.log4j.Logger;
import org.apache.log4j.LogManager;

public class OutputIntercepterEx extends AbstractPhaseInterceptor<Message>{
	private static final Logger log = LogManager.getLogger("log4j.properties");
	
    public OutputIntercepterEx() {
        super(Phase.PRE_STREAM);
        addBefore(SoapPreProtocolOutInterceptor.class.getName());
    }
    public void handleMessage(Message message) {
        //TODO

        boolean isOutbound = false;
        isOutbound = message == message.getExchange().getOutMessage()
               || message == message.getExchange().getOutFaultMessage();

        if (isOutbound) {
            OutputStream os = message.getContent(OutputStream.class);
            CachedStream cs = new CachedStream();
            message.setContent(OutputStream.class, cs);
            
            message.getInterceptorChain().doIntercept(message);

            try {
                cs.flush();
                CachedOutputStream csnew = (CachedOutputStream) message
                    .getContent(OutputStream.class);
//                OutputStream output = new ByteArrayOutputStream();
//                CachedOutputStream.copyStream(csnew.getInputStream(), output, 1024);
                CachedOutputStream.copyStream(csnew.getInputStream(), os, 1024);
//				InputStream in = csnew.getInputStream();
//				String xml = convertStreamToString(in);
//				IOUtils.copy(new ByteArrayInputStream(xml.getBytes("utf-8")), os);
                
//                GZIPOutputStream zipOutput = new GZIPOutputStream(os);
//                CachedOutputStream.copyStream(csnew.getInputStream(), zipOutput, 1024);

                cs.close();
//                in.close();
//                zipOutput.close();
                os.flush();
//                output.flush();

                message.setContent(OutputStream.class, os);
                
//                System.out.println("OutputStream:" + xml);
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        } else {
            try {
                InputStream is = message.getContent(InputStream.class);
                String xml = convertStreamToString(is);
                InputStream newis = new ByteArrayInputStream(xml.getBytes("utf-8"));
//                GZIPInputStream zipInput = new GZIPInputStream(is);
//                String xml = convertStreamToString(is);
                message.setContent(InputStream.class, newis);
                System.out.println("InputStream:" + xml);
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

    public void handleFault(Message message) {
    }
    public void handleMessage(Message message,long noused) throws Fault {
        System.out.println("############handleMessage##########");
        System.out.println(message);
        if (message.getDestination() != null) {
            System.out.println(message.getId() + "#" + message.getDestination().getMessageObserver());
        }
        if (message.getExchange() != null) {
            System.out.println(message.getExchange().getInMessage() + "#" + message.getExchange().getInFaultMessage());
            System.out.println(message.getExchange().getOutMessage() + "#" + message.getExchange().getOutFaultMessage());
        }
    }
    public void handleMessage(Message message, String v_noused) {
        //TODO
    	try{
			System.out.println("ADDRESS:" + message.get(Message.ENDPOINT_ADDRESS));
			System.out.println("URI:" + message.get(Message.REQUEST_URI));
//			if(message.get(Message.HTTP_REQUEST_METHOD) == null){
				OutputStream os = message.getContent(OutputStream.class);
				CachedStream cos = new CachedStream();
				message.setContent(OutputStream.class, cos);
				message.getInterceptorChain().doIntercept(message);
				CachedOutputStream csnew = (CachedOutputStream)message.getContent(OutputStream.class);
				InputStream in = csnew.getInputStream();
				String xml = convertStreamToString(in);
				
				message.setContent(OutputStream.class, os);
				//这里对xml做处理，处理完后同理，写回流中
				IOUtils.copy(new ByteArrayInputStream(xml.getBytes("utf-8")), os);
				cos.close();
				os.flush();
				message.getInterceptorChain().doIntercept(message);
//				cos.registerCallback(new MessageCallback(message, os));
				
//				CacheAndWriteOutputStream cs = new CacheAndWriteOutputStream(os);
//				OutputStreamWriter outw = new OutputStreamWriter(os);
//				ByteArrayOutputStream baos=new ByteArrayOutputStream();
//				os.write(baos.toByteArray());
//				String xml = baos.toString();
				System.out.println("XML:" + xml);
//	            String xml = convertStreamToString(in);
//				message.setContent(OutputStream.class, os);
//				message.setContent(OutputStream.class, cs);
//				cs.registerCallback(new MessageCallback(message, os));
//			}
//			message.getInterceptorChain().doIntercept(message);
    	} catch (Exception e) {
//    		if(log != null)
//    			log.error("Error when split original inputStream. CausedBy : " + "\n" + e);
//    		else
    			System.out.println("Error when split original inputStream. CausedBy : " + "\n" + e);
        }
    }
	private class CachedStream extends CachedOutputStream {
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
	class MessageCallback implements CachedOutputStreamCallback{
        private final Message message;
        private final OutputStream origStream;
        MessageCallback(final Message msg, final OutputStream os) {
            this.message = msg;
            this.origStream = os;
        }
        public void onFlush(CachedOutputStream cos) {  
        }
        public void onClose(CachedOutputStream cos) {
        	try{
				CachedOutputStream csnew = (CachedOutputStream)this.message.getContent(OutputStream.class);
				InputStream in = csnew.getInputStream();
				String xml = convertStreamToString(in);
				//这里对xml做处理，处理完后同理，写回流中
				IOUtils.copy(new ByteArrayInputStream(xml.getBytes("utf-8")), origStream);
//				csnew.close();
				in.close();
				origStream.flush();
				System.out.println("request:" + message.get(Message.HTTP_REQUEST_METHOD));
				System.out.println("response:" + message.get(Message.RESPONSE_CODE));
				System.out.println("====================111===========");
//				if(log != null)
//					log.debug("respose or call:" + xml);
//				else
					System.out.println("respose or call:" + xml);
				message.setContent(OutputStream.class, this.origStream);
				message.getInterceptorChain().doIntercept(message);
				System.out.println("====================222===========");
        	}catch(Exception e){
        		e.printStackTrace();
        	}
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
