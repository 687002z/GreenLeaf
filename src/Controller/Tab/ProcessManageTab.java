package Controller.tab;

import Controller.Db.ConnDB;
import Controller.Parse.BufferedImageTranscoder;
import Controller.Parse.EPCParser;
import Model.Node.Process;
import Model.Node.INode;
import Model.Node.TreeNode.TypeNode;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * Created by SunnyD on 2017/5/3.
 */
public class ProcessManageTab extends Tab {
    @FXML TreeView<INode> processTreeView;
    @FXML TreeView<TypeNode> typeManageTreeView;
    @FXML VBox processMainVBox;
    @FXML ScrollPane roleScrollView;
    @FXML ImageView processImageView;
    @FXML StackPane processStackPane;
    @FXML Label processNameLabel;
    @FXML Label processModelIDLabel;
    @FXML Label processUserLabel;
    @FXML Label processIDLabel;
    private static HashMap<Integer,Process> processList;
    private Process selectedProcessNode;
    private BufferedImageTranscoder trans;
    private TreeItem<INode> finishedItem;
    private TreeItem<INode> runningItem;

    public ProcessManageTab(){
        FXMLLoader loader=new FXMLLoader(getClass().getResource("/View/ProcessManageTab.fxml"));
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
        trans = new BufferedImageTranscoder();
        processList = new HashMap<>();
    }

    /*
    * 初始化流程实例列表
     */
    public void initProcessList(){
        processList.clear();
        processTreeView.setRoot(new TreeItem<>(new TypeNode("Root")));
        processTreeView.getRoot().getChildren().clear();
        this.initProcessBaseTree();
        processTreeView.getRoot().setExpanded(true);
        String sql = "select * from process";
        ResultSet res= ConnDB.getInstance().executeQuery(sql,ConnDB.getInstance().getConn());
        try {
            while(res.next()){
                Process p = new Process(res.getInt("ProcessId"),res.getString("Name"),res.getInt("ModelId"),
                        res.getInt("Status"),res.getString("UserId"),res.getString("Func"));
                EPCParser epc=new EPCParser();//生成流程实例对应的EPC解析对象
                epc.read(TaskManageTab.getProcessModelMap().get(p.getModelId()).getModelData());//通过流程模型Map获取modeldata
                p.setEpc(epc);
                runningItem.getChildren().add(new TreeItem<>(p));
                processList.put(p.getId(),p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        this.addProcessListenner();
    }

    private void initProcessBaseTree(){

        INode finished = new TypeNode("已完成");
        finishedItem=new TreeItem<>(finished);
        processTreeView.getRoot().getChildren().add(finishedItem);
        INode running = new TypeNode("正在进行");
        runningItem=new TreeItem<>(running);
        processTreeView.getRoot().getChildren().add(runningItem);
    }

    /*
    * 给流程实例选项添加监听器
     */
    private void addProcessListenner(){
        processTreeView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<INode>>() {
            @Override
            public void changed(ObservableValue<? extends TreeItem<INode>> observable, TreeItem<INode> oldValue, TreeItem<INode> newValue) {
                if(newValue.getValue() instanceof Process){
                    selectedProcessNode =  (Process) newValue.getValue();

                    // file may be an InputStream.
                    // Consult Batik's documentation for more possibilities!
                    int modelID=(selectedProcessNode).getModelId();
                    String graphData=TaskManageTab.getProcessModelMap().get(modelID).getGraphData();
                    TranscoderInput transIn = new TranscoderInput(new ByteArrayInputStream(graphData.getBytes()));

                    try {
                        trans.transcode(transIn, null);
                        // Use WritableImage if you want to further modify the image (by using a PixelWriter)
                        Image img = SwingFXUtils.toFXImage(trans.getBufferedImage(), null);
                        processImageView.setImage(img);
                    } catch (TranscoderException e) {
                        e.printStackTrace();
                    }

                    updateLabelInfo(selectedProcessNode);
                }

            }
        });

    }

    private void updateLabelInfo(Process p){
        processIDLabel.setText("ID为:"+p.getId());
        processNameLabel.setText("名称为:"+p.getName());
        processModelIDLabel.setText("模型ID为:"+p.getModelId());
        processUserLabel.setText("创建用户为:"+p.getUserid());
    }

    public void initTypeManageTree(){
        TreeItem<TypeNode> p= new TreeItem<>(new TypeNode("Root",0));
        typeManageTreeView.setRoot(p);
        Connection conn=ConnDB.getInstance().getConn();
        this.generateTypeManageTreeType(p,conn);
    }
    private void generateTypeManageTreeType(TreeItem<TypeNode> root,Connection conn){
        int id = root.getValue().getId();
        String sql = "select id,name from type_user where parentID ='"+id+"'";
        ResultSet res=ConnDB.getInstance().executeQuery(sql,ConnDB.getInstance().getConn());
        try {
            while(res.next()){
                TreeItem<TypeNode> p = new TreeItem<>(new TypeNode(res.getString("name"),res.getInt("id")));
                root.getChildren().add(p);
                generateTypeManageTreeType(p,conn);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    /*
    * 进程实例列表的刷新回调函数
     */
    @FXML
    private void refreshList(ActionEvent e){
        processTreeView.refresh();
        this.initProcessList();
    }

    /*
    * 显示角色管理界面的回调函数
     */
    @FXML
    private void showRoleManageView(){
        processMainVBox.setVisible(false);
        roleScrollView.setVisible(true);
    }

    /*
    * 显示流程实例管理的回调函数
     */
    @FXML
    private void showProcessView(){
        processMainVBox.setVisible(true);
        roleScrollView.setVisible(false);
    }

    public static HashMap<Integer, Process> getProcessList() {
        return processList;
    }
}
