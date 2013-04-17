package kei10in.test.unit.auctionsniper;
import static org.hamcrest.Matchers.equalTo;
import kei10in.auctionsniper.Auction;
import kei10in.auctionsniper.AuctionEventListener.PriceSource;
import kei10in.auctionsniper.AuctionSniper;
import kei10in.auctionsniper.Item;
import kei10in.auctionsniper.SniperListener;
import kei10in.auctionsniper.SniperSnapshot;
import kei10in.auctionsniper.SniperState;

import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.Sequence;
import org.jmock.States;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;


public class AuctionSniperTest {
    private static final String ITEM_ID = "item-id";
    private static final Item ITEM = new Item(ITEM_ID, 1234);
    
    @Rule
    public final JUnitRuleMockery context = new JUnitRuleMockery();
        
    private final States sniperState = context.states("sniper");
    private final Auction auction = context.mock(Auction.class);
    private final SniperListener sniperListener =
        context.mock(SniperListener.class);
    private final AuctionSniper cut = new AuctionSniper(ITEM, auction);
   
    @Before
    public void attachSniperListener() {
        cut.addSniperListener(sniperListener);
    }
    
    @Test
    public void reportsLostWhenAuctionClosesImmediately() {
        context.checking(new Expectations() {{
            atLeast(1).of(sniperListener).sniperStateChanged(
                with(aSniperThatIs(SniperState.LOST)));
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
            atLeast(1).of(sniperListener).sniperStateChanged(
                new SniperSnapshot(ITEM_ID, price, bid, SniperState.BIDDING));
        }});
        
        cut.currentPrice(price, increment, PriceSource.FromOtherBidder);
    }
    
    @Test
    public void reportsWinningWhenCurrentPriceComesFromSniper() {
        allowingSniperBidding();
        context.checking(new Expectations() {{
            ignoring(auction);
                    
            atLeast(1).of(sniperListener).sniperStateChanged(
                new SniperSnapshot(ITEM_ID, 135, 135, SniperState.WINNING));
                    when(sniperState.is("bidding"));
        }});

        cut.currentPrice(123, 12, PriceSource.FromOtherBidder);
        cut.currentPrice(135, 45, PriceSource.FromSniper);
    }
    
    @Test
    public void reportsLostIfAuctionClosesWhenBidding() {
        allowingSniperBidding();
        context.checking(new Expectations() {{
            ignoring(auction);
            atLeast(1).of(sniperListener).sniperStateChanged(
                with(aSniperThatIs(SniperState.LOST)));
                    when(sniperState.is("bidding"));
        }});
        
        cut.currentPrice(123, 45, PriceSource.FromOtherBidder);
        cut.auctionClosed();
    }
    
    @Test
    public void reportsWonIfAuctionClosesWhenWinning() {
        context.checking(new Expectations() {{
            ignoring(auction);
            allowing(sniperListener).sniperStateChanged(
                with(aSniperThatIs(SniperState.WINNING)));
                    then(sniperState.is("winning"));
            atLeast(1).of(sniperListener).sniperStateChanged(
                with(aSniperThatIs(SniperState.WON)));
                    when(sniperState.is("winning"));
        }});
        
        cut.currentPrice(123, 45, PriceSource.FromSniper);
        cut.auctionClosed();
    }
    
    @Test
    public void doesNotBidAndReportsLosingIfSubsequentPriceIsAboveStopPrice() {
        allowingSniperBidding();
        context.checking(new Expectations() {
            {
                int bid = 123 + 45;
                allowing(auction).bid(bid);
                atLeast(1).of(sniperListener).sniperStateChanged(
                    new SniperSnapshot(ITEM_ID, 2345, bid, SniperState.LOSING));
                when(sniperState.is("bidding"));
            }
        });

        cut.currentPrice(123, 45, PriceSource.FromOtherBidder);
        cut.currentPrice(2345, 25, PriceSource.FromOtherBidder);
    }
    
    @Test
    public void doesNotBidAndReportsLosingIfFirstPriceIsAboveStopPrice() {
        context.checking(new Expectations() {
            {
                atLeast(1).of(sniperListener).sniperStateChanged(
                    new SniperSnapshot(ITEM_ID, 2345, 0, SniperState.LOSING));
            }
        });
        
        cut.currentPrice(2345, 25, PriceSource.FromOtherBidder);
    }
    
    @Test
    public void reportsLostIfAuctionClosesWhenLosing() {
        allowingSniperLosing();
        context.checking(new Expectations() {
            {
                atLeast(1).of(sniperListener).sniperStateChanged(
                    new SniperSnapshot(ITEM_ID, 2345, 0, SniperState.LOST));
                when(sniperState.is("losing"));
            }
        });
        
        cut.currentPrice(2345, 25, PriceSource.FromOtherBidder);
        cut.auctionClosed();
    }
    
    @Test
    public void continuesToBeLosingOnceStopPriceHasBeenReached() {
        final Sequence states = context.sequence("sniper states");
        context.checking(new Expectations() {
            {
                atLeast(1).of(sniperListener).sniperStateChanged(
                    new SniperSnapshot(ITEM_ID, 1234, 0, SniperState.LOSING));
                inSequence(states);
                atLeast(1).of(sniperListener).sniperStateChanged(
                    new SniperSnapshot(ITEM_ID, 2345, 0, SniperState.LOSING));
                inSequence(states);
            }
        });
        cut.currentPrice(1234, 56, PriceSource.FromOtherBidder);
        cut.currentPrice(2345, 78, PriceSource.FromOtherBidder);
    }
    
    @Test
    public void doesNotBidAndReportsLosingIfPriceAfterWinningIsAboveStopPrice() {
        allowingSniperBidding();
        allowingSniperWinning();
        context.checking(new Expectations() {
            {
                int bid = 1000 + 70;
                allowing(auction).bid(bid);
                atLeast(1).of(sniperListener).sniperStateChanged(
                    new SniperSnapshot(ITEM_ID, 2345, bid, SniperState.LOSING));
                when(sniperState.is("winning"));
            }
        });
        cut.currentPrice(1000, 70, PriceSource.FromOtherBidder);
        cut.currentPrice(1070, 80, PriceSource.FromSniper);
        cut.currentPrice(2345, 78, PriceSource.FromOtherBidder);
    }

    private void allowingSniperBidding() {
        context.checking(new Expectations() {
            {
                allowing(sniperListener).sniperStateChanged(
                    with(aSniperThatIs(SniperState.BIDDING)));
                then(sniperState.is("bidding"));
            }
        });
    }
    
    private void allowingSniperLosing() {
        context.checking(new Expectations() {
            {
                allowing(sniperListener).sniperStateChanged(
                    with(aSniperThatIs(SniperState.LOSING)));
                then(sniperState.is("losing"));
            }
        });
    }

    private void allowingSniperWinning() {
        context.checking(new Expectations() {
            {
                allowing(sniperListener).sniperStateChanged(
                    with(aSniperThatIs(SniperState.WINNING)));
                then(sniperState.is("winning"));
            }
        });
    }
    
    private Matcher<SniperSnapshot> aSniperThatIs(final SniperState state) {
        return new FeatureMatcher<SniperSnapshot, SniperState>(
            equalTo(state), "sniper that is ", "was")
            {
                @Override
                protected SniperState featureValueOf(SniperSnapshot actual) {
                    return actual.state;
                }
            };
    }
}
