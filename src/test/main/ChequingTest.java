package main;

import org.junit.Before;
import org.junit.Test;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import static org.mockito.Mockito.mock;

public class ChequingTest {
    private Chequing chequing;

    @Before
    public void setUp(){
        chequing = new Chequing();
    }

    @Test
    public void testWithdrawZeroBalance() throws IOException {
        Machine atm = mock(Machine.class);
        chequing.withdraw(100, atm);

        assert(chequing.getBalance() == 0.0);
    }

    @Test
    public void testWithdrawPositiveOver() throws IOException {
        chequing.setBalance(100);
        Machine atm = mock(Machine.class);
        chequing.withdraw(200, atm);

        assert(chequing.getBalance() == -100.0);
    }

    @Test
    public void testWithdrawPositiveOverLimit() throws IOException {
        chequing.setBalance(100);
        Machine atm = mock(Machine.class);
        chequing.withdraw(300, atm);

        assert(chequing.getBalance() == -100.0);
    }

    @Test
    public void testDepositCash() throws IOException {
        PrintWriter out = new PrintWriter(new FileWriter(chequing.deposits, false));
        out.println("cash, 700");
        out.close();

        Machine atm = mock(Machine.class);
        Transaction transaction = mock(Transaction.class);
        chequing.deposit(atm, transaction);

        assert(chequing.getBalance() == 700.0);
    }

    @Test
    public void testDepositCheque() throws IOException {
        PrintWriter out = new PrintWriter(new FileWriter(chequing.deposits, false));
        out.println("cheque, 700");
        out.close();

        Machine atm = mock(Machine.class);
        Transaction transaction = mock(Transaction.class);
        chequing.deposit(atm, transaction);

        assert(chequing.getBalance() == 700.0);
    }

    @Test
    public void testTransferIn() {
        chequing.transfer(100);

        assert(chequing.getBalance() == 100.0);
    }

    @Test
    public void testTransferOut() {
        chequing.setBalance(100);
        chequing.transfer(-100);

        assert(chequing.getBalance() == 0);
    }

    @Test
    public void testTransferOutOver() {
        chequing.setBalance(100);
        chequing.transfer(-200);

        assert(chequing.getBalance() == -100);
    }

    @Test
    public void testTransferOutOverLimit() {
        chequing.setBalance(100);
        chequing.transfer(-300);

        assert(chequing.getBalance() == 100);
    }

}
