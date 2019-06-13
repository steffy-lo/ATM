package accounts;

import main.Machine;

import java.io.IOException;

public class TimeDeposit extends Savings {

    private static final String ACCOUNT_TYPE = "time deposit account";
    private static final double TIMEDEPOSIT_INTEREST = 0.01;
    private int termLength;
    private int remainingTerm;
    private static int num;

    public TimeDeposit(int termLength) {
        super();
        this.termLength = termLength;
        remainingTerm = termLength;
        num ++;
        accountNum = Account.TIMEDEPOSIT + num;
    }

    /**
     * Withdraws the specified amount from the account.
     * It also updates the amount of bills in the atm that it withdraws from.
     * A penalty is administered if the account has a remaining term left.
     *
     * @param amount the amount to be withdrawn
     * @param atm an instance of the Machine class that the amount is withdrawn from
     * @throws IOException in the case that the alerts.txt file cannot be written to if it needs to
     */
    @Override
    public boolean withdraw(double amount, Machine atm) throws IOException {
        if (remainingTerm > 0) {
            double penalty = balance * TIMEDEPOSIT_INTEREST * (Math.ceil(termLength/12.0)*3.0);
            if (penalty < 25) {
                penalty = 25;
            }
            amount += penalty;
        }
        return super.withdraw(amount, atm);
    }

    @Override
    public String toString() { return "Account No. " + accountNum; }

    /**
     * Adds interest to the account every month and decrements the remaining term by 1.
     */
    @Override
    public void addInterest() {
        if (remainingTerm > 0) {
            remainingTerm--;
            balance += balance * TIMEDEPOSIT_INTEREST;
        }
    }

    public String getAccountType(){
        return ACCOUNT_TYPE;
    }

    public static String type(){
        return ACCOUNT_TYPE;
    }
}
