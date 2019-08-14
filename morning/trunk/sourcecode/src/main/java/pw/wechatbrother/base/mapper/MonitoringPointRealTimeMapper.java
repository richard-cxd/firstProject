package pw.wechatbrother.base.mapper;

import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by YN on 2018/3/30.
 */
@Component
public interface MonitoringPointRealTimeMapper {
    //调用汤总存储过程获取该维保公司的所有监测点信息
    List<LinkedHashMap> getRealTimeByZoneId(String zoneid);

    List<LinkedHashMap> findMonitoringDetails(String monitoringId);
}
