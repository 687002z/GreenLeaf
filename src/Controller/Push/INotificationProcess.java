package Controller.Push;

import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

/**
 * <b>function</b>: WSN调用处理通知消息服务的接口
 * @version 1.0
 *
 */
@WebService(targetNamespace = "http://org.apache.servicemix.wsn.push",name = "INotificationProcess")
public interface INotificationProcess{
	public void notificationProcess(@WebParam(name="notification",targetNamespace = "http://org.apache.servicemix.wsn.push") String notification);
}