package com.openchat;

/**
 * Service interface for one to one chat.
 *
 * @author wche
 * @since 9/22/14
 */
public interface ChatService {

    /**
     * Send a chat message to an user
     * @param to receiving user
     * @param message chat message
     */
    public void sendMessage(String to, String message);

}
