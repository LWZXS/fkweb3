package com.longshine.cams.fk.structs;

import java.util.Date;

public class TaskServerJob {
	private String taskserverid;
	private String taskmxid;
	private String request_xml;
	private String response_xml;
	private Date begin_time;
	private Date end_time;
	private Long oversecs;
	private String jobsign;	// 任务识别标签，供JOB发布者设定该任务的标签，当任务完成时，JOB发布者可以根据该标识执行不同的处理逻辑
	private TaskAttribute.TSJobStatus status;
	private TaskServerJobs job_container;
	private int errercode;		// 保存后台服务返回的错误码
	private String errormsg;	// 保存后台服务返回的错误信息
	
	public TaskServerJob(String v_taskserverid, String v_taskmxid, String v_req_xml, long v_oversecs){
		this.taskserverid = v_taskserverid;
		this.taskmxid = v_taskmxid;
		this.request_xml = v_req_xml;
		this.oversecs = v_oversecs;
		this.status = TaskAttribute.TSJobStatus.initial;
	}
	public String getTaskServerID() {
		return taskserverid;
	}
	public String getTaskmxid() {
		return taskmxid;
	}
	public String getRequestXml() {
		return request_xml;
	}
	public String getResponseXml() {
		return response_xml;
	}
	public Date getBeginDate() {
		return begin_time;
	}
	public Date getEndDate() {
		return end_time;
	}
	public Long getOversecs() {
		return oversecs;
	}
	public TaskServerJobs getJobContainer() {
		return job_container;
	}
	public TaskAttribute.TSJobStatus getStatus() {
		return status;
	}
	public String getJobSign() {
		return jobsign;
	}
	public int getErrerCode() {
		return errercode;
	}
	public String getErrorMessage() {
		return errormsg;
	}
	public void setErrorCodeAndMsg(int v_errcode, String v_errmsg) {
		this.errercode = v_errcode;
		this.errormsg = v_errmsg;
	}
	public void setBeginDate(Date begin_dt) {
		this.begin_time = begin_dt;
	}
	public void setBeginDate() {
		this.begin_time = new Date();
	}
	public void setEndDate(Date end_dt) {
		this.end_time = end_dt;
	}
	public void setEndDate() {
		this.end_time = new Date();
	}
	public void setOversecs(Long oversecs) {
		if(this.oversecs == null)
			this.oversecs = oversecs;
	}
	public void setRequestXml(String request_xml) {
		this.request_xml = request_xml;
	}
	public void setResponseXml(String response_xml) {
		this.response_xml = response_xml;
	}
	public void setTaskServerID(String taskserverid) {
		this.taskserverid = taskserverid;
	}
	public void setTaskmxid(String taskmxid) {
		this.taskmxid = taskmxid;
	}
	/**该方法只能供TaskServerJobs容器调用，不能在外部调用
	 * @author wolf 2016-7-15
	 * @param v_status
	 */
	void setStatus(TaskAttribute.TSJobStatus v_status) {
		this.status = v_status;
	}
	public void setJobContainer(TaskServerJobs job_container) {
		this.job_container = job_container;
	}
	public void setJobSign(String jobsign) {
		this.jobsign = jobsign;
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "taskserverid:" + taskserverid + ";taskmxid:" + taskmxid + ";oversecs:" + oversecs + ";request_xml:" + request_xml + ";response_xml:" + response_xml;
	}
}
