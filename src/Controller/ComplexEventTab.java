package Controller;

import Controller.Parse.EPCParser;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.io.IOException;

/**
 * Created by SunnyD on 2016/11/13.
 */
public class ComplexEventTab extends Tab{

    @FXML private Label infolabel;
    private EPCParser parser;

    public ComplexEventTab(){
        FXMLLoader loader=new FXMLLoader(getClass().getResource("/View/ComplexEventTab.fxml"));
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
        parser=EPCParser.getEPCParserInstance();

    }
    @FXML
    public void onOpenEPMLAction(){//读取文件并解析节点
        parser.setInfoLabel(infolabel);//传入infolabel实例

        Window window=infolabel.getScene().getWindow();
        FileChooser openfile=new FileChooser();
        openfile.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("XML","*.xml"),
                new FileChooser.ExtensionFilter("EPML","*.epml")
        );
        openfile.setTitle("请打开EPML文件");
        File file=openfile.showOpenDialog(window);
        if(file!=null){
            EPCParser.getEPCParserInstance().read(file);
        }
    }
    @FXML
    public void onParseAction(){
        EPCParser.getEPCParserInstance().publishAndSubscribe();
    }
}
