package main;

import org.junit.Before;
import org.junit.Test;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import static org.mockito.Mockito.mock;

public class StockAccountTest {
    private StockAccount stockAccount;
    private Stock stock;

    @Before
    public void setUp() throws IOException {
        stock = YahooFinance.get("MSFT");
        stockAccount = new StockAccount(stock, 10);
    }

    @Test
    public void testWithdraw() throws IOException {
        Machine atm = mock(Machine.class);
        stockAccount.withdraw(500, atm);

        assert(stockAccount.getBalance() == 10 - (int)(500/stock.getQuote().getPrice().doubleValue()));
    }

    @Test
    public void testWithdrawZeroBalance() throws IOException {
        stockAccount.setBalance(0);
        Machine atm = mock(Machine.class);
        stockAccount.withdraw(500, atm);

        assert(stockAccount.getBalance() == 0);
    }

    @Test
    public void testWithdrawInsufficientAmount() throws IOException {
        Machine atm = mock(Machine.class);
        stockAccount.withdraw(10, atm);

        assert(stockAccount.getBalance() == 10);
    }

    @Test
    public void testWithdrawPositiveOver() throws IOException {
        Machine atm = mock(Machine.class);
        stockAccount.withdraw(2000, atm);

        assert(stockAccount.getBalance() == 10);
    }

    @Test
    public void testDepositCash() throws IOException {
        PrintWriter out = new PrintWriter(new FileWriter(stockAccount.deposits, false));
        out.println("cash, 700");
        out.close();

        Machine atm = mock(Machine.class);
        Transaction transaction = mock(Transaction.class);
        stockAccount.deposit(atm, transaction);

        assert(stockAccount.getBalance() == 10 + (int)(700.0/stock.getQuote().getPrice().doubleValue()));
    }

    @Test
    public void testDepositCashInsufficientAmount() throws IOException {
        PrintWriter out = new PrintWriter(new FileWriter(stockAccount.deposits, false));
        out.println("cash, 10");
        out.close();

        Machine atm = mock(Machine.class);
        Transaction transaction = mock(Transaction.class);
        stockAccount.deposit(atm, transaction);

        assert(stockAccount.getBalance() == 10);
    }

    @Test
    public void testDepositCheque() throws IOException {
        PrintWriter out = new PrintWriter(new FileWriter(stockAccount.deposits, false));
        out.println("cheque, 700");
        out.close();

        Machine atm = mock(Machine.class);
        Transaction transaction = mock(Transaction.class);
        stockAccount.deposit(atm, transaction);

        assert(stockAccount.getBalance() == 10 + (int)(700.0/stock.getQuote().getPrice().doubleValue()));
    }

    @Test
    public void testDepositChequeInsufficientAmount() throws IOException {
        PrintWriter out = new PrintWriter(new FileWriter(stockAccount.deposits, false));
        out.println("cheque, 10");
        out.close();

        Machine atm = mock(Machine.class);
        Transaction transaction = mock(Transaction.class);
        stockAccount.deposit(atm, transaction);

        assert(stockAccount.getBalance() == 10);
    }

    @Test
    public void testTransferIn() {
        stockAccount.transfer(500);

        assert(stockAccount.getBalance() == 10 + (int)(500.0/stock.getQuote().getPrice().doubleValue()));
    }

    @Test
    public void testTransferInInsufficientAmount() {
        stockAccount.transfer(10);

        assert(stockAccount.getBalance() == 10);
    }

    @Test
    public void testTransferOut() {
        stockAccount.transfer(-500);

        assert(stockAccount.getBalance() == 10 - (int)(500.0/stock.getQuote().getPrice().doubleValue()));
    }

    @Test
    public void testTransferOutOver() {
        stockAccount.transfer(-2000);

        assert(stockAccount.getBalance() == 10);
    }

}
