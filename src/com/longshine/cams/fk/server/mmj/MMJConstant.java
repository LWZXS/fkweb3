package com.longshine.cams.fk.server.mmj;



public class MMJConstant {
		//#临时子文件夹前缀
		public static final String folderPreName = ResourceUtil.getKeyValue("dp.folderprename", "mmj");
		//#接收临时文件后缀名
		public static final String chgExtName = ResourceUtil.getKeyValue("dp.chgextname", "tmp");
		//#出错临时文件后缀名
		public static final String errExtName = ResourceUtil.getKeyValue("dp.errextname", "error");
		//#接收临时文件目录
		public static final String dirPath = ResourceUtil.getKeyValue("dp.dirpath", "E:\\tempfk");
		//#处理接收中间文件目录
		public static final String dirdesPath = ResourceUtil.getKeyValue("dp.dirdespath", "E:\\tempfk");
		//#处理接收出错文件目录
		public static final String direrrorPath = ResourceUtil.getKeyValue("dp.direrrorpath", "E:\\tempfk\\error");
		//#上传文件目录
		public static final String uploadPath = ResourceUtil.getKeyValue("dp.uploadpath", "E:\\tempfk\\sendok");
		//#数据文件存放期限,单位为天，默认3天
		public static final String nDay = ResourceUtil.getKeyValue("dp.nday", "3");
		//#生成上传文件内记录数量
		public static final String popSize = ResourceUtil.getKeyValue("dp.popsize", "1000");
		//#装载后是否删除原文件及中间文件，1-删除，0-保留
		//# 中间过程文件
		public static final String midFileDel = ResourceUtil.getKeyValue("dp.midfiledel", "0");
		//# 原始文件 ，1-删除，0-保留
		public static final String srcFileDel = ResourceUtil.getKeyValue("dp.srcfiledel", "1");
		//#接口生成文件计量远程身份认证前缀名称
		public static final String jlycsfrzpre=ResourceUtil.getKeyValue("dp.jlycsfrzpre", "JLYCSFRZ_");
		//#接口生成文件远程控制前缀名称
		public static final String yckzpre=ResourceUtil.getKeyValue("dp.yckzpre", "YCKZ_");
		//#配置任务执行的线程数量
		public static final String performprocess=ResourceUtil.getKeyValue("dp.performprocess", "1");
		//#费控服务调用密码机WEB服务地址，格式为：http://ip:port/pmis/fkservice
		public static final String mmjserveraddr=ResourceUtil.getKeyValue("dp.mmjserveraddr", "http://127.0.0.1:8082/fkweb/services/");
		//#配置等待返回结果超时时间 120000  120秒
		public static final String timeout=ResourceUtil.getKeyValue("dp.timeout", "120000"); 
		//#配置是否使用隔离文件获取密码机Web服务方式  1是，0否
		public static final String fkmmjws=ResourceUtil.getKeyValue("dp.fkmmjws", "0"); 
		//#配置自调用返回低压费控用户远程控制
		public static final String callserverurl=ResourceUtil.getKeyValue("dp.callserverurl", "http://127.0.0.1:8080/fk2/services/"); 

		private static char getCharByStr(String s,int radix){
			s=s.toUpperCase();
			if(s.startsWith("0X")){
				s=s.substring(2);
			}
			return (char)Integer.parseInt(s, radix);
		}
}
