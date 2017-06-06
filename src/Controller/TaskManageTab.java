package Controller;

import Controller.Db.ConnDB;
import Model.Login;
import Model.ProcessModel;
import Model.Task;
import Model.TaskModel;
import Model.Tree.ITreeNode;
import Model.Tree.TypeNode;
import Dialog.Dialogs;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by SunnyD on 2017/5/3.
 */
public class TaskManageTab extends Tab {
    @FXML ListView<ITreeNode> processModelList;
    @FXML TreeView<ITreeNode> taskTree;
    @FXML ContextMenu contextMenu;
    @FXML ScrollPane taskScrollView;
    @FXML WebView taskWebView;
    @FXML VBox taskViewVBox;
    private HashMap<Integer,Task> taskMap;
    private HashMap<Integer,ProcessModel> processModelMap;
    private HashMap<String, ArrayList<String>> taskDataMap;

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
    }

    public void init(){
        taskMap=new HashMap<>();
        processModelMap=new HashMap<>();
        taskDataMap=new HashMap<String, ArrayList<String>>();
        this.initProcessModelList();

    }
    /*
    初始化任务列表，任务列表中分为：已完成，待处理，未分配三个类别。从数据库中的task表中读取对应用户的任务。
     */
    public void initTaskList(){
        String username=Login.getInstance().getUsername();
        try{
            String sql="select * from task where UserId = '"+username+"'";
            ResultSet res=ConnDB.getInstance().executeQuery(sql);
            while(res.next()){
                Task p = new Task(res.getInt("TaskId"),res.getInt("TaskModelId"),
                        res.getInt("ProcessId"),res.getString("Data"),res.getString("UserId"),res.getInt("Status"));

                String sql2="select * from task_model where TaskModelId='"+res.getString("TaskModelId")+"'";
                ResultSet res2=ConnDB.getInstance().executeQuery(sql2);
                while(res2.next()){
                    p.setTaskModel(new TaskModel(res2.getInt("TaskModelId"),res2.getString("Name"),res2.getString("Form"),
                            res2.getString("Type"),res2.getString("Domins"),res2.getString("Role"),res2.getString("Topic")));
                }
                p.setName(p.getTaskModel().getName());
                taskMap.put(p.getId(),p);
            }

        }catch (SQLException e){
            e.printStackTrace();
        }
        //init TreeTypeNode
        TreeItem<ITreeNode> root=new TreeItem<ITreeNode>(new TypeNode("root"));
        root.setExpanded(true);
        taskTree.setRoot(root);
        TreeItem<ITreeNode> finished=new TreeItem<ITreeNode>(new TypeNode("已完成任务"));
        TreeItem<ITreeNode> running=new TreeItem<ITreeNode>(new TypeNode("待处理任务"));
        TreeItem<ITreeNode> wait=new TreeItem<ITreeNode>(new TypeNode("未分配任务"));
        root.getChildren().add(finished);
        root.getChildren().add(running);
        root.getChildren().add(wait);


        for(Task a:taskMap.values()){
            if(a.getStatus()==Task.STATUS_RUNNING){
                running.getChildren().add(new TreeItem<>(a));
            }else if(a.getStatus()==Task.STATUS_FINISHED){
                finished.getChildren().add(new TreeItem<>(a));
            }else{
                wait.getChildren().add(new TreeItem<>(a));
            }

        }

        this.addTaskTreeListenner();
    }
    /*
        添加任务管理树的监听器
     */
    private void addTaskTreeListenner(){
        taskTree.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<ITreeNode>>() {
            @Override
            public void changed(ObservableValue<? extends TreeItem<ITreeNode>> observable, TreeItem<ITreeNode> oldValue, TreeItem<ITreeNode> newValue) {
                taskDataMap.clear();

                taskViewVBox.getChildren().clear();

                if(newValue.getValue() instanceof TypeNode){
                    System.out.println(newValue.getValue());
                }else if(newValue.getValue() instanceof Task){
                    Task p = (Task)newValue.getValue();

                    generateTaskContend(p);

                    //提交按钮
                    if(newValue.getParent().getValue().getName().equals("待处理任务")){
                        Button bt=new Button("提交");

                        bt.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent event) {
                                saveAndSendMessage();//发送消息
                            }
                        });
                        taskViewVBox.getChildren().add(bt);
                    }

                }
            }
        });
    }

    /*
        任务表单内容的生成
     */
    private void generateTaskContend (Task p) {
        String[] coms=p.getData().split(";");
        for(String com: coms){
            //字段信息保存
            String[] values=com.split(":");
            taskDataMap.put(values[0],new ArrayList<>());
            for(int i=1;i<values.length;i++){
                taskDataMap.get(values[0]).add(values[i]);
            }
            //生成表单界面
            HBox hb=new HBox();
            hb.setSpacing(4);
            try{
                if(values[1].equals("text")){//字段类型的判断
                    Label label=new Label(values[0]);
                    hb.getChildren().add(label);
                    TextField tf =new TextField();
                    hb.getChildren().add(tf);
                    if(values[2].equals("r")&&values.length<=4){
                        tf.setEditable(false);
                        tf.setText(values[3]);
                    }
                    tf.setUserData(values[0]);
                }else if(values[1].equals("date")){
                    Label label=new Label(values[0]);
                    hb.getChildren().add(label);

                    DatePicker dp=new DatePicker();
                    hb.getChildren().add(dp);
                    if(values[2].equals("r")&&values.length<=4){
                        dp.setEditable(false);
                        String date[]=values[3].split("-");
                        dp.setValue(LocalDate.of(Integer.valueOf(date[0]),Integer.valueOf(date[1]),Integer.valueOf(date[2])));
                    }
                    dp.setUserData(values[0]);
                }
                taskViewVBox.getChildren().add(hb);
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }

    /*
    *   初始化显示流程模型列表，从数据库中读取数据
     */

    public void initProcessModelList(){
        String sql="select * from process_model";
        ResultSet res=ConnDB.getInstance().executeQuery(sql);
        try {
            while(res.next()){
                ProcessModel p=new ProcessModel(res.getInt("ProcessModelId"),res.getString("Name"),res.getString("ModelData"));
                processModelMap.put(p.getId(),p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        ObservableList<ITreeNode> olist= FXCollections.observableArrayList();

        processModelList.setItems(olist);

        for(ProcessModel p:processModelMap.values()){
            olist.add(p);
        }
        this.addProcessModelListenner();
    }

    private void addProcessModelListenner(){
        processModelList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ITreeNode>() {
            @Override
            public void changed(ObservableValue<? extends ITreeNode> observable, ITreeNode oldValue, ITreeNode newValue) {
                System.out.println(newValue.getName());
                WebEngine we=taskWebView.getEngine();
                we.load("http://localhost:8080/GraphEditor/index.html");
                we.executeScript("");
            }
        });

    }
    /*
        点击流程模型列表出发的回调函数
     */
    @FXML
    private void onClickedProcessListView(MouseEvent e){
        if(e.getClickCount()==2){
            Dialogs.getInstance().showNewProcessDialog(new Stage());
        }

    }
    /*
    * 存储任务编辑后的信息并发送信息到到主题,任务信息需要从@Value taskDataMap 与控件中的value进行合并。
    * */
    private void saveAndSendMessage(){
        try{
            for(int i=0;i<taskViewVBox.getChildren().size()-1;i++){//存储控件的编辑信息
                HBox hb=(HBox)taskViewVBox.getChildren().get(i);
                for(int j=0;j<hb.getChildren().size();j++){
                    if(hb.getChildren().get(j).getUserData()!=null&&taskDataMap.get(hb.getChildren().get(j).getUserData()).size()<3){
                        if(hb.getChildren().get(j) instanceof TextField){
                            TextField tf=(TextField) hb.getChildren().get(j);
                            taskDataMap.get(tf.getUserData()).add(tf.getText());
                        }else if(hb.getChildren().get(j) instanceof DatePicker){
                            DatePicker dp=(DatePicker)hb.getChildren().get(j);
                            taskDataMap.get(dp.getUserData()).add(dp.getValue().toString());
                        }
                    }
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
            Dialogs.getInstance().showMessageDialog(new Stage(),"填写内容出错！","错误");
        }

        for(String s:taskDataMap.keySet()){
            System.out.println(s+":"+taskDataMap.get(s));
        }

    }
    /*
        点击 新建流程 标签触发的回调函数，会显示webView
     */
    @FXML
    void showWebView(){
        taskWebView.setVisible(true);
        taskScrollView.setVisible(false);
    }
    /*
        点击 任务处理 标签触发的回调函数，会显示任务信息内容
     */
    @FXML
    void showTaskScroll(){
        taskWebView.setVisible(false);
        taskScrollView.setVisible(true);
    }

}
