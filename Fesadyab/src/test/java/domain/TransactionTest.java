package domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TransactionTest {

    @Test
    public void testEqualsSameId() {
        Transaction txn1 = new Transaction();
        txn1.setTransactionId(1);

        Transaction txn2 = new Transaction();
        txn2.setTransactionId(1);

        assertEquals(txn1, txn2);
    }

    @Test
    public void testEqualsDifferentId() {
        Transaction txn1 = new Transaction();
        txn1.setTransactionId(1);

        Transaction txn2 = new Transaction();
        txn2.setTransactionId(2);

        assertNotEquals(txn1, txn2);
    }

    @Test
    public void testEqualsDifferentObjectType() {
        Transaction txn = new Transaction();
        assertNotEquals(txn, "some string");
    }

    @Test
    public void testEqualsWithNull() {
        Transaction txn = new Transaction();
        assertNotEquals(txn, null);
    }

    @Test
    public void testEqualsWithSameObject() {
        Transaction txn = new Transaction();
        assertEquals(txn, txn);
    }

    @Test
    public void testGettersAndSetters() {
        Transaction txn = new Transaction();
        txn.setTransactionId(1);
        txn.setAccountId(2);
        txn.setAmount(100);
        txn.setDebit(true);

        assertEquals(1, txn.getTransactionId());
        assertEquals(2, txn.getAccountId());
        assertEquals(100, txn.getAmount());
        assertTrue(txn.isDebit());
    }
}
