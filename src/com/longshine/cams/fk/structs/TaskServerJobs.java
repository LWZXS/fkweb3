package com.longshine.cams.fk.structs;

import java.util.Hashtable;
import java.util.Map;

public class TaskServerJobs {
	private long total_jobs;
	private long remain_jobs;
	private Map<String,TaskServerJob> job_list = null;
	public TaskServerJobs(){
		this.total_jobs = 0;
		this.remain_jobs = 0;
		this.job_list = new Hashtable<String,TaskServerJob>();
	}
	/**增加一个后台TaskServer执行工作
	 * @author wolf 2016-7-15
	 * @param v_job 待添加到队列的Job实例
	 * @return false:添加任务失败；true:添加任务成功
	 */
	public boolean addJob(TaskServerJob v_job){
		if(this.job_list == null)
			this.job_list = new Hashtable<String,TaskServerJob>();
		if(this.job_list.containsKey(v_job.getTaskServerID()))
			return false;
		v_job.setStatus(TaskAttribute.TSJobStatus.initial);
		v_job.setJobContainer(this);
		this.job_list.put(v_job.getTaskServerID(), v_job);
		this.total_jobs = this.job_list.size();
		return true;
	}
	public boolean filledTSJobResponseResult(TaskServerJob v_job, String v_resxml){
		if(v_job == null)
			return false;
		if(this.job_list == null)
			return false;
		if(!this.job_list.containsKey(v_job.getTaskServerID()))
			return false;
		TaskServerJob job = this.job_list.get(v_job.getTaskServerID());
		job.setResponseXml(v_resxml);
		job.setStatus(TaskAttribute.TSJobStatus.ts_recieve_ok);
		job.setEndDate();
		return true;
	}
	/**设置任务状态
	 * @param v_job 待设置状态的Job实例
	 * @param v_status 设置对应Job实例的状态值，是一个TaskAttribute.TSJobStatus枚举类型
	 * @return true 设置成功，false 设置失败
	 */
	public boolean setJobStatus(TaskServerJob v_job, TaskAttribute.TSJobStatus v_status){
		if(v_job == null)
			return false;
		if(this.job_list == null)
			return false;
		if(!this.job_list.containsKey(v_job.getTaskServerID()))
			return false;
		TaskServerJob job = this.job_list.get(v_job.getTaskServerID());
		// 修改任务的当前状态
		job.setStatus(v_status);
		
		long temp_status = v_status.getValue();
		long temp_long = 0;
		if(temp_status >= TaskAttribute.TSJobStatus.ts_recieve_ok.getValue()){	// 检查已经完成的任务个数
			// 执行完成后，设置JOB的任务完成时间
			job.setEndDate();
			// 检查所有的子任务，统计剩余未完成的任务数
			for(String jobid:this.job_list.keySet()){
				temp_status = this.job_list.get(jobid).getStatus().getValue();
				if(temp_status >= TaskAttribute.TSJobStatus.ts_recieve_ok.getValue())
					temp_long += 1;
			}
			this.remain_jobs = this.total_jobs - temp_long;
		}
		return true;
	}
	public long getTotalJobs() {
		return total_jobs;
	}
	public long getRemainJobs() {
		return remain_jobs;
	}
	public Map<String,TaskServerJob> getJobList() {
		return job_list;
	}
	public void setTotalJobs(long total_jobs) {
		this.total_jobs = total_jobs;
	}
	public void setRemainJobs(long remain_jobs) {
		this.remain_jobs = remain_jobs;
	}
}
