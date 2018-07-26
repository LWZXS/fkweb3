package com.longshine.cams.fk.server.mmj;



public class TxJkJlycsfrzRes implements java.io.Serializable,Cloneable{
		
	private static final long serialVersionUID = 1029335837481303466L;
		// 密码机随机数,字符型,8字节
		private String MMJSJS;
		// 随机数密文,字符型,8字节。
		private String SJSMW;
		// 接收状态，成功返回0，失败返回-1。
		private Long JSZT;
		// 异常信息，成功返回空，错误返回系统处理异常信息，错误码详见附件-密码机异常代码表
		private String YCXX;
		
		private String taskId;
		//建立时间
		private String createDate;

		public TxJkJlycsfrzRes() {
		}

		public String getMMJSJS() {
			return MMJSJS;
		}
		public void setMMJSJS(String mMJSJS) {
			this.MMJSJS = mMJSJS;
		}
		public String getSJSMW() {
			return SJSMW;
		}
		public void setSJSMW(String sJSMW) {
			this.SJSMW = sJSMW;
		}
		public Long getJSZT() {
			return JSZT;
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
			TxJkJlycsfrzRes o = null;
	        try {
	            o = (TxJkJlycsfrzRes) super.clone();
	        } catch (CloneNotSupportedException e) {
	            e.printStackTrace();
	        }
	        return o;
	    }
		
		@Override
		public String toString(){
			return "[FK_MMJ_JLYCSFRZ_RES]MMJSJS:" +  this.MMJSJS + ";SJSMW:" +  this.SJSMW + ";JSZT:" +  this.JSZT + ";YCXX:" +  this.YCXX+";TASKID:"+ this.taskId;
		}
}
