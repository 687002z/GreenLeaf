package Model.Node;

import Controller.Parse.EPCParser;
import Controller.Parse.SVGModelParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by SunnyD on 2017/5/22.
 */
public class Process implements INode {
    private int id;
    private String name;
    private int modelId;
    private int status;
    private String userid;
    private EPCParser epc;
    private SVGModelParser svg;
    private List<String> finishedFuncList;
    private List<String> finishedEventList;

    public Process(int id,String name,int modelId,int status,String userid,String finishedFunc,String finishedEvent){
        this.id=id;
        this.name=name;
        this.modelId=modelId;
        this.status=status;
        this.userid=userid;

        finishedFuncList= Arrays.asList(finishedFunc.split(","));
        finishedEventList=Arrays.asList(finishedEvent.split(","));
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

    public SVGModelParser getSvg() {
        return svg;
    }

    public void setSvg(SVGModelParser svg) {
        this.svg = svg;
    }

    public List<String> getFinishedFuncList() {
        return finishedFuncList;
    }

    public List<String> getFinishedEventList() {
        return finishedEventList;
    }
}
