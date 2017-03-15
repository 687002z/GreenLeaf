package Controller;

import Controller.Parse.Node.ComplexNode;
import Controller.Parse.Node.Event;
import Controller.Parse.Node.Rule;
import Controller.Push.NotificationProcessImpl;

import javax.xml.ws.Endpoint;

/**
 * Created by SunnyD on 2016/11/25.
 */
public class TestCalculate {

    public static void main(String[] args){
        ComplexNode cmpNode=new ComplexNode();
        Event e1=new Event();
        e1.setEventName("event1");
        e1.setMessage("你好");
        e1.setTheme("fire");
        Event e2=new Event();
        e2.setEventName("event2");
        e2.setMessage("陈雨彤");
        e2.setTheme("water");

        Event out = new Event();

        Rule rule= new Rule();
        rule.setType("and");

        cmpNode.setEventIn1(e1);
        cmpNode.setEventIn1(e2);
        cmpNode.setEventOut(out);
        cmpNode.setRule(rule);

        String addrName1= PubSubNode.getInstance().getWebAdd()+"test1";
        System.out.println(addrName1);
        Endpoint.publish(addrName1,new NotificationProcessImpl(cmpNode,ComplexCalculate.getInstance(),e1));//发布服务

        String addrName2= PubSubNode.getInstance().getWebAdd()+"test2";
        System.out.println(addrName2);
        Endpoint.publish(addrName2,new NotificationProcessImpl(cmpNode,ComplexCalculate.getInstance(),e1));//发布服务


    }
}
