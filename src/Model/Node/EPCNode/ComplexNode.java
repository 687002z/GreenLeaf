package Model.Node.EPCNode;

/**
 * Created by SunnyD on 2016/11/3.
 */
public class ComplexNode {
//    private ArrayList<Event> inEvents=new ArrayList<>(2);
    private Event eventIn1=null;
    private Event eventIn2=null;
    private Rule rule=null;
    private Event eventOut=null;
    public ComplexNode(){

    }

    public Event getEventIn1() {
        return eventIn1;
    }

    public void setEventIn1(Event eventIn1) {
        this.eventIn1 = eventIn1;
    }

    public Event getEventIn2() {
        return eventIn2;
    }

    public void setEventIn2(Event eventIn2) {
        this.eventIn2 = eventIn2;
    }

    public Rule getRule() {
        return rule;
    }

    public void setRule(Rule rule) {
        this.rule = rule;
    }

    public Event getEventOut() {
        return eventOut;
    }

    public void setEventOut(Event eventOut) {
        this.eventOut = eventOut;
    }
    public String checkIntegrity(){//检查完整性，输出信息
        String res="";
        res+="eventIn1为:"+((eventIn1==null)?"null":eventIn1.getEventName())+"\n";
        res+="eventIn2为:"+((eventIn2==null)?"null":eventIn2.getEventName())+"\n";
        res+="eventOut为:"+((eventOut==null)?"null":eventOut.getEventName())+"\n";
        res+="rule为:"+((rule==null)?"null":rule.getType());
        return res;
    }
}
