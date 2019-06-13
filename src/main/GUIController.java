package main;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;

import java.io.IOException;
import java.net.URL;
import java.util.*;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import users.BankManager;
import users.Customer;

public class GUIController implements Initializable {

    private final ObservableList<String> security_questions = FXCollections.observableArrayList(
            "What is the colour of your cat?",
            "What is the eye colour of your cat?",
            "What is the name of your cat?",
            "Why you don't have a cat?"
    );

    private SystemManager sm;

    @FXML
    private Pane user_login, main_menu, manager_login, user_signup, admin_login, admin_menu,
            admin_pages, pn_accCreation;

    @FXML
    private TextField user_username, user_password, manager_username, manager_password,
            user_reg_username, user_reg_answer, admin_code, new_manager_id, new_manager_pw, answer;

    @FXML
    private ChoiceBox<String> user_reg_question;

    @FXML
    private Label manager_cre_text;

    @FXML
    private Button user_login_btn, manager_login_btn;

    /**
     * Button event for customer and bank manager login menus.
     * Verifies user login request using text fields corresponding to username and password.
     * If the verifying process succeeds, sets stage and scene with information of logged in user. If the process fails,
     * alerts the user and displays the reason (wrong password or user doesn't exist).
     *
     * @param event ActionEvent to determine which button is pushed
     * @throws Exception
     */
    @FXML
    private void handleLoginAction (ActionEvent event) throws Exception {
        String verify;

        if (event.getSource() == user_login_btn) {
            String username = user_username.getText();
            String password = user_password.getText();
            verify = verify("customer", username, password);
            if(verify.equals("success")) {
                Customer cm = sm.getCustomer(username);
                FXMLLoader loader = new FXMLLoader(getClass().getResource("CustomerView.fxml"));
                Parent root = (Parent) loader.load();
                CustomerViewController customerViewController = loader.getController();
                customerViewController.passVariables(cm, sm);
                sm.save();
                Stage stage = (Stage) user_login_btn.getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
                return;
            }
        }
        else {
            String username = manager_username.getText();
            String password = manager_password.getText();
            verify = verify("bank manager", username, password);
            if (verify.equals("success")) {
                BankManager bm = sm.getManager(username);
                FXMLLoader loader = new FXMLLoader(getClass().getResource("ManagerView.fxml"));
                Parent root = (Parent) loader.load();
                ManagerViewController managerViewController = loader.getController();
                managerViewController.passVariables(bm, sm);
                sm.save();
                Stage stage = (Stage) manager_login_btn.getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
                return;
            }
        }
        if (verify.equals("wrong"))
            new Alert(Alert.AlertType.ERROR, "Wrong password. Please try again.", ButtonType.OK).showAndWait();
        else
            new Alert(Alert.AlertType.ERROR, "User does not exist.", ButtonType.OK).showAndWait();
    }

    @Override
    public void initialize(URL url, ResourceBundle RB){
        user_reg_question.setItems(security_questions);
        sm = new SystemManager();
        toMainMenu();
    }

    @FXML
    void undoTransactions(){
        toMainMenu();
        //TODO: To be implemented
    }

    /**
     * Displays user login menu.
     */
    @FXML
    void toUserLogin(){
        user_login.setVisible(true);
        user_login.toFront();
    }

    /**
     * Displays main menu.
     */
    @FXML
    void toMainMenu(){
        main_menu.setVisible(true);
        main_menu.toFront();
    }

    /**
     * Displays manager login menu.
     */
    @FXML
    void toManagerLogin() {
        manager_login.setVisible(true);
        manager_login.toFront();
    }

    /**
     * Displays user sign up menu.
     */
    @FXML
    void toUserSignUp(){
        user_signup.setVisible(true);
        user_signup.toFront();
    }

    /**
     * Displays administrator login menu.
     */
    @FXML
    void toAdminLogin(){
        admin_pages.setVisible(true);
        admin_pages.toFront();
        admin_login.setVisible(true);
        admin_login.toFront();
    }

    /**
     * Displays administrator menu (for bank manager creation).
     */
    @FXML
    void toAdminMenu(){
        admin_pages.setVisible(true);
        admin_pages.toFront();
        admin_menu.setVisible(true);
        admin_menu.toFront();
    }

    /**
     * Button event for user sign up menu.
     * Reads all required text fields and sends a new user request. Alerts the user when a request is sent, whenever a
     * text field is missing, and when the entered username already exists.
     */
    @FXML
    void userSignUp() throws IOException {
        String username = user_reg_username.getText();
        String securityAnswer = user_reg_answer.getText();
        String securityQuestion = user_reg_question.getValue();

        if (sm.getCustomer(username) == null) {
            if (username.equals("") || securityAnswer.equals("") || securityQuestion == null) {
                new Alert(Alert.AlertType.INFORMATION, "Please fill in all the fields.", ButtonType.OK).showAndWait();
            } else {
                if(sm.getRequestManager().newUserRequest(username, securityQuestion, securityAnswer)){
                    new Alert(Alert.AlertType.INFORMATION, "A request to make a user account was sent.", ButtonType.OK).showAndWait();
                }
                else {
                    new Alert(Alert.AlertType.ERROR, "Request failed.", ButtonType.OK);
                }
                toUserLogin();
            }
        } else {
            new Alert(Alert.AlertType.ERROR, "Sorry, that username already exist.", ButtonType.OK).show();
        }
    }

    /**
     * Automatic event (when a key is typed) for administrator login menu.
     * Verifies if the typed administrator code is correct, and if so, displays the administrator menu.
     *
     * @param event typed Event
     */
    @FXML
    void verifyAdminCode(Event event){
        String ch = ((KeyEvent)event).getCharacter();
        if (sm.verifyAdminCode(admin_code.getText() + ch)){
            toAdminMenu();
        }
    }

    /**
     * Button event for administrator menu.
     * Clears current administrator code text field and displays the main menu.
     */
    @FXML
    void adminLogout(){
        admin_code.clear();
        toMainMenu();
    }

    /**
     * Displays bank manager creation menu.
     */
    @FXML
    void showAccCreation(){
        pn_accCreation.setVisible(true);
        //pn_accCreation.toFront();
    }

    /**
     * Button event for bank manager creation menu.
     * Reads all required text fields and creates a new bank manager. Alerts the administrator when the bank manager is
     * created, whenever a text field is missing, and when the entered username already exists.
     */
    @FXML
    void createManagerAccount(){
        String username = new_manager_id.getText();
        String password = new_manager_pw.getText();

        if (sm.getManager(username) == null) {
            if (username.equals("") || password.equals("")) {
                new Alert(Alert.AlertType.INFORMATION, "Please fill in all the fields.", ButtonType.OK).showAndWait();
            } else {
                sm.addManager(username, new BankManager(username, password));
                sm.save();
                new Alert(Alert.AlertType.INFORMATION, "A new bank manager account was successfully created.", ButtonType.OK).showAndWait();
                pn_accCreation.setVisible(false);
                //toAdminMenu();
                manager_cre_text.setText("Would you like to create another bank manager account?");
            }
        } else {
            new Alert(Alert.AlertType.ERROR, "Sorry, that username already exist.", ButtonType.OK).show();
        }
    }

    /**
     * Verifies information, name and password, with given user type. Returns results of verification.
     *
     * @param type specifies whether the verified user is a customer or bank manager
     * @param name entered login username
     * @param password entered login password
     * @return "wrong" if login failed, "success" if login is successful, "null" if the username does not exist
     */
    private String verify(String type, String name, String password) {
        if (type.equals("bank manager")){
            BankManager user = sm.getManager(name);
            if (user == null){
                return "null";
            }
            else if (user.verifyPassword(password)){
                return "success";
            }
            else{
                return "wrong";
            }
        }
        else if (type.equals("customer")){
            Customer user = sm.getCustomer(name);
            if (user == null){
                return "null";
            }else if (user.outOfAttempts()){
                TextInputDialog dialog = new TextInputDialog();
                dialog.setTitle("Security Question");
                dialog.setHeaderText("You failed too many times!");
                dialog.setContentText(user.getSecurityQuestion());
                Optional<String> answer = dialog.showAndWait();
                if (answer.isPresent()){
                    if (user.verifySecurityAnswer(answer.get())){
                        user.clearLoginAttempts();
                    }
                    else{
                        new Alert(Alert.AlertType.INFORMATION, "Wrong answer! Please try again",
                                ButtonType.OK);
                    }
                }
                else{
                    return "wrong";
                }

            }
            if (user.verifyPassword(password)){
                user.loginAttempted();
                return "success";
            }
            else{
                user.loginAttempted();
                return "wrong";
            }
        }
        else {
            return "null";
        }
    }
}
