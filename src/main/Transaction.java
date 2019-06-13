package main;

import java.io.Serializable;
import java.util.Date;

public class Transaction implements Serializable {
    private double amount;
    private Date date;
    private int type;
    private int id;
    private String note;

    /**A constructor for the Transaction class.
     *
     * @param amount the amount involved in the transaction
     * @param type the type of transaction (e.g., transfer out, transfer in, withdrawal, deposit)
     * @param note the note of the transaction (e.g., an account num, ATM, bill payment)
     */
    public Transaction(double amount, int type, String note) {
        this.note = note;
        this.amount = amount;
        this.date = new Date();
        this.type = type;
        this.id = Math.abs(this.hashCode());
    }

    public Transaction(String note) {
        this.note = note;
    }

    Date getDate() {
        return this.date;
    }

    String getNote() {
        return this.note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getAmount() {
        return this.amount;
    }

    public int getType() {return this.type;}

    public String toString() {
        return "Transaction " + id + "\n" +
                "Amount: " + amount + "\n" +
                "Type: " + type + "\n" +
                "Date: " + date + "\n" +
                "Note: " + note + "\n";

    }

    public static abstract class TransactionType{
        public static final int TRANSFEROUT = 1;
        public static final int TRANSFERIN = 2;
        public static final int WITHDRAW = 3;
        public static final int DEPOSIT = 4;
    }

}
