package com.openchat.impl;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import com.openchat.RoomService;
import com.openchat.constants.Constant;
import com.openchat.utils.XMPPUtil;

import tigase.jaxmpp.core.client.AsyncCallback;
import tigase.jaxmpp.core.client.JID;
import tigase.jaxmpp.core.client.XMPPException.ErrorCondition;
import tigase.jaxmpp.core.client.exceptions.JaxmppException;
import tigase.jaxmpp.core.client.observer.Listener;
import tigase.jaxmpp.core.client.xml.DefaultElement;
import tigase.jaxmpp.core.client.xml.Element;
import tigase.jaxmpp.core.client.xml.XMLException;
import tigase.jaxmpp.core.client.xmpp.modules.chat.ChatState;
import tigase.jaxmpp.core.client.xmpp.modules.chat.MessageModule.AbstractMessageEvent;
import tigase.jaxmpp.core.client.xmpp.modules.muc.MucModule;
import tigase.jaxmpp.core.client.xmpp.modules.muc.MucModule.InvitationEvent;
import tigase.jaxmpp.core.client.xmpp.modules.muc.MucModule.MucEvent;
import tigase.jaxmpp.core.client.xmpp.modules.muc.Role;
import tigase.jaxmpp.core.client.xmpp.modules.muc.Room;
import tigase.jaxmpp.core.client.xmpp.stanzas.IQ;
import tigase.jaxmpp.core.client.xmpp.stanzas.Stanza;
import tigase.jaxmpp.core.client.xmpp.stanzas.StanzaType;

/**
 * Implementation of RoomService.
 *
 * @author wche
 * @since 9/23/14
 */
@Component
public class RoomServiceImpl extends BaseServiceImpl implements RoomService, Listener<AbstractMessageEvent> {

    private MucModule mucModule;

    @Override
    public void afterPropertiesSet() throws Exception {
        this.mucModule =  this.xmppClient.getModuleManager().getModule(MucModule.class);
        this.mucModule.addListener(this);
    }

    @Override
    public void handleEvent(AbstractMessageEvent event) throws JaxmppException {
    	if(event instanceof MucModule.MucEvent){
    		MucEvent mucEvent = (MucEvent)event;
    		handleMucEvent(mucEvent);
    	}else if(event instanceof InvitationEvent){
    		InvitationEvent invitationEvent = (InvitationEvent)event;
    		handleInvitationEvent(invitationEvent);
    	}else{
    		System.err.println("AbstractMessageEvent:" + event.getType() + ", message: " + event.getMessage());
    	}
        
    }
    
    private void handleInvitationEvent(InvitationEvent invitationEvent) {
    	log.info(String.format("invitationEvent, roomId: %s, InviterJID: %s, InvitationType: %s", 
    			invitationEvent.getRoomJID().toString(), invitationEvent.getInviterJID().toString(), invitationEvent.getInvitationType()));
	}

	private void handleMucEvent(MucModule.MucEvent mucEvent) throws XMLException{
    	if (MucModule.NewRoomCreated.equals(mucEvent.getType())) {
            log.info("NewRoomCreated, room jid:" + mucEvent.getRoom().getRoomJid() + ", room nickName:" + mucEvent.getRoom().getNickname());
            
            Room room = mucEvent.getRoom();
            //把room设置为members-only
            configMembersOnlyRoom(room);
            //向该room添加member
            addMembers(room, Arrays.asList("chaohui"));
        }
        else if (MucModule.InvitationReceived.equals(mucEvent.getType())) {
            log.info("InvitationReceived " + mucEvent.getRoom().getNickname());
        }
        else if (MucModule.OccupantComes.equals(mucEvent.getType())) {
            log.info("OccupantComes " + mucEvent.getRoom().getNickname() + " Occupant " + mucEvent.getOccupant().getNickname());
        }
        else if (MucModule.OccupantLeaved.equals(mucEvent.getType())) {
            log.info("OccupantLeaved " + mucEvent.getRoom().getNickname() + " Occupant " + mucEvent.getOccupant().getNickname());
        }
        else if (MucModule.MucMessageReceived.equals(mucEvent.getType())) {
            List<Element> states = mucEvent.getMessage().getChildrenNS(ChatState.XMLNS);
            String body = mucEvent.getMessage().getBody();
            if (states.size() > 0) {
                ChatState chatState = ChatState.fromElement(states.get(0));
                log.info("ChatState in room" + mucEvent.getRoom().getNickname() + " from " + mucEvent.getNickname() + " state " + chatState);
            }
            else if (body != null) {
                log.info("Chat in room " + mucEvent.getRoom().getRoomJid() + " from " + mucEvent.getNickname() + " body " + body);
            }
        }
        else if (MucModule.OccupantChangedPresence.equals(mucEvent.getType())) {
            // chat state update
            log.info("OccupantChangedPresence " + mucEvent.getRoom().getRoomJid() + " Occupant " + mucEvent.getOccupant().getNickname() +
                    " state " + mucEvent.getOccupant().getChatState());
        }
        else if (MucModule.StateChange.equals(mucEvent.getType())) {
            log.info("StateChange " + mucEvent.getRoom().getRoomJid() + " State " + mucEvent.getRoom().getState().name() + ", occupant:" + mucEvent.getNickname());
        }
        else if (MucModule.JoinRequested.equals(mucEvent.getType())) {
            log.info("JoinRequested " + mucEvent.getRoom().getRoomJid());
        }
        else if (MucModule.RoomClosed.equals(mucEvent.getType())) {
            log.info("RoomClosed " + mucEvent.getRoom().getRoomJid());
        }
        else {
            log.warn("Ignoring MucEvent " + mucEvent.getType());
        }
    }

    @Override
    public void join(String roomName, String nickName) {
        try {
            log.info("join " + roomName);
            this.mucModule.join(roomName, this.xmppClient.getMucDomain(), nickName);
        } catch (JaxmppException e) {
            log.error("Error join room", e);
        }
    }

    @Override
    public void leave(String roomName) {
        log.info("Leaving " + roomName);
        Room room = this.getRoom(roomName);
        if (room != null) {
            try {
                this.mucModule.leave(room);
            } catch (JaxmppException e) {
                log.error("Error leaving room", e);
            }
        } else {
            log.error("Room not exist " + roomName);
        }
    }
    
    @Override
    public Room getRoom(String roomName) {
        Room value = null;
        String roomJid = this.xmppClient.getRoomJidFromName(roomName);
        for (Room room : this.mucModule.getRooms()) {
            if (room.getRoomJid().toString().equals(roomJid)) {
                value = room;
                break;
            }
        }
        return value;
    }

    @Override
    public void invite(Room room, String inviteeJID, String reason){
    	try {
			this.mucModule.invite(room, JID.jidInstance(XMPPUtil.getBareJID(inviteeJID)), reason);
		} catch (JaxmppException e) {
			e.printStackTrace();
		}
    }
    
    @Override
    public void destroy(Room room){
    	try {
			this.mucModule.destroy(room);
		} catch (JaxmppException e) {
			e.printStackTrace();
		}
    }
    
	/**
	 * 禁言用户
	 */
    @Override
	public void change2Visitor(String roomName, String admin, String visitor){
		try {
			IQ iq = IQ.create();
			iq.setAttribute("to", Constant.RICHMJ_COMPONNET_JID);
			iq.setAttribute(Constant.RICHMJ_STANZA_FROM, XMPPUtil.getBareJID(admin));
			iq.setAttribute(Constant.RICHMJ_STANZA_TO, XMPPUtil.getRoomJID(roomName));
			iq.setType(StanzaType.set);
			Element query = new DefaultElement("query", null, "http://jabber.org/protocol/muc#admin");
			Element item = new DefaultElement("item", null, null);
			item.setAttribute("nick", XMPPUtil.getBareName(visitor));
			item.setAttribute("role", Role.visitor.name());
			query.addChild(item);
			iq.addChild(query);
			send(iq);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    
    @Override
    public void configMembersOnlyRoom(Room room){
//    	<iq from='crone1@shakespeare.lit/desktop' id='create2' to='coven@chat.shakespeare.lit' type='set'>
//		  <query xmlns='http://jabber.org/protocol/muc#owner'>
//		    <x xmlns='jabber:x:data' type='submit'>
//		      <field var='FORM_TYPE'>
//		        <value>http://jabber.org/protocol/muc#roomconfig</value>
//		      </field>
//		    </x>
//		  </query>
//		</iq>
    	try {
			IQ iq = iq(room.getRoomJid().toString(), StanzaType.set);
			Element query = new DefaultElement("query", null, "http://jabber.org/protocol/muc#owner");
			Element x = new DefaultElement("x", null, "jabber:x:data");
			x.setAttribute("type", "submit");
			x.addChild(field("FORM_TYPE", "http://jabber.org/protocol/muc#roomconfig"));
			x.addChild(field("muc#roomconfig_membersonly", "1"));
			query.addChild(x);
			iq.addChild(query);
			log.info("membersOnly:" + iq.getAsString());
			System.err.println("membersOnly:" + iq.getAsString());
			send(iq);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    @Override
    public void addMembers(Room membersOnlyRoom, List<String> members){
    	addMembers(membersOnlyRoom.getRoomJid().toString(), members);
    }
    
    @Override
    public void addMembers(String roomName, List<String> members){
		if(CollectionUtils.isEmpty(members)){
			return;
		}
		try {
			IQ iq = iq(XMPPUtil.getRoomJID(roomName), StanzaType.set);
			Element query = new DefaultElement("query", null, "http://jabber.org/protocol/muc#admin");
			iq.addChild(query);
			for(String member : members){
				Element item = new DefaultElement("item", null, null);
				item.setAttribute("affiliation", "member");
				item.setAttribute("jid", XMPPUtil.getBareJID(member));
				query.addChild(item);
			}
			log.info("addMembers:" + iq.getAsString());
			send(iq);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    @Override
    public void getRooms(){
    	System.out.println("all rooms: " + this.mucModule.getRooms());
    }
    
    private IQ iq(String to, StanzaType type) throws XMLException{
    	IQ iq = IQ.create();
		iq.setAttribute("to", to);
		iq.setType(type);
		return iq;
    }
    
    private Element field(String fieldName, String value) throws XMLException{
    	Element field = new DefaultElement("field", null, null);
		setAttribute(field, "var", fieldName);
		field.addChild(element("value", value, null));
		return field;
    }
    
    private Element element(String name, String value, String xmlns){
    	return new DefaultElement(name, value, xmlns);
    }
    
    private void setAttribute(Element element, String attrName, String attrValue) throws XMLException{
    	element.setAttribute(attrName, attrValue);
    }

	@Override
	public void joinMembersOnlyRoom(String roomName, String creator) {
		try {
			xmppClient.getJaxmpp().send(XMPPUtil.joinRoomStanza(roomName, creator), new AsyncCallback() {
				@Override
				public void onTimeout() throws JaxmppException {
					log.info("onTimeout");
				}
				@Override
				public void onSuccess(Stanza responseStanza) throws JaxmppException {
					log.info("onSuccess:" + responseStanza.getAsString());
				}
				@Override
				public void onError(Stanza responseStanza, ErrorCondition error) throws JaxmppException {
					log.error("onError:" + responseStanza.getAsString());
				}
			});
		} catch (Exception e) {
			log.error("joinMembersOnlyRoom异常：", e);
			e.printStackTrace();
		}
	}
	
	@Override
	public void richMJMemberOnlyRoon(String roomName, String username, List<String> members){
		try {
			//首先代表用户去创建房间
			send(XMPPUtil.joinRoomStanza(roomName, username));
			//等待一会，确保该房间已经创建成功
			Thread.sleep(200);
			//设置房间为member-only
			send(memberOnlyStanza(roomName, username));
			//添加member
			addMembers(roomName, members);
		} catch (Exception e) {
			log.error("richMJMemberOnlyRoon异常：", e);
			e.printStackTrace();
		}
	}
	
	/**
	 * 配置member-only room的stanza
	 * @param roomName
	 * @param username
	 * @return
	 * @throws XMLException
	 */
	private IQ memberOnlyStanza(String roomName, String username) throws XMLException{
		IQ iq = iq(XMPPUtil.getRoomJID(roomName), StanzaType.set);
		iq.setAttribute("to", Constant.RICHMJ_COMPONNET_JID);
		iq.setAttribute(Constant.RICHMJ_STANZA_FROM, XMPPUtil.getBareJID(username));
		iq.setAttribute(Constant.RICHMJ_STANZA_TO, XMPPUtil.getRoomJID(roomName));
		Element query = new DefaultElement("query", null, "http://jabber.org/protocol/muc#owner");
		Element x = new DefaultElement("x", null, "jabber:x:data");
		x.setAttribute("type", "submit");
		x.addChild(field("FORM_TYPE", "http://jabber.org/protocol/muc#roomconfig"));
		x.addChild(field("muc#roomconfig_membersonly", "1"));
		query.addChild(x);
		iq.addChild(query);
		log.info("membersOnly:" + iq.getAsString());
		return iq;
	}
	
	/**
	 * 向服务端发送stanza
	 * @param stanza
	 * @throws XMLException
	 * @throws JaxmppException
	 */
	private void send(Stanza stanza) throws XMLException, JaxmppException{
		log.info("开始发送stanza:" + stanza.getAsString());
		//如果是iq类型的stanza，则等待响应
		if("iq".equalsIgnoreCase(stanza.getName())){
			xmppClient.getJaxmpp().send(stanza, new AsyncCallback() {
				@Override
				public void onTimeout() throws JaxmppException {
					log.info("onTimeout");
				}
				@Override
				public void onSuccess(Stanza responseStanza) throws JaxmppException {
					log.info("onSuccess:" + responseStanza.getAsString());
				}
				@Override
				public void onError(Stanza responseStanza, ErrorCondition error) throws JaxmppException {
					log.error("onError:" + responseStanza.getAsString());
				}
			});
			return;
		}
		xmppClient.getJaxmpp().send(stanza);
	}

}
