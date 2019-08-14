package pw.wechatbrother.base.service.impl;

import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pw.wechatbrother.base.dto.DetailDTO;
import pw.wechatbrother.base.dto.onlinemonitoring.OnLineMonitoringDTO;
import pw.wechatbrother.base.dto.onlinemonitoring.WarningConfigDTO;
import pw.wechatbrother.base.mapper.AlarmMapper;
import pw.wechatbrother.base.mapper.YnBaseMapper;
import pw.wechatbrother.base.service.AlarmService;
import pw.wechatbrother.base.utils.HttpClientUtil;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class AlarmServiceImpl implements AlarmService {

    @Autowired
    AlarmMapper alarmMapper;
    @Resource
    YnBaseMapper ynBaseMapper;


    public List<Map> getcompany(int companyId){
        List<Map> list = alarmMapper.findcompany(companyId);
        return list;

    }


    public List<Map> getSubstation(int companyId){
        List<Map> list = alarmMapper.findSubstation(companyId);
        return list;
    }


    public List<Map> getmonitoringInfo(int substationId){
        List<Map> list = alarmMapper.findmonitoringInfo(substationId);
        return list;
    }

    public List<Map> getmessgae(String aliasId ){
        List<Map> list = alarmMapper.findmessage(aliasId);
        return list;
    }

    public String alarmsetting(String data){
        net.sf.json.JSONArray dataArray = net.sf.json.JSONArray.fromObject(data);



        String warningTypeList=null;//类型(长字符串）
        String warningStatusList=null;//状态1启用，0禁用(长字符串）
        String warningValueList=null;//上限值(长字符串）
        String warningUnitList=null;//单位(长字符串）
        String ratedCurrent=null;//额定电流
        String ratedCurrentBalance=null;//额定电流不平衡度
        String ratedVoltage=null;//额定电压
        String ratedVoltageBalance =null;//额定电压不平衡度
        String ratedCapacity=null;//额定容量
        String ratedTemperature=null;//额定温度
        String ratedHumidity=null;//额定湿度
        Map map = new HashMap();
        for (int i=1;i<dataArray.size();i++){
            map = (Map) dataArray.get(i);
            System.out.println(map.get("warningType"));

            if(map.get("warningType")=="DY"||map.get("warningType").equals("DY")){
                ratedVoltage= (String) map.get("warninRate");
            }else if(map.get("warningType")=="DL"||map.get("warningType").equals("DL")){
                ratedCurrent=(String) map.get("warninRate");
            }else if(map.get("warningType")=="GL"||map.get("warningType").equals("GL")){
                ratedCapacity=(String) map.get("warninRate");
            }else if(map.get("warningType")=="WD"||map.get("warningType").equals("WD")){
                ratedTemperature=(String) map.get("warninRate");
            }else if(map.get("warningType")=="SD"||map.get("warningType").equals("SD")){
                ratedHumidity=(String) map.get("warninRate");
            }else if(map.get("warningType")=="YH"||map.get("warningType").equals("YH")){
                ratedVoltageBalance=(String) map.get("warninRate");
            }else if(map.get("warningType")=="LH"||map.get("warningType").equals("LH")){
                ratedCurrentBalance=(String) map.get("warninRate");
            }

            if(warningTypeList==null||warningTypeList.equals("")){
                warningTypeList=(String) map.get("warningType");
            }else{
                warningTypeList+=","+(String) map.get("warningType");
            }
            if(warningStatusList==null||warningStatusList.equals("")){
                warningStatusList= String.valueOf(map.get("warningStatus"));
            }else{
                warningStatusList+=","+String.valueOf(map.get("warningStatus"));
            }
            if(warningValueList==null||warningValueList.equals("")){
                warningValueList=(String) map.get("warninValue");
            }else{
                warningValueList+=","+(String) map.get("warninValue");
            }
            if(warningUnitList==null||warningUnitList.equals("")){
                warningUnitList=(String) map.get("warningUnit");
            }else{
                warningUnitList+=","+(String) map.get("warningUnit");
            }
        }

        Map paramsMap= new HashMap();
        paramsMap.put("warningTypeList",warningTypeList);
        paramsMap.put("warningStatusList",warningStatusList);
        paramsMap.put("warningValueList",warningValueList);
        paramsMap.put("warningUnitList",warningUnitList);
        paramsMap.put("monitoringPointAliasId",(String)map.get("aliasId")) ;
        paramsMap.put("ratedCurrent",ratedCurrent);
        paramsMap.put("ratedVoltage",ratedVoltage);
        paramsMap.put("ratedCapacity",ratedCapacity);
        paramsMap.put("ratedTemperature",ratedTemperature);
        paramsMap.put("ratedHumidity",ratedHumidity);
        paramsMap.put("ratedVoltageBalance",ratedVoltageBalance);
        paramsMap.put("ratedCurrentBalance",ratedCurrentBalance);

        try {
            alarmMapper.updateWarningConfig(paramsMap);
//            String sendal = sendAlarmParameter(data);
            return "success";
        }catch (Exception e){
            return e.toString();
        }

    }


    public String sendAlarmParameter(String data){
        net.sf.json.JSONArray dataArray = net.sf.json.JSONArray.fromObject(data);
        JSONObject jsonObject = new JSONObject();
        JSONObject dy = new JSONObject();
        JSONObject dl = new JSONObject();
        JSONObject gl = new JSONObject();
        JSONObject qx = new JSONObject();
        JSONObject ys = new JSONObject();

        Map map = new HashMap();
        for (int i=1;i<dataArray.size();i++){
            map = (Map) dataArray.get(i);
            if(map.get("warningType")=="DY"||map.get("warningType").equals("DY")){
                dy.put("ison",String.valueOf(map.get("warningStatus")));
                dy.put("normal",(String) map.get("warninRate"));
                dy.put("up",(String) map.get("warninValue"));
            }else if(map.get("warningType")=="DL"||map.get("warningType").equals("DL")){
                dl.put("ison",String.valueOf(map.get("warningStatus")));
                dl.put("normal",(String) map.get("warninRate"));
                dl.put("up",(String) map.get("warninValue"));
            }else if(map.get("warningType")=="GL"||map.get("warningType").equals("GL")){
                gl.put("ison",String.valueOf(map.get("warningStatus")));
                gl.put("normal",(String) map.get("warninRate"));
                gl.put("up",(String) map.get("warninValue"));
            }else if(map.get("warningType")=="QX"||map.get("warningType").equals("QX")){
                qx.put("ison",String.valueOf(map.get("warningStatus")));
            }else if(map.get("warningType")=="YS"||map.get("warningType").equals("YS")){
                ys.put("ison",String.valueOf(map.get("warningStatus")));
            }
        }
        List<Map> ids = alarmMapper.findcodeid((String)map.get("aliasId"));
        Map m2 = ids.get(0);
        System.out.println(ids.get(0));
        jsonObject.put("id",m2.get("Coreid"));
        jsonObject.put("dy",dy);
        jsonObject.put("dl",dl);
        jsonObject.put("gl",gl);
        jsonObject.put("qx",qx);
        jsonObject.put("ys",ys);
        String outputStr= JSONObject.fromObject(jsonObject).toString();
        System.out.println("参数"+outputStr);
        String url = "http://127.0.0.1:23102/transfer/set";
        JSONObject returnJson= HttpClientUtil.httpClinetPostFunction(url, outputStr);
        System.out.println(returnJson.toString());
//        String url2 = "http://127.0.0.1:23102/transfer/set?detail=" + outputStr;
//        JSONObject returnJson2 = HttpClientUtil.httpClinetGetFunction(url2);
//        System.out.println(returnJson2.toString());



        return null;
    }


    public DetailDTO getalarmMessage(OnLineMonitoringDTO onLineMonitoringDTO){

        DetailDTO detailDTO=new DetailDTO(true);
        List<LinkedHashMap> mapList=alarmMapper.selectAlarmByMonitoringPoint(onLineMonitoringDTO);
//        List<LinkedHashMap> mapList=ynBaseMapper.selectWarningByMonitoringPoint(onLineMonitoringDTO);
        if(mapList!=null&&mapList.size()>0){
            //处理Json的null的问题
            for(Map map1:mapList){
                Object warningDealUser=map1.get("warningDealUser");
                Object warningDealDate=map1.get("warningDealDate");
                if(warningDealUser==null){
                    map1.put("warningDealUser","");
                }
                if(warningDealDate==null){
                    map1.put("warningDealDate","");
                }
            }
            detailDTO.setDetail(mapList);
        }else{
            detailDTO.setDetail(new ArrayList<LinkedHashMap>());
        }
//        List<LinkedHashMap> mapListCount=ynBaseMapper.selectWarningByMonitoringPointCount(onLineMonitoringDTO);
//        if(mapListCount!=null&&mapListCount.size()>0){
//            detailDTO.setEnumeration((String) mapListCount.get(0).get("countNumber"));
//        }
        List<Map> list = alarmMapper.findGJdate(onLineMonitoringDTO.getMonitoringPointAliasId());
        List<Map> m = alarmMapper.selectGJCOUNT(onLineMonitoringDTO.getMonitoringPointAliasId());
        if (m.size()>0){
            detailDTO.setEnumeration(String.valueOf(m.size()));
        }


        return detailDTO;


    }


    //获取告警日期
    public List<Map> getGJdate(String aliasId){
        List<Map> list = alarmMapper.findGJdate(aliasId);
        Map m = new HashMap();
        System.out.println(list.size());
        m = list.get(list.size()-1);
        Date times = (Date) m.get("datetime");
        System.out.println(times);
        Date nowtime = new Date();
        System.out.println(nowtime);
        long t1 = nowtime.getTime() - times.getTime();
//        SimpleDateFormat sDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        try {
//            Date date =sDateFormat.parse(times);
//        }catch (ParseException e){
//            e.printStackTrace();
//        }
//        Date nowdate = sDateFormat.format(new Date());
        List<Map> list2 = new ArrayList<Map>();
        Map map2 = new HashMap();
        map2.put("times",t1/(1000*60));
        list2.add(map2);
        return list2;
    }




}
