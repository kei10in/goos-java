package kei10in.test.endtoend.auctionsniper;

import kei10in.auctionsniper.Main;
import kei10in.auctionsniper.ui.MainWindow;

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
        driver.showsSniperStatus(MainWindow.STATUS_JOINING);
    }
    
    public void hasShownSniperIsBidding() {
        driver.showsSniperStatus(MainWindow.STATUS_BIDDING);        
    }

    public void showsSniperHasLostAution() {
        driver.showsSniperStatus(MainWindow.STATUS_LOST);
    }

    public void hasShownSniperIsWinning() {
        driver.showsSniperStatus(MainWindow.STATUS_WINNING);
    }

    public void showsSniperHasWonAution() {
        driver.showsSniperStatus(MainWindow.STATUS_WON);        
    }

    public void stop() {
        if (driver != null) {
            driver.dispose();
        }
    }
}
