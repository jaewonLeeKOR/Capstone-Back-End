package com.inha.capstone.util;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.List;

public class ApplicationUtil {
    public static String parseApplicationUi(JSONObject ui) throws ParseException {
        List<String> list = ui.keySet().stream().toList();
        StringBuilder javaCode = new StringBuilder();
        String ret = "";
        for(String key : list){
            Object value =  ui.get(key);

            if(value instanceof Number){
                javaCode.append(String.format("double %s = %s;\n",key ,value));
            }
            else{
                javaCode.append(String.format("%s %s = %s;\n",key, key ,value));
            }
        }
        return javaCode.toString();
    }
}
