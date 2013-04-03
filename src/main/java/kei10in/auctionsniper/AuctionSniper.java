package kei10in.auctionsniper;

public class AuctionSniper implements AuctionEventListener {
    private final SniperListener listener;

    public AuctionSniper(SniperListener listener) {
        this.listener = listener;
    }
    
    public void auctionClosed() {
        listener.sniperLost();
    }

    public void currentPrice(int price, int increment) {
        // TODO Auto-generated method stub

    }

}
