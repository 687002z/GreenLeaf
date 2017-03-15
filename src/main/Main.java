package main;

import com.sun.org.apache.bcel.internal.generic.FMUL;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/View/EventsSystem.fxml"));
        primaryStage.setTitle("GreenLeaf");
        Scene mainScene=new Scene(root, 800, 600);
        primaryStage.setScene(mainScene);
        mainScene.getStylesheets().add(getClass().getResource("/View/mainScene.css").toExternalForm());
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
