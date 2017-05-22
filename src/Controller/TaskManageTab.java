package Controller;

import Dialog.Login;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.layout.Border;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.io.IOException;

/**
 * Created by SunnyD on 2017/5/3.
 */
public class TaskManageTab extends Tab {
    @FXML ListView<Process> newProcess;
    public TaskManageTab(){
        FXMLLoader loader=new FXMLLoader(getClass().getResource("/View/TaskManageTab.fxml"));
        loader.setRoot(this);

        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.init();
        System.out.println("test");
    }

    public void init(){
        

    }


}
