package Controller;

import Controller.Db.ConnDB;
import Controller.Parse.BufferedImageTranscoder;
import Controller.Parse.EPCParser;
import Model.Process;
import Model.Tree.INode;
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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * Created by SunnyD on 2017/5/3.
 */
public class ProcessManageTab extends Tab {
    @FXML ListView<INode> processListView;
    @FXML VBox processMainVBox;
    @FXML ScrollPane roleScrollView;
    @FXML ImageView processImageView;
    @FXML StackPane processStackPane;
    private static HashMap<Integer,Process> processList;
    private Process selectedProcessNode;
    private BufferedImageTranscoder trans;

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
        processListView.getItems().clear();
        String sql = "select * from process";
        ResultSet res= ConnDB.getInstance().executeQuery(sql);
        try {
            while(res.next()){
                Process p = new Process(res.getInt("ProcessId"),res.getString("Name"),res.getInt("ModelId"),
                        res.getInt("Status"),res.getString("UserId"),res.getString("Func"));
                EPCParser epc=new EPCParser();//生成流程实例对应的EPC解析对象
                epc.read(TaskManageTab.getProcessModelMap().get(p.getModelId()).getModelData());//通过流程模型Map获取modeldata
                p.setEpc(epc);
                processListView.getItems().add(p);
                processList.put(p.getId(),p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        this.addProcessListenner();
    }
    /*
    * 给流程实例选项添加监听器
     */
    private void addProcessListenner(){
        processListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<INode>() {
            @Override
            public void changed(ObservableValue<? extends INode> observable, INode oldValue, INode newValue) {
                selectedProcessNode = (Process) newValue;

                // file may be an InputStream.
                // Consult Batik's documentation for more possibilities!
                int modelID=((Process) newValue).getModelId();
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

            }
        });

    }
    /*
    * 进程实例列表的刷新回调函数
     */
    @FXML
    private void refreshList(ActionEvent e){
        processListView.refresh();
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
