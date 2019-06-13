package accounts;

import main.Machine;

import java.io.IOException;
import java.io.Serializable;

public class Savings extends AssetAccount implements Serializable {
    /**
    A savings account stores a $0 or positive balance at all times. Before closing, on the 1st of every
    month, all savings account balances increase by a factor of 0.1%. In other words, a savings account
    with $100 in it on January 31, will have a balance of $100.10 on February 1st.
     */

    private final static String ACCOUNT_TYPE = "savings account";
    private final static double SAVING_INTEREST = 0.001;
    private static int num;

    public Savings() {
        super(0);
        num++;
        accountNum = Account.SAVING + num;

    }

    public boolean withdraw(double amount, Machine atm) throws IOException {
        if (balance >= amount) {
            balance -= amount;
            atm.withdraw(amount);
            return true;
        } else {
            return false;
        }
    }

    public void addInterest() {
        setBalance(balance * (1 + SAVING_INTEREST));
    }

    public String toString() { return "Account No. " + accountNum; }

    public String overview() {
        return "Account No.: " + getAccountNum() + "\n" +
                "Type: Savings Account \n" +
                "Balance: " + balance + "\n" +
                "Date created: " + creationDate + "\n";
    }

    public String getAccountType(){
        return ACCOUNT_TYPE;
    }

    public static String type(){
        return ACCOUNT_TYPE;
    }
}