package pw.wechatbrother.base.mapper;



import pw.wechatbrother.base.dto.onlinemonitoring.MonitoringObjectDTO;
import pw.wechatbrother.base.dto.onlinemonitoring.OnLineMonitoringDTO;

import javax.annotation.Resource;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Resource
public interface YnMonitoringInitMapper {

    //根据设备整形id查询该设备下所有的计量点信息
    List<LinkedHashMap> selectMonitoringInfoByEquipmentLongId(OnLineMonitoringDTO onLineMonitoringDTO);

    List<LinkedHashMap> selectMonitoringPointType(OnLineMonitoringDTO onLineMonitoringDTO);

    List<LinkedHashMap> initJcd(MonitoringObjectDTO monitoringObjectDTO);

    List<LinkedHashMap> jcdSetLocation(MonitoringObjectDTO monitoringObjectDTO);

    int updateMonitoringToZero(MonitoringObjectDTO monitoringObjectDTO);
    int updateMonitoringMStatus(MonitoringObjectDTO monitoringObjectDTO);

    int updateMonitoringAndAliasId(MonitoringObjectDTO monitoringObjectDTO);

    int updateMonitoringNoAliasId(MonitoringObjectDTO monitoringObjectDTO);

    List<LinkedHashMap> selectSubstationMsgByEnterpriseId(OnLineMonitoringDTO onLineMonitoringDTO);

    String selectYnYjInFoByMonitoringTotalCount(MonitoringObjectDTO monitoringObjectDTO);
    List<LinkedHashMap> selectYnYjInFoByMonitoringObjectDTO(MonitoringObjectDTO monitoringObjectDTO);
    List<LinkedHashMap> selectMonitoringSubstationByObject(MonitoringObjectDTO monitoringObjectDTO);
    List<LinkedHashMap> selectYnYjEquipmentInFoByMonitoringObjectDTO(MonitoringObjectDTO monitoringObjectDTO);

    List<LinkedHashMap> selectCompanyByOnLineMonitoringDTO(OnLineMonitoringDTO onLineMonitoringDTO);

    List<LinkedHashMap> selectActivePowerByMonitoringObjectDTO(MonitoringObjectDTO monitoringObjectDTO);

    List<LinkedHashMap> selectMonitoringProtocol(OnLineMonitoringDTO onLineMonitoringDTO);

    List<LinkedHashMap> selectMonitoringMStatus(OnLineMonitoringDTO onLineMonitoringDTO);

    List<LinkedHashMap> selectMonitoringInfoByType1(OnLineMonitoringDTO onLineMonitoringDTO1);

    List<LinkedHashMap> selectMonitoringTJNFByObject(OnLineMonitoringDTO onLineMonitoringDTO);
    List<LinkedHashMap> selectMonitoringTJYFByObject(OnLineMonitoringDTO onLineMonitoringDTO);

    List<LinkedHashMap> getMonitoringWarningListByObject(OnLineMonitoringDTO onLineMonitoringDTO);
    List<LinkedHashMap> selectWarningConfigByObject(MonitoringObjectDTO monitoringObjectDTO);

    void updateWarningConfig(Map paramsMap);

    List<LinkedHashMap> selectDTUMsgByEterpriseId(MonitoringObjectDTO monitoringObjectDTO);


    int updateYjDTUName(MonitoringObjectDTO monitoringObjectDTO);

    List<LinkedHashMap> selectOperationDateByEnterpriseOrSubstationOrEquipmentOrMonitoring(OnLineMonitoringDTO onLineMonitoringDTO);

    List<LinkedHashMap> selectCountMonitringAndEnterprise(MonitoringObjectDTO monitoringObjectDTO);
    List<LinkedHashMap> selectCountMonitringAndSubstation(MonitoringObjectDTO monitoringObjectDTO);

    List<LinkedHashMap> selectElectricAnalysisByEnterpriseLongId(MonitoringObjectDTO monitoringObjectDTO);
}