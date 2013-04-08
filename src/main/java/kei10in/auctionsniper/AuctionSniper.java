package kei10in.auctionsniper;

public class AuctionSniper implements AuctionEventListener {
    private final Auction auction; 
    private final SniperListener sniperListener;
    private SniperSnapshot snapshot;
    
    private boolean isWinning = false; 

    public AuctionSniper(
        String itemId, Auction auction, SniperListener listener) {
        this.auction = auction;
        this.sniperListener = listener;
        this.snapshot = SniperSnapshot.joining(itemId);
    }

    public void auctionClosed() {
        if (isWinning) {
            sniperListener.sniperWon();
        } else {
            sniperListener.sniperLost();
        }
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
        sniperListener.sniperStateChanged(snapshot);
    }

}
