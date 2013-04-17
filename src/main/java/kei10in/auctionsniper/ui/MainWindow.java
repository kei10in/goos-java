package kei10in.auctionsniper.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import kei10in.auctionsniper.Item;
import kei10in.auctionsniper.SniperPortfolio;
import kei10in.auctionsniper.UserRequestListener;
import kei10in.auctionsniper.util.Announcer;

public class MainWindow extends JFrame {
    private static final long serialVersionUID = 1L;
    public static final String APPLICATION_TITLE = "Auction Sniper";
    public static final String MAIN_WINDOW_NAME = "main window";
    public static final String SNIPER_STATUS_NAME = "sniper status";
    public static final String SNIPERS_TABLE_NAME = "snipers table";
    public static final String NEW_ITEM_ID_NAME = "item id";
    public static final String NEW_ITEM_STOP_PRICE_NAME = "stop price";
    public static final String JOIN_BUTTON_NAME = "join";
    
    private final Announcer<UserRequestListener> userRequests =
        new Announcer<UserRequestListener>(UserRequestListener.class);

    public MainWindow(SniperPortfolio portfolio) {
        super(APPLICATION_TITLE);
        setName(MainWindow.MAIN_WINDOW_NAME);
        fillContentPane(makeSniperTable(portfolio), makeControls());
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }
    
    public void addUserRequestListener(UserRequestListener listener) {
        userRequests.addListener(listener);
    }
    

    private void fillContentPane(JTable snipersTable, JPanel controls) {
        final Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        
        contentPane.add(controls, BorderLayout.NORTH);
        contentPane.add(new JScrollPane(snipersTable), BorderLayout.CENTER);
    }
    
    private JTable makeSniperTable(SniperPortfolio portfolio) {
        SnipersTableModel model = new SnipersTableModel();
        portfolio.addPortfolioListener(model);
        final JTable snipersTable = new JTable(model);
        snipersTable.setName(SNIPERS_TABLE_NAME);
        return snipersTable;
    }
    
    private JPanel makeControls() {
        final JTextField itemIdField = itemIdField();
        final JFormattedTextField stopPriceField = stopPriceField();

        JPanel controls = new JPanel(new FlowLayout());
        controls.add(new JLabel("Item:"));
        controls.add(itemIdField);
        controls.add(new JLabel("Stop price:"));
        controls.add(stopPriceField);
        
        JButton joinAuctionButton = new JButton("Join Auction");
        joinAuctionButton.setName(JOIN_BUTTON_NAME);
        joinAuctionButton.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {
            userRequests.announce().joinAuction(
                new Item(itemId(), stopPrice()));
           } 
           
           private String itemId() {
               return itemIdField.getText();
           }
           
           private int stopPrice() {
               return ((Number)stopPriceField.getValue()).intValue();
           }
        });
        controls.add(joinAuctionButton);
        
        return controls;
    }

    private JTextField itemIdField() {
        final JTextField itemIdField = new JTextField();
        itemIdField.setColumns(10);
        itemIdField.setName(NEW_ITEM_ID_NAME);
        return itemIdField;
    }

    private JFormattedTextField stopPriceField() {
        final JFormattedTextField stopPriceField =
            new JFormattedTextField(NumberFormat.getIntegerInstance());
        stopPriceField.setColumns(7);
        stopPriceField.setName(NEW_ITEM_STOP_PRICE_NAME);
        return stopPriceField;
    }

}
