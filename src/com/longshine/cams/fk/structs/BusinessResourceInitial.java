package com.longshine.cams.fk.structs;

public class BusinessResourceInitial {

	/* 定义业务层类初始化类路径名称 */
	private String class_name;
	/* 定义业务层类初始化静态方法名称 */
	private String function_name;
	/* 定义业务层类初始化静态方法处理方式，不同方式将采用不同的系统参数初始化，如system_mode方式，将使用TaskAttribute.InitialType变量去初始化 */
	private TaskAttribute.InitialType type;
	
	public TaskAttribute.InitialType getType() {
		return type;
	}
	public void setType(TaskAttribute.InitialType v_type) {
		this.type = v_type;
	}
	public String getFunctionName() {
		return function_name;
	}
	public void setFunctionName(String function_name) {
		this.function_name = function_name;
	}
	public String getClassName() {
		return class_name;
	}
	public void setClassName(String class_name) {
		this.class_name = class_name;
	}
}
