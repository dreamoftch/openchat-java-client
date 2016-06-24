package com.openchat.impl;

import com.openchat.XMPPClient;
import com.openchat.utils.XMPPUtil;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import tigase.jaxmpp.core.client.XmppModulesManager;
import tigase.jaxmpp.core.client.exceptions.JaxmppException;
import tigase.jaxmpp.j2se.ConnectionConfiguration.ConnectionType;
import tigase.jaxmpp.j2se.Jaxmpp;

/**
 * Implementation of XMPPClient.
 *
 * @author wche
 * @since 9/22/14
 */
@Component
@PropertySource("classpath:app.properties")
public class XMPPClientImpl implements InitializingBean, XMPPClient {
    protected final Log log = LogFactory.getLog(getClass());

    private String server;
    private Integer port;
    private String domain;
    private String username;
    private String password;
    private Jaxmpp jaxmpp;
    private String mucDomain;  // domain for MUC (Chat room)

    @Value("${xmpp.server}")
    public void setServer(String server) {
        this.server = server;
        XMPPUtil.SERVER = server;
    }

    @Value("${xmpp.port}")
    public void setPort(Integer port) {
        this.port = port;
    }

    @Value("${xmpp.domain}")
    public void setDomain(String domain) {
        this.domain = domain;
    }

    @Value("${xmpp.username}")
    public void setUsername(String username) {
        this.username = username;
    }

    @Value("${xmpp.password}")
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.mucDomain = "muc." + this.domain;
        this.jaxmpp = new Jaxmpp();
    }

    @Override
    public Jaxmpp getJaxmpp() {
        return this.jaxmpp;
    }

    @Override
    public XmppModulesManager getModuleManager() {
        return this.jaxmpp.getModulesManager();
    }

    @Override
    public String getMucDomain() {
        return this.mucDomain;
    }

    @Override
    public String getRoomJidFromName(String roomName) {
        return roomName + "@" + this.mucDomain;
    }

    public void login() throws Exception {
    	this.jaxmpp.getSessionObject().setProperty(tigase.jaxmpp.j2se.connectors.socket.SocketConnector.HOSTNAME_VERIFIER_DISABLED_KEY, Boolean.TRUE);
    	this.jaxmpp.getConnectionConfiguration().setConnectionType(ConnectionType.socket);
    	this.jaxmpp.getConnectionConfiguration().setUseSASL(false);
        this.jaxmpp.getConnectionConfiguration().setServer(this.server);
        this.jaxmpp.getConnectionConfiguration().setPort(this.port);
        String jid = this.createOpenChatJid(this.username);
        this.jaxmpp.getConnectionConfiguration().setUserJID(jid);
        this.jaxmpp.getConnectionConfiguration().setUserPassword(this.password);

        log.info("Logging in as "+ jid);
        try {
            this.jaxmpp.login();
            log.info("Logged in as " + jid);
        } catch (JaxmppException e) {
            log.error(e);
            throw e;
        }
    }

    public String createOpenChatJid(String username) {
        //String jid = StringUtils.replace(username, "@", "$");
    	String jid = username;
        jid = StringUtils.appendIfMissing(jid, "@" + domain);
        log.info("createOpenChatJid:" + jid);
        return jid;
    }

    public String extractUsernameFromJid(String jid) {
        String username = StringUtils.substring(jid, 0, jid.indexOf("@"));
        username = StringUtils.replace(username, "$", "@");
        return username;
    }

}
