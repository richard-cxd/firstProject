package pw.wechatbrother.base.service.impl;

import net.sf.json.JSONArray;
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
import java.sql.SQLOutput;
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

    public  List<LinkedHashMap> getmessgae(String aliasId ){
        List<LinkedHashMap> list = alarmMapper.findmessage(aliasId);
        return list;
    }

    public String alarmsetting(String data){
        Map paramsMap= new HashMap();
        net.sf.json.JSONArray dataArray = net.sf.json.JSONArray.fromObject(data);
        JSONArray isonAry = (JSONArray) dataArray.get(0);
        JSONArray valuenAry = (JSONArray) dataArray.get(1);
        String idJCD = dataArray.get(2).toString();

        String ison = "";
        for (int i=0;i<isonAry.size();i++){
            if (i==isonAry.size()-1){
                ison += isonAry.get(i);
            }else {
                ison += isonAry.get(i)+",";
            }

        }
        paramsMap.put("ison",ison);



        String[] keys = {"AD","RT","DYUP","DYD","ODY","ODYR","DL","DLCO","DLCOR","DLH","DLHR","DYH","DYHR",
                "LO","LOR","ZL","ZLR","TC"};

        for (int i=0;i<valuenAry.size();i++){
            paramsMap.put(keys[i],valuenAry.get(i));
        }

        paramsMap.put("idJCD",idJCD);

        String CoreID = "";
        List<Map> list = alarmMapper.findCoreId(idJCD);
        CoreID = ((Map) list.get(0)).get("CoreID").toString();

        ArrayList itemList = new ArrayList();
        ArrayList mqList = new ArrayList();


        for (int i=0;i<isonAry.size();i++){
            Map maps = new HashMap();
            Map mqMaps = new HashMap();
            if (Integer.parseInt(isonAry.get(i).toString())==1){
                maps.put("keys",keys[i]);
                maps.put("values",valuenAry.get(i));
                maps.put("idJCD",idJCD);
                maps.put("allison",ison);
                maps.put("ison",isonAry.get(i));
                itemList.add(maps);
            }
            mqMaps.put("keys",keys[i]);
            mqMaps.put("values",valuenAry.get(i));
            mqMaps.put("idJCD",idJCD);
            mqMaps.put("allison",ison);
            mqMaps.put("ison",isonAry.get(i));
            mqList.add(mqMaps);
        }


        try {
            if (itemList.size()>0){
                for (int i=0;i<itemList.size();i++){
                    Map updataMaps = (Map) itemList.get(i);
                    String wanringName = updataMaps.get("keys").toString();

                    if (wanringName == "AD" || wanringName.equals("AD")){
                        alarmMapper.updateWarningConfigAD(updataMaps);
                        continue;
                    }
                    if (wanringName == "RT" || wanringName.equals("RT")){
                        alarmMapper.updateWarningConfigRT(updataMaps);
                        continue;
                    }
                    if (wanringName == "DYUP" || wanringName.equals("DYUP")){
                        alarmMapper.updateWarningConfigDYUP(updataMaps);
                        continue;
                    }
                    if (wanringName == "DYD" || wanringName.equals("DYD")){
                        alarmMapper.updateWarningConfigDYD(updataMaps);
                        continue;
                    }
                    if (wanringName == "ODY" || wanringName.equals("ODY")){
                        alarmMapper.updateWarningConfigODY(updataMaps);
                        continue;
                    }
                    if (wanringName == "ODYR" || wanringName.equals("ODYR")){
                        alarmMapper.updateWarningConfigODYR(updataMaps);
                        continue;
                    }
                    if (wanringName == "DL" || wanringName.equals("DL")){
                        alarmMapper.updateWarningConfigDL(updataMaps);
                        continue;
                    }
                    if (wanringName == "DLCO" || wanringName.equals("DLCO")){
                        alarmMapper.updateWarningConfigDLCO(updataMaps);
                        continue;
                    }
                    if (wanringName == "DLCOR" || wanringName.equals("DLCOR")){
                        alarmMapper.updateWarningConfigDLCOR(updataMaps);
                        continue;
                    }
                    if (wanringName == "DLH" || wanringName.equals("DLH")){
                        alarmMapper.updateWarningConfigDLH(updataMaps);
                        continue;
                    }
                    if (wanringName == "DLHR" || wanringName.equals("DLHR")){
                        alarmMapper.updateWarningConfigDLHR(updataMaps);
                        continue;
                    }
                    if (wanringName == "DYH" || wanringName.equals("DYH")){
                        alarmMapper.updateWarningConfigDYH(updataMaps);
                        continue;
                    }
                    if (wanringName == "DYHR" || wanringName.equals("DYHR")){
                        alarmMapper.updateWarningConfigDYHR(updataMaps);
                        continue;
                    }
                    if (wanringName == "LO" || wanringName.equals("LO")){
                        alarmMapper.updateWarningConfigLO(updataMaps);
                        continue;
                    }
                    if (wanringName == "LOR" || wanringName.equals("LOR")){
                        alarmMapper.updateWarningConfigLOR(updataMaps);
                        continue;
                    }
                    if (wanringName == "ZL" || wanringName.equals("ZL")){
                        alarmMapper.updateWarningConfigZL(updataMaps);
                        continue;
                    }
                    if (wanringName == "ZLR" || wanringName.equals("ZLR")){
                        alarmMapper.updateWarningConfigZLR(updataMaps);
                        continue;
                    }
                    if (wanringName == "PF" || wanringName.equals("PF")){
                        alarmMapper.updateWarningConfigPF(updataMaps);
                        continue;
                    }
                    if (wanringName == "TC" || wanringName.equals("TC")){
                        alarmMapper.updateWarningConfigTC(updataMaps);
                        continue;
                    }

                }
            }

            String sendal = sendAlarmParameter(mqList,CoreID);
            return sendal;
        }catch (Exception e){
            return e.toString();
        }

    }


    public String sendAlarmParameter(ArrayList itemList,String CoreID){
        JSONObject jsonObject = new JSONObject();


        jsonObject.put("CoreID",CoreID);
        jsonObject.put("dataList",itemList);
        String outputStr= JSONObject.fromObject(jsonObject).toString();
        System.out.println("参数"+outputStr);
        String url = "http://172.18.34.2:23102/transfer/set";
        JSONObject returnJson= HttpClientUtil.httpClinetPostFunction(url, outputStr);
        System.out.println(returnJson.toString());
        if (!returnJson.isEmpty()){
            System.out.println(returnJson.get("msg").toString());
            return returnJson.get("msg").toString();
        }else {
            return null;
        }
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
        List<Map> list2 = new ArrayList<Map>();
        Map map2 = new HashMap();
        map2.put("times",t1/(1000*60));
        list2.add(map2);
        return list2;
    }
   //拉取历史数据
    public String datapullsetting(String data) {
        System.out.println(data);
        JSONObject myjson = JSONObject.fromObject(data);
        String task_id = myjson.getString("datatask");
        String start_time =myjson.getString("startVisitDate");
        String end_time = myjson.getString("endVisitDate");
        String idJCD= myjson.getString("jcdId");
        List<Map> list = alarmMapper.findCoreId(idJCD);
        String jcd_id = "";
        Map map =(Map) list.get(0);
        jcd_id = (String)map.get("CoreID");
        SimpleDateFormat df = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");

        Map pullMap = new HashMap();

        pullMap.put("send_time",df.format(new Date()));
        pullMap.put("jcd_id",jcd_id);
        pullMap.put("task_id",task_id);
        pullMap.put("start_time",start_time);
        pullMap.put("end_time",end_time);
        System.out.println(pullMap);
        JSONObject mqjson = new JSONObject();
        mqjson.put("pullMap",pullMap);
        String outputStr= JSONObject.fromObject(mqjson).toString();
       String url = "http://172.18.34.2:23102/transfer/pullset";
       // String url = "http://127.0.0.1:23102/transfer/pullset";
        JSONObject returnJson= HttpClientUtil.httpClinetPostFunction(url, outputStr);
        System.out.println(returnJson.toString());
        return "下发成功";
    }
}
