package Controller.tab;

import Controller.Db.ConnDB;
import Controller.Parse.BufferedImageTranscoder;
import Controller.Parse.EPCParser;
import Controller.Parse.SVGModelParser;
import Model.Node.Process;
import Model.Node.INode;
import Model.Node.TreeNode.TypeNode;
import Model.Node.TreeNode.UserNode;
import Model.Dialog.Dialogs;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.dom4j.Element;

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
    @FXML TreeView<INode> typeManageTreeView;
    @FXML VBox processMainVBox;
    @FXML VBox roleVBox;
    @FXML ImageView processImageView;
    @FXML StackPane processStackPane;
    @FXML Label processNameLabel;
    @FXML Label processModelIDLabel;
    @FXML Label processUserLabel;
    @FXML Label processIDLabel;
    @FXML ListView<INode> userTypeListView;
    @FXML Label selectedUserLabel;
    private static HashMap<Integer,Process> processList;
    private Process selectedProcessNode;
    private UserNode selectedUserNode;
    private TreeItem<INode> selectedTypeTreeItem;
    private BufferedImageTranscoder trans;
    private TreeItem<INode> finishedItem;
    private TreeItem<INode> runningItem;
    private TreeItem<INode> userTypeTreeRoot;
    private Tooltip showProcessTip;
    private DropShadow dropShadow;
    private ColorAdjust colorAdjust;

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
        dropShadow = new DropShadow( 20, Color.AQUA );
        showProcessTip=new Tooltip();
        colorAdjust = new ColorAdjust();
        colorAdjust.setBrightness(-0.8);

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
                        res.getInt("Status"),res.getString("UserId"),res.getString("Finished_func"),res.getString("Finished_event"));
                //生成流程实例对应的EPC解析对象
                EPCParser epc=new EPCParser();
                epc.read(TaskManageTab.getProcessModelMap().get(p.getModelId()).getModelData());//通过流程模型Map获取modeldata
                p.setEpc(epc);
                //生成流程实例对应的SVG解析对象
                SVGModelParser svg = new SVGModelParser();
                svg.read(TaskManageTab.getProcessModelMap().get(p.getModelId()).getGraphData());
                p.setSvg(svg);
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
                processStackPane.getChildren().clear();
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
                        processStackPane.getChildren().add(processImageView);
                    } catch (TranscoderException e) {
                        e.printStackTrace();
                    }
                    //添加元素节点
                    addImageViewstoStackPane(selectedProcessNode);

                    //更新流程元素状态
                    loadProcessBaseState(selectedProcessNode);

                    //更新标签信息
                    updateLabelInfo(selectedProcessNode);
                }

            }
        });

    }

    private void addImageViewstoStackPane(Process p){
        HashMap<String,ImageView> funcMap =p.getSvg().getFuncImagesMap();

        for(String name: funcMap.keySet()){
            System.out.println(name);
            ImageView v = funcMap.get(name);
            processStackPane.getChildren().add(v);
            v.setOnMouseEntered(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    showProcessTip.setText(p.getEpc().getFuncByName(name).getFuncInfo());
                    showProcessTip.show(v,event.getScreenX()+10,event.getScreenY()+10);
                    if(v.getEffect()==null){
                        v.setEffect(dropShadow);
                    }

                    System.out.println(event.getScreenX() +","+event.getScreenY());
                }
            });
            v.setOnMouseExited(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    showProcessTip.hide();
                    if(v.getEffect()==dropShadow){
                        v.setEffect(null);
                    }
                    System.out.println("exit");
                }
            });


        }
        HashMap<String,ImageView> eventMap =p.getSvg().getEventImagesMap();
        for(ImageView v: eventMap.values()){
//            v.setEffect(colorAdjust);
            processStackPane.getChildren().add(v);
        }

    }

    private void loadProcessBaseState(Process p){
        for(String name : p.getFinishedFuncList()){
            try{
                if(!name.equals("")){
                    ImageView v=p.getSvg().getFuncImagesMap().get(name);
                    v.setEffect(colorAdjust);
                }

            }catch (NullPointerException e){
                e.printStackTrace();
            }
        }

        for(String name : p.getFinishedEventList()){
            try{
                if(!name.equals("")){
                    ImageView v=p.getSvg().getEventImagesMap().get(name);
                    v.setEffect(colorAdjust);
                }
            }catch (NullPointerException e){
                e.printStackTrace();
            }
        }
    }

    private void updateLabelInfo(Process p){
        processIDLabel.setText("ID为:"+p.getId());
        processNameLabel.setText("名称为:"+p.getName());
        processModelIDLabel.setText("模型ID为:"+p.getModelId());
        processUserLabel.setText("创建用户为:"+p.getUserid());
    }

    public void initTypeManageTree(){
        userTypeTreeRoot= new TreeItem<INode>(new TypeNode("Root",0));
        userTypeTreeRoot.setExpanded(true);
        typeManageTreeView.setRoot(userTypeTreeRoot);
        Connection conn=ConnDB.getInstance().getConn();
        this.generateTypeTreeType(userTypeTreeRoot,conn,true);
        this.addTypeTreeListenner();
    }

    /*
    生成组织管理树的类型节点
     */
    private void generateTypeTreeType(TreeItem<INode> root,Connection conn,boolean leaf){
        int id = ((TypeNode)root.getValue()).getId();
        String sql = "select id,name from type_user where parentID ='"+id+"'";
        ResultSet res=ConnDB.getInstance().executeQuery(sql,conn);
        try {
            while(res.next()){
                TreeItem<INode> p = new TreeItem<>(new TypeNode(res.getString("name"),res.getInt("id")));
                p.setGraphic(new ImageView(new Image("imgs/icons/Folder_mac_16px.png")));
                root.getChildren().add(p);
                generateTypeTreeType(p,conn,leaf);
                if(leaf){
                    this.generateTypeTreeLeaf(p,conn);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /*
    生成组织管理树的叶子节点
     */
    private void generateTypeTreeLeaf(TreeItem<INode> root,Connection conn){

        if(root.getValue() instanceof TypeNode){
            int id = ((TypeNode)root.getValue()).getId();
            String sql = "select userid from user_type where typeid='"+id+"'";
            ResultSet res =ConnDB.getInstance().executeQuery(sql,conn);
            try{
                while(res.next()){
                    TreeItem<INode> p = new TreeItem<>(new UserNode(res.getString("userid"),id));
                    p.setGraphic(new ImageView(new Image("imgs/icons/user_16px.png")));
                    root.getChildren().add(p);
                }
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
    }
    /*
    添加组织管理树的监听器
     */
    private void addTypeTreeListenner(){
        typeManageTreeView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<INode>>() {
            @Override
            public void changed(ObservableValue<? extends TreeItem<INode>> observable, TreeItem<INode> oldValue, TreeItem<INode> newValue) {
                selectedTypeTreeItem=newValue;
                INode p1 = newValue.getValue();
                if(p1 instanceof UserNode){
                    UserNode p =(UserNode)p1;
                    selectedUserLabel.setText("选择的用户是:"+p.getName());
                    initUserTypeListView(p.getName());
                    selectedUserNode=p;
                }
            }
        });
    }

    private void initUserTypeListView(String name){
        userTypeListView.getItems().clear();
        String sql = "select a.name,a.id from type_user as a,user_type as b where b.typeid=a.id and b.userid='"+name+"'";
        ResultSet res = ConnDB.getInstance().executeQuery(sql,ConnDB.getInstance().getConn());
        try {
            while(res.next()){
                userTypeListView.getItems().add(new TypeNode(res.getString("name"),res.getInt("id")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /*
    组织管理树添加按钮的回调函数
     */
    @FXML
    public void addUserTypeAction(){
        TreeItem<INode> p = Dialogs.getInstance().showAddUserTypeDialog(new Stage(),userTypeTreeRoot);
        if(p!=null){
            TypeNode type = (TypeNode)p.getValue();
            UserNode u =new UserNode(selectedUserNode.getUserID(),type.getId());
            TreeItem<INode> t = new TreeItem<>(u);
            t.setGraphic(new ImageView(new Image("imgs/icons/user_16px.png")));
            p.getChildren().add(t);//给选择类型树添加用户节点

            //给组织列表添加组织类型
            userTypeListView.getItems().add(type);
            //更新数据库数据
            String sql = "insert into user_type (userid,typeid) values('"+selectedUserNode.getUserID()+"','"+type.getId()+"')";
            ConnDB.getInstance().executeUpdate(sql);
        }
    }

    /*
        对用户组织树进行删除操作的回调函数
     */
    @FXML
    private void deleteUserAndBackCallBack(){
        //从数据库中删除相应节点
        if(selectedTypeTreeItem.getValue() instanceof TypeNode){
            TypeNode p = (TypeNode) selectedTypeTreeItem.getValue();

            String sql = "delete from type_user where id = '"+p.getId()+"'";
            ConnDB.getInstance().executeUpdate(sql);
        }else if(selectedTypeTreeItem.getValue() instanceof  UserNode){
            UserNode u = (UserNode) selectedTypeTreeItem.getValue();

            String sql = "delete from user_type where userid ='"+u.getUserID()+"'and typeid='"+u.getTypeID()+"'";
            ConnDB.getInstance().executeUpdate(sql);
        }
        selectedTypeTreeItem.getParent().getChildren().remove(selectedTypeTreeItem);

    }

    /*
    * 用户组织树的刷新回调函数
     */
    @FXML
    private void refreshTypeTreeCallBack(ActionEvent e){
        typeManageTreeView.refresh();
    }

    /*
    * 进程实例树的刷新回调函数
     */
    @FXML
    private void refreshProcessTreeList(ActionEvent e){
        processTreeView.refresh();
        this.initProcessList();
    }

    /*
    * 显示角色管理界面的回调函数
     */
    @FXML
    private void showRoleManageView(){
        processMainVBox.setVisible(false);
        roleVBox.setVisible(true);
    }

    /*
    * 显示流程实例管理的回调函数
     */
    @FXML
    private void showProcessView(){
        processMainVBox.setVisible(true);
        roleVBox.setVisible(false);
    }

    public static HashMap<Integer, Process> getProcessList() {
        return processList;
    }
}
