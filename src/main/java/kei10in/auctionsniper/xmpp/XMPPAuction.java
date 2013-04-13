package kei10in.auctionsniper.xmpp;

import kei10in.auctionsniper.Auction;
import kei10in.auctionsniper.AuctionEventListener;
import kei10in.auctionsniper.util.Announcer;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

public class XMPPAuction implements Auction {
    public static final String JOIN_COMMAND_FORMAT =
        "SOLVersion: 1.1; Command: JOIN;";
    private static final String BID_COMMAND_FORMAT =
        "SOLVersion: 1.1; Command: BID; Price: %d;";
    
    private final Announcer<AuctionEventListener> auctionEventListeners =  
        Announcer.to(AuctionEventListener.class);
    private final Chat chat;
    
    public XMPPAuction(XMPPConnection connection, String auctionId) {
        chat = connection.getChatManager().createChat(
            auctionId,
            new AuctionMessageTranslator(
                connection.getUser(),
                auctionEventListeners.announce()));
    }
    
    public void addAuctionEventListener(AuctionEventListener listener) {
        auctionEventListeners.addListener(listener);
    }

    public void join() {
        sendMessage(JOIN_COMMAND_FORMAT);
    }

    public void bid(int amount) {
        sendMessage(String.format(BID_COMMAND_FORMAT, amount));
    }
    
    private void sendMessage(final String message) {
        try {
            chat.sendMessage(message);
        } catch (XMPPException e) {
            e.printStackTrace();
        }
    }

}