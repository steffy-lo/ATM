package accounts;

import main.Machine;
import main.Transaction;
import main.Transfer;

import java.io.*;

public abstract class AssetAccount extends Account implements Serializable {

    private double lowestBalance;

    AssetAccount(double lowestBalance) {
        super();
        this.balance = 0;
        this.lowestBalance = lowestBalance;
    }

    public double getLowestBalance() {
        return lowestBalance;
    }

    /**
     * Transfers money to another account.
     *
     * @param account the destination account.
     * @param amount the amount to be transferred
     * @return successful or not.
     */
    public boolean transferOut(Account account, double amount) {
        if (this.balance - amount < lowestBalance) {
            return false;
        }
        else{
            balance -= amount;
            account.transferIn(this, amount);
            addTransaction(new Transfer(amount, account));
            return true;
        }
    }

    void transferIn(Account account, double amount) {
        this.balance += amount;
        addTransaction(new Transaction(amount, Transaction.TransactionType.TRANSFERIN,
                "From Account "+account.getAccountNum()));
    }

    /**
     * Decreases the account balance by the given parameter and outputs to the outgoing.txt file
     * Precondition: amount is positive
     *
     * @param amount the amount that will be paid
     * @throws IOException if the file outgoing.txt cannot be written to
     */
    void payBill(double amount) throws IOException {
        balance -= amount;
        addTransaction(new Transaction(-amount, Transaction.TransactionType.TRANSFEROUT, "Bill"));
        billsOut(amount);
    }

    public void addBalance(double amount){
        balance += amount;
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
        transaction.setAmount(amount);
        balance += amount;
        addTransaction(transaction);
        return amount;
    }

}