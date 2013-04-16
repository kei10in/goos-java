package kei10in.test.integration.auctionsniper.ui;

import static org.hamcrest.Matchers.equalTo;
import kei10in.auctionsniper.UserRequestListener;
import kei10in.auctionsniper.ui.MainWindow;
import kei10in.auctionsniper.ui.SnipersTableModel;
import kei10in.test.endtoend.auctionsniper.AuctionSniperDriver;

import org.junit.BeforeClass;
import org.junit.Test;
import com.objogate.wl.swing.probe.ValueMatcherProbe;

public class MainWindowTest {
    private final SnipersTableModel tableModel = new SnipersTableModel();
    private final MainWindow mainWindow = new MainWindow(tableModel);
    private final AuctionSniperDriver driver = new AuctionSniperDriver(100);

    @Test
    public void makesUserRequestWhenJoinButtonClicked() {
        final ValueMatcherProbe<String> buttonProbe =
            new ValueMatcherProbe<String>(
                equalTo("an item-id"), "join request");
        
        mainWindow.addUserRequestListener(
            new UserRequestListener() {
                public void joinAuction(String itemId) {
                    buttonProbe.setReceivedValue(itemId);
                }
            });
        
        driver.startBiddingFor("an item-id");
        driver.check(buttonProbe);
    }
    
    @BeforeClass
    public static void setupKeyboardLayout() {
        System.setProperty("com.objogate.wl.keyboard", "US");
    }

}
