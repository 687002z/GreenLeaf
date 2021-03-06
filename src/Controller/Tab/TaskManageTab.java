package Controller.tab;

import Controller.ComplexEventCal;
import Controller.Db.ConnDB;
import Controller.Parse.BufferedImageTranscoder;
import Controller.Parse.EPCParser;
import Controller.Parse.SVGModelParser;
import Model.*;
import Model.Node.*;
import Model.Node.EPCNode.Func;
import Model.Node.Process;
import Model.Node.TreeNode.TypeNode;
import Model.Dialog.Dialogs;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by SunnyD on 2017/5/3.
 */
public class TaskManageTab extends Tab {
    @FXML ListView<INode> processModelList;
    @FXML TreeView<INode> taskTree;
    @FXML ContextMenu contextMenu;
    @FXML ScrollPane taskScrollView;
    @FXML VBox taskViewVBox;
    @FXML ImageView processModelImageView;
    @FXML StackPane processModelStackPane;
    @FXML Label processModelNameLabel;
    @FXML Label processModelIdLabel;
    private HashMap<Integer,Task> taskMap;
    public static HashMap<Integer,ProcessModel> processModelMap;
    private HashMap<String, ArrayList<String>> taskDataMap;
    private ProcessModel selectedProcessModelNode;
    private ImageView test;
    private BufferedImageTranscoder trans;

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

        trans = new BufferedImageTranscoder();

        this.initProcessModelList();

    }
    /*
    初始化任务列表，任务列表中分为：已完成，待处理，未分配三个类别。从数据库中的task表中读取对应用户的任务。
     */
    public void initTaskList(){
        String username=Login.getInstance().getUsername();
        try{
            String sql="select * from task where UserId = '"+username+"'";
            ConnDB conn= ConnDB.getInstance();
            Connection connection=conn.getConn();
            ResultSet res=conn.executeQuery(sql,connection);
            while(res.next()){
                Task p = new Task(res.getInt("TaskId"),res.getInt("TaskModelId"),
                        res.getInt("ProcessId"),res.getString("Data"),res.getString("UserId"),res.getInt("Status"));

                String sql2="select * from task_model where TaskModelId='"+res.getString("TaskModelId")+"'";
                ResultSet res2=ConnDB.getInstance().executeQuery(sql2,connection);
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
        TreeItem<INode> root=new TreeItem<INode>(new TypeNode("root"));
        root.setExpanded(true);
        taskTree.setRoot(root);
        TreeItem<INode> finished=new TreeItem<INode>(new TypeNode("已完成任务"));
        TreeItem<INode> running=new TreeItem<INode>(new TypeNode("待处理任务"));
        TreeItem<INode> wait=new TreeItem<INode>(new TypeNode("未分配任务"));
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
        taskTree.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<INode>>() {
            @Override
            public void changed(ObservableValue<? extends TreeItem<INode>> observable, TreeItem<INode> oldValue, TreeItem<INode> newValue) {
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
                    if(values[2].equals("r")&&values.length>=4){
                        tf.setEditable(false);
                        tf.setText(values[3]);
                    }
                    tf.setUserData(values[0]);
                }else if(values[1].equals("date")){
                    Label label=new Label(values[0]);
                    hb.getChildren().add(label);

                    DatePicker dp=new DatePicker();
                    hb.getChildren().add(dp);
                    if(values[2].equals("r")&&values.length>=4){
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
        ResultSet res=ConnDB.getInstance().executeQuery(sql,ConnDB.getInstance().getConn());
        try {
            while(res.next()){
                ProcessModel p=new ProcessModel(res.getInt("ProcessModelId"),res.getString("Name"),
                        res.getString("ModelData"),res.getString("GraphData"));
                processModelMap.put(p.getId(),p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        ObservableList<INode> olist= FXCollections.observableArrayList();

        processModelList.setItems(olist);

        for(ProcessModel p:processModelMap.values()){
            olist.add(p);
        }
        this.addProcessModelListenner();
    }

    private void addProcessModelListenner(){
        processModelList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<INode>() {
            @Override
            public void changed(ObservableValue<? extends INode> observable, INode oldValue, INode newValue) {
                System.out.println(newValue.getName());
                selectedProcessModelNode = (ProcessModel) newValue;

                processModelNameLabel.setText("进程模型名称:"+newValue.getName());
                processModelIdLabel.setText("模型编号:"+((ProcessModel) newValue).getId());

                // file may be an InputStream.
                // Consult Batik's documentation for more possibilities!
                TranscoderInput transIn = new TranscoderInput(new ByteArrayInputStream(((ProcessModel) newValue).getGraphData().getBytes()));

                try {
                    trans.transcode(transIn, null);

                    // Use WritableImage if you want to further modify the image (by using a PixelWriter)
                    Image img = SwingFXUtils.toFXImage(trans.getBufferedImage(), null);

                    processModelImageView.setImage(img);

                } catch (TranscoderException e) {
                    e.printStackTrace();
                }

            }
        });

    }
    /*
        点击流程模型列表出发的回调函数
     */
    @FXML
    private void onClickedProcessListView(MouseEvent e){
        if(e.getClickCount()==2){
            String name=Dialogs.getInstance().showNewProcessDialog(new Stage());
            System.out.println(selectedProcessModelNode.toString());
            Process p=null;
            if(name!=null){
                p=this.createProcess(name);

            }
            if(p!=null){
                //创建任务实例,自动生成发起人和发起时间
                createTask(p);
            }
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
            Dialogs.getInstance().showMessageDialog(new Stage(),"填写内容出错！","错误");
        }

        for(String s:taskDataMap.keySet()){
            System.out.println(s+":"+taskDataMap.get(s));
        }

        //TODO
        //向发布订阅系统发布任务消息
        int statusCode = PubSubNode.getInstance().postMessage("Test2","test");
        System.out.println("Status Code:"+statusCode);



    }
    /**
     * 点击 新建流程 标签触发的回调函数，会显示webView
     */
    @FXML
    void showProcessScroll(){
        processModelStackPane.setVisible(true);
        taskScrollView.setVisible(false);
    }
    /**
     * 点击 任务处理 标签触发的回调函数，会显示任务信息内容
     */
    @FXML
    void showTaskScroll(){
        processModelStackPane.setVisible(false);
        taskScrollView.setVisible(true);
    }
    /*
    * 从流程模型表中选择需要创建的流程模型，创建流程实例，写入数据库
     */
    private Process createProcess(String name){
        //开启的流程实例注册到数据库中
        String sql="insert into process(Name,ModelId,Status,UserId,Finished_func,Finished_event) values ('"+name+"','"+selectedProcessModelNode.getId()+"','"
                +1+"','"+Login.getInstance().getUsername()+"','','')";//插入流程实例记录到数据库中
        ConnDB.getInstance().executeUpdate(sql);
        String sql2="SELECT max(ProcessId) FROM process";
        int id=-1;
        ResultSet res = ConnDB.getInstance().executeQuery(sql2,ConnDB.getInstance().getConn());
        try{
            while(res.next()){
                id=res.getInt(1);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }

        //创建进程节点实例，放入集合中用以维护
        Process p = new Process(id,name,selectedProcessModelNode.getId(),1,Login.getInstance().getUsername(),"","");

        //生成流程实例对应的EPC解析对象
        EPCParser epc=new EPCParser();
        epc.read(this.getProcessModelMap().get(p.getModelId()).getModelData());//通过流程模型Map获取modeldata
        p.setEpc(epc);
        //生成流程实例对应的SVG解析对象
        SVGModelParser svg = new SVGModelParser();
        svg.read(this.getProcessModelMap().get(p.getModelId()).getGraphData());
        p.setSvg(svg);

        ProcessManageTab.getProcessList().put(p.getId(),p);

        //开启复杂事件计算
        this.startComplexEventService(p);

        //发送起始事件
        sendStartEvent();

        return p;
    }
    /*
    *创建任务实例
    *需要获取职称下的所有用户并分发任务
     */
    private void createTask(Process p){
        HashMap<String,Func> funcs=p.getEpc().getFuncs();
        for(Func f:funcs.values()){
            System.out.println("ModelId"+f.getTaskModelId());

            //任务模型数据获取
            String taskData = "";
            String sql="select Domins from task_model where TaskModelId = '"+f.getTaskModelId()+"'";
            ConnDB conn= ConnDB.getInstance();
            Connection connection=conn.getConn();
            ResultSet res=conn.executeQuery(sql,connection);
            try {
                while(res.next()){
                    taskData = res.getString("Domins");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            //拼接发起人和发布时间
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String startDate = "发起时间:text:r:"+sdf.format(new Date())+";";
            String startPerson = "发起人:text:r:"+p.getUserid()+";"+startDate;
            taskData = startPerson + taskData;

            //任务实例入库
            sql="insert into task(TaskModelId,ProcessId,Data,UserId,status) values ('"+f.getTaskModelId()+
                    "','"+p.getId()+"',N'" +taskData+"','"+p.getUserid()+"','"+1+"')";//插入流程实例记录到数据库中
            ConnDB.getInstance().executeUpdate(sql);

            this.initTaskList();

        }
    }

    /*
    *发送起始事件
     */
    private void sendStartEvent(){
        System.out.println("发送起始事件开启流程！");
        //向发布订阅系统发布起始消息
        int statusCode = PubSubNode.getInstance().postMessage("start","@start@");
        System.out.println("Status Code:"+statusCode);
    }

    /*
    *开启复杂事件计算服务
     */
    private void startComplexEventService(Process p){
        System.out.println("准备开启复杂事件计算服务。");
        ComplexEventCal.getInstance().publishAndSubscribe(p.getEpc());
    }
    /*
    *右键菜单的刷新回调函数
     */
    @FXML
    public void refreshTree(ActionEvent e){
        taskTree.refresh();
    }

    public static HashMap<Integer, ProcessModel> getProcessModelMap() {
        return processModelMap;
    }

    //test Main
    public static void main(String[] args) {
        TaskManageTab t = new TaskManageTab();

    }
}
