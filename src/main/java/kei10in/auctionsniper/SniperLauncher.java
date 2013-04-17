package kei10in.auctionsniper;


public class SniperLauncher implements UserRequestListener {
    private final AuctionHouse auctionHouse;
    private final SniperCollector collector;
    
    public SniperLauncher(
        AuctionHouse auctionHouse,
        SniperCollector collector) {
        this.auctionHouse = auctionHouse;
        this.collector = collector;
    }
    
    public void joinAuction(Item item) {
        Auction auction = auctionHouse.auctionFor(item.identifier);
        AuctionSniper sniper = new AuctionSniper(item.identifier, auction);
        auction.addAuctionEventListener(sniper);
        collector.addSniper(sniper);
        auction.join();
    }
}
