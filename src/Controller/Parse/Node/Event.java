package Controller.Parse.Node;

/**
 * Created by SunnyD on 2016/11/3.
 */
public class Event{
    private String id;
    private String eventName;
    private String theme;
    private String message;
    public Event(){
    }
    public Event(String id,String eventName,String theme){
        this.id=id;
        this.eventName=eventName;
        this.theme=theme;
        this.message=null;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
