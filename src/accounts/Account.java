package accounts;

import main.Machine;
import main.Transaction;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

public abstract class Account implements Serializable {

    public static final String CREDIT = "1";
    public static final String LOCREDIT = "2";
    public static final String CHEQUING = "3";
    public static final String SAVING = "4";
    public static final String TIMEDEPOSIT = "5";
    public static final String STOCK = "6";
    static final File deposits = new File("phase2/deposits.txt");
    private static final File outgoing = new File("phase2/outgoing.txt");
    String accountNum;
    Date creationDate;
    double balance;
    private ArrayList<Transaction> transactions;
    private boolean isJoint = false;

    Account() {
        this.creationDate = new Date();
        transactions = new ArrayList<>();
    }

    public boolean isJoint() {return isJoint;}

    public void setJoint() {this.isJoint = true;}

    public void setBalance(double balance) { this.balance = balance; }

    public double getBalance() { return this.balance; }

    public abstract boolean withdraw(double amount, Machine atm) throws IOException;

    public abstract String toString();

    public String getAccountNum(){
        return this.accountNum;
    }

    abstract void transferIn(Account account, double amount);

    public abstract void addBalance(double amount);

    void billsOut(double amount){
        PrintWriter out;
        try {
            out = new PrintWriter(new FileWriter(outgoing, true));
            out.write("transferred out " + amount + "\n");
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public double deposit(Machine atm, Transaction transaction){
        double amount = 0;
        try {
            Scanner sc = new Scanner(new FileInputStream(deposits));
            String[] depositInfo = null;
            if (sc.hasNextLine()) {
                depositInfo = sc.nextLine().split(",");
            }
            if (depositInfo != null) {
                amount = Double.parseDouble(depositInfo[1]);
                if (depositInfo[0].equals("cash")) {
                    atm.deposit(amount);
                    transaction.setNote("Cash");
                } else transaction.setNote("Cheque");
                balance += amount;
                transaction.setAmount(amount);

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
        }catch (Exception e){
            e.printStackTrace();
        }
        return amount;
    }

    public abstract String overview();

    public void addTransaction(Transaction transaction) { transactions.add(transaction); }

    public void removeTransaction(Transaction transaction) {transactions.remove(transaction);}

    public ArrayList<Transaction> getTransactions() { return this.transactions; }

}