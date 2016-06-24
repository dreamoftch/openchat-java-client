import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;

import javax.net.ssl.X509TrustManager;

import tigase.jaxmpp.core.client.BareJID;
import tigase.jaxmpp.core.client.SessionObject;
import tigase.jaxmpp.core.client.exceptions.JaxmppException;
import tigase.jaxmpp.core.client.observer.Listener;
import tigase.jaxmpp.core.client.xmpp.modules.roster.RosterItem;
import tigase.jaxmpp.core.client.xmpp.modules.roster.RosterStore;
import tigase.jaxmpp.j2se.Jaxmpp;
import tigase.jaxmpp.j2se.connectors.socket.SocketConnector;
import tigase.jaxmpp.j2se.connectors.socket.SocketConnector.SocketConnectorEvent;

public class Jaxmpp2Demo {
    public static void main(String[] args) throws JaxmppException, InterruptedException {
        final Jaxmpp jaxmpp = new Jaxmpp();

        X509TrustManager trustManager = new X509TrustManager() {

            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                System.out.println("SERVER CERTIFICATE: " + Arrays.toString(chain));
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };
        jaxmpp.getSessionObject().setProperty(tigase.jaxmpp.j2se.connectors.socket.SocketConnector.HOSTNAME_VERIFIER_DISABLED_KEY, Boolean.TRUE);
        jaxmpp.getProperties().setUserProperty(SocketConnector.TRUST_MANAGERS_KEY, new X509TrustManager[] { trustManager });

        jaxmpp.addListener(SocketConnector.EncryptionEstablished, new Listener<SocketConnectorEvent>() {

            @Override
            public void handleEvent(SocketConnectorEvent be) throws JaxmppException {
                System.out.println("Stream is now encrypted!");
            }
        });

        jaxmpp.getProperties().setUserProperty(SessionObject.USER_BARE_JID, BareJID.bareJIDInstance("richmj@192.168.43.146"));
        jaxmpp.getProperties().setUserProperty(SessionObject.PASSWORD, "richmj");

        System.out.println("Loging in...");

        jaxmpp.login();

        System.out.println("登陆成功");

        RosterStore rosterStore = jaxmpp.getRoster();
        for(RosterItem rosterItem : rosterStore.getAll()){
        	System.out.println("rosterItem：" + rosterItem);
        }
        
        jaxmpp.disconnect();
    }
}