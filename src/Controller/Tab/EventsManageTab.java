package Controller.Tab;

import Controller.Parse.Xsd.XSDNode;
import Controller.Parse.Xsd.XSDReader;
import Controller.Db.ConnDB;
import Model.Node.Tree.EventNode;
import Model.Node.Tree.INode;
import Model.Node.Tree.TopicNode;
import Model.Node.Tree.TypeNode;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Created by SunnyD on 2016/8/9.
 */
public class EventsManageTab extends Tab{
    @FXML private TreeView topicTree;
    @FXML private HBox hbox1;
    @FXML private ComboBox fieldComboBox;
    @FXML private ComboBox operatorComboBox;
    @FXML private TextField valueTextField;
    @FXML private ListView eventsInfoListView;
    @FXML private Button addButton;
    @FXML private Button deleteButton;
    @FXML private Button saveButton;
    private ConnDB conn;
    private TreeItem<INode> root;
    private XSDReader xsdReader;

    public EventsManageTab(){
        FXMLLoader loader=new FXMLLoader(getClass().getResource("/View/EventsManageTab.fxml"));
        loader.setRoot(this);

        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.init();
    }

    private void init() {//初始化函数
        TypeNode r=new TypeNode("root");
        root=new TreeItem<INode>(r);
        topicTree.setRoot(root);
        conn=new ConnDB();

        xsdReader = new XSDReader();

        operatorComboBox.getItems().addAll(">","<","=",">=","<=");

        this.createTopicNode();//创建主题节点
        this.generateEventNode(root);//生成事件节点
        this.topicTreeSelectionHandle();
        this.addButtonAction();//添加按钮动作
    }
    
    private void createTopicNode(){
        TopicNode topic1=new TopicNode("note");
        topic1.setXsdFileName("note.xsd");
        root.getChildren().add(new TreeItem<INode>(topic1));

        TopicNode topic2=new TopicNode("shiporder");
        topic2.setXsdFileName("shiporder.xsd");
        root.getChildren().add(new TreeItem<INode>(topic2));
        
    }
    private void generateEventNode(TreeItem<INode> root){
        INode value=root.getValue();
        if(value instanceof TypeNode){
            root.setGraphic(new ImageView(new Image("imgs/icons/Folder_Mac_16px.png")));
            ObservableList<TreeItem<INode>> list=root.getChildren();
            for(int i=0;i<list.size();i++){
                generateEventNode(list.get(i));
            }
        }else if(value instanceof TopicNode){
            root.setGraphic(new ImageView(new Image("imgs/icons/topic_21px.png")));
            ResultSet rs=conn.executeQuery("SELECT * FROM event_node WHERE Topic='"+value.toString()+"'");
            try {
                while(rs.next()){
                    EventNode eventNode=new EventNode(rs.getString("Name"));
                    eventNode.setTopic(value.toString());
                    eventNode.setConditions(rs.getString("Conditions"));
                    TreeItem<INode> item=new TreeItem<INode>(eventNode);
                    item.setGraphic(new ImageView(new Image("imgs/icons/event_node_21px.png")));
                    root.getChildren().add(item);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void topicTreeSelectionHandle(){
        topicTree.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                TreeItem<INode> node=(TreeItem<INode>)newValue;
                Object value=node.getValue();
                if(value instanceof TypeNode){
                    clearContent();
                }else if(value instanceof TopicNode){
                    clearContent();
                    fieldComboBox.getItems().clear();
                    String fileName = ((TopicNode) value).getXsdFileName();
                    try {

                        List<XSDNode> nodes = xsdReader.paserXSD("res/"+fileName, 0);
                        for (XSDNode node1 : nodes) {
                            fieldComboBox.getItems().add(node1.getName());
                        }
                        nodes.clear();
                        nodes = xsdReader.paserXSD("res/"+fileName, 1);
                        for (XSDNode node1 : nodes) {
                            fieldComboBox.getItems().add(node1.getName());
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                }else if(value instanceof EventNode){
                    clearContent();
                }
            }
        });
    }
    private void addButtonAction(){
        addButton.setOnAction((event)->{//addButton Action
            if(fieldComboBox.getSelectionModel().getSelectedItem()!=null&&operatorComboBox.getSelectionModel().getSelectedItem()!=null&&!valueTextField.getText().equals("")){
                String fieldStr=fieldComboBox.getSelectionModel().getSelectedItem().toString();
                String operatorStr=operatorComboBox.getSelectionModel().getSelectedItem().toString();
                String valueStr=valueTextField.getText();
                eventsInfoListView.getItems().addAll(fieldStr+" "+operatorStr+" "+valueStr);
            }
        });
        deleteButton.setOnAction((event)->{

            Object selectedObj=eventsInfoListView.getSelectionModel().getSelectedItem();
            if(selectedObj!=null){
                eventsInfoListView.getItems().remove(selectedObj);
            }
        });
        saveButton.setOnAction((event)->{
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Please input event name.");
            dialog.setContentText("Please input event name.");

            Optional<String> result=dialog.showAndWait();
            if(result.isPresent()){
                String eventName=result.get();
                System.out.println(eventName);

                TreeItem<INode> topicNode= (TreeItem) topicTree.getSelectionModel().getSelectedItem();
                EventNode eventNode=new EventNode(eventName);
                eventNode.setTopic(topicNode.getValue().getName());
                String conditions="";
                ObservableList<String> conlist=eventsInfoListView.getItems();
                for(int i=0;i<conlist.size();i++){
                    conditions+=conlist.get(i)+";";
                }
                eventNode.setConditions(conditions);

                TreeItem<INode> eventItem=new TreeItem<INode>(eventNode);
                eventItem.setGraphic(new ImageView(new Image("imgs/icons/event_node_21px.png")));
                topicNode.getChildren().add(eventItem);//树形结构中添加子节点

                conn.executeUpdate("INSERT INTO event_node (Name,Topic,Conditions) VALUES('" + eventName + "','"
                        + eventNode.getTopic() + "','" + eventNode.getConditions() + "')");
                conn.close();//关闭数据库

            }

        });

    }
    private void clearContent(){
        fieldComboBox.getItems().clear();
        valueTextField.setText("");
        eventsInfoListView.getItems().clear();
    }
}
