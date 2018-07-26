package com.longshine.cams.fk.structs;

public class StringWrapper {
	// 保存后台任务执行后的XML执行结果
	public String str;
	public String toString(){
		if(str != null)
			return str.toString();
		else
			return "";
	}
}
