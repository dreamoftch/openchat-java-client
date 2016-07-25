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

        Thread.sleep(5000);  // give XMPP enough time to initialize
//        xmppClient.getJaxmpp().sendMessage(JID.jidInstance("chaohui1@192.168.1.120"), "Test", "This is a test");
        // get all my buddies
//        for (RosterItem item : xmppClient.getJaxmpp().getRoster().getAll()) {
//            log.info(item.getName() + " subscription " + item.getSubscription().name());
//        }

        //以指定的用户作为创建人创建团队，并且配置member only，然后邀请指定的成员加入
        RoomService roomService = context.getBean(RoomService.class);
        roomService.richMJMemberOnlyRoon("2016-07-25-4", "136", Arrays.asList("77"));
        //roomService.change2Visitor("richMJMemberOnlyRoon-1", "chaohui", "richmj");
        //roomService.invite(roomService.getRoom("richMJMemberOnlyRoon-1"), "chaohui", "reason...");
        //roomService.richmjDirectInviteUser("richMJMemberOnlyRoon-1", Arrays.asList("chaohui", "admin", "richmj"));
        
//        membersOnly(context, "members-only-67");
//        richmjMembersOnly(context, xmppClient, "richmj-chaohui-members-only-11", "chaohui");
        
        // 发送聊天信息
//        ChatService chatService = context.getBean(ChatService.class);
//        chatService.sendMessage("136@192.168.1.120", "Hello from Java app");

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
        Thread.sleep(2000);
        xmppClient.getJaxmpp().disconnect();
        context.close();
    }
    
    
    
}
