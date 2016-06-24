package com.openchat.utils;

import org.apache.commons.lang3.StringUtils;

public final class XMPPUtil {
	
	public static String SERVER = null;

	private XMPPUtil(){}
	
	/**
	 * 获取bareJID
	 * @param name
	 * @return
	 */
	public static String getBareJID(String name){
		if(StringUtils.isBlank(name)){
			throw new RuntimeException("name为空");
		}
		if(SERVER == null){
			throw new RuntimeException("SERVER为空");
		}
		String suffix = "@" + SERVER;
		if(name.endsWith(suffix)){
			return name;
		}
		return name + suffix;
	}
	
	/**
	 * 获取bareName，不含hostName
	 * @param name
	 * @return
	 */
	public static String getBareName(String name){
		if(StringUtils.isBlank(name)){
			throw new RuntimeException("name为空");
		}
		if(SERVER == null){
			throw new RuntimeException("SERVER为空");
		}
		String suffix = "@" + SERVER;
		if(name.endsWith(suffix)){
			return name.substring(0, name.length() - (suffix).length());
		}
		return name;
	}
	
	public static void main(String[] args) {
		SERVER = "192.168.43.146";
		System.out.println(getBareName("chaohui"));
		System.out.println(getBareName("chaohui@192.168.43.146"));
		System.out.println(getBareJID("chaohui"));
		System.out.println(getBareJID("chaohui@192.168.43.146"));
	}

}
