package Controller;

import Controller.Parse.EPCParser;
import Controller.Parse.Node.ComplexNode;
import Controller.Parse.Node.Event;
import Controller.Push.NotificationProcessImpl;
import com.bupt.sunnyd.SchemaParser;
import org.dom4j.Document;
import wsn.wsnclient.command.SendWSNCommandWSSyn;

import javax.xml.ws.Endpoint;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by SunnyD on 2016/11/17.
 */
public class ComplexEventCal {
    private final static ComplexEventCal instance=new ComplexEventCal();
    private ArrayList<Event> andArry;
    private HashMap<Event,Event> xorRule;

    public ComplexEventCal(){

        andArry = new ArrayList<Event>();
        xorRule = new HashMap<Event,Event>();
    }

    public static ComplexEventCal getInstance(){
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

    /*
    根据ComplexNode节点进行发布服务和订阅主题的操作。
     */

    public void publishAndSubscribe(EPCParser epc){//根据ComplexNode进行订阅操作
        ArrayList<ComplexNode> cmpNodes= epc.getCmpNodes();
        String servicesName=epc.getServicesName();
        System.out.println("进行发布订阅操作...");
        for(int i=0;i<cmpNodes.size();i++){
            if(cmpNodes.get(i).getRule().getType().equals("and")){
                Event e1=cmpNodes.get(i).getEventIn1();
                Event e2=cmpNodes.get(i).getEventIn2();
                String name1=e1.getTheme()+e1.getEventName();
                String addrName1= PubSubNode.getInstance().getWebAdd()+servicesName+"/"+name1;
                Endpoint.publish(addrName1,new NotificationProcessImpl(cmpNodes.get(i), ComplexEventCal.getInstance(),e1));//发布服务
                System.out.println("发布服务"+addrName1);
                String res=PubSubNode.getInstance().subcribe(addrName1,e1.getTheme());//订阅e1主题
                System.out.println("订阅"+e1.getTheme()+":"+res);

                String name2=e2.getTheme()+e2.getEventName();
                String addrName2=PubSubNode.getInstance().getWebAdd()+servicesName+"/"+name2;
                Endpoint.publish(addrName2,new NotificationProcessImpl(cmpNodes.get(i), ComplexEventCal.getInstance(),e2));//发布服务
                System.out.println("发布服务"+addrName2);
                res=PubSubNode.getInstance().subcribe(addrName2,e2.getTheme());//订阅e2主题
                System.out.println("订阅"+e2.getTheme()+":"+res);
            }
        }
    }
}
