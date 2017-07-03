package Model.Node.EPCNode;

/**
 * Created by SunnyD on 2017/6/9.
 */
public class Func {
    private String id;
    private String funcName;
    private String taskModelId;
    private String funcInfo;
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

    public String getFuncInfo(){
        return "函数ID为:"+id+"\n函数名为:"+funcName+"\n绑定的任务模型ID为:"+taskModelId+"\n运行状态:运行中\n";
    }

}

