package kei10in.auctionsniper;

import kei10in.auctionsniper.util.Announcer;

public class AuctionSniper implements AuctionEventListener {
    private final Item item;
    private final Auction auction;
    private final Announcer<SniperListener> sniperListeners =
        Announcer.to(SniperListener.class);
    private SniperSnapshot snapshot;
    
    private boolean isWinning = false; 

    public AuctionSniper(Item item, Auction auction) {
        this.item = item;
        this.auction = auction;
        this.snapshot = SniperSnapshot.joining(item.identifier);
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
            if (item.allowsBid(bid)) {
                auction.bid(bid);
                snapshot = snapshot.bidding(price, bid);
            } else {
                snapshot = snapshot.losing(price);
            }
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
