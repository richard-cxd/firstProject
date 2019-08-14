package pw.wechatbrother.base.mapper;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Created by YN on 2018/5/8.
 */
@Component
public interface BoNaMapper {
    //表底
    List<Map> addBD(@Param("jcdId")String jcdId,@Param("DataDate")String DataDate,@Param("time")String time,@Param("param")String param);
    //电流
    List<Map> addDL(@Param("jcdId")String jcdId,@Param("DataDate")String DataDate,@Param("time")String time,@Param("param")String param);
    //电压
    List<Map> addDY(@Param("jcdId")String jcdId,@Param("DataDate")String DataDate,@Param("time")String time,@Param("param")String param);
    //频率
    List<Map> addPL(@Param("jcdId")String jcdId,@Param("DataDate")String DataDate,@Param("time")String time,@Param("param")String param);
    //视在
    List<Map> addSZ(@Param("jcdId")String jcdId,@Param("DataDate")String DataDate,@Param("time")String time,@Param("param")String param);
    //无功
    List<Map> addWG(@Param("jcdId")String jcdId,@Param("DataDate")String DataDate,@Param("time")String time,@Param("param")String param);
    //有功
    List<Map> addYG(@Param("jcdId")String jcdId,@Param("DataDate")String DataDate,@Param("time")String time,@Param("param")String param);
    //因数
    List<Map> addYS(@Param("jcdId")String jcdId,@Param("DataDate")String DataDate,@Param("time")String time,@Param("param")String param);
    //电压谐波
    List<Map> addXY(@Param("jcdId")String jcdId,@Param("DataDate")String DataDate,@Param("time")String time,@Param("param")String param);
    //电流谐波
    List<Map> addXL(@Param("jcdId")String jcdId,@Param("DataDate")String DataDate,@Param("time")String time,@Param("param")String param);
    //分时电能
    List<Map> addFS(@Param("jcdId")String jcdId,@Param("DataDate")String DataDate,@Param("time")String time,@Param("param")String param);
    //零序
    List<Map> addNX(@Param("jcdId")String jcdId,@Param("DataDate")String DataDate,@Param("time")String time,@Param("param")String param);





    String selectTB(@Param("jcdId")String jcdId);
    int updateDD(@Param("TB")String TB,@Param("TingCount")String TingCount,@Param("TingDate")String TingDate,@Param("jcdId")String jcdId,@Param("DataDate")String DataDate);
    List<Map> addDN(@Param("jcdId")String jcdId,@Param("DataDate")String DataDate,@Param("time")String time,@Param("param")String param);

    List<Map> addAMAX(@Param("jcdId")String jcdId,@Param("DataDate")String DataDate,@Param("time")String time,@Param("param")String param);
    List<Map> addDLJZ(@Param("jcdId")String jcdId,@Param("DataDate")String DataDate,@Param("time")String time,@Param("param")String param);
    List<Map> addDYJZ(@Param("jcdId")String jcdId,@Param("DataDate")String DataDate,@Param("time")String time,@Param("param")String param);
    List<Map> addRZXB(@Param("jcdId")String jcdId,@Param("DataDate")String DataDate,@Param("time")String time,@Param("param")String param);
    List<Map> addRDX(@Param("jcdId")String jcdId,@Param("DataDate")String DataDate,@Param("time")String time,@Param("param")String param);
    List<Map> addDNM(@Param("jcdId")String jcdId,@Param("DataDate")String DataDate,@Param("time")String time,@Param("param")String param);
    List<Map> addYYGLM(@Param("jcdId")String jcdId,@Param("DataDate")String DataDate,@Param("time")String time,@Param("param")String param);
    List<Map> addDLJZM(@Param("jcdId")String jcdId,@Param("DataDate")String DataDate,@Param("time")String time,@Param("param")String param);
    List<Map> addDYJZM(@Param("jcdId")String jcdId,@Param("DataDate")String DataDate,@Param("time")String time,@Param("param")String param);
    List<Map> addMZXB(@Param("jcdId")String jcdId,@Param("DataDate")String DataDate,@Param("time")String time,@Param("param")String param);
    List<Map> addMDX(@Param("jcdId")String jcdId,@Param("DataDate")String DataDate,@Param("time")String time,@Param("param")String param);

    List<Map> addMonthTD(@Param("jcdId")String jcdId,@Param("DataDate")String DataDate,@Param("time")String time,@Param("param")String param);

    List<Map> addGJ(@Param("jcdId")String jcdId, @Param("DataDate")String dataDate, @Param("level")String level,@Param("type")String type,@Param("msg")String msg);
}
