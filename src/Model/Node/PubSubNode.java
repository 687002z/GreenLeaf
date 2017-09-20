package Model.Node;

import wsn.wsnclient.command.SendWSNCommand;
import wsn.wsnclient.command.SendWSNCommandWSSyn;

/**
 * Created by SunnyD on 2017/9/12.
 */
public class PubSubNode {
    private String preWebAdd="http://10.108.167.253:8998/axis2/services/";
    private String WSNAdd="http://10.108.164.213:9000/wsn-core";
    private static PubSubNode instance = new PubSubNode();

    public static PubSubNode getInstance(){
        return instance;
    }

    public String subcribe(String addr,String topic){
        SendWSNCommand send=new SendWSNCommand(addr,WSNAdd);
        String res="";
        try {
            res=send.subscribe(topic);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * 向发布订阅系统发布消息
     * @param topic 主题
     * @param message 发布的消息
     */
    public int postMessage(String topic,String message){
        System.out.println(topic);
        return this.getSend("").reliableNotify(topic,message,false,"A");

    }

    public SendWSNCommandWSSyn getSend(String addr){
        SendWSNCommandWSSyn send=new SendWSNCommandWSSyn(addr,WSNAdd);
        return send;
    }

    public String getpreWebAdd() {
        return preWebAdd;
    }

    public String getWSNAdd() {
        return WSNAdd;
    }

}
