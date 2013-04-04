package kei10in.auctionsniper;

public class AuctionSniper implements AuctionEventListener {
    private final Auction auction; 
    private final SniperListener sniperListener;

    public AuctionSniper(Auction auction, SniperListener listener) {
        this.auction = auction;
        this.sniperListener = listener;
    }
    
    public void auctionClosed() {
        sniperListener.sniperLost();
    }

    public void currentPrice(int price, int increment) {
        auction.bid(price + increment);
        sniperListener.sniperBidding();
    }

}
