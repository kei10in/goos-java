package kei10in.auctionsniper;

import java.util.EventListener;

public interface SniperListener extends EventListener {
    void sniperWinning();
    void sniperBidding();
    void sniperWon();
    void sniperLost();
}
