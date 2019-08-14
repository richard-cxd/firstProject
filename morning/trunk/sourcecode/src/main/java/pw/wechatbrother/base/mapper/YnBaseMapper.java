package pw.wechatbrother.base.mapper;



import pw.wechatbrother.base.domain.UserD;
import pw.wechatbrother.base.dto.onlinemonitoring.MonitoringObjectDTO;
import pw.wechatbrother.base.dto.onlinemonitoring.OnLineMonitoringDTO;

import javax.annotation.Resource;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Resource
public interface YnBaseMapper {

   List<LinkedHashMap> selectSubstationMsgByEnterpriseId(OnLineMonitoringDTO onLineMonitoringDTO);

   List<LinkedHashMap> selectEquipmentMonitoringPointBySubstationId(OnLineMonitoringDTO onLineMonitoringDTO);

   List<LinkedHashMap> selectReportByMonitoringPoint(OnLineMonitoringDTO onLineMonitoringDTO);


   List<String> selectXaxisDataWhenResultIsNull(Map paramMap);

   List<LinkedHashMap> selectWarningByMonitoringPoint(OnLineMonitoringDTO onLineMonitoringDTO);

   List<LinkedHashMap> getCompanyId();
   List<LinkedHashMap> getHNCompanyId();

   // 用到的峰平谷
   List<LinkedHashMap> selectElectricAnalysisByMonitoringPoint(OnLineMonitoringDTO onLineMonitoringDTO);

   List<LinkedHashMap> selectYnYjInFoByMonitoringObjectDTO(MonitoringObjectDTO monitoringObjectDTO);
   List<LinkedHashMap> selectEnterpriseSubstationHaveMonitoringPoint(MonitoringObjectDTO monitoringObjectDTO);
   List<LinkedHashMap> selectAllEnterpriseSubstationHaveMonitoringPoint(MonitoringObjectDTO monitoringObjectDTO);
   List<LinkedHashMap> selectHNEnterpriseSubstationHaveMonitoringPoint(MonitoringObjectDTO monitoringObjectDTO);
    List<LinkedHashMap> selectMZEnterpriseSubstationHaveMonitoringPoint(MonitoringObjectDTO monitoringObjectDTO);

   // 谐波查询
   List<LinkedHashMap> selectHarmonicMonitoringPoint(OnLineMonitoringDTO onLineMonitoringDTO);
   // 不平衡度查询
   List<LinkedHashMap> selectReportByMonitoringPointTwo(OnLineMonitoringDTO onLineMonitoringDTO);
   //零序电流查询
   List<LinkedHashMap> selectZoer(OnLineMonitoringDTO onLineMonitoringDTO);
   // 23：45分的分时电能查询
   List<LinkedHashMap> selectTimeSharing(Map map);

   //查询日用电电度
   List<LinkedHashMap> selectElectricAnalysisByMonitoringPointDay(OnLineMonitoringDTO onLineMonitoringDTO);


   //额定容量
   List<LinkedHashMap> selectElectricAnalysisVolume(OnLineMonitoringDTO onLineMonitoringDTO);


   //查询月用电电度
   List<LinkedHashMap> selectElectricAnalysisByMonitoringPointMonth(OnLineMonitoringDTO onLineMonitoringDTO);

   //查询停电次数、时间
   List<LinkedHashMap> selectDDTingdianCount(OnLineMonitoringDTO onLineMonitoringDTO);
   //查询电能
   List<LinkedHashMap> selectDianneng(OnLineMonitoringDTO onLineMonitoringDTO);
   //查询昨日功率及功率因数极值
   List<LinkedHashMap> selectGLGLYS(OnLineMonitoringDTO onLineMonitoringDTO);
   //查询昨日电流极值
   List<LinkedHashMap> selectDLJZ(OnLineMonitoringDTO onLineMonitoringDTO);
   //查询昨日电压极值
   List<LinkedHashMap> selectDYJZ(OnLineMonitoringDTO onLineMonitoringDTO);
   //查询昨日总谐波
   List<LinkedHashMap> selectRZXB(OnLineMonitoringDTO onLineMonitoringDTO);
   //查询昨日断相
   List<LinkedHashMap> selectRDX(OnLineMonitoringDTO onLineMonitoringDTO);
   //查询昨日的昨日电能
   List<LinkedHashMap> selectDNTWO(OnLineMonitoringDTO onLineMonitoringDTO);
   //---------月
   //查询月电能
   List<LinkedHashMap> selectMDianneng(OnLineMonitoringDTO onLineMonitoringDTO);
   //查询月功率及功率因数极值
   List<LinkedHashMap> selectMGLGLYS(OnLineMonitoringDTO onLineMonitoringDTO);
   //查询月电流极值
   List<LinkedHashMap> selectMDLJZ(OnLineMonitoringDTO onLineMonitoringDTO);
   //查询月电压极值
   List<LinkedHashMap> selectMDYJZ(OnLineMonitoringDTO onLineMonitoringDTO);
   //查询月总谐波
   List<LinkedHashMap> selectMRZXB(OnLineMonitoringDTO onLineMonitoringDTO);
   //查询月断相
   List<LinkedHashMap> selectMRDX(OnLineMonitoringDTO onLineMonitoringDTO);
   //------------------------月曲线
   //查询月电能曲线
   List<LinkedHashMap> selectMDiannengQ(OnLineMonitoringDTO onLineMonitoringDTO);
   //查询月功率因数
   List<LinkedHashMap> selectMGLYSQ(OnLineMonitoringDTO onLineMonitoringDTO);
   //查询月电流极值
   List<LinkedHashMap> selectMDLQ(OnLineMonitoringDTO onLineMonitoringDTO);
   //查询月电压合格率
   List<LinkedHashMap> selectMDYQ(OnLineMonitoringDTO onLineMonitoringDTO);
   //查询月A相谐波
   List<LinkedHashMap> selectMAXBQ(OnLineMonitoringDTO onLineMonitoringDTO);
   //查询月断相
   List<LinkedHashMap> selectMDXQ(OnLineMonitoringDTO onLineMonitoringDTO);

   //查询月停电
   List<LinkedHashMap> selectMTD(OnLineMonitoringDTO onLineMonitoringDTO);



   //视在 有功  无功 功率 分时
   List<LinkedHashMap> selectAggregate(OnLineMonitoringDTO onLineMonitoringDTO);
   List<LinkedHashMap> selectEnterpriseInfoByCompanyIdAndEnterpriseName(MonitoringObjectDTO monitoringObjectDTO);
   List<LinkedHashMap> selectAllEnterpriseInfoByCompanyIdAndEnterpriseName(MonitoringObjectDTO monitoringObjectDTO);
   List<LinkedHashMap> selectHNEnterpriseInfoByCompanyIdAndEnterpriseName(MonitoringObjectDTO monitoringObjectDTO);

   List<LinkedHashMap> selectCustomerEnterpriseInfoByCompanyIdAndEnterpriseName(MonitoringObjectDTO monitoringObjectDTO);

   List<LinkedHashMap> selectWarningByMonitoringPointCount(OnLineMonitoringDTO onLineMonitoringDTO);

   List<LinkedHashMap> selectCompanyByOnLineMonitoringDTO(OnLineMonitoringDTO onLineMonitoringDTO);


   //查所有公司名
   List<LinkedHashMap> selectEnterpriseInfo(String date);

   //月度报告所需数据电费峰平谷等
   List<LinkedHashMap> selectMonthReport(OnLineMonitoringDTO onLineMonitoringDTO);

   //月度报告所需数据   需量
   List<LinkedHashMap> selectMonthReportXL(OnLineMonitoringDTO onLineMonitoringDTO);

   //月度报告所需数据   告警
   List<LinkedHashMap> selectMonthReportGJ(OnLineMonitoringDTO onLineMonitoringDTO);


   //月度报告所需数据   功率因数
   List<LinkedHashMap> selectMonthReportGLYS(OnLineMonitoringDTO onLineMonitoringDTO);



   //查公司名下所有监测点
   List<LinkedHashMap> selectEnterpriseInfoJCD();

   List<Map> selectInfoCompany(Map maps);
   List<Map> selectAllInfoCompany(Map maps);
   List<Map> selectHNInfoCompany(Map maps);

   //查询用电企业的报警数据
   List<LinkedHashMap> selectIntelligentAlarmMapper(OnLineMonitoringDTO onLineMonitoringDTO);
   //查询维保公司下的报警数据
   List<LinkedHashMap> selectIntelligentAlarmMapperTwo(OnLineMonitoringDTO onLineMonitoringDTO);

   //查询终端异常状态
   List<LinkedHashMap> selectTerminAlbnormalMapper(OnLineMonitoringDTO onLineMonitoringDTO);
   //查询终端正常状态
   List<LinkedHashMap> selectTerminNormalMapper(OnLineMonitoringDTO onLineMonitoringDTO);
   //根据省查询终端

   //根据省市区查询终端状态个数  正常
   List<LinkedHashMap> selectTerminalCityNumberMapperOne(OnLineMonitoringDTO onLineMonitoringDTO);
   //根据省市区查询终端状态个数  异常
   List<LinkedHashMap> selectTerminalCityNumberMapperTwo(OnLineMonitoringDTO onLineMonitoringDTO);
   //根据省市区查询终端状信息
   List<LinkedHashMap> selectTerminalDataCityMapper(OnLineMonitoringDTO onLineMonitoringDTO);
   //查询报警个数
   Map selectalarmNumber();

   //电房状态
   List<LinkedHashMap> selectDFState(OnLineMonitoringDTO onLineMonitoringDTO);


}