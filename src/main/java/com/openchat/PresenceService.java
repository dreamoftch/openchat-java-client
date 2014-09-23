package com.openchat;

import tigase.jaxmpp.core.client.xmpp.stanzas.Presence;

/**
 * Service interface for presence.
 *
 * @author wche
 * @since 9/22/14
 */
public interface PresenceService {

    /**
     * Update current presence
     * @param show new presence
     * @param status status message associated with the new presence
     */
    public void updatePresence(Presence.Show show, String status);

}
