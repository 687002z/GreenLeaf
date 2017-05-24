package Model;

import Model.Tree.ITreeNode;

/**
 * Created by SunnyD on 2017/5/22.
 */
public class Task implements ITreeNode{
    public static final int STATUS_RUNNING=1;
    public static final int STATUS_FINISHED=2;
    private int id;
    private int modelId;
    private int processId;
    private String data;
    private String userId;
    private String role;
    private int status;
    private TaskModel taskModel;

    public Task(int id,int modelId,int processId,String data,String userId,String role,int status){
        this.id=id;
        this.modelId=modelId;
        this.processId=processId;
        this.data=data;
        this.userId=userId;
        this.role=role;
        this.status=status;
    }

    public TaskModel getTaskModel() {
        return taskModel;
    }

    public void setTaskModel(TaskModel taskModel) {
        this.taskModel = taskModel;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getModelId() {
        return modelId;
    }

    public void setModelId(int modelId) {
        this.modelId = modelId;
    }

    public int getProcessId() {
        return processId;
    }

    public void setProcessId(int processId) {
        this.processId = processId;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public void setName(String name) {

    }
}
