package kei10in.auctionsniper;

import java.util.EventListener;

public interface SniperListener extends EventListener {
    void sniperWinning();
    void sniperBidding(SniperState state);
    void sniperWon();
    void sniperLost();
}
