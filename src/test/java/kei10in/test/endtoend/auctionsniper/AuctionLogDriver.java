package kei10in.test.endtoend.auctionsniper;

import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.logging.LogManager;

import kei10in.auctionsniper.xmpp.XMPPAuctionHouse;

import org.apache.commons.io.FileUtils;
import org.hamcrest.Matcher;

public class AuctionLogDriver {
    public static final String LOG_FILE_NAME = XMPPAuctionHouse.LOG_FILE_NAME;
    private final File logFile = new File(LOG_FILE_NAME);
    
    public void hasEntry(Matcher<String> matcher) throws IOException {
        assertThat(FileUtils.readFileToString(logFile), matcher);
    }

    public void clearLog() {
        logFile.delete();
        LogManager.getLogManager().reset();
    }

}
