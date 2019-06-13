package main;

import accounts.*;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;
import users.BankManager;
import users.Customer;

import java.io.*;
import java.lang.reflect.Array;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.ResourceBundle;

public class ManagerViewController implements Initializable {

    private BankManager bankManager;
    private Calendar calendar;
    private SystemManager sm;

    @Override
    public void initialize(URL url, ResourceBundle RB) {
        displayTime(manager_time);
    }

    void passVariables(BankManager bankManager, SystemManager sm) {
        this.bankManager = bankManager;
        this.sm = sm;
        calendar = sm.getCalendar();
    }
    /**
     * Creates and plays a TimeLine with either a custom date or current computer time. The given Label is updated
     * whenever time changes.
     *
     * @param time Label containing a set time
     */
    private void displayTime(Label time) {
        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            calendar.add(Calendar.SECOND, 1);
            time.setText(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(calendar.getTime()));

        }),
                new KeyFrame(Duration.seconds(1))
        );
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
    }

    @FXML private Label manager_time, requests_label, decision_label, joint_label, pass_label, undo_label, date_label;

    @FXML private ComboBox<Integer> hour_picker, minute_picker;

    @FXML private ChoiceBox<String> user_accounts;

    @FXML private ChoiceBox<Integer> num_transactions;

    @FXML private DatePicker date_picker;

    @FXML private TextField bill5, bill10, bill20, bill50, user_undo;

    @FXML private Pane customerRequest, billRestock, undoTransaction, setDateTime;

    @FXML private Button accept_btn, reject_btn, next_btn, undo_btn, logout_btn;


    /**
     * If action is true, show all labels for request menu. If false, hide them instead.
     *
     * @param action determines whether to hide or show request menu labels
     */
    private void requestDecision(boolean action) {
        pass_label.setText("");
        decision_label.setText("");
        joint_label.setText("");
        accept_btn.setVisible(action);
        reject_btn.setVisible(action);
        next_btn.setVisible(action);
        decision_label.setVisible(action);
        joint_label.setVisible(action);
        pass_label.setVisible(action);
    }

    /**
     * Button event for logout button.
     * Saves SystemData, refreshes and displays the main menu.
     *
     * @throws IOException when SystemData fails to save, or GUIView.fxml fails to load
     */
    @FXML void logout() throws IOException {
        sm.save();
        Stage stage = (Stage) logout_btn.getScene().getWindow();
        Scene scene = new Scene(FXMLLoader.load(getClass().getResource("GUIView.fxml")));
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Displays available requests.
     */
    @FXML void showRequests(){
        RequestManager rm = sm.getRequestManager();
        String [] accInfo = rm.getAccInfo();
        if (accInfo == null){
            requests_label.setText("There are no pending requests.");
            requestDecision(false);
        }
        else {
            requests_label.setText("There is a request for a " + rm.getAccType(accInfo[1]) + " creation from " + accInfo[0] + "...");
            requestDecision(true);
        }
        customerRequest.setVisible(true);
        customerRequest.toFront();
    }

    /**
     * Displays menu for undoing transactions.
     */
    @FXML void showUndoTransactions() {
        undoTransaction.setVisible(true);
        undoTransaction.toFront();
    }

    /**
     * Displays date selection menu, with two choiceBoxes for hour and minute.
     */
    @FXML void manageDate() {
        ArrayList<Integer> hour = new ArrayList<>();
        ArrayList<Integer> minute = new ArrayList<>();
        for (int i = 0; i < 59; i++) {
            if (i < 24) {
                hour.add(i);
            }
            minute.add(i);
        }
        hour_picker.setItems(FXCollections.observableArrayList(hour));
        minute_picker.setItems(FXCollections.observableArrayList(minute));
        setDateTime.setVisible(true);
        setDateTime.toFront();
    }

    /**
     * Displays machine restock menu.
     */
    @FXML void showMachineRestock() {
        billRestock.setVisible(true);
        billRestock.toFront();
    }
    /**
     * Button event for machine restock menu.
     * Restock bills equal to entered integer number for each text field (corresponding to bill types). Alerts the user
     * whenever bills are restocked and a none-integer is entered.
     */
    @FXML void restockBills() {
        String[] restock = {bill5.getText(), bill10.getText(), bill20.getText(), bill50.getText()};
        int[] intAmount = new int[4];
        for (int i = 0; i < 4; i++) {
            if (!(restock[i].equals(""))) {
                try {
                    intAmount[i] = Integer.parseInt(restock[i]);
                } catch (NumberFormatException e) {
                    new Alert(Alert.AlertType.ERROR, "Please enter an integer.",
                            ButtonType.OK).showAndWait();
                }
            }
        }
        for (int i = 0; i < 4; i++) {
            sm.getATM().restock(i, intAmount[i]);
        }
        new Alert(Alert.AlertType.INFORMATION, "Successfully restocked bills.",
                ButtonType.OK).showAndWait();
    }

    /**
     * Button event for account creation menu (accept button).
     * Creates an account for the displayed account request.
     */
    @FXML void createAccount() {
        boolean isSuccessful = false;
        RequestManager rm = sm.getRequestManager();
        if (rm.isNewUserRequest()) {
            Customer customer = rm.processNewUserRequest(bankManager);
            if (customer != null) {
                pass_label.setText("A password for " + customer.getName() +
                        " has been randomly generated: " + customer.getPassword());
                System.out.println(customer.getPassword());
                isSuccessful = true;
            }
        }
        else if (rm.isJointAccountRequest()){
            Customer otherCustomer = rm.processJointAccountRequest();
            if (otherCustomer != null) {
                joint_label.setText("Joint account creation with " + otherCustomer.getName() + " is successful.");
                isSuccessful = true;
            }
        }
        else{
            Account account = rm.processNewAccountRequest();
            if (account != null) {
                decision_label.setText("A " + rm.getAccType(account) + " has been successfully created.");
                isSuccessful = true;
            }
        }

        if (isSuccessful) {
            accept_btn.setVisible(false);
            reject_btn.setVisible(false);
        }
        else{
            new Alert(Alert.AlertType.ERROR, "Failed to create new account.", ButtonType.OK).showAndWait();
        }
    }

    /**
     * Button event for account creation menu (reject button).
     * Rejects and removes the displayed account request.
     */
    @FXML void rejectAccount() {
        decision_label.setText("Rejected account creation.");
        try {
            sm.getRequestManager().removeFirst();
        }catch (IOException ignore){}
        reject_btn.setVisible(false);
        accept_btn.setVisible(false);
    }

    /**
     * Button event for date selection menu.
     * Sets date in the program to selected hour and minute.
     */
    @FXML void setDate() {
        Calendar previous = calendar;
        calendar = bankManager.setDate(date_picker.getValue(), new int[]{hour_picker.getValue(), minute_picker.getValue()});
        sm.setNewDateTime(calendar.getTime());
        sm.updateFeatures(previous, calendar);
        date_label.setText("Date and time have been successfully set as specified.");
    }

    /**
     * Button event for date selection menu.
     * Sets date in the program to previous date.
     */
    @FXML void resetDate() {
        calendar = Calendar.getInstance();
        sm.setNewDateTime(calendar.getTime());
        date_label.setText("The date has been reset.");
    }

    /**
     * Key event for enter key in undo transaction menu.
     * Searches and displays all accounts of an entered user. Notifies the searcher when the given username is not a user.
     *
     * @param event pressed key
     */
    @FXML void search(KeyEvent event) {
        if (event.getCode().equals(KeyCode.ENTER)) {
            ArrayList<Account> accounts = null;
            try {
                accounts = sm.getCustomer(user_undo.getText()).getAccounts();
            } catch (NullPointerException e) {
                undo_label.setText("The user doesn't exist.");
            }
            if (accounts != null) {
                user_undo.setDisable(true);
                undo_label.setText("Which of the following accounts would the customer like to undo? Select a number to undo the nth transaction.");
                ArrayList<String> acc = new ArrayList<>();
                for (int i = 0; i < accounts.size(); i++) {
                    acc.add(i + ": Account No." + accounts.get(i).getAccountNum());
                }
                user_accounts.setItems(FXCollections.observableArrayList(acc));
                user_accounts.setVisible(true);
                undo_btn.setVisible(true);
                num_transactions.setVisible(true);
                setUpListener(num_transactions, user_accounts, accounts);
            }
        }
    }

    /**
     * Removes a transaction between current user and provided username, depending on the selected choiceBox. Alerts
     * the requester whenever the removal is successful and a missing field is required.
     */
    @FXML void undoTransactions() {
        String acc = user_accounts.getValue();
        String username = user_undo.getText();
        int num = num_transactions.getValue();
        if (acc != null && num != -1) {
            ArrayList<Account> accounts = sm.getCustomer(username).getAccounts();
            bankManager.undoTransaction(accounts.get(Character.getNumericValue(acc.charAt(0))), num);
            new Alert(Alert.AlertType.INFORMATION, "The " + num + "th transaction for " + username + " was undone.", ButtonType.OK).showAndWait();
        } else new Alert(Alert.AlertType.ERROR, "Please fill in all the fields.", ButtonType.OK).showAndWait();
    }

    /**
     * Changes num_transactions choiceBox based on selected account.
     *
     * @param num_transactions choiceBox to select which number of transaction to remove
     * @param accountSelect choiceBox to select an account
     * @param accounts list of corresponding accounts
     */
    private void setUpListener(ChoiceBox<Integer> num_transactions, ChoiceBox<String> accountSelect, ArrayList<Account> accounts) {
        accountSelect.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue ov, Number value, Number new_value)
            {
                if(new_value.intValue() != -1 && accountSelect.getValue() != null) {
                    Account account = accounts.get(Character.getNumericValue((accountSelect.getValue()).charAt(0)));
                    ArrayList<Integer> n = new ArrayList<>();
                    for (int i=1; i <= account.getTransactions().size(); i++) {
                        n.add(i);
                    }
                    num_transactions.setItems(FXCollections.observableArrayList(n));
                }
            }
        });
    }

}
