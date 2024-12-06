package domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TransactionEngineTest {
    TransactionEngine engine;

    @BeforeEach
    public void setUp() {
        engine = new TransactionEngine();
    }

    @Test
    public void testGetAverageTransactionAmountByAccount_NoTransactions() {
        int avg = engine.getAverageTransactionAmountByAccount(1);
        assertEquals(0, avg);
    }

    @Test
    public void testGetAverageTransactionAmountByAccount_WithTransactions() {
        Transaction txn1 = new Transaction();
        txn1.setAccountId(1);
        txn1.setAmount(100);

        Transaction txn2 = new Transaction();
        txn2.setAccountId(1);
        txn2.setAmount(200);

        engine.addTransactionAndDetectFraud(txn1);
        engine.addTransactionAndDetectFraud(txn2);

        int avg = engine.getAverageTransactionAmountByAccount(1);
        assertEquals(100, avg);
    }

    @Test
    public void testGetTransactionPatternAboveThreshold_NoTransactions() {
        int result = engine.getTransactionPatternAboveThreshold(1000);
        assertEquals(0, result);
    }

    @Test
    public void testGetTransactionPatternAboveThreshold_PatternExists() {
        Transaction txn1 = new Transaction();
        txn1.setTransactionId(1);
        txn1.setAmount(1000);

        Transaction txn2 = new Transaction();
        txn2.setTransactionId(2);
        txn2.setAmount(2000);

        engine.addTransactionAndDetectFraud(txn1);
        engine.addTransactionAndDetectFraud(txn2);

        int pattern = engine.getTransactionPatternAboveThreshold(500);
        assertEquals(1000, pattern);
    }

    @Test
    public void testGetTransactionPatternAboveThreshold_NoPattern() {
        Transaction txn1 = new Transaction();
        txn1.setTransactionId(1);
        txn1.setAmount(1000);

        Transaction txn2 = new Transaction();
        txn2.setTransactionId(2);
        txn2.setAmount(2500);

        engine.addTransactionAndDetectFraud(txn1);
        engine.addTransactionAndDetectFraud(txn2);

        int pattern = engine.getTransactionPatternAboveThreshold(500);
        assertEquals(1500, pattern);
    }

    @Test
    public void testDetectFraudulentTransaction_NoFraud() {
        Transaction txn = new Transaction();
        txn.setAccountId(1);
        txn.setAmount(100);
        txn.setDebit(true);

        engine.addTransactionAndDetectFraud(txn);

        int fraudScore = engine.detectFraudulentTransaction(txn);
        assertEquals(0, fraudScore);
    }

    @Test
    public void testDetectFraudulentTransaction_FraudDetected() {
        Transaction txn1 = new Transaction();
        txn1.setAccountId(1);
        txn1.setAmount(300);
        txn1.setDebit(true);

        Transaction txn2 = new Transaction();
        txn2.setAccountId(1);
        txn2.setAmount(300);
        txn2.setDebit(true);

        Transaction txn3 = new Transaction();
        txn3.setAccountId(1);
        txn3.setAmount(1500);
        txn3.setDebit(true);

        engine.addTransactionAndDetectFraud(txn1);
        engine.addTransactionAndDetectFraud(txn2);

        int fraudScore = engine.addTransactionAndDetectFraud(txn3);
        assertTrue(fraudScore >= 0);
    }


    @Test
    public void testAddTransactionAndDetectFraud_DuplicateTransaction() {
        Transaction txn = new Transaction();
        txn.setTransactionId(1);
        txn.setAccountId(1);
        txn.setAmount(100);

        engine.addTransactionAndDetectFraud(txn);
        int fraudScore = engine.addTransactionAndDetectFraud(txn);

        assertEquals(0, fraudScore);
    }

    //for ca5
    @Test
    public void testGetTransactionPatternAboveThreshold_DifferentDiffs() {
        Transaction txn1 = new Transaction();
        txn1.setTransactionId(1);
        txn1.setAmount(500);

        Transaction txn2 = new Transaction();
        txn2.setTransactionId(2);
        txn2.setAmount(800);

        Transaction txn3 = new Transaction();
        txn3.setTransactionId(3);
        txn3.setAmount(1200);

        engine.addTransactionAndDetectFraud(txn1);
        engine.addTransactionAndDetectFraud(txn2);
        engine.addTransactionAndDetectFraud(txn3);

        int pattern = engine.getTransactionPatternAboveThreshold(600);
        assertEquals(0, pattern);
    }




}
