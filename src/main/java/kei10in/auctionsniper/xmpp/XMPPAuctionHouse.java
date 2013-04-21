package kei10in.auctionsniper.xmpp;

import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import kei10in.auctionsniper.Auction;
import kei10in.auctionsniper.AuctionHouse;

import org.apache.commons.io.FilenameUtils;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

public class XMPPAuctionHouse implements AuctionHouse {
    private static final String LOGGER_NAME = "auction-sniper";
    public static final String LOG_FILE_NAME = "auction-sniper.log";
    
    public static final String AUCTION_RESOURCE = "Auction";
    public static final String ITEM_ID_AS_LOGIN = "auction-%s";
    public static final String AUCTION_ID_FORMAT =
        ITEM_ID_AS_LOGIN + "@%s/" + AUCTION_RESOURCE;
    
    private final XMPPConnection connection;
    private final LoggingXMPPFailureReporter failureReporter;

    public XMPPAuctionHouse(
        String hostname, String username, String password)
            throws XMPPAuctionException {
        try {
            XMPPConnection connection = new XMPPConnection(hostname);
            connection.connect();
            connection.login(username, password, AUCTION_RESOURCE);
            this.connection = connection;
        } catch (XMPPException xmppe) {
            throw new XMPPAuctionException(
                "Could not connect to auction: " + this.connection.toString(),
                xmppe);
        }
            
        failureReporter = new LoggingXMPPFailureReporter(makeLogger());
    }
    
    public Auction auctionFor(String itemId) {
        return new XMPPAuction(connection, auctionId(itemId), failureReporter);
    }
    
    public void disconnect() {
        connection.disconnect();
    }

    private String auctionId(String itemId) {
        return String.format(
            AUCTION_ID_FORMAT, itemId, connection.getServiceName());
    }
    
    private Logger makeLogger() throws XMPPAuctionException {
        Logger logger = Logger.getLogger(LOGGER_NAME);
        logger.setUseParentHandlers(false);
        logger.addHandler(simpleFileHandler());
        return logger;
    }
    
    private FileHandler simpleFileHandler() throws XMPPAuctionException {
        try {
            FileHandler handler = new FileHandler(LOG_FILE_NAME);
            handler.setFormatter(new SimpleFormatter());
            return handler;
        } catch (Exception e) {
            throw new XMPPAuctionException(
                "Could not create logger FileHandler " +
                    FilenameUtils.getFullPath(LOG_FILE_NAME), e);
        }
    }

}
