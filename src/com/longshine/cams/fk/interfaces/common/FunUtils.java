package com.longshine.cams.fk.interfaces.common;

public class FunUtils {
		//取0304XXX，第三四位值
		public static String zzjgbm(String qxdwbm) {
			if(qxdwbm!=null && qxdwbm.length()>2){
				if(qxdwbm.length()>3){
					return qxdwbm.substring(2, 4);
				}else{
					return qxdwbm.substring(2, 3);
				}
			}else{
				return qxdwbm;
			}
			
		}
}