package kei10in.test.integration.auctionsniper.xmpp;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.CountDownLatch;

import kei10in.auctionsniper.Auction;
import kei10in.auctionsniper.AuctionEventListener;
import kei10in.auctionsniper.xmpp.XMPPAuctionException;
import kei10in.auctionsniper.xmpp.XMPPAuctionHouse;
import kei10in.test.endtoend.auctionsniper.ApplicationRunner;
import kei10in.test.endtoend.auctionsniper.FakeAuctionServer;

import org.jivesoftware.smack.XMPPException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class XMPPAuctionHouseTest {
    private XMPPAuctionHouse auctionHouse;
    private FakeAuctionServer server = new FakeAuctionServer("item-54321");

    @Test
    public void receivesEventsFromAuctionServerAfterJoining()
        throws Exception {
        CountDownLatch auctionWasClosed = new CountDownLatch(1);

        Auction auction = auctionHouse.auctionFor(server.getItemId());
        auction.addAuctionEventListener(
            auctionClosedListener(auctionWasClosed));
        
        auction.join();
        server.hasReceivedJoinRequestFromSniper(
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
            public void auctionFailed() {
                // not implement
            }
        };
    }
    
    @Before
    public void openAuctionHouse() throws XMPPAuctionException {
        auctionHouse = new XMPPAuctionHouse(
            FakeAuctionServer.XMPP_HOSTNAME,
            ApplicationRunner.SNIPER_ID,
            ApplicationRunner.SNIPER_PASSWORD);
    }

    @After
    public void closeAuctionHouse() {
        auctionHouse.disconnect();
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
