package Controller;

import Controller.Db.ConnDB;
import Model.Login;
import Model.Task;
import Model.TaskModel;
import Model.Tree.ITreeNode;
import Model.Tree.TypeNode;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by SunnyD on 2017/5/3.
 */
public class TaskManageTab extends Tab {
    @FXML ListView<Process> newProcess;
    @FXML TreeView<ITreeNode> taskTree;
    private ArrayList<Task> taskList;
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
        taskList=new ArrayList<>();
        this.initTaskList();
    }

    private void initTaskList(){
        String username=Login.getInstance().getUsername();
            try{
                String sql="select * from task where UserId = '"+username+"'";
                ResultSet res=ConnDB.getInstance().executeQuery(sql);
                while(res.next()){
                    Task p = new Task(res.getInt("TaskId"),res.getInt("TaskModelId"),
                            res.getInt("ProcessId"),res.getString("Data"),res.getString("UserId"),
                            res.getString("Role"),res.getInt("Status"));

                    String sql2="select * from task_model where TaskModelId='"+res.getString("TaskModelId")+"'";
                    ResultSet res2=ConnDB.getInstance().executeQuery(sql2);
                    while(res2.next()){
                        p.setTaskModel(new TaskModel(res.getInt("TaskModelId"),res.getString("Name"),res.getString("Form"),
                                res.getString("Type"),res.getString("Domins"),res.getString("Role"),res.getString("Topic")));
                    }
                    p.setName(p.getTaskModel().getName());
                    taskList.add(p);
                }


            }catch (SQLException e){
                e.printStackTrace();
            }
            //init TreeTypeNode
            TreeItem<ITreeNode> root=new TreeItem<ITreeNode>(new TypeNode("root"));
            taskTree.setRoot(root);
            TreeItem<ITreeNode> finished=new TreeItem<ITreeNode>(new TypeNode("已完成任务"));
            TreeItem<ITreeNode> running=new TreeItem<ITreeNode>(new TypeNode("待处理任务"));
            TreeItem<ITreeNode> wait=new TreeItem<ITreeNode>(new TypeNode("未分配任务"));
            root.getChildren().add(finished);
            root.getChildren().add(running);
            root.getChildren().add(wait);


            for(Task a:taskList){
                if(a.getStatus()==Task.STATUS_RUNNING){
                    running.getChildren().add(new TreeItem<>(a));
                }else if(a.getStatus()==Task.STATUS_FINISHED){
                    finished.getChildren().add(new TreeItem<>(a));
                }else{
                    wait.getChildren().add(new TreeItem<>(a));
                }

            }

    }


}
