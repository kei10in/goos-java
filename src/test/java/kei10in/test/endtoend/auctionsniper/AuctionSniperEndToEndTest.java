package kei10in.test.endtoend.auctionsniper;

import org.junit.After;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(Runtime.class)
public class AuctionSniperEndToEndTest {
    private final FakeAuctionServer auction =
        new FakeAuctionServer("item-54321");
    private final ApplicationRunner application = new ApplicationRunner();

    @Test
    public void sniperJoinsAuctionUntilAuctionCloses() throws Exception {
        auction.startSellingItem();
        application.startBiddingIn(auction);
        auction.hasRecievedJoinRequestFromSniper(
            ApplicationRunner.SNIPER_XMPP_ID);
        auction.announceClosed();
        application.showsSniperHasLostAution(auction, 0, 0);
    }
    
    @Test
    public void sniperMakesAHigherBidButLoses() throws Exception {
        auction.startSellingItem();
        
        application.startBiddingIn(auction);
        auction.hasRecievedJoinRequestFromSniper(
            ApplicationRunner.SNIPER_XMPP_ID);
        
        auction.reportPrice(1000, 98, "other bidder");
        
        application.hasShownSniperIsBidding(auction, 1000, 1098);
        auction.hasRecievedBid(1098, ApplicationRunner.SNIPER_XMPP_ID);
        
        auction.announceClosed();
        application.showsSniperHasLostAution(auction, 1000, 1098);
    }
    
    @Test
    public void sniperWinsAnAuctionByBiddingHigher() throws Exception {
        auction.startSellingItem();
        
        application.startBiddingIn(auction);
        auction.hasRecievedJoinRequestFromSniper(
            ApplicationRunner.SNIPER_XMPP_ID);
        
        auction.reportPrice(1000, 98, "other bidder");
        
        application.hasShownSniperIsBidding(auction, 1000, 1098);
        
        auction.hasRecievedBid(1098, ApplicationRunner.SNIPER_XMPP_ID);
        
        auction.reportPrice(1098, 97, ApplicationRunner.SNIPER_XMPP_ID);
        application.hasShownSniperIsWinning(auction, 1098);
        
        auction.announceClosed();
        application.showsSniperHasWonAution(auction, 1098);
    }

    @After
    public void stopAuction() {
        auction.stop();
    }

    @After
    public void stopApplication() {
        application.stop();
    }

}
