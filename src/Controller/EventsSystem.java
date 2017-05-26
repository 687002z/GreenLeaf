package Controller;

import Model.Login;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class EventsSystem implements Initializable{
    @FXML private BorderPane borderPane;
    @FXML private TaskManageTab taskManageTab;
    @FXML private TabPane tabPane;
    @FXML private Label username;
    @FXML private Label title;
    @FXML private Accordion accordion;
    public EventsSystem(){

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //当点击任务管理tab的时候
        Login login= Login.getInstance();
        while(login.logined==false){

            if(login.show(new Stage(),"logiessage","login")){
                this.updateInfo();
            }
            taskManageTab.initTaskList();
        }
    }

    @FXML
    public void onOpenMenuItem(){

    }

    public void updateInfo(){
        username.setText(Login.getInstance().getUsername());
        username.setFont(Font.font("Verdana", FontWeight.BOLD, 15));
        title.setText("职位："+Login.getInstance().getTitle());
        title.setFont(Font.font("Verdana", FontWeight.BOLD, 15));
    }

}
