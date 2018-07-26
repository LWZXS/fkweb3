package com.longshine.cams.fk.structs;

public class InterfaceProperty {
	// 接口配置属性的方法名
	public String function;
	// 异步消息的回调接口方法名
	public String call_func;
	// 配置接口的指令编码
	public String zlbm;
	// 接口配置中文名
	public String name;
	// 接口配置模式,priority,block,unblock
	public String mode;
	// 接口配置超时等待时间
	public Long oversecs;
	// 接口配置任务检查实现类路径
	public String class_parse;
	// 接口配置任务执行实现类路径
	public String class_perform;
}
