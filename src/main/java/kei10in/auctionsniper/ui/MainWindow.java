package kei10in.auctionsniper.ui;

import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import kei10in.auctionsniper.SniperState;

public class MainWindow extends JFrame {
    private static final long serialVersionUID = 1L;
    public static final String MAIN_WINDOW_NAME = "main window";
    public static final String SNIPER_STATUS_NAME = "sniper status";
    public static final String SNIPERS_TABLE_NAME = "snipers table";
    
    public static final String STATUS_JOINING = "Joining";
    public static final String STATUS_BIDDING = "Bidding";
    public static final String STATUS_WINNING = "WINNING";
    public static final String STATUS_WON = "WON";
    public static final String STATUS_LOST = "Lost";
    
    private final SnipersTableModel snipers = new SnipersTableModel();


    public MainWindow() {
        super("Auction Sniper");
        setName(MainWindow.MAIN_WINDOW_NAME);
        fillContentPane(makeSniperTable());
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }
    

    private void fillContentPane(JTable snipersTable) {
        final Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        
        contentPane.add(new JScrollPane(snipersTable), BorderLayout.CENTER);
    }
    
    private JTable makeSniperTable() {
        final JTable snipersTable = new JTable(snipers);
        snipersTable.setName(SNIPERS_TABLE_NAME);
        return snipersTable;
    }
    
    public void sniperStatusChanged(
        SniperState sniperState, String statusText) {
        snipers.sniperStatusChanged(sniperState, statusText);
    }
   
    public void showStatus(String statusText) {
        snipers.setStatusText(statusText);
    }

}
