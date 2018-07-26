package com.longshine.cams.fk.server.mmj;



public class TxJkJlycsfrzReq implements java.io.Serializable,Cloneable{
		
	private static final long serialVersionUID = -4382985429909644438L;
		// 密钥状态；整型，0: 测试密钥状态；1: 正式密钥状态；
		private Long MYZT;
		// 分散因子；字符型,8字节，“0000”+表号；
		private String FSYZ;
		
		private String taskId;
		//建立时间
		private String createDate;

		public TxJkJlycsfrzReq() {
		}

		public Long getMYZT() {
			return MYZT;
		}

		public void setMYZT(Long mYZT) {
			this.MYZT = mYZT;
		}

		public String getFSYZ() {
			return FSYZ;
		}

		public void setFSYZ(String fSYZ) {
			this.FSYZ = fSYZ;
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
			TxJkJlycsfrzReq o = null;
	        try {
	            o = (TxJkJlycsfrzReq) super.clone();
	        } catch (CloneNotSupportedException e) {
	            e.printStackTrace();
	        }
	        return o;
	    }
		
		@Override
		public String toString(){
			return "[FK_MMJ_JLYCSFRZ_REQ]MYZT:" +  this.MYZT + ";FSYZ:" +  this.FSYZ+";TASKID:"+ this.taskId;
		}
}
