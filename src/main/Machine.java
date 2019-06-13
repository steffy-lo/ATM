package main;

import java.io.*;

public class Machine implements Serializable {
    /**
    index 0: no. of $5 bills
    index 1: no. of $10 bills
    index 2: no. of $20 bills
    index 3: no. of $50 bills
     */
    private int[] bills;
    private File alerts;

    Machine() {
        bills = new int[]{100, 100, 100, 100};
        alerts = new File("phase2/alerts.txt");
    }

    /**
     * Using helper methods, this method updates the no. of bills currently in the machine from the withdrawal and
     * outputs to the alerts.txt file if any type of bill is less than 20.
     *
     * @param amount the amount to withdraw from the machine
     * @throws IOException if the alerts.txt cannot be written to
     */
    public void withdraw(double amount) throws IOException {
        if (amount >= 50  && bills[3] > 0) {
            amount = updateBills(amount, 50, 3, "withdraw");
        }
        if (amount >= 20  && bills[2] > 0) {
            amount = updateBills(amount, 20, 2, "withdraw");
        }
        if (amount >= 10  && bills[1] > 0) {
            amount = updateBills(amount, 10, 1, "withdraw");
        }
        if (amount >= 5  && bills[0] > 0) {
            updateBills(amount, 5, 0, "withdraw");
        }
        checkBillCount();
    }

    /**
     * Using the updateBills helper method, updates the no. of bills currently in the machine.
     *
     * @param amount the amount to be deposited into the machine
     */
    public void deposit(double amount) {
        if (amount >= 50) {
            amount = updateBills(amount, 50, 3, "deposit");
        }
        if (amount >= 20) {
            amount = updateBills(amount, 20, 2, "deposit");
        }
        if (amount >= 10) {
            amount = updateBills(amount, 10, 1, "deposit");
        }
        if (amount >= 5) {
            updateBills(amount, 5, 0, "deposit");
        }
    }

    /**
     * Updates the number of bills in the machine using an automatic algorithm.
     * Automatic algorithm: increment/decrement the $50 bills as much as possible from the given amount, followed by
     * $20, $10, and finally $5
     *
     * @param amount the amount to deposit/withdraw from the machine
     * @param bill the value of the bill (i.e., 50, 20, 10, or 5)
     * @param i the bill type (0: $5 bill, 1: $10 bill, 2: $20 bill, 3: $50)
     * @param action the string representation of a withdrawal or a deposit
     * @return the amount left after the decrement/increment of the bill
     */
    private double updateBills(double amount, int bill, int i, String action) {
        if (action.equals("withdraw")) {
            int decrement = (int) (amount / bill);
            if (decrement < bills[i]) {
                bills[i] -= decrement;
                amount -= decrement * bill; // update amount
            } else {
                bills[i] = 0;
                amount -= bills[i] * bill;
            }
        } else if (action.equals("deposit")) {
            int increment = (int) (amount / bill);
            bills[i] += increment;
            amount -= increment * bill; // update amount
        }
        return amount;
    }

    /**
     * Write to the alerts.txt file if any bill type count is less than 20.
     *
     * @throws IOException if the alerts.txt cannot be written to
     */
    private void checkBillCount() throws IOException {
        PrintWriter out = new PrintWriter(new FileWriter(alerts, true));
        if (bills[0] < 20) {
            out.println("Need to restock $5 bills.");
        }
        if (bills[1] < 20) {
            out.println("Need to restock $10 bills.");
        }
        if (bills[2] < 20) {
            out.println("Need to restock $20 bills.");
        }
        if (bills[3] < 20) {
            out.println("Need to restock $50 bills.");
        }
        out.close();
    }

    public int[] getBills() { return this.bills; }

    void setBills(int billType, int amount) {bills[billType] = amount;}

    File getAlerts() {return this.alerts;}

    public void restock(int billType, int amount) { bills[billType] += amount; }
}
