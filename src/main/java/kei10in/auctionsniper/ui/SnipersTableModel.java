package kei10in.auctionsniper.ui;

import javax.swing.table.AbstractTableModel;

import kei10in.auctionsniper.SniperSnapshot;

public class SnipersTableModel extends AbstractTableModel {
    private static final long serialVersionUID = 1L;
    private static final SniperSnapshot STARTING_UP = new SniperSnapshot("", 0, 0);
    
    private SniperSnapshot sniperState = STARTING_UP;    
    private String statusText = MainWindow.STATUS_JOINING;
    

    public int getColumnCount() {
        return Column.values().length;
    }

    public int getRowCount() {
        return 1;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (Column.at(columnIndex)) {
        case ITEM_IDENTIFIER:
            return sniperState.itemId;
        case LAST_PRICE:
            return sniperState.lastPrice;
        case LAST_BID:
            return sniperState.lastBid;
        case SNIPER_STATE:
            return statusText;
        default:
            throw new IllegalArgumentException("No column at " + columnIndex);                
        }
    }
    
    public void setStatusText(String newStatusText) {
        statusText = newStatusText;
        fireTableRowsUpdated(0, 0);
    }
    
    public void sniperStatusChanged(
        SniperSnapshot newSniperState, String newStatusText) {
        this.sniperState = newSniperState;
        this.statusText = newStatusText;
        fireTableRowsUpdated(0, 0);
    }

}
