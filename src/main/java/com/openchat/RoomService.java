package com.openchat;


import java.util.List;

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
    
    /**
     * 邀请用户到room
     * @param room
     * @param inviteeJID
     * @param reason
     */
    void invite(Room room, String inviteeJID, String reason);

    /**
     * 删除room
     * @param room
     */
    void destroy(Room room);
    
    /**
     * 禁言用户
     * @param room
     * @param name
     */
    void change2Visitor(String roomName, String admin, String visitor);
    
    /**
     * 设置房间为members-only
     * @param room
     */
    void configMembersOnlyRoom(Room room);
    
    /**
     * 为members-only的房间添加成员
     * @param membersOnlyRoom
     */
    void addMembers(Room membersOnlyRoom, List<String> members);
    
    /**
     * 为members-only的房间添加成员
     * @param membersOnlyRoom
     */
    void addMembers(String roomName, List<String> members);
    
    void getRooms();
    
    /**
     * 加入members-only room
     */
    void joinMembersOnlyRoom(String roomName, String creator);
    
    /**
     * 代表用户username创建member-only的room并且邀请指定的用户加入
     * @param roomName
     * @param username
     * @param memebers
     */
    public void richMJMemberOnlyRoon(String roomName, String username, List<String> members);
}
