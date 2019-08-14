package pw.wechatbrother.base.service.impl;


import com.fasterxml.jackson.core.JsonProcessingException;
import net.sf.json.JSONObject;
import org.jfree.data.category.DefaultCategoryDataset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pw.wechatbrother.base.dto.DetailDTO;
import pw.wechatbrother.base.dto.LoginAppDTO;
import pw.wechatbrother.base.dto.onlinemonitoring.BigWarningConfigDTO;
import pw.wechatbrother.base.dto.onlinemonitoring.MonitoringObjectDTO;
import pw.wechatbrother.base.dto.onlinemonitoring.OnLineMonitoringDTO;
import pw.wechatbrother.base.dto.onlinemonitoring.WarningConfigDTO;
import pw.wechatbrother.base.mapper.YnMonitoringInitMapper;
import pw.wechatbrother.base.service.BoNaService;
import pw.wechatbrother.base.service.YnMonitoringInitService;
import pw.wechatbrother.base.utils.JavaUUIDGenerator;
import pw.wechatbrother.base.utils.jfreechart.Line;
import pw.wechatbrother.base.utils.safety.Utils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class YnMonitoringInitServiceImpl implements YnMonitoringInitService {
    @Resource
    YnMonitoringInitMapper ynMonitoringInitMapper;
    @Autowired
    BoNaService boNaService;

    //根据设备整形id查询该设备下所有的计量点信息
    public DetailDTO selectMonitoringInfoByEquipmentLongId(OnLineMonitoringDTO onLineMonitoringDTO, LoginAppDTO appLoginUser) {
        DetailDTO detailDTO=new DetailDTO(true);
        List<LinkedHashMap> mapList=ynMonitoringInitMapper.selectMonitoringInfoByEquipmentLongId(onLineMonitoringDTO);
        for(Map map:mapList){
            Iterator entries = map.entrySet().iterator();
            while (entries.hasNext()) {
                Map.Entry entry = (Map.Entry) entries.next();
                String key = (String)entry.getKey();
                Object value = entry.getValue();
                if(value==null||value.equals("")){
                    map.put(key,"");
                }
               // System.out.println("Key = " + key + ", Value = " + value);
            }
        }
        if(mapList!=null&&mapList.size()>0){
            detailDTO.setDetail(mapList);
        }else{
            detailDTO.setDetail(new ArrayList<LinkedHashMap>());
        }
        return detailDTO;
    }
    //根据设备整形id查询该设备下所有的计量点信息
    public DetailDTO selectSubstationMsgByEnterpriseId(OnLineMonitoringDTO onLineMonitoringDTO, LoginAppDTO appLoginUser) {
        DetailDTO detailDTO=new DetailDTO(true);
        List<LinkedHashMap> mapList=ynMonitoringInitMapper.selectSubstationMsgByEnterpriseId(onLineMonitoringDTO);
        if(mapList!=null&&mapList.size()>0){
            detailDTO.setDetail(mapList);
        }else{
            detailDTO.setDetail(new ArrayList<LinkedHashMap>());
        }
        return detailDTO;
    }
    //根据用电企业整形id查询该企业下所有的计量点的有功功率
    public DetailDTO selectActivePowerByEnterpriseLongId(OnLineMonitoringDTO onLineMonitoringDTO, LoginAppDTO appLoginUser) {
        DetailDTO detailDTO=new DetailDTO(true);
       List<LinkedHashMap> listCompany=ynMonitoringInitMapper.selectCompanyByOnLineMonitoringDTO(onLineMonitoringDTO);//根据用电企业id查询当前维保公司id，目的为了拼凑查询有功功率的表名
        if(listCompany!=null&&listCompany.size()>0){
            String ynBaseStaffCompanyId= (String) listCompany.get(0).get("ynBaseStaffCompanyId");//获取到维保公司id
            MonitoringObjectDTO monitoringObjectDTO=new MonitoringObjectDTO();
            monitoringObjectDTO.setYnBaseCustomerEnterpriseLongId(onLineMonitoringDTO.getYnBaseCustomerEnterpriseLongId());
            if(onLineMonitoringDTO.getYnBaseEnterpriseSubstationLongId()!=null&&!onLineMonitoringDTO.getYnBaseEnterpriseSubstationLongId().equals("")){
                monitoringObjectDTO.setYnBaseEnterpriseSubstationLongId(onLineMonitoringDTO.getYnBaseEnterpriseSubstationLongId());
            }
            if(onLineMonitoringDTO.getLimitStart()!=null&&onLineMonitoringDTO.getLimitStart().trim().length()>0){
                monitoringObjectDTO.setLimitStart(Integer.valueOf(onLineMonitoringDTO.getLimitStart()));
            }
            if(onLineMonitoringDTO.getLimitEnd()!=null&&onLineMonitoringDTO.getLimitEnd().trim().length()>0){
                monitoringObjectDTO.setLimitEnd(Integer.valueOf(onLineMonitoringDTO.getLimitEnd()));
            }
            if(onLineMonitoringDTO.getOderCol()!=null&&onLineMonitoringDTO.getOderCol().trim().length()>0){
                monitoringObjectDTO.setOderCol(onLineMonitoringDTO.getOderCol());
            }
            if(onLineMonitoringDTO.getSortDir()!=null&&onLineMonitoringDTO.getSortDir().trim().length()>0){
                monitoringObjectDTO.setSortDir(onLineMonitoringDTO.getSortDir());
            }
            if(onLineMonitoringDTO.getMonitoringPointName()!=null&&onLineMonitoringDTO.getMonitoringPointName().length()>0){
                monitoringObjectDTO.setMonitoringPointName(onLineMonitoringDTO.getMonitoringPointName());
            }

            //根据用电企业查询用电企业的下的所有计量点信息
            List<LinkedHashMap> listMonitoringMsg=ynMonitoringInitMapper.selectYnYjInFoByMonitoringObjectDTO(monitoringObjectDTO);
            String monitoringMsgTotalCount=ynMonitoringInitMapper.selectYnYjInFoByMonitoringTotalCount(monitoringObjectDTO);//liidu-根据用电企业查询用电企业的下的所有计量点信息总数
            detailDTO.setEnumeration(monitoringMsgTotalCount);//liyidu-不想起新字段就用这个字段来装了
            //根据用电企业id查询当前天的总有功情况TO_打头的字段
            monitoringObjectDTO.setTableNameString("jc_yg_"+ynBaseStaffCompanyId);//设置查询有功功率的表名，每个维保公司的都不一样所以需要根据维保公司id去插叙
            List<LinkedHashMap> listMonitoringActivePower=ynMonitoringInitMapper.selectActivePowerByMonitoringObjectDTO(monitoringObjectDTO);
            for(Map mapMonitoringMsg:listMonitoringMsg){//遍历所有的计量点
                Object lastDataTagObejct=mapMonitoringMsg.get("lastDataTag");//获得当前计量点的最后刷新时间
                String lastDataTagObejctStr="TO_"+lastDataTagObejct;//拼成最后需要查询的那个字段的名称
                Object monitoringPointCoreId=mapMonitoringMsg.get("monitoringPointCoreId");//获得当前计量点信息的终端机器码
                for(Map mapMonitoringActivePower:listMonitoringActivePower){//遍历循环该用电企业下的所有计量点的有功功率列表
                    Object monitoringPointCoreIdSon=mapMonitoringActivePower.get("monitoringPointCoreId");//获取到有功的终端机器码id
                    if(monitoringPointCoreIdSon.equals(monitoringPointCoreId)){//判断当前循环的有功功功率列表中的终端机器码是否与前面计量点列表的终端机器码一致，如果一致则把当前的最新的有功功率的值扔扔回计量点列表中
                        Object latelyActivePower=mapMonitoringActivePower.get(lastDataTagObejctStr);
                        if(latelyActivePower==null||latelyActivePower.equals("")){
                            latelyActivePower="0";
                        }
                        mapMonitoringMsg.put("latelyActivePower",latelyActivePower);//设置这个计量点的最新总有功值
                        mapMonitoringMsg.put("ratedCapacity",mapMonitoringActivePower.get("ratedCapacity"));
                        break;//找到了当前计量点的最新的有功功率之后直接跳出循环
                    }
                }
            }
            if(listMonitoringMsg!=null&&listMonitoringMsg.size()>0){
                detailDTO.setDetail(listMonitoringMsg);
            }else{
                detailDTO.setDetail(new ArrayList<Map>());
            }

        } else {
            detailDTO.setStatus(false);
            detailDTO.setErrorCode("-1");
            detailDTO.setErrorMessage("根据用电企业id：" + onLineMonitoringDTO.getYnBaseCustomerEnterpriseLongId() + "查询维保公司信息失败，请查看yj_info表");
            return detailDTO;
        }
        return detailDTO;
    }

    //根据电房id和运行的时间获取运行报告的数据
    public DetailDTO selectOperationReportByEnterpriseSubstationLongId(HttpServletRequest request,HttpServletResponse response,OnLineMonitoringDTO onLineMonitoringDTO, LoginAppDTO appLoginUser) throws ParseException {
        DetailDTO detailDTO=new DetailDTO(true);
        DateFormat format0= new SimpleDateFormat("yyyyMM");
        DateFormat format1= new SimpleDateFormat("yyyyMMdd");
        DateFormat format2= new SimpleDateFormat("yyyy年MM月dd日");
        DateFormat format3= new SimpleDateFormat("HHmm");
        DateFormat format4= new SimpleDateFormat("HH时mm分");
        DateFormat format5= new SimpleDateFormat("yyyyMMddHHmm");
        DateFormat format6= new SimpleDateFormat("yyyy年MM月dd日HH时mm分");
      /*  try {
            System.out.println(format2.format(format1.parse("20171012")));
            System.out.println(format4.format(format3.parse("1501")));
             System.out.println(format6Parse(format5Parse("201710121501")));
        } catch (ParseException e) {
            e.printStackTrace();
        }*/
        String tomcatImgPath = request.getSession().getServletContext().getRealPath("/")+"WEB-INF/static/operationReportImg/";//获取到当前tomcat的图片目录
        Map returnMap = new HashMap();//组装一个要返回回去的大对象
        List<LinkedHashMap> listCompany=ynMonitoringInitMapper.selectCompanyByOnLineMonitoringDTO(onLineMonitoringDTO);//根据用电企业id查询当前维保公司id，目的为了拼凑查询有功功率的表名
        if(listCompany!=null&&listCompany.size()>0){
            String ynBaseStaffCompanyId= (String) listCompany.get(0).get("ynBaseStaffCompanyId");//获取到维保公司id
            MonitoringObjectDTO monitoringObjectDTO=new MonitoringObjectDTO();
            if(onLineMonitoringDTO.getYnBaseEnterpriseSubstationLongId()!=null&&!onLineMonitoringDTO.getYnBaseEnterpriseSubstationLongId().equals("")){
                monitoringObjectDTO.setYnBaseEnterpriseSubstationLongId(onLineMonitoringDTO.getYnBaseEnterpriseSubstationLongId());
            }
            if(onLineMonitoringDTO.getYnBaseCustomerEnterpriseLongId()!=null&&!onLineMonitoringDTO.getYnBaseCustomerEnterpriseLongId().equals("")){
                monitoringObjectDTO.setYnBaseCustomerEnterpriseLongId(onLineMonitoringDTO.getYnBaseCustomerEnterpriseLongId());
            }
            if(onLineMonitoringDTO.getYnBaseEquipmentLongId()!=null&&!onLineMonitoringDTO.getYnBaseEquipmentLongId().equals("")){
                monitoringObjectDTO.setYnBaseEquipmentLongId(onLineMonitoringDTO.getYnBaseEquipmentLongId());
            }
            // monitoringObjectDTO.setYnBaseCustomerEnterpriseLongId(onLineMonitoringDTO.getYnBaseCustomerEnterpriseLongId());
            //根据用电企业查询用电企业的或指定电房下的所有计量点信息
            List<LinkedHashMap> listMonitoringMsg=ynMonitoringInitMapper.selectYnYjEquipmentInFoByMonitoringObjectDTO(monitoringObjectDTO);
            if(listMonitoringMsg==null||listMonitoringMsg.size()==0){
                detailDTO.setStatus(false);
                detailDTO.setErrorCode("-1");
                detailDTO.setErrorMessage("查询所有符合条件计量点为空。入参："+ JSONObject.fromObject(monitoringObjectDTO));
                return detailDTO;
            }
            //returnMap.put("listMonitoringMsg",listMonitoringMsg);//把电房下的所有计量点信息装进列表中
            if(onLineMonitoringDTO.getYear()!=null&&!onLineMonitoringDTO.getYear().equals("")){
                if(onLineMonitoringDTO.getMonth()!=null&&!onLineMonitoringDTO.getMonth().equals("")){
                    if(onLineMonitoringDTO.getDay()!=null&&!onLineMonitoringDTO.getDay().equals("")){//查询每天的表
                        String dateString =onLineMonitoringDTO.getYear()+onLineMonitoringDTO.getMonth()+onLineMonitoringDTO.getDay();
                        //onLineMonitoringDTO.setDateString(dateString);
                        List<String> tableTypes=new ArrayList<String>();
                        tableTypes.add("dl");//电流
                        tableTypes.add("dy");//电压
                        tableTypes.add("yg");//有功功率
                        tableTypes.add("dd");//电度
                        tableTypes.add("gj");//告警
                        /*//因为汤总说目前只有这些数据所以暂时这三种类型，以后有的再从里面加进来 外加告警
                        tableTypes.add("dl");//电流
                        tableTypes.add("dy");//电压
                        tableTypes.add("gj");//告警
                        tableTypes.add("pl");//频率
                        tableTypes.add("sd");//湿度
                        tableTypes.add("sz");//视在功率
                        tableTypes.add("wd");//温度
                        tableTypes.add("wg");//无功功率
                        tableTypes.add("yg");//有功功率
                        tableTypes.add("ys");//功率因数*/
                        List<LinkedHashMap> listMonitoringDD=new ArrayList<LinkedHashMap>();//根据电房查询所有计量点电度的数据
                        List<LinkedHashMap> listMonitoringDL=new ArrayList<LinkedHashMap>();//根据电房查询所有计量点电流的数据
                        List<LinkedHashMap> listMonitoringDY=new ArrayList<LinkedHashMap>();//根据电房查询所有计量点电压的数据
                        List<LinkedHashMap> listMonitoringGJ=new ArrayList<LinkedHashMap>();//根据电房查询所有计量点告警的数据
                        List<LinkedHashMap> listMonitoringPL=new ArrayList<LinkedHashMap>();//根据电房查询所有计量点频率的数据
                        List<LinkedHashMap> listMonitoringSD=new ArrayList<LinkedHashMap>();//根据电房查询所有计量点湿度的数据
                        List<LinkedHashMap> listMonitoringSZ=new ArrayList<LinkedHashMap>();//根据电房查询所有计量点视在功率的数据
                        List<LinkedHashMap> listMonitoringWD=new ArrayList<LinkedHashMap>();//根据电房查询所有计量点温度的数据
                        List<LinkedHashMap> listMonitoringYG=new ArrayList<LinkedHashMap>();//根据电房查询所有计量点有功的数据
                        List<LinkedHashMap> listMonitoringWG=new ArrayList<LinkedHashMap>();//根据电房查询所有计量点无功的数据
                        List<LinkedHashMap> listMonitoringYS=new ArrayList<LinkedHashMap>();//根据电房查询所有计量点功率因数的数据
                        for(String tableType:tableTypes) {//遍历循环所有的计量类型查出所有的计量信息
                            String tableName = "jc_" + tableType + "_" + ynBaseStaffCompanyId;
                            OnLineMonitoringDTO onLineMonitoringDTO1 = new OnLineMonitoringDTO();
                            onLineMonitoringDTO1.setTableNameString(tableName);
                            if (tableType.equals("sz") || tableType.equals("yg") || tableType.equals("wg")) {
                                //onLineMonitoringDTO1.setDivisor("1000");
                                onLineMonitoringDTO1.setDivisor("1");
                            }else{
                                onLineMonitoringDTO1.setDivisor("1");
                            }
                            onLineMonitoringDTO1.setYnBaseCustomerEnterpriseLongId(onLineMonitoringDTO.getYnBaseCustomerEnterpriseLongId());
                            onLineMonitoringDTO1.setYnBaseEnterpriseSubstationLongId(onLineMonitoringDTO.getYnBaseEnterpriseSubstationLongId());
                            onLineMonitoringDTO1.setStartDate(onLineMonitoringDTO.getStartDate());
                            onLineMonitoringDTO1.setEndDate(onLineMonitoringDTO.getEndDate());
                            onLineMonitoringDTO1.setTableType(tableType);
                            onLineMonitoringDTO1.setDateString(dateString);
                            if(tableType.equals("dd")) {
                                listMonitoringDD=ynMonitoringInitMapper.selectMonitoringInfoByType1(onLineMonitoringDTO1);
                            }else if (tableType.equals("dl")) {
                                listMonitoringDL = ynMonitoringInitMapper.selectMonitoringInfoByType1(onLineMonitoringDTO1);
                            }else if (tableType.equals("dy")) {
                                listMonitoringDY= ynMonitoringInitMapper.selectMonitoringInfoByType1(onLineMonitoringDTO1);
                            }else if (tableType.equals("gj")) {
                                listMonitoringGJ=ynMonitoringInitMapper.selectMonitoringInfoByType1(onLineMonitoringDTO1);
                            }else if (tableType.equals("pl")) {
                                listMonitoringPL= ynMonitoringInitMapper.selectMonitoringInfoByType1(onLineMonitoringDTO1);
                            }else if (tableType.equals("sd")) {
                                listMonitoringSD= ynMonitoringInitMapper.selectMonitoringInfoByType1(onLineMonitoringDTO1);
                            }else if (tableType.equals("sz")) {
                                listMonitoringSZ= ynMonitoringInitMapper.selectMonitoringInfoByType1(onLineMonitoringDTO1);
                            }else if (tableType.equals("wd")) {
                                listMonitoringWD= ynMonitoringInitMapper.selectMonitoringInfoByType1(onLineMonitoringDTO1);
                            }else if (tableType.equals("yg")) {
                                listMonitoringYG= ynMonitoringInitMapper.selectMonitoringInfoByType1(onLineMonitoringDTO1);
                            }else if (tableType.equals("wg")) {
                                listMonitoringWG= ynMonitoringInitMapper.selectMonitoringInfoByType1(onLineMonitoringDTO1);
                            }else if (tableType.equals("ys")) {
                                listMonitoringYS = ynMonitoringInitMapper.selectMonitoringInfoByType1(onLineMonitoringDTO1);
                            }
                        }
                        List<Map> substationList=new ArrayList<Map>();//电房列表
                        Map substationMap =new HashMap();//电房对象
                        List<Map> sameMonitoringList=new ArrayList<Map>();//存放相同监测点id的总结
                        Map sameMonitoringObject= new HashMap();//这个大对象里面带着计量点的列表和计量点的共有名称
                        List<Map> warningSubstations=new ArrayList<Map>();//电房的所有计量点告警列表
                        List<Map> monitoringList=new ArrayList<Map>();//存放监测点的list
                        Map monitoringObject=new HashMap();//监测点对象，每个监测点有监控类型名称，运行结果描述，曲线图
                        List<Map> monitoringReportList=new ArrayList<Map>();//监测点报告列表
                        Map monitoringReportObject=new HashMap();//监测点报告对象
                        String oldYnBaseEnterpriseSubstationLongId=null;//前一个电房的id
                        String oldMonitoringPointCoreId = null;//前一个计量点的id
                        String shebeiyunxingneirong=null;//设备运行内容
                        String yunxingxiang=null;//运行项
                        String yunxingjieguo=null;//运行结果
                        Integer orderInt=0;//排序
                        List<Map> monitoringContentList=new ArrayList<Map>();//电房监测点内容列表
                        Integer equimentCount=0;//运行内容设备总数
                        Integer monitoringCount=0;//运行内容监测点总数
                        String oldEquipmentContent=null;//listMonitoringMsg遍历训时候记录设备的前一个id
                        String oldMonitoringContent=null;//listMonitoringMsg遍历训时候记录监测点的前一个id
                        List<LinkedHashMap> allSubstationList=new ArrayList<LinkedHashMap>();
                        allSubstationList=ynMonitoringInitMapper.selectMonitoringSubstationByObject(monitoringObjectDTO);
                        for(Map map1:allSubstationList){//遍历循环企业下的所有的电房计量点信息，包括计量点名称，所属设备，监测点类型
                            Object ynBaseEnterpriseSubstationName=map1.get("ynBaseEnterpriseSubstationName");
                            Object ynBaseEnterpriseSubstationLongId=map1.get("ynBaseEnterpriseSubstationLongId");
                            for(Map mapContent:listMonitoringMsg){//遍历循环企业下的所有的电房计量点信息，包括计量点名称，所属设备，监测点类型
                                Object equipmentContent=mapContent.get("ynBaseEquipmentLongId");//设备id
                                Object monitoringContent=mapContent.get("monitoringPointCoreId");//计量点机器码
                                Object ynBaseEnterpriseSubstationLongIdContent=mapContent.get("ynBaseEnterpriseSubstationLongId");
                                if(ynBaseEnterpriseSubstationLongIdContent.toString().equals(ynBaseEnterpriseSubstationLongId.toString())){
                                    if(oldEquipmentContent==null){
                                        oldEquipmentContent=equipmentContent.toString();
                                        equimentCount++;//统计电房下设备个数
                                    }else if(!oldEquipmentContent.equals(equipmentContent.toString())){
                                        oldEquipmentContent=equipmentContent.toString();
                                        equimentCount++;//统计电房下设备个数
                                    }
                                    if(oldMonitoringContent==null){
                                        oldMonitoringContent=monitoringContent.toString();
                                        monitoringCount++;//统计电房下计量点个数
                                    }else if(!oldMonitoringContent.equals(monitoringContent.toString())){
                                        oldMonitoringContent=monitoringContent.toString();
                                        monitoringCount++;//统计电房下计量点个数
                                    }
                                    monitoringContentList.add(mapContent);
                                }
                            }
                            substationMap.put("monitoringsContent",monitoringContentList);//把获得的当前电房下的监测点内容放回到电房对象中
                            shebeiyunxingneirong="本次设备运行报告时间"+dateString+"，累计对电房("+ynBaseEnterpriseSubstationName+")的"+equimentCount+"个大设备进行运行状态监测，"+monitoringCount+"个监测点，设备运行健康状态详情如下";
                            substationMap.put("shebeiyunxingneirong",shebeiyunxingneirong);//把设备运行内容扔回到电房对象表中
                            substationMap.put("shebeiyunxingneirong",shebeiyunxingneirong);//把设备运行内容扔回到电房对象表中
                            substationMap.put("ynBaseEnterpriseSubstationName",ynBaseEnterpriseSubstationName);//电房名称
                            shebeiyunxingneirong=null;//重置设备运行内容
                            monitoringContentList=new ArrayList<Map>();//重置监测点内容列表
                            monitoringCount=0;//重置电房下计量点个数
                            equimentCount=0;//  重置电房下设备蛇叔
                            //处理电房告警信息
                            for(Map warningMap:listMonitoringGJ){
                                Object warningSubstaionId=warningMap.get("ynBaseEnterpriseSubstationLongId");
                                if(ynBaseEnterpriseSubstationLongId.toString().equals(warningSubstaionId.toString())){
                                    warningSubstations.add(warningMap);
                                }
                            }
                            substationMap.put("warningSubstation",warningSubstations);//把电房下面的所有计量点告警信息列表仍会到电房对象中
                            warningSubstations=new ArrayList<Map>();//电房告警列表初始化
                            //找出该电房下的所有计量点，拿到所有计量点的具体信息
                            for(Map mapMonitoringBySubstaion:listMonitoringMsg){
                                Object ynBaseEnterpriseSubstationLongIdNew = mapMonitoringBySubstaion.get("ynBaseEnterpriseSubstationLongId");
                                ynBaseEnterpriseSubstationLongIdNew=ynBaseEnterpriseSubstationLongIdNew+"";
                                if(ynBaseEnterpriseSubstationLongIdNew.toString().equals(ynBaseEnterpriseSubstationLongId.toString())){//根据当前电房id查询电房下面所有的计量点信息
                                    //拿到这些计量点的信息
                                    String monitoringPointCoreIdNew = (String) mapMonitoringBySubstaion.get("monitoringPointCoreId");
                                    String monitoringPointNameNew = (String) mapMonitoringBySubstaion.get("monitoringPointName");
                                    Integer monitoringPointTypeNew = (Integer) mapMonitoringBySubstaion.get("monitoringPointType");
                                    String monitoringPointTypeStrNew = (String) mapMonitoringBySubstaion.get("monitoringPointTypeStr");
                                    //根据计量点id的信息查询所有的计量信息并组装成各种类型
                                    /*********************************处理计量点对象信息开始**************************************/
                                    monitoringObject.put("orderInt",orderInt);//监测点的排序
                                    sameMonitoringObject.put("monitoringPointName",monitoringPointNameNew);//设置计量点公有名称
                                    monitoringObject.put("monitoringPointName",monitoringPointNameNew);//单相可以直接出计量点名称（最后决定把A相,B,C相合并所以计量点名称就直接使用就好）
                                    for(Map map11:listMonitoringDD){//处理电度
                                        String monitoringPointCoreIdDD = (String) map11.get("monitoringPointCoreId");
                                        if(monitoringPointCoreIdDD.equals(monitoringPointCoreIdNew)){//判断电度计量点id和当前循环的计量点id是否相等
                                            Integer monitoringPointTypeDD       = (Integer) map11.get("monitoringPointType");
                                            Object TO_AVG         = map11.get("TO_AVG")         ;
                                            Object TO_MIN         = map11.get("TO_MIN")         ;
                                            Object TO_MIN_DATA_TAG= map11.get("TO_MIN_DATA_TAG");
                                            Object TO_MIN_TIME_TAG= map11.get("TO_MIN_TIME_TAG");
                                            Object TO_MAX         = map11.get("TO_MAX")         ;
                                            Object TO_MAX_DATA_TAG= map11.get("TO_MAX_DATA_TAG");
                                            Object TO_MAX_TIME_TAG= map11.get("TO_MAX_TIME_TAG");
                                            String Tj_Type_Str="电度";
                                            yunxingjieguo=dateString+"，"+monitoringPointNameNew+Tj_Type_Str+"的平均值为："+TO_AVG+"；最高值为："+TO_MAX+"("+format6Parse(format5Parse(TO_MAX_DATA_TAG+" "+TO_MAX_TIME_TAG))+")"+"；最低值为："+TO_MIN+"("+format6Parse(format5Parse(TO_MIN_DATA_TAG+" "+TO_MIN_TIME_TAG))+")";
                                            yunxingxiang=Tj_Type_Str.toString();
                                            monitoringObject.put("yunxingxiang",yunxingxiang);//运行项
                                            monitoringObject.put("yunxingjieguo",yunxingjieguo);//运行结果
                                            //开始初始化折线图列表数据,遍历当前的最大的map1拿到折线图要展现的数据
                                            Iterator entries1 = map11.entrySet().iterator();
                                            DefaultCategoryDataset mDataset = new DefaultCategoryDataset();
                                            //96个点太多了想折成三张32个点的折线图
                                            /*DefaultCategoryDataset mDatasetOne = new DefaultCategoryDataset();
                                            DefaultCategoryDataset mDatasetTwo = new DefaultCategoryDataset();
                                            DefaultCategoryDataset mDatasetThree = new DefaultCategoryDataset();*/
                                            Integer indexValue=0;//记录放进折线图数据的总数
                                            while (entries1.hasNext()) {
                                                Map.Entry entry1 = (Map.Entry) entries1.next();
                                                String key1 = (String)entry1.getKey();

                                                if(key1.contains("TO_")&&key1.length()==11){//判断获取到自己所需要的折线图数据
                                                    Object value1 = entry1.getValue();
                                                    if(value1==null ||value1.toString().equals("")){
                                                        indexValue++;
                                                        continue;
                                                    }
                                                    String value1String=value1.toString();//先把值设置成字符串类型扔回去
                                                    String toMonth=key1.split("_")[1];//获取到月份的具体数字
                                                   /* monitoringReportObject.put("value",value1String);//Y轴
                                                    monitoringReportObject.put("rowKey",Tj_Type_Str);//图例
                                                    monitoringReportObject.put("columnKey",toMonth);//Y轴
                                                    monitoringReportList.add(monitoringReportObject);
                                                    monitoringReportObject=new HashMap();*/
                                                    //把数据分成三段
                                                    /*if(indexValue<=32){
                                                        mDatasetOne.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                        indexValue++;
                                                    }else if(indexValue>32&&indexValue<=64){
                                                        mDatasetTwo.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                        indexValue++;
                                                    }else if(indexValue>64&&indexValue<=96){
                                                        mDatasetThree.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                        indexValue++;
                                                    }else if(indexValue>96&&indexValue<=128){
                                                        mDatasetOne.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                        indexValue++;
                                                    }else if(indexValue>128&&indexValue<=160){
                                                        mDatasetTwo.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                        indexValue++;
                                                    }else if(indexValue>160&&indexValue<=192){
                                                        mDatasetThree.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                        indexValue++;
                                                    }else if(indexValue>192&&indexValue<=224){
                                                        mDatasetOne.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                        indexValue++;
                                                    }else if(indexValue>224&&indexValue<=256){
                                                        mDatasetTwo.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                        indexValue++;
                                                    }else if(indexValue>256&&indexValue<=288){
                                                        mDatasetThree.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                        indexValue++;
                                                    }*/
                                                    mDataset.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                }
                                                // System.out.println("Key = " + key + ", Value = " + value);
                                            }
                                             /*List<String> imgNames=new ArrayList<String>();
                                            imgNames.add(imgName);*/
                                            List<Map> imgNames=new ArrayList<Map>();
                                            String imgName= JavaUUIDGenerator.getUUID();
                                            String savePath=tomcatImgPath+ imgName+".png";
                                            Line.getDataToFileAndSaveTOThisTomcat(mDataset,savePath,1024,420);
                                            Map imgObject = new HashMap();
                                            imgObject.put("imgName",imgName);
                                            imgObject.put("monitoringPointNameNew",monitoringPointNameNew+"-"+Tj_Type_Str);
                                            imgObject.put("imgFullName",imgName+".png");
                                            imgNames.add(imgObject);
                                            /*折线图分成三张图片保存
                                            for(int iii=1;iii<=3;iii++){
                                                String imgName= JavaUUIDGenerator.getUUID();
                                                String savePath=tomcatImgPath+ imgName+".png";
                                                if(iii==1){
                                                    if(mDatasetOne.getColumnCount()==0){
                                                        continue;
                                                    }
                                                    Line.getDataToFileAndSaveTOThisTomcat(mDatasetOne,savePath,1024,420);
                                                }else if(iii==2){
                                                    if(mDatasetTwo.getColumnCount()==0){
                                                        continue;
                                                    }
                                                    Line.getDataToFileAndSaveTOThisTomcat(mDatasetTwo,savePath,1024,420);
                                                }else if(iii==3){
                                                    if(mDatasetThree.getColumnCount()==0){
                                                        continue;
                                                    }
                                                    Line.getDataToFileAndSaveTOThisTomcat(mDatasetThree,savePath,1024,420);
                                                }
                                                Map imgObject = new HashMap();
                                                imgObject.put("imgName",imgName);
                                                imgObject.put("monitoringPointNameNew",monitoringPointNameNew+"-"+Tj_Type_Str);
                                                imgObject.put("imgFullName",imgName+".png");
                                                imgNames.add(imgObject);
                                            }*/
                                            monitoringObject.put("monitoringReportList",imgNames);//把保存成功之后的图片名称和格式放回到列表中返回去
                                            //monitoringObject.put("monitoringReportList",monitoringReportList);//把当前监测点折线图扔回到监测点对象中
                                            monitoringReportList=new ArrayList<Map>();//重置折线图的列表对象
                                            monitoringList.add(monitoringObject);
                                            monitoringObject=new HashMap();//当前监测点执行完成重新初始化
                                        }
                                    }
                                    //处理电流
                                    for(Map map11:listMonitoringDL){
                                        String monitoringPointCoreIdDL = (String) map11.get("monitoringPointCoreId");
                                        if(monitoringPointCoreIdDL.equals(monitoringPointCoreIdNew)) {//判断电流计量点id和当前循环的计量点id是否相等
                                            Integer monitoringPointTypeDL       = (Integer) map11.get("monitoringPointType");
                                            Object TO_AVG         = map11.get("TO_AVG")         ;
                                            Object TO_MIN         = map11.get("TO_MIN")         ;
                                            Object TO_MIN_DATA_TAG= map11.get("TO_MIN_DATA_TAG");
                                            Object TO_MIN_TIME_TAG= map11.get("TO_MIN_TIME_TAG");
                                            Object TO_MAX         = map11.get("TO_MAX")         ;
                                            Object TO_MAX_DATA_TAG= map11.get("TO_MAX_DATA_TAG");
                                            Object TO_MAX_TIME_TAG= map11.get("TO_MAX_TIME_TAG");
                                            Object AX_AVG         = map11.get("AX_AVG")         ;
                                            Object AX_MIN         = map11.get("AX_MIN")         ;
                                            Object AX_MIN_DATA_TAG= map11.get("AX_MIN_DATA_TAG");
                                            Object AX_MIN_TIME_TAG= map11.get("AX_MIN_TIME_TAG");
                                            Object AX_MAX         = map11.get("AX_MAX")         ;
                                            Object AX_MAX_DATA_TAG= map11.get("AX_MAX_DATA_TAG");
                                            Object AX_MAX_TIME_TAG= map11.get("AX_MAX_TIME_TAG");
                                            Object BX_AVG         = map11.get("BX_AVG")         ;
                                            Object BX_MIN         = map11.get("BX_MIN")         ;
                                            Object BX_MIN_DATA_TAG= map11.get("BX_MIN_DATA_TAG");
                                            Object BX_MIN_TIME_TAG= map11.get("BX_MIN_TIME_TAG");
                                            Object BX_MAX         = map11.get("BX_MAX")         ;
                                            Object BX_MAX_DATA_TAG= map11.get("BX_MAX_DATA_TAG");
                                            Object BX_MAX_TIME_TAG= map11.get("BX_MAX_TIME_TAG");
                                            Object CX_AVG         = map11.get("CX_AVG")         ;
                                            Object CX_MIN         = map11.get("CX_MIN")         ;
                                            Object CX_MIN_DATA_TAG= map11.get("CX_MIN_DATA_TAG");
                                            Object CX_MIN_TIME_TAG= map11.get("CX_MIN_TIME_TAG");
                                            Object CX_MAX         = map11.get("CX_MAX")         ;
                                            Object CX_MAX_DATA_TAG= map11.get("CX_MAX_DATA_TAG");
                                            Object CX_MAX_TIME_TAG= map11.get("CX_MAX_TIME_TAG");
                                            String Tj_Type_Str="电流";
                                            if(monitoringPointTypeDL==1){//表示电流单相
                                                yunxingjieguo=dateString+"，"+monitoringPointNameNew+Tj_Type_Str+"的平均值为："+TO_AVG+"；最高值为："+TO_MAX+"("+format6Parse(format5Parse(TO_MAX_DATA_TAG+" "+TO_MAX_TIME_TAG))+")"+"；最低值为："+TO_MIN+"("+format6Parse(format5Parse(TO_MIN_DATA_TAG+" "+TO_MIN_TIME_TAG))+")";
                                            }else if(monitoringPointTypeDL==3){//便是电流三相
                                                yunxingjieguo=dateString+"，"+monitoringPointNameNew
                                                        +Tj_Type_Str+"A相的平均值为："+AX_AVG+"；最高值为："+AX_MAX+"("+format6Parse(format5Parse(AX_MAX_DATA_TAG+" "+AX_MAX_TIME_TAG))+")"+"；最低值为："+AX_MIN+"("+format6Parse(format5Parse(AX_MIN_DATA_TAG+" "+AX_MIN_TIME_TAG))+")"+"。"
                                                        +Tj_Type_Str+"B相的平均值为："+BX_AVG+"；最高值为："+BX_MAX+"("+format6Parse(format5Parse(BX_MAX_DATA_TAG+" "+BX_MAX_TIME_TAG))+")"+"；最低值为："+BX_MIN+"("+format6Parse(format5Parse(BX_MIN_DATA_TAG+" "+BX_MIN_TIME_TAG))+")"+"。"
                                                        +Tj_Type_Str+"C相的平均值为："+CX_AVG+"；最高值为："+CX_MAX+"("+format6Parse(format5Parse(CX_MAX_DATA_TAG+" "+CX_MAX_TIME_TAG))+")"+"；最低值为："+CX_MIN+"("+format6Parse(format5Parse(CX_MIN_DATA_TAG+" "+CX_MIN_TIME_TAG))+")"+"。";
                                            }
                                            yunxingxiang=Tj_Type_Str.toString();
                                            monitoringObject.put("yunxingxiang",yunxingxiang);//运行项
                                            monitoringObject.put("yunxingjieguo",yunxingjieguo);//运行结果
                                            //开始初始化折线图列表数据,遍历当前的最大的map1拿到折线图要展现的数据
                                            Iterator entries2 = map11.entrySet().iterator();
                                            //因为有96个点所以分成三张图片每张32个点
                                            DefaultCategoryDataset mDataset = new DefaultCategoryDataset();//1到96
                                            //96个点太多了想折成三张32个点的折线图
                                           /* DefaultCategoryDataset mDatasetOne = new DefaultCategoryDataset();
                                            DefaultCategoryDataset mDatasetTwo = new DefaultCategoryDataset();
                                            DefaultCategoryDataset mDatasetThree = new DefaultCategoryDataset();*/
                                            Integer indexValue=0;//记录放进折线图数据的总数
                                            //mDataset.getColumnCount() 获取总数
                                            while (entries2.hasNext()) {
                                                Map.Entry entry2 = (Map.Entry) entries2.next();
                                                String key2 = (String)entry2.getKey();

                                                if(monitoringPointTypeDL==1){
                                                    if(key2.contains("TO_")&&key2.length()==7){//判断获取到自己所需要的折线图数据
                                                        Object value1 = entry2.getValue();
                                                        if(value1==null ||value1.toString().equals("")){
                                                            indexValue++;
                                                            continue;
                                                        }
                                                        String value1String=value1.toString();//先把值设置成字符串类型扔回去
                                                        String toMonth=key2.split("_")[1];//获取到月份的具体数字
                                                        /*if(indexValue<=32){
                                                            mDatasetOne.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                            indexValue++;
                                                        }else if(indexValue>32&&indexValue<=64){
                                                            mDatasetTwo.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                            indexValue++;
                                                        }else if(indexValue>64&&indexValue<=96){
                                                            mDatasetThree.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                            indexValue++;
                                                        }else if(indexValue>96&&indexValue<=128){
                                                            mDatasetOne.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                            indexValue++;
                                                        }else if(indexValue>128&&indexValue<=160){
                                                            mDatasetTwo.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                            indexValue++;
                                                        }else if(indexValue>160&&indexValue<=192){
                                                            mDatasetThree.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                            indexValue++;
                                                        }else if(indexValue>192&&indexValue<=224){
                                                            mDatasetOne.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                            indexValue++;
                                                        }else if(indexValue>224&&indexValue<=256){
                                                            mDatasetTwo.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                            indexValue++;
                                                        }else if(indexValue>256&&indexValue<=288){
                                                            mDatasetThree.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                            indexValue++;
                                                        }*/
                                                        mDataset.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                       /* monitoringReportObject.put("value",value1String);//Y轴
                                                        monitoringReportObject.put("rowKey",Tj_Type_Str);//图例
                                                        monitoringReportObject.put("columnKey",toMonth);//Y轴
                                                        monitoringReportList.add(monitoringReportObject);
                                                        monitoringReportObject=new HashMap();*/
                                                    }
                                                }else if(monitoringPointTypeDL==3){
                                                    if(key2.contains("AX_")||key2.contains("BX_")||key2.contains("CX_") ){//判断获取到自己所需要的折线图数据
                                                        if(key2.length()==7) {//必须在做一个判断否则平均值，最高值，最低值也进来了，因为那也是以TO_,AX_,BX_,CX_打头的
                                                            Object value1 = entry2.getValue();
                                                            if(value1==null ||value1.toString().equals("")){
                                                                indexValue++;
                                                                continue;
                                                            }
                                                            String value1String=value1.toString();//先把值设置成字符串类型扔回去
                                                            String toMonth = key2.split("_")[1];//获取到月份的具体数字
                                                            /*if(indexValue<=32){
                                                                mDatasetOne.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                                indexValue++;
                                                            }else if(indexValue>32&&indexValue<=64){
                                                                mDatasetTwo.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                                indexValue++;
                                                            }else if(indexValue>64&&indexValue<=96){
                                                                mDatasetThree.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                                indexValue++;
                                                            }else if(indexValue>96&&indexValue<=128){
                                                                mDatasetOne.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                                indexValue++;
                                                            }else if(indexValue>128&&indexValue<=160){
                                                                mDatasetTwo.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                                indexValue++;
                                                            }else if(indexValue>160&&indexValue<=192){
                                                                mDatasetThree.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                                indexValue++;
                                                            }else if(indexValue>192&&indexValue<=224){
                                                                mDatasetOne.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                                indexValue++;
                                                            }else if(indexValue>224&&indexValue<=256){
                                                                mDatasetTwo.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                                indexValue++;
                                                            }else if(indexValue>256&&indexValue<=288){
                                                                mDatasetThree.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                                indexValue++;
                                                            }*/
                                                            mDataset.addValue(Float.parseFloat(value1String), Tj_Type_Str+key2.split("_")[0], toMonth);
                                                           /* monitoringReportObject.put("value", value1String);//Y轴
                                                            monitoringReportObject.put("rowKey", Tj_Type_Str + key2.split("_")[0]);//图例
                                                            monitoringReportObject.put("columnKey", toMonth);//Y轴
                                                            monitoringReportList.add(monitoringReportObject);
                                                            monitoringReportObject = new HashMap();*/
                                                        }
                                                    }
                                                }
                                                // System.out.println("Key = " + key + ", Value = " + value);
                                            }
                                            List<Map> imgNames=new ArrayList<Map>();
                                             String imgName= JavaUUIDGenerator.getUUID();
                                            String savePath=tomcatImgPath+ imgName+".png";
                                            Line.getDataToFileAndSaveTOThisTomcat(mDataset,savePath,1024,420);
                                            Map imgObject = new HashMap();
                                            imgObject.put("imgName",imgName);
                                            imgObject.put("monitoringPointNameNew",monitoringPointNameNew+"-"+Tj_Type_Str);
                                            imgObject.put("imgFullName",imgName+".png");
                                            imgNames.add(imgObject);
                                            /*for(int iii=1;iii<=3;iii++){
                                                String imgName= JavaUUIDGenerator.getUUID();
                                                String savePath=tomcatImgPath+ imgName+".png";
                                                if(iii==1){
                                                    if(mDatasetOne.getColumnCount()==0){
                                                        continue;
                                                    }
                                                    Line.getDataToFileAndSaveTOThisTomcat(mDatasetOne,savePath,1024,420);
                                                }else if(iii==2){
                                                    if(mDatasetTwo.getColumnCount()==0){
                                                        continue;
                                                    }
                                                    Line.getDataToFileAndSaveTOThisTomcat(mDatasetTwo,savePath,1024,420);
                                                }else if(iii==3){
                                                    if(mDatasetThree.getColumnCount()==0){
                                                        continue;
                                                    }
                                                    Line.getDataToFileAndSaveTOThisTomcat(mDatasetThree,savePath,1024,420);
                                                }
                                                Map imgObject = new HashMap();
                                                imgObject.put("imgName",imgName);
                                                imgObject.put("monitoringPointNameNew",monitoringPointNameNew+"-"+Tj_Type_Str);
                                                imgObject.put("imgFullName",imgName+".png");
                                                imgNames.add(imgObject);
                                            }*/
                                            monitoringObject.put("monitoringReportList",imgNames);//把保存成功之后的图片名称和格式放回到列表中返回去
                                            //monitoringObject.put("monitoringReportList",monitoringReportList);//把当前监测点折线图扔回到监测点对象中
                                            monitoringReportList=new ArrayList<Map>();//重置折线图的列表对象
                                            monitoringList.add(monitoringObject);
                                            monitoringObject=new HashMap();//当前监测点执行完成重新初始化
                                        }
                                    }

                                    //处理电压
                                    for(Map map11:listMonitoringDY){
                                        String monitoringPointCoreIdDY = (String) map11.get("monitoringPointCoreId");
                                        if(monitoringPointCoreIdDY.equals(monitoringPointCoreIdNew)) {//判断电流计量点id和当前循环的计量点id是否相等
                                            Integer monitoringPointTypeDY       = (Integer) map11.get("monitoringPointType");
                                            Object TO_AVG         = map11.get("TO_AVG")         ;
                                            Object TO_MIN         = map11.get("TO_MIN")         ;
                                            Object TO_MIN_DATA_TAG= map11.get("TO_MIN_DATA_TAG");
                                            Object TO_MIN_TIME_TAG= map11.get("TO_MIN_TIME_TAG");
                                            Object TO_MAX         = map11.get("TO_MAX")         ;
                                            Object TO_MAX_DATA_TAG= map11.get("TO_MAX_DATA_TAG");
                                            Object TO_MAX_TIME_TAG= map11.get("TO_MAX_TIME_TAG");
                                            Object AX_AVG         = map11.get("AX_AVG")         ;
                                            Object AX_MIN         = map11.get("AX_MIN")         ;
                                            Object AX_MIN_DATA_TAG= map11.get("AX_MIN_DATA_TAG");
                                            Object AX_MIN_TIME_TAG= map11.get("AX_MIN_TIME_TAG");
                                            Object AX_MAX         = map11.get("AX_MAX")         ;
                                            Object AX_MAX_DATA_TAG= map11.get("AX_MAX_DATA_TAG");
                                            Object AX_MAX_TIME_TAG= map11.get("AX_MAX_TIME_TAG");
                                            Object BX_AVG         = map11.get("BX_AVG")         ;
                                            Object BX_MIN         = map11.get("BX_MIN")         ;
                                            Object BX_MIN_DATA_TAG= map11.get("BX_MIN_DATA_TAG");
                                            Object BX_MIN_TIME_TAG= map11.get("BX_MIN_TIME_TAG");
                                            Object BX_MAX         = map11.get("BX_MAX")         ;
                                            Object BX_MAX_DATA_TAG= map11.get("BX_MAX_DATA_TAG");
                                            Object BX_MAX_TIME_TAG= map11.get("BX_MAX_TIME_TAG");
                                            Object CX_AVG         = map11.get("CX_AVG")         ;
                                            Object CX_MIN         = map11.get("CX_MIN")         ;
                                            Object CX_MIN_DATA_TAG= map11.get("CX_MIN_DATA_TAG");
                                            Object CX_MIN_TIME_TAG= map11.get("CX_MIN_TIME_TAG");
                                            Object CX_MAX         = map11.get("CX_MAX")         ;
                                            Object CX_MAX_DATA_TAG= map11.get("CX_MAX_DATA_TAG");
                                            Object CX_MAX_TIME_TAG= map11.get("CX_MAX_TIME_TAG");
                                            String Tj_Type_Str="电压";
                                            if(monitoringPointTypeDY==1){//表示单相电压
                                                yunxingjieguo=dateString+"，"+monitoringPointNameNew+Tj_Type_Str+"的平均值为："+TO_AVG+"；最高值为："+TO_MAX+"("+format6Parse(format5Parse(TO_MAX_DATA_TAG+" "+TO_MAX_TIME_TAG))+")"+"；最低值为："+TO_MIN+"("+format6Parse(format5Parse(TO_MIN_DATA_TAG+" "+TO_MIN_TIME_TAG))+")";
                                            }else if(monitoringPointTypeDY==3){//表示三相电压
                                                yunxingjieguo=dateString+"，"+monitoringPointNameNew
                                                        +Tj_Type_Str+"A相的平均值为："+AX_AVG+"；最高值为："+AX_MAX+"("+format6Parse(format5Parse(AX_MAX_DATA_TAG+" "+AX_MAX_TIME_TAG))+")"+"；最低值为："+AX_MIN+"("+format6Parse(format5Parse(AX_MIN_DATA_TAG+" "+AX_MIN_TIME_TAG))+")"+"。"
                                                        +Tj_Type_Str+"B相的平均值为："+BX_AVG+"；最高值为："+BX_MAX+"("+format6Parse(format5Parse(BX_MAX_DATA_TAG+" "+BX_MAX_TIME_TAG))+")"+"；最低值为："+BX_MIN+"("+format6Parse(format5Parse(BX_MIN_DATA_TAG+" "+BX_MIN_TIME_TAG))+")"+"。"
                                                        +Tj_Type_Str+"C相的平均值为："+CX_AVG+"；最高值为："+CX_MAX+"("+format6Parse(format5Parse(CX_MAX_DATA_TAG+" "+CX_MAX_TIME_TAG))+")"+"；最低值为："+CX_MIN+"("+format6Parse(format5Parse(CX_MIN_DATA_TAG+" "+CX_MIN_TIME_TAG))+")"+"。";

                                            }
                                            yunxingxiang=Tj_Type_Str.toString();
                                            monitoringObject.put("yunxingxiang",yunxingxiang);//运行项
                                            monitoringObject.put("yunxingjieguo",yunxingjieguo);//运行结果
                                            //开始初始化折线图列表数据,遍历当前的最大的map1拿到折线图要展现的数据
                                            Iterator entries2 = map11.entrySet().iterator();
                                            DefaultCategoryDataset mDataset = new DefaultCategoryDataset();
                                            //96个点太多了想折成三张32个点的折线图
                                           /* DefaultCategoryDataset mDatasetOne = new DefaultCategoryDataset();
                                            DefaultCategoryDataset mDatasetTwo = new DefaultCategoryDataset();
                                            DefaultCategoryDataset mDatasetThree = new DefaultCategoryDataset();*/
                                            Integer indexValue=0;//记录放进折线图数据的总数
                                            while (entries2.hasNext()) {
                                                Map.Entry entry2 = (Map.Entry) entries2.next();
                                                String key2 = (String)entry2.getKey();

                                                if(monitoringPointTypeDY==1){
                                                    if(key2.contains("TO_")&&key2.length()==7){//判断获取到自己所需要的折线图数据
                                                        Object value2 = entry2.getValue();
                                                        if(value2==null||value2.toString().equals("")){
                                                            indexValue++;
                                                            continue;
                                                            //value2=0;//设置一个默认值
                                                        }
                                                        String value1String=value2.toString();//先把值设置成字符串类型扔回去
                                                        String toMonth=key2.split("_")[1];//获取到月份的具体数字
                                                        /*if(indexValue<=32){
                                                            mDatasetOne.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                            indexValue++;
                                                        }else if(indexValue>32&&indexValue<=64){
                                                            mDatasetTwo.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                            indexValue++;
                                                        }else if(indexValue>64&&indexValue<=96){
                                                            mDatasetThree.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                            indexValue++;
                                                        }else if(indexValue>96&&indexValue<=128){
                                                            mDatasetOne.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                            indexValue++;
                                                        }else if(indexValue>128&&indexValue<=160){
                                                            mDatasetTwo.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                            indexValue++;
                                                        }else if(indexValue>160&&indexValue<=192){
                                                            mDatasetThree.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                            indexValue++;
                                                        }else if(indexValue>192&&indexValue<=224){
                                                            mDatasetOne.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                            indexValue++;
                                                        }else if(indexValue>224&&indexValue<=256){
                                                            mDatasetTwo.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                            indexValue++;
                                                        }else if(indexValue>256&&indexValue<=288){
                                                            mDatasetThree.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                            indexValue++;
                                                        }*/
                                                        mDataset.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                       /* monitoringReportObject.put("value",value1String);//Y轴
                                                        monitoringReportObject.put("rowKey",Tj_Type_Str);//图例
                                                        monitoringReportObject.put("columnKey",toMonth);//Y轴
                                                        monitoringReportList.add(monitoringReportObject);
                                                        monitoringReportObject=new HashMap();*/
                                                    }
                                                }else if(monitoringPointTypeDY==3){
                                                    if(key2.contains("AX_")||key2.contains("BX_")||key2.contains("CX_") ){//判断获取到自己所需要的折线图数据
                                                        if(key2.length()==7) {//必须在做一个判断否则平均值，最高值，最低值也进来了，因为那也是以TO_,AX_,BX_,CX_打头的
                                                            Object value2 = entry2.getValue();
                                                            if(value2==null||value2.toString().equals("")){
                                                                indexValue++;
                                                                continue;
                                                                //value2=0;//设置一个默认值
                                                            }
                                                            String value1String=value2.toString();//先把值设置成字符串类型扔回去
                                                            String toMonth = key2.split("_")[1];//获取到月份的具体数字
                                                            /*if(indexValue<=32){
                                                                mDatasetOne.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                                indexValue++;
                                                            }else if(indexValue>32&&indexValue<=64){
                                                                mDatasetTwo.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                                indexValue++;
                                                            }else if(indexValue>64&&indexValue<=96){
                                                                mDatasetThree.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                                indexValue++;
                                                            }else if(indexValue>96&&indexValue<=128){
                                                                mDatasetOne.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                                indexValue++;
                                                            }else if(indexValue>128&&indexValue<=160){
                                                                mDatasetTwo.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                                indexValue++;
                                                            }else if(indexValue>160&&indexValue<=192){
                                                                mDatasetThree.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                                indexValue++;
                                                            }else if(indexValue>192&&indexValue<=224){
                                                                mDatasetOne.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                                indexValue++;
                                                            }else if(indexValue>224&&indexValue<=256){
                                                                mDatasetTwo.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                                indexValue++;
                                                            }else if(indexValue>256&&indexValue<=288){
                                                                mDatasetThree.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                                indexValue++;
                                                            }*/
                                                            mDataset.addValue(Float.parseFloat(value1String), Tj_Type_Str+key2.split("_")[0], toMonth);
                                                            /*monitoringReportObject.put("value", value1String);//Y轴
                                                            monitoringReportObject.put("rowKey", Tj_Type_Str + key2.split("_")[0]);//图例
                                                            monitoringReportObject.put("columnKey", toMonth);//Y轴
                                                            monitoringReportList.add(monitoringReportObject);
                                                            monitoringReportObject = new HashMap();*/
                                                        }
                                                    }
                                                }
                                                // System.out.println("Key = " + key + ", Value = " + value);
                                            }

                                            List<Map> imgNames=new ArrayList<Map>();
                                            String imgName= JavaUUIDGenerator.getUUID();
                                            String savePath=tomcatImgPath+ imgName+".png";
                                            Line.getDataToFileAndSaveTOThisTomcat(mDataset,savePath,1024,420);
                                            Map imgObject = new HashMap();
                                            imgObject.put("imgName",imgName);
                                            imgObject.put("monitoringPointNameNew",monitoringPointNameNew+"-"+Tj_Type_Str);
                                            imgObject.put("imgFullName",imgName+".png");
                                            imgNames.add(imgObject);
                                            /*for(int iii=1;iii<=3;iii++){
                                                String imgName= JavaUUIDGenerator.getUUID();
                                                String savePath=tomcatImgPath+ imgName+".png";
                                                if(iii==1){
                                                    if(mDatasetOne.getColumnCount()==0){
                                                        continue;
                                                    }
                                                    Line.getDataToFileAndSaveTOThisTomcat(mDatasetOne,savePath,1024,420);
                                                }else if(iii==2){
                                                    if(mDatasetTwo.getColumnCount()==0){
                                                        continue;
                                                    }
                                                    Line.getDataToFileAndSaveTOThisTomcat(mDatasetTwo,savePath,1024,420);
                                                }else if(iii==3){
                                                    if(mDatasetThree.getColumnCount()==0){
                                                        continue;
                                                    }
                                                    Line.getDataToFileAndSaveTOThisTomcat(mDatasetThree,savePath,1024,420);
                                                }
                                                Map imgObject = new HashMap();
                                                imgObject.put("imgName",imgName);
                                                imgObject.put("monitoringPointNameNew",monitoringPointNameNew+"-"+Tj_Type_Str);
                                                imgObject.put("imgFullName",imgName+".png");
                                                imgNames.add(imgObject);
                                            }*/
                                            monitoringObject.put("monitoringReportList",imgNames);//把保存成功之后的图片名称和格式放回到列表中返回去
                                           // monitoringObject.put("monitoringReportList",monitoringReportList);//把当前监测点折线图扔回到监测点对象中
                                            monitoringReportList=new ArrayList<Map>();//重置折线图的列表对象
                                            monitoringList.add(monitoringObject);
                                            monitoringObject=new HashMap();//当前监测点执行完成重新初始化
                                        }
                                    }
                                    //处理频率
                                    for(Map map11:listMonitoringPL){
                                        String monitoringPointCoreIdPL = (String) map11.get("monitoringPointCoreId");
                                        if(monitoringPointCoreIdPL.equals(monitoringPointCoreIdNew)){//判断电度计量点id和当前循环的计量点id是否相等
                                            Integer monitoringPointTypePL       = (Integer) map11.get("monitoringPointType");
                                            Object TO_AVG         = map11.get("TO_AVG")         ;
                                            Object TO_MIN         = map11.get("TO_MIN")         ;
                                            Object TO_MIN_DATA_TAG= map11.get("TO_MIN_DATA_TAG");
                                            Object TO_MIN_TIME_TAG= map11.get("TO_MIN_TIME_TAG");
                                            Object TO_MAX         = map11.get("TO_MAX")         ;
                                            Object TO_MAX_DATA_TAG= map11.get("TO_MAX_DATA_TAG");
                                            Object TO_MAX_TIME_TAG= map11.get("TO_MAX_TIME_TAG");
                                            String Tj_Type_Str="频率";
                                            yunxingjieguo=dateString+"，"+monitoringPointNameNew+Tj_Type_Str+"的平均值为："+TO_AVG+"；最高值为："+TO_MAX+"("+format6Parse(format5Parse(TO_MAX_DATA_TAG+" "+TO_MAX_TIME_TAG))+")"+"；最低值为："+TO_MIN+"("+format6Parse(format5Parse(TO_MIN_DATA_TAG+" "+TO_MIN_TIME_TAG))+")";
                                            yunxingxiang=Tj_Type_Str.toString();
                                            monitoringObject.put("yunxingxiang",yunxingxiang);//运行项
                                            monitoringObject.put("yunxingjieguo",yunxingjieguo);//运行结果
                                            //开始初始化折线图列表数据,遍历当前的最大的map1拿到折线图要展现的数据
                                            Iterator entries1 = map11.entrySet().iterator();
                                            DefaultCategoryDataset mDataset = new DefaultCategoryDataset();
                                            //96个点太多了想折成三张32个点的折线图
                                            /*DefaultCategoryDataset mDatasetOne = new DefaultCategoryDataset();
                                            DefaultCategoryDataset mDatasetTwo = new DefaultCategoryDataset();
                                            DefaultCategoryDataset mDatasetThree = new DefaultCategoryDataset();*/
                                            Integer indexValue=0;//记录放进折线图数据的总数
                                            while (entries1.hasNext()) {
                                                Map.Entry entry1 = (Map.Entry) entries1.next();
                                                String key1 = (String)entry1.getKey();
                                               /* Object value1 = entry1.getValue();
                                                if(value1==null){
                                                    value1=0;//设置一个默认值
                                                }
                                                String value1String=value1.toString();//先把值设置成字符串类型扔回去*/
                                                if(key1.contains("TO_")&&key1.length()==7){//判断获取到自己所需要的折线图数据
                                                    Object value1 = entry1.getValue();
                                                    if(value1==null||value1.toString().equals("")){
                                                        indexValue++;
                                                        continue;
                                                        //value1=0;//设置一个默认值
                                                    }
                                                    String value1String=value1.toString();//先把值设置成字符串类型扔回去
                                                    String toMonth=key1.split("_")[1];//获取到月份的具体数字
                                                    mDataset.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                    /*if(indexValue<=32){
                                                        mDatasetOne.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                        indexValue++;
                                                    }else if(indexValue>32&&indexValue<=64){
                                                        mDatasetTwo.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                        indexValue++;
                                                    }else if(indexValue>64&&indexValue<=96){
                                                        mDatasetThree.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                        indexValue++;
                                                    }else if(indexValue>96&&indexValue<=128){
                                                        mDatasetOne.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                        indexValue++;
                                                    }else if(indexValue>128&&indexValue<=160){
                                                        mDatasetTwo.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                        indexValue++;
                                                    }else if(indexValue>160&&indexValue<=192){
                                                        mDatasetThree.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                        indexValue++;
                                                    }else if(indexValue>192&&indexValue<=224){
                                                        mDatasetOne.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                        indexValue++;
                                                    }else if(indexValue>224&&indexValue<=256){
                                                        mDatasetTwo.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                        indexValue++;
                                                    }else if(indexValue>256&&indexValue<=288){
                                                        mDatasetThree.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                        indexValue++;
                                                    }*/
                                                   /* monitoringReportObject.put("value",value1String);//Y轴
                                                    monitoringReportObject.put("rowKey",Tj_Type_Str);//图例
                                                    monitoringReportObject.put("columnKey",toMonth);//Y轴
                                                    monitoringReportList.add(monitoringReportObject);
                                                    monitoringReportObject=new HashMap();*/
                                                }
                                                // System.out.println("Key = " + key + ", Value = " + value);
                                            }
                                            List<Map> imgNames=new ArrayList<Map>();
                                            String imgName= JavaUUIDGenerator.getUUID();
                                            String savePath=tomcatImgPath+ imgName+".png";
                                            Line.getDataToFileAndSaveTOThisTomcat(mDataset,savePath,1024,420);
                                            Map imgObject = new HashMap();
                                            imgObject.put("imgName",imgName);
                                            imgObject.put("monitoringPointNameNew",monitoringPointNameNew+"-"+Tj_Type_Str);
                                            imgObject.put("imgFullName",imgName+".png");
                                            imgNames.add(imgObject);
                                            /*for(int iii=1;iii<=3;iii++){
                                                String imgName= JavaUUIDGenerator.getUUID();
                                                String savePath=tomcatImgPath+ imgName+".png";
                                                if(iii==1){
                                                    if(mDatasetOne.getColumnCount()==0){
                                                        continue;
                                                    }
                                                    Line.getDataToFileAndSaveTOThisTomcat(mDatasetOne,savePath,1024,420);
                                                }else if(iii==2){
                                                    if(mDatasetTwo.getColumnCount()==0){
                                                        continue;
                                                    }
                                                    Line.getDataToFileAndSaveTOThisTomcat(mDatasetTwo,savePath,1024,420);
                                                }else if(iii==3){
                                                    if(mDatasetThree.getColumnCount()==0){
                                                        continue;
                                                    }
                                                    Line.getDataToFileAndSaveTOThisTomcat(mDatasetThree,savePath,1024,420);
                                                }
                                                Map imgObject = new HashMap();
                                                imgObject.put("imgName",imgName);
                                                imgObject.put("monitoringPointNameNew",monitoringPointNameNew+"-"+Tj_Type_Str);
                                                imgObject.put("imgFullName",imgName+".png");
                                                imgNames.add(imgObject);
                                            }*/
                                            monitoringObject.put("monitoringReportList",imgNames);//把保存成功之后的图片名称和格式放回到列表中返回去
                                            //monitoringObject.put("monitoringReportList",monitoringReportList);//把当前监测点折线图扔回到监测点对象中
                                            monitoringReportList=new ArrayList<Map>();//重置折线图的列表对象
                                            monitoringList.add(monitoringObject);
                                            monitoringObject=new HashMap();//当前监测点执行完成重新初始化
                                        }
                                    }
                                    //处理湿度
                                    for(Map map11:listMonitoringSD){
                                        String monitoringPointCoreIdSD = (String) map11.get("monitoringPointCoreId");
                                        if(monitoringPointCoreIdSD.equals(monitoringPointCoreIdNew)){//判断电度计量点id和当前循环的计量点id是否相等
                                            Integer monitoringPointTypeSD       = (Integer) map11.get("monitoringPointType");
                                            Object TO_AVG         = map11.get("TO_AVG")         ;
                                            Object TO_MIN         = map11.get("TO_MIN")         ;
                                            Object TO_MIN_DATA_TAG= map11.get("TO_MIN_DATA_TAG");
                                            Object TO_MIN_TIME_TAG= map11.get("TO_MIN_TIME_TAG");
                                            Object TO_MAX         = map11.get("TO_MAX")         ;
                                            Object TO_MAX_DATA_TAG= map11.get("TO_MAX_DATA_TAG");
                                            Object TO_MAX_TIME_TAG= map11.get("TO_MAX_TIME_TAG");
                                            String Tj_Type_Str="湿度";
                                            yunxingjieguo=dateString+"，"+monitoringPointNameNew+Tj_Type_Str+"的平均值为："+TO_AVG+"；最高值为："+TO_MAX+"("+format6Parse(format5Parse(TO_MAX_DATA_TAG+" "+TO_MAX_TIME_TAG))+")"+"；最低值为："+TO_MIN+"("+format6Parse(format5Parse(TO_MIN_DATA_TAG+" "+TO_MIN_TIME_TAG))+")";
                                            yunxingxiang=Tj_Type_Str.toString();
                                            monitoringObject.put("yunxingxiang",yunxingxiang);//运行项
                                            monitoringObject.put("yunxingjieguo",yunxingjieguo);//运行结果
                                            //开始初始化折线图列表数据,遍历当前的最大的map1拿到折线图要展现的数据
                                            Iterator entries1 = map11.entrySet().iterator();
                                            DefaultCategoryDataset mDataset = new DefaultCategoryDataset();
                                            //96个点太多了想折成三张32个点的折线图
                                           /* DefaultCategoryDataset mDatasetOne = new DefaultCategoryDataset();
                                            DefaultCategoryDataset mDatasetTwo = new DefaultCategoryDataset();
                                            DefaultCategoryDataset mDatasetThree = new DefaultCategoryDataset();*/
                                            Integer indexValue=0;//记录放进折线图数据的总数
                                            while (entries1.hasNext()) {
                                                Map.Entry entry1 = (Map.Entry) entries1.next();
                                                String key1 = (String)entry1.getKey();
                                                if(key1.contains("TO_")&&key1.length()==7){//判断获取到自己所需要的折线图数据
                                                    Object value1 = entry1.getValue();
                                                    if(value1==null||value1.toString().equals("")){
                                                        indexValue++;
                                                        continue;
                                                        //value1=0;//设置一个默认值
                                                    }
                                                    String value1String=value1.toString();//先把值设置成字符串类型扔回去
                                                    String toMonth=key1.split("_")[1];//获取到月份的具体数字
                                                    mDataset.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                    /*if(indexValue<=32){
                                                        mDatasetOne.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                        indexValue++;
                                                    }else if(indexValue>32&&indexValue<=64){
                                                        mDatasetTwo.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                        indexValue++;
                                                    }else if(indexValue>64&&indexValue<=96){
                                                        mDatasetThree.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                        indexValue++;
                                                    }else if(indexValue>96&&indexValue<=128){
                                                        mDatasetOne.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                        indexValue++;
                                                    }else if(indexValue>128&&indexValue<=160){
                                                        mDatasetTwo.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                        indexValue++;
                                                    }else if(indexValue>160&&indexValue<=192){
                                                        mDatasetThree.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                        indexValue++;
                                                    }else if(indexValue>192&&indexValue<=224){
                                                        mDatasetOne.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                        indexValue++;
                                                    }else if(indexValue>224&&indexValue<=256){
                                                        mDatasetTwo.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                        indexValue++;
                                                    }else if(indexValue>256&&indexValue<=288){
                                                        mDatasetThree.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                        indexValue++;
                                                    }*/
                                                   /* monitoringReportObject.put("value",value1String);//Y轴
                                                    monitoringReportObject.put("rowKey",Tj_Type_Str);//图例
                                                    monitoringReportObject.put("columnKey",toMonth);//Y轴
                                                    monitoringReportList.add(monitoringReportObject);
                                                    monitoringReportObject=new HashMap();*/
                                                }
                                                // System.out.println("Key = " + key + ", Value = " + value);
                                            }
                                            List<Map> imgNames=new ArrayList<Map>();
                                            String imgName= JavaUUIDGenerator.getUUID();
                                            String savePath=tomcatImgPath+ imgName+".png";
                                            Line.getDataToFileAndSaveTOThisTomcat(mDataset,savePath,1024,420);
                                            Map imgObject = new HashMap();
                                            imgObject.put("imgName",imgName);
                                            imgObject.put("monitoringPointNameNew",monitoringPointNameNew+"-"+Tj_Type_Str);
                                            imgObject.put("imgFullName",imgName+".png");
                                            imgNames.add(imgObject);
                                            /*for(int iii=1;iii<=3;iii++){
                                                String imgName= JavaUUIDGenerator.getUUID();
                                                String savePath=tomcatImgPath+ imgName+".png";
                                                if(iii==1){
                                                    if(mDatasetOne.getColumnCount()==0){
                                                        continue;
                                                    }
                                                    Line.getDataToFileAndSaveTOThisTomcat(mDatasetOne,savePath,1024,420);
                                                }else if(iii==2){
                                                    if(mDatasetTwo.getColumnCount()==0){
                                                        continue;
                                                    }
                                                    Line.getDataToFileAndSaveTOThisTomcat(mDatasetTwo,savePath,1024,420);
                                                }else if(iii==3){
                                                    if(mDatasetThree.getColumnCount()==0){
                                                        continue;
                                                    }
                                                    Line.getDataToFileAndSaveTOThisTomcat(mDatasetThree,savePath,1024,420);
                                                }
                                                Map imgObject = new HashMap();
                                                imgObject.put("imgName",imgName);
                                                imgObject.put("monitoringPointNameNew",monitoringPointNameNew+"-"+Tj_Type_Str);
                                                imgObject.put("imgFullName",imgName+".png");
                                                imgNames.add(imgObject);
                                            }*/
                                            monitoringObject.put("monitoringReportList",imgNames);//把保存成功之后的图片名称和格式放回到列表中返回去
                                            //monitoringObject.put("monitoringReportList",monitoringReportList);//把当前监测点折线图扔回到监测点对象中
                                            monitoringReportList=new ArrayList<Map>();//重置折线图的列表对象
                                            monitoringList.add(monitoringObject);
                                            monitoringObject=new HashMap();//当前监测点执行完成重新初始化
                                        }
                                    }
                                    //处理视在功率
                                    for(Map map11:listMonitoringSZ){
                                        String monitoringPointCoreIdSZ = (String) map11.get("monitoringPointCoreId");
                                        if(monitoringPointCoreIdSZ.equals(monitoringPointCoreIdNew)) {//判断电流计量点id和当前循环的计量点id是否相等
                                            Integer monitoringPointTypeSZ       = (Integer) map11.get("monitoringPointType");
                                            Object TO_AVG         = map11.get("TO_AVG")         ;
                                            Object TO_MIN         = map11.get("TO_MIN")         ;
                                            Object TO_MIN_DATA_TAG= map11.get("TO_MIN_DATA_TAG");
                                            Object TO_MIN_TIME_TAG= map11.get("TO_MIN_TIME_TAG");
                                            Object TO_MAX         = map11.get("TO_MAX")         ;
                                            Object TO_MAX_DATA_TAG= map11.get("TO_MAX_DATA_TAG");
                                            Object TO_MAX_TIME_TAG= map11.get("TO_MAX_TIME_TAG");
                                            Object AX_AVG         = map11.get("AX_AVG")         ;
                                            Object AX_MIN         = map11.get("AX_MIN")         ;
                                            Object AX_MIN_DATA_TAG= map11.get("AX_MIN_DATA_TAG");
                                            Object AX_MIN_TIME_TAG= map11.get("AX_MIN_TIME_TAG");
                                            Object AX_MAX         = map11.get("AX_MAX")         ;
                                            Object AX_MAX_DATA_TAG= map11.get("AX_MAX_DATA_TAG");
                                            Object AX_MAX_TIME_TAG= map11.get("AX_MAX_TIME_TAG");
                                            Object BX_AVG         = map11.get("BX_AVG")         ;
                                            Object BX_MIN         = map11.get("BX_MIN")         ;
                                            Object BX_MIN_DATA_TAG= map11.get("BX_MIN_DATA_TAG");
                                            Object BX_MIN_TIME_TAG= map11.get("BX_MIN_TIME_TAG");
                                            Object BX_MAX         = map11.get("BX_MAX")         ;
                                            Object BX_MAX_DATA_TAG= map11.get("BX_MAX_DATA_TAG");
                                            Object BX_MAX_TIME_TAG= map11.get("BX_MAX_TIME_TAG");
                                            Object CX_AVG         = map11.get("CX_AVG")         ;
                                            Object CX_MIN         = map11.get("CX_MIN")         ;
                                            Object CX_MIN_DATA_TAG= map11.get("CX_MIN_DATA_TAG");
                                            Object CX_MIN_TIME_TAG= map11.get("CX_MIN_TIME_TAG");
                                            Object CX_MAX         = map11.get("CX_MAX")         ;
                                            Object CX_MAX_DATA_TAG= map11.get("CX_MAX_DATA_TAG");
                                            Object CX_MAX_TIME_TAG= map11.get("CX_MAX_TIME_TAG");
                                            String Tj_Type_Str="视在功率";
                                            if(monitoringPointTypeSZ==1){//表示视在功率单相
                                                yunxingjieguo=dateString+"，"+monitoringPointNameNew+Tj_Type_Str+"的平均值为："+TO_AVG+"；最高值为："+TO_MAX+"("+format6Parse(format5Parse(TO_MAX_DATA_TAG+" "+TO_MAX_TIME_TAG))+")"+"；最低值为："+TO_MIN+"("+format6Parse(format5Parse(TO_MIN_DATA_TAG+" "+TO_MIN_TIME_TAG))+")";
                                            }else if(monitoringPointTypeSZ==3){//表示视在功率三相
                                                yunxingjieguo=dateString+"，"+monitoringPointNameNew
                                                        +Tj_Type_Str+"A相的平均值为："+AX_AVG+"；最高值为："+AX_MAX+"("+format6Parse(format5Parse(AX_MAX_DATA_TAG+" "+AX_MAX_TIME_TAG))+")"+"；最低值为："+AX_MIN+"("+format6Parse(format5Parse(AX_MIN_DATA_TAG+" "+AX_MIN_TIME_TAG))+")"+"。"
                                                        +Tj_Type_Str+"B相的平均值为："+BX_AVG+"；最高值为："+BX_MAX+"("+format6Parse(format5Parse(BX_MAX_DATA_TAG+" "+BX_MAX_TIME_TAG))+")"+"；最低值为："+BX_MIN+"("+format6Parse(format5Parse(BX_MIN_DATA_TAG+" "+BX_MIN_TIME_TAG))+")"+"。"
                                                        +Tj_Type_Str+"C相的平均值为："+CX_AVG+"；最高值为："+CX_MAX+"("+format6Parse(format5Parse(CX_MAX_DATA_TAG+" "+CX_MAX_TIME_TAG))+")"+"；最低值为："+CX_MIN+"("+format6Parse(format5Parse(CX_MIN_DATA_TAG+" "+CX_MIN_TIME_TAG))+")"+"。";
                                            }
                                                  yunxingxiang=Tj_Type_Str.toString();
                                            monitoringObject.put("yunxingxiang",yunxingxiang);//运行项
                                            monitoringObject.put("yunxingjieguo",yunxingjieguo);//运行结果
                                            //开始初始化折线图列表数据,遍历当前的最大的map1拿到折线图要展现的数据
                                            Iterator entries2 = map11.entrySet().iterator();
                                            DefaultCategoryDataset mDataset = new DefaultCategoryDataset();
                                            //96个点太多了想折成三张32个点的折线图
                                           /* DefaultCategoryDataset mDatasetOne = new DefaultCategoryDataset();
                                            DefaultCategoryDataset mDatasetTwo = new DefaultCategoryDataset();
                                            DefaultCategoryDataset mDatasetThree = new DefaultCategoryDataset();*/
                                            Integer indexValue=0;//记录放进折线图数据的总数
                                            while (entries2.hasNext()) {
                                                Map.Entry entry2 = (Map.Entry) entries2.next();
                                                String key2 = (String)entry2.getKey();

                                                if(monitoringPointTypeSZ==1){
                                                    if(key2.contains("TO_")&&key2.length()==7){//判断获取到自己所需要的折线图数据
                                                        Object value2 = entry2.getValue();
                                                        if(value2==null||value2.toString().equals("")){
                                                            indexValue++;
                                                            continue;
                                                            //value2=0;//设置一个默认值
                                                        }
                                                        String value1String=value2.toString();//先把值设置成字符串类型扔回去
                                                        String toMonth=key2.split("_")[1];//获取到月份的具体数字
                                                       /* monitoringReportObject.put("value",value1String);//Y轴
                                                        monitoringReportObject.put("rowKey",Tj_Type_Str);//图例
                                                        monitoringReportObject.put("columnKey",toMonth);//Y轴
                                                        monitoringReportList.add(monitoringReportObject);
                                                        monitoringReportObject=new HashMap();*/
                                                        mDataset.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                        /*if(indexValue<=32){
                                                            mDatasetOne.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                            indexValue++;
                                                        }else if(indexValue>32&&indexValue<=64){
                                                            mDatasetTwo.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                            indexValue++;
                                                        }else if(indexValue>64&&indexValue<=96){
                                                            mDatasetThree.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                            indexValue++;
                                                        }else if(indexValue>96&&indexValue<=128){
                                                            mDatasetOne.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                            indexValue++;
                                                        }else if(indexValue>128&&indexValue<=160){
                                                            mDatasetTwo.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                            indexValue++;
                                                        }else if(indexValue>160&&indexValue<=192){
                                                            mDatasetThree.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                            indexValue++;
                                                        }else if(indexValue>192&&indexValue<=224){
                                                            mDatasetOne.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                            indexValue++;
                                                        }else if(indexValue>224&&indexValue<=256){
                                                            mDatasetTwo.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                            indexValue++;
                                                        }else if(indexValue>256&&indexValue<=288){
                                                            mDatasetThree.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                            indexValue++;
                                                        }*/
                                                    }
                                                }else if(monitoringPointTypeSZ==3){
                                                    if(key2.contains("AX_")||key2.contains("BX_")||key2.contains("CX_") ){//判断获取到自己所需要的折线图数据
                                                        if(key2.length()==7) {//必须在做一个判断否则平均值，最高值，最低值也进来了，因为那也是以TO_,AX_,BX_,CX_打头的
                                                            Object value2 = entry2.getValue();
                                                            if(value2==null||value2.toString().equals("")){
                                                                indexValue++;
                                                                continue;
                                                                //value2=0;//设置一个默认值
                                                            }
                                                            String value1String=value2.toString();//先把值设置成字符串类型扔回去
                                                            String toMonth = key2.split("_")[1];//获取到月份的具体数字
                                                           /* monitoringReportObject.put("value", value1String);//Y轴
                                                            monitoringReportObject.put("rowKey", Tj_Type_Str + key2.split("_")[0]);//图例
                                                            monitoringReportObject.put("columnKey", toMonth);//Y轴
                                                            monitoringReportList.add(monitoringReportObject);
                                                            monitoringReportObject = new HashMap();*/
                                                            mDataset.addValue(Float.parseFloat(value1String), Tj_Type_Str+key2.split("_")[0], toMonth);
                                                            /*if(indexValue<=32){
                                                                mDatasetOne.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                                indexValue++;
                                                            }else if(indexValue>32&&indexValue<=64){
                                                                mDatasetTwo.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                                indexValue++;
                                                            }else if(indexValue>64&&indexValue<=96){
                                                                mDatasetThree.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                                indexValue++;
                                                            }else if(indexValue>96&&indexValue<=128){
                                                                mDatasetOne.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                                indexValue++;
                                                            }else if(indexValue>128&&indexValue<=160){
                                                                mDatasetTwo.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                                indexValue++;
                                                            }else if(indexValue>160&&indexValue<=192){
                                                                mDatasetThree.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                                indexValue++;
                                                            }else if(indexValue>192&&indexValue<=224){
                                                                mDatasetOne.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                                indexValue++;
                                                            }else if(indexValue>224&&indexValue<=256){
                                                                mDatasetTwo.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                                indexValue++;
                                                            }else if(indexValue>256&&indexValue<=288){
                                                                mDatasetThree.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                                indexValue++;
                                                            }*/
                                                        }
                                                    }
                                                }
                                                // System.out.println("Key = " + key + ", Value = " + value);
                                            }
                                            List<Map> imgNames=new ArrayList<Map>();
                                            String imgName= JavaUUIDGenerator.getUUID();
                                            String savePath=tomcatImgPath+ imgName+".png";
                                            Line.getDataToFileAndSaveTOThisTomcat(mDataset,savePath,1024,420);
                                            Map imgObject = new HashMap();
                                            imgObject.put("imgName",imgName);
                                            imgObject.put("monitoringPointNameNew",monitoringPointNameNew+"-"+Tj_Type_Str);
                                            imgObject.put("imgFullName",imgName+".png");
                                            imgNames.add(imgObject);
                                            /*for(int iii=1;iii<=3;iii++){
                                                String imgName= JavaUUIDGenerator.getUUID();
                                                String savePath=tomcatImgPath+ imgName+".png";
                                                if(iii==1){
                                                    if(mDatasetOne.getColumnCount()==0){
                                                        continue;
                                                    }
                                                    Line.getDataToFileAndSaveTOThisTomcat(mDatasetOne,savePath,1024,420);
                                                }else if(iii==2){
                                                    if(mDatasetTwo.getColumnCount()==0){
                                                        continue;
                                                    }
                                                    Line.getDataToFileAndSaveTOThisTomcat(mDatasetTwo,savePath,1024,420);
                                                }else if(iii==3){
                                                    if(mDatasetThree.getColumnCount()==0){
                                                        continue;
                                                    }
                                                    Line.getDataToFileAndSaveTOThisTomcat(mDatasetThree,savePath,1024,420);
                                                }
                                                Map imgObject = new HashMap();
                                                imgObject.put("imgName",imgName);
                                                imgObject.put("monitoringPointNameNew",monitoringPointNameNew+"-"+Tj_Type_Str);
                                                imgObject.put("imgFullName",imgName+".png");
                                                imgNames.add(imgObject);
                                            }*/
                                            monitoringObject.put("monitoringReportList",imgNames);//把保存成功之后的图片名称和格式放回到列表中返回去
                                            //monitoringObject.put("monitoringReportList",monitoringReportList);//把当前监测点折线图扔回到监测点对象中
                                            monitoringReportList=new ArrayList<Map>();//重置折线图的列表对象
                                            monitoringList.add(monitoringObject);
                                            monitoringObject=new HashMap();//当前监测点执行完成重新初始化
                                        }
                                    }
                                    //处理无功
                                    for(Map map11:listMonitoringWG){
                                        String monitoringPointCoreIdWG = (String) map11.get("monitoringPointCoreId");
                                        if(monitoringPointCoreIdWG.equals(monitoringPointCoreIdNew)) {//判断电流计量点id和当前循环的计量点id是否相等
                                            Integer monitoringPointTypeWG       = (Integer) map11.get("monitoringPointType");
                                            Object TO_AVG         = map11.get("TO_AVG")         ;
                                            Object TO_MIN         = map11.get("TO_MIN")         ;
                                            Object TO_MIN_DATA_TAG= map11.get("TO_MIN_DATA_TAG");
                                            Object TO_MIN_TIME_TAG= map11.get("TO_MIN_TIME_TAG");
                                            Object TO_MAX         = map11.get("TO_MAX")         ;
                                            Object TO_MAX_DATA_TAG= map11.get("TO_MAX_DATA_TAG");
                                            Object TO_MAX_TIME_TAG= map11.get("TO_MAX_TIME_TAG");
                                            Object AX_AVG         = map11.get("AX_AVG")         ;
                                            Object AX_MIN         = map11.get("AX_MIN")         ;
                                            Object AX_MIN_DATA_TAG= map11.get("AX_MIN_DATA_TAG");
                                            Object AX_MIN_TIME_TAG= map11.get("AX_MIN_TIME_TAG");
                                            Object AX_MAX         = map11.get("AX_MAX")         ;
                                            Object AX_MAX_DATA_TAG= map11.get("AX_MAX_DATA_TAG");
                                            Object AX_MAX_TIME_TAG= map11.get("AX_MAX_TIME_TAG");
                                            Object BX_AVG         = map11.get("BX_AVG")         ;
                                            Object BX_MIN         = map11.get("BX_MIN")         ;
                                            Object BX_MIN_DATA_TAG= map11.get("BX_MIN_DATA_TAG");
                                            Object BX_MIN_TIME_TAG= map11.get("BX_MIN_TIME_TAG");
                                            Object BX_MAX         = map11.get("BX_MAX")         ;
                                            Object BX_MAX_DATA_TAG= map11.get("BX_MAX_DATA_TAG");
                                            Object BX_MAX_TIME_TAG= map11.get("BX_MAX_TIME_TAG");
                                            Object CX_AVG         = map11.get("CX_AVG")         ;
                                            Object CX_MIN         = map11.get("CX_MIN")         ;
                                            Object CX_MIN_DATA_TAG= map11.get("CX_MIN_DATA_TAG");
                                            Object CX_MIN_TIME_TAG= map11.get("CX_MIN_TIME_TAG");
                                            Object CX_MAX         = map11.get("CX_MAX")         ;
                                            Object CX_MAX_DATA_TAG= map11.get("CX_MAX_DATA_TAG");
                                            Object CX_MAX_TIME_TAG= map11.get("CX_MAX_TIME_TAG");
                                            String Tj_Type_Str="无功功率";
                                            if(monitoringPointTypeWG==1){
                                                yunxingjieguo=dateString+"，"+monitoringPointNameNew+Tj_Type_Str+"的平均值为："+TO_AVG+"；最高值为："+TO_MAX+"("+format6Parse(format5Parse(TO_MAX_DATA_TAG+" "+TO_MAX_TIME_TAG))+")"+"；最低值为："+TO_MIN+"("+format6Parse(format5Parse(TO_MIN_DATA_TAG+" "+TO_MIN_TIME_TAG))+")";
                                            }else  if(monitoringPointTypeWG==3){
                                                yunxingjieguo=dateString+"，"+monitoringPointNameNew
                                                        +Tj_Type_Str+"A相的平均值为："+AX_AVG+"；最高值为："+AX_MAX+"("+format6Parse(format5Parse(AX_MAX_DATA_TAG+" "+AX_MAX_TIME_TAG))+")"+"；最低值为："+AX_MIN+"("+format6Parse(format5Parse(AX_MIN_DATA_TAG+" "+AX_MIN_TIME_TAG))+")"+"。"
                                                        +Tj_Type_Str+"B相的平均值为："+BX_AVG+"；最高值为："+BX_MAX+"("+format6Parse(format5Parse(BX_MAX_DATA_TAG+" "+BX_MAX_TIME_TAG))+")"+"；最低值为："+BX_MIN+"("+format6Parse(format5Parse(BX_MIN_DATA_TAG+" "+BX_MIN_TIME_TAG))+")"+"。"
                                                        +Tj_Type_Str+"C相的平均值为："+CX_AVG+"；最高值为："+CX_MAX+"("+format6Parse(format5Parse(CX_MAX_DATA_TAG+" "+CX_MAX_TIME_TAG))+")"+"；最低值为："+CX_MIN+"("+format6Parse(format5Parse(CX_MIN_DATA_TAG+" "+CX_MIN_TIME_TAG))+")"+"。";
                                            }
                                            yunxingxiang=Tj_Type_Str.toString();
                                            monitoringObject.put("yunxingxiang",yunxingxiang);//运行项
                                            monitoringObject.put("yunxingjieguo",yunxingjieguo);//运行结果
                                            //开始初始化折线图列表数据,遍历当前的最大的map1拿到折线图要展现的数据
                                            Iterator entries2 = map11.entrySet().iterator();
                                            DefaultCategoryDataset mDataset = new DefaultCategoryDataset();
                                            //96个点太多了想折成三张32个点的折线图
                                           /* DefaultCategoryDataset mDatasetOne = new DefaultCategoryDataset();
                                            DefaultCategoryDataset mDatasetTwo = new DefaultCategoryDataset();
                                            DefaultCategoryDataset mDatasetThree = new DefaultCategoryDataset();*/
                                            Integer indexValue=0;//记录放进折线图数据的总数
                                            while (entries2.hasNext()) {
                                                Map.Entry entry2 = (Map.Entry) entries2.next();
                                                String key2 = (String)entry2.getKey();
                                               /* Object value2 = entry2.getValue();
                                                if(value2==null){
                                                    value2=0;//设置一个默认值
                                                }
                                                String value1String=value2.toString();//先把值设置成字符串类型扔回去*/
                                                if(monitoringPointTypeWG==1){
                                                    if(key2.contains("TO_")&&key2.length()==7){//判断获取到自己所需要的折线图数据
                                                        Object value2 = entry2.getValue();
                                                        if(value2==null||value2.toString().equals("")){
                                                            indexValue++;
                                                            continue;
                                                            //value2=0;//设置一个默认值
                                                        }
                                                        String value1String=value2.toString();//先把值设置成字符串类型扔回去
                                                        String toMonth=key2.split("_")[1];//获取到月份的具体数字
                                                        mDataset.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                        /*if(indexValue<=32){
                                                            mDatasetOne.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                            indexValue++;
                                                        }else if(indexValue>32&&indexValue<=64){
                                                            mDatasetTwo.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                            indexValue++;
                                                        }else if(indexValue>64&&indexValue<=96){
                                                            mDatasetThree.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                            indexValue++;
                                                        }else if(indexValue>96&&indexValue<=128){
                                                            mDatasetOne.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                            indexValue++;
                                                        }else if(indexValue>128&&indexValue<=160){
                                                            mDatasetTwo.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                            indexValue++;
                                                        }else if(indexValue>160&&indexValue<=192){
                                                            mDatasetThree.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                            indexValue++;
                                                        }else if(indexValue>192&&indexValue<=224){
                                                            mDatasetOne.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                            indexValue++;
                                                        }else if(indexValue>224&&indexValue<=256){
                                                            mDatasetTwo.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                            indexValue++;
                                                        }else if(indexValue>256&&indexValue<=288){
                                                            mDatasetThree.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                            indexValue++;
                                                        }*/
                                                        /*monitoringReportObject.put("value",value1String);//Y轴
                                                        monitoringReportObject.put("rowKey",Tj_Type_Str);//图例
                                                        monitoringReportObject.put("columnKey",toMonth);//Y轴
                                                        monitoringReportList.add(monitoringReportObject);
                                                        monitoringReportObject=new HashMap();*/
                                                    }
                                                }else if(monitoringPointTypeWG==3){
                                                    if(key2.contains("AX_")||key2.contains("BX_")||key2.contains("CX_") ){//判断获取到自己所需要的折线图数据
                                                        if(key2.length()==7) {//必须在做一个判断否则平均值，最高值，最低值也进来了，因为那也是以TO_,AX_,BX_,CX_打头的
                                                            Object value2 = entry2.getValue();
                                                            if(value2==null||value2.toString().equals("")){
                                                                indexValue++;
                                                                continue;
                                                                //value2=0;//设置一个默认值
                                                            }
                                                            String value1String=value2.toString();//先把值设置成字符串类型扔回去
                                                            String toMonth = key2.split("_")[1];//获取到月份的具体数字
                                                            mDataset.addValue(Float.parseFloat(value1String), Tj_Type_Str+key2.split("_")[0], toMonth);
                                                            /*if(indexValue<=32){
                                                                mDatasetOne.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                                indexValue++;
                                                            }else if(indexValue>32&&indexValue<=64){
                                                                mDatasetTwo.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                                indexValue++;
                                                            }else if(indexValue>64&&indexValue<=96){
                                                                mDatasetThree.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                                indexValue++;
                                                            }else if(indexValue>96&&indexValue<=128){
                                                                mDatasetOne.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                                indexValue++;
                                                            }else if(indexValue>128&&indexValue<=160){
                                                                mDatasetTwo.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                                indexValue++;
                                                            }else if(indexValue>160&&indexValue<=192){
                                                                mDatasetThree.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                                indexValue++;
                                                            }else if(indexValue>192&&indexValue<=224){
                                                                mDatasetOne.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                                indexValue++;
                                                            }else if(indexValue>224&&indexValue<=256){
                                                                mDatasetTwo.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                                indexValue++;
                                                            }else if(indexValue>256&&indexValue<=288){
                                                                mDatasetThree.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                                indexValue++;
                                                            }*/
                                                           /* monitoringReportObject.put("value", value1String);//Y轴
                                                            monitoringReportObject.put("rowKey", Tj_Type_Str + key2.split("_")[0]);//图例
                                                            monitoringReportObject.put("columnKey", toMonth);//Y轴
                                                            monitoringReportList.add(monitoringReportObject);
                                                            monitoringReportObject = new HashMap();*/
                                                        }
                                                    }
                                                }
                                                // System.out.println("Key = " + key + ", Value = " + value);
                                            }
                                            List<Map> imgNames=new ArrayList<Map>();
                                            String imgName= JavaUUIDGenerator.getUUID();
                                            String savePath=tomcatImgPath+ imgName+".png";
                                            Line.getDataToFileAndSaveTOThisTomcat(mDataset,savePath,1024,420);
                                            Map imgObject = new HashMap();
                                            imgObject.put("imgName",imgName);
                                            imgObject.put("monitoringPointNameNew",monitoringPointNameNew+"-"+Tj_Type_Str);
                                            imgObject.put("imgFullName",imgName+".png");
                                            imgNames.add(imgObject);
                                            /*for(int iii=1;iii<=3;iii++){
                                                String imgName= JavaUUIDGenerator.getUUID();
                                                String savePath=tomcatImgPath+ imgName+".png";
                                                if(iii==1){
                                                    if(mDatasetOne.getColumnCount()==0){
                                                        continue;
                                                    }
                                                    Line.getDataToFileAndSaveTOThisTomcat(mDatasetOne,savePath,1024,420);
                                                }else if(iii==2){
                                                    if(mDatasetTwo.getColumnCount()==0){
                                                        continue;
                                                    }
                                                    Line.getDataToFileAndSaveTOThisTomcat(mDatasetTwo,savePath,1024,420);
                                                }else if(iii==3){
                                                    if(mDatasetThree.getColumnCount()==0){
                                                        continue;
                                                    }
                                                    Line.getDataToFileAndSaveTOThisTomcat(mDatasetThree,savePath,1024,420);
                                                }
                                                Map imgObject = new HashMap();
                                                imgObject.put("imgName",imgName);
                                                imgObject.put("monitoringPointNameNew",monitoringPointNameNew+"-"+Tj_Type_Str);
                                                imgObject.put("imgFullName",imgName+".png");
                                                imgNames.add(imgObject);
                                            }*/
                                            monitoringObject.put("monitoringReportList",imgNames);//把保存成功之后的图片名称和格式放回到列表中返回去
                                            //monitoringObject.put("monitoringReportList",monitoringReportList);//把当前监测点折线图扔回到监测点对象中
                                            monitoringReportList=new ArrayList<Map>();//重置折线图的列表对象
                                            monitoringList.add(monitoringObject);
                                            monitoringObject=new HashMap();//当前监测点执行完成重新初始化
                                        }
                                    }
                                    //处理有功
                                    for(Map map11:listMonitoringYG){
                                        String monitoringPointCoreIdYG = (String) map11.get("monitoringPointCoreId");
                                        if(monitoringPointCoreIdYG.equals(monitoringPointCoreIdNew)) {//判断电流计量点id和当前循环的计量点id是否相等
                                            Integer monitoringPointTypeYG       = (Integer) map11.get("monitoringPointType");
                                            Object TO_AVG         = map11.get("TO_AVG")         ;
                                            Object TO_MIN         = map11.get("TO_MIN")         ;
                                            Object TO_MIN_DATA_TAG= map11.get("TO_MIN_DATA_TAG");
                                            Object TO_MIN_TIME_TAG= map11.get("TO_MIN_TIME_TAG");
                                            Object TO_MAX         = map11.get("TO_MAX")         ;
                                            Object TO_MAX_DATA_TAG= map11.get("TO_MAX_DATA_TAG");
                                            Object TO_MAX_TIME_TAG= map11.get("TO_MAX_TIME_TAG");
                                            Object AX_AVG         = map11.get("AX_AVG")         ;
                                            Object AX_MIN         = map11.get("AX_MIN")         ;
                                            Object AX_MIN_DATA_TAG= map11.get("AX_MIN_DATA_TAG");
                                            Object AX_MIN_TIME_TAG= map11.get("AX_MIN_TIME_TAG");
                                            Object AX_MAX         = map11.get("AX_MAX")         ;
                                            Object AX_MAX_DATA_TAG= map11.get("AX_MAX_DATA_TAG");
                                            Object AX_MAX_TIME_TAG= map11.get("AX_MAX_TIME_TAG");
                                            Object BX_AVG         = map11.get("BX_AVG")         ;
                                            Object BX_MIN         = map11.get("BX_MIN")         ;
                                            Object BX_MIN_DATA_TAG= map11.get("BX_MIN_DATA_TAG");
                                            Object BX_MIN_TIME_TAG= map11.get("BX_MIN_TIME_TAG");
                                            Object BX_MAX         = map11.get("BX_MAX")         ;
                                            Object BX_MAX_DATA_TAG= map11.get("BX_MAX_DATA_TAG");
                                            Object BX_MAX_TIME_TAG= map11.get("BX_MAX_TIME_TAG");
                                            Object CX_AVG         = map11.get("CX_AVG")         ;
                                            Object CX_MIN         = map11.get("CX_MIN")         ;
                                            Object CX_MIN_DATA_TAG= map11.get("CX_MIN_DATA_TAG");
                                            Object CX_MIN_TIME_TAG= map11.get("CX_MIN_TIME_TAG");
                                            Object CX_MAX         = map11.get("CX_MAX")         ;
                                            Object CX_MAX_DATA_TAG= map11.get("CX_MAX_DATA_TAG");
                                            Object CX_MAX_TIME_TAG= map11.get("CX_MAX_TIME_TAG");
                                            String Tj_Type_Str="有功功率";
                                            if(monitoringPointTypeYG==1){//单相有功功率
                                                yunxingjieguo=dateString+"，"+monitoringPointNameNew+Tj_Type_Str+"的平均值为："+TO_AVG+"；最高值为："+TO_MAX+"("+format6Parse(format5Parse(TO_MAX_DATA_TAG+" "+TO_MAX_TIME_TAG))+")"+"；最低值为："+TO_MIN+"("+format6Parse(format5Parse(TO_MIN_DATA_TAG+" "+TO_MIN_TIME_TAG))+")";
                                            }else if(monitoringPointTypeYG==3){//三相有功功率
                                                yunxingjieguo=dateString+"，"+monitoringPointNameNew
                                                        +Tj_Type_Str+"A相的平均值为："+AX_AVG+"；最高值为："+AX_MAX+"("+format6Parse(format5Parse(AX_MAX_DATA_TAG+" "+AX_MAX_TIME_TAG))+")"+"；最低值为："+AX_MIN+"("+format6Parse(format5Parse(AX_MIN_DATA_TAG+" "+AX_MIN_TIME_TAG))+")"+"。"
                                                        +Tj_Type_Str+"B相的平均值为："+BX_AVG+"；最高值为："+BX_MAX+"("+format6Parse(format5Parse(BX_MAX_DATA_TAG+" "+BX_MAX_TIME_TAG))+")"+"；最低值为："+BX_MIN+"("+format6Parse(format5Parse(BX_MIN_DATA_TAG+" "+BX_MIN_TIME_TAG))+")"+"。"
                                                        +Tj_Type_Str+"C相的平均值为："+CX_AVG+"；最高值为："+CX_MAX+"("+format6Parse(format5Parse(CX_MAX_DATA_TAG+" "+CX_MAX_TIME_TAG))+")"+"；最低值为："+CX_MIN+"("+format6Parse(format5Parse(CX_MIN_DATA_TAG+" "+CX_MIN_TIME_TAG))+")"+"。";
                                            }
                                            yunxingxiang=Tj_Type_Str.toString();
                                            monitoringObject.put("yunxingxiang",yunxingxiang);//运行项
                                            monitoringObject.put("yunxingjieguo",yunxingjieguo);//运行结果
                                            //开始初始化折线图列表数据,遍历当前的最大的map1拿到折线图要展现的数据
                                            Iterator entries2 = map11.entrySet().iterator();
                                            DefaultCategoryDataset mDataset = new DefaultCategoryDataset();
                                            //96个点太多了想折成三张32个点的折线图
                                            /*DefaultCategoryDataset mDatasetOne = new DefaultCategoryDataset();
                                            DefaultCategoryDataset mDatasetTwo = new DefaultCategoryDataset();
                                            DefaultCategoryDataset mDatasetThree = new DefaultCategoryDataset();*/
                                            Integer indexValue=0;//记录放进折线图数据的总数
                                            while (entries2.hasNext()) {
                                                Map.Entry entry2 = (Map.Entry) entries2.next();
                                                String key2 = (String)entry2.getKey();
                                                /*Object value2 = entry2.getValue();
                                                if(value2==null){
                                                    value2=0;//设置一个默认值
                                                }
                                                String value1String=value2.toString();//先把值设置成字符串类型扔回去*/
                                                if(monitoringPointTypeYG==1){
                                                    if(key2.contains("TO_")&&key2.length()==7){//判断获取到自己所需要的折线图数据
                                                        Object value2 = entry2.getValue();
                                                        if(value2==null||value2.toString().equals("")){
                                                            indexValue++;
                                                            continue;
                                                            //value2=0;//设置一个默认值
                                                        }
                                                        String value1String=value2.toString();//先把值设置成字符串类型扔回去
                                                        String toMonth=key2.split("_")[1];//获取到月份的具体数字
                                                        mDataset.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                        /*if(indexValue<=32){
                                                            mDatasetOne.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                            indexValue++;
                                                        }else if(indexValue>32&&indexValue<=64){
                                                            mDatasetTwo.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                            indexValue++;
                                                        }else if(indexValue>64&&indexValue<=96){
                                                            mDatasetThree.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                            indexValue++;
                                                        }else if(indexValue>96&&indexValue<=128){
                                                            mDatasetOne.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                            indexValue++;
                                                        }else if(indexValue>128&&indexValue<=160){
                                                            mDatasetTwo.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                            indexValue++;
                                                        }else if(indexValue>160&&indexValue<=192){
                                                            mDatasetThree.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                            indexValue++;
                                                        }else if(indexValue>192&&indexValue<=224){
                                                            mDatasetOne.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                            indexValue++;
                                                        }else if(indexValue>224&&indexValue<=256){
                                                            mDatasetTwo.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                            indexValue++;
                                                        }else if(indexValue>256&&indexValue<=288){
                                                            mDatasetThree.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                            indexValue++;
                                                        }*/
                                                        /*monitoringReportObject.put("value",value1String);//Y轴
                                                        monitoringReportObject.put("rowKey",Tj_Type_Str);//图例
                                                        monitoringReportObject.put("columnKey",toMonth);//Y轴
                                                        monitoringReportList.add(monitoringReportObject);
                                                        monitoringReportObject=new HashMap();*/
                                                    }
                                                }else if(monitoringPointTypeYG==3){
                                                    if(key2.contains("AX_")||key2.contains("BX_")||key2.contains("CX_") ){//判断获取到自己所需要的折线图数据
                                                        if(key2.length()==7) {//必须在做一个判断否则平均值，最高值，最低值也进来了，因为那也是以TO_,AX_,BX_,CX_打头的
                                                            Object value2 = entry2.getValue();
                                                            if(value2==null||value2.toString().equals("")){
                                                                indexValue++;
                                                                continue;
                                                                //value2=0;//设置一个默认值
                                                            }
                                                            String value1String=value2.toString();//先把值设置成字符串类型扔回去
                                                            String toMonth = key2.split("_")[1];//获取到月份的具体数字
                                                            mDataset.addValue(Float.parseFloat(value1String), Tj_Type_Str+key2.split("_")[0], toMonth);
                                                            /*if(indexValue<=32){
                                                                mDatasetOne.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                                indexValue++;
                                                            }else if(indexValue>32&&indexValue<=64){
                                                                mDatasetTwo.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                                indexValue++;
                                                            }else if(indexValue>64&&indexValue<=96){
                                                                mDatasetThree.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                                indexValue++;
                                                            }else if(indexValue>96&&indexValue<=128){
                                                                mDatasetOne.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                                indexValue++;
                                                            }else if(indexValue>128&&indexValue<=160){
                                                                mDatasetTwo.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                                indexValue++;
                                                            }else if(indexValue>160&&indexValue<=192){
                                                                mDatasetThree.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                                indexValue++;
                                                            }else if(indexValue>192&&indexValue<=224){
                                                                mDatasetOne.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                                indexValue++;
                                                            }else if(indexValue>224&&indexValue<=256){
                                                                mDatasetTwo.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                                indexValue++;
                                                            }else if(indexValue>256&&indexValue<=288){
                                                                mDatasetThree.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                                indexValue++;
                                                            }*/
                                                           /* monitoringReportObject.put("value", value1String);//Y轴
                                                            monitoringReportObject.put("rowKey", Tj_Type_Str + key2.split("_")[0]);//图例
                                                            monitoringReportObject.put("columnKey", toMonth);//Y轴
                                                            monitoringReportList.add(monitoringReportObject);
                                                            monitoringReportObject = new HashMap();*/
                                                        }
                                                    }
                                                }
                                                // System.out.println("Key = " + key + ", Value = " + value);
                                            }
                                            List<Map> imgNames=new ArrayList<Map>();
                                            String imgName= JavaUUIDGenerator.getUUID();
                                            String savePath=tomcatImgPath+ imgName+".png";
                                            Line.getDataToFileAndSaveTOThisTomcat(mDataset,savePath,1024,420);
                                            Map imgObject = new HashMap();
                                            imgObject.put("imgName",imgName);
                                            imgObject.put("monitoringPointNameNew",monitoringPointNameNew+"-"+Tj_Type_Str);
                                            imgObject.put("imgFullName",imgName+".png");
                                            imgNames.add(imgObject);
                                            /*for(int iii=1;iii<=3;iii++){
                                                String imgName= JavaUUIDGenerator.getUUID();
                                                String savePath=tomcatImgPath+ imgName+".png";
                                                if(iii==1){
                                                    if(mDatasetOne.getColumnCount()==0){
                                                        continue;
                                                    }
                                                    Line.getDataToFileAndSaveTOThisTomcat(mDatasetOne,savePath,1024,420);
                                                }else if(iii==2){
                                                    if(mDatasetTwo.getColumnCount()==0){
                                                        continue;
                                                    }
                                                    Line.getDataToFileAndSaveTOThisTomcat(mDatasetTwo,savePath,1024,420);
                                                }else if(iii==3){
                                                    if(mDatasetThree.getColumnCount()==0){
                                                        continue;
                                                    }
                                                    Line.getDataToFileAndSaveTOThisTomcat(mDatasetThree,savePath,1024,420);
                                                }
                                                Map imgObject = new HashMap();
                                                imgObject.put("imgName",imgName);
                                                imgObject.put("monitoringPointNameNew",monitoringPointNameNew+"-"+Tj_Type_Str);
                                                imgObject.put("imgFullName",imgName+".png");
                                                imgNames.add(imgObject);
                                            }*/
                                            monitoringObject.put("monitoringReportList",imgNames);//把保存成功之后的图片名称和格式放回到列表中返回去
                                            //monitoringObject.put("monitoringReportList",monitoringReportList);//把当前监测点折线图扔回到监测点对象中
                                            monitoringReportList=new ArrayList<Map>();//重置折线图的列表对象
                                            monitoringList.add(monitoringObject);
                                            monitoringObject=new HashMap();//当前监测点执行完成重新初始化
                                        }
                                    }
                                    //处理温度
                                    for(Map map11:listMonitoringWD){
                                        String monitoringPointCoreIdWD = (String) map11.get("monitoringPointCoreId");
                                        if(monitoringPointCoreIdWD.equals(monitoringPointCoreIdNew)) {//判断电流计量点id和当前循环的计量点id是否相等
                                            Integer monitoringPointTypeWD       = (Integer) map11.get("monitoringPointType");
                                            Object TO_AVG         = map11.get("TO_AVG")         ;
                                            Object TO_MIN         = map11.get("TO_MIN")         ;
                                            Object TO_MIN_DATA_TAG= map11.get("TO_MIN_DATA_TAG");
                                            Object TO_MIN_TIME_TAG= map11.get("TO_MIN_TIME_TAG");
                                            Object TO_MAX         = map11.get("TO_MAX")         ;
                                            Object TO_MAX_DATA_TAG= map11.get("TO_MAX_DATA_TAG");
                                            Object TO_MAX_TIME_TAG= map11.get("TO_MAX_TIME_TAG");
                                            Object AX_AVG         = map11.get("AX_AVG")         ;
                                            Object AX_MIN         = map11.get("AX_MIN")         ;
                                            Object AX_MIN_DATA_TAG= map11.get("AX_MIN_DATA_TAG");
                                            Object AX_MIN_TIME_TAG= map11.get("AX_MIN_TIME_TAG");
                                            Object AX_MAX         = map11.get("AX_MAX")         ;
                                            Object AX_MAX_DATA_TAG= map11.get("AX_MAX_DATA_TAG");
                                            Object AX_MAX_TIME_TAG= map11.get("AX_MAX_TIME_TAG");
                                            Object BX_AVG         = map11.get("BX_AVG")         ;
                                            Object BX_MIN         = map11.get("BX_MIN")         ;
                                            Object BX_MIN_DATA_TAG= map11.get("BX_MIN_DATA_TAG");
                                            Object BX_MIN_TIME_TAG= map11.get("BX_MIN_TIME_TAG");
                                            Object BX_MAX         = map11.get("BX_MAX")         ;
                                            Object BX_MAX_DATA_TAG= map11.get("BX_MAX_DATA_TAG");
                                            Object BX_MAX_TIME_TAG= map11.get("BX_MAX_TIME_TAG");
                                            Object CX_AVG         = map11.get("CX_AVG")         ;
                                            Object CX_MIN         = map11.get("CX_MIN")         ;
                                            Object CX_MIN_DATA_TAG= map11.get("CX_MIN_DATA_TAG");
                                            Object CX_MIN_TIME_TAG= map11.get("CX_MIN_TIME_TAG");
                                            Object CX_MAX         = map11.get("CX_MAX")         ;
                                            Object CX_MAX_DATA_TAG= map11.get("CX_MAX_DATA_TAG");
                                            Object CX_MAX_TIME_TAG= map11.get("CX_MAX_TIME_TAG");
                                            String Tj_Type_Str="温度";
                                            if(monitoringPointTypeWD==11||monitoringPointTypeWD==14){//温度单相
                                                yunxingjieguo=dateString+"，"+monitoringPointNameNew+Tj_Type_Str+"的平均值为："+TO_AVG+"；最高值为："+TO_MAX+"("+format6Parse(format5Parse(TO_MAX_DATA_TAG+" "+TO_MAX_TIME_TAG))+")"+"；最低值为："+TO_MIN+"("+format6Parse(format5Parse(TO_MIN_DATA_TAG+" "+TO_MIN_TIME_TAG))+")";
                                            }else if(monitoringPointTypeWD==13){//温度三相
                                                yunxingjieguo=dateString+"，"+monitoringPointNameNew
                                                        +Tj_Type_Str+"A相的平均值为："+AX_AVG+"；最高值为："+AX_MAX+"("+format6Parse(format5Parse(AX_MAX_DATA_TAG+" "+AX_MAX_TIME_TAG))+")"+"；最低值为："+AX_MIN+"("+format6Parse(format5Parse(AX_MIN_DATA_TAG+" "+AX_MIN_TIME_TAG))+")"+"。"
                                                        +Tj_Type_Str+"B相的平均值为："+BX_AVG+"；最高值为："+BX_MAX+"("+format6Parse(format5Parse(BX_MAX_DATA_TAG+" "+BX_MAX_TIME_TAG))+")"+"；最低值为："+BX_MIN+"("+format6Parse(format5Parse(BX_MIN_DATA_TAG+" "+BX_MIN_TIME_TAG))+")"+"。"
                                                        +Tj_Type_Str+"C相的平均值为："+CX_AVG+"；最高值为："+CX_MAX+"("+format6Parse(format5Parse(CX_MAX_DATA_TAG+" "+CX_MAX_TIME_TAG))+")"+"；最低值为："+CX_MIN+"("+format6Parse(format5Parse(CX_MIN_DATA_TAG+" "+CX_MIN_TIME_TAG))+")"+"。";
                                            }
                                            yunxingxiang=Tj_Type_Str.toString();
                                            monitoringObject.put("yunxingxiang",yunxingxiang);//运行项
                                            monitoringObject.put("yunxingjieguo",yunxingjieguo);//运行结果
                                            //开始初始化折线图列表数据,遍历当前的最大的map1拿到折线图要展现的数据
                                            Iterator entries2 = map11.entrySet().iterator();
                                            DefaultCategoryDataset mDataset = new DefaultCategoryDataset();
                                            //96个点太多了想折成三张32个点的折线图
                                            /*DefaultCategoryDataset mDatasetOne = new DefaultCategoryDataset();
                                            DefaultCategoryDataset mDatasetTwo = new DefaultCategoryDataset();
                                            DefaultCategoryDataset mDatasetThree = new DefaultCategoryDataset();*/
                                            Integer indexValue=0;//记录放进折线图数据的总数
                                            while (entries2.hasNext()) {
                                                Map.Entry entry2 = (Map.Entry) entries2.next();
                                                String key2 = (String)entry2.getKey();
                                               /* Object value2 = entry2.getValue();
                                                if(value2==null){
                                                    value2=0;//设置一个默认值
                                                }
                                                String value1String=value2.toString();//先把值设置成字符串类型扔回去*/
                                                if(monitoringPointTypeWD==11||monitoringPointTypeWD==14){
                                                    if(key2.contains("TO_")&&key2.length()==7){//判断获取到自己所需要的折线图数据
                                                        Object value2 = entry2.getValue();
                                                        if(value2==null||value2.toString().equals("")){
                                                            indexValue++;
                                                            continue;
                                                            //value2=0;//设置一个默认值
                                                        }
                                                        String value1String=value2.toString();//先把值设置成字符串类型扔回去
                                                        String toMonth=key2.split("_")[1];//获取到月份的具体数字
                                                        mDataset.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                        /*if(indexValue<=32){
                                                            mDatasetOne.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                            indexValue++;
                                                        }else if(indexValue>32&&indexValue<=64){
                                                            mDatasetTwo.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                            indexValue++;
                                                        }else if(indexValue>64&&indexValue<=96){
                                                            mDatasetThree.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                            indexValue++;
                                                        }else if(indexValue>96&&indexValue<=128){
                                                            mDatasetOne.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                            indexValue++;
                                                        }else if(indexValue>128&&indexValue<=160){
                                                            mDatasetTwo.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                            indexValue++;
                                                        }else if(indexValue>160&&indexValue<=192){
                                                            mDatasetThree.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                            indexValue++;
                                                        }else if(indexValue>192&&indexValue<=224){
                                                            mDatasetOne.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                            indexValue++;
                                                        }else if(indexValue>224&&indexValue<=256){
                                                            mDatasetTwo.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                            indexValue++;
                                                        }else if(indexValue>256&&indexValue<=288){
                                                            mDatasetThree.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                            indexValue++;
                                                        }*/
                                                        /*monitoringReportObject.put("value",value1String);//Y轴
                                                        monitoringReportObject.put("rowKey",Tj_Type_Str);//图例
                                                        monitoringReportObject.put("columnKey",toMonth);//Y轴
                                                        monitoringReportList.add(monitoringReportObject);
                                                        monitoringReportObject=new HashMap();*/
                                                    }
                                                }else if(monitoringPointTypeWD==13){
                                                    if(key2.contains("AX_")||key2.contains("BX_")||key2.contains("CX_") ){//判断获取到自己所需要的折线图数据
                                                        if(key2.length()==7) {//必须在做一个判断否则平均值，最高值，最低值也进来了，因为那也是以TO_,AX_,BX_,CX_打头的
                                                            Object value2 = entry2.getValue();
                                                            if(value2==null||value2.toString().equals("")){
                                                                indexValue++;
                                                                continue;
                                                                //value2=0;//设置一个默认值
                                                            }
                                                            String value1String=value2.toString();//先把值设置成字符串类型扔回去
                                                            String toMonth = key2.split("_")[1];//获取到月份的具体数字
                                                            mDataset.addValue(Float.parseFloat(value1String), Tj_Type_Str+key2.split("_")[0], toMonth);
                                                            /*if(indexValue<=32){
                                                                mDatasetOne.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                                indexValue++;
                                                            }else if(indexValue>32&&indexValue<=64){
                                                                mDatasetTwo.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                                indexValue++;
                                                            }else if(indexValue>64&&indexValue<=96){
                                                                mDatasetThree.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                                indexValue++;
                                                            }else if(indexValue>96&&indexValue<=128){
                                                                mDatasetOne.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                                indexValue++;
                                                            }else if(indexValue>128&&indexValue<=160){
                                                                mDatasetTwo.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                                indexValue++;
                                                            }else if(indexValue>160&&indexValue<=192){
                                                                mDatasetThree.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                                indexValue++;
                                                            }else if(indexValue>192&&indexValue<=224){
                                                                mDatasetOne.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                                indexValue++;
                                                            }else if(indexValue>224&&indexValue<=256){
                                                                mDatasetTwo.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                                indexValue++;
                                                            }else if(indexValue>256&&indexValue<=288){
                                                                mDatasetThree.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                                indexValue++;
                                                            }*/
                                                            /*monitoringReportObject.put("value", value1String);//Y轴
                                                            monitoringReportObject.put("rowKey", Tj_Type_Str + key2.split("_")[0]);//图例
                                                            monitoringReportObject.put("columnKey", toMonth);//Y轴
                                                            monitoringReportList.add(monitoringReportObject);
                                                            monitoringReportObject = new HashMap();*/
                                                        }
                                                    }
                                                }
                                                // System.out.println("Key = " + key + ", Value = " + value);
                                            }
                                            List<Map> imgNames=new ArrayList<Map>();
                                            String imgName= JavaUUIDGenerator.getUUID();
                                            String savePath=tomcatImgPath+ imgName+".png";
                                            Line.getDataToFileAndSaveTOThisTomcat(mDataset,savePath,1024,420);
                                            Map imgObject = new HashMap();
                                            imgObject.put("imgName",imgName);
                                            imgObject.put("monitoringPointNameNew",monitoringPointNameNew+"-"+Tj_Type_Str);
                                            imgObject.put("imgFullName",imgName+".png");
                                            imgNames.add(imgObject);
                                            /*for(int iii=1;iii<=3;iii++){
                                                String imgName= JavaUUIDGenerator.getUUID();
                                                String savePath=tomcatImgPath+ imgName+".png";
                                                if(iii==1){
                                                    if(mDatasetOne.getColumnCount()==0){
                                                        continue;
                                                    }
                                                    Line.getDataToFileAndSaveTOThisTomcat(mDatasetOne,savePath,1024,420);
                                                }else if(iii==2){
                                                    if(mDatasetTwo.getColumnCount()==0){
                                                        continue;
                                                    }
                                                    Line.getDataToFileAndSaveTOThisTomcat(mDatasetTwo,savePath,1024,420);
                                                }else if(iii==3){
                                                    if(mDatasetThree.getColumnCount()==0){
                                                        continue;
                                                    }
                                                    Line.getDataToFileAndSaveTOThisTomcat(mDatasetThree,savePath,1024,420);
                                                }
                                                Map imgObject = new HashMap();
                                                imgObject.put("imgName",imgName);
                                                imgObject.put("monitoringPointNameNew",monitoringPointNameNew+"-"+Tj_Type_Str);
                                                imgObject.put("imgFullName",imgName+".png");
                                                imgNames.add(imgObject);
                                            }*/
                                            monitoringObject.put("monitoringReportList",imgNames);//把保存成功之后的图片名称和格式放回到列表中返回去
                                            //monitoringObject.put("monitoringReportList",monitoringReportList);//把当前监测点折线图扔回到监测点对象中
                                            monitoringReportList=new ArrayList<Map>();//重置折线图的列表对象
                                            monitoringList.add(monitoringObject);
                                            monitoringObject=new HashMap();//当前监测点执行完成重新初始化
                                        }
                                    }
                                    //处理功率因数
                                    for(Map map11:listMonitoringYS){
                                        String monitoringPointCoreIdYS = (String) map11.get("monitoringPointCoreId");
                                        if(monitoringPointCoreIdYS.equals(monitoringPointCoreIdNew)) {//判断电流计量点id和当前循环的计量点id是否相等
                                            Integer monitoringPointTypeYS       = (Integer) map11.get("monitoringPointType");
                                            Object TO_AVG         = map11.get("TO_AVG")         ;
                                            Object TO_MIN         = map11.get("TO_MIN")         ;
                                            Object TO_MIN_DATA_TAG= map11.get("TO_MIN_DATA_TAG");
                                            Object TO_MIN_TIME_TAG= map11.get("TO_MIN_TIME_TAG");
                                            Object TO_MAX         = map11.get("TO_MAX")         ;
                                            Object TO_MAX_DATA_TAG= map11.get("TO_MAX_DATA_TAG");
                                            Object TO_MAX_TIME_TAG= map11.get("TO_MAX_TIME_TAG");
                                            Object AX_AVG         = map11.get("AX_AVG")         ;
                                            Object AX_MIN         = map11.get("AX_MIN")         ;
                                            Object AX_MIN_DATA_TAG= map11.get("AX_MIN_DATA_TAG");
                                            Object AX_MIN_TIME_TAG= map11.get("AX_MIN_TIME_TAG");
                                            Object AX_MAX         = map11.get("AX_MAX")         ;
                                            Object AX_MAX_DATA_TAG= map11.get("AX_MAX_DATA_TAG");
                                            Object AX_MAX_TIME_TAG= map11.get("AX_MAX_TIME_TAG");
                                            Object BX_AVG         = map11.get("BX_AVG")         ;
                                            Object BX_MIN         = map11.get("BX_MIN")         ;
                                            Object BX_MIN_DATA_TAG= map11.get("BX_MIN_DATA_TAG");
                                            Object BX_MIN_TIME_TAG= map11.get("BX_MIN_TIME_TAG");
                                            Object BX_MAX         = map11.get("BX_MAX")         ;
                                            Object BX_MAX_DATA_TAG= map11.get("BX_MAX_DATA_TAG");
                                            Object BX_MAX_TIME_TAG= map11.get("BX_MAX_TIME_TAG");
                                            Object CX_AVG         = map11.get("CX_AVG")         ;
                                            Object CX_MIN         = map11.get("CX_MIN")         ;
                                            Object CX_MIN_DATA_TAG= map11.get("CX_MIN_DATA_TAG");
                                            Object CX_MIN_TIME_TAG= map11.get("CX_MIN_TIME_TAG");
                                            Object CX_MAX         = map11.get("CX_MAX")         ;
                                            Object CX_MAX_DATA_TAG= map11.get("CX_MAX_DATA_TAG");
                                            Object CX_MAX_TIME_TAG= map11.get("CX_MAX_TIME_TAG");
                                            String Tj_Type_Str="温度";
                                            if(monitoringPointTypeYS==1){//温度单相
                                                yunxingjieguo=dateString+"，"+monitoringPointNameNew+Tj_Type_Str+"的平均值为："+TO_AVG+"；最高值为："+TO_MAX+"("+format6Parse(format5Parse(TO_MAX_DATA_TAG+" "+TO_MAX_TIME_TAG))+")"+"；最低值为："+TO_MIN+"("+format6Parse(format5Parse(TO_MIN_DATA_TAG+" "+TO_MIN_TIME_TAG))+")";
                                            }else if(monitoringPointTypeYS==3){//温度三相
                                                yunxingjieguo=dateString+"，"+monitoringPointNameNew
                                                        +Tj_Type_Str+"A相的平均值为："+AX_AVG+"；最高值为："+AX_MAX+"("+format6Parse(format5Parse(AX_MAX_DATA_TAG+" "+AX_MAX_TIME_TAG))+")"+"；最低值为："+AX_MIN+"("+format6Parse(format5Parse(AX_MIN_DATA_TAG+" "+AX_MIN_TIME_TAG))+")"+"。"
                                                        +Tj_Type_Str+"B相的平均值为："+BX_AVG+"；最高值为："+BX_MAX+"("+format6Parse(format5Parse(BX_MAX_DATA_TAG+" "+BX_MAX_TIME_TAG))+")"+"；最低值为："+BX_MIN+"("+format6Parse(format5Parse(BX_MIN_DATA_TAG+" "+BX_MIN_TIME_TAG))+")"+"。"
                                                        +Tj_Type_Str+"C相的平均值为："+CX_AVG+"；最高值为："+CX_MAX+"("+format6Parse(format5Parse(CX_MAX_DATA_TAG+" "+CX_MAX_TIME_TAG))+")"+"；最低值为："+CX_MIN+"("+format6Parse(format5Parse(CX_MIN_DATA_TAG+" "+CX_MIN_TIME_TAG))+")"+"。";
                                            }
                                            yunxingxiang=Tj_Type_Str.toString();
                                            monitoringObject.put("yunxingxiang",yunxingxiang);//运行项
                                            monitoringObject.put("yunxingjieguo",yunxingjieguo);//运行结果
                                            //开始初始化折线图列表数据,遍历当前的最大的map1拿到折线图要展现的数据
                                            Iterator entries2 = map11.entrySet().iterator();
                                            DefaultCategoryDataset mDataset = new DefaultCategoryDataset();
                                            //96个点太多了想折成三张32个点的折线图
                                            /*DefaultCategoryDataset mDatasetOne = new DefaultCategoryDataset();
                                            DefaultCategoryDataset mDatasetTwo = new DefaultCategoryDataset();
                                            DefaultCategoryDataset mDatasetThree = new DefaultCategoryDataset();*/
                                            Integer indexValue=0;//记录放进折线图数据的总数
                                            while (entries2.hasNext()) {
                                                Map.Entry entry2 = (Map.Entry) entries2.next();
                                                String key2 = (String)entry2.getKey();
                                               /* Object value2 = entry2.getValue();
                                                if(value2==null){
                                                    value2=0;//设置一个默认值
                                                }
                                                String value1String=value2.toString();//先把值设置成字符串类型扔回去*/
                                                if(monitoringPointTypeYS==1){
                                                    if(key2.contains("TO_")&&key2.length()==7){//判断获取到自己所需要的折线图数据
                                                        Object value2 = entry2.getValue();
                                                        if(value2==null||value2.toString().equals("")){
                                                            indexValue++;
                                                            continue;
                                                            //value2=0;//设置一个默认值
                                                        }
                                                        String value1String=value2.toString();//先把值设置成字符串类型扔回去
                                                        String toMonth=key2.split("_")[1];//获取到月份的具体数字
                                                        mDataset.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                        /*if(indexValue<=32){
                                                            mDatasetOne.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                            indexValue++;
                                                        }else if(indexValue>32&&indexValue<=64){
                                                            mDatasetTwo.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                            indexValue++;
                                                        }else if(indexValue>64&&indexValue<=96){
                                                            mDatasetThree.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                            indexValue++;
                                                        }else if(indexValue>96&&indexValue<=128){
                                                            mDatasetOne.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                            indexValue++;
                                                        }else if(indexValue>128&&indexValue<=160){
                                                            mDatasetTwo.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                            indexValue++;
                                                        }else if(indexValue>160&&indexValue<=192){
                                                            mDatasetThree.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                            indexValue++;
                                                        }else if(indexValue>192&&indexValue<=224){
                                                            mDatasetOne.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                            indexValue++;
                                                        }else if(indexValue>224&&indexValue<=256){
                                                            mDatasetTwo.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                            indexValue++;
                                                        }else if(indexValue>256&&indexValue<=288){
                                                            mDatasetThree.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                            indexValue++;
                                                        }*/
                                                        /*monitoringReportObject.put("value",value1String);//Y轴
                                                        monitoringReportObject.put("rowKey",Tj_Type_Str);//图例
                                                        monitoringReportObject.put("columnKey",toMonth);//Y轴
                                                        monitoringReportList.add(monitoringReportObject);
                                                        monitoringReportObject=new HashMap();*/
                                                    }
                                                }else if(monitoringPointTypeYS==3){
                                                    if(key2.contains("AX_")||key2.contains("BX_")||key2.contains("CX_") ){//判断获取到自己所需要的折线图数据
                                                        if(key2.length()==7) {//必须在做一个判断否则平均值，最高值，最低值也进来了，因为那也是以TO_,AX_,BX_,CX_打头的
                                                            Object value2 = entry2.getValue();
                                                            if(value2==null||value2.toString().equals("")){
                                                                indexValue++;
                                                                continue;
                                                                //value2=0;//设置一个默认值
                                                            }
                                                            String value1String=value2.toString();//先把值设置成字符串类型扔回去
                                                            String toMonth = key2.split("_")[1];//获取到月份的具体数字
                                                            mDataset.addValue(Float.parseFloat(value1String), Tj_Type_Str+key2.split("_")[0], toMonth);
                                                            /*if(indexValue<=32){
                                                                mDatasetOne.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                                indexValue++;
                                                            }else if(indexValue>32&&indexValue<=64){
                                                                mDatasetTwo.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                                indexValue++;
                                                            }else if(indexValue>64&&indexValue<=96){
                                                                mDatasetThree.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                                indexValue++;
                                                            }else if(indexValue>96&&indexValue<=128){
                                                                mDatasetOne.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                                indexValue++;
                                                            }else if(indexValue>128&&indexValue<=160){
                                                                mDatasetTwo.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                                indexValue++;
                                                            }else if(indexValue>160&&indexValue<=192){
                                                                mDatasetThree.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                                indexValue++;
                                                            }else if(indexValue>192&&indexValue<=224){
                                                                mDatasetOne.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                                indexValue++;
                                                            }else if(indexValue>224&&indexValue<=256){
                                                                mDatasetTwo.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                                indexValue++;
                                                            }else if(indexValue>256&&indexValue<=288){
                                                                mDatasetThree.addValue(Float.parseFloat(value1String), Tj_Type_Str, toMonth);
                                                                indexValue++;
                                                            }*/
                                                            /*monitoringReportObject.put("value", value1String);//Y轴
                                                            monitoringReportObject.put("rowKey", Tj_Type_Str + key2.split("_")[0]);//图例
                                                            monitoringReportObject.put("columnKey", toMonth);//Y轴
                                                            monitoringReportList.add(monitoringReportObject);
                                                            monitoringReportObject = new HashMap();*/
                                                        }
                                                    }
                                                }
                                                // System.out.println("Key = " + key + ", Value = " + value);
                                            }
                                            List<Map> imgNames=new ArrayList<Map>();
                                            String imgName= JavaUUIDGenerator.getUUID();
                                            String savePath=tomcatImgPath+ imgName+".png";
                                            Line.getDataToFileAndSaveTOThisTomcat(mDataset,savePath,1024,420);
                                            Map imgObject = new HashMap();
                                            imgObject.put("imgName",imgName);
                                            imgObject.put("monitoringPointNameNew",monitoringPointNameNew+"-"+Tj_Type_Str);
                                            imgObject.put("imgFullName",imgName+".png");
                                            imgNames.add(imgObject);
                                           /* for(int iii=1;iii<=3;iii++){
                                                String imgName= JavaUUIDGenerator.getUUID();
                                                String savePath=tomcatImgPath+ imgName+".png";
                                                if(iii==1){
                                                    if(mDatasetOne.getColumnCount()==0){
                                                        continue;
                                                    }
                                                    Line.getDataToFileAndSaveTOThisTomcat(mDatasetOne,savePath,1024,420);
                                                }else if(iii==2){
                                                    if(mDatasetTwo.getColumnCount()==0){
                                                        continue;
                                                    }
                                                    Line.getDataToFileAndSaveTOThisTomcat(mDatasetTwo,savePath,1024,420);
                                                }else if(iii==3){
                                                    if(mDatasetThree.getColumnCount()==0){
                                                        continue;
                                                    }
                                                    Line.getDataToFileAndSaveTOThisTomcat(mDatasetThree,savePath,1024,420);
                                                }
                                                Map imgObject = new HashMap();
                                                imgObject.put("imgName",imgName);
                                                imgObject.put("monitoringPointNameNew",monitoringPointNameNew+"-"+Tj_Type_Str);
                                                imgObject.put("imgFullName",imgName+".png");
                                                imgNames.add(imgObject);
                                            }*/
                                            monitoringObject.put("monitoringReportList",imgNames);//把保存成功之后的图片名称和格式放回到列表中返回去
                                            //monitoringObject.put("monitoringReportList",monitoringReportList);//把当前监测点折线图扔回到监测点对象中
                                            monitoringReportList=new ArrayList<Map>();//重置折线图的列表对象
                                            monitoringList.add(monitoringObject);
                                            monitoringObject=new HashMap();//当前监测点执行完成重新初始化
                                        }
                                    }
                                    sameMonitoringObject.put("monitoringList",monitoringList);//把计量点列表仍回到共有计量点大对象中
                                    sameMonitoringList.add(sameMonitoringObject);//把共有计量点大对象扔回到共有计量点大列表中
                                    monitoringList=new ArrayList<Map>();//初始化计量点列表
                                    sameMonitoringObject=new HashMap();//初始化共有计量点大对象
                                    /*********************************处理计量点对象信息结束**************************************/
                                }//判断计量点所属电房是否是当前电房
                            }//遍历循环所有的根据企业或者电房查找出来的计量点
                            substationMap.put("sameMonitoringList",sameMonitoringList);//把共有的计量点大列表放回到电房对象中
                            substationList.add(substationMap);//把电房对象扔回到电房列表中
                            monitoringList=new ArrayList<Map>();//初始化计量点列表
                            sameMonitoringObject=new HashMap();//初始化共有计量点大对象
                            sameMonitoringList=new ArrayList<Map>();//初始化共有计量点列表大对象
                            substationMap=new HashMap();//初始化电房对象
                        }
                        detailDTO.setDetail(substationList);
                    }else { //查询每月的表
                        String nowDay=format1.format(new Date());//获取现在时间的年月日
                        String tableName = "jc_tj_yf_" + ynBaseStaffCompanyId;
                        onLineMonitoringDTO.setTableNameString(tableName);
                        String dateString =onLineMonitoringDTO.getYear()+onLineMonitoringDTO.getMonth();
                        onLineMonitoringDTO.setDateString(dateString);
                        //获取月份计量点每个月的各种告警信息
                        OnLineMonitoringDTO onLineMonitoringDTOGJ=new OnLineMonitoringDTO();
                        onLineMonitoringDTOGJ.setTableNameString("jc_gj_"+ynBaseStaffCompanyId);
                        onLineMonitoringDTOGJ.setYear(onLineMonitoringDTO.getYear());
                        onLineMonitoringDTOGJ.setMonth(onLineMonitoringDTO.getMonth());
                        onLineMonitoringDTOGJ.setDay(onLineMonitoringDTO.getDay());
                        onLineMonitoringDTOGJ.setDateString(dateString);
                        onLineMonitoringDTOGJ.setYnBaseCustomerEnterpriseLongId(onLineMonitoringDTO.getYnBaseCustomerEnterpriseLongId());
                        onLineMonitoringDTOGJ.setYnBaseEnterpriseSubstationLongId(onLineMonitoringDTO.getYnBaseEnterpriseSubstationLongId());
                        List<LinkedHashMap> monitoringWarningList=ynMonitoringInitMapper.getMonitoringWarningListByObject(onLineMonitoringDTOGJ);
                        //获取月份计量点每个月的各种参数信息
                        List<LinkedHashMap> monitoringMsgFromMonthList=ynMonitoringInitMapper.selectMonitoringTJYFByObject(onLineMonitoringDTO);
                        List<Map> substationList=new ArrayList<Map>();//电房列表
                        Map substationMap =new HashMap();//电房对象
                        List<Map> sameMonitoringList=new ArrayList<Map>();//存放相同监测点id的总结
                        Map sameMonitoringObject= new HashMap();//这个大对象里面带着计量点的列表和计量点的共有名称
                        List<Map> warningSubstations=new ArrayList<Map>();//电房的所有计量点告警列表
                        List<Map> monitoringList=new ArrayList<Map>();//存放监测点的list
                        Map monitoringObject=new HashMap();//监测点对象，每个监测点有监控类型名称，运行结果描述，曲线图
                        List<Map> monitoringReportList=new ArrayList<Map>();//监测点报告列表
                        Map monitoringReportObject=new HashMap();//监测点报告对象
                        String oldYnBaseEnterpriseSubstationLongId=null;//前一个电房的id
                        String oldMonitoringPointCoreId = null;//前一个计量点的id
                        String shebeiyunxingneirong=null;//设备运行内容
                        String yunxingxiang=null;//运行项
                        String yunxingjieguo=null;//运行结果
                        Integer orderInt=0;//排序
                        List<Map> monitoringContentList=new ArrayList<Map>();//电房监测点内容列表
                        Integer equimentCount=0;//运行内容设备总数
                        Integer monitoringCount=0;//运行内容监测点总数
                        for(int i=0;i<monitoringMsgFromMonthList.size();i++){//for 循环开始
                            Map map1=monitoringMsgFromMonthList.get(i);
                            Integer yfDays           = (Integer) map1.get("YF_Days");//月份总天数
                            Object warningAutoIncrementId           = map1.get("idJC")           ;
                            Object monitoringPointAliasId    = map1.get("JCD_AliasID")    ;
                            Object monitoringPointCoreId     = map1.get("JCD_CoreID")     ;
                            Object JCD_id         = map1.get("JCD_id")         ;
                            Object monitoringPointName       = map1.get("JCD_Name")       ;
                            Integer monitoringPointType       = (Integer) map1.get("JCD_Type");
                            Object Tj_Type        = map1.get("Tj_Type")        ;
                            Object Tj_Type_Str        = map1.get("Tj_Type_Str")        ;
                            Object ynBaseEnterpriseSubstationLongId          = map1.get("DF_id")          ;
                            Object ynBaseEnterpriseSubstationName        = map1.get("DF_Name")        ;
                            Object ynBaseEquipmentLongId        = map1.get("SBTZ_id")        ;
                            Object ynBaseEquipmentLongName      = map1.get("SBTZ_Name")      ;
                            Object ynBaseCustomerEnterpriseLongId        = map1.get("YDQY_id")        ;
                            Object ynBaseCustomerEnterpriseName      = map1.get("YDQY_Name")      ;
                            Object ynBaseStaffCompanyId1        = map1.get("WBGS_id")        ;
                            Object ynBaseStaffCompanyName      = map1.get("WBGS_Name")      ;
                            Object createDate     = map1.get("CreateDate")     ;
                            Object dataDate       = map1.get("DataDate")       ;
                            Object TO_AVG         = map1.get("TO_AVG")         ;
                            Object TO_MIN         = map1.get("TO_MIN")         ;
                            Object TO_MIN_DATA_TAG= map1.get("TO_MIN_DATA_TAG");
                            Object TO_MIN_TIME_TAG= map1.get("TO_MIN_TIME_TAG");
                            Object TO_MAX         = map1.get("TO_MAX")         ;
                            Object TO_MAX_DATA_TAG= map1.get("TO_MAX_DATA_TAG");
                            Object TO_MAX_TIME_TAG= map1.get("TO_MAX_TIME_TAG");
                            Object AX_AVG         = map1.get("AX_AVG")         ;
                            Object AX_MIN         = map1.get("AX_MIN")         ;
                            Object AX_MIN_DATA_TAG= map1.get("AX_MIN_DATA_TAG");
                            Object AX_MIN_TIME_TAG= map1.get("AX_MIN_TIME_TAG");
                            Object AX_MAX         = map1.get("AX_MAX")         ;
                            Object AX_MAX_DATA_TAG= map1.get("AX_MAX_DATA_TAG");
                            Object AX_MAX_TIME_TAG= map1.get("AX_MAX_TIME_TAG");
                            Object BX_AVG         = map1.get("BX_AVG")         ;
                            Object BX_MIN         = map1.get("BX_MIN")         ;
                            Object BX_MIN_DATA_TAG= map1.get("BX_MIN_DATA_TAG");
                            Object BX_MIN_TIME_TAG= map1.get("BX_MIN_TIME_TAG");
                            Object BX_MAX         = map1.get("BX_MAX")         ;
                            Object BX_MAX_DATA_TAG= map1.get("BX_MAX_DATA_TAG");
                            Object BX_MAX_TIME_TAG= map1.get("BX_MAX_TIME_TAG");
                            Object CX_AVG         = map1.get("CX_AVG")         ;
                            Object CX_MIN         = map1.get("CX_MIN")         ;
                            Object CX_MIN_DATA_TAG= map1.get("CX_MIN_DATA_TAG");
                            Object CX_MIN_TIME_TAG= map1.get("CX_MIN_TIME_TAG");
                            Object CX_MAX         = map1.get("CX_MAX")         ;
                            Object CX_MAX_DATA_TAG= map1.get("CX_MAX_DATA_TAG");
                            Object CX_MAX_TIME_TAG= map1.get("CX_MAX_TIME_TAG");
                            if(monitoringPointType==11){//单相温度
                                orderInt=1;
                            }else if(monitoringPointType==13){//三相温度
                                orderInt=2;
                            }else if(monitoringPointType==12){//湿度
                                orderInt=3;
                            }else{
                                orderInt=4;
                            }
                            if(i==0){//表示第一次
                                oldYnBaseEnterpriseSubstationLongId=  ynBaseEnterpriseSubstationLongId.toString();//前一个电房的id
                                oldMonitoringPointCoreId = monitoringPointCoreId.toString();//前一个计量点的id
                                /*********************************处理电房对象信息开始**************************************/
                                substationMap.put("ynBaseEnterpriseSubstationName",ynBaseEnterpriseSubstationName);//设置电房名称
                                String oldEquipmentContent=null;//listMonitoringMsg遍历训时候记录设备的前一个id
                                String oldMonitoringContent=null;//listMonitoringMsg遍历训时候记录监测点的前一个id
                                for(Map mapContent:listMonitoringMsg){//遍历循环企业下的所有的电房计量点信息，包括计量点名称，所属设备，监测点类型
                                    Object ynBaseEnterpriseSubstationLongIdContent=mapContent.get("ynBaseEnterpriseSubstationLongId");
                                    Object equipmentContent=mapContent.get("ynBaseEquipmentLongId");
                                    Object monitoringContent=mapContent.get("monitoringPointCoreId");
                                    if(ynBaseEnterpriseSubstationLongIdContent.toString().equals(ynBaseEnterpriseSubstationLongId.toString())){
                                        if(oldEquipmentContent==null){
                                            oldEquipmentContent=equipmentContent.toString();
                                            equimentCount++;//统计电房下设备个数
                                        }else if(!oldEquipmentContent.equals(equipmentContent)){
                                            oldEquipmentContent=equipmentContent.toString();
                                            equimentCount++;//统计电房下设备个数
                                        }
                                        if(oldMonitoringContent==null){
                                            oldMonitoringContent=monitoringContent.toString();
                                            monitoringCount++;//统计电房下计量点个数
                                        }else if(!oldMonitoringContent.equals(monitoringContent)){
                                            oldMonitoringContent=monitoringContent.toString();
                                            monitoringCount++;//统计电房下计量点个数
                                        }
                                        monitoringContentList.add(mapContent);
                                    }
                                    /*Object equipmentContent=mapContent.get("ynBaseEquipmentLongId");
                                    Object monitoringContent=mapContent.get("monitoringPointCoreId");
                                    if(oldEquipmentContent==null){
                                        oldEquipmentContent=equipmentContent.toString();
                                        equimentCount++;//统计电房下设备个数
                                    }else if(!oldEquipmentContent.equals(equipmentContent)){
                                        oldEquipmentContent=equipmentContent.toString();
                                        equimentCount++;//统计电房下设备个数
                                    }
                                    if(oldMonitoringContent==null){
                                        oldMonitoringContent=monitoringContent.toString();
                                        monitoringCount++;//统计电房下计量点个数
                                    }else if(!oldMonitoringContent.equals(monitoringContent)){
                                        oldMonitoringContent=monitoringContent.toString();
                                        monitoringCount++;//统计电房下计量点个数
                                    }
                                    Object ynBaseEnterpriseSubstationLongIdContent=mapContent.get("ynBaseEnterpriseSubstationLongId");
                                    if(ynBaseEnterpriseSubstationLongIdContent.toString().equals(ynBaseEnterpriseSubstationLongId.toString())){
                                        monitoringContentList.add(mapContent);
                                    }*/
                                }
                                substationMap.put("monitoringsContent",monitoringContentList);//把获得的当前电房下的监测点内容放回到电房对象中
                                shebeiyunxingneirong="本次设备运行报告时间"+dateString+"，累计对电房("+ynBaseEnterpriseSubstationName+")的"+equimentCount+"个大设备进行运行状态监测，"+monitoringCount+"个监测点，设备运行健康状态详情如下";
                                substationMap.put("shebeiyunxingneirong",shebeiyunxingneirong);//把设备运行内容扔回到电房对象表中
                                shebeiyunxingneirong=null;//重置设备运行内容
                                monitoringContentList=new ArrayList<Map>();//重置监测点内容列表
                                monitoringCount=0;//重置电房下计量点个数
                                equimentCount=0;//  重置电房下设备蛇叔
                                //处理电房告警信息
                                for(Map warningMap:monitoringWarningList){
                                    Object warningSubstaionId=warningMap.get("ynBaseEnterpriseSubstationLongId");
                                    if(ynBaseEnterpriseSubstationLongId.toString().equals(warningSubstaionId.toString())){
                                        warningSubstations.add(warningMap);
                                    }
                                }
                                substationMap.put("warningSubstation",warningSubstations);//把电房下面的所有计量点告警信息列表仍会到电房对象中
                                warningSubstations=new ArrayList<Map>();//电房告警列表初始化
                                /*********************************处理电房对象信息结束**************************************/
                                /*********************************处理计量点对象信息开始**************************************/
                                monitoringObject.put("orderInt",orderInt);//监测点的排序
                                sameMonitoringObject.put("monitoringPointName",monitoringPointName);//设置计量点公有名称
                                monitoringObject.put("monitoringPointName",monitoringPointName);//单相可以直接出计量点名称（最后决定把A相,B,C相合并所以计量点名称就直接使用就好）
                                if(monitoringPointType==1||monitoringPointType==11||monitoringPointType==12||monitoringPointType==14){//1单相,11温度单相，12湿度单相
                                    yunxingjieguo=dateString+"，"+monitoringPointName+Tj_Type_Str+"的平均值为："+TO_AVG+"；最高值为："+TO_MAX+"("+format6Parse(format5Parse(TO_MAX_DATA_TAG+" "+TO_MAX_TIME_TAG))+")"+"；最低值为："+TO_MIN+"("+format6Parse(format5Parse(TO_MIN_DATA_TAG+" "+TO_MIN_TIME_TAG))+")";
                                    yunxingxiang=Tj_Type_Str.toString();
                                    monitoringObject.put("yunxingxiang",yunxingxiang);//运行项
                                    monitoringObject.put("yunxingjieguo",yunxingjieguo);//运行结果
                                    //开始初始化折线图列表数据,遍历当前的最大的map1拿到折线图要展现的数据
                                    Iterator entries1 = map1.entrySet().iterator();
                                    DefaultCategoryDataset mDataset = new DefaultCategoryDataset();
                                    while (entries1.hasNext()) {
                                        Map.Entry entry1 = (Map.Entry) entries1.next();
                                        String key1 = (String)entry1.getKey();
                                        Object value1 = entry1.getValue();
//                                        if(value1==null){//&&dataDate+yfDays
//                                            value1=0;//设置一个默认值
//                                        }
//                                        String value1String=value1.toString();//先把值设置成字符串类型扔回去
                                        if(key1.contains("TO_")&&key1.length()==5){//判断获取到自己所需要的折线图数据
                                            //判断当前获得的折线图对象的个数如果超过的月份的天数则整个对象属性遍历的循环终止（针对月份）
                                            if(monitoringReportList.size()>=yfDays){
                                                break;
                                            }
                                            String toMonth=key1.split("_")[1];//获取到月份的具体数字
                                            if(value1==null&&Integer.parseInt(nowDay)>=Integer.parseInt(dataDate+toMonth)){//&&dataDate+yfDays
                                                value1=0;//设置一个默认值
                                            }
                                            if(value1!=null){
                                                String value1String=value1.toString();//先把值设置成字符串类型扔回去
                                                mDataset.addValue(Float.parseFloat(value1String), Tj_Type_Str.toString(), toMonth);
                                            }else{
                                                mDataset.addValue(null, Tj_Type_Str.toString(), toMonth);
                                            }
                                           /* monitoringReportObject.put("value",value1String);//Y轴
                                            monitoringReportObject.put("rowKey",Tj_Type_Str);//图例
                                            monitoringReportObject.put("columnKey",toMonth);//Y轴
                                            monitoringReportList.add(monitoringReportObject);
                                            monitoringReportObject=new HashMap();*/
                                        }
                                        // System.out.println("Key = " + key + ", Value = " + value);
                                    }
                                    String imgName= JavaUUIDGenerator.getUUID();
                                    String savePath=tomcatImgPath+ imgName+".png";
                                    Line.getDataToFileAndSaveTOThisTomcat(mDataset,savePath,1024,420);
                                    /*List<String> imgNames=new ArrayList<String>();
                                    imgNames.add(imgName);*/
                                    List<Map> imgNames=new ArrayList<Map>();
                                    Map imgObject = new HashMap();
                                    imgObject.put("imgName",imgName);
                                    imgObject.put("monitoringPointNameNew",monitoringPointName);
                                    imgObject.put("imgFullName",imgName+".png");
                                    imgNames.add(imgObject);
                                    monitoringObject.put("monitoringReportList",imgNames);//把保存成功之后的图片名称和格式放回到列表中返回去
                                    //monitoringObject.put("monitoringReportList",monitoringReportList);//把当前监测点折线图扔回到监测点对象中
                                    monitoringReportList=new ArrayList<Map>();//重置折线图的列表对象
                                    monitoringList.add(monitoringObject);
                                    monitoringObject=new HashMap();//当前监测点执行完成重新初始化
                                }else if(monitoringPointType==3||monitoringPointType==13){//三相
                                    yunxingjieguo=dateString+"，"+monitoringPointName
                                            +Tj_Type_Str+"A相的平均值为："+AX_AVG+"；最高值为："+AX_MAX+"("+format6Parse(format5Parse(AX_MAX_DATA_TAG+" "+AX_MAX_TIME_TAG))+")"+"；最低值为："+AX_MIN+"("+format6Parse(format5Parse(AX_MIN_DATA_TAG+" "+AX_MIN_TIME_TAG))+")"+"。"
                                            +Tj_Type_Str+"B相的平均值为："+BX_AVG+"；最高值为："+BX_MAX+"("+format6Parse(format5Parse(BX_MAX_DATA_TAG+" "+BX_MAX_TIME_TAG))+")"+"；最低值为："+BX_MIN+"("+format6Parse(format5Parse(BX_MIN_DATA_TAG+" "+BX_MIN_TIME_TAG))+")"+"。"
                                            +Tj_Type_Str+"C相的平均值为："+CX_AVG+"；最高值为："+CX_MAX+"("+format6Parse(format5Parse(CX_MAX_DATA_TAG+" "+CX_MAX_TIME_TAG))+")"+"；最低值为："+CX_MIN+"("+format6Parse(format5Parse(CX_MIN_DATA_TAG+" "+CX_MIN_TIME_TAG))+")"+"。";
                                    yunxingxiang=Tj_Type_Str.toString();
                                    monitoringObject.put("yunxingxiang",yunxingxiang);//运行项
                                    monitoringObject.put("yunxingjieguo",yunxingjieguo);//运行结果
                                    //开始初始化折线图列表数据,遍历当前的最大的map1拿到折线图要展现的数据
                                    Iterator entries2 = map1.entrySet().iterator();
                                    DefaultCategoryDataset mDataset = new DefaultCategoryDataset();
                                    while (entries2.hasNext()) {
                                        Map.Entry entry2 = (Map.Entry) entries2.next();
                                        String key2 = (String)entry2.getKey();
                                        Object value2 = entry2.getValue();
//                                        if(value2==null){
//                                            value2=0;//设置一个默认值
//                                        }
//                                        String value1String=value2.toString();//先把值设置成字符串类型扔回去
                                        if(key2.contains("AX_")||key2.contains("BX_")||key2.contains("CX_") ){//判断获取到自己所需要的折线图数据
                                            if(key2.length()==5) {//必须在做一个判断否则平均值，最高值，最低值也进来了，因为那也是以TO_,AX_,BX_,CX_打头的
                                                //判断当前获得的折线图对象的个数如果超过的月份的天数则整个对象属性遍历的循环终止（针对月份）
                                                if(monitoringReportList.size()>=(yfDays*3)){
                                                    break;
                                                }
                                                String toMonth = key2.split("_")[1];//获取到月份的具体数字
                                                if(value2==null&&Integer.parseInt(nowDay)>=Integer.parseInt(dataDate+toMonth)){//&&dataDate+yfDays
                                                    value2=0;//设置一个默认值
                                                }
                                                if(value2!=null){
                                                    String value1String=value2.toString();//先把值设置成字符串类型扔回去
                                                    mDataset.addValue(Float.parseFloat(value1String), Tj_Type_Str.toString()+key2.split("_")[0], toMonth);
                                                }else{
                                                    mDataset.addValue(null, Tj_Type_Str.toString()+key2.split("_")[0], toMonth);
                                                }
                                               // mDataset.addValue(Float.parseFloat(value1String), Tj_Type_Str.toString()+key2.split("_")[0], toMonth);
                                               /* monitoringReportObject.put("value", value1String);//Y轴
                                                monitoringReportObject.put("rowKey", Tj_Type_Str + key2.split("_")[0]);//图例
                                                monitoringReportObject.put("columnKey", toMonth);//Y轴
                                                monitoringReportList.add(monitoringReportObject);
                                                monitoringReportObject = new HashMap();*/
                                            }
                                        }
                                        // System.out.println("Key = " + key + ", Value = " + value);
                                    }
                                    String imgName= JavaUUIDGenerator.getUUID();
                                    String savePath=tomcatImgPath+ imgName+".png";
                                    Line.getDataToFileAndSaveTOThisTomcat(mDataset,savePath,1024,420);
                                    /*List<String> imgNames=new ArrayList<String>();
                                    imgNames.add(imgName);*/
                                    List<Map> imgNames=new ArrayList<Map>();
                                    Map imgObject = new HashMap();
                                    imgObject.put("imgName",imgName);
                                    imgObject.put("monitoringPointNameNew",monitoringPointName);
                                    imgObject.put("imgFullName",imgName+".png");
                                    imgNames.add(imgObject);
                                    monitoringObject.put("monitoringReportList",imgNames);//把保存成功之后的图片名称和格式放回到列表中返回去
                                    //monitoringObject.put("monitoringReportList",monitoringReportList);//把当前监测点折线图扔回到监测点对象中
                                    monitoringReportList=new ArrayList<Map>();//重置折线图的列表对象
                                    monitoringList.add(monitoringObject);
                                    monitoringObject=new HashMap();//当前监测点执行完成重新初始化
                                }
                                /*********************************处理计量点对象信息结束**************************************/
                                /****************************判断循环是否结束--程序开始*********************************/
                                if(i==monitoringMsgFromMonthList.size()-1){
                                    sameMonitoringObject.put("monitoringList",monitoringList);
                                    sameMonitoringList.add(sameMonitoringObject);//把共有的计量点大对象放到共有计量点大列表中
                                    substationMap.put("sameMonitoringList",sameMonitoringList);//把共有的计量点大列表放回到电房对象中
                                    substationList.add(substationMap);//把电房对象扔回到电房列表中
                                    monitoringList=new ArrayList<Map>();//初始化计量点列表
                                    sameMonitoringObject=new HashMap();//初始化共有计量点大对象
                                    sameMonitoringList=new ArrayList<Map>();//初始化共有计量点列表大对象
                                    substationMap=new HashMap();//初始化电房对象
                                }
                                /****************************判断循环是否结束--程序结束*********************************/
                                continue;
                            }
                            //第二次的时候就需要首先判断电房id是否一致，电房id如果一致则循环遍历计量点，电房如果不一致的话需要把之前的电房信息完结掉然后重新初始化新电房的计量点信息
                            //如果电房一致，遍历计量点的时候需要判断当前和之前计量点id是否一致
                            if(oldYnBaseEnterpriseSubstationLongId.equals(ynBaseEnterpriseSubstationLongId.toString())){//判断当前循环电房id与上一个循环的电房id一致
                                if(oldMonitoringPointCoreId.equals(monitoringPointCoreId)){//判断同一个电房中，当前循环的计量点id与上一个循环的计量点id一致
                                    /*********************************处理计量点对象信息开始**************************************/
                                    monitoringObject.put("orderInt",orderInt);//监测点的排序
                                    monitoringObject.put("monitoringPointName",monitoringPointName);//单相可以直接出计量点名称（最后决定把A相,B,C相合并所以计量点名称就直接使用就好）
                                    if(monitoringPointType==1||monitoringPointType==11||monitoringPointType==12||monitoringPointType==14){//1单相,11温度单相，12湿度单相
                                        yunxingjieguo=dateString+"，"+monitoringPointName+Tj_Type_Str+"的平均值为："+TO_AVG+"；最高值为："+TO_MAX+"("+format6Parse(format5Parse(TO_MAX_DATA_TAG+" "+TO_MAX_TIME_TAG))+")"+"；最低值为："+TO_MIN+"("+format6Parse(format5Parse(TO_MIN_DATA_TAG+" "+TO_MIN_TIME_TAG))+")";
                                        yunxingxiang=Tj_Type_Str.toString();
                                        monitoringObject.put("yunxingxiang",yunxingxiang);//运行项
                                        monitoringObject.put("yunxingjieguo",yunxingjieguo);//运行结果
                                        //开始初始化折线图列表数据,遍历当前的最大的map1拿到折线图要展现的数据
                                        Iterator entries1 = map1.entrySet().iterator();
                                        DefaultCategoryDataset mDataset = new DefaultCategoryDataset();
                                        while (entries1.hasNext()) {
                                            Map.Entry entry1 = (Map.Entry) entries1.next();
                                            String key1 = (String)entry1.getKey();
                                            Object value1 = entry1.getValue();
//                                            if(value1==null){
//                                                value1=0;//设置一个默认值
//                                            }
//                                            String value1String=value1.toString();//先把值设置成字符串类型扔回去
                                            if(key1.contains("TO_")&&key1.length()==5){//判断获取到自己所需要的折线图数据
                                                //判断当前获得的折线图对象的个数如果超过的月份的天数则整个对象属性遍历的循环终止（针对月份）
                                                if(monitoringReportList.size()>=yfDays){
                                                    break;
                                                }
                                                String toMonth=key1.split("_")[1];//获取到月份的具体数字
                                                if(value1==null&&Integer.parseInt(nowDay)>=Integer.parseInt(dataDate+toMonth)){//&&dataDate+yfDays
                                                    value1=0;//设置一个默认值
                                                }

                                                if(value1!=null){
                                                    String value1String=value1.toString();//先把值设置成字符串类型扔回去
                                                    mDataset.addValue(Float.parseFloat(value1String), Tj_Type_Str.toString(), toMonth);
                                                }else{
                                                    mDataset.addValue(null, Tj_Type_Str.toString(), toMonth);
                                                }
                                               // mDataset.addValue(Float.parseFloat(value1String), Tj_Type_Str.toString(), toMonth);
                                               /* monitoringReportObject.put("value",value1String);//Y轴
                                                monitoringReportObject.put("rowKey",Tj_Type_Str);//图例
                                                monitoringReportObject.put("columnKey",toMonth);//Y轴
                                                monitoringReportList.add(monitoringReportObject);
                                                monitoringReportObject=new HashMap();*/
                                            }
                                            // System.out.println("Key = " + key + ", Value = " + value);
                                        }
                                        String imgName= JavaUUIDGenerator.getUUID();
                                        String savePath=tomcatImgPath+ imgName+".png";
                                        Line.getDataToFileAndSaveTOThisTomcat(mDataset,savePath,1024,420);
                                        /*List<String> imgNames=new ArrayList<String>();
                                        imgNames.add(imgName);*/
                                        List<Map> imgNames=new ArrayList<Map>();
                                        Map imgObject = new HashMap();
                                        imgObject.put("imgName",imgName);
                                        imgObject.put("monitoringPointNameNew",monitoringPointName);
                                        imgObject.put("imgFullName",imgName+".png");
                                        imgNames.add(imgObject);
                                        monitoringObject.put("monitoringReportList",imgNames);//把保存成功之后的图片名称和格式放回到列表中返回去
                                        //monitoringObject.put("monitoringReportList",monitoringReportList);//把当前监测点折线图扔回到监测点对象中
                                        monitoringReportList=new ArrayList<Map>();//重置折线图的列表对象
                                        monitoringList.add(monitoringObject);
                                        monitoringObject=new HashMap();//当前监测点执行完成重新初始化
                                    }else if(monitoringPointType==3||monitoringPointType==13){//三相
                                        yunxingjieguo=dateString+"，"+monitoringPointName
                                                +Tj_Type_Str+"A相的平均值为："+AX_AVG+"；最高值为："+AX_MAX+"("+format6Parse(format5Parse(AX_MAX_DATA_TAG+" "+AX_MAX_TIME_TAG))+")"+"；最低值为："+AX_MIN+"("+format6Parse(format5Parse(AX_MIN_DATA_TAG+" "+AX_MIN_TIME_TAG))+")"+"。"
                                                +Tj_Type_Str+"B相的平均值为："+BX_AVG+"；最高值为："+BX_MAX+"("+format6Parse(format5Parse(BX_MAX_DATA_TAG+" "+BX_MAX_TIME_TAG))+")"+"；最低值为："+BX_MIN+"("+format6Parse(format5Parse(BX_MIN_DATA_TAG+" "+BX_MIN_TIME_TAG))+")"+"。"
                                                +Tj_Type_Str+"C相的平均值为："+CX_AVG+"；最高值为："+CX_MAX+"("+format6Parse(format5Parse(CX_MAX_DATA_TAG+" "+CX_MAX_TIME_TAG))+")"+"；最低值为："+CX_MIN+"("+format6Parse(format5Parse(CX_MIN_DATA_TAG+" "+CX_MIN_TIME_TAG))+")"+"。";
                                        yunxingxiang=Tj_Type_Str.toString();
                                        monitoringObject.put("yunxingxiang",yunxingxiang);//运行项
                                        monitoringObject.put("yunxingjieguo",yunxingjieguo);//运行结果
                                        //开始初始化折线图列表数据,遍历当前的最大的map1拿到折线图要展现的数据
                                        Iterator entries2 = map1.entrySet().iterator();
                                        DefaultCategoryDataset mDataset = new DefaultCategoryDataset();
                                        while (entries2.hasNext()) {
                                            Map.Entry entry2 = (Map.Entry) entries2.next();
                                            String key2 = (String)entry2.getKey();
                                            Object value2 = entry2.getValue();
//                                            if(value2==null){
//                                                value2=0;//设置一个默认值
//                                            }
//                                            String value1String=value2.toString();//先把值设置成字符串类型扔回去
                                            if(key2.contains("AX_")||key2.contains("BX_")||key2.contains("CX_") ){//判断获取到自己所需要的折线图数据
                                                if(key2.length()==5) {//必须在做一个判断否则平均值，最高值，最低值也进来了，因为那也是以TO_,AX_,BX_,CX_打头的
                                                    //判断当前获得的折线图对象的个数如果超过的月份的天数则整个对象属性遍历的循环终止（针对月份）
                                                    if(monitoringReportList.size()>=(yfDays*3)){
                                                        break;
                                                    }
                                                    String toMonth = key2.split("_")[1];//获取到月份的具体数字
                                                    if(value2==null&&Integer.parseInt(nowDay)>=Integer.parseInt(dataDate+toMonth)){//&&dataDate+yfDays
                                                        value2=0;//设置一个默认值
                                                    }

                                                    if(value2!=null){
                                                        String value1String=value2.toString();//先把值设置成字符串类型扔回去
                                                        mDataset.addValue(Float.parseFloat(value1String), Tj_Type_Str.toString()+key2.split("_")[0], toMonth);
                                                    }else{
                                                        mDataset.addValue(null, Tj_Type_Str.toString()+key2.split("_")[0], toMonth);
                                                    }
                                                   // mDataset.addValue(Float.parseFloat(value1String), Tj_Type_Str.toString()+key2.split("_")[0], toMonth);
                                                   /* monitoringReportObject.put("value", value1String);//Y轴
                                                    monitoringReportObject.put("rowKey", Tj_Type_Str + key2.split("_")[0]);//图例
                                                    monitoringReportObject.put("columnKey", toMonth);//Y轴
                                                    monitoringReportList.add(monitoringReportObject);
                                                    monitoringReportObject = new HashMap();*/
                                                }
                                            }
                                            // System.out.println("Key = " + key + ", Value = " + value);
                                        }
                                        String imgName= JavaUUIDGenerator.getUUID();
                                        String savePath=tomcatImgPath+ imgName+".png";
                                        Line.getDataToFileAndSaveTOThisTomcat(mDataset,savePath,1024,420);
                                        /*List<String> imgNames=new ArrayList<String>();
                                        imgNames.add(imgName);*/
                                        List<Map> imgNames=new ArrayList<Map>();
                                        Map imgObject = new HashMap();
                                        imgObject.put("imgName",imgName);
                                        imgObject.put("monitoringPointNameNew",monitoringPointName);
                                        imgObject.put("imgFullName",imgName+".png");
                                        imgNames.add(imgObject);
                                        monitoringObject.put("monitoringReportList",imgNames);//把保存成功之后的图片名称和格式放回到列表中返回去
                                       // monitoringObject.put("monitoringReportList",monitoringReportList);//把当前监测点折线图扔回到监测点对象中
                                        monitoringReportList=new ArrayList<Map>();//重置折线图的列表对象
                                        monitoringList.add(monitoringObject);
                                        monitoringObject=new HashMap();//当前监测点执行完成重新初始化
                                    }
                                    /*********************************处理计量点对象信息结束**************************************/
                                }else{//判断同一个电房中，当前循环的计量点id与上一个循环的计量点id不一致
                                    //把当前监测点列表放回到相同计量点统计的对象中，并扔回相同计量点统计列表然后初始化相同计量点统计的对象和计量点列表
                                    sameMonitoringObject.put("monitoringList",monitoringList);//把计量点列表仍回到共有计量点大对象中
                                    sameMonitoringList.add(sameMonitoringObject);//把共有计量点大对象扔回到共有计量点大列表中
                                    monitoringList=new ArrayList<Map>();//初始化计量点列表
                                    sameMonitoringObject=new HashMap();//初始化共有计量点大对象
                                    oldMonitoringPointCoreId = monitoringPointCoreId.toString();//把当前计量点id记录到前一个计量点id中
                                    /*********************************处理计量点对象信息开始**************************************/
                                    monitoringObject.put("orderInt",orderInt);//监测点的排序
                                    sameMonitoringObject.put("monitoringPointName",monitoringPointName);//设置计量点公有名称
                                    monitoringObject.put("monitoringPointName",monitoringPointName);//单相可以直接出计量点名称（最后决定把A相,B,C相合并所以计量点名称就直接使用就好）
                                    if(monitoringPointType==1||monitoringPointType==11||monitoringPointType==12||monitoringPointType==14){//1单相,11温度单相，12湿度单相
                                        yunxingjieguo=dateString+"，"+monitoringPointName+Tj_Type_Str+"的平均值为："+TO_AVG+"；最高值为："+TO_MAX+"("+format6Parse(format5Parse(TO_MAX_DATA_TAG+" "+TO_MAX_TIME_TAG))+")"+"；最低值为："+TO_MIN+"("+format6Parse(format5Parse(TO_MIN_DATA_TAG+" "+TO_MIN_TIME_TAG))+")";
                                        yunxingxiang=Tj_Type_Str.toString();
                                        monitoringObject.put("yunxingxiang",yunxingxiang);//运行项
                                        monitoringObject.put("yunxingjieguo",yunxingjieguo);//运行结果
                                        //开始初始化折线图列表数据,遍历当前的最大的map1拿到折线图要展现的数据
                                        Iterator entries1 = map1.entrySet().iterator();
                                        DefaultCategoryDataset mDataset = new DefaultCategoryDataset();
                                        while (entries1.hasNext()) {
                                            Map.Entry entry1 = (Map.Entry) entries1.next();
                                            String key1 = (String)entry1.getKey();
                                            Object value1 = entry1.getValue();
//                                            if(value1==null){
//                                                value1=0;//设置一个默认值
//                                            }
//                                            String value1String=value1.toString();//先把值设置成字符串类型扔回去
                                            if(key1.contains("TO_")&&key1.length()==5){//判断获取到自己所需要的折线图数据
                                                //判断当前获得的折线图对象的个数如果超过的月份的天数则整个对象属性遍历的循环终止（针对月份）
                                                if(monitoringReportList.size()>=yfDays){
                                                    break;
                                                }
                                                String toMonth=key1.split("_")[1];//获取到月份的具体数字
                                                if(value1==null&&Integer.parseInt(nowDay)>=Integer.parseInt(dataDate+toMonth)){//&&dataDate+yfDays
                                                    value1=0;//设置一个默认值
                                                }

                                                if(value1!=null){
                                                    String value1String=value1.toString();//先把值设置成字符串类型扔回去
                                                    mDataset.addValue(Float.parseFloat(value1String), Tj_Type_Str.toString(), toMonth);
                                                }else{
                                                    mDataset.addValue(null, Tj_Type_Str.toString(), toMonth);
                                                }
                                              //  mDataset.addValue(Float.parseFloat(value1String), Tj_Type_Str.toString(), toMonth);
                                               /* monitoringReportObject.put("value",value1String);//Y轴
                                                monitoringReportObject.put("rowKey",Tj_Type_Str);//图例
                                                monitoringReportObject.put("columnKey",toMonth);//Y轴
                                                monitoringReportList.add(monitoringReportObject);
                                                monitoringReportObject=new HashMap();*/
                                            }
                                            // System.out.println("Key = " + key + ", Value = " + value);
                                        }
                                        String imgName= JavaUUIDGenerator.getUUID();
                                        String savePath=tomcatImgPath+ imgName+".png";
                                        Line.getDataToFileAndSaveTOThisTomcat(mDataset,savePath,1024,420);
                                        /*List<String> imgNames=new ArrayList<String>();
                                        imgNames.add(imgName);*/
                                        List<Map> imgNames=new ArrayList<Map>();
                                        Map imgObject = new HashMap();
                                        imgObject.put("imgName",imgName);
                                        imgObject.put("monitoringPointNameNew",monitoringPointName);
                                        imgObject.put("imgFullName",imgName+".png");
                                        imgNames.add(imgObject);
                                        monitoringObject.put("monitoringReportList",imgNames);//把保存成功之后的图片名称和格式放回到列表中返回去
                                        //monitoringObject.put("monitoringReportList",monitoringReportList);//把当前监测点折线图扔回到监测点对象中
                                        monitoringReportList=new ArrayList<Map>();//重置折线图的列表对象
                                        monitoringList.add(monitoringObject);
                                        monitoringObject=new HashMap();//当前监测点执行完成重新初始化
                                    }else if(monitoringPointType==3||monitoringPointType==13){//三相
                                        yunxingjieguo=dateString+"，"+monitoringPointName
                                                +Tj_Type_Str+"A相的平均值为："+AX_AVG+"；最高值为："+AX_MAX+"("+format6Parse(format5Parse(AX_MAX_DATA_TAG+" "+AX_MAX_TIME_TAG))+")"+"；最低值为："+AX_MIN+"("+format6Parse(format5Parse(AX_MIN_DATA_TAG+" "+AX_MIN_TIME_TAG))+")"+"。"
                                                +Tj_Type_Str+"B相的平均值为："+BX_AVG+"；最高值为："+BX_MAX+"("+format6Parse(format5Parse(BX_MAX_DATA_TAG+" "+BX_MAX_TIME_TAG))+")"+"；最低值为："+BX_MIN+"("+format6Parse(format5Parse(BX_MIN_DATA_TAG+" "+BX_MIN_TIME_TAG))+")"+"。"
                                                +Tj_Type_Str+"C相的平均值为："+CX_AVG+"；最高值为："+CX_MAX+"("+format6Parse(format5Parse(CX_MAX_DATA_TAG+" "+CX_MAX_TIME_TAG))+")"+"；最低值为："+CX_MIN+"("+format6Parse(format5Parse(CX_MIN_DATA_TAG+" "+CX_MIN_TIME_TAG))+")"+"。";
                                        yunxingxiang=Tj_Type_Str.toString();
                                        monitoringObject.put("yunxingxiang",yunxingxiang);//运行项
                                        monitoringObject.put("yunxingjieguo",yunxingjieguo);//运行结果
                                        //开始初始化折线图列表数据,遍历当前的最大的map1拿到折线图要展现的数据
                                        Iterator entries2 = map1.entrySet().iterator();
                                        DefaultCategoryDataset mDataset = new DefaultCategoryDataset();
                                        while (entries2.hasNext()) {
                                            Map.Entry entry2 = (Map.Entry) entries2.next();
                                            String key2 = (String)entry2.getKey();
                                            Object value2 = entry2.getValue();
//                                            if(value2==null){
//                                                value2=0;//设置一个默认值
//                                            }
//                                            String value1String=value2.toString();//先把值设置成字符串类型扔回去
                                            if(key2.contains("AX_")||key2.contains("BX_")||key2.contains("CX_") ){//判断获取到自己所需要的折线图数据
                                                if(key2.length()==5) {//必须在做一个判断否则平均值，最高值，最低值也进来了，因为那也是以TO_,AX_,BX_,CX_打头的
                                                    //判断当前获得的折线图对象的个数如果超过的月份的天数则整个对象属性遍历的循环终止（针对月份）
                                                    if(monitoringReportList.size()>=(yfDays*3)){
                                                        break;
                                                    }
                                                    String toMonth = key2.split("_")[1];//获取到月份的具体数字
                                                    if(value2==null&&Integer.parseInt(nowDay)>=Integer.parseInt(dataDate+toMonth)){//&&dataDate+yfDays
                                                        value2=0;//设置一个默认值
                                                    }

                                                    if(value2!=null){
                                                        String value1String=value2.toString();//先把值设置成字符串类型扔回去
                                                        mDataset.addValue(Float.parseFloat(value1String), Tj_Type_Str.toString()+key2.split("_")[0], toMonth);
                                                    }else{
                                                        mDataset.addValue(null, Tj_Type_Str.toString()+key2.split("_")[0], toMonth);
                                                    }
                                                    //mDataset.addValue(Float.parseFloat(value1String), Tj_Type_Str.toString()+key2.split("_")[0], toMonth);
                                                   /* monitoringReportObject.put("value", value1String);//Y轴
                                                    monitoringReportObject.put("rowKey", Tj_Type_Str + key2.split("_")[0]);//图例
                                                    monitoringReportObject.put("columnKey", toMonth);//Y轴
                                                    monitoringReportList.add(monitoringReportObject);
                                                    monitoringReportObject = new HashMap();*/
                                                }
                                            }
                                            // System.out.println("Key = " + key + ", Value = " + value);
                                        }
                                        String imgName= JavaUUIDGenerator.getUUID();
                                        String savePath=tomcatImgPath+ imgName+".png";
                                        Line.getDataToFileAndSaveTOThisTomcat(mDataset,savePath,1024,420);
                                        /*List<String> imgNames=new ArrayList<String>();
                                        imgNames.add(imgName);*/
                                        List<Map> imgNames=new ArrayList<Map>();
                                        Map imgObject = new HashMap();
                                        imgObject.put("imgName",imgName);
                                        imgObject.put("monitoringPointNameNew",monitoringPointName);
                                        imgObject.put("imgFullName",imgName+".png");
                                        imgNames.add(imgObject);
                                        monitoringObject.put("monitoringReportList",imgNames);//把保存成功之后的图片名称和格式放回到列表中返回去
                                        //monitoringObject.put("monitoringReportList",monitoringReportList);//把当前监测点折线图扔回到监测点对象中
                                        monitoringReportList=new ArrayList<Map>();//重置折线图的列表对象
                                        monitoringList.add(monitoringObject);
                                        monitoringObject=new HashMap();//当前监测点执行完成重新初始化
                                    }
                                    /*********************************处理计量点对象信息结束**************************************/
                                }
                            }else{//判断当前循环电房id与上一个循环的电房id不一致，把之前的电房信息完结掉然后重新初始化新电房的计量点信息
                                sameMonitoringObject.put("monitoringList",monitoringList);
                                sameMonitoringList.add(sameMonitoringObject);//把共有的计量点大对象放到共有计量点大列表中
                                substationMap.put("sameMonitoringList",sameMonitoringList);//把共有的计量点大列表放回到电房对象中
                                substationList.add(substationMap);//把电房对象扔回到电房列表中
                                monitoringList=new ArrayList<Map>();//初始化计量点列表
                                sameMonitoringObject=new HashMap();//初始化共有计量点大对象
                                sameMonitoringList=new ArrayList<Map>();//初始化共有计量点列表大对象
                                substationMap=new HashMap();//初始化电房对象
                                //oldYnBaseEnterpriseSubstationLongId=  ynBaseEnterpriseSubstationLongId.toString();//把当前电房id设置为前一个电房的id
                                oldYnBaseEnterpriseSubstationLongId=  ynBaseEnterpriseSubstationLongId.toString();//前一个电房的id
                                oldMonitoringPointCoreId = monitoringPointCoreId.toString();//前一个计量点的id
                                /*********************************处理电房对象信息开始**************************************/
                                substationMap.put("ynBaseEnterpriseSubstationName",ynBaseEnterpriseSubstationName);//设置电房名称
                                String oldEquipmentContent=null;//listMonitoringMsg遍历训时候记录设备的前一个id
                                String oldMonitoringContent=null;//listMonitoringMsg遍历训时候记录监测点的前一个id
                                for(Map mapContent:listMonitoringMsg){//遍历循环企业下的所有的电房计量点信息，包括计量点名称，所属设备，监测点类型
                                    Object equipmentContent=mapContent.get("ynBaseEquipmentLongId");
                                    Object monitoringContent=mapContent.get("monitoringPointCoreId");
                                    Object ynBaseEnterpriseSubstationLongIdContent=mapContent.get("ynBaseEnterpriseSubstationLongId");
                                    if(ynBaseEnterpriseSubstationLongIdContent.toString().equals(ynBaseEnterpriseSubstationLongId.toString())){
                                        if(oldEquipmentContent==null){
                                            oldEquipmentContent=equipmentContent.toString();
                                            equimentCount++;//统计电房下设备个数
                                        }else if(!oldEquipmentContent.equals(equipmentContent)){
                                            oldEquipmentContent=equipmentContent.toString();
                                            equimentCount++;//统计电房下设备个数
                                        }
                                        if(oldMonitoringContent==null){
                                            oldMonitoringContent=monitoringContent.toString();
                                            monitoringCount++;//统计电房下计量点个数
                                        }else if(!oldMonitoringContent.equals(monitoringContent)){
                                            oldMonitoringContent=monitoringContent.toString();
                                            monitoringCount++;//统计电房下计量点个数
                                        }
                                        monitoringContentList.add(mapContent);
                                    }
                                }
                                substationMap.put("monitoringsContent",monitoringContentList);//把获得的当前电房下的监测点内容放回到电房对象中
                                shebeiyunxingneirong="本次设备运行报告时间"+dateString+"，累计对电房("+ynBaseEnterpriseSubstationName+")的"+equimentCount+"个大设备进行运行状态监测，"+monitoringCount+"个监测点，设备运行健康状态详情如下";
                                substationMap.put("shebeiyunxingneirong",shebeiyunxingneirong);//把设备运行内容扔回到电房对象表中
                                shebeiyunxingneirong=null;//重置设备运行内容
                                monitoringContentList=new ArrayList<Map>();//重置监测点内容列表
                                monitoringCount=0;//重置电房下计量点个数
                                equimentCount=0;//  重置电房下设备蛇叔
                                //处理电房告警信息
                                for(Map warningMap:monitoringWarningList){
                                    Object warningSubstaionId=warningMap.get("ynBaseEnterpriseSubstationLongId");
                                    if(ynBaseEnterpriseSubstationLongId.equals(warningSubstaionId.toString())){
                                        warningSubstations.add(warningMap);
                                    }
                                }
                                substationMap.put("warningSubstation",warningSubstations);//把电房下面的所有计量点告警信息列表仍会到电房对象中
                                warningSubstations=new ArrayList<Map>();//电房告警列表初始化
                                /*********************************处理电房对象信息结束**************************************/
                                /*********************************处理计量点对象信息开始**************************************/
                                monitoringObject.put("orderInt",orderInt);//监测点的排序
                                sameMonitoringObject.put("monitoringPointName",monitoringPointName);//设置计量点公有名称
                                monitoringObject.put("monitoringPointName",monitoringPointName);//单相可以直接出计量点名称（最后决定把A相,B,C相合并所以计量点名称就直接使用就好）
                                if(monitoringPointType==1||monitoringPointType==11||monitoringPointType==12||monitoringPointType==14){//1单相,11温度单相，12湿度单相
                                    yunxingjieguo=dateString+"，"+monitoringPointName+Tj_Type_Str+"的平均值为："+TO_AVG+"；最高值为："+TO_MAX+"("+format6Parse(format5Parse(TO_MAX_DATA_TAG+" "+TO_MAX_TIME_TAG))+")"+"；最低值为："+TO_MIN+"("+format6Parse(format5Parse(TO_MIN_DATA_TAG+" "+TO_MIN_TIME_TAG))+")";
                                    yunxingxiang=Tj_Type_Str.toString();
                                    monitoringObject.put("yunxingxiang",yunxingxiang);//运行项
                                    monitoringObject.put("yunxingjieguo",yunxingjieguo);//运行结果
                                    //开始初始化折线图列表数据,遍历当前的最大的map1拿到折线图要展现的数据
                                    Iterator entries1 = map1.entrySet().iterator();
                                    DefaultCategoryDataset mDataset = new DefaultCategoryDataset();
                                    while (entries1.hasNext()) {
                                        Map.Entry entry1 = (Map.Entry) entries1.next();
                                        String key1 = (String)entry1.getKey();
                                        Object value1 = entry1.getValue();
//                                        if(value1==null){
//                                            value1=0;//设置一个默认值
//                                        }
//                                        String value1String=value1.toString();//先把值设置成字符串类型扔回去
                                        if(key1.contains("TO_")&&key1.length()==5){//判断获取到自己所需要的折线图数据
                                            //判断当前获得的折线图对象的个数如果超过的月份的天数则整个对象属性遍历的循环终止（针对月份）
                                            if(monitoringReportList.size()>=yfDays){
                                                break;
                                            }
                                            String toMonth=key1.split("_")[1];//获取到月份的具体数字
                                            if(value1==null&&Integer.parseInt(nowDay)>=Integer.parseInt(dataDate+toMonth)){//&&dataDate+yfDays
                                                value1=0;//设置一个默认值
                                            }

                                            if(value1!=null){
                                                String value1String=value1.toString();//先把值设置成字符串类型扔回去
                                                mDataset.addValue(Float.parseFloat(value1String), Tj_Type_Str.toString(), toMonth);
                                            }else{
                                                mDataset.addValue(null, Tj_Type_Str.toString(), toMonth);
                                            }
                                            //mDataset.addValue(Float.parseFloat(value1String), Tj_Type_Str.toString(), toMonth);
                                            /*monitoringReportObject.put("value",value1String);//Y轴
                                            monitoringReportObject.put("rowKey",Tj_Type_Str);//图例
                                            monitoringReportObject.put("columnKey",toMonth);//Y轴
                                            monitoringReportList.add(monitoringReportObject);
                                            monitoringReportObject=new HashMap();*/
                                        }
                                        // System.out.println("Key = " + key + ", Value = " + value);
                                    }
                                    String imgName= JavaUUIDGenerator.getUUID();
                                    String savePath=tomcatImgPath+ imgName+".png";
                                    Line.getDataToFileAndSaveTOThisTomcat(mDataset,savePath,1024,420);
                                    /*List<String> imgNames=new ArrayList<String>();
                                    imgNames.add(imgName);*/
                                    List<Map> imgNames=new ArrayList<Map>();
                                    Map imgObject = new HashMap();
                                    imgObject.put("imgName",imgName);
                                    imgObject.put("monitoringPointNameNew",monitoringPointName);
                                    imgObject.put("imgFullName",imgName+".png");
                                    imgNames.add(imgObject);
                                    monitoringObject.put("monitoringReportList",imgNames);//把保存成功之后的图片名称和格式放回到列表中返回去
                                    //monitoringObject.put("monitoringReportList",monitoringReportList);//把当前监测点折线图扔回到监测点对象中
                                    monitoringReportList=new ArrayList<Map>();//重置折线图的列表对象
                                    monitoringList.add(monitoringObject);
                                    monitoringObject=new HashMap();//当前监测点执行完成重新初始化
                                }else if(monitoringPointType==3||monitoringPointType==13){//三相
                                    yunxingjieguo=dateString+"，"+monitoringPointName
                                            +Tj_Type_Str+"A相的平均值为："+AX_AVG+"；最高值为："+AX_MAX+"("+format6Parse(format5Parse(AX_MAX_DATA_TAG+" "+AX_MAX_TIME_TAG))+")"+"；最低值为："+AX_MIN+"("+format6Parse(format5Parse(AX_MIN_DATA_TAG+" "+AX_MIN_TIME_TAG))+")"+"。"
                                            +Tj_Type_Str+"B相的平均值为："+BX_AVG+"；最高值为："+BX_MAX+"("+format6Parse(format5Parse(BX_MAX_DATA_TAG+" "+BX_MAX_TIME_TAG))+")"+"；最低值为："+BX_MIN+"("+format6Parse(format5Parse(BX_MIN_DATA_TAG+" "+BX_MIN_TIME_TAG))+")"+"。"
                                            +Tj_Type_Str+"C相的平均值为："+CX_AVG+"；最高值为："+CX_MAX+"("+format6Parse(format5Parse(CX_MAX_DATA_TAG+" "+CX_MAX_TIME_TAG))+")"+"；最低值为："+CX_MIN+"("+format6Parse(format5Parse(CX_MIN_DATA_TAG+" "+CX_MIN_TIME_TAG))+")"+"。";
                                    yunxingxiang=Tj_Type_Str.toString();
                                    monitoringObject.put("yunxingxiang",yunxingxiang);//运行项
                                    monitoringObject.put("yunxingjieguo",yunxingjieguo);//运行结果
                                    //开始初始化折线图列表数据,遍历当前的最大的map1拿到折线图要展现的数据
                                    Iterator entries2 = map1.entrySet().iterator();
                                    DefaultCategoryDataset mDataset = new DefaultCategoryDataset();
                                    while (entries2.hasNext()) {
                                        Map.Entry entry2 = (Map.Entry) entries2.next();
                                        String key2 = (String)entry2.getKey();
                                        Object value2 = entry2.getValue();
//                                        if(value2==null){
//                                            value2=0;//设置一个默认值
//                                        }
//                                        String value1String=value2.toString();//先把值设置成字符串类型扔回去
                                        if(key2.contains("AX_")||key2.contains("BX_")||key2.contains("CX_") ){//判断获取到自己所需要的折线图数据
                                            if(key2.length()==5) {//必须在做一个判断否则平均值，最高值，最低值也进来了，因为那也是以TO_,AX_,BX_,CX_打头的
                                                //判断当前获得的折线图对象的个数如果超过的月份的天数则整个对象属性遍历的循环终止（针对月份）
                                                if(monitoringReportList.size()>=(yfDays*3)){
                                                    break;
                                                }
                                                String toMonth = key2.split("_")[1];//获取到月份的具体数字
                                                if(value2==null&&Integer.parseInt(nowDay)>=Integer.parseInt(dataDate+toMonth)){//&&dataDate+yfDays
                                                    value2=0;//设置一个默认值
                                                }

                                                if(value2!=null){
                                                    String value1String=value2.toString();//先把值设置成字符串类型扔回去
                                                    mDataset.addValue(Float.parseFloat(value1String), Tj_Type_Str.toString()+key2.split("_")[0], toMonth);
                                                }else{
                                                    mDataset.addValue(null, Tj_Type_Str.toString()+key2.split("_")[0], toMonth);
                                                }
                                               // mDataset.addValue(Float.parseFloat(value1String), Tj_Type_Str.toString()+key2.split("_")[0], toMonth);
                                                /*monitoringReportObject.put("value", value1String);//Y轴
                                                monitoringReportObject.put("rowKey", Tj_Type_Str + key2.split("_")[0]);//图例
                                                monitoringReportObject.put("columnKey", toMonth);//Y轴
                                                monitoringReportList.add(monitoringReportObject);
                                                monitoringReportObject = new HashMap();*/
                                            }
                                        }
                                        // System.out.println("Key = " + key + ", Value = " + value);
                                    }
                                    String imgName= JavaUUIDGenerator.getUUID();
                                    String savePath=tomcatImgPath+ imgName+".png";
                                    Line.getDataToFileAndSaveTOThisTomcat(mDataset,savePath,1024,420);
                                    /*List<String> imgNames=new ArrayList<String>();
                                    imgNames.add(imgName);*/
                                    List<Map> imgNames=new ArrayList<Map>();
                                    Map imgObject = new HashMap();
                                    imgObject.put("imgName",imgName);
                                    imgObject.put("monitoringPointNameNew",monitoringPointName);
                                    imgObject.put("imgFullName",imgName+".png");
                                    imgNames.add(imgObject);
                                    monitoringObject.put("monitoringReportList",imgNames);//把保存成功之后的图片名称和格式放回到列表中返回去
                                   // monitoringObject.put("monitoringReportList",monitoringReportList);//把当前监测点折线图扔回到监测点对象中
                                    monitoringReportList=new ArrayList<Map>();//重置折线图的列表对象
                                    monitoringList.add(monitoringObject);
                                    monitoringObject=new HashMap();//当前监测点执行完成重新初始化
                                }
                                /*********************************处理计量点对象信息结束**************************************/
                            }

                            /****************************判断循环是否结束--程序开始*********************************/
                            if(i==monitoringMsgFromMonthList.size()-1){
                                sameMonitoringObject.put("monitoringList",monitoringList);
                                sameMonitoringList.add(sameMonitoringObject);//把共有的计量点大对象放到共有计量点大列表中
                                substationMap.put("sameMonitoringList",sameMonitoringList);//把共有的计量点大列表放回到电房对象中
                                substationList.add(substationMap);//把电房对象扔回到电房列表中
                                monitoringList=new ArrayList<Map>();//初始化计量点列表
                                sameMonitoringObject=new HashMap();//初始化共有计量点大对象
                                sameMonitoringList=new ArrayList<Map>();//初始化共有计量点列表大对象
                                substationMap=new HashMap();//初始化电房对象
                            }
                            /****************************判断循环是否结束--程序结束*********************************/
                        }//for 结束
                        detailDTO.setDetail(substationList);
                    }//查询每月的表结束
                }else{//查询统计年份表
                    String nowMonth=format0.format(new Date());//获取现在时间的年月
                    String tableName = "jc_tj_nf_" + ynBaseStaffCompanyId;
                    onLineMonitoringDTO.setTableNameString(tableName);
                    String dateString =onLineMonitoringDTO.getYear();
                    onLineMonitoringDTO.setDateString(dateString);
                    //获取年份计量点每个月的各种告警信息
                    OnLineMonitoringDTO onLineMonitoringDTOGJ=new OnLineMonitoringDTO();
                    onLineMonitoringDTOGJ.setTableNameString("jc_gj_"+ynBaseStaffCompanyId);
                    onLineMonitoringDTOGJ.setYear(onLineMonitoringDTO.getYear());
                    onLineMonitoringDTOGJ.setMonth(onLineMonitoringDTO.getMonth());
                    onLineMonitoringDTOGJ.setDay(onLineMonitoringDTO.getDay());
                    onLineMonitoringDTOGJ.setDateString(dateString);
                    onLineMonitoringDTOGJ.setYnBaseCustomerEnterpriseLongId(onLineMonitoringDTO.getYnBaseCustomerEnterpriseLongId());
                    onLineMonitoringDTOGJ.setYnBaseEnterpriseSubstationLongId(onLineMonitoringDTO.getYnBaseEnterpriseSubstationLongId());
                    List<LinkedHashMap> monitoringWarningList=ynMonitoringInitMapper.getMonitoringWarningListByObject(onLineMonitoringDTOGJ);
                    //获取年份计量点每个月的各种参数信息
                    List<LinkedHashMap> monitoringMsgFromYearList=ynMonitoringInitMapper.selectMonitoringTJNFByObject(onLineMonitoringDTO);
                    List<Map> substationList=new ArrayList<Map>();//电房列表
                    Map substationMap =new HashMap();//电房对象
                    List<Map> sameMonitoringList=new ArrayList<Map>();//存放相同监测点id的总结
                    Map sameMonitoringObject= new HashMap();//这个大对象里面带着计量点的列表和计量点的共有名称
                    List<Map> warningSubstations=new ArrayList<Map>();//电房的所有计量点告警列表
                    List<Map> monitoringList=new ArrayList<Map>();//存放监测点的list
                    Map monitoringObject=new HashMap();//监测点对象，每个监测点有监控类型名称，运行结果描述，曲线图
                    List<Map> monitoringReportList=new ArrayList<Map>();//监测点报告列表
                    Map monitoringReportObject=new HashMap();//监测点报告对象
                    String oldYnBaseEnterpriseSubstationLongId=null;//前一个电房的id
                    String oldMonitoringPointCoreId = null;//前一个计量点的id
                    String shebeiyunxingneirong=null;//设备运行内容
                    String yunxingxiang=null;//运行项
                    String yunxingjieguo=null;//运行结果
                    Integer orderInt=0;//排序
                    List<Map> monitoringContentList=new ArrayList<Map>();//电房监测点内容列表
                    Integer equimentCount=0;//运行内容设备总数
                    Integer monitoringCount=0;//运行内容监测点总数
                    for(int i=0;i<monitoringMsgFromYearList.size();i++){//for 循环开始
                        Map map1=monitoringMsgFromYearList.get(i);
                        Object warningAutoIncrementId           = map1.get("idJC")           ;
                        Object monitoringPointAliasId    = map1.get("JCD_AliasID")    ;
                        Object monitoringPointCoreId     = map1.get("JCD_CoreID")     ;
                        Object JCD_id         = map1.get("JCD_id")         ;
                        Object monitoringPointName       = map1.get("JCD_Name")       ;
                        Integer monitoringPointType       = (Integer) map1.get("JCD_Type");
                        Object Tj_Type        = map1.get("Tj_Type")        ;
                        Object Tj_Type_Str        = map1.get("Tj_Type_Str")        ;
                        Object ynBaseEnterpriseSubstationLongId          = map1.get("DF_id")          ;
                        Object ynBaseEnterpriseSubstationName        = map1.get("DF_Name")        ;
                        Object ynBaseEquipmentLongId        = map1.get("SBTZ_id")        ;
                        Object ynBaseEquipmentLongName      = map1.get("SBTZ_Name")      ;
                        Object ynBaseCustomerEnterpriseLongId        = map1.get("YDQY_id")        ;
                        Object ynBaseCustomerEnterpriseName      = map1.get("YDQY_Name")      ;
                        Object ynBaseStaffCompanyId1        = map1.get("WBGS_id")        ;
                        Object ynBaseStaffCompanyName      = map1.get("WBGS_Name")      ;
                        Object createDate     = map1.get("CreateDate")     ;
                        Object dataDate       = map1.get("DataDate")       ;
                        Object TO_AVG         = map1.get("TO_AVG")         ;
                        Object TO_MIN         = map1.get("TO_MIN")         ;
                        Object TO_MIN_DATA_TAG= map1.get("TO_MIN_DATA_TAG");
                        Object TO_MIN_TIME_TAG= map1.get("TO_MIN_TIME_TAG");
                        Object TO_MAX         = map1.get("TO_MAX")         ;
                        Object TO_MAX_DATA_TAG= map1.get("TO_MAX_DATA_TAG");
                        Object TO_MAX_TIME_TAG= map1.get("TO_MAX_TIME_TAG");
                        Object AX_AVG         = map1.get("AX_AVG")         ;
                        Object AX_MIN         = map1.get("AX_MIN")         ;
                        Object AX_MIN_DATA_TAG= map1.get("AX_MIN_DATA_TAG");
                        Object AX_MIN_TIME_TAG= map1.get("AX_MIN_TIME_TAG");
                        Object AX_MAX         = map1.get("AX_MAX")         ;
                        Object AX_MAX_DATA_TAG= map1.get("AX_MAX_DATA_TAG");
                        Object AX_MAX_TIME_TAG= map1.get("AX_MAX_TIME_TAG");
                        Object BX_AVG         = map1.get("BX_AVG")         ;
                        Object BX_MIN         = map1.get("BX_MIN")         ;
                        Object BX_MIN_DATA_TAG= map1.get("BX_MIN_DATA_TAG");
                        Object BX_MIN_TIME_TAG= map1.get("BX_MIN_TIME_TAG");
                        Object BX_MAX         = map1.get("BX_MAX")         ;
                        Object BX_MAX_DATA_TAG= map1.get("BX_MAX_DATA_TAG");
                        Object BX_MAX_TIME_TAG= map1.get("BX_MAX_TIME_TAG");
                        Object CX_AVG         = map1.get("CX_AVG")         ;
                        Object CX_MIN         = map1.get("CX_MIN")         ;
                        Object CX_MIN_DATA_TAG= map1.get("CX_MIN_DATA_TAG");
                        Object CX_MIN_TIME_TAG= map1.get("CX_MIN_TIME_TAG");
                        Object CX_MAX         = map1.get("CX_MAX")         ;
                        Object CX_MAX_DATA_TAG= map1.get("CX_MAX_DATA_TAG");
                        Object CX_MAX_TIME_TAG= map1.get("CX_MAX_TIME_TAG");
                        if(monitoringPointType==11){//单相温度
                            orderInt=1;
                        }else if(monitoringPointType==13){//三相温度
                            orderInt=2;
                        }else if(monitoringPointType==12){//湿度
                            orderInt=3;
                        }else{
                            orderInt=4;
                        }
                        if(i==0){//表示第一次
                            oldYnBaseEnterpriseSubstationLongId=  ynBaseEnterpriseSubstationLongId.toString();//前一个电房的id
                            oldMonitoringPointCoreId = monitoringPointCoreId.toString();//前一个计量点的id
                            /*********************************处理电房对象信息开始**************************************/
                            substationMap.put("ynBaseEnterpriseSubstationName",ynBaseEnterpriseSubstationName);//设置电房名称
                            String oldEquipmentContent=null;//listMonitoringMsg遍历训时候记录设备的前一个id
                            String oldMonitoringContent=null;//listMonitoringMsg遍历训时候记录监测点的前一个id
                            for(Map mapContent:listMonitoringMsg){//遍历循环企业下的所有的电房计量点信息，包括计量点名称，所属设备，监测点类型
                                Object ynBaseEnterpriseSubstationLongIdContent=mapContent.get("ynBaseEnterpriseSubstationLongId");
                                Object equipmentContent=mapContent.get("ynBaseEquipmentLongId");
                                Object monitoringContent=mapContent.get("monitoringPointCoreId");
                                if(ynBaseEnterpriseSubstationLongIdContent.toString().equals(ynBaseEnterpriseSubstationLongId.toString())){
                                    if(oldEquipmentContent==null){
                                        oldEquipmentContent=equipmentContent.toString();
                                        equimentCount++;//统计电房下设备个数
                                    }else if(!oldEquipmentContent.equals(equipmentContent)){
                                        oldEquipmentContent=equipmentContent.toString();
                                        equimentCount++;//统计电房下设备个数
                                    }
                                    if(oldMonitoringContent==null){
                                        oldMonitoringContent=monitoringContent.toString();
                                        monitoringCount++;//统计电房下计量点个数
                                    }else if(!oldMonitoringContent.equals(monitoringContent)){
                                        oldMonitoringContent=monitoringContent.toString();
                                        monitoringCount++;//统计电房下计量点个数
                                    }
                                    monitoringContentList.add(mapContent);
                                }
                            }
                            substationMap.put("monitoringsContent",monitoringContentList);//把获得的当前电房下的监测点内容放回到电房对象中
                            shebeiyunxingneirong="本次设备运行报告时间"+dateString+"，累计对电房("+ynBaseEnterpriseSubstationName+")的"+equimentCount+"个大设备进行运行状态监测，"+monitoringCount+"个监测点，设备运行健康状态详情如下";
                            substationMap.put("shebeiyunxingneirong",shebeiyunxingneirong);//把设备运行内容扔回到电房对象表中
                            shebeiyunxingneirong=null;//重置设备运行内容
                            monitoringContentList=new ArrayList<Map>();//重置监测点内容列表
                            monitoringCount=0;//重置电房下计量点个数
                            equimentCount=0;//  重置电房下设备蛇叔
                            //处理电房告警信息
                            for(Map warningMap:monitoringWarningList){
                                Object warningSubstaionId=warningMap.get("ynBaseEnterpriseSubstationLongId");
                                if(ynBaseEnterpriseSubstationLongId.toString().equals(warningSubstaionId.toString())){
                                    warningSubstations.add(warningMap);
                                }
                            }
                            substationMap.put("warningSubstation",warningSubstations);//把电房下面的所有计量点告警信息列表仍会到电房对象中
                            warningSubstations=new ArrayList<Map>();//电房告警列表初始化
                            /*********************************处理电房对象信息结束**************************************/
                            /*********************************处理计量点对象信息开始**************************************/
                            monitoringObject.put("orderInt",orderInt);//监测点的排序
                            sameMonitoringObject.put("monitoringPointName",monitoringPointName);//设置计量点公有名称
                            monitoringObject.put("monitoringPointName",monitoringPointName);//单相可以直接出计量点名称（最后决定把A相,B,C相合并所以计量点名称就直接使用就好）
                            if(monitoringPointType==1||monitoringPointType==11||monitoringPointType==12||monitoringPointType==14){//1单相,11温度单相，12湿度单相,14温湿度一体单相
                                yunxingjieguo=dateString+"，"+monitoringPointName+Tj_Type_Str+"的平均值为："+TO_AVG+"；最高值为："+TO_MAX+"("+format6Parse(format5Parse(TO_MAX_DATA_TAG+" "+TO_MAX_TIME_TAG))+")"+"；最低值为："+TO_MIN+"("+format6Parse(format5Parse(TO_MIN_DATA_TAG+" "+TO_MIN_TIME_TAG))+")";
                                yunxingxiang=Tj_Type_Str.toString();
                                monitoringObject.put("yunxingxiang",yunxingxiang);//运行项
                                monitoringObject.put("yunxingjieguo",yunxingjieguo);//运行结果
                                //开始初始化折线图列表数据,遍历当前的最大的map1拿到折线图要展现的数据
                                Iterator entries1 = map1.entrySet().iterator();
                                DefaultCategoryDataset mDataset = new DefaultCategoryDataset();
                                while (entries1.hasNext()) {
                                    Map.Entry entry1 = (Map.Entry) entries1.next();
                                    String key1 = (String)entry1.getKey();
                                    Object value1 = entry1.getValue();
//                                    if(value1==null){
//                                        value1=0;//设置一个默认值
//                                    }
//                                    String value1String=value1.toString();//先把值设置成字符串类型扔回去
                                    if(key1.contains("TO_")&&key1.length()==5){//判断获取到自己所需要的折线图数据
                                        String toMonth=key1.split("_")[1];//获取到月份的具体数字
                                        if(value1==null&&Integer.parseInt(nowMonth)>=Integer.parseInt(dataDate+toMonth)){//&&dataDate+yfDays
                                            value1=0;//设置一个默认值
                                        }
                                        if(value1!=null){
                                            String value1String=value1.toString();//先把值设置成字符串类型扔回去
                                            mDataset.addValue(Float.parseFloat(value1String), Tj_Type_Str.toString(), toMonth);
                                        }else{
                                            mDataset.addValue(null, Tj_Type_Str.toString(), toMonth);
                                        }
                                        //mDataset.addValue(Float.parseFloat(value1String), Tj_Type_Str.toString(), toMonth);
                                       /* monitoringReportObject.put("value",value1String);//Y轴
                                        monitoringReportObject.put("rowKey",Tj_Type_Str);//图例
                                        monitoringReportObject.put("columnKey",toMonth);//Y轴
                                        monitoringReportList.add(monitoringReportObject);
                                        monitoringReportObject=new HashMap();*/
                                    }
                                    // System.out.println("Key = " + key + ", Value = " + value);
                                }
                                String imgName= JavaUUIDGenerator.getUUID();
                                String savePath=tomcatImgPath+ imgName+".png";
                                Line.getDataToFileAndSaveTOThisTomcat(mDataset,savePath,1024,420);
                                /*List<String> imgNames=new ArrayList<String>();
                                imgNames.add(imgName);*/
                                List<Map> imgNames=new ArrayList<Map>();
                                Map imgObject = new HashMap();
                                imgObject.put("imgName",imgName);
                                imgObject.put("monitoringPointNameNew",monitoringPointName);
                                imgObject.put("imgFullName",imgName+".png");
                                imgNames.add(imgObject);
                                monitoringObject.put("monitoringReportList",imgNames);//把保存成功之后的图片名称和格式放回到列表中返回去
                               // monitoringObject.put("monitoringReportList",monitoringReportList);//把当前监测点折线图扔回到监测点对象中
                                monitoringReportList=new ArrayList<Map>();//重置折线图的列表对象
                                monitoringList.add(monitoringObject);
                                monitoringObject=new HashMap();//当前监测点执行完成重新初始化
                            }else if(monitoringPointType==3||monitoringPointType==13){//三相
                                yunxingjieguo=dateString+"，"+monitoringPointName
                                        +Tj_Type_Str+"A相的平均值为："+AX_AVG+"；最高值为："+AX_MAX+"("+format6Parse(format5Parse(AX_MAX_DATA_TAG+" "+AX_MAX_TIME_TAG))+")"+"；最低值为："+AX_MIN+"("+format6Parse(format5Parse(AX_MIN_DATA_TAG+" "+AX_MIN_TIME_TAG))+")"+"。"
                                        +Tj_Type_Str+"B相的平均值为："+BX_AVG+"；最高值为："+BX_MAX+"("+format6Parse(format5Parse(BX_MAX_DATA_TAG+" "+BX_MAX_TIME_TAG))+")"+"；最低值为："+BX_MIN+"("+format6Parse(format5Parse(BX_MIN_DATA_TAG+" "+BX_MIN_TIME_TAG))+")"+"。"
                                        +Tj_Type_Str+"C相的平均值为："+CX_AVG+"；最高值为："+CX_MAX+"("+format6Parse(format5Parse(CX_MAX_DATA_TAG+" "+CX_MAX_TIME_TAG))+")"+"；最低值为："+CX_MIN+"("+format6Parse(format5Parse(CX_MIN_DATA_TAG+" "+CX_MIN_TIME_TAG))+")"+"。";
                                yunxingxiang=Tj_Type_Str.toString();
                                monitoringObject.put("yunxingxiang",yunxingxiang);//运行项
                                monitoringObject.put("yunxingjieguo",yunxingjieguo);//运行结果
                                //开始初始化折线图列表数据,遍历当前的最大的map1拿到折线图要展现的数据
                                Iterator entries2 = map1.entrySet().iterator();
                                DefaultCategoryDataset mDataset = new DefaultCategoryDataset();
                                while (entries2.hasNext()) {
                                    Map.Entry entry2 = (Map.Entry) entries2.next();
                                    String key2 = (String)entry2.getKey();
                                    Object value2 = entry2.getValue();
//                                    if(value2==null){
//                                        value2=0;//设置一个默认值
//                                    }
//                                    String value1String=value2.toString();//先把值设置成字符串类型扔回去
                                    if(key2.contains("AX_")||key2.contains("BX_")||key2.contains("CX_") ){//判断获取到自己所需要的折线图数据
                                       if(key2.length()==5) {
                                           String toMonth = key2.split("_")[1];//获取到月份的具体数字
                                           if(value2==null&&Integer.parseInt(nowMonth)>=Integer.parseInt(dataDate+toMonth)){//&&dataDate+yfDays
                                               value2=0;//设置一个默认值
                                           }
                                           if(value2!=null){
                                               String value1String=value2.toString();//先把值设置成字符串类型扔回去
                                               mDataset.addValue(Float.parseFloat(value1String), Tj_Type_Str.toString()+key2.split("_")[0], toMonth);
                                           }else{
                                               mDataset.addValue(null, Tj_Type_Str.toString()+key2.split("_")[0], toMonth);
                                           }
                                          // mDataset.addValue(Float.parseFloat(value1String), Tj_Type_Str.toString()+key2.split("_")[0], toMonth);
                                          /* monitoringReportObject.put("value", value1String);//Y轴
                                           monitoringReportObject.put("rowKey", Tj_Type_Str + key2.split("_")[0]);//图例
                                           monitoringReportObject.put("columnKey", toMonth);//Y轴
                                           monitoringReportList.add(monitoringReportObject);
                                           monitoringReportObject = new HashMap();*/
                                       }
                                    }
                                    // System.out.println("Key = " + key + ", Value = " + value);
                                }
                                String imgName= JavaUUIDGenerator.getUUID();
                                String savePath=tomcatImgPath+ imgName+".png";
                                Line.getDataToFileAndSaveTOThisTomcat(mDataset,savePath,1024,420);
                                /*List<String> imgNames=new ArrayList<String>();
                                imgNames.add(imgName);*/
                                List<Map> imgNames=new ArrayList<Map>();
                                Map imgObject = new HashMap();
                                imgObject.put("imgName",imgName);
                                imgObject.put("monitoringPointNameNew",monitoringPointName);
                                imgObject.put("imgFullName",imgName+".png");
                                imgNames.add(imgObject);
                                monitoringObject.put("monitoringReportList",imgNames);//把保存成功之后的图片名称和格式放回到列表中返回去
                                //monitoringObject.put("monitoringReportList",monitoringReportList);//把当前监测点折线图扔回到监测点对象中
                                monitoringReportList=new ArrayList<Map>();//重置折线图的列表对象
                                monitoringList.add(monitoringObject);
                                monitoringObject=new HashMap();//当前监测点执行完成重新初始化
                            }
                            /*********************************处理计量点对象信息结束**************************************/
                            /****************************判断循环是否结束--程序开始*********************************/
                            if(i==monitoringMsgFromYearList.size()-1){
                                sameMonitoringObject.put("monitoringList",monitoringList);
                                sameMonitoringList.add(sameMonitoringObject);//把共有的计量点大对象放到共有计量点大列表中
                                substationMap.put("sameMonitoringList",sameMonitoringList);//把共有的计量点大列表放回到电房对象中
                                substationList.add(substationMap);//把电房对象扔回到电房列表中
                                monitoringList=new ArrayList<Map>();//初始化计量点列表
                                sameMonitoringObject=new HashMap();//初始化共有计量点大对象
                                sameMonitoringList=new ArrayList<Map>();//初始化共有计量点列表大对象
                                substationMap=new HashMap();//初始化电房对象
                            }
                            /****************************判断循环是否结束--程序结束*********************************/
                            continue;
                        }
                        //第二次的时候就需要首先判断电房id是否一致，电房id如果一致则循环遍历计量点，电房如果不一致的话需要把之前的电房信息完结掉然后重新初始化新电房的计量点信息
                        //如果电房一致，遍历计量点的时候需要判断当前和之前计量点id是否一致
                        if(oldYnBaseEnterpriseSubstationLongId.equals(ynBaseEnterpriseSubstationLongId.toString())){//判断当前循环电房id与上一个循环的电房id一致
                            if(oldMonitoringPointCoreId.equals(monitoringPointCoreId)){//判断同一个电房中，当前循环的计量点id与上一个循环的计量点id一致
                                /*********************************处理计量点对象信息开始**************************************/
                                monitoringObject.put("orderInt",orderInt);//监测点的排序
                                monitoringObject.put("monitoringPointName",monitoringPointName);//单相可以直接出计量点名称（最后决定把A相,B,C相合并所以计量点名称就直接使用就好）
                                if(monitoringPointType==1||monitoringPointType==11||monitoringPointType==12||monitoringPointType==14){//1单相,11温度单相，12湿度单相
                                    yunxingjieguo=dateString+"，"+monitoringPointName+Tj_Type_Str+"的平均值为："+TO_AVG+"；最高值为："+TO_MAX+"("+format6Parse(format5Parse(TO_MAX_DATA_TAG+" "+TO_MAX_TIME_TAG))+")"+"；最低值为："+TO_MIN+"("+format6Parse(format5Parse(TO_MIN_DATA_TAG+" "+TO_MIN_TIME_TAG))+")";
                                    yunxingxiang=Tj_Type_Str.toString();
                                    monitoringObject.put("yunxingxiang",yunxingxiang);//运行项
                                    monitoringObject.put("yunxingjieguo",yunxingjieguo);//运行结果
                                    //开始初始化折线图列表数据,遍历当前的最大的map1拿到折线图要展现的数据
                                    Iterator entries1 = map1.entrySet().iterator();
                                    DefaultCategoryDataset mDataset = new DefaultCategoryDataset();
                                    while (entries1.hasNext()) {
                                        Map.Entry entry1 = (Map.Entry) entries1.next();
                                        String key1 = (String)entry1.getKey();
                                        Object value1 = entry1.getValue();
//                                        if(value1==null){
//                                            value1=0;//设置一个默认值
//                                        }
//                                        String value1String=value1.toString();//先把值设置成字符串类型扔回去
                                        if(key1.contains("TO_")&&key1.length()==5){//判断获取到自己所需要的折线图数据
                                            String toMonth=key1.split("_")[1];//获取到月份的具体数字
                                            if(value1==null&&Integer.parseInt(nowMonth)>=Integer.parseInt(dataDate+toMonth)){//&&dataDate+yfDays
                                                value1=0;//设置一个默认值
                                            }
                                            if(value1!=null){
                                                String value1String=value1.toString();//先把值设置成字符串类型扔回去
                                                mDataset.addValue(Float.parseFloat(value1String), Tj_Type_Str.toString(), toMonth);
                                            }else{
                                                mDataset.addValue(null, Tj_Type_Str.toString(), toMonth);
                                            }
                                            //mDataset.addValue(Float.parseFloat(value1String), Tj_Type_Str.toString(), toMonth);
                                           /* monitoringReportObject.put("value",value1String);//Y轴
                                            monitoringReportObject.put("rowKey",Tj_Type_Str);//图例
                                            monitoringReportObject.put("columnKey",toMonth);//Y轴
                                            monitoringReportList.add(monitoringReportObject);
                                            monitoringReportObject=new HashMap();*/
                                        }
                                        // System.out.println("Key = " + key + ", Value = " + value);
                                    }
                                    String imgName= JavaUUIDGenerator.getUUID();
                                    String savePath=tomcatImgPath+ imgName+".png";
                                    Line.getDataToFileAndSaveTOThisTomcat(mDataset,savePath,1024,420);
                                    /*List<String> imgNames=new ArrayList<String>();
                                    imgNames.add(imgName);*/
                                    List<Map> imgNames=new ArrayList<Map>();
                                    Map imgObject = new HashMap();
                                    imgObject.put("imgName",imgName);
                                    imgObject.put("monitoringPointNameNew",monitoringPointName);
                                    imgObject.put("imgFullName",imgName+".png");
                                    imgNames.add(imgObject);
                                    monitoringObject.put("monitoringReportList",imgNames);//把保存成功之后的图片名称和格式放回到列表中返回去
                                    //monitoringObject.put("monitoringReportList",monitoringReportList);//把当前监测点折线图扔回到监测点对象中
                                    monitoringReportList=new ArrayList<Map>();//重置折线图的列表对象
                                    monitoringList.add(monitoringObject);
                                    monitoringObject=new HashMap();//当前监测点执行完成重新初始化
                                }else if(monitoringPointType==3||monitoringPointType==13){//三相
                                    yunxingjieguo=dateString+"，"+monitoringPointName
                                            +Tj_Type_Str+"A相的平均值为："+AX_AVG+"；最高值为："+AX_MAX+"("+format6Parse(format5Parse(AX_MAX_DATA_TAG+" "+AX_MAX_TIME_TAG))+")"+"；最低值为："+AX_MIN+"("+format6Parse(format5Parse(AX_MIN_DATA_TAG+" "+AX_MIN_TIME_TAG))+")"+"。"
                                            +Tj_Type_Str+"B相的平均值为："+BX_AVG+"；最高值为："+BX_MAX+"("+format6Parse(format5Parse(BX_MAX_DATA_TAG+" "+BX_MAX_TIME_TAG))+")"+"；最低值为："+BX_MIN+"("+format6Parse(format5Parse(BX_MIN_DATA_TAG+" "+BX_MIN_TIME_TAG))+")"+"。"
                                            +Tj_Type_Str+"C相的平均值为："+CX_AVG+"；最高值为："+CX_MAX+"("+format6Parse(format5Parse(CX_MAX_DATA_TAG+" "+CX_MAX_TIME_TAG))+")"+"；最低值为："+CX_MIN+"("+format6Parse(format5Parse(CX_MIN_DATA_TAG+" "+CX_MIN_TIME_TAG))+")"+"。";
                                    yunxingxiang=Tj_Type_Str.toString();
                                    monitoringObject.put("yunxingxiang",yunxingxiang);//运行项
                                    monitoringObject.put("yunxingjieguo",yunxingjieguo);//运行结果
                                    //开始初始化折线图列表数据,遍历当前的最大的map1拿到折线图要展现的数据
                                    Iterator entries2 = map1.entrySet().iterator();
                                    DefaultCategoryDataset mDataset = new DefaultCategoryDataset();
                                    while (entries2.hasNext()) {
                                        Map.Entry entry2 = (Map.Entry) entries2.next();
                                        String key2 = (String)entry2.getKey();
                                        Object value2 = entry2.getValue();
//                                        if(value2==null){
//                                            value2=0;//设置一个默认值
//                                        }
//                                        String value1String=value2.toString();//先把值设置成字符串类型扔回去
                                        if(key2.contains("AX_")||key2.contains("BX_")||key2.contains("CX_") ){//判断获取到自己所需要的折线图数据
                                            if(key2.length()==5){//必须在做一个判断否则平均值，最高值，最低值也进来了，因为那也是以TO_,AX_,BX_,CX_打头的
                                                String toMonth=key2.split("_")[1];//获取到月份的具体数字
                                                if(value2==null&&Integer.parseInt(nowMonth)>=Integer.parseInt(dataDate+toMonth)){//&&dataDate+yfDays
                                                    value2=0;//设置一个默认值
                                                }
                                                if(value2!=null){
                                                    String value1String=value2.toString();//先把值设置成字符串类型扔回去
                                                    mDataset.addValue(Float.parseFloat(value1String), Tj_Type_Str.toString()+key2.split("_")[0], toMonth);
                                                }else{
                                                    mDataset.addValue(null, Tj_Type_Str.toString()+key2.split("_")[0], toMonth);
                                                }
                                              //  mDataset.addValue(Float.parseFloat(value1String), Tj_Type_Str.toString()+key2.split("_")[0], toMonth);
                                               /* monitoringReportObject.put("value",value1String);//Y轴
                                                monitoringReportObject.put("rowKey",Tj_Type_Str+key2.split("_")[0]);//图例
                                                monitoringReportObject.put("columnKey",toMonth);//Y轴
                                                monitoringReportList.add(monitoringReportObject);
                                                monitoringReportObject=new HashMap();*/
                                            }

                                        }
                                        // System.out.println("Key = " + key + ", Value = " + value);
                                    }
                                    String imgName= JavaUUIDGenerator.getUUID();
                                    String savePath=tomcatImgPath+ imgName+".png";
                                    Line.getDataToFileAndSaveTOThisTomcat(mDataset,savePath,1024,420);
                                    /*List<String> imgNames=new ArrayList<String>();
                                    imgNames.add(imgName);*/
                                    List<Map> imgNames=new ArrayList<Map>();
                                    Map imgObject = new HashMap();
                                    imgObject.put("imgName",imgName);
                                    imgObject.put("monitoringPointNameNew",monitoringPointName);
                                    imgObject.put("imgFullName",imgName+".png");
                                    imgNames.add(imgObject);
                                    monitoringObject.put("monitoringReportList",imgNames);//把保存成功之后的图片名称和格式放回到列表中返回去
                                    //monitoringObject.put("monitoringReportList",monitoringReportList);//把当前监测点折线图扔回到监测点对象中
                                    monitoringReportList=new ArrayList<Map>();//重置折线图的列表对象
                                    monitoringList.add(monitoringObject);
                                    monitoringObject=new HashMap();//当前监测点执行完成重新初始化
                                }
                                /*********************************处理计量点对象信息结束**************************************/
                            }else{//判断同一个电房中，当前循环的计量点id与上一个循环的计量点id不一致
                                //把当前监测点列表放回到相同计量点统计的对象中，并扔回相同计量点统计列表然后初始化相同计量点统计的对象和计量点列表
                                sameMonitoringObject.put("monitoringList",monitoringList);//把计量点列表仍回到共有计量点大对象中
                                sameMonitoringList.add(sameMonitoringObject);//把共有计量点大对象扔回到共有计量点大列表中
                                monitoringList=new ArrayList<Map>();//初始化计量点列表
                                sameMonitoringObject=new HashMap();//初始化共有计量点大对象
                                oldMonitoringPointCoreId = monitoringPointCoreId.toString();//把当前计量点id记录到前一个计量点id中
                                /*********************************处理计量点对象信息开始**************************************/
                                monitoringObject.put("orderInt",orderInt);//监测点的排序
                                sameMonitoringObject.put("monitoringPointName",monitoringPointName);//设置计量点公有名称
                                monitoringObject.put("monitoringPointName",monitoringPointName);//单相可以直接出计量点名称（最后决定把A相,B,C相合并所以计量点名称就直接使用就好）
                                if(monitoringPointType==1||monitoringPointType==11||monitoringPointType==12||monitoringPointType==14){//1单相,11温度单相，12湿度单相
                                    yunxingjieguo=dateString+"，"+monitoringPointName+Tj_Type_Str+"的平均值为："+TO_AVG+"；最高值为："+TO_MAX+"("+format6Parse(format5Parse(TO_MAX_DATA_TAG+" "+TO_MAX_TIME_TAG))+")"+"；最低值为："+TO_MIN+"("+format6Parse(format5Parse(TO_MIN_DATA_TAG+" "+TO_MIN_TIME_TAG))+")";
                                    yunxingxiang=Tj_Type_Str.toString();
                                    monitoringObject.put("yunxingxiang",yunxingxiang);//运行项
                                    monitoringObject.put("yunxingjieguo",yunxingjieguo);//运行结果
                                    //开始初始化折线图列表数据,遍历当前的最大的map1拿到折线图要展现的数据
                                    Iterator entries1 = map1.entrySet().iterator();
                                    DefaultCategoryDataset mDataset = new DefaultCategoryDataset();
                                    while (entries1.hasNext()) {
                                        Map.Entry entry1 = (Map.Entry) entries1.next();
                                        String key1 = (String)entry1.getKey();
                                        Object value1 = entry1.getValue();
//                                        if(value1==null){
//                                            value1=0;//设置一个默认值
//                                        }
//                                        String value1String=value1.toString();//先把值设置成字符串类型扔回去
                                        if(key1.contains("TO_")&&key1.length()==5){//判断获取到自己所需要的折线图数据
                                            String toMonth=key1.split("_")[1];//获取到月份的具体数字
                                            if(value1==null&&Integer.parseInt(nowMonth)>=Integer.parseInt(dataDate+toMonth)){//&&dataDate+yfDays
                                                value1=0;//设置一个默认值
                                            }
                                            if(value1!=null){
                                                String value1String=value1.toString();//先把值设置成字符串类型扔回去
                                                mDataset.addValue(Float.parseFloat(value1String), Tj_Type_Str.toString(), toMonth);
                                            }else{
                                                mDataset.addValue(null, Tj_Type_Str.toString(), toMonth);
                                            }
                                            //mDataset.addValue(Float.parseFloat(value1String), Tj_Type_Str.toString(), toMonth);
                                           /* monitoringReportObject.put("value",value1String);//Y轴
                                            monitoringReportObject.put("rowKey",Tj_Type_Str);//图例
                                            monitoringReportObject.put("columnKey",toMonth);//Y轴
                                            monitoringReportList.add(monitoringReportObject);
                                            monitoringReportObject=new HashMap();*/
                                        }
                                        // System.out.println("Key = " + key + ", Value = " + value);
                                    }
                                    String imgName= JavaUUIDGenerator.getUUID();
                                    String savePath=tomcatImgPath+ imgName+".png";
                                    Line.getDataToFileAndSaveTOThisTomcat(mDataset,savePath,1024,420);
                                    /*List<String> imgNames=new ArrayList<String>();
                                    imgNames.add(imgName);*/
                                    List<Map> imgNames=new ArrayList<Map>();
                                    Map imgObject = new HashMap();
                                    imgObject.put("imgName",imgName);
                                    imgObject.put("monitoringPointNameNew",monitoringPointName);
                                    imgObject.put("imgFullName",imgName+".png");
                                    imgNames.add(imgObject);
                                    monitoringObject.put("monitoringReportList",imgNames);//把保存成功之后的图片名称和格式放回到列表中返回去
                                    //monitoringObject.put("monitoringReportList",monitoringReportList);//把当前监测点折线图扔回到监测点对象中
                                    monitoringReportList=new ArrayList<Map>();//重置折线图的列表对象
                                    monitoringList.add(monitoringObject);
                                    monitoringObject=new HashMap();//当前监测点执行完成重新初始化
                                }else if(monitoringPointType==3||monitoringPointType==13){//三相
                                    yunxingjieguo=dateString+"，"+monitoringPointName
                                            +Tj_Type_Str+"A相的平均值为："+AX_AVG+"；最高值为："+AX_MAX+"("+format6Parse(format5Parse(AX_MAX_DATA_TAG+" "+AX_MAX_TIME_TAG))+")"+"；最低值为："+AX_MIN+"("+format6Parse(format5Parse(AX_MIN_DATA_TAG+" "+AX_MIN_TIME_TAG))+")"+"。"
                                            +Tj_Type_Str+"B相的平均值为："+BX_AVG+"；最高值为："+BX_MAX+"("+format6Parse(format5Parse(BX_MAX_DATA_TAG+" "+BX_MAX_TIME_TAG))+")"+"；最低值为："+BX_MIN+"("+format6Parse(format5Parse(BX_MIN_DATA_TAG+" "+BX_MIN_TIME_TAG))+")"+"。"
                                            +Tj_Type_Str+"C相的平均值为："+CX_AVG+"；最高值为："+CX_MAX+"("+format6Parse(format5Parse(CX_MAX_DATA_TAG+" "+CX_MAX_TIME_TAG))+")"+"；最低值为："+CX_MIN+"("+format6Parse(format5Parse(CX_MIN_DATA_TAG+" "+CX_MIN_TIME_TAG))+")"+"。";
                                    yunxingxiang=Tj_Type_Str.toString();
                                    monitoringObject.put("yunxingxiang",yunxingxiang);//运行项
                                    monitoringObject.put("yunxingjieguo",yunxingjieguo);//运行结果
                                    //开始初始化折线图列表数据,遍历当前的最大的map1拿到折线图要展现的数据
                                    Iterator entries2 = map1.entrySet().iterator();
                                    DefaultCategoryDataset mDataset = new DefaultCategoryDataset();
                                    while (entries2.hasNext()) {
                                        Map.Entry entry2 = (Map.Entry) entries2.next();
                                        String key2 = (String)entry2.getKey();
                                        Object value2 = entry2.getValue();
//                                        if(value2==null){
//                                            value2=0;//设置一个默认值
//                                        }
//                                        String value1String=value2.toString();//先把值设置成字符串类型扔回去
                                        if(key2.contains("AX_")||key2.contains("BX_")||key2.contains("CX_") ){//判断获取到自己所需要的折线图数据
                                            if(key2.length()==5){//必须在做一个判断否则平均值，最高值，最低值也进来了，因为那也是以TO_,AX_,BX_,CX_打头的
                                                String toMonth=key2.split("_")[1];//获取到月份的具体数字
                                                if(value2==null&&Integer.parseInt(nowMonth)>=Integer.parseInt(dataDate+toMonth)){//&&dataDate+yfDays
                                                    value2=0;//设置一个默认值
                                                }
                                                if(value2!=null){
                                                    String value1String=value2.toString();//先把值设置成字符串类型扔回去
                                                    mDataset.addValue(Float.parseFloat(value1String), Tj_Type_Str.toString()+key2.split("_")[0], toMonth);
                                                }else{
                                                    mDataset.addValue(null, Tj_Type_Str.toString()+key2.split("_")[0], toMonth);
                                                }
                                               // mDataset.addValue(Float.parseFloat(value1String), Tj_Type_Str.toString()+key2.split("_")[0], toMonth);
                                                /*monitoringReportObject.put("value",value1String);//Y轴
                                                monitoringReportObject.put("rowKey",Tj_Type_Str+key2.split("_")[0]);//图例
                                                monitoringReportObject.put("columnKey",toMonth);//Y轴
                                                monitoringReportList.add(monitoringReportObject);
                                                monitoringReportObject=new HashMap();*/
                                            }

                                        }
                                        // System.out.println("Key = " + key + ", Value = " + value);
                                    }
                                    String imgName= JavaUUIDGenerator.getUUID();
                                    String savePath=tomcatImgPath+ imgName+".png";
                                    Line.getDataToFileAndSaveTOThisTomcat(mDataset,savePath,1024,420);
                                   /* List<String> imgNames=new ArrayList<String>();
                                    imgNames.add(imgName);*/
                                    List<Map> imgNames=new ArrayList<Map>();
                                    Map imgObject = new HashMap();
                                    imgObject.put("imgName",imgName);
                                    imgObject.put("monitoringPointNameNew",monitoringPointName);
                                    imgObject.put("imgFullName",imgName+".png");
                                    imgNames.add(imgObject);
                                    monitoringObject.put("monitoringReportList",imgNames);//把保存成功之后的图片名称和格式放回到列表中返回去
                                    //monitoringObject.put("monitoringReportList",monitoringReportList);//把当前监测点折线图扔回到监测点对象中
                                    monitoringReportList=new ArrayList<Map>();//重置折线图的列表对象
                                    monitoringList.add(monitoringObject);
                                    monitoringObject=new HashMap();//当前监测点执行完成重新初始化
                                }
                                /*********************************处理计量点对象信息结束**************************************/
                            }
                        }else{//判断当前循环电房id与上一个循环的电房id不一致，把之前的电房信息完结掉然后重新初始化新电房的计量点信息
                            sameMonitoringObject.put("monitoringList",monitoringList);
                            sameMonitoringList.add(sameMonitoringObject);//把共有的计量点大对象放到共有计量点大列表中
                            substationMap.put("sameMonitoringList",sameMonitoringList);//把共有的计量点大列表放回到电房对象中
                            substationList.add(substationMap);//把电房对象扔回到电房列表中
                            monitoringList=new ArrayList<Map>();//初始化计量点列表
                            sameMonitoringObject=new HashMap();//初始化共有计量点大对象
                            sameMonitoringList=new ArrayList<Map>();//初始化共有计量点列表大对象
                            substationMap=new HashMap();//初始化电房对象
                            //oldYnBaseEnterpriseSubstationLongId=  ynBaseEnterpriseSubstationLongId.toString();//把当前电房id设置为前一个电房的id
                            oldYnBaseEnterpriseSubstationLongId=  ynBaseEnterpriseSubstationLongId.toString();//前一个电房的id
                            oldMonitoringPointCoreId = monitoringPointCoreId.toString();//前一个计量点的id
                            /*********************************处理电房对象信息开始**************************************/
                            substationMap.put("ynBaseEnterpriseSubstationName",ynBaseEnterpriseSubstationName);//设置电房名称
                            String oldEquipmentContent=null;//listMonitoringMsg遍历训时候记录设备的前一个id
                            String oldMonitoringContent=null;//listMonitoringMsg遍历训时候记录监测点的前一个id
                            for(Map mapContent:listMonitoringMsg){//遍历循环企业下的所有的电房计量点信息，包括计量点名称，所属设备，监测点类型
                                Object equipmentContent=mapContent.get("ynBaseEquipmentLongId");
                                Object monitoringContent=mapContent.get("monitoringPointCoreId");
                                Object ynBaseEnterpriseSubstationLongIdContent=mapContent.get("ynBaseEnterpriseSubstationLongId");
                                if(ynBaseEnterpriseSubstationLongIdContent.toString().equals(ynBaseEnterpriseSubstationLongId.toString())){
                                    if(oldEquipmentContent==null){
                                        oldEquipmentContent=equipmentContent.toString();
                                        equimentCount++;//统计电房下设备个数
                                    }else if(!oldEquipmentContent.equals(equipmentContent)){
                                        oldEquipmentContent=equipmentContent.toString();
                                        equimentCount++;//统计电房下设备个数
                                    }
                                    if(oldMonitoringContent==null){
                                        oldMonitoringContent=monitoringContent.toString();
                                        monitoringCount++;//统计电房下计量点个数
                                    }else if(!oldMonitoringContent.equals(monitoringContent)){
                                        oldMonitoringContent=monitoringContent.toString();
                                        monitoringCount++;//统计电房下计量点个数
                                    }
                                    monitoringContentList.add(mapContent);
                                }
                            }
                            substationMap.put("monitoringsContent",monitoringContentList);//把获得的当前电房下的监测点内容放回到电房对象中
                            shebeiyunxingneirong="本次设备运行报告时间"+dateString+"，累计对电房("+ynBaseEnterpriseSubstationName+")的"+equimentCount+"个大设备进行运行状态监测，"+monitoringCount+"个监测点，设备运行健康状态详情如下";
                            substationMap.put("shebeiyunxingneirong",shebeiyunxingneirong);//把设备运行内容扔回到电房对象表中
                            shebeiyunxingneirong=null;//重置设备运行内容
                            monitoringContentList=new ArrayList<Map>();//重置监测点内容列表
                            monitoringCount=0;//重置电房下计量点个数
                            equimentCount=0;//  重置电房下设备蛇叔
                            //处理电房告警信息
                            for(Map warningMap:monitoringWarningList){
                                Object warningSubstaionId=warningMap.get("ynBaseEnterpriseSubstationLongId");
                                if(ynBaseEnterpriseSubstationLongId.equals(warningSubstaionId.toString())){
                                    warningSubstations.add(warningMap);
                                }
                            }
                            substationMap.put("warningSubstation",warningSubstations);//把电房下面的所有计量点告警信息列表仍会到电房对象中
                            warningSubstations=new ArrayList<Map>();//电房告警列表初始化
                            /*********************************处理电房对象信息结束**************************************/
                            /*********************************处理计量点对象信息开始**************************************/
                            monitoringObject.put("orderInt",orderInt);//监测点的排序
                            sameMonitoringObject.put("monitoringPointName",monitoringPointName);//设置计量点公有名称
                            monitoringObject.put("monitoringPointName",monitoringPointName);//单相可以直接出计量点名称（最后决定把A相,B,C相合并所以计量点名称就直接使用就好）
                            if(monitoringPointType==1||monitoringPointType==11||monitoringPointType==12||monitoringPointType==14){//1单相,11温度单相，12湿度单相
                                yunxingjieguo=dateString+"，"+monitoringPointName+Tj_Type_Str+"的平均值为："+TO_AVG+"；最高值为："+TO_MAX+"("+format6Parse(format5Parse(TO_MAX_DATA_TAG+" "+TO_MAX_TIME_TAG))+")"+"；最低值为："+TO_MIN+"("+format6Parse(format5Parse(TO_MIN_DATA_TAG+" "+TO_MIN_TIME_TAG))+")";
                                yunxingxiang=Tj_Type_Str.toString();
                                monitoringObject.put("yunxingxiang",yunxingxiang);//运行项
                                monitoringObject.put("yunxingjieguo",yunxingjieguo);//运行结果
                                //开始初始化折线图列表数据,遍历当前的最大的map1拿到折线图要展现的数据
                                Iterator entries1 = map1.entrySet().iterator();
                                DefaultCategoryDataset mDataset = new DefaultCategoryDataset();
                                while (entries1.hasNext()) {
                                    Map.Entry entry1 = (Map.Entry) entries1.next();
                                    String key1 = (String)entry1.getKey();
                                    Object value1 = entry1.getValue();
//                                    if(value1==null){
//                                        value1=0;//设置一个默认值
//                                    }
//                                    String value1String=value1.toString();//先把值设置成字符串类型扔回去
                                    if(key1.contains("TO_")&&key1.length()==5){//判断获取到自己所需要的折线图数据
                                        String toMonth=key1.split("_")[1];//获取到月份的具体数字
                                        if(value1==null&&Integer.parseInt(nowMonth)>=Integer.parseInt(dataDate+toMonth)){//&&dataDate+yfDays
                                            value1=0;//设置一个默认值
                                        }
                                        if(value1!=null){
                                            String value1String=value1.toString();//先把值设置成字符串类型扔回去
                                            mDataset.addValue(Float.parseFloat(value1String), Tj_Type_Str.toString(), toMonth);
                                        }else{
                                            mDataset.addValue(null, Tj_Type_Str.toString(), toMonth);
                                        }
                                        //mDataset.addValue(Float.parseFloat(value1String), Tj_Type_Str.toString(), toMonth);
                                       /* monitoringReportObject.put("value",value1String);//Y轴
                                        monitoringReportObject.put("rowKey",Tj_Type_Str);//图例
                                        monitoringReportObject.put("columnKey",toMonth);//Y轴
                                        monitoringReportList.add(monitoringReportObject);
                                        monitoringReportObject=new HashMap();*/
                                    }
                                    // System.out.println("Key = " + key + ", Value = " + value);
                                }
                                String imgName= JavaUUIDGenerator.getUUID();
                                String savePath=tomcatImgPath+ imgName+".png";
                                Line.getDataToFileAndSaveTOThisTomcat(mDataset,savePath,1024,420);
                                /*List<String> imgNames=new ArrayList<String>();
                                imgNames.add(imgName);*/
                                List<Map> imgNames=new ArrayList<Map>();
                                Map imgObject = new HashMap();
                                imgObject.put("imgName",imgName);
                                imgObject.put("monitoringPointNameNew",monitoringPointName);
                                imgObject.put("imgFullName",imgName+".png");
                                imgNames.add(imgObject);
                                monitoringObject.put("monitoringReportList",imgNames);//把保存成功之后的图片名称和格式放回到列表中返回去
                                //monitoringObject.put("monitoringReportList",monitoringReportList);//把当前监测点折线图扔回到监测点对象中
                                monitoringReportList=new ArrayList<Map>();//重置折线图的列表对象
                                monitoringList.add(monitoringObject);
                                monitoringObject=new HashMap();//当前监测点执行完成重新初始化
                            }else if(monitoringPointType==3||monitoringPointType==13){//三相
                                yunxingjieguo=dateString+"，"+monitoringPointName
                                        +Tj_Type_Str+"A相的平均值为："+AX_AVG+"；最高值为："+AX_MAX+"("+format6Parse(format5Parse(AX_MAX_DATA_TAG+" "+AX_MAX_TIME_TAG))+")"+"；最低值为："+AX_MIN+"("+format6Parse(format5Parse(AX_MIN_DATA_TAG+" "+AX_MIN_TIME_TAG))+")"+"。"
                                        +Tj_Type_Str+"B相的平均值为："+BX_AVG+"；最高值为："+BX_MAX+"("+format6Parse(format5Parse(BX_MAX_DATA_TAG+" "+BX_MAX_TIME_TAG))+")"+"；最低值为："+BX_MIN+"("+format6Parse(format5Parse(BX_MIN_DATA_TAG+" "+BX_MIN_TIME_TAG))+")"+"。"
                                        +Tj_Type_Str+"C相的平均值为："+CX_AVG+"；最高值为："+CX_MAX+"("+format6Parse(format5Parse(CX_MAX_DATA_TAG+" "+CX_MAX_TIME_TAG))+")"+"；最低值为："+CX_MIN+"("+format6Parse(format5Parse(CX_MIN_DATA_TAG+" "+CX_MIN_TIME_TAG))+")"+"。";
                                yunxingxiang=Tj_Type_Str.toString();
                                monitoringObject.put("yunxingxiang",yunxingxiang);//运行项
                                monitoringObject.put("yunxingjieguo",yunxingjieguo);//运行结果
                                //开始初始化折线图列表数据,遍历当前的最大的map1拿到折线图要展现的数据
                                Iterator entries2 = map1.entrySet().iterator();
                                DefaultCategoryDataset mDataset = new DefaultCategoryDataset();
                                while (entries2.hasNext()) {
                                    Map.Entry entry2 = (Map.Entry) entries2.next();
                                    String key2 = (String)entry2.getKey();
                                    Object value2 = entry2.getValue();
//                                    if(value2==null){
//                                        value2=0;//设置一个默认值
//                                    }
//                                    String value1String=value2.toString();//先把值设置成字符串类型扔回去
                                    if(key2.contains("AX_")||key2.contains("BX_")||key2.contains("CX_") ){//判断获取到自己所需要的折线图数据
                                        if(key2.length()==5){//必须在做一个判断否则平均值，最高值，最低值也进来了，因为那也是以TO_,AX_,BX_,CX_打头的
                                            String toMonth=key2.split("_")[1];//获取到月份的具体数字
                                            if(value2==null&&Integer.parseInt(nowMonth)>=Integer.parseInt(dataDate+toMonth)){//&&dataDate+yfDays
                                                value2=0;//设置一个默认值
                                            }
                                            if(value2!=null){
                                                String value1String=value2.toString();//先把值设置成字符串类型扔回去
                                                mDataset.addValue(Float.parseFloat(value1String), Tj_Type_Str.toString()+key2.split("_")[0], toMonth);
                                            }else{
                                                mDataset.addValue(null, Tj_Type_Str.toString()+key2.split("_")[0], toMonth);
                                            }
                                            //mDataset.addValue(Float.parseFloat(value1String), Tj_Type_Str.toString()+key2.split("_")[0], toMonth);
                                            /*monitoringReportObject.put("value",value1String);//Y轴
                                            monitoringReportObject.put("rowKey",Tj_Type_Str+key2.split("_")[0]);//图例
                                            monitoringReportObject.put("columnKey",toMonth);//Y轴
                                            monitoringReportList.add(monitoringReportObject);
                                            monitoringReportObject=new HashMap();*/
                                        }
                                    }
                                    // System.out.println("Key = " + key + ", Value = " + value);
                                }
                                String imgName= JavaUUIDGenerator.getUUID();
                                String savePath=tomcatImgPath+ imgName+".png";
                                Line.getDataToFileAndSaveTOThisTomcat(mDataset,savePath,1024,420);
                                /*List<String> imgNames=new ArrayList<String>();
                                imgNames.add(imgName);*/
                                List<Map> imgNames=new ArrayList<Map>();
                                Map imgObject = new HashMap();
                                imgObject.put("imgName",imgName);
                                imgObject.put("monitoringPointNameNew",monitoringPointName);
                                imgObject.put("imgFullName",imgName+".png");
                                imgNames.add(imgObject);
                                monitoringObject.put("monitoringReportList",imgNames);//把保存成功之后的图片名称和格式放回到列表中返回去
                                //monitoringObject.put("monitoringReportList",monitoringReportList);//把当前监测点折线图扔回到监测点对象中
                                monitoringReportList=new ArrayList<Map>();//重置折线图的列表对象
                                monitoringList.add(monitoringObject);
                                monitoringObject=new HashMap();//当前监测点执行完成重新初始化
                            }
                            /*********************************处理计量点对象信息结束**************************************/
                        }

                        /****************************判断循环是否结束--程序开始*********************************/
                        if(i==monitoringMsgFromYearList.size()-1){
                            sameMonitoringObject.put("monitoringList",monitoringList);
                            sameMonitoringList.add(sameMonitoringObject);//把共有的计量点大对象放到共有计量点大列表中
                            substationMap.put("sameMonitoringList",sameMonitoringList);//把共有的计量点大列表放回到电房对象中
                            substationList.add(substationMap);//把电房对象扔回到电房列表中
                            monitoringList=new ArrayList<Map>();//初始化计量点列表
                            sameMonitoringObject=new HashMap();//初始化共有计量点大对象
                            sameMonitoringList=new ArrayList<Map>();//初始化共有计量点列表大对象
                            substationMap=new HashMap();//初始化电房对象
                        }
                        /****************************判断循环是否结束--程序结束*********************************/
                    }//for 结束
                    detailDTO.setDetail(substationList);
                }//查询统计年份表结束
            }else{
                detailDTO.setStatus(false);
                detailDTO.setErrorCode("-1");
                detailDTO.setErrorMessage("年份year不能为空！");
                return detailDTO;
            }

        } else {
            detailDTO.setStatus(false);
            detailDTO.setErrorCode("-1");
            detailDTO.setErrorMessage("根据用电企业id：" + onLineMonitoringDTO.getYnBaseCustomerEnterpriseLongId() + "查询维保公司信息失败，请查看yj_info表");
            return detailDTO;
        }
        return detailDTO;
    }
    //获取到配置表的信息
    public DetailDTO selectWarningConfigByObject(MonitoringObjectDTO monitoringObjectDTO, LoginAppDTO appLoginUser) {
        DetailDTO detailDTO=new DetailDTO(true);
        List<Map> idjcdList = ynMonitoringInitMapper.selectIDJCD(monitoringObjectDTO.getMonitoringPointAliasId());
        String idJCD = ((Map)idjcdList.get(0)).get("idJCD").toString();
        List<LinkedHashMap> reGJList = ynMonitoringInitMapper.selectGJMessage(idJCD);
        if(reGJList!=null&&reGJList.size()>0){
            detailDTO.setDetail(reGJList);
        }else{
            detailDTO.setDetail(new ArrayList<LinkedHashMap>());
        }
        return detailDTO;
    }
   //修改配置表信息
    public DetailDTO updateWarningConfig(BigWarningConfigDTO bigWarningConfigDTO, LoginAppDTO appLoginUser) {
        DetailDTO detailDTO = new DetailDTO(true);
        String warningTypeList=null;//类型(长字符串）
        String warningStatusList=null;//状态1启用，0禁用(长字符串）
        String warningValueList=null;//上限值(长字符串）
        String warningUnitList=null;//单位(长字符串）
        String ratedCurrent=null;//额定电流
        String ratedVoltage=null;//额定电压
        String ratedCapacity=null;//额定容量
        String ratedTemperature=null;//额定温度
        String ratedHumidity=null;//额定湿度
        for(WarningConfigDTO warningConfigDTO:bigWarningConfigDTO.getWarningConfigDTOList()){
            if(warningConfigDTO.getWarningType()=="DY"||warningConfigDTO.getWarningType().equals("DY")){
                ratedVoltage=warningConfigDTO.getRatedValue();
            }else if(warningConfigDTO.getWarningType()=="DL"||warningConfigDTO.getWarningType().equals("DL")){
                ratedCurrent=warningConfigDTO.getRatedValue();
            }else if(warningConfigDTO.getWarningType()=="GL"||warningConfigDTO.getWarningType().equals("GL")){
                ratedCapacity=warningConfigDTO.getRatedValue();
            }else if(warningConfigDTO.getWarningType()=="WD"||warningConfigDTO.getWarningType().equals("WD")){
                ratedTemperature=warningConfigDTO.getRatedValue();
            }else if(warningConfigDTO.getWarningType()=="SD"||warningConfigDTO.getWarningType().equals("SD")){
                ratedHumidity=warningConfigDTO.getRatedValue();
            }
            if(warningTypeList==null||warningTypeList.equals("")){
                warningTypeList=warningConfigDTO.getWarningType();
            }else{
                warningTypeList+=","+warningConfigDTO.getWarningType();
            }
            if(warningStatusList==null||warningStatusList.equals("")){
                warningStatusList=warningConfigDTO.getWarningStatus();
            }else{
                warningStatusList+=","+warningConfigDTO.getWarningStatus();
            }
            if(warningValueList==null||warningValueList.equals("")){
                warningValueList=warningConfigDTO.getWarningValue();
            }else{
                warningValueList+=","+warningConfigDTO.getWarningValue();
            }
            if(warningUnitList==null||warningUnitList.equals("")){
                warningUnitList=warningConfigDTO.getWarningUnit();
            }else{
                warningUnitList+=","+warningConfigDTO.getWarningUnit();
            }
        }
        Map paramsMap= new HashMap();
        paramsMap.put("warningTypeList",warningTypeList);
        paramsMap.put("warningStatusList",warningStatusList);
        paramsMap.put("warningValueList",warningValueList);
        paramsMap.put("warningUnitList",warningUnitList);
        paramsMap.put("monitoringPointAliasId",bigWarningConfigDTO.getMonitoringPointAliasId()) ;
        paramsMap.put("ratedCurrent",ratedCurrent);
        paramsMap.put("ratedVoltage",ratedVoltage);
        paramsMap.put("ratedCapacity",ratedCapacity);
        paramsMap.put("ratedTemperature",ratedTemperature);
        paramsMap.put("ratedHumidity",ratedHumidity);
        ynMonitoringInitMapper.updateWarningConfig(paramsMap);

        //调用博纳把告警信息需要的参数转过去
        String s = boNaService.sendGJParameter(bigWarningConfigDTO);



        return detailDTO;
    }
    //根据用电企业id查询dtu信息
    public DetailDTO selectDTUMsgByEterpriseId(MonitoringObjectDTO monitoringObjectDTO, LoginAppDTO appLoginUser) {
        DetailDTO detailDTO=new DetailDTO(true);
        List<LinkedHashMap> mapList=ynMonitoringInitMapper.selectDTUMsgByEterpriseId(monitoringObjectDTO);
        if(mapList!=null&&mapList.size()>0){
            detailDTO.setDetail(mapList);
        }else{
            detailDTO.setDetail(new ArrayList<LinkedHashMap>());
        }
        return detailDTO;
    }

    //获取通讯规约的所有枚举
    public DetailDTO selectMonitoringProtocol(OnLineMonitoringDTO onLineMonitoringDTO, LoginAppDTO appLoginUser) {
        DetailDTO detailDTO=new DetailDTO(true);
        List<LinkedHashMap> mapList=ynMonitoringInitMapper.selectMonitoringProtocol(onLineMonitoringDTO);
        if(mapList!=null&&mapList.size()>0){
            detailDTO.setDetail(mapList);
        }else{
            detailDTO.setDetail(new ArrayList<LinkedHashMap>());
        }
        return detailDTO;
    }

    public DetailDTO selectMonitoringMStatus(OnLineMonitoringDTO onLineMonitoringDTO, LoginAppDTO appLoginUser) {
        DetailDTO detailDTO=new DetailDTO(true);
        List<LinkedHashMap> mapList=ynMonitoringInitMapper.selectMonitoringMStatus(onLineMonitoringDTO);
        if(mapList!=null&&mapList.size()>0){
            detailDTO.setDetail(mapList);
        }else{
            detailDTO.setDetail(new ArrayList<LinkedHashMap>());
        }
        return detailDTO;
    }



    //获取计量点类型（终端类型）
    public DetailDTO selectMonitoringPointType(OnLineMonitoringDTO onLineMonitoringDTO, LoginAppDTO appLoginUser) {
        DetailDTO detailDTO=new DetailDTO(true);
        List<LinkedHashMap> mapList=ynMonitoringInitMapper.selectMonitoringPointType(onLineMonitoringDTO);
        if(mapList!=null&&mapList.size()>0){
            detailDTO.setDetail(mapList);
        }else{
            detailDTO.setDetail(new ArrayList<LinkedHashMap>());
        }
        return detailDTO;
    }

    public DetailDTO addMonitoring(MonitoringObjectDTO monitoringObjectDTO, LoginAppDTO appLoginUser) {
        DetailDTO detailDTO=new DetailDTO(true);
       // int intsertInt =ynMonitoringInitMapper.insertIntoMonitoring(monitoringObjectDTO);
        monitoringObjectDTO.setMonitoringPointAliasId(monitoringObjectDTO.getYnBaseStaffCompanyId()+"_"+monitoringObjectDTO.getYnBaseCustomerEnterpriseLongId()+"_"+monitoringObjectDTO.getYnBaseEquipmentLongId()+"_"+monitoringObjectDTO.getMonitoringSimCardInnerNo()+"_"+monitoringObjectDTO.getMonitoringPointLogicAddr());
        //调用汤总存储过程 INIT_JCD
        List<LinkedHashMap> mapList1=ynMonitoringInitMapper.initJcd(monitoringObjectDTO);
        if(mapList1!=null&&mapList1.size()>0){
            try {
                System.out.println("计量点新增add："+Utils.objectToJson(mapList1.get(0)));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            Map map = mapList1.get(0);
            Object o=map.get("c");
            System.out.println(o);
            String c=o.toString();// (String) mapList1.get(0).get("c");
            System.out.println(c);
            if(c=="1"||c.equals("1")){
            /*
                //调用汤总存储过程 JCD_SET_Location
                List<LinkedHashMap> mapList2=ynMonitoringInitMapper.jcdSetLocation(monitoringObjectDTO);
                if(mapList2!=null&&mapList2.size()>0){
                    Map map2=mapList2.get(0);
                    Object cObject=map2.get("c");
                    String cc=cObject.toString();
                    if(cc=="1"||cc.equals("1")){
                     //成功暂不做操作
                        detailDTO.setStatus(true);
                        detailDTO.setErrorCode("1");
                        detailDTO.setErrorMessage("计量点新增成功");
                    }else{
                        String str1=(String)map2.get("str");
                        detailDTO.setStatus(false);
                        detailDTO.setErrorCode("-1");
                        detailDTO.setErrorMessage(str1);
                    }
                }else{
                    //这里表示调用jcd_Set_Location存储过程失败，但是暂时不需要做处理先
//                    detailDTO.setStatus(false);
//                    detailDTO.setErrorCode("-1");
//                    detailDTO.setErrorMessage("调用jcd_Set_Location存储过程失败！");
                }*/
            }else{
                String str=(String) mapList1.get(0).get("str");
                detailDTO.setStatus(false);
                detailDTO.setErrorCode("-1");
                detailDTO.setErrorMessage(str);
            }
        }
        return detailDTO;
    }
    //计量点修改
    public DetailDTO updateMonitoring(MonitoringObjectDTO monitoringObjectDTO, LoginAppDTO appLoginUser) {
        DetailDTO detailDTO=new DetailDTO(true);
        int updateInt=0;
        monitoringObjectDTO.setMonitoringPointAliasId(monitoringObjectDTO.getYnBaseStaffCompanyId()+"_"+monitoringObjectDTO.getYnBaseCustomerEnterpriseLongId()+"_"+monitoringObjectDTO.getYnBaseEquipmentLongId()+"_"+monitoringObjectDTO.getMonitoringSimCardInnerNo()+"_"+monitoringObjectDTO.getMonitoringPointLogicAddr());
        List<LinkedHashMap> list=ynMonitoringInitMapper.selectYnYjInFoByMonitoringObjectDTO(monitoringObjectDTO);
//        if(monitoringObjectDTO.getYnBaseEnterpriseSubstationLongId()!=null ||monitoringObjectDTO.getYnBaseEquipmentLongId() !=null
//                || monitoringObjectDTO.getMonitoringSimCardInnerNo()!=null||monitoringObjectDTO.getMonitoringPointLogicAddr()!=null){
        if(list==null||list.size()==0){//判断根据aliasid去查询是否能查询得到，如果查询不到数据就修改aliasid
            updateInt=ynMonitoringInitMapper.updateMonitoringAndAliasId(monitoringObjectDTO);
        }else{
            updateInt=ynMonitoringInitMapper.updateMonitoringNoAliasId(monitoringObjectDTO);
        }
        //修改完成之后判断dtu名称是否存在如果存在则修改dtu名称
        int updatDTUNameInt=0;
        if(monitoringObjectDTO.getMonitoringSimCardInnerName()!=null&&!monitoringObjectDTO.getMonitoringSimCardInnerName().equals("")){
            updatDTUNameInt=ynMonitoringInitMapper.updateYjDTUName(monitoringObjectDTO);
        }
        System.out.println("updateInt="+updateInt+"；updatDTUNameInt="+updatDTUNameInt);
        return detailDTO;
    }



    //计量点删除
    public DetailDTO deleteMonitoring(MonitoringObjectDTO monitoringObjectDTO, LoginAppDTO appLoginUser) {
        DetailDTO detailDTO=new DetailDTO(true);
        int deleteInt =ynMonitoringInitMapper.updateMonitoringToZero(monitoringObjectDTO);
        if(deleteInt==0){
            detailDTO.setStatus(false);
            detailDTO.setErrorCode("-1");
            detailDTO.setErrorMessage("计量点删除失败");
        }
        return detailDTO;
    }

    public DetailDTO updateMonitoringMStatus(MonitoringObjectDTO monitoringObjectDTO, LoginAppDTO appLoginUser) {
        DetailDTO detailDTO=new DetailDTO(true);
        int deleteInt =ynMonitoringInitMapper.updateMonitoringMStatus(monitoringObjectDTO);
        if(deleteInt==0){
            detailDTO.setStatus(false);
            detailDTO.setErrorCode("-1");
            detailDTO.setErrorMessage("计量点状态修改失败");
        }
        return detailDTO;
    }

    public DetailDTO selectOperationDateByEnterpriseOrSubstationOrEquipmentOrMonitoring(HttpServletRequest request, HttpServletResponse response, OnLineMonitoringDTO onLineMonitoringDTO, LoginAppDTO appLoginUser) {
        //获取当前时间的年月日
        Calendar now=Calendar.getInstance();
       // System.out.println("年: "+now.get(Calendar.YEAR));
        int nowYear=now.get(Calendar.YEAR);
       // System.out.println("月: "+(now.get(Calendar.MONTH)+1)+"");
        int nowMonth=now.get(Calendar.MONTH)+1;
      //  System.out.println("日: " + now.get(Calendar.DAY_OF_MONTH));
        int nowDay=now.get(Calendar.DAY_OF_MONTH);
        DetailDTO detailDTO=new DetailDTO(true);
        String ynBaseStaffCompanyId ="";
        //tag ：1计量点,2用电企业，3维保公司，4电房
        if(onLineMonitoringDTO.getTag()=="3"||onLineMonitoringDTO.getTag().equals("3")){
            ynBaseStaffCompanyId=onLineMonitoringDTO.getYnBaseStaffCompanyId();
        }else{
            List<LinkedHashMap> listCompany=ynMonitoringInitMapper.selectCompanyByOnLineMonitoringDTO(onLineMonitoringDTO);//根据用电企业id查询当前维保公司id，目的为了拼凑查询有功功率的表名
            if(listCompany!=null&&listCompany.size()>0) {
                ynBaseStaffCompanyId = (String) listCompany.get(0).get("ynBaseStaffCompanyId");//获取到维保公司id
            }else{
                detailDTO.setStatus(false);
                detailDTO.setErrorMessage("维保公司id不存在");
                return detailDTO;
            }
        }
        String tableString ="";//保存将要查询统计表的表名
        if(onLineMonitoringDTO.getYear()!=null&&!onLineMonitoringDTO.getYear().equals("")){//判断年份必填
            if(onLineMonitoringDTO.getMonth()==null||onLineMonitoringDTO.getMonth().equals("")){//表示查询的是年份的统计
                tableString="jc_tj_nf_"+ynBaseStaffCompanyId;
                onLineMonitoringDTO.setTableNameString(tableString);//设置即将查询统计表的表名
                /****************************年份参考代码开始********************************************/
                String dateString =onLineMonitoringDTO.getYear();
                onLineMonitoringDTO.setDateString(dateString);
                //获取月份计量点每个月的各种参数信息
                List<LinkedHashMap> monitoringMsgFromYearList=ynMonitoringInitMapper.selectMonitoringTJNFByObject(onLineMonitoringDTO);
                List<Map> sameMonitoringList=new ArrayList<Map>();//存放相同监测点id的总结
                Map sameMonitoringObject= new HashMap();//这个大对象里面带着计量点的列表和计量点的共有名称
                //应小李要求，把结果集拆分成x的值的数组，和y的值的数据
                List<Map> TOOList=new ArrayList<Map>();//单相列表
                Map TOObject= new HashMap();//单相对象
                List<Map> AXOList=new ArrayList<Map>();//A相列表
                Map AXObject= new HashMap();//A相对象
                List<Map> BXOList=new ArrayList<Map>();//B相列表
                Map BXObject= new HashMap();//B相对象
                List<Map> CXOList=new ArrayList<Map>();//C相列表
                Map CXObject= new HashMap();//C相对象
                List<String> monitoringListX=new ArrayList<String>();//X轴
                List<String> monitoringListY=new ArrayList<String>();//Y轴
                List<String> monitoringListYAX=new ArrayList<String>();//Y轴AX
                List<String> monitoringListYBX=new ArrayList<String>();//Y轴BX
                List<String> monitoringListYCX=new ArrayList<String>();//Y轴CX

                for(int i=0;i<monitoringMsgFromYearList.size();i++){//for 循环开始
                    Map map1=monitoringMsgFromYearList.get(i);
                    Object warningAutoIncrementId           = map1.get("idJC")           ;
                    Object monitoringPointAliasId    = map1.get("JCD_AliasID")    ;
                    Object monitoringPointCoreId     = map1.get("JCD_CoreID")     ;
                    Object JCD_id         = map1.get("JCD_id")         ;
                    Object monitoringPointName       = map1.get("JCD_Name")       ;
                    Integer monitoringPointType       = (Integer) map1.get("JCD_Type");
                    Object Tj_Type        = map1.get("Tj_Type")        ;
                    Object Tj_Type_Str        = map1.get("Tj_Type_Str")        ;
                    Object ynBaseEnterpriseSubstationLongId          = map1.get("DF_id")          ;
                    Object ynBaseEnterpriseSubstationName        = map1.get("DF_Name")        ;
                    Object ynBaseEquipmentLongId        = map1.get("SBTZ_id")        ;
                    Object ynBaseEquipmentName      = map1.get("SBTZ_Name")      ;
                    Object ynBaseCustomerEnterpriseLongId        = map1.get("YDQY_id")        ;
                    Object ynBaseCustomerEnterpriseName      = map1.get("YDQY_Name")      ;
                    Object ynBaseStaffCompanyId1        = map1.get("WBGS_id")        ;
                    Object ynBaseStaffCompanyName      = map1.get("WBGS_Name")      ;
                    Object createDate     = map1.get("CreateDate")     ;
                    Object dataDate       = map1.get("DataDate")       ;
                    Object TO_AVG         = map1.get("TO_AVG")         ;
                    Object TO_MIN         = map1.get("TO_MIN")         ;
                    Object TO_MIN_DATA_TAG= map1.get("TO_MIN_DATA_TAG");
                    Object TO_MIN_TIME_TAG= map1.get("TO_MIN_TIME_TAG");
                    Object TO_MAX         = map1.get("TO_MAX")         ;
                    Object TO_MAX_DATA_TAG= map1.get("TO_MAX_DATA_TAG");
                    Object TO_MAX_TIME_TAG= map1.get("TO_MAX_TIME_TAG");
                    Object AX_AVG         = map1.get("AX_AVG")         ;
                    Object AX_MIN         = map1.get("AX_MIN")         ;
                    Object AX_MIN_DATA_TAG= map1.get("AX_MIN_DATA_TAG");
                    Object AX_MIN_TIME_TAG= map1.get("AX_MIN_TIME_TAG");
                    Object AX_MAX         = map1.get("AX_MAX")         ;
                    Object AX_MAX_DATA_TAG= map1.get("AX_MAX_DATA_TAG");
                    Object AX_MAX_TIME_TAG= map1.get("AX_MAX_TIME_TAG");
                    Object BX_AVG         = map1.get("BX_AVG")         ;
                    Object BX_MIN         = map1.get("BX_MIN")         ;
                    Object BX_MIN_DATA_TAG= map1.get("BX_MIN_DATA_TAG");
                    Object BX_MIN_TIME_TAG= map1.get("BX_MIN_TIME_TAG");
                    Object BX_MAX         = map1.get("BX_MAX")         ;
                    Object BX_MAX_DATA_TAG= map1.get("BX_MAX_DATA_TAG");
                    Object BX_MAX_TIME_TAG= map1.get("BX_MAX_TIME_TAG");
                    Object CX_AVG         = map1.get("CX_AVG")         ;
                    Object CX_MIN         = map1.get("CX_MIN")         ;
                    Object CX_MIN_DATA_TAG= map1.get("CX_MIN_DATA_TAG");
                    Object CX_MIN_TIME_TAG= map1.get("CX_MIN_TIME_TAG");
                    Object CX_MAX         = map1.get("CX_MAX")         ;
                    Object CX_MAX_DATA_TAG= map1.get("CX_MAX_DATA_TAG");
                    Object CX_MAX_TIME_TAG= map1.get("CX_MAX_TIME_TAG");
                    sameMonitoringObject.put("monitoringPointName",monitoringPointName);//设置计量点公有名称
                    sameMonitoringObject.put("monitoringPointAliasId",monitoringPointAliasId);
                    sameMonitoringObject.put("ynBaseEquipmentLongId",ynBaseEquipmentLongId);
                    sameMonitoringObject.put("ynBaseEquipmentName",ynBaseEquipmentName);
                    sameMonitoringObject.put("ynBaseEnterpriseSubstationLongId",ynBaseEnterpriseSubstationLongId);
                    sameMonitoringObject.put("ynBaseEnterpriseSubstationName",ynBaseEnterpriseSubstationName);
                    sameMonitoringObject.put("ynBaseCustomerEnterpriseLongId",ynBaseCustomerEnterpriseLongId);
                    sameMonitoringObject.put("ynBaseCustomerEnterpriseName",ynBaseCustomerEnterpriseName);
                    sameMonitoringObject.put("ynBaseStaffCompanyId",ynBaseStaffCompanyId1);
                    sameMonitoringObject.put("ynBaseStaffCompanyName",ynBaseStaffCompanyName);
                    int nfMonths=12;//设置年份月数
                    if(monitoringPointType==1||monitoringPointType==11||monitoringPointType==12||monitoringPointType==14){//1单相,11温度单相，12湿度单相
                        Iterator entries1 = map1.entrySet().iterator();
                        //TOObject.put("valueType",Tj_Type_Str.toString());
                        sameMonitoringObject.put("monitoringPointType",monitoringPointType);
                        sameMonitoringObject.put("valueType",Tj_Type_Str.toString());
                        //组装X轴
                        for(int xInt =0;xInt<nfMonths;xInt++){
                            monitoringListX.add((xInt+1)+"");
                        }
                        TOObject.put("monitoringListX",monitoringListX);//填X轴
                        while (entries1.hasNext()) {
                            Map.Entry entry1 = (Map.Entry) entries1.next();
                            String key1 = (String)entry1.getKey();
                            Object value1 = entry1.getValue();
                            if(value1==null){
                                value1=0;//设置一个默认值
                            }
                            String value1String=value1.toString();//先把值设置成字符串类型扔回去
                            if(key1.contains("TO_")&&key1.length()==5){//判断获取到自己所需要的折线图数据
                                String toMonthString=key1.split("_")[1];//获取到月份的具体天数
                                //存放监测点的“每天XX值”对象的个数如果超过的月份的天数则整个对象属性遍历的循环终止（针对月份）
                                if(monitoringListY.size()>=nfMonths){
                                    break;
                                }
                                //如果查询天数还在查询月总天数范围内，则需要判断当前月份是否为查询月份
                                //如果当前月份为查询的月份则需要判断查询的天数如果大于当前天数则Y轴数据不再赋值
                                if(Integer.parseInt(onLineMonitoringDTO.getYear())==nowYear){
                                    if(Integer.parseInt(toMonthString)<=nowMonth){
                                        monitoringListY.add(value1String);
                                    }
                                }else if(Integer.parseInt(onLineMonitoringDTO.getYear())<nowYear){
                                    monitoringListY.add(value1String);
                                }
                            }
                        }
                        TOObject.put("monitoringListYTO",monitoringListY);
                        TOObject.put("monitoringListX",monitoringListX);
                        TOOList.add(TOObject);
                        sameMonitoringObject.put("TO",TOOList);
                        sameMonitoringList.add(sameMonitoringObject);
                        monitoringListX=new ArrayList<String>();
                        monitoringListY=new ArrayList<String>();
                        TOObject=new HashMap();
                        TOOList=new ArrayList<Map>();
                        sameMonitoringObject=new HashMap();
                    }else if(monitoringPointType==3||monitoringPointType==13){//三相
                        sameMonitoringObject.put("valueType",Tj_Type_Str.toString());
                        sameMonitoringObject.put("monitoringPointType",monitoringPointType);
                        //组装X轴
                        for(int xInt =0;xInt<nfMonths;xInt++){
                            monitoringListX.add((xInt+1)+"");
                        }
                        //开始初始化折线图列表数据,遍历当前的最大的map1拿到折线图要展现的数据
                        Iterator entries2 = map1.entrySet().iterator();
                        while (entries2.hasNext()) {
                            Map.Entry entry2 = (Map.Entry) entries2.next();
                            String key2 = (String)entry2.getKey();
                            Object value2 = entry2.getValue();
                            if(value2==null){
                                value2=0;//设置一个默认值
                            }
                            String value1String=value2.toString();//先把值设置成字符串类型扔回去
                            if(key2.contains("AX_")){//判断获取到自己所需要的折线图数据
                                if(key2.length()==5) {//必须在做一个判断否则平均值，最高值，最低值也进来了，因为那也是以TO_,AX_,BX_,CX_打头的
                                    //判断当前获得的折线图对象的个数如果超过的月份的天数则整个对象属性遍历的循环终止（针对月份）
                                    if(monitoringListYAX.size()>=nfMonths){
                                        break;
                                    }
                                    String toMonthString = key2.split("_")[1];//获取到月份的具体数字
                                    //如果查询天数还在查询月总天数范围内，则需要判断当前月份是否为查询月份
                                    //如果当前月份为查询的月份则需要判断查询的天数如果大于当前天数则Y轴数据不再赋值
                                    if(Integer.parseInt(onLineMonitoringDTO.getYear())==nowYear){
                                        if(Integer.parseInt(toMonthString)<=nowMonth){
                                            monitoringListYAX.add(value1String);
                                        }
                                    }else if(Integer.parseInt(onLineMonitoringDTO.getYear())<nowYear){
                                        monitoringListYAX.add(value1String);
                                    }
                                }
                            }else if(key2.contains("BX_")){
                                if(key2.length()==5) {//必须在做一个判断否则平均值，最高值，最低值也进来了，因为那也是以TO_,AX_,BX_,CX_打头的
                                    //判断当前获得的折线图对象的个数如果超过的月份的天数则整个对象属性遍历的循环终止（针对月份）
                                    if(monitoringListYBX.size()>=nfMonths){
                                        break;
                                    }
                                    String toMonthString = key2.split("_")[1];//获取到月份的具体数字
                                    //如果查询天数还在查询月总天数范围内，则需要判断当前月份是否为查询月份
                                    //如果当前月份为查询的月份则需要判断查询的天数如果大于当前天数则Y轴数据不再赋值
                                    if(Integer.parseInt(onLineMonitoringDTO.getYear())==nowYear){
                                        if(Integer.parseInt(toMonthString)<=nowMonth){
                                            monitoringListYBX.add(value1String);
                                        }
                                    }else if(Integer.parseInt(onLineMonitoringDTO.getYear())<nowYear){
                                        monitoringListYBX.add(value1String);
                                    }
                                }
                            }else if(key2.contains("CX_") ){
                                if(key2.length()==5) {//必须在做一个判断否则平均值，最高值，最低值也进来了，因为那也是以TO_,AX_,BX_,CX_打头的
                                    //判断当前获得的折线图对象的个数如果超过的月份的天数则整个对象属性遍历的循环终止（针对月份）
                                    if(monitoringListYCX.size()>=nfMonths){
                                        break;
                                    }
                                    String toMonthString = key2.split("_")[1];//获取到月份的具体数字
                                    //如果查询天数还在查询月总天数范围内，则需要判断当前月份是否为查询月份
                                    //如果当前月份为查询的月份则需要判断查询的天数如果大于当前天数则Y轴数据不再赋值
                                    if(Integer.parseInt(onLineMonitoringDTO.getYear())==nowYear){
                                        if(Integer.parseInt(toMonthString)<=nowMonth){
                                            monitoringListYCX.add(value1String);
                                        }
                                    }else  if(Integer.parseInt(onLineMonitoringDTO.getYear())<nowYear){
                                        monitoringListYCX.add(value1String);
                                    }
                                }
                            }
                        }
                    AXObject.put("monitoringListYAX",monitoringListYAX);
                    AXObject.put("monitoringListX",monitoringListX);
                    AXOList.add(AXObject);
                    sameMonitoringObject.put("AX",AXOList);

                    BXObject.put("monitoringListYBX",monitoringListYBX);
                    BXObject.put("monitoringListX",monitoringListX);
                    BXOList.add(BXObject);
                    sameMonitoringObject.put("BX",BXOList);

                    CXObject.put("monitoringListYCX",monitoringListYCX);
                    CXObject.put("monitoringListX",monitoringListX);
                    CXOList.add(CXObject);
                    sameMonitoringObject.put("CX",CXOList);

                    sameMonitoringList.add(sameMonitoringObject);
                    monitoringListX=new ArrayList<String>();
                    monitoringListYAX=new ArrayList<String>();
                    monitoringListYBX=new ArrayList<String>();
                    monitoringListYCX=new ArrayList<String>();
                    AXObject=new HashMap();
                    BXObject=new HashMap();
                    CXObject=new HashMap();
                    AXOList=new ArrayList<Map>();
                    BXOList=new ArrayList<Map>();
                    CXOList=new ArrayList<Map>();
                    sameMonitoringObject=new HashMap();
                }
            }//for 结束
            detailDTO.setDetail(sameMonitoringList);
                /****************************年份参考代码结束********************************************/
            }else{//如果月份不为空表示查询的是月份的数据
                tableString="jc_tj_yf_"+ynBaseStaffCompanyId;
                onLineMonitoringDTO.setTableNameString(tableString);//设置即将查询统计表的表名
                /****************************参考代码开始********************************************/
                String dateString =onLineMonitoringDTO.getYear()+onLineMonitoringDTO.getMonth();
                onLineMonitoringDTO.setDateString(dateString);
                //获取月份计量点每个月的各种参数信息
                List<LinkedHashMap> monitoringMsgFromMonthList=ynMonitoringInitMapper.selectMonitoringTJYFByObject(onLineMonitoringDTO);
                List<Map> sameMonitoringList=new ArrayList<Map>();//存放相同监测点id的总结
                Map sameMonitoringObject= new HashMap();//这个大对象里面带着计量点的列表和计量点的共有名称
                //应小李要求，把结果集拆分成x的值的数组，和y的值的数据
                List<Map> TOOList=new ArrayList<Map>();//单相列表
                Map TOObject= new HashMap();//单相对象
                List<Map> AXOList=new ArrayList<Map>();//A相列表
                Map AXObject= new HashMap();//A相对象
                List<Map> BXOList=new ArrayList<Map>();//B相列表
                Map BXObject= new HashMap();//B相对象
                List<Map> CXOList=new ArrayList<Map>();//C相列表
                Map CXObject= new HashMap();//C相对象
                List<String> monitoringListX=new ArrayList<String>();//X轴
                List<String> monitoringListY=new ArrayList<String>();//Y轴
                List<String> monitoringListYAX=new ArrayList<String>();//Y轴AX
                List<String> monitoringListYBX=new ArrayList<String>();//Y轴BX
                List<String> monitoringListYCX=new ArrayList<String>();//Y轴CX


                for(int i=0;i<monitoringMsgFromMonthList.size();i++){//for 循环开始
                    Map map1=monitoringMsgFromMonthList.get(i);
                    Integer yfDays           = (Integer) map1.get("YF_Days");//月份总天数
                    Object warningAutoIncrementId           = map1.get("idJC")           ;
                    Object monitoringPointAliasId    = map1.get("JCD_AliasID")    ;
                    Object monitoringPointCoreId     = map1.get("JCD_CoreID")     ;
                    Object JCD_id         = map1.get("JCD_id")         ;
                    Object monitoringPointName       = map1.get("JCD_Name")       ;
                    Integer monitoringPointType       = (Integer) map1.get("JCD_Type");
                    Object Tj_Type        = map1.get("Tj_Type")        ;
                    Object Tj_Type_Str        = map1.get("Tj_Type_Str")        ;
                    Object ynBaseEnterpriseSubstationLongId          = map1.get("DF_id")          ;
                    Object ynBaseEnterpriseSubstationName        = map1.get("DF_Name")        ;
                    Object ynBaseEquipmentLongId        = map1.get("SBTZ_id")        ;
                    Object ynBaseEquipmentName      = map1.get("SBTZ_Name")      ;
                    Object ynBaseCustomerEnterpriseLongId        = map1.get("YDQY_id")        ;
                    Object ynBaseCustomerEnterpriseName      = map1.get("YDQY_Name")      ;
                    Object ynBaseStaffCompanyId1        = map1.get("WBGS_id")        ;
                    Object ynBaseStaffCompanyName      = map1.get("WBGS_Name")      ;
                    Object createDate     = map1.get("CreateDate")     ;
                    Object dataDate       = map1.get("DataDate")       ;
                    Object TO_AVG         = map1.get("TO_AVG")         ;
                    Object TO_MIN         = map1.get("TO_MIN")         ;
                    Object TO_MIN_DATA_TAG= map1.get("TO_MIN_DATA_TAG");
                    Object TO_MIN_TIME_TAG= map1.get("TO_MIN_TIME_TAG");
                    Object TO_MAX         = map1.get("TO_MAX")         ;
                    Object TO_MAX_DATA_TAG= map1.get("TO_MAX_DATA_TAG");
                    Object TO_MAX_TIME_TAG= map1.get("TO_MAX_TIME_TAG");
                    Object AX_AVG         = map1.get("AX_AVG")         ;
                    Object AX_MIN         = map1.get("AX_MIN")         ;
                    Object AX_MIN_DATA_TAG= map1.get("AX_MIN_DATA_TAG");
                    Object AX_MIN_TIME_TAG= map1.get("AX_MIN_TIME_TAG");
                    Object AX_MAX         = map1.get("AX_MAX")         ;
                    Object AX_MAX_DATA_TAG= map1.get("AX_MAX_DATA_TAG");
                    Object AX_MAX_TIME_TAG= map1.get("AX_MAX_TIME_TAG");
                    Object BX_AVG         = map1.get("BX_AVG")         ;
                    Object BX_MIN         = map1.get("BX_MIN")         ;
                    Object BX_MIN_DATA_TAG= map1.get("BX_MIN_DATA_TAG");
                    Object BX_MIN_TIME_TAG= map1.get("BX_MIN_TIME_TAG");
                    Object BX_MAX         = map1.get("BX_MAX")         ;
                    Object BX_MAX_DATA_TAG= map1.get("BX_MAX_DATA_TAG");
                    Object BX_MAX_TIME_TAG= map1.get("BX_MAX_TIME_TAG");
                    Object CX_AVG         = map1.get("CX_AVG")         ;
                    Object CX_MIN         = map1.get("CX_MIN")         ;
                    Object CX_MIN_DATA_TAG= map1.get("CX_MIN_DATA_TAG");
                    Object CX_MIN_TIME_TAG= map1.get("CX_MIN_TIME_TAG");
                    Object CX_MAX         = map1.get("CX_MAX")         ;
                    Object CX_MAX_DATA_TAG= map1.get("CX_MAX_DATA_TAG");
                    Object CX_MAX_TIME_TAG= map1.get("CX_MAX_TIME_TAG");
                    sameMonitoringObject.put("monitoringPointName",monitoringPointName);//设置计量点公有名称
                    sameMonitoringObject.put("monitoringPointAliasId",monitoringPointAliasId);
                    sameMonitoringObject.put("ynBaseEquipmentLongId",ynBaseEquipmentLongId);
                    sameMonitoringObject.put("ynBaseEquipmentName",ynBaseEquipmentName);
                    sameMonitoringObject.put("ynBaseEnterpriseSubstationLongId",ynBaseEnterpriseSubstationLongId);
                    sameMonitoringObject.put("ynBaseEnterpriseSubstationName",ynBaseEnterpriseSubstationName);
                    sameMonitoringObject.put("ynBaseCustomerEnterpriseLongId",ynBaseCustomerEnterpriseLongId);
                    sameMonitoringObject.put("ynBaseCustomerEnterpriseName",ynBaseCustomerEnterpriseName);
                    sameMonitoringObject.put("ynBaseStaffCompanyId",ynBaseStaffCompanyId1);
                    sameMonitoringObject.put("ynBaseStaffCompanyName",ynBaseStaffCompanyName);
                    if(monitoringPointType==1||monitoringPointType==11||monitoringPointType==12||monitoringPointType==14){//1单相,11温度单相，12湿度单相
                        Iterator entries1 = map1.entrySet().iterator();
                        //TOObject.put("valueType",Tj_Type_Str.toString());
                        sameMonitoringObject.put("monitoringPointType",monitoringPointType);
                        sameMonitoringObject.put("valueType",Tj_Type_Str.toString());
                        //组装X轴
                        for(int xInt =0;xInt<yfDays;xInt++){
                            monitoringListX.add((xInt+1)+"");
                        }
                        TOObject.put("monitoringListX",monitoringListX);//填X轴
                        while (entries1.hasNext()) {
                            Map.Entry entry1 = (Map.Entry) entries1.next();
                            String key1 = (String)entry1.getKey();
                            Object value1 = entry1.getValue();
                            if(value1==null){
                                value1=0;//设置一个默认值
                            }
                            String value1String=value1.toString();//先把值设置成字符串类型扔回去
                            if(key1.contains("TO_")&&key1.length()==5){//判断获取到自己所需要的折线图数据
                                String toDayString=key1.split("_")[1];//获取到月份的具体天数
                                //存放监测点的“每天XX值”对象的个数如果超过的月份的天数则整个对象属性遍历的循环终止（针对月份）
                                if(monitoringListY.size()>=yfDays){
                                    break;
                                }
                                //如果查询天数还在查询月总天数范围内，则需要判断当前月份是否为查询月份
                                //如果当前月份为查询的月份则需要判断查询的天数如果大于当前天数则Y轴数据不再赋值
                                if(Integer.parseInt(onLineMonitoringDTO.getYear())==nowYear&&Integer.parseInt(onLineMonitoringDTO.getMonth())==nowMonth){
                                    if(Integer.parseInt(toDayString)<=nowDay){
                                        monitoringListY.add(value1String);
                                    }
                                }else{
                                    monitoringListY.add(value1String);
                                }
                            }
                        }
                        TOObject.put("monitoringListYTO",monitoringListY);
                        TOObject.put("monitoringListX",monitoringListX);
                        TOOList.add(TOObject);
                        sameMonitoringObject.put("TO",TOOList);
                        sameMonitoringList.add(sameMonitoringObject);
                        monitoringListX=new ArrayList<String>();
                        monitoringListY=new ArrayList<String>();
                        TOObject=new HashMap();
                        TOOList=new ArrayList<Map>();
                        sameMonitoringObject=new HashMap();
                    }else if(monitoringPointType==3||monitoringPointType==13){//三相
                        sameMonitoringObject.put("valueType",Tj_Type_Str.toString());
                        sameMonitoringObject.put("monitoringPointType",monitoringPointType);
                        //组装X轴
                        for(int xInt =0;xInt<yfDays;xInt++){
                            monitoringListX.add((xInt+1)+"");
                        }
                        //开始初始化折线图列表数据,遍历当前的最大的map1拿到折线图要展现的数据
                        Iterator entries2 = map1.entrySet().iterator();
                        DefaultCategoryDataset mDataset = new DefaultCategoryDataset();
                        while (entries2.hasNext()) {
                            Map.Entry entry2 = (Map.Entry) entries2.next();
                            String key2 = (String)entry2.getKey();
                            Object value2 = entry2.getValue();
                            if(value2==null){
                                value2=0;//设置一个默认值
                            }
                            String value1String=value2.toString();//先把值设置成字符串类型扔回去
                            if(key2.contains("AX_")){//判断获取到自己所需要的折线图数据
                                if(key2.length()==5) {//必须在做一个判断否则平均值，最高值，最低值也进来了，因为那也是以TO_,AX_,BX_,CX_打头的
                                    //判断当前获得的折线图对象的个数如果超过的月份的天数则整个对象属性遍历的循环终止（针对月份）
                                    if(monitoringListYAX.size()>=yfDays){
                                        break;
                                    }
                                    String toDayString = key2.split("_")[1];//获取到月份的具体数字
                                    //如果查询天数还在查询月总天数范围内，则需要判断当前月份是否为查询月份
                                    //如果当前月份为查询的月份则需要判断查询的天数如果大于当前天数则Y轴数据不再赋值
                                    if(Integer.parseInt(onLineMonitoringDTO.getYear())==nowYear&&Integer.parseInt(onLineMonitoringDTO.getMonth())==nowMonth){
                                        if(Integer.parseInt(toDayString)<=nowDay){
                                            monitoringListYAX.add(value1String);
                                        }
                                    }else{
                                        monitoringListYAX.add(value1String);
                                    }
                                }
                            }else if(key2.contains("BX_")){
                                if(key2.length()==5) {//必须在做一个判断否则平均值，最高值，最低值也进来了，因为那也是以TO_,AX_,BX_,CX_打头的
                                    //判断当前获得的折线图对象的个数如果超过的月份的天数则整个对象属性遍历的循环终止（针对月份）
                                    if(monitoringListYBX.size()>=yfDays){
                                        break;
                                    }
                                    String toDayString = key2.split("_")[1];//获取到月份的具体数字
                                    //如果查询天数还在查询月总天数范围内，则需要判断当前月份是否为查询月份
                                    //如果当前月份为查询的月份则需要判断查询的天数如果大于当前天数则Y轴数据不再赋值
                                    if(Integer.parseInt(onLineMonitoringDTO.getYear())==nowYear&&Integer.parseInt(onLineMonitoringDTO.getMonth())==nowMonth){
                                        if(Integer.parseInt(toDayString)<=nowDay){
                                            monitoringListYBX.add(value1String);
                                        }
                                    }else{
                                        monitoringListYBX.add(value1String);
                                    }
                                }
                            }else if(key2.contains("CX_") ){
                                if(key2.length()==5) {//必须在做一个判断否则平均值，最高值，最低值也进来了，因为那也是以TO_,AX_,BX_,CX_打头的
                                    //判断当前获得的折线图对象的个数如果超过的月份的天数则整个对象属性遍历的循环终止（针对月份）
                                    if(monitoringListYCX.size()>=yfDays){
                                        break;
                                    }
                                    String toDayString = key2.split("_")[1];//获取到月份的具体数字
                                    //如果查询天数还在查询月总天数范围内，则需要判断当前月份是否为查询月份
                                    //如果当前月份为查询的月份则需要判断查询的天数如果大于当前天数则Y轴数据不再赋值
                                    if(Integer.parseInt(onLineMonitoringDTO.getYear())==nowYear&&Integer.parseInt(onLineMonitoringDTO.getMonth())==nowMonth){
                                        if(Integer.parseInt(toDayString)<=nowDay){
                                            monitoringListYCX.add(value1String);
                                        }
                                    }else{
                                        monitoringListYCX.add(value1String);
                                    }
                                }
                            }
                        }
                        AXObject.put("monitoringListYAX",monitoringListYAX);
                        AXObject.put("monitoringListX",monitoringListX);
                        AXOList.add(AXObject);
                        sameMonitoringObject.put("AX",AXOList);

                        BXObject.put("monitoringListYBX",monitoringListYBX);
                        BXObject.put("monitoringListX",monitoringListX);
                        BXOList.add(BXObject);
                        sameMonitoringObject.put("BX",BXOList);

                        CXObject.put("monitoringListYCX",monitoringListYCX);
                        CXObject.put("monitoringListX",monitoringListX);
                        CXOList.add(CXObject);
                        sameMonitoringObject.put("CX",CXOList);

                        sameMonitoringList.add(sameMonitoringObject);
                        monitoringListX=new ArrayList<String>();
                        monitoringListYAX=new ArrayList<String>();
                        monitoringListYBX=new ArrayList<String>();
                        monitoringListYCX=new ArrayList<String>();
                        AXObject=new HashMap();
                        BXObject=new HashMap();
                        CXObject=new HashMap();
                        AXOList=new ArrayList<Map>();
                        BXOList=new ArrayList<Map>();
                        CXOList=new ArrayList<Map>();
                        sameMonitoringObject=new HashMap();
                    }
                }//for 结束
                detailDTO.setDetail(sameMonitoringList);
                /****************************参考代码结束********************************************/
            }
        }else{
            detailDTO.setStatus(false);
            detailDTO.setErrorMessage("年份year不能为空");
            return detailDTO;
        }
        return detailDTO;
    }

    //辅助去掉空格
    public static Date format5Parse(String v){
       // System.out.println("v==="+v);
        if(v==null||v.equals("")||v.equals("null null")||v.replace(" ","").equals("")||v.equals(" ")||v==" "){
            return  null;
        }
        DateFormat format5= new SimpleDateFormat("yyyyMMddHHmm");
        try {
            return  format5.parse(v.replace(" ",""));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return  null;
    }
    public static String format6Parse(Date timeString){
      //  System.out.println("timeString===="+timeString);
        if(timeString==null||timeString.equals("")){
            return "";
        }
        DateFormat format6= new SimpleDateFormat("yyyy年MM月dd日HH时mm分");
        return format6.format(timeString);
    }



    public DetailDTO selectCountMonitringAndEnterprise(MonitoringObjectDTO monitoringObjectDTO, LoginAppDTO appLoginUser,ArrayList<String> serviceAreas) {
        DetailDTO detailDTO=new DetailDTO(true);
        List<LinkedHashMap> resultList = new ArrayList<LinkedHashMap>();

        String companyId = monitoringObjectDTO.getYnBaseStaffCompanyId();
        Map maps = new HashMap();
        maps.put("companyId",companyId);
        maps.put("servicesList",serviceAreas);
        if (companyId!=null&&companyId!=""){
            if (Integer.parseInt(companyId)==83650){
                resultList = ynMonitoringInitMapper.selectAllCountMonitringAndEnterprise(maps);
            }else if (Integer.parseInt(companyId)==83800){
                resultList = ynMonitoringInitMapper.selectHNCountMonitringAndEnterprise(maps);
            }else if (Integer.parseInt(companyId)==84000){
                resultList = ynMonitoringInitMapper.selectMZCountMonitringAndEnterprise(maps);
            }else {
                resultList =ynMonitoringInitMapper.selectCountMonitringAndEnterprise(maps);
            }
        }


        detailDTO.setDetail(resultList);
        return detailDTO;
    }
    public DetailDTO selectCountMonitringAndSubstation(MonitoringObjectDTO monitoringObjectDTO, LoginAppDTO appLoginUser) {
        DetailDTO detailDTO=new DetailDTO(true);
        List<LinkedHashMap> resultList =ynMonitoringInitMapper.selectCountMonitringAndSubstation(monitoringObjectDTO);
        detailDTO.setDetail(resultList);
        return detailDTO;
    }
    /**根据用電企業id统计用电企业的总用电量-
     todayTotalSUM           今日总电量
     yestodayTotalSUM        昨日总电量
     dayGrowthRateSUM        日环比昨日（数值）
     dayGrowthRateStateSUM   今日环比昨日状态：1表示环比为整数，0表示环比为相等，-1表示环比为负数
     currentMonthTotalSUM    当前月总电量
     LastMonthTotalSUM       上月总电量
     monthGrowthRateSUM      当前月环比上月（数值）
     monthGrowthRateStateSUM 当前月环比上月状态：1表示环比为正数，0表示环比为相等，-1表示环比为负数
     */
    public DetailDTO selectElectricAnalysisByEnterpriseLongId(MonitoringObjectDTO monitoringObjectDTO, LoginAppDTO appLoginUser) {
        DetailDTO detailDTO=new DetailDTO(true);
        OnLineMonitoringDTO onLineMonitoringDTO = new OnLineMonitoringDTO();
        onLineMonitoringDTO.setYnBaseCustomerEnterpriseLongId(monitoringObjectDTO.getYnBaseCustomerEnterpriseLongId());
        List<LinkedHashMap> listCompany=ynMonitoringInitMapper.selectCompanyByOnLineMonitoringDTO(onLineMonitoringDTO);//根据用电企业id查询当前维保公司id，目的为了拼凑查询有功功率的表名
        if(listCompany!=null&&listCompany.size()>0){
            String ynBaseStaffCompanyId= (String) listCompany.get(0).get("ynBaseStaffCompanyId");//获取到维保公司id
            //根据用电企业id查询当前天的总有功情况TO_打头的字段
           // monitoringObjectDTO.setTableNameString("jc_dd_"+ynBaseStaffCompanyId);//设置查询电度的表名，每个维保公司的都不一样所以需要根据维保公司id去查询
            monitoringObjectDTO.setTableNameString("jc_fs_"+ynBaseStaffCompanyId);
            List<LinkedHashMap> listActivePower=ynMonitoringInitMapper.selectElectricAnalysisByid(monitoringObjectDTO);
            String value = null;
            List alias  = new ArrayList();
            for(int i = 0;i<listActivePower.size();i++){
                Map lmap = listActivePower.get(i);
                for(Object key : lmap.keySet()) {
                    String aliID = (String) lmap.get("JCD_AliasID");
                    alias.add(aliID);
                }
            }

            monitoringObjectDTO.setMonitoringPointAliasId(value);
            Date day=new Date();
            SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
            System.out.println(df.format(day));
            monitoringObjectDTO.setDateString(df.format(day));
            Map ysAvg=ynMonitoringInitMapper.selectYSAVG(monitoringObjectDTO);
            String avgList = null;
            String jcdNAME = null;
            String MDList = null;
            for(Object key : ysAvg.keySet()) {
                 avgList =  ysAvg.get("avgList").toString();
                 jcdNAME =  ysAvg.get("jcdNAME").toString();
                 MDList =  ysAvg.get("MD").toString();
            }
            String [] avg  = avgList.split(",");
            String [] jcd  = jcdNAME.split(",");
            String [] md  = MDList.split(",");

            List avgL = new ArrayList();
            for(int i=0;i<avg.length;i++){
                Map avgMap = new HashMap();
                avgMap.put("avg",avg[i]);
                avgMap.put("jcd",jcd[i]);
                avgMap.put("md",md[i]);
                avgL.add(avgMap);
            }
            List<LinkedHashMap> listMonitoringActivePower = new ArrayList<LinkedHashMap>();
            List<LinkedHashMap> FPG = new ArrayList<LinkedHashMap>();
            Map amap = new HashMap();
            Double  todayTotal = 0.0;
            Double yestodayTotal = 0.0;
            Double dayGrowthRateState = 0.0;
            Double dayGrowthRate = 0.0;
            Double currentMonthTotal = 0.0;
            Double LastMonthTotal = 0.0;
            Double monthGrowthRate = 0.0;
            Double monthGrowthRateState = 0.0;
            Double RL = 0.0;
            int count = 0;
            Double  FFO = 0.0;
            Double  FPO = 0.0;
            Double  FGO = 0.0;
            Double  LFO = 0.0;
            Double  LPO = 0.0;
            Double  LGO = 0.0;
            Double  DF = 0.0;
            Double  DP = 0.0;
            Double  DG = 0.0;
            Double  DLF = 0.0;
            Double  DLP = 0.0;
            Double  DLG = 0.0;


            for(int j = 0;j<alias.size();j++){
                monitoringObjectDTO.setMonitoringPointAliasId((String) alias.get(j));
                listMonitoringActivePower=ynMonitoringInitMapper.selectElectricAnalysisByEnterpriseLongId(monitoringObjectDTO);
                FPG=ynMonitoringInitMapper.selectFPGAndYe(monitoringObjectDTO);
                Map powerMap = listMonitoringActivePower.get(0);
                String te =powerMap.get("todayTotal").toString();
                Double te1 = Double.parseDouble(te);
                todayTotal += te1;
                String yestoday = powerMap.get("yestodayTotal").toString();
                yestodayTotal += Double.parseDouble(yestoday);

                String dayGrowthRat = powerMap.get("dayGrowthRate").toString();
                dayGrowthRate += Double.valueOf(dayGrowthRat);

                String dayGrowthRateStat = powerMap.get("dayGrowthRateState").toString();
                count  = count + 1;
                dayGrowthRateState = Double.parseDouble(dayGrowthRateStat);

                String currentMonthTota = powerMap.get("currentMonthTotal").toString();
                currentMonthTotal += Double.parseDouble(currentMonthTota);
                String LastMonthTota = powerMap.get("LastMonthTotal").toString();
                LastMonthTotal += Double.parseDouble(LastMonthTota);

                String monthGrowthRat = powerMap.get("monthGrowthRate").toString();
                monthGrowthRate += Double.valueOf(monthGrowthRat);
                String monthGrowthRateStat = powerMap.get("monthGrowthRateState").toString();

                monthGrowthRateState = Double.parseDouble(monthGrowthRateStat);
                String R = powerMap.get("RL").toString();
                RL += Double.parseDouble(R);

                Map fpgMap = FPG.get(0);
                String FP = fpgMap.get("PPO").toString();
                FPO += Double.parseDouble(FP);
                String FF = fpgMap.get("FPO").toString();
                FFO += Double.parseDouble(FF);
                String FG = fpgMap.get("GPO").toString();
                FGO += Double.parseDouble(FG);
                String LF = fpgMap.get("LFO").toString();
                LFO += Double.parseDouble(LF);
                String LP = fpgMap.get("LPO").toString();
                LPO += Double.parseDouble(LP);
                String LG = fpgMap.get("LGO").toString();
                LGO += Double.parseDouble(LG);
                // 电能
                String F = fpgMap.get("F").toString();
                DF += Double.parseDouble(F);
                String P = fpgMap.get("P").toString();
                DP += Double.parseDouble(P);
                String G = fpgMap.get("G").toString();
                DG += Double.parseDouble(G);
                String LFT = fpgMap.get("LF").toString();
                DLF += Double.parseDouble(LFT);
                String LPT = fpgMap.get("LP").toString();
                DLP += Double.parseDouble(LPT);
                String LGT = fpgMap.get("LG").toString();
                DLG += Double.parseDouble(LGT);


            }
            // List<LinkedHashMap> listMonitoringActivePower=ynMonitoringInitMapper.selectElectricAnalysisByEnterpriseLongId(monitoringObjectDTO);
            // List<LinkedHashMap> FPG=ynMonitoringInitMapper.selectFPGAndYe(monitoringObjectDTO);
            if(listMonitoringActivePower!=null&&listMonitoringActivePower.size()>0){
                Map pmap = new HashMap();
                pmap.put("todayTotal",todayTotal);
                pmap.put("yestodayTotal",yestodayTotal);
                pmap.put("dayGrowthRate",dayGrowthRate/count);
                pmap.put("dayGrowthRateState",dayGrowthRateState);

                pmap.put("currentMonthTotal",currentMonthTotal);
                pmap.put("LastMonthTotal",LastMonthTotal);
                pmap.put("monthGrowthRate",monthGrowthRate/count);
                pmap.put("monthGrowthRateState",monthGrowthRateState);

                pmap.put("FPO",FFO);
                pmap.put("PPO",FPO);
                pmap.put("GPO",FGO);

                pmap.put("LFO",LFO);
                pmap.put("LPO",LPO);
                pmap.put("LGO",LGO);
                pmap.put("F",DF);
                pmap.put("P",DP);
                pmap.put("G",DG);

                pmap.put("LF",DLF);
                pmap.put("LP",DLP);
                pmap.put("LG",DLG);
                pmap.put("RL",RL);

                List lPower = new ArrayList();
                lPower.add(pmap);


                amap.put("power",lPower);
                amap.put("ysAvg",avgL);
                detailDTO.setDetail(amap);
            }else{
                detailDTO.setDetail(new ArrayList<Map>());
            }

        } else {
            detailDTO.setStatus(false);
            detailDTO.setErrorCode("-1");
            detailDTO.setErrorMessage("根据用电企业id：" + onLineMonitoringDTO.getYnBaseCustomerEnterpriseLongId() + "查询维保公司信息失败，请查看yj_info表");
            return detailDTO;
        }
        return detailDTO;
    }


    public DetailDTO selectSYPower(MonitoringObjectDTO monitoringObjectDTO, LoginAppDTO appLoginUser,String userType) {
        DetailDTO detailDTO=new DetailDTO(true);
        OnLineMonitoringDTO onLineMonitoringDTO = new OnLineMonitoringDTO();
        onLineMonitoringDTO.setYnBaseCustomerEnterpriseLongId(monitoringObjectDTO.getYnBaseCustomerEnterpriseLongId());
        List<LinkedHashMap> listCompany=ynMonitoringInitMapper.selectCompanyByOnLineMonitoringDTO(onLineMonitoringDTO);//根据用电企业id查询当前维保公司id，目的为了拼凑查询有功功率的表名
        if(listCompany!=null&&listCompany.size()>0){
            String ynBaseStaffCompanyId= (String) listCompany.get(0).get("ynBaseStaffCompanyId");//获取到维保公司id
            //根据用电企业id查询当前天的总有功情况TO_打头的字段
            // monitoringObjectDTO.setTableNameString("jc_dd_"+ynBaseStaffCompanyId);//设置查询电度的表名，每个维保公司的都不一样所以需要根据维保公司id去查询
            if(Integer.parseInt(userType) == 1 || Integer.parseInt(userType) == 2){
                monitoringObjectDTO.setYnBaseCustomerEnterpriseLongId("");
                onLineMonitoringDTO.setYnBaseCustomerEnterpriseLongId("");
            }
            List<LinkedHashMap> listActivePower=ynMonitoringInitMapper.selectElectricAnalysisByid(monitoringObjectDTO);
            String value = null;
            List alias  = new ArrayList();
            for(int i = 0;i<listActivePower.size();i++){
                Map lmap = listActivePower.get(i);
                for(Object key : lmap.keySet()) {
                    String aliID = (String) lmap.get("JCD_AliasID");
                    alias.add(aliID);
                }
            }

            monitoringObjectDTO.setMonitoringPointAliasId(value);
            Date day=new Date();
            SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
            System.out.println(df.format(day));
            monitoringObjectDTO.setDateString(df.format(day));
            List<LinkedHashMap> listMonitoringActivePower = new ArrayList<LinkedHashMap>();
            List<LinkedHashMap> FPG = new ArrayList<LinkedHashMap>();
            Map amap = new HashMap();
            Double  todayTotal = 0.0;
            Double yestodayTotal = 0.0;
            int dayGrowthRateState = 0;
            Double dayGrowthRate = 0.0;
            Double currentMonthTotal = 0.0;
            Double LastMonthTotal = 0.0;
            Double monthGrowthRate = 0.0;
            int monthGrowthRateState = 0;

            int count = 0;
            Double  FFO = 0.0;
            Double  FPO = 0.0;
            Double  FGO = 0.0;
            Double  LFO = 0.0;
            Double  LPO = 0.0;
            Double  LGO = 0.0;


            for(int j = 0;j<alias.size();j++){
                monitoringObjectDTO.setMonitoringPointAliasId((String) alias.get(j));
                listMonitoringActivePower=ynMonitoringInitMapper.selectSYPower(monitoringObjectDTO);
                FPG=ynMonitoringInitMapper.selectFPGAndYe(monitoringObjectDTO);
                Map powerMap = listMonitoringActivePower.get(0);
                String te =powerMap.get("todayTotal").toString();
                Double te1 = Double.parseDouble(te);
                todayTotal += te1;
                String yestoday = powerMap.get("yestodayTotal").toString();
                yestodayTotal += Double.parseDouble(yestoday);

                String currentMonthTota = powerMap.get("currentMonthTotal").toString();
                currentMonthTotal += Double.parseDouble(currentMonthTota);
                String LastMonthTota = powerMap.get("LastMonthTotal").toString();
                LastMonthTotal += Double.parseDouble(LastMonthTota);

                Map fpgMap = FPG.get(0);
                String FP = fpgMap.get("PPO").toString();
                FPO += Double.parseDouble(FP);

                String FF = fpgMap.get("FPO").toString();
                FFO += Double.parseDouble(FF);
                String FG = fpgMap.get("GPO").toString();
                FGO += Double.parseDouble(FG);
                String LF = fpgMap.get("LFO").toString();
                LFO += Double.parseDouble(LF);
                String LP = fpgMap.get("LPO").toString();
                LPO += Double.parseDouble(LP);
                String LG = fpgMap.get("LGO").toString();
                LGO += Double.parseDouble(LG);



            }
            // List<LinkedHashMap> listMonitoringActivePower=ynMonitoringInitMapper.selectElectricAnalysisByEnterpriseLongId(monitoringObjectDTO);
            // List<LinkedHashMap> FPG=ynMonitoringInitMapper.selectFPGAndYe(monitoringObjectDTO);
            if(listMonitoringActivePower!=null&&listMonitoringActivePower.size()>0){
                Map pmap = new HashMap();
                pmap.put("todayTotal",todayTotal);
                pmap.put("yestodayTotal",yestodayTotal);

                if(yestodayTotal!=0){
                    dayGrowthRate = (todayTotal - yestodayTotal)/yestodayTotal;
                    if(todayTotal - yestodayTotal>0){
                        dayGrowthRateState = 1;
                    }else{
                        dayGrowthRateState = 0;
                    }
                }

                pmap.put("dayGrowthRate",dayGrowthRate);
                pmap.put("dayGrowthRateState",dayGrowthRateState);

                pmap.put("currentMonthTotal",currentMonthTotal);
                pmap.put("LastMonthTotal",LastMonthTotal);

                if(LastMonthTotal!=0){
                    monthGrowthRate = (currentMonthTotal - LastMonthTotal)/LastMonthTotal;
                    if(currentMonthTotal - LastMonthTotal>0){
                        monthGrowthRateState = 1;
                    }else{
                        monthGrowthRateState = 0;
                    }
                }
                pmap.put("monthGrowthRate",monthGrowthRate);
                pmap.put("monthGrowthRateState",monthGrowthRateState);

                pmap.put("FPO",FFO);
                pmap.put("PPO",FPO);
                pmap.put("GPO",FGO);

                pmap.put("LFO",LFO);
                pmap.put("LPO",LPO);
                pmap.put("LGO",LGO);


                List lPower = new ArrayList();
                lPower.add(pmap);


                amap.put("power",lPower);
                detailDTO.setDetail(amap);
            }else{
                detailDTO.setDetail(new ArrayList<Map>());
            }

        } else {
            detailDTO.setStatus(false);
            detailDTO.setErrorCode("-1");
            detailDTO.setErrorMessage("根据用电企业id：" + onLineMonitoringDTO.getYnBaseCustomerEnterpriseLongId() + "查询维保公司信息失败，请查看yj_info表");
            return detailDTO;
        }
        return detailDTO;
    }

    public DetailDTO selectSYCountAndRL(MonitoringObjectDTO monitoringObjectDTO, LoginAppDTO appLoginUser, String userType,OnLineMonitoringDTO onLineMonitoringDTO) {
        SimpleDateFormat formatter  = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat formatter2  = new SimpleDateFormat("yyyy-MM");
        DetailDTO detailDTO=new DetailDTO(true);
        Map aMap = new HashMap();
        // OnLineMonitoringDTO onLineMonitoringDTO = new OnLineMonitoringDTO();
        onLineMonitoringDTO.setYnBaseCustomerEnterpriseLongId(monitoringObjectDTO.getYnBaseCustomerEnterpriseLongId());

        List<LinkedHashMap> listCompany=ynMonitoringInitMapper.selectCompanyByOnLineMonitoringDTO(onLineMonitoringDTO);//根据用电企业id查询当前维保公司id，目的为了拼凑查询有功功率的表名
        if(listCompany!=null&&listCompany.size()>0){
            String ynBaseStaffCompanyId= (String) listCompany.get(0).get("ynBaseStaffCompanyId");//获取到维保公司id
            //根据用电企业id查询当前天的总有功情况TO_打头的字段
            onLineMonitoringDTO.setTableNameString("jc_gj_"+ynBaseStaffCompanyId);//设置查询电度的表名，每个维保公司的都不一样所以需要根据维保公司id去查询
            if(Integer.parseInt(userType) == 1 || Integer.parseInt(userType) == 2){
                monitoringObjectDTO.setYnBaseCustomerEnterpriseLongId("");
                onLineMonitoringDTO.setYnBaseCustomerEnterpriseLongId("");
            }
            int zCount=ynMonitoringInitMapper.selectZDCount(monitoringObjectDTO);
            int zRL=ynMonitoringInitMapper.selectZRL(monitoringObjectDTO);
            aMap.put("zCount",zCount);
            aMap.put("zRL",zRL);
            String date = onLineMonitoringDTO.getMonitoringPointDate();
            if(date.length() == 6){
                // String dateSix = formatter2.format(date);
                StringBuilder sb = new StringBuilder(date);//构造一个StringBuilder对象
                date = sb.toString();
                sb.insert(4, "-");
                System.out.println(date);
                onLineMonitoringDTO.setMonitoringPointDate(String.valueOf(sb));
                List<LinkedHashMap> gjList  = ynMonitoringInitMapper.selectGJBySix(onLineMonitoringDTO);
                aMap.put("gjList",gjList);
            }else {
                StringBuilder sb = new StringBuilder(date);//构造一个StringBuilder对象
                date = sb.toString();
                sb.insert(4, "-");
                sb.insert(7, "-");
                System.out.println(sb);
                onLineMonitoringDTO.setMonitoringPointDate(String.valueOf(sb));
                List<LinkedHashMap> gjList  = ynMonitoringInitMapper.selectGJByEight(onLineMonitoringDTO);
                aMap.put("gjList",gjList);
            }




            detailDTO.setDetail(aMap);

        } else {
            detailDTO.setStatus(false);
            detailDTO.setErrorCode("-1");
            detailDTO.setErrorMessage("查询首页告警数据后台出错");
            return detailDTO;
        }
        return detailDTO;
    }

}
