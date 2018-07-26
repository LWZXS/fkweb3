package com.longshine.cams.fk.server.mmj;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
public class MMJStorageUtil
{
	public static Map<String,String> mmjMap = new ConcurrentHashMap<String,String> ();

	public static void addMsg(String info, String key)
	{
			mmjMap.put(key, info);
	}

	public static void cleanMsg(String key)
	{
		mmjMap.remove(key);
	}

	public static synchronized String getMsg(String key)
	{
		String msg = mmjMap.get(key);
		if(msg!=null){
			mmjMap.remove(key);
//			System.out.println("==move key==:"+key);
		}
		return msg;
	}

	
	public static int getSize()
	{
		return mmjMap.size();
	}
	
}
