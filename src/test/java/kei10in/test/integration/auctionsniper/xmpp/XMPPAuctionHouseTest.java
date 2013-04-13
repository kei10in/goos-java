package kei10in.test.integration.auctionsniper.xmpp;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.CountDownLatch;

import kei10in.auctionsniper.Auction;
import kei10in.auctionsniper.AuctionEventListener;
import kei10in.auctionsniper.XMPPAuction;
import kei10in.test.endtoend.auctionsniper.ApplicationRunner;
import kei10in.test.endtoend.auctionsniper.FakeAuctionServer;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class XMPPAuctionHouseTest {
    private XMPPConnection connection;
    private FakeAuctionServer server = new FakeAuctionServer("item-54321");

    @Test
    public void receivesEventsFromAuctionServerAfterJoining()
        throws Exception {
        CountDownLatch auctionWasClosed = new CountDownLatch(1);

        Auction auction = new XMPPAuction(connection, server.getItemId());
        auction.addAuctionEventListener(
            auctionClosedListener(auctionWasClosed));
        
        auction.join();
        server.hasRecievedJoinRequestFromSniper(
            ApplicationRunner.SNIPER_XMPP_ID);
        server.announceClosed();
        
        assertTrue(
            "should have been closed", auctionWasClosed.await(2, SECONDS));
    }
    
    public AuctionEventListener auctionClosedListener(
        final CountDownLatch auctionWasClosed) {
        return new AuctionEventListener() {
            public void auctionClosed() {
                auctionWasClosed.countDown();
            }
            public void currentPrice(
                int price, int increment, PriceSource priceSource) {
                // not implement
            }
        };
    }
    
    @Before
    public void connect() throws XMPPException {
        connection = new XMPPConnection(FakeAuctionServer.XMPP_HOSTNAME);
        connection.connect();
        connection.login(
            ApplicationRunner.SNIPER_ID,
            ApplicationRunner.SNIPER_PASSWORD,
            XMPPAuction.AUCTION_RESOURCE);
    }

    @After
    public void disconnect() {
        connection.disconnect();
    }
    
    @Before
    public void startAuction() throws XMPPException {
        server.startSellingItem();
    }
    
    @After
    public void stopAuction() {
        server.stop();
    }
    
}