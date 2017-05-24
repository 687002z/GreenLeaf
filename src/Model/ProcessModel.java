package Model;

/**
 * Created by SunnyD on 2017/5/22.
 */
public class ProcessModel {
    private int id;
    private String name;
    private String data;

     public ProcessModel(int id,String name,String data){
        this.id=id;
        this.name=name;
        this.data=data;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
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


}
