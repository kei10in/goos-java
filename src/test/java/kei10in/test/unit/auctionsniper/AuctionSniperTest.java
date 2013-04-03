package kei10in.test.unit.auctionsniper;
import kei10in.auctionsniper.AuctionSniper;
import kei10in.auctionsniper.SniperListener;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;


public class AuctionSniperTest {
    @Rule
    public final JUnitRuleMockery context = new JUnitRuleMockery();
    private final SniperListener sniperListener =
        context.mock(SniperListener.class);
    private final AuctionSniper sut = new AuctionSniper(sniperListener);
   
    @Test
    public void reportsLostWhenAuctionCloses() {
        context.checking(new Expectations() {{
            oneOf(sniperListener).sniperLost();
        }});
        
        sut.auctionClosed();
    }

}
