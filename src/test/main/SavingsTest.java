package main;

import org.junit.Before;
import org.junit.Test;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import static org.mockito.Mockito.mock;

public class SavingsTest {
    private Savings savings;

    @Before
    public void setUp(){
        savings = new Savings();
    }

    @Test
    public void testWithdrawZeroBalance() throws IOException {
        Machine atm = mock(Machine.class);
        savings.withdraw(100, atm);

        assert(savings.getBalance() == 0.0);
    }

    @Test
    public void testWithdrawPositiveOver() throws IOException {
        savings.setBalance(100);
        Machine atm = mock(Machine.class);
        savings.withdraw(200, atm);

        assert(savings.getBalance() == 100);
    }

    @Test
    public void testDepositCash() throws IOException {
        PrintWriter out = new PrintWriter(new FileWriter(savings.deposits, false));
        out.println("cash, 700");
        out.close();

        Machine atm = mock(Machine.class);
        Transaction transaction = mock(Transaction.class);
        savings.deposit(atm, transaction);

        assert(savings.getBalance() == 700.0);
    }

    @Test
    public void testDepositCheque() throws IOException {
        PrintWriter out = new PrintWriter(new FileWriter(savings.deposits, false));
        out.println("cheque, 700");
        out.close();

        Machine atm = mock(Machine.class);
        Transaction transaction = mock(Transaction.class);
        savings.deposit(atm, transaction);

        assert(savings.getBalance() == 700.0);
    }

    @Test
    public void testTransferIn() {
        savings.transfer(100);

        assert(savings.getBalance() == 100.0);
    }

    @Test
    public void testTransferOut() {
        savings.setBalance(100);
        savings.transfer(-100);

        assert(savings.getBalance() == 0);
    }

    @Test
    public void testTransferOutOver() {
        savings.setBalance(100);
        savings.transfer(-200);

        assert(savings.getBalance() == 100);
    }

    @Test
    public void testInterest() {
        savings.setBalance(100);
        savings.addInterest();

        assert(savings.getBalance() == 100*(1+savings.getInterest()));
    }
}
