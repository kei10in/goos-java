package kei10in.auctionsniper;

import kei10in.auctionsniper.util.Announcer;

public class AuctionSniper implements AuctionEventListener {
    private final Auction auction;
    private final Announcer<SniperListener> sniperListeners =
        Announcer.to(SniperListener.class);
    private SniperSnapshot snapshot;
    
    private boolean isWinning = false; 

    public AuctionSniper(String itemId, Auction auction) {
        this.auction = auction;
        this.snapshot = SniperSnapshot.joining(itemId);
    }
    
    public void auctionClosed() {
        snapshot = snapshot.closed();
        nofityChang();
    }

    public void currentPrice(
        int price, int increment, PriceSource priceSource) {
        isWinning = priceSource == PriceSource.FromSniper;
        if (isWinning) {
            snapshot = snapshot.winning(price);
        } else {
            final int bid = price + increment;
            auction.bid(bid);
            snapshot = snapshot.bidding(price, bid);
        }
        nofityChang();
    }
    
    public SniperSnapshot getSnapshot() {
        return snapshot;
    }
    
    public void addSniperListener(SniperListener listener) {
        sniperListeners.addListener(listener);
    }

    private void nofityChang() {
        sniperListeners.announce().sniperStateChanged(snapshot);
    }
}
