package kei10in.auctionsniper.ui;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import kei10in.auctionsniper.AuctionSniper;
import kei10in.auctionsniper.PortfolioListener;
import kei10in.auctionsniper.SniperListener;
import kei10in.auctionsniper.SniperSnapshot;
import kei10in.auctionsniper.SniperState;
import kei10in.auctionsniper.util.Defect;

public class SnipersTableModel extends AbstractTableModel
    implements SniperListener, PortfolioListener {
    private static final long serialVersionUID = 1L;
    
    private static final String[] STATUS_TEXT = {
        "Joining",
        "Bidding",
        "WINNING",
        "Losing",
        "Lost",
        "WON",
        "Failed"
    };
    
    private ArrayList<SniperSnapshot> snapshots =
        new ArrayList<SniperSnapshot>();
    
    @Override
    public String getColumnName(int column) {
        return Column.at(column).name;
    }

    public int getColumnCount() {
        return Column.values().length;
    }

    public int getRowCount() {
        return snapshots.size();
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        return Column.at(columnIndex).valueIn(snapshots.get(rowIndex));
    }
    
    public void sniperStateChanged(SniperSnapshot newSnapshot) {
        int row = rowMatching(newSnapshot);
        snapshots.set(row, newSnapshot);
        fireTableRowsUpdated(row, row);
    }
    
    private int rowMatching(SniperSnapshot snapshot) {
        for (int i = 0; i < snapshots.size(); i++) {
            if (snapshot.isForSameItemAs(snapshots.get(i))) {
                snapshots.set(i, snapshot);
                return i;
            }
        }
        throw new Defect("Cannot find match for " + snapshot); 
    }
    
    public static String textFor(SniperState state) {
        return STATUS_TEXT[state.ordinal()];
    }

    public void sniperAdded(AuctionSniper sniper) {
        addSniperSnapshot(sniper.getSnapshot());
        sniper.addSniperListener(new SwingThreadSniperListener(this));
    }

    private void addSniperSnapshot(SniperSnapshot snapshot) {
        snapshots.add(snapshot);
        fireTableRowsInserted(snapshots.size() - 1, snapshots.size() - 1);
    }

}
