package users;

import accounts.Account;
import accounts.Chequing;
import main.Transaction;
import main.Transfer;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;


public class BankManager extends User implements Serializable {


    public BankManager(String name, String pw) {
        super(name, pw);
    }


    public Customer createCustomer(String username, String securityQ, String securityA) {
        Customer customer = new Customer(username, securityQ, securityA);
        Chequing account = new Chequing();
        customer.addAccount(account);
        customer.setPrimary(account);
        return customer;
    }

    /**
     * Undo the previous n transaction.
     * @param acc the account to undo the transaction from
     * @param n the nth transaction to undo
     */
    public void undoTransaction(Account acc, int n) {
        ArrayList<Transaction> transactions = acc.getTransactions();
        for (int i=0; i<n;i++){
            Transaction transaction = transactions.get(transactions.size() - 1);
            double amount = transaction.getAmount();
            if (transaction.getType() == Transaction.TransactionType.TRANSFEROUT
                    || transaction.getType() == Transaction.TransactionType.WITHDRAW){
                acc.addBalance(amount);
                if (transaction instanceof Transfer) {
                    ((Transfer) transaction).getRecipient().addBalance(-amount);
                }
            }
            else {
                acc.addBalance(-amount);
                if (transaction instanceof Transfer) {
                    ((Transfer) transaction).getRecipient().addBalance(amount);
                }
            }
            acc.removeTransaction(transaction);
        }

    }

    /**
     * Sets the date and time as specified in the parameters.
     *
     * @param date a LocalDate instance that specifies the year, month, and day to set to
     * @param time contains the hour and minute to set the time to
     */
    public Calendar setDate(LocalDate date, int[] time) {
        Calendar cal = Calendar.getInstance();
        cal.set(date.getYear(), date.getMonthValue()-1, date.getDayOfMonth());
        cal.set(Calendar.HOUR, time[0]);
        cal.set(Calendar.MINUTE, time[1]);
        return cal;
    }


}
