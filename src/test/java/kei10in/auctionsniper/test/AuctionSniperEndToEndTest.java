package kei10in.auctionsniper.test;

import static org.junit.Assert.*;

import org.junit.Test;

public class AuctionSniperEndToEndTest {
	private final FakeAuctionServer auction = new FakeAuctionServer("item-54321");
	private final ApplicationRunner application = new ApplicationRunner();

	@Test
	public void sniperJoinsAuctionUntilAuctionCloses() throw Exception {
		auction.startSellingItem();
		application.startBiddingIn(auction);
		auction.hasRecievedJoinRequestFromSniper();
		auction.announceClosed();
		application.showsSniperHasLostAution();
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
