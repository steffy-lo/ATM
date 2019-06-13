package main;

import org.junit.Before;
import org.junit.Test;
import accounts.TimeDeposit;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import static org.mockito.Mockito.mock;

public class TimeDepositTest {
    private TimeDeposit timeDeposit;

    @Before
    public void setUp(){
        timeDeposit = new TimeDeposit(1);
    }

    @Test
    public void testWithdrawZeroBalanceDuringTerm() throws IOException {
        Machine atm = mock(Machine.class);
        timeDeposit.setBalance(200);
        timeDeposit.withdraw(100, atm);

        assert(timeDeposit.getBalance() < (200 - 100));
    }

    @Test
    public void testWithdrawZeroBalanceAfterTerm() throws IOException {
        Machine atm = mock(Machine.class);
        timeDeposit.setBalance(200);
        timeDeposit.addInterest();
        timeDeposit.withdraw(100, atm);

        assert(timeDeposit.getBalance() == 200*(1+timeDeposit.getInterest()) - 100);
    }

    @Test
    public void testWithdrawPositiveOver() throws IOException {
        timeDeposit.setBalance(100);
        Machine atm = mock(Machine.class);
        timeDeposit.withdraw(200, atm);

        assert(timeDeposit.getBalance() == 100);
    }

    @Test
    public void testDepositCash() throws IOException {
        PrintWriter out = new PrintWriter(new FileWriter(timeDeposit.deposits, false));
        out.println("cash, 700");
        out.close();

        Machine atm = mock(Machine.class);
        Transaction transaction = mock(Transaction.class);
        timeDeposit.deposit(atm, transaction);

        assert(timeDeposit.getBalance() == 700.0);
    }

    @Test
    public void testDepositCheque() throws IOException {
        PrintWriter out = new PrintWriter(new FileWriter(timeDeposit.deposits, false));
        out.println("cheque, 700");
        out.close();

        Machine atm = mock(Machine.class);
        Transaction transaction = mock(Transaction.class);
        timeDeposit.deposit(atm, transaction);

        assert(timeDeposit.getBalance() == 700.0);
    }

    @Test
    public void testTransferIn() {
        timeDeposit.transfer(100);

        assert(timeDeposit.getBalance() == 100.0);
    }

    @Test
    public void testTransferOut() {
        timeDeposit.setBalance(100);
        timeDeposit.transfer(-100);

        assert(timeDeposit.getBalance() == 0);
    }

    @Test
    public void testTransferOutOver() {
        timeDeposit.setBalance(100);
        timeDeposit.transfer(-200);

        assert(timeDeposit.getBalance() == 100);
    }

    @Test
    public void testInterest() {
        timeDeposit.setBalance(100);
        timeDeposit.addInterest();

        assert(timeDeposit.getBalance() == 100*(1+timeDeposit.getInterest()));
    }
}
