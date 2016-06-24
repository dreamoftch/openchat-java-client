package com.openchat.impl;

import org.springframework.stereotype.Component;

import com.openchat.PresenceService;

import tigase.jaxmpp.core.client.exceptions.JaxmppException;
import tigase.jaxmpp.core.client.observer.Listener;
import tigase.jaxmpp.core.client.xmpp.modules.presence.PresenceModule;
import tigase.jaxmpp.core.client.xmpp.stanzas.Presence;

/**
 * Implementation of PresenceService.
 *
 * @author wche
 * @since 9/22/14
 */
@Component
public class PresenceServiceImpl extends BaseServiceImpl implements PresenceService, Listener<PresenceModule.PresenceEvent> {

    @Override
    public void handleEvent(PresenceModule.PresenceEvent be) throws JaxmppException {
        log.info("Presence update: " + be.getJid() + " status " + be.getStatus() + " show " + be.getShow());
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.xmppClient.getModuleManager().getModule(PresenceModule.class).addListener(this);
    }

    @Override
    public void updatePresence(Presence.Show show, String status) {
        try {
            log.info("UpdatePresence " + show.name());
            this.xmppClient.getJaxmpp().getPresence().setPresence(show, status, null);
        } catch (JaxmppException e) {
            log.error("Error updatePresence ", e);
        }
    }
}
