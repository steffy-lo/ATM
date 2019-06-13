package main;

import accounts.Account;
import accounts.Savings;
import users.BankManager;
import users.Customer;

import java.io.*;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

class SystemManager implements Serializable {
    private static final String ADMIN_CODE = "I love jellies";
    private SystemData data;
    private RequestManager requestManager;
    private Machine billStockManger;
    private final String serializationPath = "SystemData.ser";

    /**
     * Initializes SystemManager by reading and storing all HashMaps from SystemData.ser. Also sets the clock to current
     * computer time and, if it is the first day of the month, adds interest to all savings account and updates all
     * credit scores.
     */
    SystemManager() {
        requestManager = new RequestManager(this);
        billStockManger = new Machine();
        initialize();
    }

    /**
     * Runs all setup procedure for SystemManager: reading SystemData.ser and set local time.
     * Adds interest and updates credit score if today is the first date of the month.
     */
    void initialize() {
        //reads all HashMaps from SystemData.ser
        try {
            FileInputStream fileInputStream = new FileInputStream(serializationPath);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            data = (SystemData) objectInputStream.readObject();
            objectInputStream.close();
            fileInputStream.close();
        } catch (IOException | ClassNotFoundException e) {
            data = new SystemData();
        }

        LocalDate today = LocalDate.now();
        String fullDate = today.toString();
        String date = fullDate.substring(fullDate.length() - 2);
        if (date.equals("01")) {
            addInterest();
            updateCreditScore();
        }

    }

    RequestManager getRequestManager() {
        return requestManager;
    }

    /**
     * Writes all current customers and managers to SystemData.ser.
     */
    void save() {
        try {
            FileOutputStream objectFos = new FileOutputStream(serializationPath);
            ObjectOutputStream objectOos = new ObjectOutputStream(objectFos);
            objectOos.writeObject(data);
            objectOos.close();
            objectFos.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /**
     * Adds interest and updates credit score for each month passed between previous and now.
     *
     * @param previous Calendar with previous time
     * @param now      Calendar with current time
     */
    void updateFeatures(Calendar previous, Calendar now) {
        int monthDiff = now.get(Calendar.MONTH) - previous.get(Calendar.MONTH);
        for (int i = 0; i < monthDiff; i++) {
            addInterest();
            updateCreditScore();
        }
    }

    /**
     * Adds interest to all existing savings account of customers in SystemData.
     */
    private void addInterest() {
        for (Customer customer : data.customers.values()) {
            for (Account account : customer.getAccounts()) {
                if (account instanceof Savings) {
                    ((Savings) account).addInterest();
                }
            }
        }
    }

    /**
     * Updates credit scores of all customers in SystemData.
     */
    private void updateCreditScore() {
        for (Customer customer : data.customers.values()) {
            customer.updateCreditScore();
        }
    }

    /**
     * Adds a customer with key login to customers in SystemData.
     *
     * @param login    the reference/key for customer
     * @param customer Customer object containing all info
     */
    void addCustomer(String login, Customer customer) {
        data.customers.put(login, customer);
    }

    /**
     * Adds a BankManager bm with key username to managers in SystemData.
     *
     * @param username the reference/key for bm
     * @param bm       BankManager object containing all info
     */
    void addManager(String username, BankManager bm) {
        data.managers.put(username, bm);
    }

    /**
     * Returns a Customer object of corresponding name.
     *
     * @param name the key for corresponding Customer
     * @return a Customer object
     */
    Customer getCustomer(String name) {
        return data.customers.get(name);
    }

    /**
     * Returns a BankManager object of corresponding name.
     *
     * @param managerName the key for corresponding BankManager
     * @return a BankManager object
     */
    BankManager getManager(String managerName) {
        return data.managers.get(managerName);
    }

    void setNewDateTime(Date date) {
        data.calendar.setTime(date);
    }

    Calendar getCalendar() {
        return data.calendar;
    }

    Machine getATM() {
        return data.atm;
    }

    boolean verifyAdminCode(String str) {
        return ADMIN_CODE.equals(str);
    }

    private class SystemData implements Serializable {
        HashMap<String, Customer> customers = new HashMap<>();
        HashMap<String, BankManager> managers = new HashMap<>();
        Calendar calendar = Calendar.getInstance();
        Machine atm = new Machine();
    }
}
