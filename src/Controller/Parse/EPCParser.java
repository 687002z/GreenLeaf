package Controller.Parse;

import Controller.ComplexCalculate;
import Controller.Parse.Node.Arc;
import Controller.Parse.Node.ComplexNode;
import Controller.Parse.Node.Event;
import Controller.Parse.Node.Rule;
import Controller.PubSubNode;
import Controller.Push.NotificationProcessImpl;
import javafx.scene.control.*;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import javax.xml.ws.Endpoint;
import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by SunnyD on 2016/11/3.
 */
public class EPCParser {
    private static EPCParser parser = null;
    private SAXReader reader = null;
    private ArrayList<Rule> ruleArr;
    private ArrayList<Arc> arcArr;
    private HashMap<String, Event> events;
    private ArrayList<ComplexNode> cmpNodes;
//    private String WebAdd="http://10.108.164.131:9016/";
//    private String WSNAdd="http://10.108.164.131:9001/wsn-core";
    private Label infoLabel;
    private String servicesName;

    private int parseFileNum=0;

    EPCParser() {
        ruleArr = new ArrayList<>();
        arcArr = new ArrayList<>();
        events = new HashMap<>();
        cmpNodes = new ArrayList<>();
        reader = new SAXReader();
//        infoLabel= ComplexEventTab.getInfoLabelInstance();

//        InetAddress addr = null;
//        try {
//            addr = InetAddress.getLocalHost();
//        } catch (UnknownHostException e) {
//            e.printStackTrace();
//        }
//        String ip=addr.getHostAddress().toString();//获得本机IP
}

    public static EPCParser getEPCParserInstance(){
        if(parser==null){
            parser = new EPCParser();
        }
        return parser;
    }
    public void read(String filepath){
        File file=new File(filepath);
        read(file);
    }

    public void read(File file) {//读取EPC文档加载相应信息
        try {
            infoLabel.setText(infoLabel.getText()+"开始加载信息文件\n");
            Document doc = this.reader.read(file);
            Element epcNode = doc.getRootElement().element("epc");
            this.servicesName = epcNode.attributeValue("ServicesName");
            Iterator<Element> it = epcNode.elementIterator();
            while (it.hasNext()) {
                Element ele = it.next();
                if (ele.getName().equals("event")) {
                    Event e = new Event();
                    e.setId(ele.attributeValue("id"));
                    e.setTheme(ele.attributeValue("Theme"));
                    e.setEventName(ele.element("name").getText());
                    events.put(ele.attributeValue("id"), e);
                } else if (ele.getName().equals("and") || ele.getName().equals("or") || ele.getName().equals("xor")) {
                    ruleArr.add(new Rule(ele.attributeValue("id"), ele.getName()));
                } else if (ele.getName().equals("arc")) {
                    Element p = ele.element("flow");
                    arcArr.add(new Arc(ele.attributeValue("id"), p.attributeValue("source"), p.attributeValue("target")));
                }
            }

            parseFileNum++;

            System.out.println("已经解析了"+parseFileNum+"个EPML文件");

            this.generateComplexNode();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    private void generateComplexNode() {//生成复杂节点
        System.out.println("生成复杂节点...");
        infoLabel.setText(infoLabel.getText()+"生成复杂节点...\n");
        for(int i=0;i<ruleArr.size();i++){
            String id=ruleArr.get(i).getId();
            ComplexNode p=new ComplexNode();
            p.setRule(ruleArr.get(i));
            for(int j=0;j<arcArr.size();j++){
                if(arcArr.get(j).getTarget().equals(id)){
                    if(p.getEventIn1()==null){
                        p.setEventIn1(events.get(arcArr.get(j).getSource()));
                    }else if(p.getEventIn2()==null){
                        p.setEventIn2(events.get(arcArr.get(j).getSource()));
                    }
                }else if(arcArr.get(j).getSource().equals(id)){
                    p.setEventOut(events.get(arcArr.get(j).getTarget()));
                }
            }
            if(p.getEventIn1()==null||p.getEventIn2()==null){
                continue;
            }

            infoLabel.setText(infoLabel.getText()+p.checkIntegrity()+"\n");//检查完整性
            cmpNodes.add(p);
        }
    }
    /*
    根据ComplexNode节点进行发布服务和订阅主题的操作。
     */

    public void publishAndSubscribe(){//根据ComplexNode进行订阅操作
        System.out.println("进行发布订阅操作...");
        infoLabel.setText(infoLabel.getText()+"进行发布订阅操作...\n");
        for(int i=0;i<cmpNodes.size();i++){
            if(cmpNodes.get(i).getRule().getType().equals("and")){
                Event e1=cmpNodes.get(i).getEventIn1();
                Event e2=cmpNodes.get(i).getEventIn2();
                String name1=e1.getTheme()+e1.getEventName();
                String addrName1= PubSubNode.getInstance().getWebAdd()+this.servicesName+"/"+name1;
                Endpoint.publish(addrName1,new NotificationProcessImpl(cmpNodes.get(i), ComplexCalculate.getInstance(),e1));//发布服务
                System.out.println("发布服务"+addrName1);
                String res=PubSubNode.getInstance().subcribe(addrName1,e1.getTheme());//订阅e1主题
                System.out.println("订阅"+e1.getTheme()+":"+res);

                String name2=e2.getTheme()+e2.getEventName();
                String addrName2=PubSubNode.getInstance().getWebAdd()+this.servicesName+"/"+name2;
                Endpoint.publish(addrName2,new NotificationProcessImpl(cmpNodes.get(i),ComplexCalculate.getInstance(),e2));//发布服务
                System.out.println("发布服务"+addrName2);
                res=PubSubNode.getInstance().subcribe(addrName2,e2.getTheme());//订阅e2主题
                System.out.println("订阅"+e2.getTheme()+":"+res);
            }
        }
    }

    public static void main(String[] args) {
        EPCParser parser =EPCParser.getEPCParserInstance();
        parser.read("res/epc.xml");
        parser.generateComplexNode();
    }

    public Label getInfoLabel() {
        return infoLabel;
    }

    public void setInfoLabel(Label infoLabel) {
        this.infoLabel = infoLabel;
    }
}
