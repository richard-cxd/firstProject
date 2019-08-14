package pw.wechatbrother.base.service.impl;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pw.wechatbrother.base.dto.onlinemonitoring.BigWarningConfigDTO;
import pw.wechatbrother.base.dto.onlinemonitoring.WarningConfigDTO;
import pw.wechatbrother.base.mapper.BoNaMapper;
import pw.wechatbrother.base.service.BoNaService;
import pw.wechatbrother.base.utils.HttpClientUtil;

import java.sql.SQLOutput;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by YN on 2018/5/8.
 */
@Service
public class BoNaServiceImpl implements BoNaService {
   @Autowired
    private BoNaMapper boNaMapper;
    @Override
    public List<Map> addBD(String jcdId, String DataDate, String time, String param) {
        return boNaMapper.addBD(jcdId,DataDate,time,param);
    }
    //电流
    @Override
    public List<Map> addDL(String jcdId, String DataDate, String time, String param) {
        return boNaMapper.addDL(jcdId, DataDate, time, param);
    }
    //电压谐波
    @Override
    public List<Map> addXY(String jcdId, String DataDate, String time, String param) {
        System.out.println(boNaMapper.addXY(jcdId, DataDate, time, param));
        return boNaMapper.addXY(jcdId, DataDate, time, param);
    }
    //电流谐波
    @Override
    public List<Map> addXL(String jcdId, String DataDate, String time, String param) {
        System.out.println(boNaMapper.addXY(jcdId, DataDate, time, param));
        return boNaMapper.addXL(jcdId, DataDate, time, param);
    }
    //电压
    @Override
    public List<Map> addDY(String jcdId, String DataDate, String time, String param) {
        return boNaMapper.addDY(jcdId, DataDate, time, param);
    }
    //频率
    @Override
    public List<Map> addPL(String jcdId, String DataDate, String time, String param) {
        return boNaMapper.addPL(jcdId, DataDate, time, param);
    }
    //视在功率
    @Override
    public List<Map> addSZ(String jcdId, String DataDate, String time, String param) {
        return boNaMapper.addSZ(jcdId, DataDate, time, param);
    }
    //无功
    @Override
    public List<Map> addWG(String jcdId, String DataDate, String time, String param) {
        return boNaMapper.addWG(jcdId, DataDate, time, param);
    }

    //分时电能
    @Override
    public List<Map> addFS(String jcdId, String DataDate, String time, String param) {
        return boNaMapper.addFS(jcdId, DataDate, time, param);
    }
    //零序电流极值
    @Override
    public List<Map> addNX(String jcdId, String DataDate, String time, String param) {
        return boNaMapper.addNX(jcdId, DataDate, time, param);
    }
    @Override
    public List<Map> addYG(String jcdId, String DataDate, String time, String param) {
        return boNaMapper.addYG(jcdId, DataDate, time, param);
    }

    @Override
    public List<Map> addYS(String jcdId, String DataDate, String time, String param) {
        return boNaMapper.addYS(jcdId, DataDate, time, param);
    }


    /*
    * 日数据 月数据接口
    * */

    @Override
    public String selectTB(String jcdId) {
        return boNaMapper.selectTB(jcdId);
    }

    @Override
    public List<Map> addDN(String jcdId, String DataDate, String time, String param) {

        return boNaMapper.addDN(jcdId,DataDate,time,param);
    }

    @Override
    public List<Map> addAMAX(String jcdId, String DataDate, String time, String param) {
        return boNaMapper.addAMAX(jcdId,DataDate,time,param);
    }

    @Override
    public List<Map> addDLJZ(String jcdId, String DataDate, String time, String param) {
        return boNaMapper.addDLJZ(jcdId,DataDate,time,param);
    }

    @Override
    public List<Map> addDYJZ(String jcdId, String DataDate, String time, String param) {
        return boNaMapper.addDYJZ(jcdId,DataDate,time,param);
    }

    @Override
    public List<Map> addRZXB(String jcdId, String DataDate, String time, String param) {
        return boNaMapper.addRZXB(jcdId,DataDate,time,param);
    }

    @Override
    public List<Map> addRDX(String jcdId, String DataDate, String time, String param) {
        return boNaMapper.addRDX(jcdId,DataDate,time,param);
    }

    @Override
    public List<Map> addMonthTD(String jcdId, String DataDate, String time, String param) {
        return boNaMapper.addMonthTD(jcdId,DataDate,time,param);
    }

    @Override
    public List<Map> addDNM(String jcdId, String DataDate, String time, String param) {
        return boNaMapper.addDNM(jcdId,DataDate,time,param);
    }

    @Override
    public List<Map> addYYGLM(String jcdId, String DataDate, String time, String param) {
        return boNaMapper.addYYGLM(jcdId,DataDate,time,param);
    }

    @Override
    public List<Map> addDLJZM(String jcdId, String DataDate, String time, String param) {
        return boNaMapper.addDLJZM(jcdId,DataDate,time,param);
    }

    @Override
    public List<Map> addDYJZM(String jcdId, String DataDate, String time, String param) {
        return boNaMapper.addDYJZM(jcdId,DataDate,time,param);
    }

    @Override
    public List<Map> addMzxb(String jcdId, String DataDate, String time, String param) {
        return boNaMapper.addMZXB(jcdId,DataDate,time,param);
    }

    @Override
    public List<Map> addMdx(String jcdId, String DataDate, String time, String param) {
        return boNaMapper.addMDX(jcdId,DataDate,time,param);
    }
    @Override
    public int updateDD(String TingCount, String TingDate, String DataDate,String TB,String jcdId) {
        return boNaMapper.updateDD(TB,TingCount,TingDate,jcdId,DataDate);
    }



    @Override
    public String sendGJParameter(BigWarningConfigDTO bigWarningConfigDTO) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id",bigWarningConfigDTO.getMonitoringPointCoreId());
        JSONObject dy = new JSONObject();
        JSONObject dl = new JSONObject();
        JSONObject gl = new JSONObject();
        JSONObject qx = new JSONObject();
        JSONObject ys = new JSONObject();

        for (WarningConfigDTO warningConfigDTO:bigWarningConfigDTO.getWarningConfigDTOList()){
            if(warningConfigDTO.getWarningType()=="DY"||warningConfigDTO.getWarningType().equals("DY")){
                dy.put("ison",warningConfigDTO.getWarningStatus());
                dy.put("normal",warningConfigDTO.getRatedValue());
                dy.put("up",warningConfigDTO.getWarningValue());
            }else if(warningConfigDTO.getWarningType()=="DL"||warningConfigDTO.getWarningType().equals("DL")){
                dl.put("ison",warningConfigDTO.getWarningStatus());
                dl.put("normal",warningConfigDTO.getRatedValue());
                dl.put("up",warningConfigDTO.getWarningValue());
            }else if(warningConfigDTO.getWarningType()=="GL"||warningConfigDTO.getWarningType().equals("GL")){
                gl.put("ison",warningConfigDTO.getWarningStatus());
                gl.put("normal",warningConfigDTO.getRatedValue());
                gl.put("up",warningConfigDTO.getWarningValue());
            }else if(warningConfigDTO.getWarningType()=="QX"||warningConfigDTO.getWarningType().equals("QX")){
                qx.put("ison",warningConfigDTO.getWarningStatus());
            }else if(warningConfigDTO.getWarningType()=="YS"||warningConfigDTO.getWarningType().equals("YS")){
                ys.put("ison",warningConfigDTO.getWarningStatus());
            }
        }
        jsonObject.put("dy",dy);
        jsonObject.put("dl",dl);
        jsonObject.put("gl",gl);
        jsonObject.put("qx",qx);
        jsonObject.put("ys",ys);
        String outputStr= JSONObject.fromObject(jsonObject).toString();
        System.out.println("参数"+outputStr);
        String url = "http://172.18.34.2:23102/transfer/set";
        //String url = "http://127.0.0.1:23102/transfer/set";
        JSONObject returnJson= HttpClientUtil.httpClinetPostFunction(url, outputStr);
        System.out.println(returnJson.toString());
        return returnJson.toString();
    }

    // 告警数据上报
    @Override
    public  Map addGJ(String jcdId, String DataDate, String level, String type, String msg ,String lastdata) {
        String re =null;
        Date currentEndDate = new Date();
        String s =null;
        Date date = new Date();
        String url = "http://www.bonahl.com/web/onLineAlarm/acceptalarm";
        //String url = "http://127.0.0.1:8080/onLineAlarm/acceptalarm";
        //System.out.println("jcdId:"+jcdId+"时间"+DataDate+"类型："+type+"内容："+msg+"最后修改时间"+lastdata);
        boNaMapper.addGJ(jcdId,DataDate,level,type,msg,lastdata);
        Map returnMap = null;
        LinkedHashMap returnMap1 = null;
        boolean b = true;
        Map map = new  HashMap();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            s = sdf.format(new Date());
            currentEndDate = sdf.parse(lastdata);
            date = sdf.parse(s);
            b = DateUtils.isSameDay(currentEndDate,date);

        }catch (Exception a){
            a.printStackTrace();
        }
        if(b){
            map.put("jcdId",jcdId);
            map.put("lastdata",lastdata);
            map.put("msg",msg);
            returnMap  = boNaMapper.selectAlarm(map);
            JSONObject jsonArray = JSONObject.fromObject(returnMap);
            String listalarm = JSONObject.fromObject(jsonArray).toString();
            JSONObject returnJson= HttpClientUtil.httpClinetPostFunction(url,listalarm);
            return returnMap;

        }else{
            String s1="不是当天得数据不上传";
            returnMap.put("返回值",s1);
            return returnMap;

        }
    }

}
