package pw.wechatbrother.base.service.impl;


import org.springframework.stereotype.Service;
import pw.wechatbrother.base.dto.DetailDTO;
import pw.wechatbrother.base.dto.LoginAppDTO;
import pw.wechatbrother.base.dto.onlinemonitoring.MonitoringObjectDTO;
import pw.wechatbrother.base.dto.onlinemonitoring.OnLineMonitoringDTO;
import pw.wechatbrother.base.mapper.YnBaseMapper;
import pw.wechatbrother.base.service.YnBaseService;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class YnBaseServiceImpl implements YnBaseService {
    @Resource
    YnBaseMapper ynBaseMapper;
    //SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat formatterYMD = new SimpleDateFormat("yyyyMMdd");
    SimpleDateFormat formatterYMDHms = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public DetailDTO selectSubstationMsgByEnterpriseId(OnLineMonitoringDTO onLineMonitoringDTO, LoginAppDTO appLoginUser) {
        DetailDTO detailDTO = new DetailDTO(true);
        List<LinkedHashMap> listCompany=ynBaseMapper.selectCompanyByOnLineMonitoringDTO(onLineMonitoringDTO);//根据用电企业id查询当前维保公司id，目的为了拼凑查询有功功率的表名
        String ynBaseStaffCompanyId="";
        if(listCompany!=null&&listCompany.size()>0) {
            ynBaseStaffCompanyId = (String) listCompany.get(0).get("ynBaseStaffCompanyId");//获取到维保公司id
        }else{
            detailDTO.setStatus(false);
            detailDTO.setErrorMessage("维保公司id不存在");
            return detailDTO;
        }
        onLineMonitoringDTO.setTableNameString("jc_gj_"+ynBaseStaffCompanyId);
        List<LinkedHashMap> mapList=ynBaseMapper.selectSubstationMsgByEnterpriseId(onLineMonitoringDTO);
       /* Map returnMap = new HashMap();
        if(mapList!=null &&mapList.size()>0){
            returnMap.put("resultList", mapList);
        }else{
            returnMap.put("resultList", new ArrayList<Map>());
        }
        detailDTO.setDetail(returnMap);*/
        if(mapList!=null&&mapList.size()>0){
            detailDTO.setDetail(mapList);
        }else{
            detailDTO.setDetail(new ArrayList<Map>());
        }
        return detailDTO;
    }

    public DetailDTO selectEquipmentMonitoringPointBySubstationId(OnLineMonitoringDTO onLineMonitoringDTO, LoginAppDTO appLoginUser) {
        DetailDTO detailDTO = new DetailDTO(true);
        List<LinkedHashMap> mapList=ynBaseMapper.selectEquipmentMonitoringPointBySubstationId(onLineMonitoringDTO);
        //detailDTO.setDetail(mapList);
        if(mapList!=null&&mapList.size()>0){
            detailDTO.setDetail(mapList);
        }else{
            detailDTO.setDetail(new ArrayList<Map>());
        }
        return detailDTO;
    }

    public DetailDTO selectReportByMonitoringPoint(OnLineMonitoringDTO onLineMonitoringDTO, LoginAppDTO appLoginUser) {
        DetailDTO detailDTO = new DetailDTO(true);
        Map returnMapResult = new HashMap();
        List<LinkedHashMap> mapList = null;
        List<LinkedHashMap> timeSharing = null;  //分时电能
        MonitoringObjectDTO monitoringObjectDTO=new MonitoringObjectDTO();
        monitoringObjectDTO.setMonitoringPointAliasId(onLineMonitoringDTO.getMonitoringPointAliasId());
        String lastDataTag="";
        String nowDateDay=formatterYMD.format(new Date());
        if(nowDateDay.equals(onLineMonitoringDTO.getMonitoringPointDate())){//如果是当前时间就获取真正的计量点时间
            List<LinkedHashMap> list=ynBaseMapper.selectYnYjInFoByMonitoringObjectDTO(monitoringObjectDTO);
            if(list!=null&&list.size()>0){
                Map lastDataTagMap=list.get(0);
          /*  Object oo=lastDataTagMap.get(lastDataTag);
               System.out.println(oo);*/
                lastDataTag= (String) lastDataTagMap.get("lastDataTag");
                /**
                 * 判断是否电度
                 * 如果是电度需要如果是当天的话拿到最后的更新日期的小时之后减一，然后转换成电度‘
                 * ?的坐标，比如：1530拿到15，减一后等于14 然后得到的最后的坐标是14001500，注意如果拿到的是00XX的话拿到的坐标也是00000100
                 * 如果不是电度直接获取这个lastDataTag标签就好
                 */
                if(onLineMonitoringDTO.getTag().toUpperCase()=="DD"||onLineMonitoringDTO.getTag().toUpperCase().equals("DD")){
                    String lastHourString =lastDataTag.substring(0,2);
                    Integer lastHourInteger=Integer.parseInt(lastHourString);
                    Integer lastHourreduceOneInteger=lastHourInteger-1;//最后一个小时时间标签减一
                    if(lastHourInteger==0){
                        lastDataTag="00000100";
                    }else if(lastHourInteger<11){
                        lastDataTag="0"+lastHourreduceOneInteger+"00"+lastHourString+"00";
                    }else{
                        lastDataTag=lastHourreduceOneInteger+"00"+lastHourString+"00";
                    }
                }
                System.out.println("lastDataTag="+lastDataTag);
            }
        }else{//如果非当前天数则直接设置成最后一个点
            //判断是否电度类型
            if(onLineMonitoringDTO.getTag().toUpperCase()=="DD"||onLineMonitoringDTO.getTag().toUpperCase().equals("DD")){
                lastDataTag="23002400";
            }else{
                lastDataTag="2345";
            }

        }
        if(onLineMonitoringDTO.getTag().equals("xy")||onLineMonitoringDTO.getTag().equals("xl")) {

            mapList=ynBaseMapper.selectHarmonicMonitoringPoint(onLineMonitoringDTO);

        }else if(onLineMonitoringDTO.getTag().equals("DLimbalances")|| onLineMonitoringDTO.getTag().equals("DYimbalances")){

            mapList=ynBaseMapper.selectReportByMonitoringPointTwo(onLineMonitoringDTO);
        }else if(onLineMonitoringDTO.getTag().equals("nx")){
            mapList=ynBaseMapper.selectZoer(onLineMonitoringDTO);
        }else if(onLineMonitoringDTO.getTag().equals("sz")||onLineMonitoringDTO.getTag().equals("yg")||onLineMonitoringDTO.getTag().equals("wg")||onLineMonitoringDTO.getTag().equals("ys")||onLineMonitoringDTO.getTag().equals("fs")){
            mapList=ynBaseMapper.selectAggregate(onLineMonitoringDTO);

        }else{

            System.out.println("其他"+onLineMonitoringDTO.getTag());
            mapList=ynBaseMapper.selectReportByMonitoringPoint(onLineMonitoringDTO);
            // System.out.println("qita"+mapList);
        }


        List<String> xaxisData =new ArrayList<String>() ;
        List<Float> seriesDataT= new ArrayList<Float>();
        List<Float> seriesDataA= new ArrayList<Float>();
        List<Float> seriesDataB= new ArrayList<Float>();
        List<Float> seriesDataC= new ArrayList<Float>();



        if(mapList!=null &&mapList.size()>0){
            Map returnMap = mapList.get(0);
            int iType = 0;//单相还是三相
            int iNum = 0;//每天每个计量点的总个数
            int iX = 0;
            int iY = 0;
            int i = 0;

            for (Object value : returnMap.values()) {
                //System.out.println("Value = " + value);
                if(i==0)
                    iType = Integer.parseInt(value+"");
                if(i==1)
                    iNum = Integer.parseInt(value+"");
                if(i > 1)
                    break;
                i++;
                //System.out.println("这里的i时多少"+i);

            }
            i = 0;
            iX = 0*iNum + 2;
            iY = 1*iNum + 2;
            for (Object value : returnMap.values()) {
                // System.out.println("Value = " + value);
                if(i >= iX && i < iY)
                    xaxisData.add(value+"");
                if(i >= iY)
                    break;;
                i++;
            }


            returnMapResult.put("monitoringPointType",iType);//监测点类型 1单相，3三相，4四项，11温度，12湿度 （小于10就是基本电参量，大于10的就是环境参量）
            returnMapResult.put("monitoringPointNumber",iNum);//计量点数

            //分时电能处理
            if(onLineMonitoringDTO.getTag().equals("fs")){
                float fz=0;
                float fa=0;
                float fb=0;
                float fc=0;
                String monitoringPointAliasId1 = onLineMonitoringDTO.getMonitoringPointAliasId();
                String nowDateDay1=onLineMonitoringDTO.getMonitoringPointDate();
                try {
                    Date date = formatterYMD.parse(nowDateDay1);
                    Calendar calendar=Calendar.getInstance();
                    calendar.setTime(date);
                    calendar.add(Calendar.DAY_OF_YEAR,-1);
                    Date preDay = calendar.getTime();
                    nowDateDay1 =formatterYMD.format(preDay);

                }catch (ParseException e){
                    e.printStackTrace();
                }


                Map map = new HashMap();
                map.put("monitoringPointAliasId1",monitoringPointAliasId1);
                map.put("nowDateDay1",nowDateDay1);
                timeSharing = ynBaseMapper.selectTimeSharing(map);

                if(timeSharing!=null){
                    //判断list数组不等于空 就赋值
                    fz =(Float) ((LinkedHashMap) timeSharing.get(0)).get("ZDL_2345");
                    fa =(Float) ((LinkedHashMap) timeSharing.get(0)).get("ADL_2345");
                    fb =(Float) ((LinkedHashMap) timeSharing.get(0)).get("BDL_2345");
                    fc =(Float) ((LinkedHashMap) timeSharing.get(0)).get("CDL_2345");
                }else if(timeSharing ==null ||timeSharing.size() ==0){
                    //如果为空就赋值为一
                    fz =1;
                    fa = 1;
                    fb = 1;
                    fc = 1;
                }

                for(int TNum = 1;TNum<iType+2;TNum++)
                {
                    i = 0;
                    iX = TNum*iNum + 2;
                    iY = (TNum+1)*iNum + 2;

                    boolean isnotnullA =false;
                    boolean isnotnullB =false;
                    boolean isnotnullC =false;
                    boolean isnotnullT =false;
                    Iterator entries = returnMap.entrySet().iterator();
                    while (entries.hasNext()) {
                        Map.Entry entry = (Map.Entry) entries.next();
                        String key = (String)entry.getKey();
                        key=key.replace("/1000","");
                        Object value = entry.getValue();
                        BigDecimal big1;
                        float t2 = 0;
                        if(i >= iX && i < iY) {
                            float valueFloat=0l;
                            if(value==null||value.equals("")){
                                valueFloat=0l;//采用默认值

                            }else if(key.substring(0,1).equals("A")){
                                valueFloat=Float.parseFloat(value + "");
                                if(fa ==1){
                                    t2 = valueFloat - valueFloat;
                                    fa = valueFloat;
                                    valueFloat = t2;
                                }else{
                                    t2 = valueFloat-fa;
                                    fa = valueFloat;
                                    big1 = new BigDecimal(t2);
                                    t2 = big1.setScale(2,BigDecimal.ROUND_HALF_UP).floatValue();
                                    valueFloat = t2;
                                }


                            }else if(key.substring(0,1).equals("B")){
                                valueFloat=Float.parseFloat(value + "");
                                if(fb == 1){
                                    t2 = valueFloat - valueFloat;
                                    fb = valueFloat;
                                    valueFloat = t2;
                                }else{
                                    t2 = valueFloat-fb;
                                    fb = valueFloat;
                                    big1 = new BigDecimal(t2);
                                    t2 = big1.setScale(2,BigDecimal.ROUND_HALF_UP).floatValue();
                                    valueFloat = t2;
                                }

                            }else if(key.substring(0,1).equals("C")){
                                valueFloat=Float.parseFloat(value + "");
                                if(fc ==1){
                                    t2 = valueFloat - valueFloat;
                                    fc = valueFloat;
                                    valueFloat = t2;
                                }else{
                                    t2 = valueFloat-fc;
                                    fc = valueFloat;
                                    big1 = new BigDecimal(t2);
                                    t2 = big1.setScale(2,BigDecimal.ROUND_HALF_UP).floatValue();
                                    valueFloat = t2;
                                }

                            }else  if(key.substring(0,1).equals("Z")){
                                valueFloat=Float.parseFloat(value + "");
                                if(fc ==1){
                                    t2 = valueFloat - valueFloat;
                                    fz = valueFloat;
                                    valueFloat = t2;
                                }else{
                                    t2 = valueFloat-fz;
                                    fz = valueFloat;
                                    big1 = new BigDecimal(t2);
                                    t2 = big1.setScale(2,BigDecimal.ROUND_HALF_UP).floatValue();
                                    valueFloat = t2;
                                }

                            }


                            switch (TNum) {
                                case 1:
                                    if(!isnotnullA){
                                        seriesDataA.add(valueFloat);
                                    }
                                    break;
                                case 2:
                                    if(!isnotnullB){
                                        seriesDataB.add(valueFloat);
                                    }
                                    break;
                                case 3:
                                    if(!isnotnullC){
                                        seriesDataC.add(valueFloat);
                                    }
                                    break;

                                case 4:
                                    if(!isnotnullT){
                                        seriesDataT.add(valueFloat);
                                    }
                                    break;
                            }
                            String keyStr=key.split("_")[1].toString();
                            //判断是否相等相等则置为有效
                            if(keyStr==lastDataTag||keyStr.equals(lastDataTag)){
                                if(TNum==1){//A相
                                    isnotnullA=true;
                                }else if(TNum==2){//B相
                                    isnotnullB=true;
                                }else if(TNum==3){//C相
                                    isnotnullC=true;
                                }
                                else if(TNum==4){//T相
                                    isnotnullT=true;
                                }
                            }
                        }
                        if(i >= iY)
                            break;;
                        i++;
                    }

                }
            }


            //视在、有功、无功、功率因素
            else if(onLineMonitoringDTO.getTag().equals("sz")||onLineMonitoringDTO.getTag().equals("yg")||onLineMonitoringDTO.getTag().equals("wg")||onLineMonitoringDTO.getTag().equals("ys")){
                for(int TNum = 1;TNum<iType+2;TNum++)
                {
                    i = 0;
                    iX = TNum*iNum + 2;
                    iY = (TNum+1)*iNum + 2;

                    boolean isnotnullA =false;
                    boolean isnotnullB =false;
                    boolean isnotnullC =false;
                    boolean isnotnullT =false;
                    Iterator entries = returnMap.entrySet().iterator();
                    while (entries.hasNext()) {
                        Map.Entry entry = (Map.Entry) entries.next();
                        String key = (String)entry.getKey();
                        key=key.replace("/1000","");
                        Object value = entry.getValue();
                        if(i >= iX && i < iY) {
                            float valueFloat=0l;
                            if(value==null||value.equals("")){
                                valueFloat=0l;//采用默认值
                            }else{
                                valueFloat=Float.parseFloat(value + "");
                            }

                            switch (TNum) {
                                case 1:
                                    if(!isnotnullA){
                                        seriesDataA.add(valueFloat);
                                    }
                                    break;
                                case 2:
                                    if(!isnotnullB){
                                        seriesDataB.add(valueFloat);
                                    }
                                    break;
                                case 3:
                                    if(!isnotnullC){
                                        seriesDataC.add(valueFloat);
                                    }
                                    break;

                                case 4:
                                    if(!isnotnullT){
                                        seriesDataT.add(valueFloat);
                                    }
                                    break;
                            }
                            String keyStr=key.split("_")[1].toString();
                            //判断是否相等相等则置为有效
                            if(keyStr==lastDataTag||keyStr.equals(lastDataTag)){
                                if(TNum==1){//A相
                                    isnotnullA=true;
                                }else if(TNum==2){//B相
                                    isnotnullB=true;
                                }else if(TNum==3){//C相
                                    isnotnullC=true;
                                }
                                else if(TNum==4){//T相
                                    isnotnullT=true;
                                }
                            }
                        }
                        if(i >= iY)
                            break;;
                        i++;
                    }

                }


                //判断是否是单相
            }else {
                if (iType == 1 || iType == 11 || iType == 12 || iType == 14) {//表示单相

                    boolean isnotnullA = false;//单相是否存在存在
                    i = 0;
                    iX = 1 * iNum + 2;
                    iY = 2 * iNum + 2;

                    Iterator entries = returnMap.entrySet().iterator();
                    while (entries.hasNext()) {
                        Map.Entry entry = (Map.Entry) entries.next();
                        String key = (String) entry.getKey();
                        key = key.replace("/1000", "");
                        Object value = entry.getValue();
                        //System.out.println("Key = " + key + ", Value = " + value);
                        if (i >= iX && i < iY) {
                            float valueFloat = 0l;
                            if (value == null || value.equals("")) {
                                valueFloat = 0l;//也就是采用默认值
                            } else {
                                valueFloat = Float.parseFloat(value + "");
                            }
                            //seriesDataT.add(Float.parseFloat(value+""));
                            if (!isnotnullA) {//没到最后一个点才加否则不加了
                                seriesDataT.add(valueFloat);
                            }
                            String keyStr = key.split("_")[1].toString();
                            //判断是否相等相等则置为有效
                            if (keyStr == lastDataTag || keyStr.equals(lastDataTag)) {
                                isnotnullA = true;
                            }
                        }
                        if (i >= iY)
                            break;

                        i++;
                    }

                    //表示三相
                } else/* if(iType==3)*/ {//表示三相
                    //  float a1 = 100;
                    for (int TNum = 1; TNum < iType + 1; TNum++) {
                        i = 0;
                        iX = TNum * iNum + 2;
                        iY = (TNum + 1) * iNum + 2;

                        boolean isnotnullA = false;
                        boolean isnotnullB = false;
                        boolean isnotnullC = false;
                        //   boolean isnotnullT =false;
                        Iterator entries = returnMap.entrySet().iterator();
                        while (entries.hasNext()) {
                            Map.Entry entry = (Map.Entry) entries.next();
                            String key = (String) entry.getKey();

                            key = key.replace("/1000", "");
                            Object value = entry.getValue();


                            if (i >= iX && i < iY) {
                                float valueFloat = 0l;
                                if (value == null || value.equals("")) {
                                    valueFloat = 0l;//采用默认值
                                } else {
                                    valueFloat = Float.parseFloat(value + "");
                                }
                                switch (TNum) {
                                    case 1:
                                        if (!isnotnullA) {
                                            seriesDataA.add(valueFloat);
                                        }
                                        break;
                                    case 2:
                                        if (!isnotnullB) {
                                            seriesDataB.add(valueFloat);
                                        }
                                        break;
                                    case 3:
                                        if (!isnotnullC) {
                                            seriesDataC.add(valueFloat);
                                        }
                                        break;


                                }
                                String keyStr = null;
                                if(onLineMonitoringDTO.getTag().equals("xy")||onLineMonitoringDTO.getTag().equals("xl")){
                                    keyStr = key.split("_TOE_")[1].toString();
                                }else{
                                    keyStr = key.split("_")[1].toString();
                                }

                                //判断是否相等相等则置为有效
                                if (keyStr == lastDataTag || keyStr.equals(lastDataTag)) {
                                    if (TNum == 1) {//A相
                                        isnotnullA = true;
                                    } else if (TNum == 2) {//B相
                                        isnotnullB = true;
                                    } else if (TNum == 3) {//C相
                                        isnotnullC = true;
                                    }

                                }
                            }
                            if (i >= iY)
                                break;
                            ;
                            i++;
                        }

                    }
                }

            }
        }else{
            Map paramMap=new HashMap();
            paramMap.put("rowNumber",1);//如果数据为空的时候默认返回96个点。如果是96个点就传1，如果是48个点就传2，如果24个点就传3，如果是12个点就传4
            List<String> StringList=ynBaseMapper.selectXaxisDataWhenResultIsNull(paramMap);
            xaxisData=StringList;
        }
        returnMapResult.put("xaxisData",xaxisData);
        returnMapResult.put("seriesDataT",seriesDataT);
        returnMapResult.put("seriesDataA",seriesDataA);
        returnMapResult.put("seriesDataB",seriesDataB);
        returnMapResult.put("seriesDataC",seriesDataC);


        detailDTO.setDetail(returnMapResult);
        return detailDTO;
    }


//===================================================================================================================================================


    public DetailDTO selectWarningByMonitoringPoint(OnLineMonitoringDTO onLineMonitoringDTO, LoginAppDTO appLoginUser) {
        DetailDTO detailDTO=new DetailDTO(true);
        //int companyId = Integer.parseInt(onLineMonitoringDTO.getObjectId());
        Map companyMaps = new HashMap();


        int companyId = Integer.parseInt(onLineMonitoringDTO.getObjectId());
        String companyIds = "";
        companyMaps.put("companyId",companyId);
        String services = onLineMonitoringDTO.getServicesAreas();
        String[] companyStr = services.split(",");
        ArrayList serviceList = new ArrayList();
        if (Integer.parseInt(onLineMonitoringDTO.getTag())==3) {
            for (int i = 0; i < companyStr.length; i++) {
                serviceList.add(companyStr[i]);
            }
            companyMaps.put("serviceList", serviceList);
            List<Map> companyList = new ArrayList<Map>();

            if (companyId == 83650) {
                companyList = ynBaseMapper.selectAllInfoCompany(companyMaps);
            } else if (companyId == 83800) {
                companyList = ynBaseMapper.selectHNInfoCompany(companyMaps);
            } else {
                companyList = ynBaseMapper.selectInfoCompany(companyMaps);
            }
            if (companyList.size()>0) {
                companyIds = companyList.get(0).get("ids").toString();
            }
        }
        onLineMonitoringDTO.setServicesAreas(companyIds);

        List<LinkedHashMap> mapList=ynBaseMapper.selectWarningByMonitoringPoint(onLineMonitoringDTO);
        if(mapList!=null&&mapList.size()>0){
            Map maps = mapList.get(0);
            //处理Json的null的问题
            for(Map map1:mapList){
                Object warningDealUser=map1.get("warningDealUser");
                Object warningDealDate=map1.get("warningDealDate");
                if(warningDealUser==null){
                    map1.put("warningDealUser","");
                }
                if(warningDealDate==null){
                    map1.put("warningDealDate","");
                }
            }
            detailDTO.setDetail(mapList);
        }else{
            detailDTO.setDetail(new ArrayList<LinkedHashMap>());
        }
        List<LinkedHashMap> mapListCount=ynBaseMapper.selectWarningByMonitoringPointCount(onLineMonitoringDTO);
        if(mapListCount!=null&&mapListCount.size()>0){
            detailDTO.setEnumeration((String) mapListCount.get(0).get("countNumber"));
            }
            return detailDTO;
    }
    //如果查到的值有为null的  则赋值为0
    public List<LinkedHashMap> zoreReturnMap(List<LinkedHashMap> hList){
        if(hList!=null && hList.size()>0){
            Map returnMap = hList.get(0);
            for (Object key : returnMap.keySet()) {
                if(returnMap.get(key)==null||returnMap.get(key).equals("")){
                    returnMap.put(key,0);
                }
            }

        }

        return hList;
    }

    public DetailDTO selectEnterpriseInfoJCD(){
        DetailDTO detailDTO=new DetailDTO(true);
        SimpleDateFormat formatter2  = new SimpleDateFormat("yyyyMMdd");
        //当月一号
        Calendar calendar1=Calendar.getInstance();
        calendar1.set(Calendar.DAY_OF_MONTH, 1);
        System.out.println("本月第一天: "+formatter2.format(calendar1.getTime()));
        String date = formatter2.format(calendar1.getTime());
        List<LinkedHashMap> eList=ynBaseMapper.selectEnterpriseInfo(date);
      /* for (Object key : eList.get(0).keySet()) {
            value = (String) eList.get(0).get(key);
        }*/

     /* for(int i = 0;i<eList.size();i++){
          LinkedHashMap ha = eList.get(i);
          for(Object key : ha.keySet()){
             String  value = (String) ha.get(key);
              System.out.println(value);

          }

          //System.out.println(ha);
      }*/
        Map  amap = new HashMap();
        amap.put("eList",eList);
        detailDTO.setDetail(amap);
        return detailDTO;
    }


    //月度报告所需数据
    public DetailDTO selectPowerInfoJCD(OnLineMonitoringDTO onLineMonitoringDTO) {
        // OnLineMonitoringDTO onLineMonitoringDTO = new OnLineMonitoringDTO();
        DetailDTO detailDTO=new DetailDTO(true);
        List<LinkedHashMap> listCompany=ynBaseMapper.selectCompanyByOnLineMonitoringDTO(onLineMonitoringDTO);
        if(listCompany!=null&&listCompany.size()>0) {
            String ynBaseStaffCompanyId = (String) listCompany.get(0).get("ynBaseStaffCompanyId");//获取到维保公司id
            onLineMonitoringDTO.setTableNameString("jc_dn_" + ynBaseStaffCompanyId);
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
            SimpleDateFormat formatter2  = new SimpleDateFormat("yyyyMMdd");
            String data = formatter2.format(new Date());// new Date()为获取当前系统时间
            System.out.println(data);
            onLineMonitoringDTO.setMonitoringPointDate(data);

            //功率因素   日期在数据库处理为上月
            List ysList = ynBaseMapper.selectMonthReportGLYS(onLineMonitoringDTO);



            //修改日期为上月日期
            //  String dateString = "";
            try {
                Date  hh = formatter2.parse(data);
                Calendar ca = Calendar.getInstance();//得到一个Calendar的实例
                ca.setTime(hh); //设置时间为当前时间

                //求前一月
                ca.add(Calendar.MONTH, -1);
                //前一天ca.add(Calendar.DATE, -1);
                Date lastmonth = ca.getTime();
                String dateString = df.format(lastmonth);
                dateString = dateString.replace("-","");
                //System.out.println(dateString);
                dateString = dateString.substring(0,6);
                onLineMonitoringDTO.setMonitoringPointDate(dateString);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            //上月需量
            // onLineMonitoringDTO.setMonitoringPointDate(data.substring(0,6));

            List xList = ynBaseMapper.selectMonthReportXL(onLineMonitoringDTO);

            //上月告警
            onLineMonitoringDTO.setTableNameString("jc_gj_" + ynBaseStaffCompanyId);
            List gList = ynBaseMapper.selectMonthReportGJ(onLineMonitoringDTO);


            // SimpleDateFormat hDate = new SimpleDateFormat("yyyyMMdd");
            Calendar calendar = Calendar.getInstance();
            Date theDate = calendar.getTime();
            String s = formatter2.format(theDate);
            //calendar.add(Calendar.MONTH, -1); //加一个月
            calendar.set(Calendar.DATE, 1); //设置为该月第一天
            calendar.add(Calendar.DATE, -1); //再减一天即为上个月最后一天
            String day_last = formatter2.format(calendar.getTime());
            StringBuffer endStr = new StringBuffer().append(day_last);
            day_last = endStr.toString();
            StringBuffer str = new StringBuffer().append(day_last);
            System.out.println(str.toString());
            onLineMonitoringDTO.setMonitoringPointDate(str.toString());


            //电能电量其他数据
            List rList = ynBaseMapper.selectMonthReport(onLineMonitoringDTO);

       /* onLineMonitoringDTO.setMonitoringPointDate(powerDate);
        //  峰平谷 电费
        List powerList = ynBaseMapper.selectFPGAndYe(onLineMonitoringDTO);

        onLineMonitoringDTO.setDateString(powerDate);
        List<LinkedHashMap> eList=ynBaseMapper.selectElectricAnalysisVolume(onLineMonitoringDTO);
        zoreReturnMap(eList);


        //月电能信息
        List<LinkedHashMap> MdnList=ynBaseMapper.selectMDianneng(onLineMonitoringDTO);
        zoreReturnMap(MdnList);*/


            Map  amap = new HashMap();
            amap.put("rList",rList);
            amap.put("xList",xList);
            amap.put("gList",gList);
            amap.put("ysList",ysList);

            detailDTO.setDetail(amap);
        }


        return detailDTO;
    }


    //获取时间字符串插入
    public List<LinkedHashMap> dateReturnMap(List<LinkedHashMap> hList){

        if(hList!=null && hList.size()>0){
            Map dnMap = hList.get(0);
            for (Object key : dnMap.keySet()) {
                String test = (String) key;
                if(test.indexOf("Date")!=-1||test.indexOf("date")!=-1){
                    //System.out.println("===============================");
                    Object hh = dnMap.get(key);
                    String hhh = String.valueOf(hh);
                    if(hhh.length()==10){
                        String data = hhh.substring(2,10);
                        StringBuilder sb = new StringBuilder(data);//构造一个StringBuilder对象
                        sb.insert(2, ".");//在指定的位置1，插入指定的字符串
                        sb.insert(5, " ");
                        sb.insert(8, ":");
                        dnMap.put(key,sb);
                    }else if(hhh.length()==8){
                        StringBuilder sb = new StringBuilder(hhh);//构造一个StringBuilder对象
                        sb.insert(2, ".");//在指定的位置1，插入指定的字符串
                        sb.insert(5, " ");
                        sb.insert(8, ":");
                        dnMap.put(key,sb);
                    }else if(hhh.length()==12){
                        String data = hhh.substring(2,10);
                        StringBuilder sb = new StringBuilder(data);//构造一个StringBuilder对象
                        sb.insert(2, ".");//在指定的位置1，插入指定的字符串
                        sb.insert(5, " ");
                        sb.insert(8, ":");
                        dnMap.put(key,sb);
                    }
                }else{
                    //System.out.println("____________________________");
                }

            }

        }

        return hList;
    }

    //月数据表格
    public DetailDTO selectElectricAnalysisByMonitoringPointTWO(OnLineMonitoringDTO onLineMonitoringDTO, LoginAppDTO appLoginUser) {
        DetailDTO detailDTO=new DetailDTO(true);
        List<LinkedHashMap> listCompany=ynBaseMapper.selectCompanyByOnLineMonitoringDTO(onLineMonitoringDTO);
        if(listCompany!=null&&listCompany.size()>0) {
            String ynBaseStaffCompanyId = (String) listCompany.get(0).get("ynBaseStaffCompanyId");//获取到维保公司id
            // List<LinkedHashMap> mapList=ynBaseMapper.selectElectricAnalysisByMonitoringPoint(onLineMonitoringDTO);
            List<LinkedHashMap> eList=ynBaseMapper.selectElectricAnalysisVolume(onLineMonitoringDTO);

            String  h = onLineMonitoringDTO.getMonitoringPointDate();
            SimpleDateFormat formatter  = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat formatter2  = new SimpleDateFormat("yyyyMMdd");

            //------------------------------------------------------以下为查询月数据    getMonitoringPointDate设为六位
        /*String twodate = onLineMonitoringDTO.getMonitoringPointDate();
        twodate = twodate.substring(1,6);
        onLineMonitoringDTO.setMonitoringPointDate(twodate);*/
            //修改日期为上月日期
            try {
                Date  date = formatter2.parse(h);
                Calendar ca = Calendar.getInstance();//得到一个Calendar的实例
                ca.setTime(date); //设置时间为当前时间
                //ca.add(Calendar.YEAR, -1); //年份减1
                //Date lastMonth = ca.getTime(); //结果
                //求前一月
                //ca.add(Calendar.MONTH, -1);
                //前一天ca.add(Calendar.DATE, -1);
                Date lastmonth = ca.getTime();
                String dateString = formatter.format(lastmonth);
                dateString = dateString.replace("-","");
                //System.out.println(dateString);
                dateString = dateString.substring(0,6);
                onLineMonitoringDTO.setMonitoringPointDate(dateString);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            //月电能信息
            onLineMonitoringDTO.setTableNameString("jc_mn_"+ynBaseStaffCompanyId);
            List<LinkedHashMap> MdnList=ynBaseMapper.selectMDianneng(onLineMonitoringDTO);
            zoreReturnMap(MdnList);
            dateReturnMap(MdnList);
            //月功率因数
            onLineMonitoringDTO.setTableNameString("jc_mj_"+ynBaseStaffCompanyId);
            List<LinkedHashMap> MglysList=ynBaseMapper.selectMGLGLYS(onLineMonitoringDTO);
            zoreReturnMap(MglysList);
            dateReturnMap(MglysList);
            //月电流极值
            onLineMonitoringDTO.setTableNameString("jc_ml_"+ynBaseStaffCompanyId);
            List<LinkedHashMap> MdljzList=ynBaseMapper.selectMDLJZ(onLineMonitoringDTO);
            zoreReturnMap(MdljzList);
            dateReturnMap(MdljzList);
            //月电压极值
            onLineMonitoringDTO.setTableNameString("jc_my_"+ynBaseStaffCompanyId);
            List<LinkedHashMap> MdyjzList=ynBaseMapper.selectMDYJZ(onLineMonitoringDTO);
            zoreReturnMap(MdyjzList);
            dateReturnMap(MdyjzList);
            //月总谐波
            onLineMonitoringDTO.setTableNameString("jc_mx_"+ynBaseStaffCompanyId);
            List<LinkedHashMap> MrzxbList=ynBaseMapper.selectMRZXB(onLineMonitoringDTO);
            zoreReturnMap(MrzxbList);
            dateReturnMap(MrzxbList);
            //月断相
            onLineMonitoringDTO.setTableNameString("jc_md_"+ynBaseStaffCompanyId);
            List<LinkedHashMap> MrdxList=ynBaseMapper.selectMRDX(onLineMonitoringDTO);
            zoreReturnMap(MrdxList);
            dateReturnMap(MrdxList);
            String j = onLineMonitoringDTO.getTag();
            int jj = j.length();

        /*月停电
        List<LinkedHashMap> MtdList=ynBaseMapper.selectMTD(onLineMonitoringDTO);
        zoreReturnMap(MtdList);*/




            Map aMap = new HashMap();
            //aMap.put("one",mapList);
            aMap.put("mdn",MdnList);
            aMap.put("mglys",MglysList);
            aMap.put("mdljz",MdljzList);
            aMap.put("mdyjz",MdyjzList);
            aMap.put("mrzxb",MrzxbList);
            aMap.put("mrdx",MrdxList);

            aMap.put("eList",eList);

       /* if(mapList!=null&&mapList.size()>0){
            detailDTO.setDetail(aMap);
        }else{
            detailDTO.setDetail(new ArrayList<LinkedHashMap>());
        }*/
            detailDTO.setDetail(aMap);
        }

        return detailDTO;

    }

    //日数据表格
    public DetailDTO selectElectricAnalysisByMonitoringPoint(OnLineMonitoringDTO onLineMonitoringDTO, LoginAppDTO appLoginUser) {
        DetailDTO detailDTO=new DetailDTO(true);
        List<LinkedHashMap> listCompany=ynBaseMapper.selectCompanyByOnLineMonitoringDTO(onLineMonitoringDTO);
        if(listCompany!=null&&listCompany.size()>0){
            String ynBaseStaffCompanyId= (String) listCompany.get(0).get("ynBaseStaffCompanyId");//获取到维保公司id
            // onLineMonitoringDTO.setYnBaseStaffCompanyId(ynBaseStaffCompanyId);
            //根据用电企业id查询当前天的总有功情况TO_打头的字段
            // onLineMonitoringDTO.setTableNameString("jc_dd_"+ynBaseStaffCompanyId);
            // List<LinkedHashMap> mapList=ynBaseMapper.selectElectricAnalysisByMonitoringPoint(onLineMonitoringDTO);
            List<LinkedHashMap> eList=ynBaseMapper.selectElectricAnalysisVolume(onLineMonitoringDTO);

            String  h = onLineMonitoringDTO.getMonitoringPointDate();
            SimpleDateFormat formatter  = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat formatter2  = new SimpleDateFormat("yyyyMMdd");


        /*//昨日停电次数和时间
        List<LinkedHashMap> tdList=ynBaseMapper.selectDDTingdianCount(onLineMonitoringDTO);
        zoreReturnMap(tdList);*/
            //昨日电能信息
            onLineMonitoringDTO.setTableNameString("jc_dn_"+ynBaseStaffCompanyId);
            List<LinkedHashMap> dnList=ynBaseMapper.selectDianneng(onLineMonitoringDTO);
            zoreReturnMap(dnList);
            dateReturnMap(dnList);
            //昨日功率极值和功率因数极值
            onLineMonitoringDTO.setTableNameString("jc_yg_"+ynBaseStaffCompanyId);
            List<LinkedHashMap> gljzList=ynBaseMapper.selectGLGLYS(onLineMonitoringDTO);
            zoreReturnMap(gljzList);
            dateReturnMap(gljzList);
            //昨日电流极值
            onLineMonitoringDTO.setTableNameString("jc_jz_"+ynBaseStaffCompanyId);
            List<LinkedHashMap> dljzList=ynBaseMapper.selectDLJZ(onLineMonitoringDTO);
            zoreReturnMap(dljzList);
            dateReturnMap(dljzList);
            //昨日电压极值
            onLineMonitoringDTO.setTableNameString("jc_jy_"+ynBaseStaffCompanyId);
            List<LinkedHashMap> dyjzList=ynBaseMapper.selectDYJZ(onLineMonitoringDTO);
            zoreReturnMap(dyjzList);
            dateReturnMap(dyjzList);
            //昨日总谐波
            onLineMonitoringDTO.setTableNameString("jc_xr_"+ynBaseStaffCompanyId);
            List<LinkedHashMap> rzxbList=ynBaseMapper.selectRZXB(onLineMonitoringDTO);
            zoreReturnMap(rzxbList);
            dateReturnMap(rzxbList);
            //昨日断相
            onLineMonitoringDTO.setTableNameString("jc_dx_"+ynBaseStaffCompanyId);
            List<LinkedHashMap> rdxList=ynBaseMapper.selectRDX(onLineMonitoringDTO);
            zoreReturnMap(rdxList);
            dateReturnMap(rdxList);

            try {
                Date  date = formatter2.parse(h);
                Calendar ca = Calendar.getInstance();//得到一个Calendar的实例


                ca.setTime(date); //设置时间为当前时间
                //ca.add(Calendar.YEAR, -1); //年份减1
                Date lastMonth = ca.getTime(); //结果
                //求前一月
                //*ca.add(Calendar.MONTH, -1);
                Date lastMonths = ca.getTime();
                System.out.println(lastMonths);
                //前一天
                ca.add(Calendar.DATE, -1);
                Date lastday = ca.getTime();
                String dateString = formatter.format(lastday);
                dateString = dateString.replace("-","");
                //System.out.println(dateString);
                onLineMonitoringDTO.setMonitoringPointDate(dateString);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            //昨日昨日电能
            onLineMonitoringDTO.setTableNameString("jc_dn_"+ynBaseStaffCompanyId);
            List<LinkedHashMap> zdnList=ynBaseMapper.selectDNTWO(onLineMonitoringDTO);
            float one = 0;
            float two = 0;
            float three = 0;
            float four = 0;
            if(zdnList.size()>0 && zdnList != null ){
                Map dnMap = zdnList.get(0);
                Object Right_Active_Power =  dnMap.get("Right_Active_Power");
                String ont  = String.valueOf(Right_Active_Power);
                one = Float.parseFloat(ont);
                // Object  Right_Reactive_Power = dnMap.get("Right_Reactive_Power");
                Object  Contrary_Active_Power = dnMap.get("Contrary_Active_Power");
                Object  Contrary_Reactive_Power = dnMap.get("Contrary_Reactive_Power");
                //String TWO = String.valueOf(Right_Reactive_Power);
                String THREE = String.valueOf(Contrary_Active_Power);
                String FOUR = String.valueOf(Contrary_Reactive_Power);

                // two = Float.parseFloat(TWO);
                three = Float.parseFloat(THREE);
                four = Float.parseFloat(FOUR);
            }


            if(dnList != null && dnList.size()>0){
                Map dnMap = dnList.get(0);
                for (Object key : dnMap.keySet()) {
                    if(key == "Right_Active_Power" || key.equals("Right_Active_Power")){
                        Object onePower =  dnMap.get("Right_Active_Power");
                        String onepower = String.valueOf(onePower);
                        float Right_Active_Power = Float.valueOf(onepower);
                        float numone =  Right_Active_Power-one;
                        if(numone!=0){
                            int   scale  =   2;//设置位数
                            int   roundingMode  =  4;//表示四舍五入，可以选择其他舍值方式，例如去尾，等等.
                            BigDecimal bd  =   new  BigDecimal((double)numone);
                            bd   =  bd.setScale(scale,roundingMode);
                            numone   =  bd.floatValue();
                        }

                        dnMap.put(key,numone);

                    }else if(key == "Contrary_Active_Power" || key.equals("Contrary_Active_Power")){
                        Object threePower =  dnMap.get("Contrary_Active_Power");
                        String threepower = String.valueOf(threePower);
                        float Contrary_Active_Power = Float.valueOf(threepower);
                        float numthree =  Contrary_Active_Power -three ;
                        if(numthree!=0){
                            int   scale  =   2;//设置位数
                            int   roundingMode  =  4;//表示四舍五入，可以选择其他舍值方式，例如去尾，等等.
                            BigDecimal bd  =   new  BigDecimal((double)numthree);
                            bd   =  bd.setScale(scale,roundingMode);
                            numthree   =  bd.floatValue();
                        }
                        dnMap.put(key,numthree);

                    }else if(key == "Contrary_Reactive_Power" || key.equals("Contrary_Reactive_Power")){
                        Object fourPower =  dnMap.get("Contrary_Reactive_Power");
                        String fourpower = String.valueOf(fourPower);
                        float Contrary_Reactive_Power = Float.valueOf(fourpower);
                        float numfour =  Contrary_Reactive_Power - four;
                        if(numfour!=0){
                            int   scale  =   2;//设置位数
                            int   roundingMode  =  4;//表示四舍五入，可以选择其他舍值方式，例如去尾，等等.
                            BigDecimal bd  =   new  BigDecimal((double)numfour);
                            bd   =  bd.setScale(scale,roundingMode);
                            numfour   =  bd.floatValue();
                        }
                        dnMap.put(key,numfour);

                    }
                }

            }


        /*//查询电压不平衡度
        onLineMonitoringDTO.setTag("DY");
        List<LinkedHashMap> dyList=ynBaseMapper.selectReportByMonitoringPointTwo(onLineMonitoringDTO);
        //查询电流不平衡度
        onLineMonitoringDTO.setTag("DL");
        List<LinkedHashMap> dlList=ynBaseMapper.selectReportByMonitoringPointTwo(onLineMonitoringDTO);*/
        /*for(Map map:mapList){
            String powerFactor= (String) map.get("powerFactor");//功率因素  高哥说高哥处理我就不处理了
        }*/

            Map aMap = new HashMap();
            // aMap.put("one",mapList);
            //aMap.put("two",tdList);
            aMap.put("three",dnList);
            aMap.put("four",gljzList);
            aMap.put("five",dljzList);
            aMap.put("six",dyjzList);
            aMap.put("seven",rzxbList);
            aMap.put("eight",rdxList);
            //aMap.put("nine",eList);
            aMap.put("eList",eList);
            detailDTO.setDetail(aMap);
        }




       /* if(mapList!=null&&mapList.size()>0){
            detailDTO.setDetail(aMap);
        }else{
            detailDTO.setDetail(new ArrayList<LinkedHashMap>());
        }*/

        return detailDTO;
    }




    //电能曲线
    public DetailDTO selectElectricAnalysisByMonitoringPointDN(OnLineMonitoringDTO onLineMonitoringDTO, LoginAppDTO appLoginUser) {
        DetailDTO detailDTO = new DetailDTO(true);
        List<LinkedHashMap> listCompany=ynBaseMapper.selectCompanyByOnLineMonitoringDTO(onLineMonitoringDTO);
        if(listCompany!=null&&listCompany.size()>0) {
            String ynBaseStaffCompanyId = (String) listCompany.get(0).get("ynBaseStaffCompanyId");//获取到维保公司id
            onLineMonitoringDTO.setTableNameString("jc_dn_"+ynBaseStaffCompanyId);
            List<Integer> seriesData= new ArrayList<Integer>();
            String  h = onLineMonitoringDTO.getMonitoringPointDate();

            SimpleDateFormat formatter2  = new SimpleDateFormat("yyyyMMdd");
            try {
                Date  date = formatter2.parse(h);
                Calendar aCalendar = Calendar.getInstance(Locale.CHINA);
                aCalendar.setTime(date);
                int day=aCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                System.out.println("day = " + day);
                for(int i = 1;i<=day;i++){
                    seriesData.add(i);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

            h = h.substring(0,6);
            onLineMonitoringDTO.setMonitoringPointDate(h);
            //月电能曲线信息
            List<LinkedHashMap> QMdnList=ynBaseMapper.selectMDiannengQ(onLineMonitoringDTO);
            List<Float> seriesValues= new ArrayList<Float>();

            //System.out.println("==========================================");
            int num = 0;

            for(int j = 0;j<QMdnList.size();j++){

                for (Object key : QMdnList.get(j).keySet()) {
                    if(key.equals("createdate")){
                    /*String hh = (String) QMdnList.get(0).get(key);
                    System.out.println("value = " + hh);
                    String[] strArray = hh.split(",");
                    System.out.println("value = " + strArray);
                    for (int i = 0; i < strArray.length; i++) {
                        int hhh = Integer.parseInt(strArray[i].substring(8,10));
                        seriesData.add(hhh);
                    }*/
                        if(j==0){
                            Object hhh =  QMdnList.get(0).get("createdate");
                            String hh = String.valueOf(hhh);
                            int shu = Integer.parseInt(hh.substring(8,10));
                            for(int k=1;k<shu;k++){
                                seriesValues.add(Float.valueOf(0));
                            }
                        }
                    }else{
                        Map aaMap = new HashMap();
                        aaMap = QMdnList.get(j);
                        Object hh = aaMap.get(key);

                        System.out.println("value = " + hh);
                        //String[] strArray = hh.split(",");
                        if(hh==null ||hh.equals("null")){
                            seriesValues.add(Float.valueOf(0));
                        }else{
                            seriesValues.add((Float) hh);
                        }
                    }

                }
            }


            Map aMap = new HashMap();
            aMap.put("monitoringPointType",1);//监测点类型 1单相，3三相，4四项，11温度，12湿度 （小于10就是基本电参量，大于10的就是环境参量）
            aMap.put("seriesData",seriesData);
            aMap.put("seriesValues",seriesValues);
            if(aMap!=null&&aMap.size()>0){
                detailDTO.setDetail(aMap);
            }else{
                detailDTO.setDetail(new ArrayList<LinkedHashMap>());
            }

        }

        return detailDTO;
    }
    //功率因素曲线
    public DetailDTO selectElectricAnalysisByMonitoringPointGLYS(OnLineMonitoringDTO onLineMonitoringDTO, LoginAppDTO appLoginUser) {
        DetailDTO detailDTO = new DetailDTO(true);
        List<LinkedHashMap> listCompany=ynBaseMapper.selectCompanyByOnLineMonitoringDTO(onLineMonitoringDTO);
        if(listCompany!=null&&listCompany.size()>0) {
            String ynBaseStaffCompanyId = (String) listCompany.get(0).get("ynBaseStaffCompanyId");//获取到维保公司id
            onLineMonitoringDTO.setTableNameString("jc_yg_"+ynBaseStaffCompanyId);
            List<Integer> seriesDate= new ArrayList<Integer>();
            String  h = onLineMonitoringDTO.getMonitoringPointDate();
            SimpleDateFormat formatter2  = new SimpleDateFormat("yyyyMMdd");
            try {
                Date  date = formatter2.parse(h);
                Calendar aCalendar = Calendar.getInstance(Locale.CHINA);
                aCalendar.setTime(date);
                int day=aCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                System.out.println("day = " + day);
                for(int i = 1;i<=day;i++){
                    seriesDate.add(i);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            h = h.substring(0,6);
            onLineMonitoringDTO.setMonitoringPointDate(h);
            String hua = onLineMonitoringDTO.getTag();
            if(hua.equals("yggl")|| hua=="yggl"){
                onLineMonitoringDTO.setTag("AMAX_Power,AMIN_Power");
            }else if(hua.equals("wggl")|| hua=="wggl"){
                onLineMonitoringDTO.setTag("RMAX_Power,RMIN_Power");
            }else if(hua.equals("glys")|| hua=="glys"){
                onLineMonitoringDTO.setTag("YS_MAX,YS_MIN");
            }
            List<LinkedHashMap> QList=ynBaseMapper.selectMGLYSQ(onLineMonitoringDTO);



            List<Float> seriesDataMAX= new ArrayList<Float>();
            List<Float> seriesDataMIN= new ArrayList<Float>();


            int num= 1;
            List<Float> hList = new ArrayList();

            for(int j = 0;j<QList.size();j++){
                for (Object key : QList.get(j).keySet()) {
                    if(key.equals("createdate")){
                        num++;
                        if(j==0){
                            Object hhh =  QList.get(0).get("createdate");
                            String hh = String.valueOf(hhh);
                            int shu = Integer.parseInt(hh.substring(8,10));
                            for(int k=1;k<shu;k++){
                                seriesDataMAX.add(Float.valueOf(0));
                                seriesDataMIN.add(Float.valueOf(0));
                            }
                        }
                    }else{
                        Map aaMap = new HashMap();
                        aaMap = QList.get(j);
                        Object hh = aaMap.get(key);
                        String ha = String.valueOf(hh);
                        int kj = ha.length();
                        if(hh==null || hh.equals("")){
                            hList.add((float) 0.0);
                        }else{
                            hList.add((Float) hh);
                        }


                    }

                }
            }
            seriesDataMAX.add((Float) hList.get(0));
            for(int d = 1;d<hList.size();d++){

                if(d%2==0){
                    seriesDataMAX.add((Float) hList.get(d));
                }else{
                    seriesDataMIN.add((Float) hList.get(d));
                }
            }


            Map aMap = new HashMap();
            aMap.put("monitoringPointType",1);//监测点类型 1单相，3三相，4四项，11温度，12湿度 （小于10就是基本电参量，大于10的就是环境参量）
            aMap.put("seriesDate",seriesDate);
            aMap.put("seriesDataMIN",seriesDataMIN);
            aMap.put("seriesDataMAX",seriesDataMAX);
            if(aMap!=null&&aMap.size()>0){
                detailDTO.setDetail(aMap);
            }else{
                detailDTO.setDetail(new ArrayList<LinkedHashMap>());
            }
        }

        return detailDTO;
    }

    //电流极值曲线
    public DetailDTO selectElectricAnalysisByMonitoringPointDLJZ(OnLineMonitoringDTO onLineMonitoringDTO, LoginAppDTO appLoginUser) {
        DetailDTO detailDTO = new DetailDTO(true);
        List<LinkedHashMap> listCompany=ynBaseMapper.selectCompanyByOnLineMonitoringDTO(onLineMonitoringDTO);
        if(listCompany!=null&&listCompany.size()>0) {
            String ynBaseStaffCompanyId = (String) listCompany.get(0).get("ynBaseStaffCompanyId");//获取到维保公司id
            onLineMonitoringDTO.setTableNameString("jc_jz_" + ynBaseStaffCompanyId);
            List<Integer> seriesDate= new ArrayList<Integer>();
            String  h = onLineMonitoringDTO.getMonitoringPointDate();
            SimpleDateFormat formatter2  = new SimpleDateFormat("yyyyMMdd");
            try {
                Date  date = formatter2.parse(h);
                Calendar aCalendar = Calendar.getInstance(Locale.CHINA);
                aCalendar.setTime(date);
                int day=aCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                System.out.println("day = " + day);
                for(int i = 1;i<=day;i++){
                    seriesDate.add(i);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            //定义X轴

            List<Float> seriesDataMAX= new ArrayList<Float>();
            List<Float> seriesDataMIN= new ArrayList<Float>();
            List<Float> seriesDataAVG= new ArrayList<Float>();
            List<Float> seriesDataNINE= new ArrayList<Float>();

            List<Float> seriesDataAMAX= new ArrayList<Float>();
            List<Float> seriesDataAMIN= new ArrayList<Float>();
            List<Float> seriesDataBMAX= new ArrayList<Float>();
            List<Float> seriesDataBMIN= new ArrayList<Float>();
            List<Float> seriesDataCMAX= new ArrayList<Float>();
            List<Float> seriesDataCMIN= new ArrayList<Float>();

            List<Float> seriesDataLMAX= new ArrayList<Float>();
            List<Float> seriesDataLMIN= new ArrayList<Float>();



            h = h.substring(0,6);
            onLineMonitoringDTO.setMonitoringPointDate(h);
            String hua = onLineMonitoringDTO.getTag();
            if(hua.equals("bphdQ")|| hua=="bphdQ"){
                onLineMonitoringDTO.setTag("DLBPH_MAX,DLBPH_MIN,DLBPH_AVG,DLBPH_NINEFIVE");
                List<LinkedHashMap> QList=ynBaseMapper.selectMDLQ(onLineMonitoringDTO);

                List<Float> hList = new ArrayList();
                for(int j = 0;j<QList.size();j++){
                    for (Object key : QList.get(j).keySet()) {
                        if(key.equals("createdate")){
                            // num++;
                            if(j==0){
                                Object hhh =  QList.get(0).get("createdate");
                                String hh = String.valueOf(hhh);
                                int shu = Integer.parseInt(hh.substring(8,10));
                                for(int k=1;k<shu;k++){
                                    seriesDataMAX.add(Float.valueOf(0));
                                    seriesDataMIN.add(Float.valueOf(0));
                                    seriesDataAVG.add(Float.valueOf(0));
                                    seriesDataNINE.add(Float.valueOf(0));
                                }
                            }
                        }else{
                            Map aaMap = new HashMap();
                            aaMap = QList.get(j);
                            Object hh = aaMap.get(key);
                            String ha = String.valueOf(hh);
                            int kj = ha.length();
                            if(hh==null || hh.equals("")){
                                hList.add((float) 0.0);
                            }else{
                                hList.add((Float) hh);
                            }


                        }

                    }
                }
                int count = 0;
                seriesDataMAX.add((Float) hList.get(0));
                for(int d = 1;d<hList.size();d++){
                    count++;
                    if(count==1){
                        seriesDataMIN.add((Float) hList.get(d));
                    }else if(count==2){
                        seriesDataAVG.add((Float) hList.get(d));
                    }else if(count==3){
                        seriesDataNINE.add((Float) hList.get(d));
                    }else if(count==4){
                        seriesDataMAX.add((Float) hList.get(d));
                        count = 0;
                    }
                }
            }else if(hua.equals("dljzQ")|| hua=="dljzQ"){
                onLineMonitoringDTO.setTag("DL_AMAX,DL_AMIN,DL_BMAX,DL_BMIN,DL_CMAX,DL_CMIN");
                List<LinkedHashMap> QList=ynBaseMapper.selectMDLQ(onLineMonitoringDTO);

                List<Float> hList = new ArrayList();
                for(int j = 0;j<QList.size();j++){
                    for (Object key : QList.get(j).keySet()) {
                        if(key.equals("createdate")){
                            // num++;
                            if(j==0){
                                Object hhh =  QList.get(0).get("createdate");
                                String hh = String.valueOf(hhh);
                                int shu = Integer.parseInt(hh.substring(8,10));
                                for(int k=1;k<shu;k++){
                                    seriesDataAMAX.add(Float.valueOf(0));
                                    seriesDataAMIN.add(Float.valueOf(0));
                                    seriesDataBMAX.add(Float.valueOf(0));
                                    seriesDataBMIN.add(Float.valueOf(0));
                                    seriesDataCMAX.add(Float.valueOf(0));
                                    seriesDataCMIN.add(Float.valueOf(0));

                                }
                            }
                        }else{
                            Map aaMap = new HashMap();
                            aaMap = QList.get(j);
                            Object hh = aaMap.get(key);
                            String ha = String.valueOf(hh);
                            int kj = ha.length();
                            if(hh==null || hh.equals("")){
                                hList.add((float) 0.0);
                            }else{
                                hList.add((Float) hh);
                            }


                        }

                    }
                }
                int count = 0;
                seriesDataAMAX.add((Float) hList.get(0));
                for(int d = 1;d<hList.size();d++){
                    count++;
                    if(count==1){
                        seriesDataAMIN.add((Float) hList.get(d));
                    }else if(count==2){
                        seriesDataBMAX.add((Float) hList.get(d));
                    }else if(count==3){
                        seriesDataBMIN.add((Float) hList.get(d));
                    }else if(count==4){
                        seriesDataCMAX.add((Float) hList.get(d));
                    }else if(count==5){
                        seriesDataCMIN.add((Float) hList.get(d));
                    }else if(count==6){
                        seriesDataAMAX.add((Float) hList.get(d));
                        count = 0;
                    }
                }
            }else if(hua.equals("lxdlQ")|| hua=="lxdlQ"){
                onLineMonitoringDTO.setTag("DL_LMAX,DL_LMIN");
                List<LinkedHashMap> QList=ynBaseMapper.selectMDLQ(onLineMonitoringDTO);
                List<Float> hList = new ArrayList();
                for(int j = 0;j<QList.size();j++){
                    for (Object key : QList.get(j).keySet()) {
                        if(key.equals("createdate")){
                            // num++;
                            if(j==0){
                                Object hhh =  QList.get(0).get("createdate");
                                String hh = String.valueOf(hhh);
                                int shu = Integer.parseInt(hh.substring(8,10));
                                for(int k=1;k<shu;k++){
                                    seriesDataLMAX.add(Float.valueOf(0));
                                    seriesDataLMIN.add(Float.valueOf(0));

                                }
                            }
                        }else{
                            Map aaMap = new HashMap();
                            aaMap = QList.get(j);
                            Object hh = aaMap.get(key);
                            String ha = String.valueOf(hh);
                            int kj = ha.length();
                            if(hh==null || hh.equals("")){
                                hList.add((float) 0.0);
                            }else{
                                hList.add((Float) hh);
                            }


                        }

                    }
                }
                int count = 0;
                seriesDataLMAX.add((Float) hList.get(0));
                for(int d = 1;d<hList.size();d++){
                    count++;
                    if(count==1){
                        seriesDataLMIN.add((Float) hList.get(d));
                    }else if(count==2){
                        seriesDataLMAX.add((Float) hList.get(d));
                        count = 0;
                    }
                }

            }



            Map aMap = new HashMap();
            aMap.put("monitoringPointType",1);//监测点类型 1单相，3三相，4四项，11温度，12湿度 （小于10就是基本电参量，大于10的就是环境参量）
            aMap.put("seriesDate",seriesDate);
            aMap.put("seriesDataMIN",seriesDataMIN);
            aMap.put("seriesDataMAX",seriesDataMAX);
            aMap.put("seriesDataAVG",seriesDataAVG);
            aMap.put("seriesDataNINE",seriesDataNINE);

            aMap.put("seriesDataAMIN",seriesDataAMIN);
            aMap.put("seriesDataAMAX",seriesDataAMAX);
            aMap.put("seriesDataBMIN",seriesDataBMIN);
            aMap.put("seriesDataBMAX",seriesDataBMAX);
            aMap.put("seriesDataCMIN",seriesDataCMIN);
            aMap.put("seriesDataCMAX",seriesDataCMAX);

            aMap.put("seriesDataLMIN",seriesDataLMIN);
            aMap.put("seriesDataLMAX",seriesDataLMAX);


            if(aMap!=null&&aMap.size()>0){
                detailDTO.setDetail(aMap);
            }else{
                detailDTO.setDetail(new ArrayList<LinkedHashMap>());
            }
        }

        return detailDTO;
    }
    //电压极值曲线
    public DetailDTO selectElectricAnalysisByMonitoringPointDYJZ(OnLineMonitoringDTO onLineMonitoringDTO, LoginAppDTO appLoginUser) {
        DetailDTO detailDTO = new DetailDTO(true);
        List<LinkedHashMap> listCompany=ynBaseMapper.selectCompanyByOnLineMonitoringDTO(onLineMonitoringDTO);
        if(listCompany!=null&&listCompany.size()>0) {
            String ynBaseStaffCompanyId = (String) listCompany.get(0).get("ynBaseStaffCompanyId");//获取到维保公司id
            onLineMonitoringDTO.setTableNameString("jc_jy_" + ynBaseStaffCompanyId);
            //定义X轴
            List<Integer> seriesDate= new ArrayList<Integer>();
            String  h = onLineMonitoringDTO.getMonitoringPointDate();
            SimpleDateFormat formatter2  = new SimpleDateFormat("yyyyMMdd");
            try {
                Date  date = formatter2.parse(h);
                Calendar aCalendar = Calendar.getInstance(Locale.CHINA);
                aCalendar.setTime(date);
                int day=aCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                System.out.println("day = " + day);
                for(int i = 1;i<=day;i++){
                    seriesDate.add(i);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            List<Float> seriesDataMAX= new ArrayList<Float>();
            List<Float> seriesDataMIN= new ArrayList<Float>();
            List<Float> seriesDataAVG= new ArrayList<Float>();
            List<Float> seriesDataNINE= new ArrayList<Float>();
            List<Float> seriesDataHG= new ArrayList<Float>();
            List<Float> seriesDataCX= new ArrayList<Float>();


            List<Float> seriesDataZMAX= new ArrayList<Float>();
            List<Float> seriesDataZMIN= new ArrayList<Float>();
            List<Float> seriesDataZAVG= new ArrayList<Float>();
            List<Float> seriesDataZHG= new ArrayList<Float>();
            List<Float> seriesDataZCX= new ArrayList<Float>();

            List<Float> seriesDataAMAX= new ArrayList<Float>();
            List<Float> seriesDataAMIN= new ArrayList<Float>();
            List<Float> seriesDataBMAX= new ArrayList<Float>();
            List<Float> seriesDataBMIN= new ArrayList<Float>();
            List<Float> seriesDataCMAX= new ArrayList<Float>();
            List<Float> seriesDataCMIN= new ArrayList<Float>();
            List<Float> seriesDataAAVG= new ArrayList<Float>();
            List<Float> seriesDataAHG= new ArrayList<Float>();
            List<Float> seriesDataACX= new ArrayList<Float>();
            List<Float> seriesDataBAVG= new ArrayList<Float>();
            List<Float> seriesDataBHG= new ArrayList<Float>();
            List<Float> seriesDataBCX= new ArrayList<Float>();
            List<Float> seriesDataCAVG= new ArrayList<Float>();
            List<Float> seriesDataCHG= new ArrayList<Float>();
            List<Float> seriesDataCCX= new ArrayList<Float>();





            h = h.substring(0,6);
            onLineMonitoringDTO.setMonitoringPointDate(h);
            String hua = onLineMonitoringDTO.getTag();
            if(hua.equals("dybphd")|| hua=="dybphd"){
                onLineMonitoringDTO.setTag("DYBPH_HGL,DYBPH_CXL,DYBPH_MAX,DYBPH_MIN,DYBPH_AVG,DYBPH_NINEFIVE");
                List<LinkedHashMap> QList=ynBaseMapper.selectMDYQ(onLineMonitoringDTO);

                List<Float> hList = new ArrayList();
                for(int j = 0;j<QList.size();j++){
                    for (Object key : QList.get(j).keySet()) {
                        if(key.equals("createdate")){
                            // num++;
                            if(j==0){
                                Object hhh =  QList.get(0).get("createdate");
                                String hh = String.valueOf(hhh);
                                int shu = Integer.parseInt(hh.substring(8,10));
                                for(int k=1;k<shu;k++){
                                    seriesDataMAX.add(Float.valueOf(0));
                                    seriesDataMIN.add(Float.valueOf(0));
                                    seriesDataAVG.add(Float.valueOf(0));
                                    seriesDataNINE.add(Float.valueOf(0));
                                    seriesDataHG.add(Float.valueOf(0));
                                    seriesDataCX.add(Float.valueOf(0));
                                }
                            }
                        }else{
                            Map aaMap = new HashMap();
                            aaMap = QList.get(j);
                            Object hh = aaMap.get(key);
                            String ha = String.valueOf(hh);
                            int kj = ha.length();
                            if(hh==null || hh.equals("")){
                                hList.add((float) 0.0);
                            }else{
                                hList.add((Float) hh);
                            }


                        }

                    }
                }
                int count = 0;
                seriesDataHG.add((Float) hList.get(0));
                for(int d = 1;d<hList.size();d++){
                    count++;
                    if(count==1){
                        seriesDataCX.add((Float) hList.get(d));
                    }else if(count==2){
                        seriesDataMAX.add((Float) hList.get(d));
                    }else if(count==3){
                        seriesDataMIN.add((Float) hList.get(d));
                    }else if(count==4){
                        seriesDataAVG.add((Float) hList.get(d));
                    }else if(count==5){
                        seriesDataNINE.add((Float) hList.get(d));
                    }else if(count==6){
                        seriesDataHG.add((Float) hList.get(d));
                        count = 0;
                    }
                }
            }else if(hua.equals("dyhglq")|| hua=="dyhglq"){
                //电压合格率三相
            /*onLineMonitoringDTO.setTag("DYHGL_AHGL,DYHGL_ACXL,DYHGL_AMAX,DYHGL_AMIN,DYHGL_AAVG,DYHGL_BHGL,DYHGL_BCXL,DYHGL_BMAX,DYHGL_BMIN,DYHGL_BAVG,DYHGL_CHGL,DYHGL_CCXL,DYHGL_CMAX,DYHGL_CMIN,DYHGL_CAVG");
            List<LinkedHashMap> QList=ynBaseMapper.selectMDYQ(onLineMonitoringDTO);

            List<Float> hList = new ArrayList();
            for(int j = 0;j<QList.size();j++){
                for (Object key : QList.get(j).keySet()) {
                    if(key.equals("createdate")){
                        // num++;
                        if(j==0){
                            Object hhh =  QList.get(0).get("createdate");
                            String hh = String.valueOf(hhh);
                            int shu = Integer.parseInt(hh.substring(8,10));
                            for(int k=1;k<shu;k++){
                                seriesDataAMAX.add(Float.valueOf(0));
                                seriesDataAMIN.add(Float.valueOf(0));
                                seriesDataBMAX.add(Float.valueOf(0));
                                seriesDataBMIN.add(Float.valueOf(0));
                                seriesDataCMAX.add(Float.valueOf(0));
                                seriesDataCMIN.add(Float.valueOf(0));
                                seriesDataAAVG.add(Float.valueOf(0));
                                seriesDataACX .add(Float.valueOf(0));
                                seriesDataAHG.add(Float.valueOf(0));
                                seriesDataBAVG.add(Float.valueOf(0));
                                seriesDataBCX .add(Float.valueOf(0));
                                seriesDataBHG.add(Float.valueOf(0));
                                seriesDataCAVG.add(Float.valueOf(0));
                                seriesDataCCX .add(Float.valueOf(0));
                                seriesDataCHG.add(Float.valueOf(0));
                            }
                        }
                    }else{
                        Map aaMap = new HashMap();
                        aaMap = QList.get(j);
                        Object hh = aaMap.get(key);
                        String ha = String.valueOf(hh);
                        int kj = ha.length();
                        if(hh==null || hh.equals("")){
                            hList.add((float) 0.0);
                        }else{
                            hList.add((Float) hh);
                        }


                    }

                }
            }
            int count = 0;
            seriesDataAHG.add((Float) hList.get(0));
            for(int d = 1;d<hList.size();d++){
                count++;
                if(count==1){
                    seriesDataACX.add((Float) hList.get(d));
                }else if(count==2){
                    seriesDataAMAX.add((Float) hList.get(d));
                }else if(count==3){
                    seriesDataAMIN.add((Float) hList.get(d));
                }else if(count==4){
                    seriesDataAAVG.add((Float) hList.get(d));
                }else if(count==5){
                    seriesDataBHG.add((Float) hList.get(d));
                }else if(count==6){
                    seriesDataBCX.add((Float) hList.get(d));
                }else if(count==7){
                    seriesDataBMAX.add((Float) hList.get(d));
                }else if(count==8){
                    seriesDataBMIN.add((Float) hList.get(d));
                }else if(count==9){
                    seriesDataBAVG.add((Float) hList.get(d));
                }else if(count==10){
                    seriesDataCHG.add((Float) hList.get(d));
                }else if(count==11){
                    seriesDataCCX.add((Float) hList.get(d));
                }else if(count==12){
                    seriesDataCMAX.add((Float) hList.get(d));
                }else if(count==13){
                    seriesDataCMIN.add((Float) hList.get(d));
                }else if(count==14){
                    seriesDataCAVG.add((Float) hList.get(d));
                }else if(count==15){
                    seriesDataAHG.add((Float) hList.get(d));
                    count = 0;
                }
            }*/
                onLineMonitoringDTO.setTag("DYHGL_HGL,DYHGL_CXL,DYHGL_MAX,DYHGL_MIN,DYHGL_AVG");
                List<LinkedHashMap> QList=ynBaseMapper.selectMDYQ(onLineMonitoringDTO);

                List<Float> hList = new ArrayList();
                for(int j = 0;j<QList.size();j++){
                    for (Object key : QList.get(j).keySet()) {
                        if(key.equals("createdate")){
                            // num++;
                            if(j==0){
                                Object hhh =  QList.get(0).get("createdate");
                                String hh = String.valueOf(hhh);
                                int shu = Integer.parseInt(hh.substring(8,10));
                                for(int k=1;k<shu;k++){
                                    seriesDataZMAX.add(Float.valueOf(0));
                                    seriesDataZMIN.add(Float.valueOf(0));
                                    seriesDataZAVG.add(Float.valueOf(0));
                                    seriesDataZCX .add(Float.valueOf(0));
                                    seriesDataZHG.add(Float.valueOf(0));
                                }
                            }
                        }else{
                            Map aaMap = new HashMap();
                            aaMap = QList.get(j);
                            Object hh = aaMap.get(key);
                            String ha = String.valueOf(hh);
                            int kj = ha.length();
                            if(hh==null || hh.equals("")){
                                hList.add((float) 0.0);
                            }else{
                                hList.add((Float) hh);
                            }


                        }

                    }
                }
                int count = 0;
                seriesDataZHG.add((Float) hList.get(0));
                for(int d = 1;d<hList.size();d++){
                    count++;
                    if(count==1){
                        seriesDataZCX.add((Float) hList.get(d));
                    }else if(count==2){
                        seriesDataZMAX.add((Float) hList.get(d));
                    }else if(count==3){
                        seriesDataZMIN.add((Float) hList.get(d));
                    }else if(count==4){
                        seriesDataZAVG.add((Float) hList.get(d));
                    }else if(count==5){
                        seriesDataZHG.add((Float) hList.get(d));
                        count = 0;
                    }
                }
            }





            Map aMap = new HashMap();
            aMap.put("monitoringPointType",1);//监测点类型 1单相，3三相，4四项，11温度，12湿度 （小于10就是基本电参量，大于10的就是环境参量）
            aMap.put("seriesDate",seriesDate);
            aMap.put("seriesDataMIN",seriesDataMIN);
            aMap.put("seriesDataMAX",seriesDataMAX);
            aMap.put("seriesDataAVG",seriesDataAVG);
            aMap.put("seriesDataNINE",seriesDataNINE);
            aMap.put("seriesDataHG",seriesDataHG);
            aMap.put("seriesDataCX",seriesDataCX);

            aMap.put("seriesDataZMIN",seriesDataZMIN);
            aMap.put("seriesDataZMAX",seriesDataZMAX);
            aMap.put("seriesDataZAVG",seriesDataZAVG);
            aMap.put("seriesDataZHG",seriesDataZHG);
            aMap.put("seriesDataZCX",seriesDataZCX);

        /*aMap.put("seriesDataAMIN",seriesDataAMIN);
        aMap.put("seriesDataAMAX",seriesDataAMAX);
        aMap.put("seriesDataBMIN",seriesDataBMIN);
        aMap.put("seriesDataBMAX",seriesDataBMAX);
        aMap.put("seriesDataCMIN",seriesDataCMIN);
        aMap.put("seriesDataCMAX",seriesDataCMAX);
        aMap.put("seriesDataACX",seriesDataACX);
        aMap.put("seriesDataAHG",seriesDataAHG);
        aMap.put("seriesDataAAVG",seriesDataAAVG);
        aMap.put("seriesDataBCX",seriesDataBCX);
        aMap.put("seriesDataBHG",seriesDataBHG);
        aMap.put("seriesDataBAVG",seriesDataBAVG);
        aMap.put("seriesDataCCX",seriesDataCCX);
        aMap.put("seriesDataCHG",seriesDataCHG);
        aMap.put("seriesDataCAVG",seriesDataCAVG);*/

            if(aMap!=null&&aMap.size()>0){
                detailDTO.setDetail(aMap);
            }else{
                detailDTO.setDetail(new ArrayList<LinkedHashMap>());
            }
        }

        return detailDTO;
    }

    //谐波曲线
    public DetailDTO selectElectricAnalysisByMonitoringPointAXB(OnLineMonitoringDTO onLineMonitoringDTO, LoginAppDTO appLoginUser) {
        DetailDTO detailDTO = new DetailDTO(true);
        List<LinkedHashMap> listCompany=ynBaseMapper.selectCompanyByOnLineMonitoringDTO(onLineMonitoringDTO);
        if(listCompany!=null&&listCompany.size()>0) {
            String ynBaseStaffCompanyId = (String) listCompany.get(0).get("ynBaseStaffCompanyId");//获取到维保公司id
            onLineMonitoringDTO.setTableNameString("jc_xr_" + ynBaseStaffCompanyId);
            //定义X轴
            List<Integer> seriesDate= new ArrayList<Integer>();
            String  h = onLineMonitoringDTO.getMonitoringPointDate();
            SimpleDateFormat formatter2  = new SimpleDateFormat("yyyyMMdd");
            try {
                Date  date = formatter2.parse(h);
                Calendar aCalendar = Calendar.getInstance(Locale.CHINA);
                aCalendar.setTime(date);
                int day=aCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                System.out.println("day = " + day);
                for(int i = 1;i<=day;i++){
                    seriesDate.add(i);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

            List<Float> seriesDataMAX= new ArrayList<Float>();
            List<Float> seriesDataMIN= new ArrayList<Float>();
            List<Float> seriesDataAVG= new ArrayList<Float>();
            List<Float> seriesDataNINE= new ArrayList<Float>();

            List<Float> seriesDataALMAX= new ArrayList<Float>();
            List<Float> seriesDataALMIN= new ArrayList<Float>();
            List<Float> seriesDataALAVG= new ArrayList<Float>();
            List<Float> seriesDataALNINE= new ArrayList<Float>();

            h = h.substring(0,6);
            onLineMonitoringDTO.setMonitoringPointDate(h);
            String hua = onLineMonitoringDTO.getTag();
            if(hua.equals("axbdyqx")|| hua=="axbdyqx"){
                onLineMonitoringDTO.setTag("dyzxb_amax,dyzxb_amin,dyzxb_aavg,dyzxb_aninefive");
                List<LinkedHashMap> QList=ynBaseMapper.selectMAXBQ(onLineMonitoringDTO);

                List<Float> hList = new ArrayList();
                for(int j = 0;j<QList.size();j++){
                    for (Object key : QList.get(j).keySet()) {
                        if(key.equals("createdate")){
                            // num++;
                            if(j==0){
                                Object hhh =  QList.get(0).get("createdate");
                                String hh = String.valueOf(hhh);
                                int shu = Integer.parseInt(hh.substring(8,10));
                                for(int k=1;k<shu;k++){
                                    seriesDataMAX.add(Float.valueOf(0));
                                    seriesDataMIN.add(Float.valueOf(0));
                                    seriesDataAVG.add(Float.valueOf(0));
                                    seriesDataNINE.add(Float.valueOf(0));
                                }
                            }
                        }else{
                            Map aaMap = new HashMap();
                            aaMap = QList.get(j);
                            Object hh = aaMap.get(key);
                            String ha = String.valueOf(hh);
                            int kj = ha.length();
                            if(hh==null || hh.equals("")){
                                hList.add((float) 0.0);
                            }else{
                                hList.add((Float) hh);
                            }


                        }

                    }
                }
                int count = 0;
                seriesDataMAX.add((Float) hList.get(0));
                for(int d = 1;d<hList.size();d++){
                    count++;
                    if(count==1){
                        seriesDataMIN.add((Float) hList.get(d));
                    }else if(count==2){
                        seriesDataAVG.add((Float) hList.get(d));
                    }else if(count==3){
                        seriesDataNINE.add((Float) hList.get(d));
                    }else if(count==4){
                        seriesDataMAX.add((Float) hList.get(d));
                        count = 0;
                    }
                }
            }else if(hua.equals("axbdlqx")|| hua=="axbdlqx"){
                onLineMonitoringDTO.setTag("dlzxb_amax,dlzxb_amin,dlzxb_aavg,dlzxb_aninefive");
                List<LinkedHashMap> QList=ynBaseMapper.selectMAXBQ(onLineMonitoringDTO);

                List<Float> hList = new ArrayList();
                for(int j = 0;j<QList.size();j++){
                    for (Object key : QList.get(j).keySet()) {
                        if(key.equals("createdate")){
                            // num++;
                            if(j==0){
                                Object hhh =  QList.get(0).get("createdate");
                                String hh = String.valueOf(hhh);
                                int shu = Integer.parseInt(hh.substring(8,10));
                                for(int k=1;k<shu;k++){
                                    seriesDataALMAX.add(Float.valueOf(0));
                                    seriesDataALMIN.add(Float.valueOf(0));
                                    seriesDataALAVG.add(Float.valueOf(0));
                                    seriesDataALNINE.add(Float.valueOf(0));
                                }
                            }
                        }else{
                            Map aaMap = new HashMap();
                            aaMap = QList.get(j);
                            Object hh = aaMap.get(key);
                            String ha = String.valueOf(hh);
                            int kj = ha.length();
                            if(hh==null || hh.equals("")){
                                hList.add((float) 0.0);
                            }else{
                                hList.add((Float) hh);
                            }


                        }

                    }
                }
                int count = 0;
                seriesDataALMAX.add((Float) hList.get(0));
                for(int d = 1;d<hList.size();d++) {
                    count++;
                    if (count == 1) {
                        seriesDataALMIN.add((Float) hList.get(d));
                    } else if (count == 2) {
                        seriesDataALAVG.add((Float) hList.get(d));
                    } else if (count == 3) {
                        seriesDataALNINE.add((Float) hList.get(d));
                    } else if (count == 4) {
                        seriesDataALMAX.add((Float) hList.get(d));
                        count = 0;
                    }
                }
            }else if(hua.equals("bxbdyqx")|| hua=="bxbdyqx"){
                onLineMonitoringDTO.setTag("dyzxb_bmax,dyzxb_bmin,dyzxb_bavg,dyzxb_bninefive");
                List<LinkedHashMap> QList=ynBaseMapper.selectMAXBQ(onLineMonitoringDTO);

                List<Float> hList = new ArrayList();
                for(int j = 0;j<QList.size();j++){
                    for (Object key : QList.get(j).keySet()) {
                        if(key.equals("createdate")){
                            // num++;
                            if(j==0){
                                Object hhh =  QList.get(0).get("createdate");
                                String hh = String.valueOf(hhh);
                                int shu = Integer.parseInt(hh.substring(8,10));
                                for(int k=1;k<shu;k++){
                                    seriesDataMAX.add(Float.valueOf(0));
                                    seriesDataMIN.add(Float.valueOf(0));
                                    seriesDataAVG.add(Float.valueOf(0));
                                    seriesDataNINE.add(Float.valueOf(0));
                                }
                            }
                        }else{
                            Map aaMap = new HashMap();
                            aaMap = QList.get(j);
                            Object hh = aaMap.get(key);
                            String ha = String.valueOf(hh);
                            int kj = ha.length();
                            if(hh==null || hh.equals("")){
                                hList.add((float) 0.0);
                            }else{
                                hList.add((Float) hh);
                            }


                        }

                    }
                }
                int count = 0;
                seriesDataMAX.add((Float) hList.get(0));
                for(int d = 1;d<hList.size();d++){
                    count++;
                    if(count==1){
                        seriesDataMIN.add((Float) hList.get(d));
                    }else if(count==2){
                        seriesDataAVG.add((Float) hList.get(d));
                    }else if(count==3){
                        seriesDataNINE.add((Float) hList.get(d));
                    }else if(count==4){
                        seriesDataMAX.add((Float) hList.get(d));
                        count = 0;
                    }
                }
            }else if(hua.equals("bxbdlqx")|| hua=="bxbdlqx"){
                onLineMonitoringDTO.setTag("dlzxb_bmax,dlzxb_bmin,dlzxb_bavg,dlzxb_bninefive");
                List<LinkedHashMap> QList=ynBaseMapper.selectMAXBQ(onLineMonitoringDTO);

                List<Float> hList = new ArrayList();
                for(int j = 0;j<QList.size();j++){
                    for (Object key : QList.get(j).keySet()) {
                        if(key.equals("createdate")){
                            // num++;
                            if(j==0){
                                Object hhh =  QList.get(0).get("createdate");
                                String hh = String.valueOf(hhh);
                                int shu = Integer.parseInt(hh.substring(8,10));
                                for(int k=1;k<shu;k++){
                                    seriesDataALMAX.add(Float.valueOf(0));
                                    seriesDataALMIN.add(Float.valueOf(0));
                                    seriesDataALAVG.add(Float.valueOf(0));
                                    seriesDataALNINE.add(Float.valueOf(0));
                                }
                            }
                        }else{
                            Map aaMap = new HashMap();
                            aaMap = QList.get(j);
                            Object hh = aaMap.get(key);
                            String ha = String.valueOf(hh);
                            int kj = ha.length();
                            if(hh==null || hh.equals("")){
                                hList.add((float) 0.0);
                            }else{
                                hList.add((Float) hh);
                            }


                        }

                    }
                }
                int count = 0;
                seriesDataALMAX.add((Float) hList.get(0));
                for(int d = 1;d<hList.size();d++) {
                    count++;
                    if (count == 1) {
                        seriesDataALMIN.add((Float) hList.get(d));
                    } else if (count == 2) {
                        seriesDataALAVG.add((Float) hList.get(d));
                    } else if (count == 3) {
                        seriesDataALNINE.add((Float) hList.get(d));
                    } else if (count == 4) {
                        seriesDataALMAX.add((Float) hList.get(d));
                        count = 0;
                    }
                }
            }else if(hua.equals("cxbdyqx")|| hua=="cxbdyqx"){
                onLineMonitoringDTO.setTag("dyzxb_cmax,dyzxb_cmin,dyzxb_cavg,dyzxb_cninefive");
                List<LinkedHashMap> QList=ynBaseMapper.selectMAXBQ(onLineMonitoringDTO);

                List<Float> hList = new ArrayList();
                for(int j = 0;j<QList.size();j++){
                    for (Object key : QList.get(j).keySet()) {
                        if(key.equals("createdate")){
                            // num++;
                            if(j==0){
                                Object hhh =  QList.get(0).get("createdate");
                                String hh = String.valueOf(hhh);
                                int shu = Integer.parseInt(hh.substring(8,10));
                                for(int k=1;k<shu;k++){
                                    seriesDataMAX.add(Float.valueOf(0));
                                    seriesDataMIN.add(Float.valueOf(0));
                                    seriesDataAVG.add(Float.valueOf(0));
                                    seriesDataNINE.add(Float.valueOf(0));
                                }
                            }
                        }else{
                            Map aaMap = new HashMap();
                            aaMap = QList.get(j);
                            Object hh = aaMap.get(key);
                            String ha = String.valueOf(hh);
                            int kj = ha.length();
                            if(hh==null || hh.equals("")){
                                hList.add((float) 0.0);
                            }else{
                                hList.add((Float) hh);
                            }


                        }

                    }
                }
                int count = 0;
                seriesDataMAX.add((Float) hList.get(0));
                for(int d = 1;d<hList.size();d++){
                    count++;
                    if(count==1){
                        seriesDataMIN.add((Float) hList.get(d));
                    }else if(count==2){
                        seriesDataAVG.add((Float) hList.get(d));
                    }else if(count==3){
                        seriesDataNINE.add((Float) hList.get(d));
                    }else if(count==4){
                        seriesDataMAX.add((Float) hList.get(d));
                        count = 0;
                    }
                }
            }else if(hua.equals("cxbdlqx")|| hua=="cxbdlqx"){
                onLineMonitoringDTO.setTag("dlzxb_cmax,dlzxb_cmin,dlzxb_cavg,dlzxb_cninefive");
                List<LinkedHashMap> QList=ynBaseMapper.selectMAXBQ(onLineMonitoringDTO);

                List<Float> hList = new ArrayList();
                for(int j = 0;j<QList.size();j++){
                    for (Object key : QList.get(j).keySet()) {
                        if(key.equals("createdate")){
                            // num++;
                            if(j==0){
                                Object hhh =  QList.get(0).get("createdate");
                                String hh = String.valueOf(hhh);
                                int shu = Integer.parseInt(hh.substring(8,10));
                                for(int k=1;k<shu;k++){
                                    seriesDataALMAX.add(Float.valueOf(0));
                                    seriesDataALMIN.add(Float.valueOf(0));
                                    seriesDataALAVG.add(Float.valueOf(0));
                                    seriesDataALNINE.add(Float.valueOf(0));
                                }
                            }
                        }else{
                            Map aaMap = new HashMap();
                            aaMap = QList.get(j);
                            Object hh = aaMap.get(key);
                            String ha = String.valueOf(hh);
                            int kj = ha.length();
                            if(hh==null || hh.equals("")){
                                hList.add((float) 0.0);
                            }else{
                                hList.add((Float) hh);
                            }


                        }

                    }
                }
                int count = 0;
                seriesDataALMAX.add((Float) hList.get(0));
                for(int d = 1;d<hList.size();d++) {
                    count++;
                    if (count == 1) {
                        seriesDataALMIN.add((Float) hList.get(d));
                    } else if (count == 2) {
                        seriesDataALAVG.add((Float) hList.get(d));
                    } else if (count == 3) {
                        seriesDataALNINE.add((Float) hList.get(d));
                    } else if (count == 4) {
                        seriesDataALMAX.add((Float) hList.get(d));
                        count = 0;
                    }
                }
            }




            Map aMap = new HashMap();
            aMap.put("monitoringPointType",1);//监测点类型 1单相，3三相，4四项，11温度，12湿度 （小于10就是基本电参量，大于10的就是环境参量）
            aMap.put("seriesDate",seriesDate);
            aMap.put("seriesDataMIN",seriesDataMIN);
            aMap.put("seriesDataMAX",seriesDataMAX);
            aMap.put("seriesDataAVG",seriesDataAVG);
            aMap.put("seriesDataNINE",seriesDataNINE);

            aMap.put("seriesDataALMIN",seriesDataALMIN);
            aMap.put("seriesDataALMAX",seriesDataALMAX);
            aMap.put("seriesDataALAVG",seriesDataALAVG);
            aMap.put("seriesDataALNINE",seriesDataALNINE);




            if(aMap!=null&&aMap.size()>0){
                detailDTO.setDetail(aMap);
            }else{
                detailDTO.setDetail(new ArrayList<LinkedHashMap>());
            }
        }

        return detailDTO;
    }

    //电压断相曲线
    public DetailDTO selectElectricAnalysisByMonitoringPointDX(OnLineMonitoringDTO onLineMonitoringDTO, LoginAppDTO appLoginUser) {
        DetailDTO detailDTO = new DetailDTO(true);
        List<LinkedHashMap> listCompany=ynBaseMapper.selectCompanyByOnLineMonitoringDTO(onLineMonitoringDTO);
        if(listCompany!=null&&listCompany.size()>0) {
            String ynBaseStaffCompanyId = (String) listCompany.get(0).get("ynBaseStaffCompanyId");//获取到维保公司id
            onLineMonitoringDTO.setTableNameString("jc_dx_" + ynBaseStaffCompanyId);
            //定义X轴
            List<Integer> seriesDate= new ArrayList<Integer>();
            String  h = onLineMonitoringDTO.getMonitoringPointDate();
            SimpleDateFormat formatter2  = new SimpleDateFormat("yyyyMMdd");
            try {
                Date  date = formatter2.parse(h);
                Calendar aCalendar = Calendar.getInstance(Locale.CHINA);
                aCalendar.setTime(date);
                int day=aCalendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                System.out.println("day = " + day);
                for(int i = 1;i<=day;i++){
                    seriesDate.add(i);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            List<Float> seriesDataACOUNT= new ArrayList<Float>();
            List<Float> seriesDataADATE= new ArrayList<Float>();
            List<Float> seriesDataBCOUNT= new ArrayList<Float>();
            List<Float> seriesDataBDATE= new ArrayList<Float>();
            List<Float> seriesDataCCOUNT= new ArrayList<Float>();
            List<Float> seriesDataCDATE= new ArrayList<Float>();


            h = h.substring(0,6);
            onLineMonitoringDTO.setMonitoringPointDate(h);
            String hua = onLineMonitoringDTO.getTag();
            if(hua.equals("dxq")|| hua=="dxq"){
                onLineMonitoringDTO.setTag("dx_acount,dx_bcount,dx_ccount,dx_adate,dx_bdate,dx_cdate");
                List<LinkedHashMap> QList=ynBaseMapper.selectMDXQ(onLineMonitoringDTO);

                List<Float> hList = new ArrayList();
                for(int j = 0;j<QList.size();j++){
                    for (Object key : QList.get(j).keySet()) {
                        if(key.equals("createdate")){
                            // num++;
                            if(j==0){
                                Object hhh =  QList.get(0).get("createdate");
                                String hh = String.valueOf(hhh);
                                int shu = Integer.parseInt(hh.substring(8,10));
                                for(int k=1;k<shu;k++){
                                    seriesDataACOUNT.add(Float.valueOf(0));
                                    seriesDataADATE.add(Float.valueOf(0));
                                    seriesDataBCOUNT.add(Float.valueOf(0));
                                    seriesDataBDATE.add(Float.valueOf(0));
                                    seriesDataCCOUNT.add(Float.valueOf(0));
                                    seriesDataCDATE.add(Float.valueOf(0));
                                }
                            }
                        }else{
                            Map aaMap = new HashMap();
                            aaMap = QList.get(j);
                            Object hh = aaMap.get(key);
                            String ha = String.valueOf(hh);
                            int kj = ha.length();
                            if(hh==null || hh.equals("")){
                                hList.add((float) 0.0);
                            }else{
                                hList.add((Float) hh);
                            }


                        }

                    }
                }
                int count = 0;
                seriesDataACOUNT.add((Float) hList.get(0));
                for(int d = 1;d<hList.size();d++){
                    count++;
                    if(count==1){
                        seriesDataBCOUNT.add((Float) hList.get(d));
                    }else if(count==2){
                        seriesDataCCOUNT.add((Float) hList.get(d));
                    }else if(count==3){
                        seriesDataADATE.add((Float) hList.get(d));
                    }else if(count==4){
                        seriesDataBDATE.add((Float) hList.get(d));
                    }else if(count==5){
                        seriesDataCDATE.add((Float) hList.get(d));
                    }else if(count==6){
                        seriesDataACOUNT.add((Float) hList.get(d));
                        count = 0;
                    }
                }
            }







            Map aMap = new HashMap();
            aMap.put("monitoringPointType",1);//监测点类型 1单相，3三相，4四项，11温度，12湿度 （小于10就是基本电参量，大于10的就是环境参量）
            aMap.put("seriesDate",seriesDate);

            aMap.put("seriesDataACOUNT",seriesDataACOUNT);
            aMap.put("seriesDataADATE",seriesDataADATE);
            aMap.put("seriesDataBCOUNT",seriesDataBCOUNT);
            aMap.put("seriesDataBDATE",seriesDataBDATE);
            aMap.put("seriesDataCCOUNT",seriesDataCCOUNT);
            aMap.put("seriesDataCDATE",seriesDataCDATE);


            if(aMap!=null&&aMap.size()>0){
                detailDTO.setDetail(aMap);
            }else{
                detailDTO.setDetail(new ArrayList<LinkedHashMap>());
            }
        }

        return detailDTO;
    }





    //查询存在计量点的电房信息(服务于小李pc在线监测首页)
    public DetailDTO selectEnterpriseSubstationHaveMonitoringPoint(MonitoringObjectDTO monitoringObjectDTO, LoginAppDTO appLoginUser,ArrayList serviceAreas) {
        DetailDTO detailDTO = new DetailDTO(true);
        monitoringObjectDTO.setTableNameString("jc_gj_83650");
        String companyId =  monitoringObjectDTO.getYnBaseStaffCompanyId();

        List<LinkedHashMap> mapList = new ArrayList<LinkedHashMap>();
        String[] strs = new String[]{};
        List<LinkedHashMap> companyList = new ArrayList<LinkedHashMap>();

        monitoringObjectDTO.setServicesList(serviceAreas);
        if (Integer.parseInt(companyId) == 83650){
            mapList=ynBaseMapper.selectAllEnterpriseSubstationHaveMonitoringPoint(monitoringObjectDTO);
        }else if (Integer.parseInt(companyId) == 83800){
            mapList=ynBaseMapper.selectHNEnterpriseSubstationHaveMonitoringPoint(monitoringObjectDTO);
        }else if (Integer.parseInt(companyId) == 84000){
            mapList=ynBaseMapper.selectMZEnterpriseSubstationHaveMonitoringPoint(monitoringObjectDTO);
        }else {
            mapList=ynBaseMapper.selectEnterpriseSubstationHaveMonitoringPoint(monitoringObjectDTO);
        }






       /* Map returnMap = new HashMap();
        if(mapList!=null &&mapList.size()>0){
            returnMap.put("resultList", mapList);
        }else{q
            returnMap.put("resultList", new ArrayList<Map>());
        }
        detailDTO.setDetail(returnMap);*/
        if(mapList!=null&&mapList.size()>0){
            detailDTO.setDetail(mapList);
        }else{
            detailDTO.setDetail(new ArrayList<Map>());
        }
        return detailDTO;

    }

    public DetailDTO selectEnterpriseInfoByCompanyIdAndEnterpriseName(MonitoringObjectDTO monitoringObjectDTO, LoginAppDTO appLoginUser) {
        DetailDTO detailDTO = new DetailDTO(true);

        String companyid = monitoringObjectDTO.getYnBaseStaffCompanyId();
        String services = monitoringObjectDTO.getServiceAreas();
        String[] servicesStr = services.split(",");
        ArrayList a1 = new ArrayList();
        for (int i=0;i<servicesStr.length;i++){
            a1.add(servicesStr[i]);
        }

        monitoringObjectDTO.setServicesList(a1);
        List<LinkedHashMap> mapList=new ArrayList<LinkedHashMap>();
        if (Integer.parseInt(monitoringObjectDTO.getUserTypes())==3) {
            if (Integer.parseInt(companyid) == 83650) {
                mapList = ynBaseMapper.selectAllEnterpriseInfoByCompanyIdAndEnterpriseName(monitoringObjectDTO);
            } else if (Integer.parseInt(companyid) == 83800) {
                mapList = ynBaseMapper.selectHNEnterpriseInfoByCompanyIdAndEnterpriseName(monitoringObjectDTO);
            } else {
                mapList = ynBaseMapper.selectEnterpriseInfoByCompanyIdAndEnterpriseName(monitoringObjectDTO);
            }
        }else if (Integer.parseInt(monitoringObjectDTO.getUserTypes())==2){
            mapList = ynBaseMapper.selectCustomerEnterpriseInfoByCompanyIdAndEnterpriseName(monitoringObjectDTO);
        }
       /* Map returnMap = new HashMap();
        if(mapList!=null &&mapList.size()>0){
            returnMap.put("resultList", mapList);
        }else{
            returnMap.put("resultList", new ArrayList<Map>());
        }
        detailDTO.setDetail(returnMap);*/
        if(mapList!=null&&mapList.size()>0){
            detailDTO.setDetail(mapList);
        }else{
            detailDTO.setDetail(new ArrayList<Map>());
        }
        return detailDTO;
    }
    /**根据年统计用电企业的总用电量
     todayTotalSUM           今日总电量
     yestodayTotalSUM        昨日总电量
     dayGrowthRateSUM        日环比昨日（数值）
     dayGrowthRateStateSUM   今日环比昨日状态：1表示环比为整数，0表示环比为相等，-1表示环比为负数
     currentMonthTotalSUM    当前月总电量
     LastMonthTotalSUM       上月总电量
     monthGrowthRateSUM      当前月环比上月（数值）
     monthGrowthRateStateSUM 当前月环比上月状态：1表示环比为正数，0表示环比为相等，-1表示环比为负数
     */

    //查询告警信息数据
   public DetailDTO selectIntelligentAlarmService(OnLineMonitoringDTO onLineMonitoringDTO){
       DetailDTO detailDTO = new DetailDTO();
       Map map = new HashMap();
       map = ynBaseMapper.selectalarmNumber();
       Object s1 = map.get("alarmNumber");
       detailDTO.setErrorCode(String.valueOf(s1));
       List<LinkedHashMap> list = null;
       if(onLineMonitoringDTO.getYnBaseCustomerEnterpriseId()!=null){
           list = ynBaseMapper.selectIntelligentAlarmMapper(onLineMonitoringDTO);
           detailDTO.setDetail(list);
           return detailDTO;
       }
       if(onLineMonitoringDTO.getYnBaseCustomerEnterpriseId()==null){
           list = ynBaseMapper.selectIntelligentAlarmMapperTwo(onLineMonitoringDTO);
           detailDTO.setDetail(list);
           return detailDTO;
       }

       return detailDTO;
    }

    //查询终端状态个数
    public Map selectTerminalService(OnLineMonitoringDTO onLineMonitoringDTO){
       Map map =new HashMap();
        DetailDTO detailDTO =new DetailDTO(true);
        List<LinkedHashMap> listOne = null;
        List<LinkedHashMap> listTwo = null;
        listOne = ynBaseMapper.selectTerminAlbnormalMapper(onLineMonitoringDTO);
        int abnormal =listOne.size();
        listTwo = ynBaseMapper.selectTerminNormalMapper(onLineMonitoringDTO);
        int normal = listTwo.size();
        map.put("abnormal",abnormal);
        map.put("normal",normal);

       return  map;
    }


    //查询终端信息
    public List<LinkedHashMap> selectTerminalDataService(OnLineMonitoringDTO onLineMonitoringDTO){
        List<LinkedHashMap> listOne = null;
        List<LinkedHashMap> listTwo = null;
        listOne  = ynBaseMapper.selectTerminNormalMapper(onLineMonitoringDTO);
        listTwo = ynBaseMapper.selectTerminAlbnormalMapper(onLineMonitoringDTO);
        if(listTwo.size()>0){
            LinkedHashMap linkmap = new LinkedHashMap();
            for(int i=0;i<listTwo.size();i++){
                String DF_Name = (String)listTwo.get(i).get("DF_Name");
                linkmap.put("DF_Name",DF_Name);
                String JCD_Name = (String)listTwo.get(i).get("JCD_Name");
                linkmap.put("JCD_Name",JCD_Name);
                linkmap.put("mStatus",listTwo.get(i).get("mStatus"));
                listOne.add(linkmap);
            }
        }

        return  listOne;
    }

    //根据省市区查询终端信息
    public List<LinkedHashMap> selectTerminalDataCityService(OnLineMonitoringDTO onLineMonitoringDTO){
        DetailDTO detailDTO =new DetailDTO(true);
        List<LinkedHashMap> listOne = null;
        List<LinkedHashMap> listTwo = null;
        listTwo = ynBaseMapper.selectTerminalCityNumberMapperTwo(onLineMonitoringDTO);
        listOne = ynBaseMapper.selectTerminalCityNumberMapperOne(onLineMonitoringDTO);
        if(listOne.size()>0){
            LinkedHashMap linkmap = new LinkedHashMap();
            for(int i=0;i<listTwo.size();i++){
                String DF_Name = (String)listTwo.get(i).get("DF_Name");
                linkmap.put("DF_Name",DF_Name);
                String JCD_Name = (String)listTwo.get(i).get("JCD_Name");
                linkmap.put("JCD_Name",JCD_Name);
                linkmap.put("mStatus",listTwo.get(i).get("mStatus"));
                listOne.add(linkmap);
            }
        }
        return listOne;
    }

    //根据省市区查询终端状态个数  异常 正常
    public Map selectTerminalCityNumberService(OnLineMonitoringDTO onLineMonitoringDTO){
        Map map =new HashMap();
        DetailDTO detailDTO =new DetailDTO(true);
        List<LinkedHashMap> listOne = null;
        List<LinkedHashMap> listTwo = null;
        listOne = ynBaseMapper.selectTerminalCityNumberMapperOne(onLineMonitoringDTO);
        int normal =listOne.size();
        listTwo = ynBaseMapper.selectTerminalCityNumberMapperTwo(onLineMonitoringDTO);
        int abnormal = listTwo.size();
        map.put("abnormal",abnormal);
        map.put("normal",normal);


        return map;
    }

    public DetailDTO selectDFState(OnLineMonitoringDTO onLineMonitoringDTO) {
        DetailDTO detailDTO =new DetailDTO(true);

        List<LinkedHashMap> list = ynBaseMapper.selectDFState(onLineMonitoringDTO);
        detailDTO.setDetail(list);


        return detailDTO;
    }

}
