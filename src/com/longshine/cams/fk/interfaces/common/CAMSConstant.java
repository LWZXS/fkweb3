package com.longshine.cams.fk.interfaces.common;

import java.text.SimpleDateFormat;

public class CAMSConstant {
	/**默认12个0作为表号*/
	public static final String BH_DEFAULT_0 = "000000000000";
	// 时间格式常量
	/**时间格式：yyyy-MM*/
	public static final SimpleDateFormat DF_YYYY_MM = new SimpleDateFormat("yyyy-MM");
	/**时间格式：yyyy-MM-dd*/
	public static final SimpleDateFormat DF_YYYY_MM_DD = new SimpleDateFormat("yyyy-MM-dd");
	/**时间格式：yyMMddHHmmss*/
	public static final SimpleDateFormat DF_YYMMDDHH24MISS = new SimpleDateFormat("yyMMddHHmmss");
	/**时间格式：yyyy-MM-dd HH:mm:ss*/
	public static final SimpleDateFormat DF_YYYY_MM_DD_HH24_MI_SS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
}
