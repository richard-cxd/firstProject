package pw.wechatbrother.base.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import pw.wechatbrother.base.dto.DetailDTO;
import pw.wechatbrother.base.dto.RestStatus;
import pw.wechatbrother.base.service.BoNaService;

/**
 * Created by YN on 2018/5/8.
 */
@RestController
@RequestMapping("/BoNa")
public class BoNaController {
    @Autowired
    private BoNaService boNaService;
    //表底数
    @RequestMapping(value="/addBD",method = RequestMethod.POST)
    public RestStatus addBD(String jcdId,String DataDate,String time,String param){
        RestStatus restStatus = new RestStatus(true);
        try {
            boNaService.addBD(jcdId,DataDate,time,param);
        }catch (Exception e){
            restStatus.setStatus(false);
            restStatus.setErrorMessage("后台异常，请联系管理员");
        }
        return restStatus;
    }

    //电流
    @RequestMapping(value = "/addDL",method = RequestMethod.POST)
    public RestStatus addDL(String jcdId,String DataDate,String time,String param){
        RestStatus restStatus = new RestStatus(true);
        try {
            boNaService.addDL(jcdId, DataDate, time, param);
        }catch (Exception e){
            restStatus.setStatus(false);
            restStatus.setErrorMessage("后台异常，请联系管理员");
        }
        return restStatus;
    }

    //电压
    @RequestMapping(value = "/addDY",method = RequestMethod.POST)
    public RestStatus addDY(String jcdId,String DataDate,String time,String param){
        RestStatus restStatus = new RestStatus(true);
        try {
            boNaService.addDY(jcdId, DataDate, time, param);
        }catch (Exception e){
            restStatus.setStatus(false);
            restStatus.setErrorMessage("后台异常，请联系管理员");
        }
        return restStatus;
    }
    //频率
    @RequestMapping(value = "/addPL",method = RequestMethod.POST)
    public RestStatus addPL(String jcdId,String DataDate,String time,String param){
        RestStatus restStatus = new RestStatus(true);
        try {
            boNaService.addPL(jcdId, DataDate, time, param);
        }catch (Exception e){
            restStatus.setStatus(false);
            restStatus.setErrorMessage("后台异常，请联系管理员");
        }
        return restStatus;
    }
    //视在
    @RequestMapping(value = "/addSZ",method = RequestMethod.POST)
    public RestStatus addSZ(String jcdId,String DataDate,String time,String param){
        RestStatus restStatus = new RestStatus(true);
        try {
            boNaService.addSZ(jcdId, DataDate, time, param);
        }catch (Exception e){
            restStatus.setStatus(false);
            restStatus.setErrorMessage("后台异常，请联系管理员");
        }
        return restStatus;
    }

    //无功
    @RequestMapping(value = "/addWG",method = RequestMethod.POST)
    public RestStatus addWG(String jcdId,String DataDate,String time,String param){
        RestStatus restStatus = new RestStatus(true);
        try {
            boNaService.addWG(jcdId, DataDate, time, param);
        }catch (Exception e){
            restStatus.setStatus(false);
            restStatus.setErrorMessage("后台异常，请联系管理员");
        }
        return restStatus;
    }
    //有功
    @RequestMapping(value = "/addYG",method = RequestMethod.POST)
    public RestStatus addYG(String jcdId,String DataDate,String time,String param){
        RestStatus restStatus = new RestStatus(true);
        try {
            boNaService.addYG(jcdId, DataDate, time, param);
        }catch (Exception e){
            restStatus.setStatus(false);
            restStatus.setErrorMessage("后台异常，请联系管理员");
        }
        return restStatus;
    }

    //功率因数
    @RequestMapping(value = "/addYS",method = RequestMethod.POST)
    public RestStatus addYS(String jcdId,String DataDate,String time,String param){
        RestStatus restStatus = new RestStatus(true);
        try {
            boNaService.addYS(jcdId, DataDate, time, param);
        }catch (Exception e){
            restStatus.setStatus(false);
            restStatus.setErrorMessage("后台异常，请联系管理员");
        }
        return restStatus;
    }

  /*  //告警
    @RequestMapping(value = "/addGJ",method = RequestMethod.POST)
    public RestStatus addGJ(String jcdId,String DataDate,String level,String type,String msg){
        RestStatus restStatus = new RestStatus(true);
        try {
            boNaService.addGJ(jcdId, DataDate, level, type,msg);
        }catch (Exception e){
            restStatus.setStatus(false);
            restStatus.setErrorMessage("后台异常，请联系管理员");
        }
        return restStatus;
    }
*/
    //电压谐波数据
    @RequestMapping(value = "/addXY",method = RequestMethod.POST)
    public RestStatus addXY(String jcdId,String DataDate,String time,String param){
        RestStatus restStatus = new RestStatus(true);
        try {
            boNaService.addXY(jcdId, DataDate, time, param);
        }catch (Exception e){

            restStatus.setStatus(false);
            restStatus.setErrorMessage("后台异常，请联系管理员");
        }
        return restStatus;
    }

    //电流谐波数据
    @RequestMapping(value = "/addXL",method = RequestMethod.POST)
    public RestStatus addXL(String jcdId,String DataDate,String time,String param){
        RestStatus restStatus = new RestStatus(true);
        try {
            boNaService.addXL(jcdId, DataDate, time, param);
        }catch (Exception e){

            restStatus.setStatus(false);
            restStatus.setErrorMessage("后台异常，请联系管理员");
        }

        return restStatus;
    }
    //零序
    @RequestMapping(value = "/addNX",method = RequestMethod.POST)
    public RestStatus addNX(String jcdId,String DataDate,String time,String param){
        RestStatus restStatus = new RestStatus(true);
        try {
            boNaService.addNX(jcdId, DataDate, time, param);
        }catch (Exception e){

            restStatus.setStatus(false);
            restStatus.setErrorMessage("后台异常，请联系管理员");
        }

        return restStatus;
    }

    //分时电能
    @RequestMapping(value = "/addFS",method = RequestMethod.POST)
    public RestStatus addFS(String jcdId,String DataDate,String time,String param){
        RestStatus restStatus = new RestStatus(true);
        try {
            boNaService.addFS(jcdId, DataDate, time, param);
        }catch (Exception e){

            restStatus.setStatus(false);
            restStatus.setErrorMessage("后台异常，请联系管理员");
        }

        return restStatus;
    }
//========================================================================================================================

    //昨日停电次数
    @RequestMapping(value = "/addYesterdayTing",method = RequestMethod.POST)
    public RestStatus addYesterdayTing(String jcdId,String DataDate,String param){
        RestStatus restStatus = new RestStatus(true);
        String TB = "jc_dd_";
        String[] str1 = param.split(",");
        String TingCount = str1[1];
        String TingDate = str1[0];
        try {
            String hh =  boNaService.selectTB(jcdId);
            TB+=hh;
            //System.out.println(TB);
            boNaService.updateDD(TingCount,TingDate,DataDate,TB,jcdId);
        }catch (Exception e){
            restStatus.setStatus(false);
            restStatus.setErrorMessage("后台异常，请联系管理员");
        }
        return restStatus;
    }

    //月停电
    @RequestMapping(value = "/addMonthTD",method = RequestMethod.POST)
    public RestStatus addMonthTD(String jcdId,String DataDate,String time,String param){
        RestStatus restStatus = new RestStatus(true);

        try {
            boNaService.addMonthTD(jcdId, DataDate, time, param);
        }catch (Exception e){
            restStatus.setStatus(false);

            restStatus.setErrorMessage("后台异常，请联系管理员");
        }
        return restStatus;
    }

    //有功无功电能
    @RequestMapping(value = "/addActiveDn",method = RequestMethod.POST)
    public RestStatus addActiveDn(String jcdId,String DataDate,String time,String param){
        RestStatus restStatus = new RestStatus(true);

        try {
            boNaService.addDN(jcdId, DataDate, time, param);
        }catch (Exception e){
            restStatus.setStatus(false);
            restStatus.setErrorMessage("后台异常，请联系管理员");
        }
        return restStatus;
    }
    //有功无功功率因素极值
    @RequestMapping(value = "/addActiveMAX",method = RequestMethod.POST)
    public RestStatus addActiveMAX(String jcdId,String DataDate,String time,String param){
        RestStatus restStatus = new RestStatus(true);

        try {
            boNaService.addAMAX(jcdId, DataDate, time, param);
        }catch (Exception e){
            restStatus.setStatus(false);
            restStatus.setErrorMessage("后台异常，请联系管理员");
        }
        return restStatus;
    }

    //电流质量
    @RequestMapping(value = "/addDLJZ",method = RequestMethod.POST)
    public RestStatus addDLJZ(String jcdId,String DataDate,String time,String param){
        RestStatus restStatus = new RestStatus(true);

        try {
            boNaService.addDLJZ(jcdId, DataDate, time, param);
        }catch (Exception e){
            restStatus.setStatus(false);
            restStatus.setErrorMessage("后台异常，请联系管理员");
        }
        return restStatus;
    }
    //电压质量
    @RequestMapping(value = "/addDYJZ",method = RequestMethod.POST)
    public RestStatus addDYJZ(String jcdId,String DataDate,String time,String param){
        RestStatus restStatus = new RestStatus(true);

        try {
            boNaService.addDYJZ(jcdId, DataDate, time, param);
        }catch (Exception e){
            restStatus.setStatus(false);
            restStatus.setErrorMessage("后台异常，请联系管理员");
        }
        return restStatus;
    }
    //昨日谐波信息
    @RequestMapping(value = "/addRZXB",method = RequestMethod.POST)
    public RestStatus addRZXB(String jcdId,String DataDate,String time,String param){
        RestStatus restStatus = new RestStatus(true);

        try {
            boNaService.addRZXB(jcdId, DataDate, time, param);
        }catch (Exception e){
            restStatus.setStatus(false);
            restStatus.setErrorMessage("后台异常，请联系管理员");
        }
        return restStatus;
    }
    //断相监测
    @RequestMapping(value = "/addRDX",method = RequestMethod.POST)
    public RestStatus addRDX(String jcdId,String DataDate,String time,String param){
        RestStatus restStatus = new RestStatus(true);

        try {
            boNaService.addRDX(jcdId, DataDate, time, param);
        }catch (Exception e){
            restStatus.setStatus(false);
            restStatus.setErrorMessage("后台异常，请联系管理员");
        }
        return restStatus;
    }
    //上月电能
    @RequestMapping(value = "/addMDN",method = RequestMethod.POST)
    public RestStatus addMDN(String jcdId,String DataDate,String time,String param){
        RestStatus restStatus = new RestStatus(true);

        try {
            boNaService.addDNM(jcdId, DataDate, time, param);
        }catch (Exception e){
            restStatus.setStatus(false);
            restStatus.setErrorMessage("后台异常，请联系管理员");
        }
        return restStatus;
    }
    //上月功率及功率因数
    @RequestMapping(value = "/addYYGLM",method = RequestMethod.POST)
    public RestStatus addYYGLM(String jcdId,String DataDate,String time,String param){
        RestStatus restStatus = new RestStatus(true);

        try {
            boNaService.addYYGLM(jcdId, DataDate, time, param);
        }catch (Exception e){
            restStatus.setStatus(false);
            restStatus.setErrorMessage("后台异常，请联系管理员");
        }
        return restStatus;
    }
    //上月电流极值
    @RequestMapping(value = "/addDLJZM",method = RequestMethod.POST)
    public RestStatus addDLJZM(String jcdId,String DataDate,String time,String param){
        RestStatus restStatus = new RestStatus(true);

        try {
            boNaService.addDLJZM(jcdId, DataDate, time, param);
        }catch (Exception e){
            restStatus.setStatus(false);
            restStatus.setErrorMessage("后台异常，请联系管理员");
        }
        return restStatus;
    }
    //上月电压极值
    @RequestMapping(value = "/addDYJZM",method = RequestMethod.POST)
    public RestStatus addDYJZM(String jcdId,String DataDate,String time,String param){
        RestStatus restStatus = new RestStatus(true);

        try {
            boNaService.addDYJZM(jcdId, DataDate, time, param);
        }catch (Exception e){
            restStatus.setStatus(false);
            restStatus.setErrorMessage("后台异常，请联系管理员");
        }
        return restStatus;
    }
    //上月总谐波值
    @RequestMapping(value = "/addMzxb",method = RequestMethod.POST)
    public RestStatus addMzxb(String jcdId,String DataDate,String time,String param){
        RestStatus restStatus = new RestStatus(true);

        try {
            boNaService.addMzxb(jcdId, DataDate, time, param);
        }catch (Exception e){
            restStatus.setStatus(false);
            restStatus.setErrorMessage("后台异常，请联系管理员");
        }
        return restStatus;
    }
    //上月断相
    @RequestMapping(value = "/addMdx",method = RequestMethod.POST)
    public RestStatus addMdx(String jcdId,String DataDate,String time,String param){
        RestStatus restStatus = new RestStatus(true);

        try {
            boNaService.addMdx(jcdId, DataDate, time, param);
        }catch (Exception e){
            restStatus.setStatus(false);
            restStatus.setErrorMessage("后台异常，请联系管理员");
        }
        return restStatus;
    }


}
