package kei10in.auctionsniper;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.SwingUtilities;

import kei10in.auctionsniper.ui.MainWindow;
import kei10in.auctionsniper.xmpp.XMPPAuctionHouse;

public class Main {
    private static final int ARG_HOSTNAME = 0;
    private static final int ARG_USERNAME = 1;
    private static final int ARG_PASSWORD = 2;

    /**
     * @param args
     */
    public static void main(String ... args) throws Exception {
        Main main = new Main();
        XMPPAuctionHouse auctionHouse = new XMPPAuctionHouse(
            args[ARG_HOSTNAME],
            args[ARG_USERNAME],
            args[ARG_PASSWORD]);
        main.disconnectWhenUICloses(auctionHouse);
        main.addUserRequestListenerFor(auctionHouse);
    }

    private final SniperPortfolio portfolio = new SniperPortfolio();
    private MainWindow ui;

    public Main() throws Exception {
        startUserInterface();
    }

    private void startUserInterface() throws Exception {
        SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
                ui = new MainWindow(portfolio);
            }
        });
    }
    
    private void addUserRequestListenerFor(final AuctionHouse auctionHouse) {
        ui.addUserRequestListener(new SniperLauncher(auctionHouse, portfolio));
    }

    private void disconnectWhenUICloses(final XMPPAuctionHouse auctionHouse) {
        ui.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                auctionHouse.disconnect();
            }
        });
    }
}
