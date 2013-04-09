package kei10in.auctionsniper.ui;

import javax.swing.table.AbstractTableModel;

import kei10in.auctionsniper.SniperListener;
import kei10in.auctionsniper.SniperSnapshot;
import kei10in.auctionsniper.SniperState;

public class SnipersTableModel extends AbstractTableModel
    implements SniperListener {
    private static final long serialVersionUID = 1L;
    
    
    private static final String[] STATUS_TEXT = {
        "Joining",
        "Bidding",
        "WINNING",
        "Lost",
        "WON",
    };
    private static final SniperSnapshot STARTING_UP =
        new SniperSnapshot("", 0, 0, SniperState.JOINING);
        
    private SniperSnapshot snapshot = STARTING_UP;
    
    @Override
    public String getColumnName(int column) {
        return Column.at(column).name;
    }

    public int getColumnCount() {
        return Column.values().length;
    }

    public int getRowCount() {
        return 1;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        return Column.at(columnIndex).valueIn(snapshot);
    }
    
    public void sniperStateChanged(SniperSnapshot newSnapshot) {
        this.snapshot = newSnapshot;
        fireTableRowsUpdated(0, 0);
    }
    
    public static String textFor(SniperState state) {
        return STATUS_TEXT[state.ordinal()];
    }

}
