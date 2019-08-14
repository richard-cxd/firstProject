package pw.wechatbrother.base.rest;

import org.springframework.stereotype.Component;

import java.util.ArrayList;

/**
 * Created by ciro
 * Date: 2019 04 19
 */

@Component
public class ToolClass {

    public ArrayList<String> stringToList(String serviceAreas){
        ArrayList<String> list0 = new ArrayList<String>();
        String[] a1 = serviceAreas.split(",");
        for (int i=0;i<a1.length;i++){
            list0.add(a1[i]);
        }
        return list0;
    }



}
