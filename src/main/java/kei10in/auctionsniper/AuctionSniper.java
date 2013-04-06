package kei10in.auctionsniper;

public class AuctionSniper implements AuctionEventListener {
    private final Auction auction; 
    private final SniperListener sniperListener;
    
    private boolean isWinning = false; 

    public AuctionSniper(Auction auction, SniperListener listener) {
        this.auction = auction;
        this.sniperListener = listener;
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
        switch (priceSource) {
        case FromSniper:
            isWinning = true;
            sniperListener.sniperWinning();
            break;
        case FromOtherBidder:
            auction.bid(price + increment);
            sniperListener.sniperBidding();
            break;
        }
    }

}
