package webService;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by YN on 2018/1/18.
 */
public class DataUtil {
    public static void main(String[] args) {
        new DataUtil().JsonData();
    }
    public static String JsonData(){
        //封装所有需要数据的json
        JSONObject data = new JSONObject();

        JSONArray dataList = new JSONArray();
        //
        JSONObject terminalId = new JSONObject();
        terminalId.put("terminalId","100000002");
        //
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
      /*  //监测点信息
        JSONObject Monitor1 = new JSONObject();
        Monitor1.put("getTime",simpleDateFormat.format(new Date()));
        //有功
        Monitor1.put("02",0);
        Monitor1.put("03",0.014);
        Monitor1.put("04",1.275);
        //电流
        Monitor1.put("13",0);
        Monitor1.put("14",0.296);
        Monitor1.put("15",6.116);
        //电压
        Monitor1.put("17",379.36);
        Monitor1.put("18",377.68);
        Monitor1.put("19",379.54);*/

       // dataList.add(Monitor1);
        data.put("companyId","1");
        data.put("dataType","01");
        data.put("dataList",dataList);
        System.out.println(data);
        return data.toString();
    }

}
