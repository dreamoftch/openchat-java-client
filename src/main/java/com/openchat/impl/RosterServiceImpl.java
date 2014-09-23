package com.openchat.impl;

import com.openchat.RosterService;
import org.springframework.stereotype.Component;
import tigase.jaxmpp.core.client.AsyncCallback;
import tigase.jaxmpp.core.client.JID;
import tigase.jaxmpp.core.client.XMPPException;
import tigase.jaxmpp.core.client.exceptions.JaxmppException;
import tigase.jaxmpp.core.client.observer.Listener;
import tigase.jaxmpp.core.client.xmpp.modules.roster.RosterModule;
import tigase.jaxmpp.core.client.xmpp.stanzas.Stanza;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of RosterService.
 *
 * @author wche
 * @since 9/23/14
 */
@Component
public class RosterServiceImpl extends BaseServiceImpl implements RosterService, Listener<RosterModule.RosterEvent>, AsyncCallback {

    @Override
    public void afterPropertiesSet() throws Exception {
        this.xmppClient.getModuleManager().getModule(RosterModule.class).addListener(this);
    }

    @Override
    public void handleEvent(RosterModule.RosterEvent be) throws JaxmppException {
        log.info("RosterEvent type " + be.getType());
        if (RosterModule.ItemAdded.equals(be.getType())) {
            log.info("Roster added " + be.getItem().getJid() + " group " + be.getItem().getGroups().toString()
                    + " subscription " + be.getItem().getSubscription().name());
        }
        else if (RosterModule.ItemUpdated.equals(be.getType())) {
            log.info("Roster updated " + be.getItem().getJid() + " group " + be.getItem().getGroups().toString()
                    + " subscription " + be.getItem().getSubscription().name());
        }
        else if (RosterModule.ItemRemoved.equals(be.getType())) {
            log.info("Roster removed " + be.getItem().getJid() + " group " + be.getItem().getGroups().toString()
                    + " subscription " + be.getItem().getSubscription().name());
        }
    }

    @Override
    public void addBuddy(String username) {
        try {
            log.info("addBuddy " + username);
            List<String> groups = new ArrayList<String>();
            groups.add("Buddies");
            this.xmppClient.getJaxmpp().getRoster().add(JID.jidInstance(this.xmppClient.createOpenChatJid(username)).getBareJid(), username, groups, this);
        } catch (JaxmppException e) {
            log.error("Error adding buddy ", e);
        }
    }

    @Override
    public void onError(Stanza responseStanza, XMPPException.ErrorCondition error) throws JaxmppException {
        log.error("onError " + responseStanza.toString());
    }

    @Override
    public void onSuccess(Stanza responseStanza) throws JaxmppException {
        log.info("onSuccess " + responseStanza.toString());
    }

    @Override
    public void onTimeout() throws JaxmppException {
        log.error("onTimeout ");
    }

}
