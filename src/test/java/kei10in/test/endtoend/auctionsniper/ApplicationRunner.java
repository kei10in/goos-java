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

    public void startBiddingIn(final FakeAuctionServer auction) {
        Thread thread = new Thread("Test Application") {
            @Override
            public void run() {
                try {
                    Main.main(
                        XMPP_HOSTNAME,
                        SNIPER_ID,
                        SNIPER_PASSWORD,
                        auction.getItemId());
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
}
