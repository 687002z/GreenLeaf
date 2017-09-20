package Controller.Parse;

import Model.Node.EPCNode.*;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by SunnyD on 2016/11/3.
 */
public class EPCParser {
    private SAXReader reader = null;
    private ArrayList<Rule> ruleArr;
    private ArrayList<Arc> arcArr;
    private HashMap<String, Event> events;
    private ArrayList<ComplexNode> cmpNodes;
    private HashMap<String, Func> funcs;
    private String servicesName;

    private int parseFileNum=0;

    public EPCParser() {
        ruleArr = new ArrayList<>();
        arcArr = new ArrayList<>();
        events = new HashMap<>();
        funcs = new HashMap<>();
        cmpNodes = new ArrayList<>();
        reader = new SAXReader();

}

    public void read(File file){

    }

    public void read(String epcData) {//读取EPC文档加载相应信息
        try {
            Document doc = this.reader.read(new ByteArrayInputStream(epcData.getBytes("UTF-8")));
            Element epcNode = doc.getRootElement().element("epc");
            this.servicesName = epcNode.attributeValue("ServicesName");
            Iterator<Element> it = epcNode.elementIterator();
            while (it.hasNext()) {
                Element ele = it.next();
                if (ele.getName().equals("event")) {
                    Event e = new Event(ele.attributeValue("id"),ele.element("name").getText(),ele.attributeValue("Theme"));
                    events.put(ele.attributeValue("id"), e);
                } else if (ele.getName().equals("and") || ele.getName().equals("or") || ele.getName().equals("xor")) {
                    ruleArr.add(new Rule(ele.attributeValue("id"), ele.getName()));
                } else if (ele.getName().equals("arc")) {
                    Element p = ele.element("flow");
                    arcArr.add(new Arc(ele.attributeValue("id"), p.attributeValue("source"), p.attributeValue("target")));
                } else if(ele.getName().equals("function")){
                    Func f = new Func(ele.attributeValue("id"),ele.element("name").getText(),ele.attributeValue("TaskModelId"));
                    funcs.put(f.getId(),f);
                } else if(ele.getName().equals("iu")){

                }
            }
            parseFileNum++;

            this.generateComplexNode();
        } catch (DocumentException e) {
            e.printStackTrace();
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }
    }

    private void generateComplexNode() {//生成复杂节点
        for(int i=0;i<ruleArr.size();i++){
            String id=ruleArr.get(i).getId();
            ComplexNode p=new ComplexNode();
            p.setRule(ruleArr.get(i));
            for(int j=0;j<arcArr.size();j++){
                if(arcArr.get(j).getTarget().equals(id)){
                    if(p.getEventIn1()==null){
                        p.setEventIn1(events.get(arcArr.get(j).getSource()));
                    }else if(p.getEventIn2()==null){
                        p.setEventIn2(events.get(arcArr.get(j).getSource()));
                    }
                }else if(arcArr.get(j).getSource().equals(id)){
                    p.setEventOut(events.get(arcArr.get(j).getTarget()));
                }
            }
            if(p.getEventIn1()==null||p.getEventIn2()==null){
                continue;
            }

            cmpNodes.add(p);
        }
    }

    public static void main(String[] args) {
    }

    public ArrayList<Rule> getRuleArr() {
        return ruleArr;
    }

    public ArrayList<Arc> getArcArr() {
        return arcArr;
    }

    public HashMap<String, Event> getEvents() {
        return events;
    }

    public ArrayList<ComplexNode> getCmpNodes() {
        return cmpNodes;
    }

    public HashMap<String, Func> getFuncs() {
        return funcs;
    }

    public String getServicesName() {
        return servicesName;
    }

    public Func getFuncByName(String name){
        for(Func f : funcs.values()){
            System.out.println(f.getFuncName());
            if(f.getFuncName().equals(name)){
                return f;
            }
        }
        return null;
    }
    public Func getFuncByID(String id){
        return funcs.get(id);
    }

    public Event getEventByName(String name){
        for(Event e:events.values()){
            if(e.getEventName().equals(name)){
                return e;
            }
        }
        return null;
    }
    public Event getEventByID(String id){
        return events.get(id);
    }
}
