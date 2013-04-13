package kei10in.test.unit.auctionsniper;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import kei10in.auctionsniper.SniperSnapshot;
import kei10in.auctionsniper.SniperState;
import kei10in.auctionsniper.ui.Column;
import kei10in.auctionsniper.ui.SnipersTableModel;
import kei10in.auctionsniper.util.Defect;

import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class SnipersTableModelTest {
    @Rule
    public final JUnitRuleMockery context = new JUnitRuleMockery();
    private TableModelListener listener = context.mock(TableModelListener.class);
    private SnipersTableModel cut = new SnipersTableModel();
    
    @Before
    public void attachModelListener() {
        cut.addTableModelListener(listener);
    }

    @Test
    public void hasEnoughColumns() {
        assertThat(cut.getColumnCount(), equalTo(Column.values().length));    
    }
    
    @Test
    public void setsUpColumnHeadings() {
        for (Column column: Column.values()) {
            assertThat(
                column.name, equalTo(cut.getColumnName(column.ordinal())));
        }
    }
    
    @Test
    public void setsSniperValuesInColumns() {
        SniperSnapshot joining = SniperSnapshot.joining("item id");
        SniperSnapshot bidding = joining.bidding(555, 666);
        context.checking(new Expectations() {{
            allowing(listener).tableChanged(with(anyInsertionEvent()));
            oneOf(listener).tableChanged(with(aChangeInRow(0)));
        }});
        
        cut.addSniperSnapshot(joining);
        cut.sniperStateChanged(
            new SniperSnapshot("item id", 555, 666, SniperState.BIDDING));
        
        assertRowMatchesSnapshot(0, bidding);
    }
    
    @Test
    public void notifiesListenersWhenAddingASniper() {
        SniperSnapshot joining = SniperSnapshot.joining("item123");
        context.checking(new Expectations() {
            {
                oneOf(listener).tableChanged(with(anInsertionAtRow(0)));
            }
        });
        
        assertThat(cut.getRowCount(), equalTo(0));
        cut.addSniperSnapshot(joining);
        
        assertThat(cut.getRowCount(), equalTo(1));
        assertRowMatchesSnapshot(0, joining);
    }
    
    @Test
    public void addsSnipersInaAdditionOrder() {
        context.checking(new Expectations() {
            {
                ignoring(listener);
            }
        });
        
        cut.addSniperSnapshot(SniperSnapshot.joining("item 0"));
        cut.addSniperSnapshot(SniperSnapshot.joining("item 1"));
        
        assertThat(
            cellValue(0, Column.ITEM_IDENTIFIER),
            hasToString(equalTo("item 0")));
        assertThat(
            cellValue(1, Column.ITEM_IDENTIFIER),
            hasToString(equalTo("item 1")));
    }
    
    @Test
    public void updateCorrectRowForSniper() {
        context.checking(new Expectations() { {
            allowing(listener).tableChanged(with(anyInsertionEvent()));

            oneOf(listener).tableChanged(with(aChangeInRow(1)));
          }});
          
          cut.addSniperSnapshot(SniperSnapshot.joining("item 0"));
          SniperSnapshot joining1 = SniperSnapshot.joining("item 1");
          cut.addSniperSnapshot(joining1);

          SniperSnapshot winning1 = joining1.winning(123);
          cut.sniperStateChanged(winning1);
          
          assertRowMatchesSnapshot(1, winning1);
    }
    
    @Test(expected=Defect.class)
    public void throwsDefectIfNoExistingSniperForAnUpdate() {
        cut.sniperStateChanged(SniperSnapshot.joining("item 0"));
    }
    
    
    private Object cellValue(int rowIndex, Column column) {
        return cut.getValueAt(rowIndex, column.ordinal());
    }

    private void assertRowMatchesSnapshot(
        int rowIndex, SniperSnapshot snapshot) {
        assertEquals(
            cellValue(rowIndex, Column.ITEM_IDENTIFIER), snapshot.itemId);
        assertEquals(
            cellValue(rowIndex, Column.LAST_PRICE), snapshot.lastPrice);
        assertEquals(
            cellValue(rowIndex, Column.LAST_BID), snapshot.lastBid);
        assertEquals(
            cellValue(rowIndex, Column.SNIPER_STATE),
            SnipersTableModel.textFor(snapshot.state));

    }
    
    private Matcher<TableModelEvent> anyInsertionEvent() {
        return hasProperty("type", equalTo(TableModelEvent.INSERT));
    }
    
    private Matcher<TableModelEvent> aChangeInRow(int rowIndex) {
        return samePropertyValuesAs(new TableModelEvent(cut, rowIndex));
    }
    
    private Matcher<TableModelEvent> anInsertionAtRow(int rowIndex) {
        return samePropertyValuesAs(new TableModelEvent(
            cut, rowIndex, rowIndex, TableModelEvent.ALL_COLUMNS,
            TableModelEvent.INSERT));
    }
}
