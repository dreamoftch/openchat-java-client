package com.openchat.impl;

import com.openchat.XMPPClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Base abstract class for service implementation
 *
 * @author wche
 * @since 9/22/14
 */
public abstract class BaseServiceImpl implements InitializingBean {
    protected final Log log = LogFactory.getLog(getClass());

    protected XMPPClient xmppClient;

    @Autowired
    public void setXmppClient(XMPPClient xmppClient) {
        this.xmppClient = xmppClient;
    }


}
