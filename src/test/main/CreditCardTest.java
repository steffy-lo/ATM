package main;

import org.junit.Before;
import org.junit.Test;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import static org.mockito.Mockito.mock;

public class CreditCardTest {
    private CreditCard creditCard;

    @Before
    public void setUp(){
        creditCard = new CreditCard(1000);
    }

    @Test
    public void testWithdrawZeroBalance() throws IOException {
        Machine atm = mock(Machine.class);
        creditCard.withdraw(100, atm);

        assert(creditCard.getBalance() == 100);
    }

    @Test
    public void testWithdrawPositiveOverLimit() throws IOException {
        creditCard.setBalance(1000);
        Machine atm = mock(Machine.class);
        creditCard.withdraw(200, atm);

        assert(creditCard.getBalance() == 1000.0);
    }

    @Test
    public void testDepositCash() throws IOException {
        PrintWriter out = new PrintWriter(new FileWriter(creditCard.deposits, false));
        out.println("cash, 700");
        out.close();

        Machine atm = mock(Machine.class);
        Transaction transaction = mock(Transaction.class);
        creditCard.deposit(atm, transaction);

        assert(creditCard.getBalance() == -700.0);
    }

    @Test
    public void testDepositCheque() throws IOException {
        PrintWriter out = new PrintWriter(new FileWriter(creditCard.deposits, false));
        out.println("cheque, 700");
        out.close();

        Machine atm = mock(Machine.class);
        Transaction transaction = mock(Transaction.class);
        creditCard.deposit(atm, transaction);

        assert(creditCard.getBalance() == -700.0);
    }

    @Test
    public void testTransferIn() {
        creditCard.transfer(100);

        assert(creditCard.getBalance() == -100.0);
    }

    @Test
    public void testTransferOut() {
        creditCard.transfer(-100);

        assert(creditCard.getBalance() == 0);
    }

    @Test
    public void testTransferOutOverLimit() {
        creditCard.setBalance(1000);
        creditCard.transfer(-200);

        assert(creditCard.getBalance() == 1000);
    }
}
