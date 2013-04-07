package kei10in.test.unit.auctionsniper;
import kei10in.auctionsniper.Auction;
import kei10in.auctionsniper.AuctionEventListener.PriceSource;
import kei10in.auctionsniper.AuctionSniper;
import kei10in.auctionsniper.SniperListener;
import kei10in.auctionsniper.SniperSnapshot;

import org.jmock.Expectations;
import org.jmock.States;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;


public class AuctionSniperTest {
    private static final String ITEM_ID = "item-id";
    
    @Rule
    public final JUnitRuleMockery context = new JUnitRuleMockery();
        
    private final States sniperState = context.states("sniper");
    private final Auction auction = context.mock(Auction.class);
    private final SniperListener sniperListener =
        context.mock(SniperListener.class);
    private final AuctionSniper cut =
        new AuctionSniper(ITEM_ID, auction, sniperListener);
   
    @Test
    public void reportsLostWhenAuctionClosesImmediately() {
        context.checking(new Expectations() {{
            oneOf(sniperListener).sniperLost();
        }});
        
        cut.auctionClosed();
    }
    
    @Test
    public void bidsHigherAndReportsBiddingWhenNewPriceArrives() {
        final int price = 1001;
        final int increment = 25;
        final int bid = price + increment;
        
        context.checking(new Expectations() {{
            oneOf(auction).bid(price + increment);
            atLeast(1).of(sniperListener).sniperBidding(
                new SniperSnapshot(ITEM_ID, price, bid));
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
    
    @Test
    public void reportsLostIfAuctionClosesWhenBidding() {
        context.checking(new Expectations() {{
            ignoring(auction);
            allowing(sniperListener).sniperBidding(
                with(any(SniperSnapshot.class)));
                then(sniperState.is("bidding"));
            atLeast(1).of(sniperListener).sniperLost();
                when(sniperState.is("bidding"));
        }});
        
        cut.currentPrice(123, 45, PriceSource.FromOtherBidder);
        cut.auctionClosed();
    }
    
    @Test
    public void reportsWonIfAuctionClosesWhenWinning() {
        context.checking(new Expectations() {{
            ignoring(auction);
            allowing(sniperListener).sniperWinning();
                then(sniperState.is("winning"));
            atLeast(1).of(sniperListener).sniperWon();
                when(sniperState.is("winning"));
        }});
        
        cut.currentPrice(123, 45, PriceSource.FromSniper);
        cut.auctionClosed();
    }

}
