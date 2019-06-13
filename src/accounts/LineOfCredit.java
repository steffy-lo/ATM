package accounts;

import java.io.*;

public class LineOfCredit extends DebtAccount implements Serializable {
    /**
     A line of credit account allows you to transfer money in or out. But it also displays a positive
     balance when the user owes money and a negative balance when the user overpays.
     */

    private static final String ACCOUNT_TYPE = "line of credit account";
    private static int num;

    public LineOfCredit(double max) {
        super(max);
        num ++;
        accountNum = Account.LOCREDIT + num;
    }

    /**
     * Increases the account balance by the given parameter and outputs to the outgoing.txt file
     * Precondition: amount is positive
     *
     * @param amount the amount that will be paid
     * @return whether the transaction is successful
     */
    boolean payBill(double amount) {
        if (balance + amount > getMaximum()) {
            return false;
        }
        else {
            balance += amount;
            billsOut(amount);
            return true;
        }
    }

    public String toString(){ return "Account No. " + accountNum; }

    public String overview() {
        return "Account No.: " + getAccountNum() + "\n" +
                "Type: Line of Credit Account \n" +
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
