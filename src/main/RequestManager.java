package main;

import accounts.*;
import users.BankManager;
import users.Customer;
import yahoofinance.YahooFinance;

import java.io.*;
import java.util.HashMap;

/**
 * This class managers all activity of users before they logged in.
 */
class RequestManager {
    private static final String REQUEST_FILE = "requests.txt";

    private SystemManager systemManager;

    private HashMap<String, String> accType = new HashMap<>();

    RequestManager(SystemManager systemManager) {
        this.systemManager = systemManager;

        accType.put(Account.CREDIT, "credit card account");
        accType.put(Account.LOCREDIT, "line of credit account");
        accType.put(Account.CHEQUING, "chequing account");
        accType.put(Account.SAVING, "savings account");
        accType.put(Account.TIMEDEPOSIT, "time deposit account");
        accType.put(Account.STOCK, "stocks account");
    }

    /**
     * Writes a String line into request text file.
     *
     * @param line String to be written as a line in request text file
     * @throws IOException when writer fails to write to request text file
     */
    private void writeToRequest(String line) throws IOException {
        PrintWriter pw;
        try {
            pw = new PrintWriter(new FileWriter(REQUEST_FILE, true));
        } catch (FileNotFoundException e) {
            File file = new File(REQUEST_FILE);
            if (!file.createNewFile()) {
                throw new IOException();
            }
            pw = new PrintWriter(REQUEST_FILE);
        }
        pw.write(line);
        pw.close();
    }


    String getAccType(Account acc) {
        return accType.get(acc.getAccountNum().substring(0, 1));
    }

    String getAccType(String index) {
        return accType.get(index);
    }

    /**
     * Writes a user creation request to request text file. A new chequing account request will automatically added to
     * the user request.
     * The information is written in the form "username, numeric type of account, security answer, security question".
     *
     * @param username username of request sender in String
     * @param securityQuestion security question in String
     * @param securityAnswer answer to security question in String
     */
    boolean newUserRequest(String username, String securityQuestion, String securityAnswer) {
        try {
            writeToRequest(username + "," + Account.CHEQUING + "," + securityAnswer + "," + securityQuestion + "\n");
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    /**
     * Removes the first request in request text file.
     */
    void removeFirst() throws IOException {
        BufferedReader requestReader = new BufferedReader(new FileReader(REQUEST_FILE));
        PrintWriter requestAppendWriter = new PrintWriter(new FileWriter(REQUEST_FILE, true));
        requestReader.readLine();
        String next = requestReader.readLine();
        StringBuilder remaining = new StringBuilder();
        while (next != null) {
            remaining.append(next).append('\n');
            next = requestReader.readLine();
        }
        new PrintWriter(new FileWriter(REQUEST_FILE, false)); //clears
        requestAppendWriter.write(remaining.toString());
        requestAppendWriter.close();
        requestReader.close();
    }

    /**
     * Checks whether a new user request exists in request text file.
     *
     * @return true if a request is present, false if otherwise
     */
    boolean isNewUserRequest() {
        String[] accInfo = getAccInfo();
        Customer customer = systemManager.getCustomer(accInfo[0]);
        return customer == null;
    }

    /**
     * Checks whether the current request is a joint account request.
     * Precondition: request text file is not empty
     *
     * @return true if current request contains information of another user, false if otherwise
     */
    boolean isJointAccountRequest() {
        String[] accInfo = getAccInfo();
        Customer otherCustomer = systemManager.getCustomer(accInfo[1]);
        return otherCustomer != null;
    }

    /**
     * Processes a joint account request and removes it from request text file. The created account will be stored in
     * both the request sender and the receiving customer.
     *
     * @return the Customer receiving a joint account
     */
    Customer processJointAccountRequest() {
        try {
            String[] accInfo = getAccInfo();
            Customer otherCustomer = systemManager.getCustomer(accInfo[1]);
            Account account = processNewAccountRequest();
            otherCustomer.addAccount(account);
            account.setJoint();
            removeFirst();
            systemManager.save();
            return otherCustomer;
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Processes customer creation request and removes it from request text file.
     *
     * @param bm a BankManager user which creates the customer
     * @return the created Customer
     */
    Customer processNewUserRequest(BankManager bm) {
        try {
            String[] accInfo = getAccInfo();
            Customer customer = bm.createCustomer(accInfo[0], accInfo[3], accInfo[2]);
            systemManager.addCustomer(accInfo[0], customer);
            systemManager.save();
            removeFirst();
            return customer;
        } catch (IOException e) {
            return null;
        }

    }

    /**
     * Processes an account request and removes it from request text file.
     *
     * @return the created (subtype of) Account
     */
    Account processNewAccountRequest() {
        try {
            String[] accInfo = getAccInfo();
            Account account = createAccount(accInfo);
            Customer customer = systemManager.getCustomer(accInfo[0]);
            customer.addAccount(account);
            removeFirst();
            systemManager.save();
            return account;
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Reads the first line of request text file and returns a String array of information (separated by comma) in that
     * line.
     *
     * @return a String array of all information needed to create an account
     */
    String[] getAccInfo() {
        String[] accInfo = null;
        BufferedReader requestReader;
        try {
            requestReader = new BufferedReader(new FileReader(REQUEST_FILE)); //refreshes
            accInfo = requestReader.readLine().split(",");
            requestReader.close();
        } catch (NullPointerException ignored) {
        } catch (IOException e) {
            e.printStackTrace();
        }
        return accInfo;
    }

    /**
     * Writes all required information for an account request to request text file. Time deposit, stock, and joint
     * accounts have additional required information.
     *
     * @param requester requesting Customer
     * @param other receiving Customer (joint accounts only)
     * @param type type of account to be created
     * @param termLength contract length (in months) (time deposit accounts only)
     * @param ticker stock name (stock accounts only)
     * @param numStock number of stocks (stock accounts only)
     * @return true if information is written successfully, false if otherwise
     */
    boolean addAccountRequest(Customer requester, Customer other, String type, String termLength, String ticker, String numStock) {
        StringBuilder str = new StringBuilder();
        str.append(requester.getName());
        str.append(",");
        str.append(type);
        if (termLength != null) {
            str.append(",");
            str.append(termLength);
        }
        if (ticker != null) {
            str.append(",");
            str.append(ticker);
        }
        if (numStock != null) {
            str.append(",");
            str.append(numStock);
        }
        if (other != null) {
            str.append(",");
            str.append(other.getName());
        }
        str.append("\n");
        try {
            writeToRequest(str.toString());
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Returns an Account based on information in accInfo.
     *
     * @param accInfo information of format {username, account type, term length (TimeDeposit) or stock (StockAccount),
     *                balance}
     * @return a subtype of Account
     */
    private Account createAccount(String[] accInfo) {
        Account account = null;
        switch (accInfo[1]) {
            case Account.CREDIT:
                account = new CreditCard(10000);
                break;
            case Account.LOCREDIT:
                account = new LineOfCredit(10000);
                break;
            case Account.CHEQUING:
                account = new Chequing();
                break;
            case Account.SAVING:
                account = new Savings();
                break;
            case Account.TIMEDEPOSIT:
                account = new TimeDeposit(Integer.parseInt(accInfo[2]));
                break;
            case Account.STOCK:
                try {
                    account = new StockAccount(YahooFinance.get(accInfo[2]), Integer.parseInt(accInfo[3]));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
        return account;
    }


}
