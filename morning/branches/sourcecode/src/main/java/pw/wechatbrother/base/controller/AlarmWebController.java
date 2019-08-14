package pw.wechatbrother.base.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import pw.wechatbrother.base.dto.DetailDTO;
import pw.wechatbrother.base.dto.onlinemonitoring.OnLineMonitoringDTO;
import pw.wechatbrother.base.service.AlarmService;
import pw.wechatbrother.base.utils.exception.YnTransactionalException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/AlarmWeb")
public class AlarmWebController extends BaseController{
    @Autowired
    private AlarmService alarmService;

    //查找用电企业
    @RequestMapping(value = "/findCompany",method = RequestMethod.GET)
    public List<Map> findCompany(String companyId){
        return alarmService.getcompany(Integer.parseInt(companyId));
    }

    //查找电房
    @RequestMapping(value = "/findSubstation",method = RequestMethod.GET)
    public List<Map> findSubstation(String companyId){
        return alarmService.getSubstation(Integer.parseInt(companyId));
    }

    //查找监测点
    @RequestMapping(value = "/findmonitoringInfo",method = RequestMethod.GET)
    public List<Map> findmonitoringInfo(String substationId){
        int test = Integer.parseInt(substationId);
        return alarmService.getmonitoringInfo(Integer.parseInt(substationId));
    }



    //查找监测点告警信息
    @RequestMapping(value = "/findjcdmessage",method = RequestMethod.GET)
    public List<Map> findmessage(String aliasId ){

        return alarmService.getmessgae(aliasId);
    }

    //告警写入

    @RequestMapping(value = "/alarmsetting",method = RequestMethod.POST)
    public DetailDTO alarmsetting(HttpServletRequest request, HttpServletResponse response, @RequestBody String data){
        DetailDTO returnDetailDTO= new DetailDTO(true);
       System.out.print(data);
        String re = alarmService.alarmsetting(data);
        if (re == "success"){
            returnDetailDTO.setDetail("success");
        }else {
            returnDetailDTO.setDetail("false");
        }
//        List list1 = new ArrayList();
//        Map m = new HashMap();
//        m.put("re",re);
        return returnDetailDTO;

    }

    @RequestMapping(value="/getalarmMeaadge",method = RequestMethod.GET)
    public DetailDTO getalarmMeaadge(HttpServletRequest request, HttpServletResponse response, OnLineMonitoringDTO onLineMonitoringDTO){
        DetailDTO returnDetailDTO= new DetailDTO(true);

        if(onLineMonitoringDTO.getObjectId()==null||onLineMonitoringDTO.getObjectId().equals("")){
            returnDetailDTO.setStatus(false);
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage("objectId不能为空！");
            return  returnDetailDTO;
        }
        if(onLineMonitoringDTO.getTag()==null||onLineMonitoringDTO.getTag().equals("")){
            returnDetailDTO.setStatus(false);
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage("tag不能为空！");
            return  returnDetailDTO;
        }


        //System.out.println(monitoringPasswordStr+"@"+monitoringPasswordThis);

        if(onLineMonitoringDTO.getWarningBackDay()==null||onLineMonitoringDTO.getWarningBackDay().equals("")){
            onLineMonitoringDTO.setWarningBackDay(super.warningBackDay);
        }
        try {
            //onLineMonitoringDTO.setMonitoringPointAliasId("g_yj_002");
//            onLineMonitoringDTO.setMonitoringPointAliasId("g_yj_001");
//            onLineMonitoringDTO.setMonitoringPointDate("20170601");
//            onLineMonitoringDTO.setMonitoringPointType("DY");
            returnDetailDTO=alarmService.getalarmMessage(onLineMonitoringDTO);
        }catch (YnTransactionalException e) {
            returnDetailDTO.setStatus(e.isState());
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage(e.getErrorMsg());
            return returnDetailDTO;
        }
        return returnDetailDTO;
    }

    @RequestMapping(value = "/getGJdate",method = RequestMethod.GET)
    public List<Map> getGJdate(String aliasId){

        return alarmService.getGJdate(aliasId);
    }




}
