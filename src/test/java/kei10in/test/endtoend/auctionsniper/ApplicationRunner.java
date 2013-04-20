package kei10in.test.endtoend.auctionsniper;

import kei10in.auctionsniper.Main;
import kei10in.auctionsniper.SniperState;
import kei10in.auctionsniper.ui.MainWindow;
import kei10in.auctionsniper.ui.SnipersTableModel;

public class ApplicationRunner {
    public static final String SNIPER_XMPP_ID = "sniper@localhost/Auction";
    public static final String SNIPER_ID = "sniper";
    public static final String SNIPER_PASSWORD = "sniper";
    private static final String XMPP_HOSTNAME = "localhost";
    private AuctionSniperDriver driver;

    public void startBiddingIn(final FakeAuctionServer ... auctions) {
        startSniper();
        for (FakeAuctionServer auction: auctions) {
            openBiddingFor(auction.getItemId(), Integer.MAX_VALUE);
        }
    }

    public void startBiddingWithStopPrice(
        FakeAuctionServer auction, int stopPrice) {
        startSniper();
        openBiddingFor(auction.getItemId(), stopPrice);
    }
    
    private void openBiddingFor(final String itemId, final int stopPrice) {
        driver.startBiddingWithStopPrice(itemId, stopPrice);
        driver.showsSniperStatus(
            itemId, 0, 0, SnipersTableModel.textFor(SniperState.JOINING));
    }
    
    public void hasShownSniperIsBidding(
        FakeAuctionServer auction, int lastPrice, int lastBid) {
        driver.showsSniperStatus(
            auction.getItemId(), lastPrice, lastBid,
            SnipersTableModel.textFor(SniperState.BIDDING));        
    }
    
    public void hasShownSniperIsLosing(
        FakeAuctionServer auction, int lastPrice, int lastBid) {
        driver.showsSniperStatus(
            auction.getItemId(), lastPrice, lastBid,
            SnipersTableModel.textFor(SniperState.LOSING));        
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
    
    public void showsSniperHasFailed(FakeAuctionServer auction) {
        driver.showsSniperStatus(
            auction.getItemId(), 0, 0,
            SnipersTableModel.textFor(SniperState.FAILED));
    }

    public void reportsInvalidMessage(FakeAuctionServer auction,
        String brokenMessage) {
        // TODO Auto-generated method stub
    }

    public void stop() {
        if (driver != null) {
            driver.dispose();
        }
    }
    
    private void startSniper() {
        Thread thread = new Thread("Test Application") {
            @Override
            public void run() {
                try {
                    Main.main(XMPP_HOSTNAME, SNIPER_ID, SNIPER_PASSWORD);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.setDaemon(true);

        // in order to avoid deadlock between SwingUtilities.invokeAndWait and
        // GesturePerformer
        driver = new AuctionSniperDriver(1000);
        thread.start();
        
        driver.hasTitle(MainWindow.APPLICATION_TITLE);
        driver.hasColumnTitles();
    }

}
