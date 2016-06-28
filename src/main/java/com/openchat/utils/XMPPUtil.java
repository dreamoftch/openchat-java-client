package com.openchat.utils;

import org.apache.commons.lang3.StringUtils;

import com.openchat.constants.Constant;

import tigase.jaxmpp.core.client.xml.DefaultElement;
import tigase.jaxmpp.core.client.xml.Element;
import tigase.jaxmpp.core.client.xml.XMLException;
import tigase.jaxmpp.core.client.xmpp.stanzas.Presence;
import tigase.jaxmpp.core.client.xmpp.stanzas.Stanza;

public final class XMPPUtil {
	/**
	 * 聊天服务器地址
	 */
	public static String SERVER = null;
	/**
	 * 系统账号
	 */
	public static String SYSTEM_USERNAME = null;

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
	
	/**
	 * 获取房间JID
	 * @param name
	 * @return
	 */
	public static String getRoomJID(String roomName){
		if(StringUtils.isBlank(roomName)){
			throw new RuntimeException("roomName为空");
		}
		if(SERVER == null){
			throw new RuntimeException("SERVER为空");
		}
		String suffix = "@muc." + SERVER;
		if(roomName.endsWith(suffix)){
			return roomName;
		}
		return roomName + suffix;
	}
	
	/**
	 * 获取系统账号bare jid
	 * @return
	 */
	public static String getSystemUsernameBareJID(){
		if(SYSTEM_USERNAME == null){
			throw new RuntimeException("SYSTEM_USERNAME为空");
		}
		return getBareJID(SYSTEM_USERNAME);
	}
	
	/**
	 * 获取系统账号bare name
	 * @return
	 */
	public static String getSystemUsernameBareName(){
		if(SYSTEM_USERNAME == null){
			throw new RuntimeException("SYSTEM_USERNAME为空");
		}
		return getBareName(SYSTEM_USERNAME);
	}
	
	/**
	 * 进入房间的stanza
	 * @param roomName
	 * @param creator
	 * @return
	 */
	public static Stanza joinRoomStanza(String roomName, String creator) {
		Presence presence = null;
		try {
			presence = Presence.create();
			presence.setAttribute("to", Constant.RICHMJ_COMPONNET_JID);
			presence.setAttribute(Constant.RICHMJ_STANZA_FROM, getBareJID(creator));
			// 格式：roomJID/nickName
			presence.setAttribute(Constant.RICHMJ_STANZA_TO, getRoomJID(roomName) + "/" + getBareName(creator));
			Element x = new DefaultElement("x", null, "http://jabber.org/protocol/muc");
			presence.addChild(x);
			System.out.println("presence:" + presence.getAsString());
		} catch (XMLException e) {
			e.printStackTrace();
		}
		return presence;
	}
	
	public static void main(String[] args) {
		SERVER = "192.168.43.146";
		System.out.println(getBareName("chaohui"));
		System.out.println(getBareName("chaohui@192.168.43.146"));
		System.out.println(getBareJID("chaohui"));
		System.out.println(getBareJID("chaohui@192.168.43.146"));
	}

}
