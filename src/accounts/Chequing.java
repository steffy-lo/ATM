package accounts;

import main.Machine;

import java.io.IOException;
import java.io.Serializable;

public class Chequing extends AssetAccount implements Serializable {
    /**
     Chequing accounts store a positive balance when money is stored in the account. The balance is
     allowed to decrease to -$100 if the user withdraws money when there is a positive balance, but
     the withdrawal is more than the balance. A negative balance beyond -$100 will not be allowed.
     A withdrawal from a chequing account with a negative balance will not be allowed.
     */
    private static final String ACCOUNT_TYPE = "chequing account";
    private static final double LOWLIMIT = -100;
    private static int num = 0;

    public Chequing() {
        super(LOWLIMIT);
        num ++;
        accountNum = Account.CHEQUING + num;
    }


    /**
     * Withdraws the specified amount from the account.
     * It also updates the amount of bills in the atm that it withdraws from.
     *
     * @param amount the amount to be withdrawn
     * @param atm an instance of the Machine class that the amount is withdrawn from
     * @throws IOException in the case that the alerts.txt file cannot be written to if it needs to
     * @return returns true if withdrawal is successful and returns false otherwise
     */
    public boolean withdraw(double amount, Machine atm) throws IOException {
        if (balance > 0 || balance - amount > getLowestBalance()) {
            atm.withdraw(amount);
            balance -= amount;
            return true;
        } else {
            return false;
        }
    }

    public String toString() { return "Account No. " + accountNum; }

    public String overview() {
        return "Account No.: " + getAccountNum() + "\n" +
                "Type: Chequing Account \n" +
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
