package kei10in.auctionsniper.xmpp;

import kei10in.auctionsniper.Auction;
import kei10in.auctionsniper.AuctionHouse;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

public class XMPPAuctionHouse implements AuctionHouse {
    public static final String AUCTION_RESOURCE = "Auction";
    public static final String ITEM_ID_AS_LOGIN = "auction-%s";
    public static final String AUCTION_ID_FORMAT =
        ITEM_ID_AS_LOGIN + "@%s/" + AUCTION_RESOURCE;
    
    private final XMPPConnection connection;

    public XMPPAuctionHouse(
        String hostname, String username, String password)
            throws XMPPException {
        connection = new XMPPConnection(hostname);
        connection.connect();
        connection.login(username, password, AUCTION_RESOURCE);
    }
    
    public Auction auctionFor(String itemId) {
        return new XMPPAuction(connection, auctionId(itemId));
    }
    
    public void disconnect() {
        connection.disconnect();
    }

    private String auctionId(String itemId) {
        return String.format(
            AUCTION_ID_FORMAT, itemId, connection.getServiceName());
    }

}
