package com.longshine.cams.fk.server;

import com.longshine.cams.fk.common.BaseThread;
import com.longshine.cams.fk.structs.FKTask;

public class PROC_FKTaskArchieve extends BaseThread{
	// 任务管理对象引用
	private FKTaskManager taskmgr = null;
	// 线程初始化
	public PROC_FKTaskArchieve(long v_idle_sleep,FKTaskManager v_taskmgr) {
		super(v_idle_sleep);
		this.setThreadName("FKTaskArchieve Thread:" + this.getId());
		// TODO Auto-generated constructor stub
		taskmgr = v_taskmgr;
	}
	/**获取历史任务，并将历史任务从任务执行表迁移到任务历史表中，迁移逻辑：
	 * 1、从FK_TASK和FK_TASK_MX表中获取该记录；
	 * 2、将对应记录插入到FK_TASK_YYYYMM和FK_TASK_MX_YYYYMM表中，其中YYYYMM为FK_TASK.INTIME字段的YYYYMM为准
	 * 3、以FK_TASK_YYYYMM.TASKID和FK_TASK_MX_YYYYMM.TASKMXID为主键，存在则更新，不存在则插入
	 * 4、从FK_TASK_MX中，删除以TASKID存在的记录
	 * 5、从FK_TASK中删除TASKID的记录
	 * 6、以上步骤在一个数据库事务中处理。
	 */
	protected boolean dowork(){
		FKTask task = taskmgr.getTaskArchieve();
		if (task == null)
			return false;
		// 执行历史任务迁移工作
		this.archieveTask(task);
		return true;
	}
	private void archieveTask(final FKTask v_task){
		try{
			this.taskmgr.db_archieveTask(v_task);
		}catch(Exception e){
			logger.warn("Archieve Task(" + v_task + ") Exception:" + e);
		}
	}
}
