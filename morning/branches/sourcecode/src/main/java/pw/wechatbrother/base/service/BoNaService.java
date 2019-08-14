package pw.wechatbrother.base.service;

import pw.wechatbrother.base.dto.onlinemonitoring.BigWarningConfigDTO;

import java.util.List;
import java.util.Map;

/**
 * Created by YN on 2018/5/8.
 */
public interface BoNaService {
    //表底
    List<Map> addBD(String jcdId,String DataDate,String time,String param);

    //电流
    List<Map> addDL(String jcdId,String DataDate,String time,String param);
    //电压
    List<Map> addDY(String jcdId,String DataDate,String time,String param);
    //频率
    List<Map> addPL(String jcdId,String DataDate,String time,String param);
    //视在功率
    List<Map> addSZ(String jcdId,String DataDate,String time,String param);
   //无功功率
    List<Map> addWG(String jcdId,String DataDate,String time,String param);
   //有功功率
    List<Map> addYG(String jcdId,String DataDate,String time,String param);
    //功率因数
    List<Map> addYS(String jcdId,String DataDate,String time,String param);
    //电压谐波
    List<Map> addXY(String jcdId,String DataDate,String time,String param);
    //电流谐波
    List<Map> addXL(String jcdId,String DataDate,String time,String param);
    //分时电能
    List<Map> addFS(String jcdId,String DataDate,String time,String param);
    //零序电流极值
    List<Map> addNX(String jcdId,String DataDate,String time,String param);

    //报警信息额度转给博纳
    String sendGJParameter(BigWarningConfigDTO bigWarningConfigDTO);
    //告警
    List<Map> addGJ(String jcdId, String DataDate, String level, String type, String msg);



    //修改DD增加停电次数
    int updateDD(String TingCount,String TingDate,String DataDate,String TB,String jcdId);
    //根据jcdId查表名
    String selectTB(String jcdId);
    //电能
    List<Map> addDN(String jcdId,String DataDate,String time,String param);
    //有功无功功率极值
    List<Map> addAMAX(String jcdId,String DataDate,String time,String param);
    //昨日电流质量
    List<Map> addDLJZ(String jcdId,String DataDate,String time,String param);
    //昨日电压不平衡合格率
    List<Map> addDYJZ(String jcdId,String DataDate,String time,String param);
    //昨日谐波信息
    List<Map> addRZXB(String jcdId,String DataDate,String time,String param);
    //昨日断相监测
    List<Map> addRDX(String jcdId,String DataDate,String time,String param);

    //月停电
    List<Map> addMonthTD(String jcdId,String DataDate,String time,String param);
    //上月电能
    List<Map> addDNM(String jcdId,String DataDate,String time,String param);
    //上月功率及功率因数
    List<Map> addYYGLM(String jcdId,String DataDate,String time,String param);
    //月电流极值
    List<Map> addDLJZM(String jcdId,String DataDate,String time,String param);
    //月电压极值
    List<Map> addDYJZM(String jcdId,String DataDate,String time,String param);
    //月总谐波值
    List<Map> addMzxb(String jcdId,String DataDate,String time,String param);
    //月断相
    List<Map> addMdx(String jcdId,String DataDate,String time,String param);

  }
