package pw.wechatbrother.base.mapper;


import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;
import pw.wechatbrother.base.dto.onlinemonitoring.OnLineMonitoringDTO;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public interface AlarmMapper {

    //查找公司
    List<Map> findcompany(@Param("companyId")int companyId);

    List<Map> findSubstation(@Param("companyId")int companyId);

    //查找监测点
    List<Map> findmonitoringInfo(@Param("substationId")int substationId);

    List<Map> findmessage(@Param("aliasId")String aliasId);
    //更新监测点
    void updateWarningConfig(Map paramsMap);

    List<Map> findcodeid(@Param("aliasId")String aliasId);

    List<Map> findGJdate(@Param("aliasId")String aliasId);

    List<LinkedHashMap> selectAlarmByMonitoringPoint(OnLineMonitoringDTO onLineMonitoringDTO);

    List<LinkedHashMap> selectAlarmByMonitoringPointCount(OnLineMonitoringDTO onLineMonitoringDTO);

    List<Map> selectGJCOUNT(@Param("aliasId")String aliasId);

}

