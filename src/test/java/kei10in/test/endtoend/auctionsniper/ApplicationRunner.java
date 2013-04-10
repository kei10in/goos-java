package kei10in.test.endtoend.auctionsniper;

import kei10in.auctionsniper.Main;
import kei10in.auctionsniper.SniperState;
import kei10in.auctionsniper.ui.MainWindow;
import kei10in.auctionsniper.ui.SnipersTableModel;

public class ApplicationRunner {
    public static final String SNIPER_XMPP_ID = "sniper@localhost/Auction";
    private static final String SNIPER_ID = "sniper";
    private static final String SNIPER_PASSWORD = "sniper";
    private static final String XMPP_HOSTNAME = "localhost";
    private AuctionSniperDriver driver;

    public void startBiddingIn(final FakeAuctionServer ... auctions) {
        Thread thread = new Thread("Test Application") {
            @Override
            public void run() {
                try {
                    Main.main(arguments(auctions));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.setDaemon(true);
        thread.start();
        driver = new AuctionSniperDriver(1000);
        driver.hasTitle(MainWindow.APPLICATION_TITLE);
        driver.hasColumnTitles();
        driver.showsSniperStatus(
            SnipersTableModel.textFor(SniperState.JOINING));
    }
    
    public void hasShownSniperIsBidding(
        FakeAuctionServer auction, int lastPrice, int lastBid) {
        driver.showsSniperStatus(
            auction.getItemId(), lastPrice, lastBid,
            SnipersTableModel.textFor(SniperState.BIDDING));        
    }

    public void showsSniperHasLostAution(
        FakeAuctionServer auction, int lastPrice, int lastBid) {
        driver.showsSniperStatus(
            auction.getItemId(), lastPrice, lastBid,
            SnipersTableModel.textFor(SniperState.LOST));
    }

    public void hasShownSniperIsWinning(
        FakeAuctionServer auction, int winningBid) {
        driver.showsSniperStatus(
            auction.getItemId(), winningBid, winningBid,
            SnipersTableModel.textFor(SniperState.WINNING));
    }

    public void showsSniperHasWonAution(
        FakeAuctionServer auction, int lastPrice) {
        driver.showsSniperStatus(
            auction.getItemId(), lastPrice, lastPrice,
            SnipersTableModel.textFor(SniperState.WON));
    }

    public void stop() {
        if (driver != null) {
            driver.dispose();
        }
    }
    
    private static String[] arguments(FakeAuctionServer... auctions) {
        String[] arguments = new String[auctions.length + 3];
        arguments[0] = XMPP_HOSTNAME;
        arguments[1] = SNIPER_ID;
        arguments[2] = SNIPER_PASSWORD;
        for (int i = 0; i < auctions.length; i++) {
            arguments[i + 3] = auctions[i].getItemId();
        }
        return arguments;
    }
}
