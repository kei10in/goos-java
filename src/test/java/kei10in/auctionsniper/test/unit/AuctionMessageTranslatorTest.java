package kei10in.auctionsniper.test.unit;

import kei10in.auctionsniper.AuctionEventListener;
import kei10in.auctionsniper.AuctionMessageTranslator;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.packet.Message;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

public class AuctionMessageTranslatorTest {
    @Rule public final JUnitRuleMockery context = new JUnitRuleMockery();
    
    public static final Chat UNUSED_CHAT = null;
    private final AuctionEventListener listener =
        context.mock(AuctionEventListener.class);
    private final AuctionMessageTranslator sut =
        new AuctionMessageTranslator(listener);

    @Test
    public void notifiesAuctionClosedWhenCloseMessageReceived() {
        context.checking(new Expectations() {{
            oneOf(listener).auctionClosed();
        }});
        
        Message message = new Message();
        message.setBody("SOLVersion: 1.1; Event: CLOSE;");
        
        sut.processMessage(UNUSED_CHAT, message);
    }
    
    @Test
    public void notifiesBidDetailsWhenCurrentPriceMessageReceived() {
        context.checking(new Expectations() {{
            exactly(1).of(listener).currentPrice(192, 7);
        }});
        
        Message message = new Message();
        message.setBody(
            "SOLVersion: 1.1; Event: PRICE; CurrentPrice: 192; Increment: 7; "
            + "Bidder: Someone else;");
        
        sut.processMessage(UNUSED_CHAT, message);
        
    }

}
