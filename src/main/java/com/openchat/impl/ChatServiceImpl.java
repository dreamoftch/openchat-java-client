package com.openchat.impl;

import com.openchat.ChatService;
import org.springframework.stereotype.Component;
import tigase.jaxmpp.core.client.JID;
import tigase.jaxmpp.core.client.exceptions.JaxmppException;
import tigase.jaxmpp.core.client.observer.Listener;
import tigase.jaxmpp.core.client.xml.Element;
import tigase.jaxmpp.core.client.xmpp.modules.chat.Chat;
import tigase.jaxmpp.core.client.xmpp.modules.chat.ChatState;
import tigase.jaxmpp.core.client.xmpp.modules.chat.MessageModule;

import java.util.List;

/**
 * Implementation of ChatService.
 *
 * @author wche
 * @since 9/22/14
 */
@Component
public class ChatServiceImpl extends BaseServiceImpl implements ChatService, Listener<MessageModule.MessageEvent> {

    @Override
    public void afterPropertiesSet() throws Exception {
        this.xmppClient.getModuleManager().getModule(MessageModule.class).addListener(this);
    }

    @Override
    public void sendMessage(String to, String message) {
        String jid = this.xmppClient.createOpenChatJid(to);
        try {
            log.info("Sending: " + message + " to " + jid);
            Chat chat = this.xmppClient.getJaxmpp().createChat(JID.jidInstance(jid));
            chat.sendMessage(message);
        } catch (JaxmppException e) {
            log.error(e);
        }
    }

    @Override
    public void handleEvent(MessageModule.MessageEvent be) throws JaxmppException {
        log.info("MessageEvent type " + be.getType());
        if (MessageModule.ChatStateChanged.equals(be.getType())) {
            if (be.getMessage() != null) {
                String from = this.xmppClient.extractUsernameFromJid(be.getMessage().getFrom().getBareJid().toString());
                if (be.getMessage().getChildrenNS(MessageModule.RECEIPTS_XMLNS).size() > 0) {
                    log.info("Receipt from " + from);
                }
                List<Element> states = be.getMessage().getChildrenNS(ChatState.XMLNS);
                if (states.size() > 0) {
                    ChatState chatState = ChatState.fromElement(states.get(0));
                    log.info("ChatState from " + from + " state " + chatState);
                }
            }
        }
        else if (MessageModule.MessageReceived.equals(be.getType())) {
            if (be.getMessage() != null) {
                String from = this.xmppClient.extractUsernameFromJid(be.getMessage().getFrom().getBareJid().toString());
                String body = be.getMessage().getBody();
                if (body != null) {
                    log.info("Chat from " + from + " message " + body);
                }
            }
        }
    }
}
