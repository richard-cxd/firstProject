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

   // 用到的峰平谷
   List<LinkedHashMap> selectElectricAnalysisByMonitoringPoint(OnLineMonitoringDTO onLineMonitoringDTO);

   List<LinkedHashMap> selectYnYjInFoByMonitoringObjectDTO(MonitoringObjectDTO monitoringObjectDTO);
   List<LinkedHashMap> selectEnterpriseSubstationHaveMonitoringPoint(MonitoringObjectDTO monitoringObjectDTO);
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

   List<LinkedHashMap> selectWarningByMonitoringPointCount(OnLineMonitoringDTO onLineMonitoringDTO);

   List<LinkedHashMap> selectCompanyByOnLineMonitoringDTO(OnLineMonitoringDTO onLineMonitoringDTO);
}