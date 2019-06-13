package main;

import accounts.LineOfCredit;
import org.junit.Before;
import org.junit.Test;

import java.io.*;

import static org.mockito.Mockito.mock;

public class LineOfCreditTest {
    private LineOfCredit lineOfCredit;

    @Before
    public void setUp() throws IOException {
        lineOfCredit = new LineOfCredit(1000);
        new PrintWriter(new FileWriter(lineOfCredit.outgoing)).write(""); // clears the file
    }

    @Test
    public void testWithdrawZeroBalance() throws IOException {
        Machine atm = mock(Machine.class);
        lineOfCredit.withdraw(100, atm);

        assert(lineOfCredit.getBalance() == 100);
    }

    @Test
    public void testWithdrawPositiveOverLimit() throws IOException {
        lineOfCredit.setBalance(1000);
        Machine atm = mock(Machine.class);
        lineOfCredit.withdraw(200, atm);

        assert(lineOfCredit.getBalance() == 1000.0);
    }

    @Test
    public void testDepositCash() throws IOException {
        PrintWriter out = new PrintWriter(new FileWriter(lineOfCredit.deposits, false));
        out.println("cash, 700");
        out.close();

        Machine atm = mock(Machine.class);
        Transaction transaction = mock(Transaction.class);
        lineOfCredit.deposit(atm, transaction);

        assert(lineOfCredit.getBalance() == -700.0);
    }

    @Test
    public void testDepositCheque() throws IOException {
        PrintWriter out = new PrintWriter(new FileWriter(lineOfCredit.deposits, false));
        out.println("cheque, 700");
        out.close();

        Machine atm = mock(Machine.class);
        Transaction transaction = mock(Transaction.class);
        lineOfCredit.deposit(atm, transaction);

        assert(lineOfCredit.getBalance() == -700.0);
    }

    @Test
    public void testTransferIn() {
        lineOfCredit.transfer(100);

        assert(lineOfCredit.getBalance() == -100.0);
    }

    @Test
    public void testTransferOut() {
        lineOfCredit.transfer(-100);

        assert(lineOfCredit.getBalance() == 100);
    }

    @Test
    public void testTransferOutOverLimit() {
        lineOfCredit.setBalance(1000);
        lineOfCredit.transfer(-200);

        assert(lineOfCredit.getBalance() == 1000);
    }

    @Test
    public void testPayBill() throws IOException {
        lineOfCredit.payBill(100);
        BufferedReader br = new BufferedReader(new FileReader(lineOfCredit.outgoing));
        String out = br.readLine();

        assert(lineOfCredit.getBalance() == 100);
        assert(out.equals("transferred out 100.0"));

    }
}
