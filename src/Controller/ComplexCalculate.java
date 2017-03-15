package Controller;

import Controller.Parse.Node.ComplexNode;
import Controller.Parse.Node.Event;
import com.bupt.sunnyd.SchemaParser;
import org.dom4j.Document;
import wsn.wsnclient.command.SendWSNCommand;
import wsn.wsnclient.command.SendWSNCommandWSSyn;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by SunnyD on 2016/11/17.
 */
public class ComplexCalculate {
    public static ComplexCalculate instance=null;
    private ArrayList<Event> andArry;
    private HashMap<Event,Event> xorRule;

    public ComplexCalculate(){

        andArry = new ArrayList<Event>();
        xorRule = new HashMap<Event,Event>();
    }

    public static ComplexCalculate getInstance(){
        if(instance==null){
            instance = new ComplexCalculate();
        }
        return instance;
    }

    public void obtainAndRuleNode(Event come, ComplexNode cmp){
        System.out.println("received AndRule!");
        Event need=(come==cmp.getEventIn1())?cmp.getEventIn2():cmp.getEventIn1();
        if(andArry.contains(need)){
            //驱动下一个服务
            SchemaParser sp = SchemaParser.getInstance();
            Document from1 = sp.parseSchemaMessage(come.getMessage());
            Document from2 = sp.parseSchemaMessage(need.getMessage());

            String topic = cmp.getEventOut().getTheme();
            Document to = sp.parseSchemaFile("res/schema/"+topic+".xml");

            sp.transform(from1,to);
            sp.transform(from2,to);

            String message=sp.generateXMLStr(to);

            SendWSNCommandWSSyn send=PubSubNode.getInstance().getSend("");
            System.out.println("drieve EventOut!");

            System.out.println("send:"+topic + " "+message);
            send.reliableNotify(topic,message,false,"A");//发送合并的消息

            andArry.remove(need);
        }else{
            if(!andArry.contains(come)){
                andArry.add(come);
            }
        }
    }
    public void obtainOrRuleNode(Event come,ComplexNode cmp){
        System.out.println("received OrRule!");
        String message=come.getMessage();
        SendWSNCommandWSSyn send=PubSubNode.getInstance().getSend("");
        send.reliableNotify(cmp.getEventOut().getTheme(),message,false,"A");
    }

    public void obtainXorRuleNode(Event come,ComplexNode cmp){
        System.out.println("received XorRule!");
        String message=come.getMessage();
        SendWSNCommandWSSyn send=PubSubNode.getInstance().getSend("");
        send.reliableNotify(cmp.getEventOut().getTheme(),message,false,"A");
    }
}
