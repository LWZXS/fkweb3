package com.longshine.cams.fk.server.mmj;


public class TxJkYckzReq  implements java.io.Serializable,Cloneable {
	
	private static final long serialVersionUID = 8816283633850071779L;
	// 密钥状态，整型，0: 测试密钥状态；1: 正式密钥状态；
	private Long MYZT;
	// 密码机随机数,字符型,4字节，电表身份认证成功后返回；
	private String MMJSJS;
	// 分散因子,字符型,8字节，“0000”+表号；
	private String FSYZ;
	// 安全模块序列号, 字符型, 8字节；
	private String AQMKXLH;
	// 控制命令数据；表示拉闸、合闸、报警等控制命令明文,字符型,8字节；
	private String KZMLSJ;
	
	private String taskId;
	//建立时间
	private String createDate;
	
	public TxJkYckzReq() {
	}

	public Long getMYZT() {
		return MYZT;
	}

	public void setMYZT(Long mYZT) {
		this.MYZT = mYZT;
	}

	public String getMMJSJS() {
		return MMJSJS;
	}

	public void setMMJSJS(String mMJSJS) {
		this.MMJSJS = mMJSJS;
	}

	public String getFSYZ() {
		return FSYZ;
	}

	public void setFSYZ(String fSYZ) {
		this.FSYZ = fSYZ;
	}

	public String getAQMKXLH() {
		return AQMKXLH;
	}

	public void setAQMKXLH(String aQMKXLH) {
		this.AQMKXLH = aQMKXLH;
	}

	public String getKZMLSJ() {
		return KZMLSJ;
	}

	public void setKZMLSJ(String kZMLSJ) {
		this.KZMLSJ = kZMLSJ;
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

	@Override
	public String toString(){
		return "[FK_MMJ_YCKZ_REQ]MYZT:" + this.MYZT + ";MMJSJS:" +  this.MMJSJS + ";FSYZ:" +  this.FSYZ + ";AQMKXLH:" +  this.AQMKXLH + ";KZMLSJ:" +  this.KZMLSJ+";TASKID:"+ this.taskId;
	}
	
	public Object clone() {
		TxJkYckzReq o = null;
        try {
            o = (TxJkYckzReq) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return o;
    }
}
