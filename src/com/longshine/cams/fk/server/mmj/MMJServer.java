package com.longshine.cams.fk.server.mmj;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/*分别对数据表建立对应处理类，原文件-->临时文件夹/原文件+后缀名
 * 
 * 出错文件夹+临时文件夹+取数文件夹
 * 
 * 临时文件夹
 *    +--------- 前缀+YYYYMM文件夹
 *           +----------- 前缀+YYYYMMDD文件夹
 *            		  +----------- 文件
 *      
 */
public class MMJServer {
	private final static Log logger = LogFactory.getLog(MMJServer.class);
	//#接收临时文件后缀名
	private String chgExtName=MMJConstant.chgExtName;//tmp
	//#出错临时文件后缀名
	private String errExtName=MMJConstant.errExtName;//"error";
	//数据文件夹dirPath
	private String dirPath=MMJConstant.dirPath;//"E:\\tmpfk";
	//#转存接收临时文件目录
	private String dirdesPath=MMJConstant.dirdesPath;//"E:\\tmpfk";
	//#处理接收出错文件目录
	public String direrrorPath =MMJConstant.direrrorPath;//"E:\\tmpfk";
	//临时文件夹前缀
	private String folderPreName=MMJConstant.folderPreName; //"MMJ";
	//# 原始文件
	public final String srcFileDel = MMJConstant.srcFileDel; //"1";
	//#装载后是否删除原文件及中间文件，1-删除，0-保留
	//# 中间过程文件
	public final String midFileDel =MMJConstant.midFileDel; //"0";
	//文件名过滤
	private MMJFileNameSelector mmjfileNameSelector;

	//日期属性格式
	private static final ThreadLocal<SimpleDateFormat> sd2 = new ThreadLocal<SimpleDateFormat>(){
		@Override
		protected SimpleDateFormat initialValue() {
			return  new SimpleDateFormat("yyyyMMdd");
		}
	};
		
	public MMJFileNameSelector getMmjfileNameSelector() {
		return mmjfileNameSelector;
	}

	public void setMmjfileNameSelector(MMJFileNameSelector mmjfileNameSelector) {
		this.mmjfileNameSelector = mmjfileNameSelector;
	}

	public String getDirPath() {
		return dirPath;
	}

	public void setDirPath(String dirPath) {
		this.dirPath = dirPath;
	}

	public MMJServer(MMJFileNameSelector mmjfileNameSelector) {
		super();
		this.mmjfileNameSelector = mmjfileNameSelector;
	}

	public MMJServer() {
	}

		public synchronized void consumer() throws Exception{
			//数据文件夹dirPath
			if(this.dirPath==null || "".equals(this.dirPath)){
				logger.error("MMJ读取目录路径为空。");
				return;
			}
			
			File directory = new File(this.dirPath);
			//列出所有文件
			File[] files = directory.listFiles();
			if(files!=null){
				//列出所有过滤文件
				File[] filterFiles = directory.listFiles(mmjfileNameSelector);
				// 保存到临时目录 ==> /前缀+数据表KEY+YYYYMM/前缀+数据表KEY+YYYYMMDD/入库文件
				String tempPath=this.dirdesPath + File.separator +  this.folderPreName+ getCurrentDay().substring(0,6)+File.separator+ this.folderPreName+getCurrentDay();
				// 保存到出错目录 ==> /前缀+数据表KEY+YYYYMM/前缀+数据表KEY+YYYYMMDD/入库文件
				String errorPath=this.direrrorPath + File.separator +  this.folderPreName+ getCurrentDay().substring(0,6)+File.separator+ this.folderPreName+getCurrentDay();
				//移动文件+改名+缓存
				consumerFile(filterFiles,tempPath,errorPath);
			}else{
				logger.debug("MMJ读取JSON返回结果==>目录"+directory.getName()+"下没有要操作的文件.");
			}
			
		} 

			/*
			 * 改名+移动文件+缓存
			 */
		private void consumerFile(File[] files, String tempPath,String errorPath) throws Exception{
				int ret=0;
				List<String> list=null;
				String newFileName=null;
				String orgFileName=null;
				//是否删除原文件，1-删除，0-保留
				boolean isMoveFile=false;
				if("1".equals(srcFileDel)){
		    		isMoveFile=true;
		    	}
				//临时文件夹
				tempPath=tempPath+File.separator ;	
				errorPath=errorPath+File.separator;
				for (File file : files) {
					if(MMJFkUtil.isFileExists(file)==true){
						//改名+移动文件==>返回 : 0成功,1重命名文件不存在,2已经存在同名新文件
						orgFileName=file.getName();
						newFileName=orgFileName+"."+chgExtName;		
		            	ret=reNameTmpFile(file,newFileName,tempPath,isMoveFile);	
						//logger.info("MMJServer移动文件结果( "+ getReturnCnValue(ret) +" ) "+tempPath+file.getName());
						if(ret==0) {
							//缓存读文件内容
							File newFile=new File(tempPath+newFileName); 					
							list = readFileToList(newFile);
							//行记录转成输出上传文件顺序格式对应字符串行
							if(list!=null && list.size()>0){
								StringBuffer sb=new StringBuffer();
								try {
									for (String s : list) {
										sb.append(s);
									}
									//放入缓存
									pushStorage(sb.toString(),file.getName());
									
								//装载后是否删除原文件及中间文件，1-删除，0-保留 
								if("1".equals(midFileDel)){
									    logger.info("MMJServer ==>删除中间文件："+newFileName);
										newFile.delete();
								}
								} catch (Exception e) {
									//e.printStackTrace();
									logger.error("MMJServer缓存文件异常："+e.getMessage()+" 文件名称："+newFile.getAbsolutePath() +" ERR:"+MMJFkUtil.getExceptionInfo(e));
									File errFile=new File(errorPath+ orgFileName);
									//
									ret=reNameTmpFile(errFile,orgFileName+"."+errExtName,errorPath,true);	
									logger.error("MMJServer缓存文件异常改名为"+errExtName+"结尾( "+getReturnCnValue(ret)+" ) "+errorPath+errFile.getName());
								}
							}else{
								logger.info("MMJServer此文本为空不入缓存！"+newFile.getAbsolutePath());
							}

						}//end if(ret==0)
						else{
							logger.info("MMJServer移动文件失败("+getReturnCnValue(ret)+" | "+tempPath+file.getName());
						}
					}//end if( file exist)
				}//end for
			
		}
		
		private void pushStorage(String info,String key) {
				JSONObject jsonObject = JSONObject.fromObject(info);
//				logger.info("MMJServer ==> storage start  size : "+ MMJStorageUtil.getSize());
				MMJStorageUtil.addMsg(info, jsonObject.getString("taskId"));
				logger.info("MMJServer ==> storage  size : "+ MMJStorageUtil.getSize()+" |info:"+ info+" |key:"+jsonObject.getString("taskId")+" |fileName:"+key);
//				System.out.println("MMJServer ==> storage end  size : "+ MMJStorageUtil.getSize()+" key:"+jsonObject.getString("taskId"));;
		}

		public String getCurrentDay(){
	    	return sd2.get().format(new Date()) ;
	    }
		
		//移动文件结果 ==>0成功,1不存在,2同名
		private String getReturnCnValue(int ret){
			String returnVal="";
			switch(ret){
			case 0:
				returnVal="成功";
				break;
			case 1:
				returnVal="不存在";
				break;
			case 2:
				returnVal="同名";
				break;
			default:
				returnVal="未知";
				
			}
			return returnVal;
		}
		
		
		
		/**
	     * 读入文件内容到列表中
	     * @param file
	     * @return  null:文件不存在 
	     */
	    @SuppressWarnings("finally")
		public List<String> readFileToList(File file){
	    	List<String> list=new ArrayList<String>();
	    	 BufferedReader buffReader=null;
	        try {
	        	if(!file.exists()){
	        		logger.info("MMJServer ==>读取文件不存在."+file.getAbsolutePath());
	                return null;
	            }
	            //1、创建流对象构建高效流对象
	        	buffReader=new BufferedReader(new InputStreamReader(new FileInputStream(file.getCanonicalPath()),"GBK")); 
	             
	            //2、读取一行字符串
	            String line=buffReader.readLine();
	            
	            while(line!=null){
	            	if(!"".endsWith(line)){
	            		list.add(line);
	            	}
	                line=buffReader.readLine();
	            }
	        } catch (FileNotFoundException e) {
	            logger.error("MMJServer ==>文件不存在："+e.getMessage());
	        } catch (IOException e) {
	        	logger.error("MMJServer ==>文件IO读取错误："+e.getMessage());
	        } catch (Exception e) {
	        	logger.error("MMJServer ==>异常错误："+e.getMessage()+" ERR:"+MMJFkUtil.getExceptionInfo(e));
	        }finally{
	        	 //3、关闭流
	            try {
	            	if(buffReader!=null){
	            		buffReader.close();
	            	}
				} catch (IOException e) {
					logger.error("MMJServer ==>关闭流异常错误："+e.getMessage());
				}
	        	return list;
	        }
	    }
		
		/**
		 * 原文件改名,并移动到 目的路径/YYYYMM/YYYYMMDD/目录下
		 * @param oldfile
		 * @param newname
		 * @return 0成功,1重命名原文件不存在,2已经存在同名新文件
		 */
	    public int reNameTmpFile(File oldFile,String newName,String path,boolean isMoveFile) throws Exception{ 
	    	   String newFileName=null;
	    		if(oldFile!=null){
	    			newFileName=path+newName;
	    		}else{
	    			logger.info("MMJServer==>原文件为NULL,转移文件不成功。"+newFileName);
	    			return 1;//文件不存在
	    		}
	    		
	    		 if(!oldFile.exists()){
		            	logger.info("MMJServer ==>原文件不存在,移文件不成功。"+oldFile.getAbsolutePath());
		                return 1;//重命名文件不存在
		            }
	    	    
	    	   //获取文件夹路径
	    	     File tmpFile = new File(path);	    	     
	    	   //判断文件夹是否创建，没有创建则创建新文件夹
	    	     if(!tmpFile.exists()){
	    	            tmpFile.mkdirs();
	    	        }
	           
	            File newFile=new File(newFileName); 
	            
	            if(newFile.exists()){//若在该目录下已经有一个文件和新文件名相同，则不允许重命名 
	            	newFile.delete();
	            	logger.info("MMJServer ==>改名文件已经存在！删除旧文件并生成新文件！"+newFileName); 
	            	//return 2;
	            } 

	            	if(isMoveFile==true){
	            		//logger.info("MMJServer ==>移动文件："+oldFile.getName()+"-->"+newFileName);
	            		oldFile.renameTo(newFile); 
	            	}else{
	            		logger.info("MMJServer ==>复制文件："+oldFile.getName()+"-->"+newFileName);
	            		//管道对管道复制
	            		forChannel(oldFile,newFile);
	            	}
	                return 0;
	        }
	    
	    //管道对管道复制
	    private  long forChannel(File srcfile,File desfile) throws Exception{
	        long time=new Date().getTime();
	        int length=2097152;
	        FileInputStream in=new FileInputStream(srcfile);
	        FileOutputStream out=new FileOutputStream(desfile);
	        FileChannel inC=in.getChannel();
	        FileChannel outC=out.getChannel();
	        ByteBuffer b=null;
	        while(true){
	            if(inC.position()==inC.size()){
	                inC.close();
	                outC.close();
	                return new Date().getTime()-time;
	            }
	            if((inC.size()-inC.position())<length){
	                length=(int)(inC.size()-inC.position());
	            }else
	                length=2097152;
	            b=ByteBuffer.allocateDirect(length);
	            inC.read(b);
	            b.flip();
	            outC.write(b);
	            outC.force(false);
	        }
	    }
	    
	    
}
