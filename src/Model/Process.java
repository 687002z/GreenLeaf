package Model;

import Controller.Parse.EPCParser;
import Model.Tree.INode;

/**
 * Created by SunnyD on 2017/5/22.
 */
public class Process implements INode {
    private int id;
    private String name;
    private int modelId;
    private int status;
    private String userid;
    private String func;
    private EPCParser epc;

    public Process(int id,String name,int modelId,int status,String userid,String func){
        this.id=id;
        this.name=name;
        this.modelId=modelId;
        this.status=status;
        this.userid=userid;
        this.func=func;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getModelId() {
        return modelId;
    }

    public void setModelId(int modelId) {
        this.modelId = modelId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getFunc() {
        return func;
    }

    public void setFunc(String func) {
        this.func = func;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public EPCParser getEpc() {
        return epc;
    }

    public void setEpc(EPCParser epc) {
        this.epc = epc;
    }
}
