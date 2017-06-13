package Dialog;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Created by SunnyD on 2017/5/10.
 */

public class Dialogs {
    private static Dialogs instance=new Dialogs();
    private LoginResponse lr;
    private String processName;

    public static Dialogs getInstance(){
        return instance;
    }

    public enum Response {
        NO, YES, CANCEL
    }
    public class LoginResponse {
        public String username;
        public String password;
    }


    private Response buttonSelected = Response.CANCEL;

    private ImageView icon = new ImageView();

    class Dialog extends Stage {
        public Dialog(String title, Stage owner, Scene scene) {
            setTitle(title);
            initStyle(StageStyle.UTILITY);
            initModality(Modality.APPLICATION_MODAL);
            initOwner(owner);
            setResizable(false);
            setScene(scene);
//            icon.setImage(new Image(getClass().getResourceAsStream("/com/sbt/common/images/newUI/gantan.png")));
        }

        public void showDialog() {
            sizeToScene();
            centerOnScreen();
            showAndWait();
        }
    }

    class Message extends Text {
        public Message(String msg) {
            super(msg);
            setWrappingWidth(250);//自动换行的宽度
        }
    }

    public Response showConfirmDialog(Stage owner, String message,String title) {
        VBox vb = new VBox();
        Scene scene = new Scene(vb);
        final Dialog dial = new Dialog(title, owner, scene);
        vb.setPadding(new Insets(10, 10, 10, 10));
        vb.setSpacing(10);
        Button yesButton = new Button("确定");
        yesButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                dial.close();
                buttonSelected = Response.YES;
            }
        });
        Button noButton = new Button("取消");
        noButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                dial.close();
                buttonSelected = Response.NO;
            }
        });
        BorderPane bp = new BorderPane();
        HBox buttons = new HBox();
        buttons.setAlignment(Pos.CENTER);
        buttons.setSpacing(10);
        buttons.getChildren().addAll(yesButton, noButton);
        bp.setCenter(buttons);
        HBox msg = new HBox();
        msg.setSpacing(5);
        msg.getChildren().addAll(icon,new Message(message));
        vb.getChildren().addAll(msg, bp);
        dial.showDialog();
        return buttonSelected;
    }
    public void showMessageDialog(Stage owner, String message,String title) {
        showMessageDialog(owner, new Message(message), title);
    }
    //用以识别html语句
    public void showWebViewDialog(Stage owner,String text_,String title){
        VBox vb = new VBox();
        Scene scene = new Scene(vb);
        final Dialog dial = new Dialog(title, owner, scene);
        vb.setPadding(new Insets(10, 10, 10, 10));
        vb.setSpacing(10);
        Button okButton = new Button("确定");
        okButton.setAlignment(Pos.CENTER);
        okButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                dial.close();
            }
        });
        BorderPane bp = new BorderPane();
        bp.setCenter(okButton);
        HBox msg = new HBox();
        msg.setSpacing(5);
        WebView webview = new WebView();
        WebEngine engine = webview.getEngine();
        engine.loadContent(text_);
        msg.getChildren().addAll(icon, webview);
        vb.getChildren().addAll(msg, bp);
        dial.setMinHeight(150);
        dial.setMaxHeight(150);
        dial.setHeight(150);
        dial.showDialog();

    }
    public void showMessageDialog(Stage owner, Node message, String title) {
        VBox vb = new VBox();
        Scene scene = new Scene(vb);
        final Dialog dial = new Dialog(title, owner, scene);
        vb.setPadding(new Insets(10, 10, 10, 10));
        vb.setSpacing(10);
        Button okButton = new Button("确定");
        okButton.setAlignment(Pos.CENTER);
        okButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                dial.close();
            }
        });
        BorderPane bp = new BorderPane();
        bp.setCenter(okButton);
        HBox msg = new HBox();
        msg.setSpacing(5);
        msg.getChildren().addAll(icon, message);
        vb.getChildren().addAll(msg, bp);
        dial.showDialog();
    }


    public LoginResponse showLoginDialog(Stage owner) {
        lr=null;
        VBox vb = new VBox();
        Scene scene = new Scene(vb);
        final Dialog dial = new Dialog("登陆", owner, scene);
        vb.setPadding(new Insets(10, 10, 10, 10));
        vb.setSpacing(10);

        HBox user = new HBox();
        user.setSpacing(5);
        Label userlabel =new Label("用户");
        TextField usertf= new TextField();
        user.getChildren().addAll(userlabel,usertf);

        HBox passwd = new HBox();
        passwd.setSpacing(5);
        Label passwdlabel =new Label("密码");
        PasswordField  passwdtf= new PasswordField();
        passwd.getChildren().addAll(passwdlabel,passwdtf);
        vb.getChildren().addAll(user,passwd);

        Button submit = new Button("提交");
        submit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                dial.close();
                Dialogs.this.lr=new LoginResponse();
                lr.username=usertf.getText();
                lr.password=passwdtf.getText();
            }
        });
        Button noButton = new Button("取消");
        noButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                dial.close();
            }
        });
        HBox buttons = new HBox();
        buttons.setAlignment(Pos.CENTER);
        buttons.setSpacing(10);
        buttons.getChildren().addAll(submit, noButton);

        BorderPane bp = new BorderPane();
        bp.setCenter(buttons);
        vb.getChildren().add(bp);
        dial.showDialog();
        return lr;
    }

    public String showNewProcessDialog(Stage owner){
        processName=null;

        VBox vb = new VBox();
        Scene scene = new Scene(vb);
        final Dialog dial = new Dialog("新建流程", owner, scene);
        vb.setPadding(new Insets(10, 10, 10, 10));
        vb.setSpacing(10);

        HBox name = new HBox();
        name.setSpacing(5);
        Label label1 =new Label("流程名：");
        TextField processNameTf= new TextField();
        name.getChildren().addAll(label1,processNameTf);
        vb.getChildren().add(name);

        Button submit = new Button("提交");
        submit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                dial.close();
                processName=processNameTf.getText();
            }
        });
        Button noButton = new Button("取消");
        noButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                dial.close();
            }
        });
        HBox buttons = new HBox();
        buttons.setAlignment(Pos.CENTER);
        buttons.setSpacing(10);
        buttons.getChildren().addAll(submit, noButton);

        BorderPane bp = new BorderPane();
        bp.setCenter(buttons);
        vb.getChildren().add(bp);
        dial.showDialog();
        return processName;

    }

}
