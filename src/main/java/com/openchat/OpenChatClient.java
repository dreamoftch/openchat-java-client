package com.openchat;

import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import com.openchat.constants.Constant;
import com.openchat.utils.XMPPUtil;

import tigase.jaxmpp.core.client.AsyncCallback;
import tigase.jaxmpp.core.client.XMPPException.ErrorCondition;
import tigase.jaxmpp.core.client.exceptions.JaxmppException;
import tigase.jaxmpp.core.client.xml.DefaultElement;
import tigase.jaxmpp.core.client.xml.Element;
import tigase.jaxmpp.core.client.xml.XMLException;
import tigase.jaxmpp.core.client.xmpp.stanzas.IQ;
import tigase.jaxmpp.core.client.xmpp.stanzas.Stanza;
import tigase.jaxmpp.core.client.xmpp.stanzas.StanzaType;

/**
 * main of this project.
 * @author wche
 * @since 9/22/14
 */

@Configuration
@ComponentScan
@PropertySource("classpath:app.properties")
public class OpenChatClient {
    protected static final Log log = LogFactory.getLog(OpenChatClient.class);

    @Bean
    public static PropertySourcesPlaceholderConfigurer ppc() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    public static void main(String[] argv) throws Exception {
    	AnnotationConfigApplicationContext context =
                  new AnnotationConfigApplicationContext(OpenChatClient.class);
    	
        XMPPClient xmppClient = context.getBean(XMPPClient.class);
        xmppClient.login();

        Thread.sleep(1000);  // give XMPP enough time to initialize
//        xmppClient.getJaxmpp().sendMessage(JID.jidInstance("admin@192.168.43.146"), "Test", "This is a test");
        // get all my buddies
//        for (RosterItem item : xmppClient.getJaxmpp().getRoster().getAll()) {
//            log.info(item.getName() + " subscription " + item.getSubscription().name());
//        }

        RoomService roomService = context.getBean(RoomService.class);
        //roomService.richMJMemberOnlyRoon("richMJMemberOnlyRoon-1", "chaohui", Arrays.asList("richmj"));
        roomService.change2Visitor("richMJMemberOnlyRoon-1", "chaohui", "richmj");
        //roomService.invite(roomService.getRoom("richMJMemberOnlyRoon-1"), "chaohui", "reason...");
        
//        membersOnly(context, "members-only-67");
//        richmjMembersOnly(context, xmppClient, "richmj-chaohui-members-only-11", "chaohui");
        
        // example for sending a chat message
//        ChatService chatService = context.getBean(ChatService.class);
//        chatService.sendMessage("admin@192.168.43.146", "Hello from Java app");

        // example of adding a roster to default group
//        RosterService rosterService = context.getBean(RosterService.class);
//        rosterService.addBuddy("chaohui");

        // example of updating presence
//        PresenceService presenceService = context.getBean(PresenceService.class);
//        presenceService.updatePresence(Presence.Show.away, "Watching Game of Thrones");
//        Thread.sleep(8000);
//        presenceService.updatePresence(Presence.Show.online, "Working on OpenChat");

        // example of joining a room
//        RoomService roomService = context.getBean(RoomService.class);
//        String roomName = "members-only-3";
//        roomService.join(roomName, "richmj");
//        Thread.sleep(200);
//        Room room = roomService.getRoom(roomName);
        /*if (room.getState().equals(Room.State.joined)) {
            room.sendMessage("I am in the room!");
            Thread.sleep(200);
//            roomService.leave(roomName);
        } else {
            log.error("Still not in the room");
        }*/
        //把room设置为members-only
//        roomService.configMembersOnlyRoom(room);
//        //向该room添加member
//        roomService.addMembers(room, Arrays.asList("chaohui"));
        
        
        
//        roomService.invite(room, "chaohui@192.168.43.146", "richmj invited...");
        //roomService.change2Visitor(room, "chaohui");
//        roomService.destroy(room);
//        System.out.println("destroy room...");
        context.close();
    }
    
    public static void membersOnly(AnnotationConfigApplicationContext context, String roomName) throws InterruptedException{
    	RoomService roomService = context.getBean(RoomService.class);
    	//1.创建并进入room
        roomService.join(roomName, "richmj");
        roomService.getRooms();
    }
    
    public static void richmjMembersOnly(AnnotationConfigApplicationContext context, XMPPClient xmppClient, String roomName, String username) throws InterruptedException{
    	RoomService roomService = context.getBean(RoomService.class);
        try {
			xmppClient.getJaxmpp().send(XMPPUtil.joinRoomStanza(roomName, username));
			
			Thread.sleep(200);
			
			IQ iq = iq(XMPPUtil.getRoomJID(roomName), StanzaType.set);
			iq.setAttribute("to", "richmj-component@192.168.43.146");
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
			System.err.println("membersOnly:" + iq.getAsString());
			xmppClient.getJaxmpp().send(iq, new AsyncCallback() {
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
			
			Thread.sleep(200);
			
			roomService.addMembers(roomName, Arrays.asList("chaohui"));
			roomService.addMembers(roomName, Arrays.asList("richmj"));
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    private static IQ iq(String to, StanzaType type) throws XMLException{
    	IQ iq = IQ.create();
		iq.setAttribute("to", to);
		iq.setType(type);
		return iq;
    }
    
    private static Element field(String fieldName, String value) throws XMLException{
    	Element field = new DefaultElement("field", null, null);
		setAttribute(field, "var", fieldName);
		field.addChild(element("value", value, null));
		return field;
    }
    
    private static Element element(String name, String value, String xmlns){
    	return new DefaultElement(name, value, xmlns);
    }
    
    private static void setAttribute(Element element, String attrName, String attrValue) throws XMLException{
    	element.setAttribute(attrName, attrValue);
    }
    
}
