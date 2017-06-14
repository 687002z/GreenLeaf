package Model.Node;

/**
 * Created by SunnyD on 2017/5/22.
 */
public class TaskModel {
    private int id;
    private String name;
    private String form;
    private String type;
    private String domins;
    private String role;
    private String topic;

    public TaskModel(int id,String name,String form,String type,String domins,String role,String topic){
        this.id=id;
        this.name=name;
        this.form=form;
        this.type=type;
        this.domins=domins;
        this.role=role;
        this.topic=topic;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getForm() {
        return form;
    }

    public void setForm(String form) {
        this.form = form;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDomins() {
        return domins;
    }

    public void setDomins(String domins) {
        this.domins = domins;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}
