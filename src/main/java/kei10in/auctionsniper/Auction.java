package kei10in.auctionsniper;

public interface Auction {
    void join();
    void bid(int amount);
    void addAuctionEventListener(AuctionEventListener listener);
}
