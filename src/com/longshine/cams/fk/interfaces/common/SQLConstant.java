package com.longshine.cams.fk.interfaces.common;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.longshine.cams.fk.server.FKServer;
import com.longshine.cams.fk.structs.BusinessResourceInitial;
import com.longshine.cams.fk.structs.TaskAttribute;

public class SQLConstant {
	private static Log logger = LogFactory.getLog(SQLConstant.class);
	/**获取低压和高压用户的指定电表资产编号和用户号通信参数获取SQL查询行集
	 * @author wolf 2016-7-10,行集中各字段的顺序如下：
	 * 1 : YHBH; 2 : ZCBH; 3 : JFBM; 4 : ZDLJDZ; 5 : CBSXH; 6 : BDZ;
	 * 7 : GYBM; 8 : CLDBS; 9 : COMMPORT; 10 : BAUDRATE; 11 : BAUDRATE_YS
	 */
	public static String sql_infor_dyjm;
	public static String sql_infor_dyjm_yhbh;
	public static String sql_infor_dyjm_yhbh_zcbh;
	public static String sql_infor_gy;
	public static String sql_infor_gy_yhbh;
	public static String sql_infor_gy_yhbh_zcbh;

	public static String sql_infor_dyjm_yhbh_in;
	public static String sql_infor_dyjm_yhbh_zcbh_in;
	public static String sql_infor_gy_yhbh_in;
	public static String sql_infor_gy_yhbh_zcbh_in;
	/* 根据用户编号查询具体地市的低压用户档案数量 */
	public static String sql_dyjmydkh_count;
	/* 根据用户编号查询具体地市的高压用户档案数量 */
	public static String sql_ydkh_count;
	public static String sql_dyjm_update_fkbz;
	public static String sql_gy_update_fkbz;

	/* 以下定义是适应于广东省集中的语句集，包括广东省集中、广州计量 */
	public static final String gd_sql_infor_dyjm;
	public static final String gd_sql_infor_dyjm_yhbh;
	public static final String gd_sql_infor_dyjm_yhbh_zcbh;
	public static final String gd_sql_infor_gy;
	public static final String gd_sql_infor_gy_yhbh;
	public static final String gd_sql_infor_gy_yhbh_zcbh;
	public static final String gd_sql_infor_dyjm_yhbh_in;
	public static final String gd_sql_infor_dyjm_yhbh_zcbh_in;
	public static final String gd_sql_infor_gy_yhbh_in;
	public static final String gd_sql_infor_gy_yhbh_zcbh_in;
	public static final String gd_sql_dyjmydkh_count;
	public static final String gd_sql_ydkh_count;
	public static final String gd_sql_dyjm_update_fkbz;
	public static final String gd_sql_gy_update_fkbz;

	/* 以下定义是适应于省外地市主站的的语句集，包括广西地市主站、贵州地市主站、云南地市主站 */
	public static final String sw_sql_infor_dyjm;
	public static final String sw_sql_infor_dyjm_yhbh;
	public static final String sw_sql_infor_dyjm_yhbh_zcbh;
	public static final String sw_sql_infor_gy;
	public static final String sw_sql_infor_gy_yhbh;
	public static final String sw_sql_infor_gy_yhbh_zcbh;
	public static final String sw_sql_infor_dyjm_yhbh_in;
	public static final String sw_sql_infor_dyjm_yhbh_zcbh_in;
	public static final String sw_sql_infor_gy_yhbh_in;
	public static final String sw_sql_infor_gy_yhbh_zcbh_in;
	public static final String sw_sql_dyjmydkh_count;
	public static final String sw_sql_ydkh_count;
	public static final String sw_sql_dyjm_update_fkbz;
	public static final String sw_sql_gy_update_fkbz;

	static{
		StringBuffer str = new StringBuffer();
		// 广东用户档案查询
		str.append("SELECT F.YHBH,B.ZCBH,E.JFBM,C.ZDLJDZ,A.CBSXH,B.BDZ,H.GYBM,A.DYCLDBS AS CLDBS,");
		str.append(" K.CS_8904 AS COMMPORT,BTL.DMBM AS BAUDRATE,K.CS_8905 AS BAUDRATE_YS");
		str.append(" FROM DA_DYJMCLD A");
		str.append(" LEFT JOIN DA_DNBZC B ON A.DYCLDBS=B.DYCLDBS");
		str.append(" LEFT JOIN DA_ZD C ON A.ZDBS=C.ZDBS");
		str.append(" LEFT JOIN DA_ZDZC D ON C.ZDBS=D.ZDBS");
		str.append(" LEFT JOIN CJ_GY_GY H ON D.GYBS=H.GYBS");
		str.append(" LEFT JOIN DA_DYJMJLD E ON A.JLDBS=E.JLDBS");
		str.append(" LEFT JOIN DA_DYJMYDKH F ON E.DYJMYHBS=F.DYJMYHBS");
		//端口以实际的减1
		//str.append(" LEFT JOIN (SELECT GY.CLDBS,  GY.CS_8905,GY.CS_8904 FROM CJ_CS_JLDCS GY UNION ALL SELECT DY.CLDBS, DY.BTL,DY.SBWDK FROM CJ_CS_DYJMCLDCS DY ) K ON A.DYCLDBS=K.CLDBS");
		str.append(" LEFT JOIN (SELECT GY.CLDBS,  GY.CS_8905,decode(GY.CS_8904,'0','0','1','0',GY.CS_8904 -1 ) CS_8904 FROM CJ_CS_JLDCS GY UNION ALL SELECT DY.CLDBS, DY.BTL,decode(DY.SBWDK,'0','0','1','0',DY.SBWDK -1 ) SBWDK FROM CJ_CS_DYJMCLDCS DY ) K ON A.DYCLDBS=K.CLDBS");
		str.append(" LEFT JOIN (SELECT IN_A.DMBMMC,MAX(IN_A.DMBM) DMBM FROM XT_SJZDBM IN_A LEFT JOIN XT_SJZDDMFL IN_B ON IN_A.DMFLBS = IN_B.DMFLBS WHERE IN_B.DMFL='BTL' GROUP BY IN_A.DMBMMC) BTL ON K.CS_8905 = BTL.DMBM");
		str.append(" WHERE F.QXDWBM LIKE '${DSBM}%'");
		str.append(" AND ROWNUM < ${MAX_CACHE_ROWS}");	// 该变量在BaseDao的querySQL方法中有替换，如果调用该方法，则可以不用外部显示替换
		gd_sql_infor_dyjm = str.toString();
		str.append(" AND F.YHBH = '${YHBH}'");
//		str.append(" AND F.YHBH in ('0312000088990003','0312000088990004','0312000088990005','0312000088990006','0312000088990007','0312000088990008','0312000088990009')");
		gd_sql_infor_dyjm_yhbh = str.toString();
		str.append(" AND B.ZCBH = '${ZCBH}'");
		gd_sql_infor_dyjm_yhbh_zcbh = str.toString();
		str.setLength(0);
		str.append(gd_sql_infor_dyjm);
		str.append(" AND F.YHBH IN(${YHBHS})");
		gd_sql_infor_dyjm_yhbh_in = str.toString();
		str.append(" AND B.ZCBH IN(${ZCBHS})");
		gd_sql_infor_dyjm_yhbh_zcbh_in = str.toString();
		str.setLength(0);
		str.append("SELECT F.YHBH,B.ZCBH,E.JFBM,C.ZDLJDZ,A.CBSXH,B.BDZ,H.GYBM,A.CLDBS,");
		str.append(" K.CS_8904 AS COMMPORT,BTL.DMBM AS BAUDRATE,K.CS_8905 AS BAUDRATE_YS");
		str.append(" FROM DA_CLD A LEFT JOIN DA_DNBZC B ON A.CLDBS=B.CLDBS");
		str.append(" LEFT JOIN DA_ZD C ON A.ZDBS=C.ZDBS");
		str.append(" LEFT JOIN DA_ZDZC D ON C.ZDBS=D.ZDBS");
		str.append(" LEFT JOIN CJ_GY_GY H ON D.GYBS=H.GYBS");
		str.append(" LEFT JOIN DA_JLD E ON A.JLDBS=E.JLDBS");
		str.append(" LEFT JOIN DA_YDKH F ON E.YHBS=F.YHBS");
		str.append(" LEFT JOIN CJ_CS_JLDCS K ON A.CLDBS=K.CLDBS");
		str.append(" LEFT JOIN (SELECT IN_A.DMBMMC,MAX(IN_A.DMBM) DMBM FROM XT_SJZDBM IN_A LEFT JOIN XT_SJZDDMFL IN_B ON IN_A.DMFLBS = IN_B.DMFLBS WHERE IN_B.DMFL='BTL' GROUP BY IN_A.DMBMMC) BTL ON K.CS_8905 = BTL.DMBMMC");
		str.append(" WHERE F.QXDWBM LIKE '${DSBM}%'");
		str.append(" AND ROWNUM < ${MAX_CACHE_ROWS}");	// 该变量在BaseDao的querySQL方法中有替换，如果调用该方法，则可以不用外部显示替换
		gd_sql_infor_gy = str.toString();
		str.append(" AND F.YHBH = '${YHBH}'");
//		str.append(" AND F.YHBH in ('0312000088990003','0312000088990004','0312000088990005','0312000088990006','0312000088990007','0312000088990008','0312000088990009')");

		
		gd_sql_infor_gy_yhbh = str.toString();
		str.append(" AND B.ZCBH = '${ZCBH}'");
		gd_sql_infor_gy_yhbh_zcbh = str.toString();
		str.setLength(0);
		str.append(gd_sql_infor_gy);
		str.append(" AND F.YHBH IN(${YHBHS})");
		gd_sql_infor_gy_yhbh_in = str.toString();
		str.append(" AND B.ZCBH IN(${ZCBHS})");
		gd_sql_infor_gy_yhbh_zcbh_in = str.toString();

		// 省外用户档案查询(其中LAYER含义：90-专变，91-公变，92-低压居民)
		str.setLength(0);
		str.append(" SELECT CUST.USER_NO AS YHBH,ZC.CORPORATECODE AS ZCBH,MP.LOCAL_NO AS JFBM,TERM.TERM_ADDR AS ZDLJDZ,");
		str.append(" MP.COMM_NO AS CBSXH,ZC.AMM_COMM_CODE AS BDZ,TERM_ASSET.PROTOCOL_NO AS GYBM,MP.MRID CLDBS,");
		str.append(" ZC.COMM_PORT AS COMMPORT,ZC.BAUDRATE AS BAUDRATE,ZC.BAUDRATE AS BAUDRATE_YS");
		str.append(" FROM SYS_ARC_LINK SAL");
		str.append(" LEFT JOIN INF_MP MP ON SAL.MP_ID=MP.MRID");
		str.append(" LEFT JOIN INF_MP_ASSET ZC ON MP.ASSET_ID=ZC.MRID");
		str.append(" LEFT JOIN INF_TERM TERM ON SAL.TERM_ID=TERM.MRID");
		str.append(" LEFT JOIN INF_TERM_ASSET TERM_ASSET ON TERM.ASSET_ID=TERM_ASSET.MRID");
		str.append(" LEFT JOIN INF_CUSTOMERAGREEMENT CUST ON SAL.CUST_AGREE_ID=CUST.MRID");
		str.append(" WHERE SAL.IS_VALID=1 AND SAL.LAYER IN(92)");
		str.append(" AND ROWNUM < ${MAX_CACHE_ROWS}");	// 该变量在BaseDao的querySQL方法中有替换，如果调用该方法，则可以不用外部显示替换
		sw_sql_infor_dyjm = str.toString();
		str.append(" AND CUST.USER_NO = '${YHBH}'");
		sw_sql_infor_dyjm_yhbh = str.toString();
		str.append(" AND ZC.CORPORATECODE = '${ZCBH}'");
		sw_sql_infor_dyjm_yhbh_zcbh = str.toString();
		str.setLength(0);
		str.append(sw_sql_infor_dyjm);
		str.append(" AND CUST.USER_NO IN(${YHBHS})");
		sw_sql_infor_dyjm_yhbh_in = str.toString();
		str.append(" AND ZC.CORPORATECODE IN(${ZCBHS})");
		sw_sql_infor_dyjm_yhbh_zcbh_in = str.toString();
		str.setLength(0);
		str.append(" SELECT CUST.USER_NO AS YHBH,ZC.CORPORATECODE AS ZCBH,MP.LOCAL_NO AS JFBM,TERM.TERM_ADDR AS ZDLJDZ,");
		str.append(" MP.COMM_NO AS CBSXH,ZC.AMM_COMM_CODE AS BDZ,TERM_ASSET.PROTOCOL_NO AS GYBM,MP.MRID CLDBS,");
		str.append(" ZC.COMM_PORT AS COMMPORT,ZC.BAUDRATE AS BAUDRATE,ZC.BAUDRATE AS BAUDRATE_YS");
		str.append(" FROM SYS_ARC_LINK SAL");
		str.append(" LEFT JOIN INF_MP MP ON SAL.MP_ID=MP.MRID");
		str.append(" LEFT JOIN INF_MP_ASSET ZC ON MP.ASSET_ID=ZC.MRID");
		str.append(" LEFT JOIN INF_TERM TERM ON SAL.TERM_ID=TERM.MRID");
		str.append(" LEFT JOIN INF_TERM_ASSET TERM_ASSET ON TERM.ASSET_ID=TERM_ASSET.MRID");
		str.append(" LEFT JOIN INF_CUSTOMERAGREEMENT CUST ON SAL.CUST_AGREE_ID=CUST.MRID");
		str.append(" WHERE SAL.IS_VALID=1 AND SAL.LAYER IN(90,91)");
		str.append(" AND ROWNUM < ${MAX_CACHE_ROWS}");	// 该变量在BaseDao的querySQL方法中有替换，如果调用该方法，则可以不用外部显示替换
		sw_sql_infor_gy = str.toString();
		str.append(" AND CUST.USER_NO = '${YHBH}'");
		sw_sql_infor_gy_yhbh = str.toString();
		str.append(" AND ZC.CORPORATECODE = '${ZCBH}'");
		sw_sql_infor_gy_yhbh_zcbh = str.toString();
		str.setLength(0);
		str.append(sw_sql_infor_gy);
		str.append(" AND CUST.USER_NO IN(${YHBHS})");
		sw_sql_infor_gy_yhbh_in = str.toString();
		str.append(" AND ZC.CORPORATECODE IN(${ZCBHS})");
		sw_sql_infor_gy_yhbh_zcbh_in = str.toString();

		/* 在某地市根据用户编号查询具体的低压用户档案 */
		gd_sql_dyjmydkh_count = "SELECT COUNT(1) FROM DA_DYJMYDKH WHERE YHBH='${YHBH}' AND QXDWBM LIKE '${DSBM}%'";
		sw_sql_dyjmydkh_count = "SELECT COUNT(1) FROM INF_CUSTOMERAGREEMENT WHERE IS_VALID=1 AND USER_NO='${YHBH}'";
		/* 在某地市根据用户编号查询具体的高压用户档案 */
		gd_sql_ydkh_count = "SELECT COUNT(1) FROM DA_YDKH WHERE YHBH='${YHBH}' AND QXDWBM LIKE '${DSBM}%'";
		sw_sql_ydkh_count = "SELECT COUNT(1) FROM INF_CUSTOMERAGREEMENT WHERE IS_VALID=1 AND USER_NO='${YHBH}'";
		/**设置费控用户标志修改的语句
		 * */
		gd_sql_dyjm_update_fkbz = "UPDATE DA_DYJMYDKH SET FKBZ = '${FKBZ} WHERE YHBH='${YHBH}' AND QXDWBM LIKE '${DSBM}%'";
		gd_sql_gy_update_fkbz = "UPDATE DA_YDKH SET FKBZ = '${FKBZ} WHERE YHBH='${YHBH}' AND QXDWBM LIKE '${DSBM}%'";
		sw_sql_dyjm_update_fkbz = "UPDATE INF_CUSTOMERAGREEMENT SET FKBZ = '${FKBZ} WHERE IS_VALID=1 AND USER_NO='${YHBH}'";
		sw_sql_gy_update_fkbz = "UPDATE INF_CUSTOMERAGREEMENT SET FKBZ = '${FKBZ} WHERE IS_VALID=1 AND USER_NO='${YHBH}'";
		// 将资源添加到系统资源类中等待初始化了参数后再设置
		BusinessResourceInitial res = new BusinessResourceInitial();
		res.setClassName("com.longshine.cams.fk.interfaces.common.SQLConstant");
		res.setFunctionName("initialSystemMode");
		res.setType(TaskAttribute.InitialType.system_mode);
		FKServer.RegisterInitialResource(res);
	}
	/* 根据系统运行的是广东模式，还是省外模式配置查询用户档案语句 */
	public static void initialSystemMode(TaskAttribute.SystemMode v_mode){
		if(v_mode == TaskAttribute.SystemMode.sw){
			sql_infor_dyjm = sw_sql_infor_dyjm;
			sql_infor_dyjm_yhbh = sw_sql_infor_dyjm_yhbh;
			sql_infor_dyjm_yhbh_zcbh = sw_sql_infor_dyjm_yhbh_zcbh;
			sql_infor_gy = sw_sql_infor_gy;
			sql_infor_gy_yhbh = sw_sql_infor_gy_yhbh;
			sql_infor_gy_yhbh_zcbh = sw_sql_infor_gy_yhbh_zcbh;
			
			sql_infor_dyjm_yhbh_in = sw_sql_infor_dyjm_yhbh_in;
			sql_infor_dyjm_yhbh_zcbh_in = sw_sql_infor_dyjm_yhbh_zcbh_in;
			sql_infor_gy_yhbh_in = sw_sql_infor_gy_yhbh_in;
			sql_infor_gy_yhbh_zcbh_in = sw_sql_infor_gy_yhbh_zcbh_in;
			
			sql_dyjmydkh_count = sw_sql_dyjmydkh_count;
			sql_ydkh_count = sw_sql_ydkh_count;
			
			sql_dyjm_update_fkbz = sw_sql_dyjm_update_fkbz;
			sql_gy_update_fkbz = sw_sql_gy_update_fkbz;
		}else{
			sql_infor_dyjm = gd_sql_infor_dyjm;
			sql_infor_dyjm_yhbh = gd_sql_infor_dyjm_yhbh;
			sql_infor_dyjm_yhbh_zcbh = gd_sql_infor_dyjm_yhbh_zcbh;
			sql_infor_gy = gd_sql_infor_gy;
			sql_infor_gy_yhbh = gd_sql_infor_gy_yhbh;
			sql_infor_gy_yhbh_zcbh = gd_sql_infor_gy_yhbh_zcbh;
			
			sql_infor_dyjm_yhbh_in = gd_sql_infor_dyjm_yhbh_in;
			sql_infor_dyjm_yhbh_zcbh_in = gd_sql_infor_dyjm_yhbh_zcbh_in;
			sql_infor_gy_yhbh_in = gd_sql_infor_gy_yhbh_in;
			sql_infor_gy_yhbh_zcbh_in = gd_sql_infor_gy_yhbh_zcbh_in;
			
			sql_dyjmydkh_count = gd_sql_dyjmydkh_count;
			sql_ydkh_count = gd_sql_ydkh_count;

			sql_dyjm_update_fkbz = gd_sql_dyjm_update_fkbz;
			sql_gy_update_fkbz = gd_sql_gy_update_fkbz;
		}
		if(logger.isDebugEnabled())
			logger.debug("SystemMode:" + v_mode + ";sql_infor_gy:\r\n" + sql_infor_gy);
	}
}
