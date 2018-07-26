package com.longshine.cams.fk.server;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.MalformedInputException;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.longshine.cams.fk.common.BaseThread;

public class TaskServerSocket {
	private String cmd_id;
	public String getCmd_id() {	return cmd_id;}
	private SocketChannel socketchannel = null;
	private InetSocketAddress remote = null;
	private static Log logger = LogFactory.getLog(TaskServerSocket.class);
	private Long conn_timeout;
	private Long send_timeout;
	private FKConfiguration config;
	private static Long socket_idle_sleep = 10L;
	private static String endstring_task = "</tasks>";
	private boolean b_sendtail = false;
	private boolean can_recvdata = false;
	private ByteBuffer recv_buffer;
	private CharsetDecoder decoder;
	private StringBuffer recv_data;
	private boolean has_complete_resp = false;
	private Date connect_time, release_time;
	public TaskServerSocket(String v_cmd_id){
		this.cmd_id = v_cmd_id;
		this.recv_data = new StringBuffer();
		this.config = FKConfiguration.getInstance();
		this.conn_timeout = this.config.getPropertyLong(FKConfigureKeys.CAMS_FK_TASK_TASKSERVER_CONNECT_TIMEOUT_KEY);
		this.send_timeout = this.config.getPropertyLong(FKConfigureKeys.CAMS_FK_TASK_TASKSERVER_SEND_TIMEOUT_KEY);
		this.recv_buffer = ByteBuffer.allocate((int)this.config.getPropertyLong(FKConfigureKeys.CAMS_FK_TASK_TASKSERVER_RECVBUFF_KEY));
		Charset charset = Charset.forName(this.config.getProperty(FKConfigureKeys.CAMS_FK_TASK_TASKSERVER_ENCODING_KEY));
		this.decoder = charset.newDecoder();
		if("1".equals(this.config.getProperty(FKConfigureKeys.CAMS_FK_TASK_TASKSERVER_SENDTAIL_KEY)))
			this.b_sendtail = true;
//		socket_idle_sleep = 10L;
	}
	public int connectServer(String v_ip, int v_port){
		try{
			return this.connectServer(new InetSocketAddress(v_ip, v_port), false);
		}catch(Exception e){
			if(logger.isWarnEnabled())
				logger.warn("Connect Server Failed(" + v_ip + ":" + v_port + "):" + e);
			return -3;
		}
	}
	public int connectServer(InetSocketAddress v_addr){
		return this.connectServer(v_addr, false);
	}
	/**根据提供的地址连接远程服务器
	 * @param v_addr 连接服务器的地址，是一个InetSocketAddress对象结构
	 * @param blocked 配置TCP连接的阻塞类型，true阻塞，false非阻塞，缺省非阻塞
	 * @return 0 连接成功
	 *         -1 连接超时
	 *         -2 连接异常
	 */
	public int connectServer(InetSocketAddress v_addr, boolean blocked){
		int ret = 0;
		this.remote = v_addr;
		boolean bconnected = false;
		try{
			this.socketchannel = SocketChannel.open();
			this.socketchannel.configureBlocking(blocked);
			if(!this.socketchannel.connect(remote)){
				long conn_limit = System.currentTimeMillis() + this.conn_timeout * 1000;
				while(System.currentTimeMillis() < conn_limit){
					if(this.socketchannel.finishConnect()){
						bconnected = true;
						break;
					}
					BaseThread.sleep(socket_idle_sleep);
				}
			}else{
				bconnected = true;
				if(logger.isDebugEnabled())
					logger.debug("connectServer.........");
				this.setCanRecvdata(false);
				this.connect_time = new Date();
			}
			if(!bconnected){	// 超时未连接成功
				if(this.socketchannel != null){
					try{this.socketchannel.close();}
					catch(Exception e1){}
					finally{this.socketchannel = null;}
				}
				ret = -1;
			}
		}catch(Exception e){
			if(logger.isWarnEnabled())
				logger.warn("Connect Server(" + this.remote.getAddress().getHostAddress() + ":" + this.remote.getPort() + ") failed,Exception:" + e);
			ret = -2;
			if(this.socketchannel != null){
				try{this.socketchannel.close();}
				catch(Exception e1){}
				finally{this.socketchannel = null;}
			}
		}
		this.recv_buffer.clear();
		this.recv_data.setLength(0);
		return ret;
	}
	public boolean isHasCompleteResp() {
		return has_complete_resp;
	}
	public boolean isSocketLived(){
		return this.socketchannel == null ? false : true;
	}
	public Date getConnectTime() {
		return connect_time;
	}
	public Date getReleaseTime() {
		return release_time;
	}
	/**发送数据，直到发送完成才返回
	 * @param v_data 待发送的数据
	 * @return 大于0： 发送成功，返回发送成功的字符数
	 *         -1 Socket通道未建立
	 *         -2   其他发送异常
	 *         -3 发送数据超时未发送完全
	 */
	public int sendData(String v_data){
		int ret = 0;
		if(this.socketchannel == null){
			ret = -1;
			return ret;
		}
		try{
			int send_total = 0;
			int send_bytes = 0;
			ByteBuffer writeBuf = null;
			if(this.b_sendtail){	// 需要发送字符串结束符给对端时，需要多发送二进制0字节。
				send_total = v_data.getBytes().length + 1;
				writeBuf = ByteBuffer.allocate(send_total);
				writeBuf.put(v_data.getBytes());
				writeBuf.put((byte)0);
				writeBuf.flip();
			}else{
				send_total = v_data.getBytes().length;
				writeBuf = ByteBuffer.wrap(v_data.getBytes());
			}
			long send_limit = System.currentTimeMillis() + this.send_timeout * 1000;
			while(System.currentTimeMillis() < send_limit){
				send_bytes += this.socketchannel.write(writeBuf);
				if(send_bytes >= send_total){
					break;
				}
				BaseThread.sleep(socket_idle_sleep);
			}
			if(send_bytes < send_total)	// 发送数据超时
				ret = -3;
			else{
				ret = send_bytes;
				if(!this.can_recvdata){
					this.setCanRecvdata(true);
				}
				if(logger.isDebugEnabled())
					logger.debug("send to(" + this.remote.getAddress().getHostAddress() + ":" + this.remote.getPort() + ") " + send_bytes + " bytes of " + send_total + ".");
			}
		}catch(Exception e){
			if(logger.isWarnEnabled())
				logger.warn("Send data to(" + this.remote.getAddress().getHostAddress() + ":" + this.remote.getPort() + ") failed,Exception:" + e);
			ret = -2;
		}
		return ret;
	}
	/**从对端接收数据，该方法可多次调用，每次调用只调用一次接收方法
	 * @return 0 已经接收成功一个XML报文体
	 *         -1 Socket通道未建立
	 *         -2 其他接收异常
	 *         -3 服务端已经关闭
	 *         -4 解析异常，可能是字符集原因导致
	 *         1 接收正常，但没有完整的XML报文体
	 *         2 没有接收到新数据
	 */
	public int recvData(){
		int ret = -2;
		if(this.socketchannel == null){
			ret = -1;
			return ret;
		}
		try{
			long recv_len = 0;
			recv_len = this.socketchannel.read(this.recv_buffer);
			if(recv_len <= -1){	// 服务端关闭
				ret = -3;
			}else if(recv_len == 0){	// 未接收到数据
				ret = 2;
			}else{	// 接收到部分数据，则进行是否有完整XML报文判断
				// 如果返回null，需要进行处理
				CharBuffer charBuffer = this.DecodeBuffer();
				if(charBuffer != null){	// 处理成功
					synchronized(this){	// 未来考虑从recv_data中获取多报文体时，需要支持线程安全
						if(logger.isDebugEnabled())
							logger.debug("=RECV:" + charBuffer.toString());
						this.recv_data.append(charBuffer.toString());
				}
				if(this.recv_data.indexOf(endstring_task) > 0){ // 接收到一个完整的XML任务报文
					this.has_complete_resp = true;
					ret = 0;
				}else{
					this.has_complete_resp = false;
					ret = 1;
				}
				}else{
					if(logger.isWarnEnabled())
						logger.warn("RECV DATA Charset Exception. Please change charset property...");
					ret = -4;
				}
			}
		}catch(Exception e){
			if(logger.isWarnEnabled())
				logger.warn("Recv data from(" + this.remote.getAddress().getHostAddress() + ":" + this.remote.getPort() + ") failed,Exception:" + e);
			ret = -2;
		}
		return ret;
	}
	/* 网络序接收缓冲区字符集处理，本方法处理了网络序半个中文字符接收的问题，需要等待下次的接收继续处理
	 * 字符集处理异常，返回null，否则返回解析后的字符缓冲区
	 * */
	private CharBuffer DecodeBuffer(){
		CharBuffer ret = null;
		byte[] buf_reload = new byte[2];
		try{
			this.recv_buffer.flip();
			ret = decoder.decode(this.recv_buffer);
			this.recv_buffer.clear();
		}catch(MalformedInputException malexp){	// 该死的半个中文问题
			int remain = this.recv_buffer.limit() - this.recv_buffer.position();
			if(logger.isDebugEnabled())
				logger.debug("=============begin deal with half chinese character problem.");
			if(remain == 1 || remain == 2){
				this.recv_buffer.mark();
				this.recv_buffer.get(buf_reload, 0, remain);
				this.recv_buffer.reset();
				this.recv_buffer.flip();
				try{
					ret = decoder.decode(this.recv_buffer);
					if(logger.isDebugEnabled())
						logger.debug("====half chinese exclude decode:" + ret.toString());
				}catch(Exception e2){	// 再出错就要返回错误了。
					if(logger.isWarnEnabled())
						logger.warn("DecodeBuffer Function Failed. please connect author wolf...");
					ret = null;
				}
				this.recv_buffer.clear();
				this.recv_buffer.put(buf_reload, 0, remain);
			}else{	// 可能是字符集错误，暂不处理
				if(logger.isWarnEnabled())
					logger.warn("DecodeBuffer Function Failed. maybe charset error...");
				ret = null;
			}
		}catch(Exception e1){
			if(logger.isWarnEnabled())
				logger.warn("DecodeBuffer Exception:" + e1);
			ret = null;
		}
		return ret;
	}
	/**从recv_data中提取一个完整的XML报文体
	 * @return 存在则返回提取的XML报文体，其他情况均返回null
	 */
	public String pickupRecievePackage(){
		String ret = null;
		if(this.recv_data == null)
			return ret;
		if(this.recv_data.length() < endstring_task.length())
			return ret;
		int temp_int = this.recv_data.indexOf(endstring_task);
		if(temp_int > 0){ // 接收到一个完整的XML任务报文
			synchronized(this){
				ret = this.recv_data.substring(0, temp_int + endstring_task.length());
				this.recv_data.delete(0, temp_int + endstring_task.length());
				if(this.recv_data.indexOf(endstring_task) > 0){ // 后续还有完整的XML任务报文
					this.has_complete_resp = true;
				}else{
					this.has_complete_resp = false;
				}
			}
		}
		return ret;
	}
	/**释放所有的资源
	 */
	public void releaseResouce(){
		if(this.socketchannel != null){
			try{this.socketchannel.close();}
			catch(Exception e1){}
			finally{this.socketchannel = null; this.release_time = new Date();}
		}
		this.setCanRecvdata(false);
	}
	public boolean isCanRecvdata() {
		return can_recvdata;
	}
	public void setCanRecvdata(boolean can_recvdata) {
		this.can_recvdata = can_recvdata;
	}
}
