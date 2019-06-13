package accounts;

import main.Machine;
import main.Transaction;
import yahoofinance.Stock;

import java.io.*;
import java.util.Scanner;

public class StockAccount extends Account implements Serializable {
    private static final String ACCOUNT_TYPE = "stocks account";
    private SerializableStock stock;
    private double boughtPrice;
    private static int count;

    public StockAccount(Stock stock, int num) {
        super();
        this.stock = new SerializableStock(stock);
        this.boughtPrice = stock.getQuote().getPrice().doubleValue();
        this.balance = num;
        count++;
        accountNum = Account.STOCK + count;
    }

    int numPurchased(double amount, double price) {
        int num = (int)(amount/price);
        if (num == 0) {
            System.out.println("The amount is too little for" + stock.getStock().getName() + " stocks.");
            return 0;
        }
        return num;
    }


    void transferIn(Account account, double amount) {
        balance += amount;
        System.out.println("You've transferred in " + amount + " " + stock.getStock().getName() + " stocks");
    }

    /**
     * Withdraw in the Stock account means selling stocks.
     * If the amount specified is less than the price of a stock, the amount to withdraw is deemed to little and the
     * withdraw will not be successful.
     * Otherwise, the withdraw will be successful and the balance will be decremented by the number of stocks sold.
     *
     * @param amount the amount of money to withdraw from stocks
     * @param atm the amount of money the atm is withdrawing
     * @throws IOException if alerts.txt cannot be written to
     */
    public boolean withdraw(double amount, Machine atm) throws IOException {
        double price = stock.getStock().getQuote().getPrice().doubleValue();
        if (balance*price >= amount) {
            int num = numPurchased(amount, price);
            if (num != 0) {
                balance -= num;
                double withdrawAmt = num * price;
                System.out.println("You've withdraw " + Math.abs(num) + " " + stock.getStock().getName() + " stocks for " + "$" + withdrawAmt);
                atm.withdraw(withdrawAmt);
                return true;
            }
        } else {
            System.out.println("Withdraw is not successful. Insufficient balance.");
        }
        return false;
    }

    /**
     * Deposits the amount specified in the first line of the deposits.txt file and updates the deposits.txt file
     * by removing the first line it had processed.
     * If the source of deposit is cash, the amount of bills will be incremented in the atm by an automatic algorithm.
     *
     * Deposit in the Stock account means investing in a new stock using cash.
     * If cash is not sufficient to purchase new stocks, the deposit is unsuccessful and balance is not updated.
     * Otherwise, deposit will be successful and the balance will be updated to reflect the new number of stocks
     * currently held from the purchase of the new stocks.
     *
     * @param atm an instance of the Machine class to deposit into
     */
    public double deposit(Machine atm, Transaction transaction) {
        double amount = 0;
        try {
            double price = stock.getStock().getQuote().getPrice().doubleValue();
            Scanner sc = new Scanner(new FileInputStream(deposits));
            String[] depositInfo = null;
            if (sc.hasNextLine()) {
                depositInfo = sc.nextLine().split(",");
            }
            if (depositInfo != null) {
                int num = numPurchased(Double.parseDouble(depositInfo[1]), price);
                if (num != 0) {
                    balance += num;
                    amount = num * price; // essentially the value of the stocks deposited
                    System.out.println("You've deposit " + Math.abs(num) + " " + stock.getStock().getName() + " stocks");
                    if (depositInfo[0].equals("cash")) {
                        atm.deposit(amount);
                        transaction.setNote("Cash");
                    } else transaction.setNote("Cheque");
                    transaction.setAmount(amount);
                }

                // Update deposits file
                StringBuilder remaining = new StringBuilder();
                while (sc.hasNextLine()) {
                    remaining.append(sc.nextLine()).append('\n');
                }
                PrintWriter out = new PrintWriter(new FileWriter(deposits, false));
                out.print(""); // clear
                out = new PrintWriter(new FileWriter(deposits, true));
                out.write(remaining.toString());
                out.close();
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return amount;
    }

    @Override
    public void addBalance(double amount) {
        balance += amount;
    }

    public String getAccountNum() {
        return accountNum;
    }

    public String toString() { return "Account No. " + accountNum; }

    public String overview() {
        return stock.getStock().getName() + " ("+ stock.getStock().getSymbol() + ")" + " " + stock.getStock().getQuote().getChangeInPercent() + "%" + "\n" +
                "Total Value: " + Math.round(value()) + "\n" +
                "Total Returns: " + Math.round(totalReturns()) + " ("+ Math.round(percentageReturn()) + "%)" + "\n" +
                "Balance: " + balance + "\n" +
                "Date created: " + creationDate + "\n" +
                "------------------------------------------------------" + "\n" +
                "Quote: " + stock.getStock().getQuote() + "\n";

    }
    public SerializableStock getSerializableStock() {
        return this.stock;
    }

    private double totalReturns() {
        return (getCurrentPrice() - boughtPrice)*balance;
    }

    public double percentageReturn() {
        return (getCurrentPrice() - boughtPrice)/boughtPrice;
    }


    public double value() {
        return balance * getCurrentPrice();
    }

    public double getBoughtPrice() {
        return this.boughtPrice;
    }

    private double getCurrentPrice() { return stock.getStock().getQuote().getPrice().doubleValue(); }

    public String getAccountType(){
        return ACCOUNT_TYPE;
    }

    public static String type(){
        return ACCOUNT_TYPE;
    }
}
