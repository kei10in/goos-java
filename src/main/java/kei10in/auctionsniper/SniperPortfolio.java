package kei10in.auctionsniper;

import java.util.ArrayList;

import kei10in.auctionsniper.util.Announcer;

public class SniperPortfolio implements SniperCollector {

    private final ArrayList<AuctionSniper> snipers =
        new ArrayList<AuctionSniper>();
    private final Announcer<PortfolioListener> announcer =
        Announcer.to(PortfolioListener.class);
    
    public void addSniper(AuctionSniper sniper) {
        snipers.add(sniper);
        announcer.announce().sniperAdded(sniper);
    }
    
    public void addPortfolioListener(PortfolioListener listener) {
        announcer.addListener(listener);
    }
}
