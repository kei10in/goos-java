package kei10in.auctionsniper;

public class AuctionSniper implements AuctionEventListener {
    private final String itemId;
    private final Auction auction; 
    private final SniperListener sniperListener;
    
    private boolean isWinning = false; 

    public AuctionSniper(String itemId, Auction auction, SniperListener listener) {
        this.itemId = itemId;
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
            final int bid = price + increment;
            auction.bid(bid);
            sniperListener.sniperStateChanged(
                new SniperSnapshot(itemId, price, bid, SniperState.BIDDING));
            break;
        }
    }

}
