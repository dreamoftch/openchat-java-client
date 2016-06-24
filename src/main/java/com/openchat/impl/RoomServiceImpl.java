package com.openchat.impl;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import com.openchat.RoomService;
import com.openchat.utils.XMPPUtil;

import tigase.jaxmpp.core.client.JID;
import tigase.jaxmpp.core.client.exceptions.JaxmppException;
import tigase.jaxmpp.core.client.observer.Listener;
import tigase.jaxmpp.core.client.xml.DefaultElement;
import tigase.jaxmpp.core.client.xml.Element;
import tigase.jaxmpp.core.client.xml.XMLException;
import tigase.jaxmpp.core.client.xmpp.modules.chat.ChatState;
import tigase.jaxmpp.core.client.xmpp.modules.muc.MucModule;
import tigase.jaxmpp.core.client.xmpp.modules.muc.Role;
import tigase.jaxmpp.core.client.xmpp.modules.muc.Room;
import tigase.jaxmpp.core.client.xmpp.stanzas.IQ;
import tigase.jaxmpp.core.client.xmpp.stanzas.StanzaType;

/**
 * Implementation of RoomService.
 *
 * @author wche
 * @since 9/23/14
 */
@Component
public class RoomServiceImpl extends BaseServiceImpl implements RoomService, Listener<MucModule.MucEvent> {

    private MucModule mucModule;

    @Override
    public void afterPropertiesSet() throws Exception {
        this.mucModule =  this.xmppClient.getModuleManager().getModule(MucModule.class);
        this.mucModule.addListener(this);
    }

    @Override
    public void handleEvent(MucModule.MucEvent event) throws JaxmppException {
        if (MucModule.NewRoomCreated.equals(event.getType())) {
            log.info("NewRoomCreated, room jid:" + event.getRoom().getRoomJid() + ", room nickName:" + event.getRoom().getNickname());
            
            Room room = event.getRoom();
            //把room设置为members-only
            configMembersOnlyRoom(room);
            //向该room添加member
            addMembers(room, Arrays.asList("chaohui"));
        }
        else if (MucModule.InvitationReceived.equals(event.getType())) {
            log.info("InvitationReceived " + event.getRoom().getNickname());
        }
        else if (MucModule.OccupantComes.equals(event.getType())) {
            log.info("OccupantComes " + event.getRoom().getNickname() + " Occupant " + event.getOccupant().getNickname());
        }
        else if (MucModule.OccupantLeaved.equals(event.getType())) {
            log.info("OccupantLeaved " + event.getRoom().getNickname() + " Occupant " + event.getOccupant().getNickname());
        }
        else if (MucModule.MucMessageReceived.equals(event.getType())) {
            List<Element> states = event.getMessage().getChildrenNS(ChatState.XMLNS);
            String body = event.getMessage().getBody();
            if (states.size() > 0) {
                ChatState chatState = ChatState.fromElement(states.get(0));
                log.info("ChatState in room" + event.getRoom().getNickname() + " from " + event.getNickname() + " state " + chatState);
            }
            else if (body != null) {
                log.info("Chat in room " + event.getRoom().getRoomJid() + " from " + event.getNickname() + " body " + body);
            }
        }
        else if (MucModule.OccupantChangedPresence.equals(event.getType())) {
            // chat state update
            log.info("OccupantChangedPresence " + event.getRoom().getRoomJid() + " Occupant " + event.getOccupant().getNickname() +
                    " state " + event.getOccupant().getChatState());
        }
        else if (MucModule.StateChange.equals(event.getType())) {
            log.info("StateChange " + event.getRoom().getRoomJid() + " State " + event.getRoom().getState().name() + ", occupant:" + event.getNickname());
        }
        else if (MucModule.JoinRequested.equals(event.getType())) {
            log.info("JoinRequested " + event.getRoom().getRoomJid());
        }
        else if (MucModule.RoomClosed.equals(event.getType())) {
            log.info("RoomClosed " + event.getRoom().getRoomJid());
        }
        else {
            log.warn("Ignoring MucEvent " + event.getType());
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
			this.mucModule.invite(room, JID.jidInstance(inviteeJID), reason);
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
	public void change2Visitor(Room room, String name){
//		<iq xmlns="jabber:client" type="set" to="open-chat-room-1@muc.192.168.43.146" id="aaeaa">
//		    <query xmlns="http://jabber.org/protocol/muc#admin">
//		        <item nick="chaohui" role="visitor"/>
//		    </query>
//		</iq>
		try {
			IQ iq = IQ.create();
			iq.setAttribute("to", room.getRoomJid().toString());
			iq.setType(StanzaType.set);
			Element query = new DefaultElement("query", null, "http://jabber.org/protocol/muc#admin");
			Element item = new DefaultElement("item", null, null);
			item.setAttribute("nick", name);
			item.setAttribute("role", Role.visitor.name());
			query.addChild(item);
			iq.addChild(query);
			this.xmppClient.getJaxmpp().send(iq);
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
			this.xmppClient.getJaxmpp().send(iq);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    @Override
    public void addMembers(Room membersOnlyRoom, List<String> members){
//    	<iq from='crone1@shakespeare.lit/desktop'
//    		    id='member1'
//    		    to='coven@chat.shakespeare.lit'
//    		    type='set'>
//    		  <query xmlns='http://jabber.org/protocol/muc#admin'>
//    		    <item affiliation='member'
//    		          jid='hag66@shakespeare.lit'
//    		          nick='thirdwitch'/>
//    		  </query>
//    		</iq>
    	if(CollectionUtils.isEmpty(members)){
    		return;
    	}
    	try {
			IQ iq = iq(membersOnlyRoom.getRoomJid().toString(), StanzaType.set);
			Element query = new DefaultElement("query", null, "http://jabber.org/protocol/muc#admin");
			iq.addChild(query);
			for(String member : members){
				Element item = new DefaultElement("item", null, null);
				item.setAttribute("affiliation", "member");
				item.setAttribute("jid", XMPPUtil.getBareJID(member));
				query.addChild(item);
			}
			log.info("addMembers:" + iq.getAsString());
			this.xmppClient.getJaxmpp().send(iq);
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

}
