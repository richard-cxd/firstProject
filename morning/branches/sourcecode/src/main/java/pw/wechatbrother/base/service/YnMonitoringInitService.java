package pw.wechatbrother.base.service;


import pw.wechatbrother.base.dto.DetailDTO;
import pw.wechatbrother.base.dto.LoginAppDTO;
import pw.wechatbrother.base.dto.onlinemonitoring.BigWarningConfigDTO;
import pw.wechatbrother.base.dto.onlinemonitoring.MonitoringObjectDTO;
import pw.wechatbrother.base.dto.onlinemonitoring.OnLineMonitoringDTO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;


public interface YnMonitoringInitService {
    //根据设备整形id查询该设备下所有的计量点信息
    DetailDTO selectMonitoringInfoByEquipmentLongId(OnLineMonitoringDTO onLineMonitoringDTO, LoginAppDTO appLoginUser);

    DetailDTO selectMonitoringPointType(OnLineMonitoringDTO onLineMonitoringDTO, LoginAppDTO appLoginUser);

    DetailDTO addMonitoring(MonitoringObjectDTO monitoringObjectDTO, LoginAppDTO appLoginUser);

    DetailDTO deleteMonitoring(MonitoringObjectDTO monitoringObjectDTO, LoginAppDTO appLoginUser);
    DetailDTO updateMonitoringMStatus(MonitoringObjectDTO monitoringObjectDTO, LoginAppDTO appLoginUser);

    DetailDTO updateMonitoring(MonitoringObjectDTO monitoringObjectDTO, LoginAppDTO appLoginUser);

    DetailDTO selectSubstationMsgByEnterpriseId(OnLineMonitoringDTO onLineMonitoringDTO, LoginAppDTO appLoginUser);

    DetailDTO selectActivePowerByEnterpriseLongId(OnLineMonitoringDTO onLineMonitoringDTO, LoginAppDTO appLoginUser);

    DetailDTO selectMonitoringProtocol(OnLineMonitoringDTO onLineMonitoringDTO, LoginAppDTO appLoginUser);

    DetailDTO selectMonitoringMStatus(OnLineMonitoringDTO onLineMonitoringDTO, LoginAppDTO appLoginUser);

    DetailDTO selectOperationReportByEnterpriseSubstationLongId(HttpServletRequest request,HttpServletResponse response,OnLineMonitoringDTO onLineMonitoringDTO, LoginAppDTO appLoginUser) throws ParseException;

    DetailDTO selectWarningConfigByObject(MonitoringObjectDTO monitoringObjectDTO, LoginAppDTO appLoginUser);

    DetailDTO updateWarningConfig(BigWarningConfigDTO bigWwarningConfigDTO, LoginAppDTO appLoginUser);

    DetailDTO selectDTUMsgByEterpriseId(MonitoringObjectDTO monitoringObjectDTO, LoginAppDTO appLoginUser);

    DetailDTO selectOperationDateByEnterpriseOrSubstationOrEquipmentOrMonitoring(HttpServletRequest request, HttpServletResponse response, OnLineMonitoringDTO onLineMonitoringDTO, LoginAppDTO appLoginUser);

    DetailDTO selectCountMonitringAndEnterprise(MonitoringObjectDTO monitoringObjectDTO, LoginAppDTO appLoginUser);

    DetailDTO selectCountMonitringAndSubstation(MonitoringObjectDTO monitoringObjectDTO, LoginAppDTO appLoginUser);

    DetailDTO selectElectricAnalysisByEnterpriseLongId(MonitoringObjectDTO monitoringObjectDTO, LoginAppDTO appLoginUser);
}
