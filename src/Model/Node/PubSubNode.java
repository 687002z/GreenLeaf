package Model.Node;

import wsn.wsnclient.command.SendWSNCommand;
import wsn.wsnclient.command.SendWSNCommandWSSyn;

/**
 * Created by SunnyD on 2016/11/20.
 */
public class PubSubNode {
    private String WebAdd="http://10.108.164.131:8089/";
    private String WSNAdd="http://10.108.164.131:9001/wsn-core";
    private static PubSubNode instance=null;

    public static PubSubNode getInstance(){
        if(instance==null){
            instance=new PubSubNode();
        }
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

    public SendWSNCommandWSSyn getSend(String addr){
        SendWSNCommandWSSyn send=new SendWSNCommandWSSyn(addr,WSNAdd);
        return send;
    }

    public String getWebAdd() {
        return WebAdd;
    }

    public void setWebAdd(String webAdd) {
        WebAdd = webAdd;
    }

    public String getWSNAdd() {
        return WSNAdd;
    }

    public void setWSNAdd(String WSNAdd) {
        this.WSNAdd = WSNAdd;
    }
}
