package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/** Main class for the bank application */
public class GUIMain extends Application{
    @Override
    public void start(Stage primaryStage) throws Exception{
        try{
            AnchorPane root = (AnchorPane)FXMLLoader.load(GUIMain.class.getResource("GUIView.fxml"));
            Scene scene = new Scene(root, 850,500);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Bank of Awesomeness");
            primaryStage.show();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public  static void main(String[] args){
        launch(args);
    }
}
