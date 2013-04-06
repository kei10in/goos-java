package kei10in.test.unit.auctionsniper;
import kei10in.auctionsniper.Auction;
import kei10in.auctionsniper.AuctionSniper;
import kei10in.auctionsniper.SniperListener;
import kei10in.auctionsniper.AuctionEventListener.PriceSource;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;


public class AuctionSniperTest {
    @Rule
    public final JUnitRuleMockery context = new JUnitRuleMockery();
    
    private final Auction auction = context.mock(Auction.class);
    private final SniperListener sniperListener =
        context.mock(SniperListener.class);
    private final AuctionSniper cut =
        new AuctionSniper(auction, sniperListener);
   
    @Test
    public void reportsLostWhenAuctionCloses() {
        context.checking(new Expectations() {{
            oneOf(sniperListener).sniperLost();
        }});
        
        cut.auctionClosed();
    }
    
    @Test
    public void bidsHigherAndReportsBiddingWhenNewPriceArrives() {
        final int price = 1001;
        final int increment = 25;
        context.checking(new Expectations() {{
            oneOf(auction).bid(price + increment);
            atLeast(1).of(sniperListener).sniperBidding();
        }});
        
        cut.currentPrice(price, increment, PriceSource.FromOtherBidder);
    }
    
    @Test
    public void reportsWinningWhenCurrentPriceComesFromSniper() {
        context.checking(new Expectations() {{
            atLeast(1).of(sniperListener).sniperWinning();
        }});
        
        cut.currentPrice(123, 45, PriceSource.FromSniper);
    }

}
