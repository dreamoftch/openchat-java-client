package com.openchat;

import tigase.jaxmpp.core.client.xmpp.modules.muc.Room;

/**
 * Service interface for multi-user chat rooms.
 * @author wche
 * @since 9/23/14
 */
public interface RoomService {

    /**
     * Join a room
     * @param roomName
     * @param nickName
     */
    public void join(String roomName, String nickName);

    /**
     * Get a room by name
     * @param roomName
     * @return
     */
    public Room getRoom(String roomName);

    /**
     * Leave a room
     * @param roomName
     */
    public void leave(String roomName);

}
