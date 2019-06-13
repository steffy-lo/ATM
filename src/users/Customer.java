package users;

import accounts.*;

import java.io.Serializable;
import java.util.ArrayList;

public class Customer extends User implements Serializable {

    private final int MAX_ATTEMPT = 4;
    private ArrayList<Account> accounts;
    private Chequing primary;
    private double creditScore;
    private int loginAttempt;
    private String securityQuestion;
    private String securityAnswer;

    Customer(String name, String pw, String securityQuestion, String securityAnswer) {
        this(name, securityQuestion, securityAnswer);
        this.setPassword(pw);
    }

    Customer(String name, String securityQuestion, String securityAnswer){
        super(name);
        this.securityAnswer = securityAnswer;
        this.securityQuestion = securityQuestion;
        accounts = new ArrayList<>();
        Chequing chequing = new Chequing();
        this.setPrimary(chequing);
        accounts.add(chequing);
        creditScore = 50;
        loginAttempt = 0;
    }

    public void addAccount(Account account){ accounts.add(account); }

    public double getCreditScore() { return creditScore; }

    public void loginAttempted(){
        loginAttempt++;
    }

    public boolean outOfAttempts(){
        return loginAttempt >= MAX_ATTEMPT;
    }

    public void clearLoginAttempts(){
        loginAttempt = 0;
    }

    public String getSecurityQuestion(){
        return securityQuestion;
    }

    public boolean verifySecurityAnswer(String answer){
        return securityAnswer.equalsIgnoreCase(answer);
    }

    /**
     * Updates the credit score of the customer based on any late payments and credit utilization.
     * Highest credit score is 100.0 and lowest is 0.0
     */
    public void updateCreditScore() {
        double totalDebt = 0;
        double totalLimit = 0;
        double size = getDebtAccounts().size();

        // Payment History (50%)
        for (DebtAccount debtAccount : getDebtAccounts()) {
            if (debtAccount.getBalance() != 0 && creditScore > 0) creditScore -= (1/size)*50;
            else {
                if (creditScore < 100) creditScore += (1/size)*50;
            }
            totalDebt += debtAccount.getBalance();
            totalLimit += debtAccount.getMaximum();
        }
        // Utilization (50%)
        double utilization = totalDebt/totalLimit;
        if (utilization > 0.5 && creditScore > 0) {
            creditScore -= utilization*50;
        } else {
            if (creditScore < 100) creditScore += (1-utilization)*50;
        }
    }

    private ArrayList<DebtAccount> getDebtAccounts() {
        ArrayList<DebtAccount> debtAccounts = new ArrayList<>();
        for (Account acc: this.accounts) {
            if (acc instanceof DebtAccount) {
                debtAccounts.add((DebtAccount)acc);
            }
        }
        return debtAccounts;
    }

    public ArrayList<Account> getAccounts(){ return this.accounts; }

    public ArrayList<Account> getAccountsByType(String type){
        return getAccountsByType(new String[] {type});
    }

    public ArrayList<Account> getAccountsByType(String[] type){
        ArrayList<Account> accounts = new ArrayList<>();
        for (Account acc: this.accounts) {
            for (String i: type) {
                if (acc.getAccountNum().startsWith(i)) {
                    accounts.add(acc);
                }
            }
        }
        return accounts;
    }

    public ArrayList<Account> getJointAccounts(){
        ArrayList<Account> accounts = new ArrayList<>();
        for (Account acc: this.accounts){
            if (acc.isJoint()){
                accounts.add(acc);
            }
        }
        return accounts;
    }


    /**
     * Returns the net balance of currently held accounts.
     * Computed as total balance of asset accounts - total balance of debt accounts
     * Note: stocks are assets
     *
     * @return the net balance of currently held accounts
     */
    public Double getNetTotal(){
        double netBalance = 0;
        for (Account acc: accounts){
            if (acc instanceof AssetAccount) netBalance += acc.getBalance();
            else if (acc instanceof DebtAccount) netBalance -= acc.getBalance();
            else netBalance += ((StockAccount)acc).value();
        }
        return netBalance;
    }

    /**
     * Sets the primary chequing account of the Customer as the given one.
     * Sets the given chequing account's primary attribute to true.
     *
     * @param primary the chequing account to be set as primary
     */
    public void setPrimary(Chequing primary){
        this.primary = primary;
    }

    Chequing getPrimary(){
        return this.primary;
    }

}
