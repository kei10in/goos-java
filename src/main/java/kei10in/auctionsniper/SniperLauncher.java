package kei10in.auctionsniper;

import java.util.ArrayList;

import kei10in.auctionsniper.ui.SnipersTableModel;
import kei10in.auctionsniper.ui.SwingThreadSniperListener;

public class SniperLauncher implements UserRequestListener {
    private final AuctionHouse auctionHouse;
    private final SnipersTableModel snipers;
    private ArrayList<Auction> notToBeGCd = new ArrayList<Auction>();
    
    public SniperLauncher(
        AuctionHouse auctionHouse,
        SnipersTableModel snipers) {
        this.auctionHouse = auctionHouse;
        this.snipers = snipers;
    }
    
    public void joinAuction(String itemId) {
        snipers.addSniper(SniperSnapshot.joining(itemId));

        Auction auction = auctionHouse.auctionFor(itemId);
        notToBeGCd.add(auction);
        AuctionSniper sniper = new AuctionSniper(
            itemId, auction, new SwingThreadSniperListener(snipers)); 
        auction.addAuctionEventListener(sniper);
        auction.join();
    }
}
