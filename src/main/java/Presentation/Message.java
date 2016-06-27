package Presentation;

import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

/**
 * Created by Андрей on 21.06.2016.
 */
public class Message extends Application  {
    public void start(Stage primaryStage) {
        System.out.println("Message init");
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information Dialog");
        alert.setHeaderText(null);
        alert.setContentText("I have a great message for you!");

        alert.showAndWait();
    }
}
