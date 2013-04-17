package kei10in.test.unit.auctionsniper;

import static org.hamcrest.Matchers.equalTo;
import kei10in.auctionsniper.Auction;
import kei10in.auctionsniper.AuctionHouse;
import kei10in.auctionsniper.AuctionSniper;
import kei10in.auctionsniper.Item;
import kei10in.auctionsniper.SniperCollector;
import kei10in.auctionsniper.SniperLauncher;

import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.States;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

public class SniperLauncherTest {
    @Rule
    public final JUnitRuleMockery context = new JUnitRuleMockery();
    private final States auctionState =
        context.states("auction state").startsAs("not joined");
    private final AuctionHouse auctionHouse = context.mock(AuctionHouse.class);
    private final Auction auction = context.mock(Auction.class);
    private final SniperCollector sniperCollector =
        context.mock(SniperCollector.class);
    
    private final SniperLauncher cut =
        new SniperLauncher(auctionHouse, sniperCollector);
    
    @Test
    public void addNewSniperToCollectorAndThenJoinsAuction() {
        final Item item = new Item("item 123", 456);
        context.checking(new Expectations() {
            {
                allowing(auctionHouse).auctionFor(item.identifier);
                will(returnValue(auction));
                
                oneOf(auction).addAuctionEventListener(
                    with(sniperForItem(item)));
                when(auctionState.is("not joined"));
                
                oneOf(sniperCollector).addSniper(
                    with(sniperForItem(item)));
                when(auctionState.is("not joined"));
                
                oneOf(auction).join();
                then(auctionState.is("joined"));
            }
        });
        
        cut.joinAuction(item);
    }
    
    private Matcher<AuctionSniper> sniperForItem(Item item) {
        return new FeatureMatcher<AuctionSniper, String>(
            equalTo(item.identifier), "sniper with item id", "item") {
            @Override
            protected String featureValueOf(AuctionSniper actual) {
                return actual.getSnapshot().itemId;
            }
        };
    }

}
