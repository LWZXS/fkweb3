package com.longshine.cams.fk.server.mmj;


public class TxJkYckzRes  implements java.io.Serializable,Cloneable {
	
	private static final long serialVersionUID = -9209670521602748605L;
		// 控制命令密文；输出的数据，字符型，20字节。
	private String KZMLMW;
	// 接收状态，成功返回0，失败返回-1
	private Long JSZT;
	// 异常信息，成功返回空，错误返回系统处理异常信息。 
	private String YCXX;
	
	private String taskId;
	//建立时间
	private String createDate;
	
	public TxJkYckzRes() {
	}
	public String getKZMLMW() {
		return this.KZMLMW;
	}
	public void setKZMLMW(String kZMLMW) {
		this.KZMLMW = kZMLMW;
	}
	public Long getJSZT() {
		return this.JSZT;
	}
	public void setJSZT(Long jSZT) {
		this.JSZT = jSZT;
	}
	public String getYCXX() {
		return YCXX;
	}
	public void setYCXX(String yCXX) {
		this.YCXX = yCXX;
	}
	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	public String getCreateDate() {
		return createDate;
	}
	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}
	
	
	public Object clone() {
		TxJkYckzRes o = null;
        try {
            o = (TxJkYckzRes) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return o;
    }
	
	@Override
	public String toString(){
		return "[FK_MMJ_YCKZ_RES]KZMLMW:" + this.getKZMLMW() + ";JSZT:" + this.getJSZT() + ";YCXX:" + this.getYCXX()+";TASKID:"+this.taskId;
	}
}
