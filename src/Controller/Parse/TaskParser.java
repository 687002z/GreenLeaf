package Controller.Parse;

import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by SunnyD on 2017/9/19.
 */
public class TaskParser {
    public static TaskParser instance = new TaskParser();
    public static TaskParser getInstance(){
        return instance;
    }

    public HashMap<String,ArrayList<String>> parse(String taskData){
        HashMap<String,ArrayList<String>> valueMap = new HashMap<>();
        String[] coms=taskData.split(";");
        for(String com: coms){
            //字段信息保存
            String[] values=com.split(":");
            valueMap.put(values[0],new ArrayList<>());
            for(int i=1;i<values.length;i++){
                valueMap.get(values[0]).add(values[i]);
            }
        }
        return valueMap;
    }

}
