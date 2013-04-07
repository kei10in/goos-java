package kei10in.auctionsniper;

import java.util.EventListener;

public interface SniperListener extends EventListener {
    void sniperWinning();
    void sniperBidding(SniperSnapshot state);
    void sniperWon();
    void sniperLost();
}
