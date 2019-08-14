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

    List<LinkedHashMap> findmessage(@Param("aliasId")String aliasId);
    //更新监测点
    void updateWarningConfig(Map paramsMap);

    List<Map> findcodeid(@Param("aliasId")String aliasId);

    List<Map> findGJdate(@Param("aliasId")String aliasId);

    List<LinkedHashMap> selectAlarmByMonitoringPoint(OnLineMonitoringDTO onLineMonitoringDTO);

    List<LinkedHashMap> selectAlarmByMonitoringPointCount(OnLineMonitoringDTO onLineMonitoringDTO);

    List<Map> selectGJCOUNT(@Param("aliasId")String aliasId);
    //查询JCD_CoreID
    List<Map> findCoreId(@Param("idJCD")String idJCD);



    //更新监测点
    void updateWarningConfigAD(Map paramsMap);
    void updateWarningConfigRT(Map paramsMap);
    void updateWarningConfigDYUP(Map paramsMap);
    void updateWarningConfigDYD(Map paramsMap);
    void updateWarningConfigODY(Map paramsMap);
    void updateWarningConfigODYR(Map paramsMap);
    void updateWarningConfigDL(Map paramsMap);
    void updateWarningConfigDLCO(Map paramsMap);
    void updateWarningConfigDLCOR(Map paramsMap);
    void updateWarningConfigDLH(Map paramsMap);
    void updateWarningConfigDLHR(Map paramsMap);
    void updateWarningConfigDYH(Map paramsMap);
    void updateWarningConfigDYHR(Map paramsMap);
    void updateWarningConfigLO(Map paramsMap);
    void updateWarningConfigLOR(Map paramsMap);
    void updateWarningConfigZL(Map paramsMap);
    void updateWarningConfigZLR(Map paramsMap);
    void updateWarningConfigPF(Map paramsMap);
    void updateWarningConfigTC(Map paramsMap);
}

