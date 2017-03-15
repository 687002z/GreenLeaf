package Controller.Parse.Node;

/**
 * Created by SunnyD on 2016/11/3.
 */
public class Rule{
    private String type;
    private String id;
    public Rule(){}
    public Rule(String id,String type){
        this.id=id;
        this.type=type;
    }
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}