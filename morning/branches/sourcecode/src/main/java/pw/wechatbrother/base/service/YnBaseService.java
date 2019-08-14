package pw.wechatbrother.base.service;


import pw.wechatbrother.base.domain.UserD;
import pw.wechatbrother.base.dto.DetailDTO;
import pw.wechatbrother.base.dto.LoginAppDTO;
import pw.wechatbrother.base.dto.onlinemonitoring.MonitoringObjectDTO;
import pw.wechatbrother.base.dto.onlinemonitoring.OnLineMonitoringDTO;

import java.util.List;


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


    DetailDTO selectEnterpriseSubstationHaveMonitoringPoint(MonitoringObjectDTO monitoringObjectDTO, LoginAppDTO appLoginUser);

    DetailDTO selectEnterpriseInfoByCompanyIdAndEnterpriseName(MonitoringObjectDTO monitoringObjectDTO, LoginAppDTO appLoginUser);
}
