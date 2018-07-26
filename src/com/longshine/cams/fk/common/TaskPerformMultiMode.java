package com.longshine.cams.fk.common;

import com.longshine.cams.fk.structs.FKTask;

public interface TaskPerformMultiMode {
	/**采用后台任务服务多JOB交互模式执行任务时，当后台服务完成部分任务后，通过回调业务执行类中的该方法实现业务结果处理.<br>
	 * <p>当业务执行类中完成了所有的工作后，可以在该回调方法中调用tsjobs
	 * @param v_task 将完成任务的任务作为参数调用业务执行类，在该类中的tsjobs对象中包含了所有的后台服务交互工作对象List，业务执行类可以根据业务需要进行结果处理及后续过程驱动
	 */
	public void completeSomeJobs(FKTask v_task);
	public int BeginJobs(FKTask v_task, long v_oversecs);
}
