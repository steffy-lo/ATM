package main;

import accounts.Account;
import accounts.AssetAccount;
import accounts.StockAccount;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import users.Customer;
import users.ManagerAssist;
import yahoofinance.YahooFinance;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class CustomerViewController implements Initializable {

    private Customer customer;
    private SystemManager sm;

    private final ObservableList<Integer> terms = FXCollections.observableArrayList(1, 3, 6, 12, 24);

    @FXML
    private Label chq_overview, sav_overview, cre_overview, sto_overview, joint_overview, credit_score,
            credit_score_label, net_total, security_ques;

    @FXML
    private Pane pn_chq, pn_sav, pn_sav1, pn_sav2, pn_cre, pn_cre1, pn_cre2, pn_joi, pn_joi1, pn_joi2,
            pn_sto, pn_sto1, pn_sto2, pn_default, sav_cre, sto_cre, cre_cre, chq_cre, ques_answered, pn_changepw,
            Acc_canvas, pn_transaction, pn_other_user, pn_other_acc;

    @FXML
    private ChoiceBox<Account> chq_accounts, sav_accounts, cre_accounts, joint_accounts, sto_accounts, user_accounts,
    other_accounts;

    @FXML
    private ChoiceBox<Integer> term_length;

    @FXML
    private TextField chq_joint, sav_joint, cre_joint, sto_joint, ticker, num_stocks, securiry_ans, firstTime,
            secondTime, amount_accounts;

    @FXML
    private Button logout_btn;


    @Override
    public void initialize(URL url, ResourceBundle RB) {
    }

    @FXML
    void toDefaultMenu(){
        Acc_canvas.setVisible(true);
        Acc_canvas.toFront();
        if (customer.getNetTotal() < 0) net_total.setTextFill(Color.RED);
        else net_total.setTextFill(Color.GREEN);
        net_total.setText("" + Math.round(customer.getNetTotal()));
        pn_default.setVisible(true);
        pn_default.toFront();
    }

    /**
     * Saves the current SystemData
     *
     * @throws IOException when SystemData fails to save or GUIView.fxml fails to load
     */
    @FXML
    void logout() throws IOException {
        sm.save();
        Stage stage = (Stage) logout_btn.getScene().getWindow();
        Scene scene = new Scene(FXMLLoader.load(getClass().getResource("GUIView.fxml")));
        stage.setScene(scene);
        stage.show();
    }
    /**
     * Displays information of selected chequing account of current customer.
     */
    @FXML
    void showChequingAcc() {
        Acc_canvas.setVisible(true);
        Acc_canvas.toFront();
        chq_overview.setText("");
        ArrayList<Account> accounts = customer.getAccountsByType(Account.CHEQUING);
        chq_accounts.setItems(FXCollections.observableArrayList(accounts));
        setUpListener(chq_overview, chq_accounts, accounts);
        pn_chq.setVisible(true);
        pn_chq.toFront();
    }

    private void deposit(ChoiceBox<Account> account, Label overview, Pane crePane) {
        if (account.getValue() != null) {
            account.getValue().deposit(sm.getATM(), new Transaction("ATM"));
            overview.setText(account.getValue().overview()); // refreshes
            crePane.setVisible(false);
            new Alert(Alert.AlertType.INFORMATION, "Deposit Successful.", ButtonType.OK).showAndWait();
        } else new Alert(Alert.AlertType.INFORMATION, "Please select an account.", ButtonType.OK).showAndWait();
    }

    @FXML void depositChq() {
        deposit(chq_accounts, chq_overview, chq_cre);
    }

    @FXML void depositSav() {
        deposit(sav_accounts, sav_overview, sav_cre);
    }

    @FXML
    void transferSelf() throws IOException{
        double num = 0;
        try {
            num = Integer.parseInt(amount_accounts.getText());
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.INFORMATION,
                    "Please enter a double integer for the amount you want to transfer.",
                    ButtonType.OK).showAndWait();
        }
        ((AssetAccount)user_accounts.getValue()).transferOut(other_accounts.getValue(), num);
        new Alert(Alert.AlertType.INFORMATION, "Transfer successful!", ButtonType.OK).showAndWait();
        sm.save();
    }

    /**
     * Displays transaction menu.
     */
    @FXML
    void toMakeTransaction(){
        pn_transaction.toFront();
        pn_other_acc.setVisible(false);
        pn_other_user.setVisible(false);
    }

    /**
     * Button event for chequing account creation menu.
     * Sends a chequing account creation request.
     */
    @FXML
    void requestChq() throws IOException {
        stdAccRequest(Account.CHEQUING, chq_joint);
    }

    /**
     * Displays information of selected credit card account of current customer.
     */
    @FXML
    void showCreditAcc() {
        Acc_canvas.setVisible(true);
        Acc_canvas.toFront();
        ArrayList<Account> creditAccounts = customer.getAccountsByType(new String[]{Account.CREDIT, Account.LOCREDIT});
        if (creditAccounts.size() != 0) {
            pn_cre2.setVisible(true);
            pn_cre2.toFront();
            cre_overview.setText("");
            credit_score_label.setVisible(true);
            credit_score.setText("" + customer.getCreditScore());
            cre_accounts.setItems(FXCollections.observableArrayList(creditAccounts));
            setUpListener(cre_overview, cre_accounts, creditAccounts);
        } else {
            pn_cre1.setVisible(true);
            pn_cre1.toFront();
        }
        pn_cre.setVisible(true);
        pn_cre.toFront();
    }

    /**
     * Displays savings account creation menu.
     */
    @FXML
    void showSavingCre(){
        if (sav_cre.isVisible()) {
            sav_cre.setVisible(false);
        }
        else {
            Acc_canvas.toFront();
            sav_cre.toFront();
            sav_cre.setVisible(true);
        }
    }

    /**
     * Displays stock account creation menu.
     */
    @FXML
    void showStockCre(){

        if (sto_cre.isVisible()) {
            sto_cre.setVisible(false);
        }
        else {
            Acc_canvas.toFront();
            sto_cre.toFront();
            sto_cre.setVisible(true);
        }
    }

    /**
     * Displays credit account creation menu.
     */
    @FXML
    void showCreditCre(){
        if (cre_cre.isVisible()){
            cre_cre.setVisible(false);
        }
        else{
            Acc_canvas.toFront();
            cre_cre.toFront();
            cre_cre.setVisible(true);
        }
    }

    /**
     * Displays chequing account creation menu.
     */
    @FXML
    void showChequingCre(){
        if (chq_cre.isVisible()){
            chq_cre.setVisible(false);
        }
        else {
            chq_cre.setVisible(true);
        }
    }

    /**
     * Button event for credit account creation menu.
     * Sends a credit account creation request.
     */
    @FXML
    void requestCredit() throws IOException {
        stdAccRequest(Account.CREDIT, cre_joint);
    }

    /**
     * Button event for line of credit account creation menu.
     * Sends a line of credit  account creation request.
     *
     * @throws IOException
     */
    @FXML
    void requestLineCredit() throws IOException {
        stdAccRequest(Account.LOCREDIT, cre_joint);
    }

    /**
     * Displays information of selected joint account of current customer.
     */
    @FXML
    void showJointAcc() {
        Acc_canvas.setVisible(true);
        Acc_canvas.toFront();
        ArrayList<Account> jointAccounts = customer.getJointAccounts();
        if (jointAccounts.size() != 0) {
            pn_joi2.setVisible(true);
            pn_joi2.toFront();
            joint_overview.setText("");
            joint_accounts.setItems(FXCollections.observableArrayList(jointAccounts));
            setUpListener(joint_overview, joint_accounts, jointAccounts);
        } else {
            pn_joi1.setVisible(true);
            pn_joi1.toFront();
        }
        pn_joi.setVisible(true);
        pn_joi.toFront();
    }

    /**
     * Displays information of selected savings account of current customer.
     */
    @FXML
    void showSavingAcc() {
        Acc_canvas.setVisible(true);
        Acc_canvas.toFront();
        term_length.setItems(terms);
        ArrayList<Account> savAccounts = customer.getAccountsByType(new String[]{Account.SAVING, Account.TIMEDEPOSIT});
        if (savAccounts.size() != 0) {
            pn_sav2.setVisible(true);
            pn_sav2.toFront();
            sav_overview.setText("");
            sav_accounts.setItems(FXCollections.observableArrayList(savAccounts));
            setUpListener(sav_overview, sav_accounts, savAccounts);
        } else {
            pn_sav1.setVisible(true);
            pn_sav1.toFront();
        }
        pn_sav.setVisible(true);
        pn_sav.toFront();
    }

    /**
     * Button event for savings account creation menu.
     * Sends a savings account creation request.
     */
    @FXML
    void requestSav() throws IOException {
        stdAccRequest(Account.SAVING, sav_joint);
    }

    /**
     * Displays information of selected stock account of current customer.
     */
    @FXML
    void showStockAcc() {
        Acc_canvas.setVisible(true);
        Acc_canvas.toFront();
        ArrayList<Account> stockAccounts = customer.getAccountsByType(Account.STOCK);
        if (stockAccounts.size() != 0) {
            pn_sto2.setVisible(true);
            pn_sto2.toFront();
            sto_overview.setText("");
            sto_accounts.setItems(FXCollections.observableArrayList(stockAccounts));
            setUpListener(sto_overview, sto_accounts, stockAccounts);
        } else {
            pn_sto1.setVisible(true);
            pn_sto1.toFront();
        }
        pn_sto.setVisible(true);
        pn_sto.toFront();
    }

    /**
     * Button event for stock account creation menu.
     * Sends a stock account creation request. Alerts the user whenever the user enters a negative number of stocks,
     * the user already possesses a stock account of requested stock, and the stock corresponding the given ticker
     * symbol does not exist.
     *
     * @throws IOException
     */
    @FXML
    void requestStock() throws IOException {
        int num;
        try {
            num = Integer.parseInt(num_stocks.getText());
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.INFORMATION, "Please enter a positive integer for the number of stocks you want to buy.", ButtonType.OK).showAndWait();
            return;
        }
        try {
            YahooFinance.get(ticker.getText()).getQuote().getPrice();
        } catch (Exception e) {
            new Alert(Alert.AlertType.INFORMATION, "The stock associated with the ticker symbol you've entered doesn't exist.", ButtonType.OK).showAndWait();
            return;
        }
        for (Account acc: customer.getAccountsByType("6")) {
            if (((StockAccount)acc).getSerializableStock().getStock().getSymbol().equalsIgnoreCase(ticker.getText())) {
                new Alert(Alert.AlertType.INFORMATION, "You already have a stock account with this stock.", ButtonType.OK).showAndWait();
                return;
            }
        }
        if (num > 0) {
            stdAccRequest(Account.STOCK, sto_joint);
        } else {
            new Alert(Alert.AlertType.INFORMATION, "Please make sure a positive integer is entered for the number of stocks you want to buy.", ButtonType.OK).showAndWait();
        }

    }

    @FXML
    void viewPortfolio() {
        double netValue = 0;
        double returns = 0;
        for (Account acc: customer.getAccountsByType("6")) {
            StockAccount stockAccount = ((StockAccount) acc);
            System.out.println(stockAccount.getSerializableStock().getStock().getSymbol());
            System.out.println(stockAccount.value() + " (" + stockAccount.percentageReturn() + ")");
            netValue += stockAccount.value();
            returns += stockAccount.value() - stockAccount.getBoughtPrice() * stockAccount.getBalance();
        }
        System.out.println("Net Value: " + netValue);
        System.out.println("Total Returns: " + returns + " (" + returns/netValue + ")");
    }

    /**
     * Displays panel for other user's account.
     */
    @FXML
    void toOtherAccount(){
        pn_other_acc.setVisible(true);
        pn_other_user.setVisible(false);
        user_accounts.setItems(FXCollections.observableArrayList(customer.getAccounts()));
        other_accounts.setItems(FXCollections.observableArrayList(customer.getAccounts()));
    }

    /**
     * Displays panel for other user.
     */
    @FXML
    void toOtherUser(){
        pn_other_user.setVisible(true);
        pn_other_acc.setVisible(false);
    }

    /**
     * Resets password of the current customer by the entered text fields. Double checks if the requester knows the
     * current password. Alerts the requester whenever the password is reset successfully and the double check process
     * fails.
     */
    @FXML
    void resetPassword(){
        String firstTimePassword = firstTime.getText();
        String passwordDoubleCheck = secondTime.getText();
        if (firstTimePassword.equals(passwordDoubleCheck)){
            customer.setPassword(firstTimePassword);
            sm.save();
            new Alert(Alert.AlertType.INFORMATION, "Your password has been reset successfully", ButtonType.OK).showAndWait();
            Acc_canvas.toFront();
            pn_default.toFront();
        }else {
            new Alert(Alert.AlertType.ERROR, "The passwords you entered don't match.", ButtonType.OK).showAndWait();
        }
    }

    /**
     * Checks if the entered text filed contains the correct answer to current customer's security question. Displays
     * a panel upon success, and alerts the user if the answer is incorrect.
     */
    @FXML
    void secureCheck(){
        String answer = securiry_ans.getText();
        if (customer.verifySecurityAnswer(answer)){
            ques_answered.setVisible(true);
        }else {
            new Alert(Alert.AlertType.ERROR, "Incorrect answer to your security question.", ButtonType.OK).showAndWait();
        }
    }

    /**
     * Displays menu for changing password and security process.
     */
    @FXML
    void toChangePassword(){
        pn_changepw.toFront();
        ques_answered.setVisible(false);
        security_ques.setText(customer.getSecurityQuestion());
    }

    /**
     * Sets current customer to the passed customer and refreshes SystemManager.
     * @param customer customer to replace
     * @param sm updated SystemManager
     */
    void passVariables(Customer customer, SystemManager sm) {
        this.customer = customer;
        this.sm = sm;
    }

    /**
     *
     */
    @FXML
    void emergencyRestock(){
        if (customer instanceof ManagerAssist){
            ((ManagerAssist) customer).emergentRestock(sm.getATM());
            new Alert(Alert.AlertType.INFORMATION, "Restocked.", ButtonType.OK).showAndWait();
        }
        else{
            new Alert(Alert.AlertType.ERROR, "Access denied.", ButtonType.OK).showAndWait();
        }
    }
    //============================= Helper Methods =====================================================================

    /**
     * Adds listeners to choiceBox (containing accounts) that changes overview's displayed account info to corresponding
     * selected account in choiceBox.
     *
     * @param overview Label displaying account info/overview
     * @param choiceBox ComboBox containing list of accounts
     * @param accounts ArrayList of accounts in choiceBox
     */
    private void setUpListener(Label overview, ChoiceBox<Account> choiceBox, ArrayList<Account> accounts) {
        choiceBox.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue ov, Number value, Number new_value)
            {

                if(new_value.intValue() != -1)
                    overview.setText(accounts.get(new_value.intValue()).overview());
            }
        });
    }

    private void withdraw(ChoiceBox<Account> choiceBox, Label info, TextField amt) throws IOException {
        if (choiceBox.getValue() != null && !(amt.getText().equals(""))) {
            double amount;
            try {
                amount = Double.parseDouble(amt.getText());
            } catch (NumberFormatException e) {
                info.setText("Please enter a numerical amount.");
                return;
            }
            Account account = choiceBox.getValue();
            boolean success = choiceBox.getValue().withdraw(amount, sm.getATM());
            if (success) choiceBox.getValue().addTransaction(new Transaction(amount,
                    Transaction.TransactionType.WITHDRAW, "ATM"));
            else info.setText("Withdraw is not successful. Insufficient funds.");
        } else info.setText("Please select an account to withdraw from.");
    }



    /**
     * Processes a account request of a user based on accType and user. Alerts the user whenever a request is
     * successfully sent, the receiver does not exist, and user attempts to create a joint account with itself.
     *
     * @param accType type of account requested
     */
    private void stdAccRequest(String accType, TextField joint) throws IOException {
        RequestManager rm = sm.getRequestManager();
        String otherCustomerName = joint.getText();
        String termLength = null;
        String ticker = null;
        String numStock = null;

        if (accType.equals(Account.TIMEDEPOSIT)){
            termLength = String.valueOf(term_length.getValue());
        }
        else if (accType.equals(Account.STOCK)){
            ticker = this.ticker.getText();
            numStock = num_stocks.getText();
        }

        if (otherCustomerName.equals("")) {
            if(rm.addAccountRequest(customer, null, accType, termLength, ticker, numStock)){
                new Alert(Alert.AlertType.INFORMATION, "Request sent.").showAndWait();
            }
        }
        else if (customer.getName().equals(otherCustomerName)) {
            new Alert(Alert.AlertType.INFORMATION, "You cannot create a joint account with yourself!",
                    ButtonType.OK).showAndWait();
        }
        else{
            Customer otherCustomer = sm.getCustomer(otherCustomerName);
            if (otherCustomer == null){
                new Alert(Alert.AlertType.INFORMATION, "The user login you've provided doesn't exist." +
                        " Please try again.", ButtonType.OK).showAndWait();
            }
            else{
                if(rm.addAccountRequest(customer, otherCustomer, accType, termLength, ticker, numStock)) {
                    new Alert(Alert.AlertType.INFORMATION, "Request sent.").showAndWait();
                }
            }
        }


    }
}
