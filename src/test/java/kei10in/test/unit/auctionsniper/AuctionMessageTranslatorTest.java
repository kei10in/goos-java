package kei10in.test.unit.auctionsniper;

import kei10in.auctionsniper.AuctionEventListener;
import kei10in.auctionsniper.AuctionEventListener.PriceSource;
import kei10in.auctionsniper.xmpp.AuctionMessageTranslator;
import kei10in.auctionsniper.xmpp.XMPPFailureReporter;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.packet.Message;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

public class AuctionMessageTranslatorTest {
    @Rule public final JUnitRuleMockery context = new JUnitRuleMockery();
    
    public static final Chat UNUSED_CHAT = null;
    private final String SNIPER_ID = "sniper";
    private final AuctionEventListener listener =
        context.mock(AuctionEventListener.class);
    private final XMPPFailureReporter failureReporter = 
        context.mock(XMPPFailureReporter.class);
    private final AuctionMessageTranslator cut =
        new AuctionMessageTranslator(SNIPER_ID, listener, failureReporter);

    @Test
    public void notifiesAuctionClosedWhenCloseMessageReceived() {
        context.checking(new Expectations() {{
            oneOf(listener).auctionClosed();
        }});
        
        String body = "SOLVersion: 1.1; Event: CLOSE;";
        cut.processMessage(UNUSED_CHAT, message(body));
    }

    @Test public void
    notifiesBidDetailsWhenCurrentPriceMessageReceivedFromOtherBidder() {
        context.checking(new Expectations() {{
            exactly(1).of(listener).currentPrice(
                192, 7, PriceSource.FromOtherBidder);
        }});
        
        String body =
            "SOLVersion: 1.1; Event: PRICE; CurrentPrice: 192; Increment: 7; "
            + "Bidder: Someone else;";

        cut.processMessage(UNUSED_CHAT, message(body));
    }

    @Test public void
    notifiesBidDetailsWhenCurrentPriceMessageReceivedFromSniper() {
        context.checking(new Expectations() {{
            exactly(1).of(listener).currentPrice(
                234, 5, PriceSource.FromSniper);
        }});
        
        String body =
            "SOLVersion: 1.1; Event: PRICE; CurrentPrice: 234; Increment: 5; "
            + "Bidder: " + SNIPER_ID + ";"; 
        
        cut.processMessage(UNUSED_CHAT, message(body));
    }
    
    @Test public void
    notifiesAuctionFailedWhenEventTypeMissing() {
        String badMessage =
            "SOLVersion: 1.1; CurrentPrice: 234; Increment: 5;" +
            "Biddor: " + SNIPER_ID + ";";
        expectFailureWithMessage(badMessage);
        cut.processMessage(UNUSED_CHAT, message(badMessage));
    }
    
    @Test public void
    notifiesAuctionFailedWhenBadMessageReceived() {
        final String badMessage = "a bad message";
        expectFailureWithMessage(badMessage);
        cut.processMessage(UNUSED_CHAT, message(badMessage));
    }

    private Message message(String body) {
        Message message = new Message();
        message.setBody(body);
        return message;
    }
    
    private void expectFailureWithMessage(final String badMessage) {
        context.checking(new Expectations() {
            {
                exactly(1).of(listener).auctionFailed();
                exactly(1).of(failureReporter).cannotTranslateMessage(
                    with(SNIPER_ID), with(badMessage),
                    with(any(Exception.class)));
            }
        });
    }

}
