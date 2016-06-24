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

import tigase.jaxmpp.core.client.xmpp.modules.muc.Room;
import tigase.jaxmpp.core.client.xmpp.modules.roster.RosterItem;

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
        for (RosterItem item : xmppClient.getJaxmpp().getRoster().getAll()) {
            log.info(item.getName() + " subscription " + item.getSubscription().name());
        }

        membersOnly(context, "members-only-11");
        membersOnly(context, "members-only-12");
        
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
    
}
