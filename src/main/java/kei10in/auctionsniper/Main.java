package kei10in.auctionsniper;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.SwingUtilities;

import kei10in.auctionsniper.ui.MainWindow;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

public class Main {
    private static final int ARG_HOSTNAME = 0;
    private static final int ARG_USERNAME = 1;
    private static final int ARG_PASSWORD = 2;
    private static final int ARG_ITEM_ID = 3;

    public static final String AUCTION_RESOURCE = "Auction";
    public static final String ITEM_ID_AS_LOGIN = "auction-%s";
    public static final String AUCTION_ID_FORMAT =
        ITEM_ID_AS_LOGIN + "@%s/" + AUCTION_RESOURCE;

    /**
     * @param args
     */
    public static void main(String ... args) throws Exception {
        Main main = new Main();
        main.joinAuction(
            connection(
                args[ARG_HOSTNAME],
                args[ARG_USERNAME],
                args[ARG_PASSWORD]),
            args[ARG_ITEM_ID]);
    }

    private static XMPPConnection connection(
        String hostname, String username, String password)
            throws XMPPException {
        XMPPConnection connection = new XMPPConnection(hostname);
        connection.connect();
        connection.login(username, password, AUCTION_RESOURCE);

        return connection;
    }

    private static String auctionId(String itemId, XMPPConnection connection) {
        return String.format(
            AUCTION_ID_FORMAT, itemId, connection.getServiceName());
    }



    private MainWindow ui;
    @SuppressWarnings("unused")
    private Chat notToBeGCd;

    public Main() throws Exception {
        startUserInterface();
    }

    private void startUserInterface() throws Exception {
        SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
                ui = new MainWindow();
            }
        });
    }

    private void joinAuction(XMPPConnection connection, String itemId)
        throws XMPPException {

        disconnectWhenUICloses(connection);
        final Chat chat = connection.getChatManager().createChat(
            auctionId(itemId, connection), null);
        this.notToBeGCd = chat;

        Auction auction = new XMPPAuction(chat);
        chat.addMessageListener(
            new AuctionMessageTranslator(
                connection.getUser(),
                new AuctionSniper(
                    itemId, auction, new SniperStateDisplayer())));
        auction.join();
    }

    private void disconnectWhenUICloses(final XMPPConnection connection) {
        ui.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                connection.disconnect();
            }
        });
    }
    
    
    public class SniperStateDisplayer implements SniperListener {
        
        public void sniperWinning() {
            showStatus(MainWindow.STATUS_WINNING);
        }

        public void sniperBidding(SniperState state) {
            showStatus(MainWindow.STATUS_BIDDING);
        }
        
        public void sniperWon() {
            showStatus(MainWindow.STATUS_WON);
        }
        
        public void sniperLost() {
            showStatus(MainWindow.STATUS_LOST);
        }

        private void showStatus(final String status) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    ui.showStatus(status);
                }
            });
        }
    
    }

}
