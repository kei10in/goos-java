package kei10in.auctionsniper.ui;

import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class MainWindow extends JFrame {
    private static final long serialVersionUID = 1L;
    public static final String APPLICATION_TITLE = "Auction Sniper";
    public static final String MAIN_WINDOW_NAME = "main window";
    public static final String SNIPER_STATUS_NAME = "sniper status";
    public static final String SNIPERS_TABLE_NAME = "snipers table";
    
    private final SnipersTableModel snipers;

    public MainWindow(SnipersTableModel snipers) {
        super(APPLICATION_TITLE);
        this.snipers = snipers;
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

}
