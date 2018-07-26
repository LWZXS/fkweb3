package com.longshine.cams.fk.common;

import java.util.Date;

public class CommonUtil {
	// 时间增加秒数
	public static Date DateAddSeconds(Date v_dt, long v_secs){
		long dt_secs = v_dt.getTime() + v_secs * 1000;
		return new Date(dt_secs);
	}
}
