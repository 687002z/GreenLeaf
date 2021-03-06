package Controller.Push;

import Controller.ComplexEventCal;
import Model.Node.EPCNode.ComplexNode;
import Model.Node.EPCNode.Event;

import javax.jws.WebService;

@WebService(endpointInterface="Controller.Push.INotificationProcess",
		serviceName="INotificationProcess")
public class NotificationProcessImpl implements INotificationProcess{
	private int counter = 0;
	private ComplexNode cmpNode=null;
	private ComplexEventCal calculate=null;
	private Event event;
	
	public NotificationProcessImpl() {
		System.out.println("constructor");
	}
	public NotificationProcessImpl(ComplexNode cmpNode, ComplexEventCal calculate, Event event) {
		// TODO Auto-generated constructor stub
		this.cmpNode=cmpNode;
		this.calculate=calculate;
		this.event=event;
	}
	
	public  void notificationProcess(String notification) {//处理函数
		counter ++;
		System.out.println(counter);
		System.out.println("内容为"+notification);

		event.setMessage(notification);//加载收到的信息

		if(cmpNode.getRule().getType().equals("and")){
			System.out.println("开始计算and逻辑");
			calculate.obtainAndRuleNode(event,cmpNode);
		}else if(cmpNode.getRule().getType().equals("xor")){
			System.out.println("开始计算xor逻辑");
			calculate.obtainXorRuleNode(event,cmpNode);
		}else if(cmpNode.getRule().getType().equals("or")){
			System.out.println("开始计算or逻辑");
			calculate.obtainOrRuleNode(event,cmpNode);
		}


	}
}