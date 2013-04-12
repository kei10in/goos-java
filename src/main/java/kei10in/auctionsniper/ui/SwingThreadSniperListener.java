package kei10in.auctionsniper.ui;

import javax.swing.SwingUtilities;

import kei10in.auctionsniper.SniperListener;
import kei10in.auctionsniper.SniperSnapshot;

public class SwingThreadSniperListener implements SniperListener {
    private final SniperListener listener;

    public SwingThreadSniperListener(SniperListener listener) {
        this.listener = listener;
    }

    public void sniperStateChanged(final SniperSnapshot snapshot) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                listener.sniperStateChanged(snapshot);
            } 
        });
    }
}
