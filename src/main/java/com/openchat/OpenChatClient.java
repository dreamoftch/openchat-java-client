package com.openchat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import tigase.jaxmpp.core.client.xmpp.modules.muc.Room;
import tigase.jaxmpp.core.client.xmpp.modules.roster.RosterItem;
import tigase.jaxmpp.core.client.xmpp.stanzas.Presence;

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
        ApplicationContext context =
                  new AnnotationConfigApplicationContext(OpenChatClient.class);

        XMPPClient xmppClient = context.getBean(XMPPClient.class);
        xmppClient.login();

        Thread.sleep(3000);  // give XMPP enough time to initialize

        // get all my buddies
        for (RosterItem item : xmppClient.getJaxmpp().getRoster().getAll()) {
            log.info(item.getName() + " subscription " + item.getSubscription().name());
        }

        // example for sending a chat message
//        ChatService chatService = context.getBean(ChatService.class);
//        chatService.sendMessage("steve@openfin.co", "Hello from Java app");

        // example of adding a roster to default group
//        RosterService rosterService = context.getBean(RosterService.class);
//        rosterService.addBuddy("wenjun@openfin.co");

        // example of updating presence
//        PresenceService presenceService = context.getBean(PresenceService.class);
//        presenceService.updatePresence(Presence.Show.away, "Watching Game of Thrones");
//        Thread.sleep(3000);
//        presenceService.updatePresence(Presence.Show.online, "Working on OpenChat");

        // example of joining a room
//        RoomService roomService = context.getBean(RoomService.class);
//        String roomName = "idid8ak_0$openfin";
//        roomService.join(roomName, "The quiet one");
//        Thread.sleep(2000);
//        Room room = roomService.getRoom(roomName);
//        if (room.getState().equals(Room.State.joined)) {
//            room.sendMessage("I am in the room!");
//            Thread.sleep(2000);
//            roomService.leave(roomName);
//        } else {
//            log.error("Still not in the room");
//        }
    }

}
