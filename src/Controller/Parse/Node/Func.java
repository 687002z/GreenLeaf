package Controller.Parse.Node;

/**
 * Created by SunnyD on 2017/6/9.
 */
public class Func {
    private String id;
    private String funcName;
    private String taskModelId;

    public Func(String id,String funcName,String taskModelId){
        this.id=id;
        this.funcName=funcName;
        this.taskModelId=taskModelId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFuncName() {
        return funcName;
    }

    public void setFuncName(String funcName) {
        this.funcName = funcName;
    }

    public String getTaskModelId() {
        return taskModelId;
    }

    public void setTaskModelId(String taskModelId) {
        this.taskModelId = taskModelId;
    }
}

