package pw.wechatbrother.base.service;


import pw.wechatbrother.base.domain.UserD;
import pw.wechatbrother.base.dto.DetailDTO;
import pw.wechatbrother.base.dto.LoginAppDTO;
import pw.wechatbrother.base.dto.onlinemonitoring.MonitoringObjectDTO;
import pw.wechatbrother.base.dto.onlinemonitoring.OnLineMonitoringDTO;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public interface YnBaseService {


    DetailDTO selectSubstationMsgByEnterpriseId(OnLineMonitoringDTO onLineMonitoringDTO, LoginAppDTO appLoginUser);

    DetailDTO selectEquipmentMonitoringPointBySubstationId(OnLineMonitoringDTO onLineMonitoringDTO, LoginAppDTO appLoginUser);

    DetailDTO selectReportByMonitoringPoint(OnLineMonitoringDTO onLineMonitoringDTO, LoginAppDTO appLoginUser);

    DetailDTO selectWarningByMonitoringPoint(OnLineMonitoringDTO onLineMonitoringDTO, LoginAppDTO appLoginUser);

    //日数据表格
    DetailDTO selectElectricAnalysisByMonitoringPoint(OnLineMonitoringDTO onLineMonitoringDTO, LoginAppDTO appLoginUser);

    //月数据表格
    DetailDTO selectElectricAnalysisByMonitoringPointTWO(OnLineMonitoringDTO onLineMonitoringDTO, LoginAppDTO appLoginUser);

    //月电能
    DetailDTO selectElectricAnalysisByMonitoringPointDN(OnLineMonitoringDTO onLineMonitoringDTO, LoginAppDTO appLoginUser);

    //月功率因数
    DetailDTO selectElectricAnalysisByMonitoringPointGLYS(OnLineMonitoringDTO onLineMonitoringDTO, LoginAppDTO appLoginUser);

    //月电流极值
    DetailDTO selectElectricAnalysisByMonitoringPointDLJZ(OnLineMonitoringDTO onLineMonitoringDTO, LoginAppDTO appLoginUser);

    //月电压极值
    DetailDTO selectElectricAnalysisByMonitoringPointDYJZ(OnLineMonitoringDTO onLineMonitoringDTO, LoginAppDTO appLoginUser);

    //月A相谐波哦
    DetailDTO selectElectricAnalysisByMonitoringPointAXB(OnLineMonitoringDTO onLineMonitoringDTO, LoginAppDTO appLoginUser);

    //月断相
    DetailDTO selectElectricAnalysisByMonitoringPointDX(OnLineMonitoringDTO onLineMonitoringDTO, LoginAppDTO appLoginUser);


    DetailDTO selectEnterpriseSubstationHaveMonitoringPoint(MonitoringObjectDTO monitoringObjectDTO, LoginAppDTO appLoginUser, ArrayList serviceAreas);

    DetailDTO selectEnterpriseInfoByCompanyIdAndEnterpriseName(MonitoringObjectDTO monitoringObjectDTO, LoginAppDTO appLoginUser);
    //月度报告查询
    DetailDTO selectPowerInfoJCD(OnLineMonitoringDTO onLineMonitoringDTO);

    //查所有公司名下所有监测点   集合List
    DetailDTO selectEnterpriseInfoJCD();

    //大屏告警信息的查询
    DetailDTO selectIntelligentAlarmService(OnLineMonitoringDTO onLineMonitoringDTO);
    //大屏终端状态的查询个数
    Map selectTerminalService(OnLineMonitoringDTO onLineMonitoringDTO);

    //大屏终端信息的查询
    List<LinkedHashMap> selectTerminalDataService(OnLineMonitoringDTO onLineMonitoringDTO);

    //大屏根据省市区查询终端信息
    List<LinkedHashMap> selectTerminalDataCityService(OnLineMonitoringDTO onLineMonitoringDTO);

    //大屏根据省市区查询终端个数
    Map selectTerminalCityNumberService(OnLineMonitoringDTO onLineMonitoringDTO);


    //电房状态
    DetailDTO selectDFState(OnLineMonitoringDTO onLineMonitoringDTO);

}
