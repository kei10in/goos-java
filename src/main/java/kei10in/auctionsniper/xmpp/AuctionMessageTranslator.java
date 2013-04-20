package kei10in.auctionsniper.xmpp;

import java.util.HashMap;

import kei10in.auctionsniper.AuctionEventListener;
import kei10in.auctionsniper.AuctionEventListener.PriceSource;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;

public class AuctionMessageTranslator implements MessageListener {
    private final String sniperId;
    private AuctionEventListener listener;
    private XMPPFailureReporter failureReporter;

    public AuctionMessageTranslator(
        String sniperId, AuctionEventListener listener) {
        this.sniperId = sniperId;
        this.listener = listener; 
    }

    public AuctionMessageTranslator(
        String sniperId, AuctionEventListener listener,
        XMPPFailureReporter failureReporter) {
        this.sniperId = sniperId;
        this.listener = listener;
        this.failureReporter = failureReporter;
    }

    public void processMessage(Chat chat, Message message) {
        try {
            translate(message.getBody());
        } catch (Exception exception) {
            failureReporter.cannotTranslateMessage(
                sniperId, message.getBody(), exception);
            listener.auctionFailed();
        }
    }

    private void translate(String messageBody) throws Exception {
        AuctionEvent event = AuctionEvent.from(messageBody);

        String type = event.type();
        if ("CLOSE".equals(type)) {
            listener.auctionClosed();
        } else if ("PRICE".equals(type)) {
            listener.currentPrice(
                event.currentPrice(),
                event.increment(),
                event.isFrom(sniperId));
        }
    }

    
    private static class AuctionEvent {
        private final HashMap<String, String> fields =
            new HashMap<String, String>();

        public String type() throws MissingValueException {
            return get("Event");
        }
        
        public int currentPrice() throws Exception {
            return getInt("CurrentPrice");
        }

        public int increment() throws Exception {
            return getInt("Increment");
        }
        
        public PriceSource isFrom(String sniperId)
            throws MissingValueException {
            return sniperId.equals(bidder())
                ? PriceSource.FromSniper : PriceSource.FromOtherBidder;            
        }
        private String bidder() throws MissingValueException {
            return get("Bidder");
        }

        public int getInt(String fieldName) throws Exception {
            return Integer.parseInt(get(fieldName));
        }

        public String get(String fieldName) throws MissingValueException {
            final String value = fields.get(fieldName);
            if (value == null) {
                throw new MissingValueException(fieldName);
            }
            return value;
        }

        private void addField(String field) {
            String[] pair = field.split(":");
            fields.put(pair[0].trim(), pair[1].trim());
        }

        static AuctionEvent from(String messageBody) {
            AuctionEvent event = new AuctionEvent();
            for (String field : fieldsIn(messageBody)) {
                event.addField(field);
            }
            return event;
        }

        static String[] fieldsIn(String messageBody) {
            return messageBody.split(";");
        }
    }

    private static class MissingValueException extends Exception {
        private static final long serialVersionUID = 1L;

        public MissingValueException(String fieldName) {
          super("Missing value for " + fieldName);
        }
      }
}
