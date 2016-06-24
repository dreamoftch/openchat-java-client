
import tigase.jaxmpp.core.client.BareJID;
import tigase.jaxmpp.core.client.JID;
import tigase.jaxmpp.core.client.SessionObject;
import tigase.jaxmpp.core.client.exceptions.JaxmppException;
import tigase.jaxmpp.core.client.observer.Listener;
import tigase.jaxmpp.core.client.xmpp.modules.presence.PresenceModule;
import tigase.jaxmpp.core.client.xmpp.modules.presence.PresenceModule.PresenceEvent;
import tigase.jaxmpp.core.client.xmpp.modules.roster.RosterItem;
import tigase.jaxmpp.core.client.xmpp.modules.roster.RosterStore;
import tigase.jaxmpp.core.client.xmpp.stanzas.Message;
import tigase.jaxmpp.core.client.xmpp.stanzas.Stanza;
import tigase.jaxmpp.core.client.xmpp.stanzas.StanzaType;
import tigase.jaxmpp.j2se.Jaxmpp;

public class JaxmppExample {

    public static void main( String[] args ) throws JaxmppException, InterruptedException {

        final Jaxmpp jaxmpp = new Jaxmpp();
        try {
			jaxmpp.getModulesManager().getModule( PresenceModule.class ).addListener( PresenceModule.ContactChangedPresence, new Listener<PresenceModule.PresenceEvent>() {
			    @Override
			    public void handleEvent( PresenceEvent be ) throws JaxmppException {
			        System.out.println( String.format( "Presence received:\t %1$s is now %2$s (%3$s)", be.getJid(), be.getShow(), be.getStatus() != null ? be.getStatus() : "none" ) );
			    }
			} );
			jaxmpp.getSessionObject().setProperty(tigase.jaxmpp.j2se.connectors.socket.SocketConnector.HOSTNAME_VERIFIER_DISABLED_KEY, Boolean.TRUE);
			jaxmpp.getProperties().setUserProperty( SessionObject.USER_BARE_JID, BareJID.bareJIDInstance( "richmj@192.168.43.146" ) );
			jaxmpp.getProperties().setUserProperty( SessionObject.PASSWORD, "richmj" );

			System.out.println("开始登陆。。。");

			jaxmpp.login();

			System.out.println("登陆成功。。。");

	        RosterStore rosterStore = jaxmpp.getRoster();
	        for(RosterItem rosterItem : rosterStore.getAll()){
	        	System.out.println("rosterItem：" + rosterItem);
	        }
			
	        jaxmpp.send(createMessage("admin，你好啊"));
			//contact.sendMessage(JID.jidInstance("admin@192.168.43.146"), "Test", "This is a test");

		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(jaxmpp != null){
				jaxmpp.disconnect();
			}
		}
    }
    
    private static Stanza createMessage(String msg) {
		try {
			/*
				<message id="pCp2F-58" to="richmj@192.168.43.146" from="admin@192.168.43.146/Spark" type="chat">
				<body>1</body>
				</message>
			*/
			Message message = Message.create();
			message.setType(StanzaType.chat);
			message.setAttribute("from", JID.jidInstance("richmj@192.168.43.146").toString());
			message.setAttribute("to", JID.jidInstance("admin@192.168.43.146").toString());
			message.setBody(msg);
			System.out.println("message:" + message.getAsString());
			return message;
		} catch (JaxmppException e) {
			e.printStackTrace();
		}
		return null;
	}
    
}