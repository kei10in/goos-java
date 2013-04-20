package kei10in.test.unit.auctionsniper;

import java.util.logging.LogManager;
import java.util.logging.Logger;

import kei10in.auctionsniper.xmpp.LoggingXMPPFailureReporter;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.AfterClass;
import org.junit.Rule;
import org.junit.Test;

public class LoggingXMPPFailureReporterTest {
    @Rule
    public final JUnitRuleMockery context = new JUnitRuleMockery() {
        {
            setImposteriser(ClassImposteriser.INSTANCE); 
        }
    };
    
    private final Logger logger = context.mock(Logger.class);
    private final LoggingXMPPFailureReporter cut =
        new LoggingXMPPFailureReporter(logger);
    
    @AfterClass
    public static void resetLogging() {
        LogManager.getLogManager().reset();
    }
    
    @Test
    public void writesMessageTranslationFailureToLog() {
        context.checking(new Expectations() {
            {
                oneOf(logger).severe("<auction id> "
                    + "Could not translate message \"bad message\" "
                    + "because \"java.lang.Exception: bad\"");
            }
        });
        cut.cannotTranslateMessage(
            "auction id", "bad message", new Exception("bad"));
    }
}
