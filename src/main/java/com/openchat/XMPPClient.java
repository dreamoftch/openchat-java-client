package com.openchat;

import tigase.jaxmpp.core.client.XmppModulesManager;
import tigase.jaxmpp.j2se.Jaxmpp;

/**
 * Client interface for Tigase jaxmpp library.
 * @author wche
 * @since 9/22/14
 */
public interface XMPPClient {

    public void login() throws Exception;

    public XmppModulesManager getModuleManager();
    public Jaxmpp getJaxmpp();
    public String getMucDomain();
    public String getRoomJidFromName(String roomName);

    public String createOpenChatJid(String username);
    public String extractUsernameFromJid(String jid);
}
