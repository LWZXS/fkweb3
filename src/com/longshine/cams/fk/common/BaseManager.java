package com.longshine.cams.fk.common;

public abstract class BaseManager {
	// 线程启动
	public abstract void startProc();
	// 设置所有线程退出条件
	public abstract void setProcExit();
	// 等待所有线程结束
	public abstract void joinProc();
	// 管理器初始化
	public abstract void initializeManager();
}
