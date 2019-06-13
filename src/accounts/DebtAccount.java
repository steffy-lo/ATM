package accounts;

import main.Machine;
import main.Transaction;
import java.io.*;

public abstract class DebtAccount extends Account implements Serializable {
    /** Debt accounts display a positive balance when the user owes money and a negative balance
     when the user overpays. All debt accounts also have a maximum amount of debt that can be incurred.
     A debt is incurred after a transfer out or a withdrawal.
     */
    private double maximum;

    DebtAccount(double limit) {
        super();
        balance = 0;
        maximum = limit;
    }

    public double getMaximum() { return maximum; }

    public boolean withdraw(double amount, Machine atm) throws IOException {
        if (incurDebt(amount)) {
            atm.withdraw(amount);
            return true;
        }
        return false;
    }

    void transferIn(Account account, double amount){
        this.balance -= amount;
        addTransaction(new Transaction(amount, Transaction.TransactionType.TRANSFERIN,
                "From Account "+account.getAccountNum()));
    }

    public void addBalance(double amount){
        balance -= amount;
    }

    /**
     * Determines if account is allowed to incur anymore debt.
     *
     * @param amount the amount of debt incurred
     */
    boolean incurDebt(double amount) {
        if (balance + Math.abs(amount) <= maximum) {
            balance += amount;
            return true;
        } else {
            System.out.println("Your current balance is: " + balance +".");
            System.out.println("You cannot incur more than " + maximum + " in debt.");
            return false;
        }
    }

    /**
     * Deposits the amount specified in the first line of the deposits.txt file and updates the deposits.txt file
     * by removing the first line it had processed.
     * If the source of deposit is cash, the amount of bills will be incremented in the atm by an automatic algorithm
     *
     * @param atm an instance of the Machine class that cash will be deposited to
     */
    public double deposit(Machine atm){
        Transaction transaction = new Transaction(0, Transaction.TransactionType.DEPOSIT, "ATM");
        double amount = super.deposit(atm, transaction);
        transaction.setAmount(-amount);
        balance -= amount;
        addTransaction(transaction);
        return amount;
    }
}

