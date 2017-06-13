package Model;

import Model.Tree.ITreeNode;

/**
 * Created by SunnyD on 2017/5/22.
 */
public class ProcessModel implements ITreeNode{
    private int id;
    private String name;
    private String modelData;
    private String graphData;

     public ProcessModel(int id,String name,String modelData,String graphData){
         this.id=id;
         this.name=name;
         this.modelData=modelData;
         this.graphData=graphData;
    }

    public String getModelData() {
        return modelData;
    }

    public void setModelData(String modelData) {
        this.modelData = modelData;
    }

    public String getGraphData() {
        return graphData;
    }

    public void setGraphData(String graphData) {
        this.graphData = graphData;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

}
