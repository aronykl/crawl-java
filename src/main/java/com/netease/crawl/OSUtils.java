package com.netease.crawl;

public class OSUtils {
	
	public static boolean isWin(){
		String os = System.getProperty("os.name");  
		boolean isWindows = false;
		if(os.toLowerCase().startsWith("win")){  
		   isWindows = true;
		}
		return isWindows;
	}
}
