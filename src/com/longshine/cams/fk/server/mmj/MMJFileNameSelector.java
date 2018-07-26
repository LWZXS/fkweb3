package com.longshine.cams.fk.server.mmj;

import java.io.File;
import java.io.FilenameFilter;

public class MMJFileNameSelector implements FilenameFilter {

	public static final  int START=1;				//过滤文件头
	public static final int END=2;					//过滤文件扩展名
	public static final int STARTEND=3;		//过滤文件头+文件扩展名
	public static final int ALL=4;					//过滤文件关键字
	private String startStr = "";
	private String endStr = "";
	private int type=0;
	
	
	public String getStartStr() {
		return startStr;
	}

	public void setStartStr(String startStr) {
		this.startStr = startStr;
	}

	public String getEndStr() {
		return endStr;
	}

	public void setEndStr(String endStr) {
		this.endStr = endStr;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
	public MMJFileNameSelector() {
		super();
	}
	
	public MMJFileNameSelector(int type,String startStr,String endStr) {
		this.type=type;
		this.startStr = startStr;
		this.endStr=endStr;
	}
	
	public MMJFileNameSelector(int type,String startStr) {
		this.type=type;
		this.startStr = startStr;
	}
	
	public void filterStart(String startStr){
		this.type=MMJFileNameSelector.START;
		this.startStr = startStr;
	}
	
	public void filterEnd(String endStr){
		this.type=MMJFileNameSelector.END;
		this.endStr = endStr;
	}
	
	public void filterStartEnd(String startStr,String endStr){
		this.type=MMJFileNameSelector.STARTEND;
		this.endStr = endStr;
		this.startStr = startStr;
	}
	
	public void filterALL(String allStr){
		this.type=MMJFileNameSelector.ALL;
		this.startStr = allStr;
	}
	
	@Override
	public boolean accept(File dir, String name) {
		switch(type){
			case 1:
				return name.startsWith(startStr);
			case 2:
				return name.endsWith("."+endStr);
			case 3:
				return name.startsWith(startStr)&&name.endsWith(endStr);
			case 4:
				return name.indexOf(startStr)>-1;
			default:
				return true;
		}
		
	}

}
