package kei10in.auctionsniper.xmpp;

import kei10in.auctionsniper.Auction;
import kei10in.auctionsniper.AuctionHouse;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

public class XMPPAuctionHouse implements AuctionHouse {
    final XMPPConnection connection;
    
    public XMPPAuctionHouse(
        String hostname, String username, String password)
            throws XMPPException {
        connection = new XMPPConnection(hostname);
        connection.connect();
        connection.login(username, password, XMPPAuction.AUCTION_RESOURCE);
    }
    
    public Auction auctionFor(String itemId) {
        return new XMPPAuction(connection, itemId);
    }
    
    public void disconnect() {
        connection.disconnect();
    }

}
