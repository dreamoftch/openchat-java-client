package com.openchat.impl;

import com.openchat.RoomService;
import org.springframework.stereotype.Component;
import tigase.jaxmpp.core.client.exceptions.JaxmppException;
import tigase.jaxmpp.core.client.observer.Listener;
import tigase.jaxmpp.core.client.xml.Element;
import tigase.jaxmpp.core.client.xmpp.modules.chat.ChatState;
import tigase.jaxmpp.core.client.xmpp.modules.muc.MucModule;
import tigase.jaxmpp.core.client.xmpp.modules.muc.Room;

import java.util.List;

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
    public void handleEvent(MucModule.MucEvent be) throws JaxmppException {
        if (MucModule.NewRoomCreated.equals(be.getType())) {
            log.info("NewRoomCreated " + be.getRoom().getNickname());
        }
        else if (MucModule.InvitationReceived.equals(be.getType())) {
            log.info("InvitationReceived " + be.getRoom().getNickname());
        }
        else if (MucModule.OccupantComes.equals(be.getType())) {
            log.info("OccupantComes " + be.getRoom().getNickname() + " Occupant " + be.getOccupant().getNickname());
        }
        else if (MucModule.OccupantLeaved.equals(be.getType())) {
            log.info("OccupantLeaved " + be.getRoom().getNickname() + " Occupant " + be.getOccupant().getNickname());
        }
        else if (MucModule.MucMessageReceived.equals(be.getType())) {
            List<Element> states = be.getMessage().getChildrenNS(ChatState.XMLNS);
            String body = be.getMessage().getBody();
            if (states.size() > 0) {
                ChatState chatState = ChatState.fromElement(states.get(0));
                log.info("ChatState in room" + be.getRoom().getNickname() + " from " + be.getNickname() + " state " + chatState);
            }
            else if (body != null) {
                log.info("Chat in room " + be.getRoom().getRoomJid() + " from " + be.getNickname() + " body " + body);
            }
        }
        else if (MucModule.OccupantChangedPresence.equals(be.getType())) {
            // chat state update
            log.info("OccupantChangedPresence " + be.getRoom().getRoomJid() + " Occupant " + be.getOccupant().getNickname() +
                    " state " + be.getOccupant().getChatState());
        }
        else if (MucModule.StateChange.equals(be.getType())) {
            log.info("StateChange " + be.getRoom().getRoomJid() + " State " + be.getRoom().getState().name());
        }
        else if (MucModule.JoinRequested.equals(be.getType())) {
            log.info("JoinRequested " + be.getRoom().getRoomJid());
        }
        else if (MucModule.RoomClosed.equals(be.getType())) {
            log.info("RoomClosed " + be.getRoom().getRoomJid());
        }
        else {
            log.warn("Ignoring MucEvent " + be.getType());
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

}
