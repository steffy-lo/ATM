package accounts;

import java.io.Serializable;

public class CreditCard extends DebtAccount implements Serializable {
    /**
     A credit card account display a positive balance when the user owes money and a negative balance
     when the user overpays. It is not possible to transfer money out of a credit card account. But it
     is possible to transfer money in.
     */

    private static final String ACCOUNT_TYPE = "credit card account";
    private static int num;

    public CreditCard(double max) {
        super(max);
        num ++;
        accountNum = Account.CREDIT + num;
    }

    public String toString() { return "Account No. " + accountNum; }

    public String overview() {
        return "Account No.: " + getAccountNum() + "\n" +
                "Type: Credit Card Account \n" +
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