package kei10in.auctionsniper.ui;

import java.awt.Color;

import javax.swing.*;
import javax.swing.border.LineBorder;

public class MainWindow extends JFrame {
    private static final long serialVersionUID = 1L;
    public static final String MAIN_WINDOW_NAME = "main window";
    public static final String SNIPER_STATUS_NAME = "sniper status";
    
    public static final String STATUS_JOINING = "Joining";
    public static final String STATUS_BIDDING = "Bidding";
    public static final String STATUS_WINNING = "WINNING";
    public static final String STATUS_WON = "WON";
    public static final String STATUS_LOST = "Lost";

    private final JLabel sniperStatus = createLabel(STATUS_JOINING);
    
    public MainWindow() {
        super("Auction Sniper");
        setName(MainWindow.MAIN_WINDOW_NAME);
        add(sniperStatus);
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }
    
    private static JLabel createLabel(String initialText) {
        JLabel result = new JLabel(initialText);
        result.setName(MainWindow.SNIPER_STATUS_NAME);
        result.setBorder(new LineBorder(Color.BLACK));
        return result;
    }
   
    public void showStatus(String status) {
        sniperStatus.setText(status);
    }

}
