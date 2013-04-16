package kei10in.auctionsniper;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.SwingUtilities;

import kei10in.auctionsniper.ui.MainWindow;
import kei10in.auctionsniper.ui.SnipersTableModel;
import kei10in.auctionsniper.ui.SwingThreadSniperListener;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

public class Main {
    private static final int ARG_HOSTNAME = 0;
    private static final int ARG_USERNAME = 1;
    private static final int ARG_PASSWORD = 2;

    /**
     * @param args
     */
    public static void main(String ... args) throws Exception {
        Main main = new Main();
        XMPPConnection connection = connection(
            args[ARG_HOSTNAME],
            args[ARG_USERNAME],
            args[ARG_PASSWORD]);
        main.disconnectWhenUICloses(connection);
        main.addUserRequestListenerFor(connection);
    }

    private static XMPPConnection connection(
        String hostname, String username, String password)
            throws XMPPException {
        XMPPConnection connection = new XMPPConnection(hostname);
        connection.connect();
        connection.login(username, password, XMPPAuction.AUCTION_RESOURCE);

        return connection;
    }

    private final SnipersTableModel snipers = new SnipersTableModel();
    private MainWindow ui;
    private ArrayList<Auction> notToBeGCd = new ArrayList<Auction>();

    public Main() throws Exception {
        startUserInterface();
    }

    private void startUserInterface() throws Exception {
        SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
                ui = new MainWindow(snipers);
            }
        });
    }
    
    private void addUserRequestListenerFor(final XMPPConnection connection) {
        ui.addUserRequestListener(new UserRequestListener() {
            public void joinAuction(String itemId) {
                snipers.addSniper(SniperSnapshot.joining(itemId));

                Auction auction = new XMPPAuction(connection, itemId);
                notToBeGCd.add(auction);
                auction.addAuctionEventListener(
                    new AuctionSniper(itemId, auction,
                        new SwingThreadSniperListener(snipers)));
                auction.join();
            }
        });
    }

    private void disconnectWhenUICloses(final XMPPConnection connection) {
        ui.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                connection.disconnect();
            }
        });
    }
}
