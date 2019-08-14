package pw.wechatbrother.base.rest;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pw.wechatbrother.base.controller.BaseController;
import pw.wechatbrother.base.dto.DetailDTO;
import pw.wechatbrother.base.dto.JsTree;
import pw.wechatbrother.base.dto.LoginAppDTO;
import pw.wechatbrother.base.dto.onlinemonitoring.MonitoringObjectDTO;
import pw.wechatbrother.base.dto.onlinemonitoring.OnLineMonitoringDTO;

import pw.wechatbrother.base.service.JsTreeService;
import pw.wechatbrother.base.service.UserService;
import pw.wechatbrother.base.service.YnBaseService;
import pw.wechatbrother.base.utils.exception.YnTransactionalException;
import pw.wechatbrother.base.utils.safety.DesEncrypt;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** 公用的app总模块
 * Created by zhengjingli on 2016/12/18
 */
@RestController
@RequestMapping("/api/v1/YnBase")
public class YnBaseRestController extends BaseController {
    @Autowired
    private YnBaseService ynBaseService;
    @Autowired
    private JsTreeService jsTreeService;
    @Autowired
    private ToolClass toolClass;


    //根据企业id获取企业下面的电房以及电房下面的监测点以及其运行的状态
    @RequestMapping(value="/selectSubstationMsgByEnterpriseId",method = RequestMethod.GET)
    public DetailDTO selectSubstationMsgByEnterpriseId(HttpServletRequest request,HttpServletResponse response,OnLineMonitoringDTO onLineMonitoringDTO) throws UnsupportedEncodingException {
        DetailDTO returnDetailDTO= new DetailDTO(true);
        LoginAppDTO appLoginUser= new LoginAppDTO();
         /*LoginAppDTO appLoginUser= (LoginAppDTO) request.getAttribute("appLoginUser");//获取app登录用户信息
        //测试的时候可以用到，不测试的时候可以注释掉
        if(appLoginUser==null){
            DetailDTO detailDTO= DefaultLoginAppDTO.getDefaultLoginAppDTO(request, response, appLoginUser);
            if(!detailDTO.getStatus()){
                returnDetailDTO.setStatus(false);
                returnDetailDTO.setErrorCode("-1");
                returnDetailDTO.setErrorMessage("登录不成功！");
                return  returnDetailDTO;
            }else{
                appLoginUser= (LoginAppDTO)detailDTO.getDetail();
            }
        }*/
        String monitoringPassword=super.monitoringPassword;
        DesEncrypt desEncrypt=new DesEncrypt();
        if(onLineMonitoringDTO.getMonitoringPassword()==null||onLineMonitoringDTO.getMonitoringPassword().equals("")){
            returnDetailDTO.setStatus(false);
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage("monitoringPassword计量密码调用时候自带密码校验字符串不能为空！");
            return  returnDetailDTO;
        }
        String monitoringPasswordStr=desEncrypt.decrypt(java.net.URLDecoder.decode(onLineMonitoringDTO.getMonitoringPassword(),"utf-8") );
        String monitoringPasswordThis=monitoringPassword;
        //System.out.println(monitoringPasswordStr+"@"+monitoringPasswordThis);
        if(!monitoringPasswordThis.equals(monitoringPasswordStr)){
            returnDetailDTO.setStatus(false);
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage("调用权限调用失败！");
            return  returnDetailDTO;
        }
        if(onLineMonitoringDTO.getYnBaseCustomerEnterpriseLongId()==null||onLineMonitoringDTO.getYnBaseCustomerEnterpriseLongId().equals("")){
            returnDetailDTO.setStatus(false);
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage("ynBaseCustomerEnterpriseLongId不能为空！");
            return  returnDetailDTO;
        }
        try {
            returnDetailDTO=ynBaseService.selectSubstationMsgByEnterpriseId(onLineMonitoringDTO, appLoginUser);
        }catch (YnTransactionalException e) {
            returnDetailDTO.setStatus(e.isState());
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage(e.getErrorMsg());
            return returnDetailDTO;
        }
        return returnDetailDTO;
    }
    //根据维保公司id查询存在监测点的用电企业
    @RequestMapping(value="/selectEnterpriseInfoByCompanyIdAndEnterpriseName",method = RequestMethod.GET)
    public DetailDTO selectEnterpriseInfoByCompanyIdAndEnterpriseName(HttpServletRequest request,HttpServletResponse response,MonitoringObjectDTO monitoringObjectDTO) throws UnsupportedEncodingException {
        DetailDTO returnDetailDTO= new DetailDTO(true);
        LoginAppDTO appLoginUser= new LoginAppDTO();
         /*LoginAppDTO appLoginUser= (LoginAppDTO) request.getAttribute("appLoginUser");//获取app登录用户信息
        //测试的时候可以用到，不测试的时候可以注释掉
        if(appLoginUser==null){
            DetailDTO detailDTO= DefaultLoginAppDTO.getDefaultLoginAppDTO(request, response, appLoginUser);
            if(!detailDTO.getStatus()){
                returnDetailDTO.setStatus(false);
                returnDetailDTO.setErrorCode("-1");
                returnDetailDTO.setErrorMessage("登录不成功！");
                return  returnDetailDTO;
            }else{
                appLoginUser= (LoginAppDTO)detailDTO.getDetail();
            }
        }*/
        String monitoringPassword=super.monitoringPassword;
        DesEncrypt desEncrypt=new DesEncrypt();
        if(monitoringObjectDTO.getMonitoringPassword()==null||monitoringObjectDTO.getMonitoringPassword().equals("")){
            returnDetailDTO.setStatus(false);
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage("monitoringPassword计量密码调用时候自带密码校验字符串不能为空！");
            return  returnDetailDTO;
        }
        String monitoringPasswordStr=desEncrypt.decrypt(java.net.URLDecoder.decode(monitoringObjectDTO.getMonitoringPassword(),"utf-8") );
        String monitoringPasswordThis=monitoringPassword;
        //System.out.println(monitoringPasswordStr+"@"+monitoringPasswordThis);
        if(!monitoringPasswordThis.equals(monitoringPasswordStr)){
            returnDetailDTO.setStatus(false);
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage("调用权限调用失败！");
            return  returnDetailDTO;
        }
        if(monitoringObjectDTO.getYnBaseStaffCompanyId()==null||monitoringObjectDTO.getYnBaseStaffCompanyId().equals("")){
            returnDetailDTO.setStatus(false);
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage("维保公司id，ynBaseStaffCompanyId不能为空");
            return  returnDetailDTO;
        }
        try {
            returnDetailDTO=ynBaseService.selectEnterpriseInfoByCompanyIdAndEnterpriseName(monitoringObjectDTO, appLoginUser);
        }catch (YnTransactionalException e) {
            returnDetailDTO.setStatus(e.isState());
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage(e.getErrorMsg());
            return returnDetailDTO;
        }
        return returnDetailDTO;
    }



   //查询存在计量点的电房信息(服务于小李pc在线监测首页)
    @RequestMapping(value="/selectEnterpriseSubstationHaveMonitoringPoint",method = RequestMethod.GET)
    public DetailDTO selectEnterpriseSubstationHaveMonitoringPoint(HttpServletRequest request,HttpServletResponse response,MonitoringObjectDTO monitoringObjectDTO,@RequestParam String serviceAreas) throws UnsupportedEncodingException {
        DetailDTO returnDetailDTO= new DetailDTO(true);
        LoginAppDTO appLoginUser= new LoginAppDTO();
         /*LoginAppDTO appLoginUser= (LoginAppDTO) request.getAttribute("appLoginUser");//获取app登录用户信息
        //测试的时候可以用到，不测试的时候可以注释掉
        if(appLoginUser==null){
            DetailDTO detailDTO= DefaultLoginAppDTO.getDefaultLoginAppDTO(request, response, appLoginUser);
            if(!detailDTO.getStatus()){
                returnDetailDTO.setStatus(false);
                returnDetailDTO.setErrorCode("-1");
                returnDetailDTO.setErrorMessage("登录不成功！");
                return  returnDetailDTO;
            }else{
                appLoginUser= (LoginAppDTO)detailDTO.getDetail();
            }
        }*/
        String monitoringPassword=super.monitoringPassword;
        DesEncrypt desEncrypt=new DesEncrypt();
        if(monitoringObjectDTO.getMonitoringPassword()==null||monitoringObjectDTO.getMonitoringPassword().equals("")){
            returnDetailDTO.setStatus(false);
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage("monitoringPassword计量密码调用时候自带密码校验字符串不能为空！");
            return  returnDetailDTO;
        }
        String monitoringPasswordStr=desEncrypt.decrypt(java.net.URLDecoder.decode(monitoringObjectDTO.getMonitoringPassword(),"utf-8") );
        String monitoringPasswordThis=monitoringPassword;
        //System.out.println(monitoringPasswordStr+"@"+monitoringPasswordThis);
        if(!monitoringPasswordThis.equals(monitoringPasswordStr)){
            returnDetailDTO.setStatus(false);
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage("调用权限调用失败！");
            return  returnDetailDTO;
        }
        if(monitoringObjectDTO.getYnBaseStaffCompanyId()==null||monitoringObjectDTO.getYnBaseStaffCompanyId().equals("")){
            returnDetailDTO.setStatus(false);
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage("维保公司id，ynBaseStaffCompanyId不能为空");
            return  returnDetailDTO;
        }
        try {
            returnDetailDTO=ynBaseService.selectEnterpriseSubstationHaveMonitoringPoint(monitoringObjectDTO, appLoginUser,toolClass.stringToList(serviceAreas));
        }catch (YnTransactionalException e) {
            returnDetailDTO.setStatus(e.isState());
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage(e.getErrorMsg());
            return returnDetailDTO;
        }
        return returnDetailDTO;
    }

    //根据电房id搜索所有的设备台账id以及监测点id清单
    @RequestMapping(value="/selectEquipmentMonitoringPointBySubstationId",method = RequestMethod.GET)
    public DetailDTO selectEquipmentMonitoringPointBySubstationId(HttpServletRequest request,HttpServletResponse response,OnLineMonitoringDTO onLineMonitoringDTO){
        DetailDTO returnDetailDTO= new DetailDTO(true);
        LoginAppDTO appLoginUser= new LoginAppDTO();
        /*LoginAppDTO appLoginUser= (LoginAppDTO) request.getAttribute("appLoginUser");//获取app登录用户信息
        //测试的时候可以用到，不测试的时候可以注释掉
        if(appLoginUser==null){
            DetailDTO detailDTO= DefaultLoginAppDTO.getDefaultLoginAppDTO(request, response, appLoginUser);
            if(!detailDTO.getStatus()){
                returnDetailDTO.setStatus(false);
                returnDetailDTO.setErrorCode("-1");
                returnDetailDTO.setErrorMessage("登录不成功！");
                return  returnDetailDTO;
            }else{
                appLoginUser= (LoginAppDTO)detailDTO.getDetail();
            }
        }*/
        DesEncrypt desEncrypt=new DesEncrypt();
       if(onLineMonitoringDTO.getMonitoringPassword()==null||onLineMonitoringDTO.getMonitoringPassword().equals("")){
            returnDetailDTO.setStatus(false);
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage("monitoringPassword计量密码调用时候自带密码校验字符串不能为空！");
            return  returnDetailDTO;
        }
        String monitoringPasswordStr= null;
        try {
            monitoringPasswordStr = desEncrypt.decrypt(java.net.URLDecoder.decode(onLineMonitoringDTO.getMonitoringPassword(),"utf-8") );
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String monitoringPasswordThis=monitoringPassword;
        //System.out.println(monitoringPasswordStr+"@"+monitoringPasswordThis);
        if(!monitoringPasswordThis.equals(monitoringPasswordStr)){
            returnDetailDTO.setStatus(false);
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage("调用权限调用失败！");
            return  returnDetailDTO;
        }
        if(onLineMonitoringDTO.getYnBaseEnterpriseSubstationLongId()==null||onLineMonitoringDTO.getYnBaseEnterpriseSubstationLongId().equals("")){
            returnDetailDTO.setStatus(false);
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage("ynBaseEnterpriseSubstationLongId不能为空！");
            return  returnDetailDTO;
        }
        try {
            returnDetailDTO=ynBaseService.selectEquipmentMonitoringPointBySubstationId(onLineMonitoringDTO, appLoginUser);
        }catch (YnTransactionalException e) {
            returnDetailDTO.setStatus(e.isState());
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage(e.getErrorMsg());
            return returnDetailDTO;
        }
        return returnDetailDTO;
    }


    //根据计量点号、查询的类型和值查询结果
    @RequestMapping(value="/selectReportByMonitoringPoint",method = RequestMethod.GET)
    public DetailDTO selectReportByMonitoringPoint(HttpServletRequest request,HttpServletResponse response,OnLineMonitoringDTO onLineMonitoringDTO){
        DetailDTO returnDetailDTO= new DetailDTO(true);
        LoginAppDTO appLoginUser= new LoginAppDTO();
        /*LoginAppDTO appLoginUser= (LoginAppDTO) request.getAttribute("appLoginUser");//获取app登录用户信息
        //测试的时候可以用到，不测试的时候可以注释掉
        if(appLoginUser==null){
            DetailDTO detailDTO= DefaultLoginAppDTO.getDefaultLoginAppDTO(request, response, appLoginUser);
            if(!detailDTO.getStatus()){
                returnDetailDTO.setStatus(false);
                returnDetailDTO.setErrorCode("-1");
                returnDetailDTO.setErrorMessage("登录不成功！");
                return  returnDetailDTO;
            }else{
                appLoginUser= (LoginAppDTO)detailDTO.getDetail();
            }
        }*/
       /* if(onLineMonitoringDTO.getYnBaseEnterpriseSubstationLongId()==null||onLineMonitoringDTO.getYnBaseEnterpriseSubstationLongId().equals("")){
            returnDetailDTO.setStatus(false);
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage("ynBaseEnterpriseSubstationLongId不能为空！");
            return  returnDetailDTO;
        }*/
        DesEncrypt desEncrypt=new DesEncrypt();
        if(onLineMonitoringDTO.getMonitoringPassword()==null||onLineMonitoringDTO.getMonitoringPassword().equals("")){
            returnDetailDTO.setStatus(false);
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage("monitoringPassword计量密码调用时候自带密码校验字符串不能为空！");
            return  returnDetailDTO;
        }
        String monitoringPasswordStr= null;
        try {
            monitoringPasswordStr = desEncrypt.decrypt(java.net.URLDecoder.decode(onLineMonitoringDTO.getMonitoringPassword(),"utf-8") );
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String monitoringPasswordThis=monitoringPassword;
        //System.out.println(monitoringPasswordStr+"@"+monitoringPasswordThis);
        if(!monitoringPasswordThis.equals(monitoringPasswordStr)){
            returnDetailDTO.setStatus(false);
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage("调用权限调用失败！");
            return  returnDetailDTO;
        }
        try {
            //onLineMonitoringDTO.setMonitoringPointAliasId("g_yj_002");
//            onLineMonitoringDTO.setMonitoringPointAliasId("g_yj_001");
//            onLineMonitoringDTO.setMonitoringPointDate("20170601");
//            onLineMonitoringDTO.setMonitoringPointType("DY");
            returnDetailDTO=ynBaseService.selectReportByMonitoringPoint(onLineMonitoringDTO, appLoginUser);
        }catch (YnTransactionalException e) {
            returnDetailDTO.setStatus(e.isState());
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage(e.getErrorMsg());
            return returnDetailDTO;
        }
        return returnDetailDTO;
    }

    //报警查询
    @RequestMapping(value="/selectWarningByMonitoringPoint",method = RequestMethod.GET)
    public DetailDTO selectWarningByMonitoringPoint(HttpServletRequest request,HttpServletResponse response,OnLineMonitoringDTO onLineMonitoringDTO,@RequestParam String serviceAreas0){
        DetailDTO returnDetailDTO= new DetailDTO(true);
        LoginAppDTO appLoginUser= new LoginAppDTO();
        DesEncrypt desEncrypt=new DesEncrypt();
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
        String monitoringPasswordStr= null;
        try {
            monitoringPasswordStr = desEncrypt.decrypt(java.net.URLDecoder.decode(onLineMonitoringDTO.getMonitoringPassword(),"utf-8") );
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String monitoringPasswordThis=monitoringPassword;
        //System.out.println(monitoringPasswordStr+"@"+monitoringPasswordThis);
        if(!monitoringPasswordThis.equals(monitoringPasswordStr)){
            returnDetailDTO.setStatus(false);
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage("调用权限调用失败！");
            return  returnDetailDTO;
        }
        if(onLineMonitoringDTO.getWarningBackDay()==null||onLineMonitoringDTO.getWarningBackDay().equals("")){
            onLineMonitoringDTO.setWarningBackDay(super.warningBackDay);
        }
        try {
            //onLineMonitoringDTO.setMonitoringPointAliasId("g_yj_002");
//            onLineMonitoringDTO.setMonitoringPointAliasId("g_yj_001");
//            onLineMonitoringDTO.setMonitoringPointDate("20170601");
//            onLineMonitoringDTO.setMonitoringPointType("DY");
            onLineMonitoringDTO.setServicesAreas(serviceAreas0);
            returnDetailDTO=ynBaseService.selectWarningByMonitoringPoint(onLineMonitoringDTO, appLoginUser);
        }catch (YnTransactionalException e) {
            returnDetailDTO.setStatus(e.isState());
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage(e.getErrorMsg());
            return returnDetailDTO;
        }
        return returnDetailDTO;
    }

    //根据计量点的自定义id查询电量分析 Electric analysis   日数据表格
    @RequestMapping(value="/selectElectricAnalysisByMonitoringPoint",method = RequestMethod.GET)
    public DetailDTO selectElectricAnalysisByMonitoringPoint(HttpServletRequest request,HttpServletResponse response,OnLineMonitoringDTO onLineMonitoringDTO){
        DetailDTO returnDetailDTO= new DetailDTO(true);
        LoginAppDTO appLoginUser= new LoginAppDTO();
        DesEncrypt desEncrypt=new DesEncrypt();
        if(onLineMonitoringDTO.getMonitoringPointAliasId()==null||onLineMonitoringDTO.getMonitoringPointAliasId().equals("")){
            returnDetailDTO.setStatus(false);
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage("monitoringPointAliasId不能为空！");
            return  returnDetailDTO;
        }
        if(onLineMonitoringDTO.getMonitoringPointDate()==null||onLineMonitoringDTO.getMonitoringPointDate().equals("")){
            returnDetailDTO.setStatus(false);
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage("monitoringPointDate不能为空,格式如：20170603！");
            return  returnDetailDTO;
        }
        String monitoringPasswordStr= null;
        try {
            monitoringPasswordStr = desEncrypt.decrypt(java.net.URLDecoder.decode(onLineMonitoringDTO.getMonitoringPassword(),"utf-8") );
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String monitoringPasswordThis=monitoringPassword;
        //System.out.println(monitoringPasswordStr+"@"+monitoringPasswordThis);
        if(!monitoringPasswordThis.equals(monitoringPasswordStr)){
            returnDetailDTO.setStatus(false);
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage("调用权限调用失败！");
            return  returnDetailDTO;
        }
        try {
            if(onLineMonitoringDTO.getTag()== null || onLineMonitoringDTO.getTag().equals("null")){
                returnDetailDTO=ynBaseService.selectElectricAnalysisByMonitoringPoint(onLineMonitoringDTO, appLoginUser);
            }else {

                String j = onLineMonitoringDTO.getTag();
                int jj = j.length();
                if(jj==2){
                    System.out.println("---------------------------------");
                }else if(jj==3){
                    //断相  3位
                    returnDetailDTO=ynBaseService.selectElectricAnalysisByMonitoringPointDX(onLineMonitoringDTO, appLoginUser);
                }else if(jj==4){
                    //功率因数写死四位
                    returnDetailDTO=ynBaseService.selectElectricAnalysisByMonitoringPointGLYS(onLineMonitoringDTO, appLoginUser);
                }else if(jj==5){
                    //电流极值  5位
                    returnDetailDTO=ynBaseService.selectElectricAnalysisByMonitoringPointDLJZ(onLineMonitoringDTO, appLoginUser);
                }else if(jj==6){
                    //电压极值   6位
                    returnDetailDTO=ynBaseService.selectElectricAnalysisByMonitoringPointDYJZ(onLineMonitoringDTO, appLoginUser);
                }else if(jj==7){
                    //a相总谐波   7位
                    returnDetailDTO=ynBaseService.selectElectricAnalysisByMonitoringPointAXB(onLineMonitoringDTO, appLoginUser);
                }else{
                    returnDetailDTO=ynBaseService.selectElectricAnalysisByMonitoringPointDN(onLineMonitoringDTO, appLoginUser);
                }
            }
        }catch (YnTransactionalException e) {
            returnDetailDTO.setStatus(e.isState());
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage(e.getErrorMsg());
            return returnDetailDTO;
        }
        return returnDetailDTO;
    }

    //根据计量点的自定义id查询电量分析 Electric analysis   月数据表格
    @RequestMapping(value="/selectElectricAnalysisByMonitoringPointTWO",method = RequestMethod.GET)
    public DetailDTO selectElectricAnalysisByMonitoringPointTWO(HttpServletRequest request,HttpServletResponse response,OnLineMonitoringDTO onLineMonitoringDTO){
        DetailDTO returnDetailDTO= new DetailDTO(true);
        LoginAppDTO appLoginUser= new LoginAppDTO();
        DesEncrypt desEncrypt=new DesEncrypt();
        if(onLineMonitoringDTO.getMonitoringPointAliasId()==null||onLineMonitoringDTO.getMonitoringPointAliasId().equals("")){
            returnDetailDTO.setStatus(false);
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage("monitoringPointAliasId不能为空！");
            return  returnDetailDTO;
        }
        if(onLineMonitoringDTO.getMonitoringPointDate()==null||onLineMonitoringDTO.getMonitoringPointDate().equals("")){
            returnDetailDTO.setStatus(false);
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage("monitoringPointDate不能为空,格式如：20170603！");
            return  returnDetailDTO;
        }
        String monitoringPasswordStr= null;
        try {
            monitoringPasswordStr = desEncrypt.decrypt(java.net.URLDecoder.decode(onLineMonitoringDTO.getMonitoringPassword(),"utf-8") );
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String monitoringPasswordThis=monitoringPassword;
        //System.out.println(monitoringPasswordStr+"@"+monitoringPasswordThis);
        if(!monitoringPasswordThis.equals(monitoringPasswordStr)){
            returnDetailDTO.setStatus(false);
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage("调用权限调用失败！");
            return  returnDetailDTO;
        }
        try {
            if(onLineMonitoringDTO.getTag()== null || onLineMonitoringDTO.getTag().equals("null")){
                returnDetailDTO=ynBaseService.selectElectricAnalysisByMonitoringPointTWO(onLineMonitoringDTO, appLoginUser);
            }else {



            }
        }catch (YnTransactionalException e) {
            returnDetailDTO.setStatus(e.isState());
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage(e.getErrorMsg());
            return returnDetailDTO;
        }
        return returnDetailDTO;
    }

    //查询有监测点的公司集合
    @RequestMapping(value="/findAllcompany",method = RequestMethod.GET)
    public DetailDTO findAllcompany(String companyId){
        DetailDTO returnDetailDTO= new DetailDTO(true);
        returnDetailDTO =  ynBaseService.selectEnterpriseInfoJCD();
        return returnDetailDTO;
    }

    //查询监测点月度报告所需数据
    @RequestMapping(value="/findPowerInfoByid",method = RequestMethod.GET)
    public DetailDTO findPowerInfoByid(OnLineMonitoringDTO onLineMonitoringDTO){
        DetailDTO returnDetailDTO= new DetailDTO(true);
        returnDetailDTO = ynBaseService.selectPowerInfoJCD(onLineMonitoringDTO);
        return returnDetailDTO;
    }

    //查询维保公司下面的用电企业的电房下的监测点 jsTree
    @RequestMapping(value="/findJstree",method = RequestMethod.GET)
    public List<JsTree> findJstree(String companyId,String serviceAreas){
        return jsTreeService.getJsTree(companyId,toolClass.stringToList(serviceAreas));
    }




    //查询用电企业的电房下的监测点 jsTree
    @RequestMapping(value="/findJstreeByEnterprise",method = RequestMethod.GET)
    public List<JsTree> findJstreeByEnterprise(String enterpriseId){
        return jsTreeService.findJstreeByEnterprise(enterpriseId);
    }

/*
*@Author chenxudong
*@Date 2019/4/30 15:29
*@return pw.wechatbrother.base.dto.DetailDTO
*method 新的报警信息的查询
**/
    @RequestMapping(value="/selectIntelligentAlarm",method = RequestMethod.GET)
    public DetailDTO selectIntelligentAlarm(HttpServletRequest request,HttpServletResponse response,OnLineMonitoringDTO onLineMonitoringDTO){
        DetailDTO detailDTO = new DetailDTO(true);
        detailDTO =ynBaseService.selectIntelligentAlarmService(onLineMonitoringDTO);
       return detailDTO;

     }
/*
*@Author chenxudong
*@Date 2019/4/30 15:29
*@return pw.wechatbrother.base.dto.DetailDTO
*method 终端状态的查询
**/
    @RequestMapping(value = "/selectTerminal" ,method=RequestMethod.GET)
    public DetailDTO selectTerminal(HttpServletRequest request,HttpServletResponse response,OnLineMonitoringDTO onLineMonitoringDTO){
        DetailDTO detailDTO = new DetailDTO(true);
        detailDTO .setDetail(ynBaseService.selectTerminalService(onLineMonitoringDTO));
        return detailDTO;
    }
/*
*@Author chenxudong
*@Date 2019/4/30 15:30
*@return pw.wechatbrother.base.dto.DetailDTO
*method 终端信息查询
**/
    @RequestMapping(value = "/selectTerminalData" ,method=RequestMethod.GET)
    public DetailDTO selectTerminalData(HttpServletRequest request,HttpServletResponse response,OnLineMonitoringDTO onLineMonitoringDTO){
        DetailDTO detailDTO = new DetailDTO(true);
        detailDTO .setDetail(ynBaseService.selectTerminalDataService(onLineMonitoringDTO));
        return detailDTO;
    }

/*
*@Author chenxudong
*@Date 2019/4/30 15:30
*@return pw.wechatbrother.base.dto.DetailDTO
*method 根据省市区查询终端状态个数
**/
    @RequestMapping(value = "/selectTerminalCityNumber" ,method=RequestMethod.GET)
    public DetailDTO selectTerminalCityNumber(HttpServletRequest request,HttpServletResponse response,OnLineMonitoringDTO onLineMonitoringDTO){
        DetailDTO detailDTO = new DetailDTO(true);
        detailDTO .setDetail(ynBaseService.selectTerminalCityNumberService(onLineMonitoringDTO));
        return detailDTO;
    }

/*
*@Author chenxudong
*@Date 2019/4/30 15:30
*@return pw.wechatbrother.base.dto.DetailDTO
*method 根据省市区查询终端状信息
**/
    @RequestMapping(value = "/selectTerminalDataCity" ,method=RequestMethod.GET)
    public DetailDTO selectTerminalDataCity(HttpServletRequest request,HttpServletResponse response,OnLineMonitoringDTO onLineMonitoringDTO){
        DetailDTO detailDTO = new DetailDTO(true);
        detailDTO .setDetail(ynBaseService.selectTerminalDataCityService(onLineMonitoringDTO));
        return detailDTO;
    }
    //电房状态
    @RequestMapping(value = "/selectDFState" ,method=RequestMethod.GET)
    public DetailDTO selectDFState(HttpServletRequest request,HttpServletResponse response,OnLineMonitoringDTO onLineMonitoringDTO){
        DetailDTO detailDTO = new DetailDTO(true);
        detailDTO = ynBaseService.selectDFState(onLineMonitoringDTO);
        return detailDTO;
    }

}
