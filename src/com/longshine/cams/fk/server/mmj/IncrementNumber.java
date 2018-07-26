package com.longshine.cams.fk.server.mmj;

/**
 * 从0~255循环顺序号
 *
 */
public class IncrementNumber {

	private  IncrementNumber(){ 
		
	}
	
	private static IncrementNumber init=new IncrementNumber();
	private static int num=1;
	private static int numlength8=1;
	
	public static IncrementNumber getInstance(){
		return init;
	}
	
	public synchronized  int getIncrementNumber(){
		if(num>=256){
			num=1;
		}
		return num++;
	}
	
	public synchronized  int getIncNumLength8(){
		if(numlength8>=100000000){
			numlength8=1;
		}
		return numlength8++;
	}
	
	public static void main(String[] args){
		 
		for(int i=0;i<100000010;i++){
			//System.out.println(IncrementNumber.getInstance().getIncrementNumber());
			System.out.println(IncrementNumber.getInstance().getIncNumLength8());
		}
	}
}
