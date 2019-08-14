package pw.wechatbrother.base.rest;


import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pw.wechatbrother.base.controller.BaseController;
import pw.wechatbrother.base.dto.DetailDTO;
import pw.wechatbrother.base.dto.LoginAppDTO;
import pw.wechatbrother.base.dto.onlinemonitoring.BigWarningConfigDTO;
import pw.wechatbrother.base.dto.onlinemonitoring.MonitoringObjectDTO;
import pw.wechatbrother.base.dto.onlinemonitoring.OnLineMonitoringDTO;
import pw.wechatbrother.base.dto.onlinemonitoring.WarningConfigDTO;
import pw.wechatbrother.base.service.YnMonitoringInitService;
import pw.wechatbrother.base.utils.exception.YnTransactionalException;
import pw.wechatbrother.base.utils.safety.DesEncrypt;
import pw.wechatbrother.base.utils.safety.Utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;

/** 在线监测设备初始化模块
 * Created by zhengjingli on 2016/12/18
 */
@RestController
@RequestMapping("/api/v1/ynMonitoringInit")
public class YnMonitoringInitRestController extends BaseController {
    @Autowired
    private YnMonitoringInitService ynMonitoringInitService;
    @Autowired
    private ToolClass toolClass;
    //根据设备整形id查询该设备下所有的计量点信息
    @RequestMapping(value="/selectMonitoringInfoByEquipmentLongId",method = RequestMethod.GET)
    public DetailDTO selectMonitoringInfoByEquipmentLongId(HttpServletRequest request,HttpServletResponse response,OnLineMonitoringDTO onLineMonitoringDTO){
        DetailDTO returnDetailDTO= new DetailDTO(true);
        LoginAppDTO appLoginUser= new LoginAppDTO();//获取app登录用户信息

        String monitoringPassword=super.monitoringPassword;
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
            returnDetailDTO=ynMonitoringInitService.selectMonitoringInfoByEquipmentLongId(onLineMonitoringDTO, appLoginUser);
        }catch (YnTransactionalException e) {
            returnDetailDTO.setStatus(e.isState());
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage(e.getErrorMsg());
            return returnDetailDTO;
        }
        return returnDetailDTO;
    }
    //根据用电企业整形id查询该企业下所有的计量点信息
    @RequestMapping(value="/selectSubstationMsgByEnterpriseLongId",method = RequestMethod.GET)
    public DetailDTO selectSubstationMsgByEnterpriseLongId(HttpServletRequest request,HttpServletResponse response,OnLineMonitoringDTO onLineMonitoringDTO){
        DetailDTO returnDetailDTO= new DetailDTO(true);
        LoginAppDTO appLoginUser= new LoginAppDTO();//获取app登录用户信息

        String monitoringPassword=super.monitoringPassword;
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
            returnDetailDTO=ynMonitoringInitService.selectSubstationMsgByEnterpriseId(onLineMonitoringDTO, appLoginUser);
        }catch (YnTransactionalException e) {
            returnDetailDTO.setStatus(e.isState());
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage(e.getErrorMsg());
            return returnDetailDTO;
        }
        return returnDetailDTO;
    }
    //根据用电企业整形id查询该企业下所有的计量点的有功功率
    @RequestMapping(value="/selectActivePowerByEnterpriseLongId",method = RequestMethod.GET)
    public DetailDTO selectYGByEnterpriseLongId(HttpServletRequest request,HttpServletResponse response,OnLineMonitoringDTO onLineMonitoringDTO){
        DetailDTO returnDetailDTO= new DetailDTO(true);
        LoginAppDTO appLoginUser= new LoginAppDTO();//获取app登录用户信息

        String monitoringPassword=super.monitoringPassword;
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
            //添加查询--liyidu
            if(onLineMonitoringDTO.getMonitoringPointName()!=null&&onLineMonitoringDTO.getMonitoringPointName()!=""){
                onLineMonitoringDTO.setMonitoringPointName(java.net.URLDecoder.decode(onLineMonitoringDTO.getMonitoringPointName(),"utf-8")) ;
            }
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
        if(onLineMonitoringDTO.getYnBaseCustomerEnterpriseLongId()==null||onLineMonitoringDTO.getYnBaseCustomerEnterpriseLongId().equals("")) {
            returnDetailDTO.setStatus(false);
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage("用电企业整形id:ynBaseCustomerEnterpriseLongId不能为空！");
            return  returnDetailDTO;
        }
        try {
            returnDetailDTO=ynMonitoringInitService.selectActivePowerByEnterpriseLongId(onLineMonitoringDTO, appLoginUser);
        }catch (YnTransactionalException e) {
            returnDetailDTO.setStatus(e.isState());
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage(e.getErrorMsg());
            return returnDetailDTO;
        }
        return returnDetailDTO;
    }

    //根据电房id和运行的时间获取运行报告的数据request,response
    @RequestMapping(value="/selectOperationReportByEnterpriseSubstationLongId",method = RequestMethod.GET)
    public DetailDTO selectOperationReportByEnterpriseSubstationLongId(HttpServletRequest request,HttpServletResponse response,OnLineMonitoringDTO onLineMonitoringDTO){
        DetailDTO returnDetailDTO= new DetailDTO(true);
        LoginAppDTO appLoginUser= new LoginAppDTO();//获取app登录用户信息

        String monitoringPassword=super.monitoringPassword;
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
        if(onLineMonitoringDTO.getYnBaseCustomerEnterpriseLongId()==null||onLineMonitoringDTO.getYnBaseCustomerEnterpriseLongId().equals("")) {
            returnDetailDTO.setStatus(false);
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage("用电企业整形id:ynBaseCustomerEnterpriseLongId不能为空！");
            return  returnDetailDTO;
        }
        if(onLineMonitoringDTO.getYear()==null||onLineMonitoringDTO.getYear().equals("")){
            returnDetailDTO.setStatus(false);
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage("年year不能为空！");
            return  returnDetailDTO;
        }

       /* if(onLineMonitoringDTO.getYnBaseEnterpriseSubstationLongId()==null||onLineMonitoringDTO.getYnBaseEnterpriseSubstationLongId().equals("")) {
            returnDetailDTO.setStatus(false);
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage("用电企业电房整形id:ynBaseEnterpriseSubstationLongId不能为空！");
            return  returnDetailDTO;
        }
        if(onLineMonitoringDTO.getStartDate()==null||onLineMonitoringDTO.getStartDate().equals("")) {
            returnDetailDTO.setStatus(false);
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage("用电企业电房运行报告开始时间:startDate不能为空！");
            return  returnDetailDTO;
        }
        if(onLineMonitoringDTO.getEndDate()==null||onLineMonitoringDTO.getEndDate().equals("")) {
            returnDetailDTO.setStatus(false);
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage("用电企业电房运行报告结束时间:endDate不能为空！");
            return  returnDetailDTO;
        }*/

        try {
            try {
                returnDetailDTO=ynMonitoringInitService.selectOperationReportByEnterpriseSubstationLongId(request,response,onLineMonitoringDTO, appLoginUser);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }catch (YnTransactionalException e) {
            returnDetailDTO.setStatus(e.isState());
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage(e.getErrorMsg());
            return returnDetailDTO;
        }
        return returnDetailDTO;
    }


    //获取计量点类型（终端类型）
    @RequestMapping(value="/selectMonitoringPointType",method = RequestMethod.GET)
    public DetailDTO selectMonitoringPointType(HttpServletRequest request,HttpServletResponse response, OnLineMonitoringDTO onLineMonitoringDTO){
        DetailDTO returnDetailDTO= new DetailDTO(true);
        LoginAppDTO appLoginUser= new LoginAppDTO();//获取app登录用户信息
        String monitoringPassword=super.monitoringPassword;
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
            returnDetailDTO=ynMonitoringInitService.selectMonitoringPointType(onLineMonitoringDTO, appLoginUser);
        }catch (YnTransactionalException e) {
            returnDetailDTO.setStatus(e.isState());
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage(e.getErrorMsg());
            return returnDetailDTO;
        }
        return returnDetailDTO;
    }
   //计量点增加
    @RequestMapping(value="/addMonitoring",method = RequestMethod.POST)
    public DetailDTO addMonitoring(HttpServletRequest request,HttpServletResponse response,@RequestBody MonitoringObjectDTO monitoringObjectDTO){
        DetailDTO returnDetailDTO= new DetailDTO(true);
        LoginAppDTO appLoginUser= new LoginAppDTO();//获取app登录用户信息
        String monitoringPassword=super.monitoringPassword;
        DesEncrypt desEncrypt=new DesEncrypt();
        if(monitoringObjectDTO.getMonitoringPassword()==null||monitoringObjectDTO.getMonitoringPassword().equals("")){
            returnDetailDTO.setStatus(false);
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage("monitoringPassword计量密码调用时候自带密码校验字符串不能为空！");
            return  returnDetailDTO;
        }
        String monitoringPasswordStr= null;
        try {
            monitoringPasswordStr = desEncrypt.decrypt(java.net.URLDecoder.decode(monitoringObjectDTO.getMonitoringPassword(),"utf-8") );
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
            returnDetailDTO=ynMonitoringInitService.addMonitoring(monitoringObjectDTO, appLoginUser);
            try {
                System.out.println("计量点add在Controller："+ Utils.objectToJson(returnDetailDTO));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }catch (YnTransactionalException e) {
            returnDetailDTO.setStatus(e.isState());
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage(e.getErrorMsg());
            return returnDetailDTO;
        }
        return returnDetailDTO;
    }
    //计量点修改
    @RequestMapping(value="/updateMonitoring",method = RequestMethod.POST)
    public DetailDTO updateMonitoring(HttpServletRequest request,HttpServletResponse response,@RequestBody MonitoringObjectDTO monitoringObjectDTO){
        DetailDTO returnDetailDTO= new DetailDTO(true);
        LoginAppDTO appLoginUser= new LoginAppDTO();//获取app登录用户信息
        String monitoringPassword=super.monitoringPassword;
        DesEncrypt desEncrypt=new DesEncrypt();
        if(monitoringObjectDTO.getMonitoringPassword()==null||monitoringObjectDTO.getMonitoringPassword().equals("")){
            returnDetailDTO.setStatus(false);
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage("monitoringPassword计量密码调用时候自带密码校验字符串不能为空！");
            return  returnDetailDTO;
        }
        String monitoringPasswordStr= null;
        try {
            monitoringPasswordStr = desEncrypt.decrypt(java.net.URLDecoder.decode(monitoringObjectDTO.getMonitoringPassword(),"utf-8") );
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
        if(monitoringObjectDTO.getMonitoringPointCoreId()==null||monitoringObjectDTO.getMonitoringPointCoreId().equals("")){
            returnDetailDTO.setStatus(false);
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage("监测点固件核心id:monitoringPointCoreId不能为空！");
            return  returnDetailDTO;
        }
        try {
            returnDetailDTO=ynMonitoringInitService.updateMonitoring(monitoringObjectDTO, appLoginUser);
        }catch (YnTransactionalException e) {
            returnDetailDTO.setStatus(e.isState());
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage(e.getErrorMsg());
            return returnDetailDTO;
        }
        return returnDetailDTO;
    }

    //计量点删除
    @RequestMapping(value="/deleteMonitoring",method = RequestMethod.GET)
    public DetailDTO deleteMonitoring(HttpServletRequest request,HttpServletResponse response, MonitoringObjectDTO monitoringObjectDTO){
        DetailDTO returnDetailDTO= new DetailDTO(true);
        LoginAppDTO appLoginUser= new LoginAppDTO();//获取app登录用户信息
        String monitoringPassword=super.monitoringPassword;
        DesEncrypt desEncrypt=new DesEncrypt();
        if(monitoringObjectDTO.getMonitoringPassword()==null||monitoringObjectDTO.getMonitoringPassword().equals("")){
            returnDetailDTO.setStatus(false);
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage("monitoringPassword计量密码调用时候自带密码校验字符串不能为空！");
            return  returnDetailDTO;
        }
        String monitoringPasswordStr= null;
        try {
            monitoringPasswordStr = desEncrypt.decrypt(java.net.URLDecoder.decode(monitoringObjectDTO.getMonitoringPassword(),"utf-8") );
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
        if(monitoringObjectDTO.getMonitoringPointCoreId()==null||monitoringObjectDTO.getMonitoringPointCoreId().equals("")){
            returnDetailDTO.setStatus(false);
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage("监测点固件核心id:monitoringPointCoreId不能为空！");
            return  returnDetailDTO;
        }
        try {
            returnDetailDTO=ynMonitoringInitService.deleteMonitoring(monitoringObjectDTO, appLoginUser);
        }catch (YnTransactionalException e) {
            returnDetailDTO.setStatus(e.isState());
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage(e.getErrorMsg());
            return returnDetailDTO;
        }
        return returnDetailDTO;
    }
    //修改计量点状态
    @RequestMapping(value="/updateMonitoringMStatus",method = RequestMethod.GET)
    public DetailDTO updateMonitoringMStatus(HttpServletRequest request,HttpServletResponse response, MonitoringObjectDTO monitoringObjectDTO){
        DetailDTO returnDetailDTO= new DetailDTO(true);
        LoginAppDTO appLoginUser= new LoginAppDTO();//获取app登录用户信息
        String monitoringPassword=super.monitoringPassword;
        DesEncrypt desEncrypt=new DesEncrypt();
        if(monitoringObjectDTO.getMonitoringPassword()==null||monitoringObjectDTO.getMonitoringPassword().equals("")){
            returnDetailDTO.setStatus(false);
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage("monitoringPassword计量密码调用时候自带密码校验字符串不能为空！");
            return  returnDetailDTO;
        }
        String monitoringPasswordStr= null;
        try {
            monitoringPasswordStr = desEncrypt.decrypt(java.net.URLDecoder.decode(monitoringObjectDTO.getMonitoringPassword(),"utf-8") );
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
        if(monitoringObjectDTO.getMonitoringPointCoreId()==null||monitoringObjectDTO.getMonitoringPointCoreId().equals("")){
            returnDetailDTO.setStatus(false);
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage("监测点固件核心id:monitoringPointCoreId不能为空！");
            return  returnDetailDTO;
        }
        if(monitoringObjectDTO.getmStatus()==null||monitoringObjectDTO.getmStatus().equals("")){
            returnDetailDTO.setStatus(false);
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage("监测点状态:mStatus不能为空！【0】未生效，【1】为已生效，【-1】为通信异常，【-2】为逻辑删除");
            return  returnDetailDTO;
        }
        try {
            returnDetailDTO=ynMonitoringInitService.deleteMonitoring(monitoringObjectDTO, appLoginUser);
        }catch (YnTransactionalException e) {
            returnDetailDTO.setStatus(e.isState());
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage(e.getErrorMsg());
            return returnDetailDTO;
        }
        return returnDetailDTO;
    }

    //获取通讯规约的所有枚举
    @RequestMapping(value="/selectMonitoringProtocol",method = RequestMethod.GET)
    public DetailDTO selectMonitoringProtocol(HttpServletRequest request,HttpServletResponse response, OnLineMonitoringDTO onLineMonitoringDTO){
        DetailDTO returnDetailDTO= new DetailDTO(true);
        LoginAppDTO appLoginUser= new LoginAppDTO();//获取app登录用户信息
        String monitoringPassword=super.monitoringPassword;
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
            returnDetailDTO=ynMonitoringInitService.selectMonitoringProtocol(onLineMonitoringDTO, appLoginUser);
        }catch (YnTransactionalException e) {
            returnDetailDTO.setStatus(e.isState());
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage(e.getErrorMsg());
            return returnDetailDTO;
        }
        return returnDetailDTO;
    }

    //获取计量点状态的枚举
    @RequestMapping(value="/selectMonitoringMStatus",method = RequestMethod.GET)
    public DetailDTO selectMonitoringMStatus(HttpServletRequest request,HttpServletResponse response, OnLineMonitoringDTO onLineMonitoringDTO){
        DetailDTO returnDetailDTO= new DetailDTO(true);
        LoginAppDTO appLoginUser= new LoginAppDTO();//获取app登录用户信息
        String monitoringPassword=super.monitoringPassword;
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
            returnDetailDTO=ynMonitoringInitService.selectMonitoringMStatus(onLineMonitoringDTO, appLoginUser);
        }catch (YnTransactionalException e) {
            returnDetailDTO.setStatus(e.isState());
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage(e.getErrorMsg());
            return returnDetailDTO;
        }
        return returnDetailDTO;
    }


   //获取到配置表的信息
    @RequestMapping(value="/selectWarningConfigByObject",method = RequestMethod.GET)
    public DetailDTO selectWarningConfigByObject(HttpServletRequest request,HttpServletResponse response, MonitoringObjectDTO monitoringObjectDTO){
        DetailDTO returnDetailDTO= new DetailDTO(true);
        LoginAppDTO appLoginUser= new LoginAppDTO();//获取app登录用户信息
        String monitoringPassword=super.monitoringPassword;
        DesEncrypt desEncrypt=new DesEncrypt();
        if(monitoringObjectDTO.getMonitoringPassword()==null||monitoringObjectDTO.getMonitoringPassword().equals("")){
            returnDetailDTO.setStatus(false);
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage("monitoringPassword计量密码调用时候自带密码校验字符串不能为空！");
            return  returnDetailDTO;
        }
        String monitoringPasswordStr= null;
        try {
            monitoringPasswordStr = desEncrypt.decrypt(java.net.URLDecoder.decode(monitoringObjectDTO.getMonitoringPassword(),"utf-8") );
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
        if(monitoringObjectDTO.getMonitoringPointAliasId()==null||monitoringObjectDTO.getMonitoringPointAliasId().equals("")){
            returnDetailDTO.setStatus(false);
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage("monitoringPointAliasId计量点自定义id不能为空！");
            return  returnDetailDTO;
        }
        try {
            returnDetailDTO=ynMonitoringInitService.selectWarningConfigByObject(monitoringObjectDTO, appLoginUser);
        }catch (YnTransactionalException e) {
            returnDetailDTO.setStatus(e.isState());
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage(e.getErrorMsg());
            return returnDetailDTO;
        }
        return returnDetailDTO;
    }


    //告警配置表修改
    @RequestMapping(value="/updateWarningConfig",method = RequestMethod.POST)
    public DetailDTO updateWarningConfig(HttpServletRequest request,HttpServletResponse response,@RequestBody BigWarningConfigDTO bigWwarningConfigDTO){
        DetailDTO returnDetailDTO= new DetailDTO(true);
        LoginAppDTO appLoginUser= new LoginAppDTO();//获取app登录用户信息
        String monitoringPassword=super.monitoringPassword;
        DesEncrypt desEncrypt=new DesEncrypt();
        if(bigWwarningConfigDTO.getMonitoringPassword()==null||bigWwarningConfigDTO.getMonitoringPassword().equals("")){
            returnDetailDTO.setStatus(false);
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage("monitoringPassword计量密码调用时候自带密码校验字符串不能为空！");
            return  returnDetailDTO;
        }
        String monitoringPasswordStr= null;
        try {
            monitoringPasswordStr = desEncrypt.decrypt(java.net.URLDecoder.decode(bigWwarningConfigDTO.getMonitoringPassword(),"utf-8") );
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
            returnDetailDTO=ynMonitoringInitService.updateWarningConfig(bigWwarningConfigDTO, appLoginUser);
        }catch (YnTransactionalException e) {
            returnDetailDTO.setStatus(e.isState());
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage(e.getErrorMsg());
            return returnDetailDTO;
        }
        return returnDetailDTO;
    }

    //根据用电企业id查询dtu信息
    @RequestMapping(value="/selectDTUMsgByEterpriseId",method = RequestMethod.GET)
    public DetailDTO selectDTUMsgByEterpriseId(HttpServletRequest request,HttpServletResponse response, MonitoringObjectDTO monitoringObjectDTO){
        DetailDTO returnDetailDTO= new DetailDTO(true);
        LoginAppDTO appLoginUser= new LoginAppDTO();//获取app登录用户信息
        String monitoringPassword=super.monitoringPassword;
        DesEncrypt desEncrypt=new DesEncrypt();
        if(monitoringObjectDTO.getMonitoringPassword()==null||monitoringObjectDTO.getMonitoringPassword().equals("")){
            returnDetailDTO.setStatus(false);
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage("monitoringPassword计量密码调用时候自带密码校验字符串不能为空！");
            return  returnDetailDTO;
        }
        String monitoringPasswordStr= null;
        try {
            monitoringPasswordStr = desEncrypt.decrypt(java.net.URLDecoder.decode(monitoringObjectDTO.getMonitoringPassword(),"utf-8") );
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
        if(monitoringObjectDTO.getYnBaseCustomerEnterpriseLongId()==null||monitoringObjectDTO.getYnBaseCustomerEnterpriseLongId().equals("")){
            returnDetailDTO.setStatus(false);
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage("ynBaseCustomerEnterpriseLongId用电企业整形id不能为空！");
            return  returnDetailDTO;
        }
        try {
            returnDetailDTO=ynMonitoringInitService.selectDTUMsgByEterpriseId(monitoringObjectDTO, appLoginUser);
        }catch (YnTransactionalException e) {
            returnDetailDTO.setStatus(e.isState());
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage(e.getErrorMsg());
            return returnDetailDTO;
        }
        return returnDetailDTO;
    }

    //monitoringPointType==1||monitoringPointType==11||monitoringPointType==12||monitoringPointType==14表示单相
    //monitoringPointType==3||monitoringPointType==13
    //根据用电企业id或者电房id或者设备id或者计量点id查询指定时间年份或者月份的数据 for 小李pc
    @RequestMapping(value="/selectOperationDateByEnterpriseOrSubstationOrEquipmentOrMonitoring",method = RequestMethod.POST)
    public DetailDTO selectOperationDateByEnterpriseOrSubstationOrEquipmentOrMonitoring(HttpServletRequest request,HttpServletResponse response,@RequestBody OnLineMonitoringDTO onLineMonitoringDTO){
        DetailDTO returnDetailDTO= new DetailDTO(true);
        LoginAppDTO appLoginUser= new LoginAppDTO();//获取app登录用户信息

        String monitoringPassword=super.monitoringPassword;
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
        if(onLineMonitoringDTO.getTag()==null||onLineMonitoringDTO.getTag().equals("")) {
            returnDetailDTO.setStatus(false);
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage("getTag不能为空,1计量点,2用电企业，3维保公司，4电房！所属类型对应会相对应的id");
            return  returnDetailDTO;
        }
    /*    if(onLineMonitoringDTO.getYnBaseCustomerEnterpriseLongId()==null||onLineMonitoringDTO.getYnBaseCustomerEnterpriseLongId().equals("")) {
            returnDetailDTO.setStatus(false);
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage("用电企业整形id:ynBaseCustomerEnterpriseLongId不能为空！");
            return  returnDetailDTO;
        }*/
        if(onLineMonitoringDTO.getYear()==null||onLineMonitoringDTO.getYear().equals("")){
            returnDetailDTO.setStatus(false);
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage("年year不能为空！");
            return  returnDetailDTO;
        }
        if(onLineMonitoringDTO.getYnBaseEquipmentLongId()!=null&&!onLineMonitoringDTO.getYnBaseEquipmentLongId().equals("")){
            if(onLineMonitoringDTO.getYnBaseEnterpriseSubstationLongId()==null||onLineMonitoringDTO.getYnBaseEnterpriseSubstationLongId().equals("")){
                returnDetailDTO.setStatus(false);
                returnDetailDTO.setErrorCode("-1");
                returnDetailDTO.setErrorMessage("如果是按照设备查询计量点的话，电房的整形id必填！");
                return  returnDetailDTO;
            }
        }
        try {
            returnDetailDTO=ynMonitoringInitService.selectOperationDateByEnterpriseOrSubstationOrEquipmentOrMonitoring(request,response,onLineMonitoringDTO, appLoginUser);
        }catch (YnTransactionalException e) {
            returnDetailDTO.setStatus(e.isState());
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage(e.getErrorMsg());
            return returnDetailDTO;
        }
        return returnDetailDTO;
    }
    //1计量点,2用电企业，3维保公司，4电房
    //monitoringPointType==1||monitoringPointType==11||monitoringPointType==12||monitoringPointType==14表示单相
    //monitoringPointType==3||monitoringPointType==13
    //根据用电企业id或者电房id或者设备id或者计量点id查询指定时间年份或者月份的数据 for 小李pc GET for测试
    @RequestMapping(value="/selectOperationDateByEnterpriseOrSubstationOrEquipmentOrMonitoring",method = RequestMethod.GET)
    public DetailDTO selectOperationDateByEnterpriseOrSubstationOrEquipmentOrMonitoringGET(HttpServletRequest request,HttpServletResponse response,OnLineMonitoringDTO onLineMonitoringDTO){
        DetailDTO returnDetailDTO= new DetailDTO(true);
        LoginAppDTO appLoginUser= new LoginAppDTO();//获取app登录用户信息

        String monitoringPassword=super.monitoringPassword;
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
        if(onLineMonitoringDTO.getTag()==null||onLineMonitoringDTO.getTag().equals("")) {
            returnDetailDTO.setStatus(false);
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage("getTag不能为空,1计量点,2用电企业，3维保公司，4电房！所属类型对应会相对应的id");
            return  returnDetailDTO;
        }
    /*    if(onLineMonitoringDTO.getYnBaseCustomerEnterpriseLongId()==null||onLineMonitoringDTO.getYnBaseCustomerEnterpriseLongId().equals("")) {
            returnDetailDTO.setStatus(false);
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage("用电企业整形id:ynBaseCustomerEnterpriseLongId不能为空！");
            return  returnDetailDTO;
        }*/
        if(onLineMonitoringDTO.getYear()==null||onLineMonitoringDTO.getYear().equals("")){
            returnDetailDTO.setStatus(false);
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage("年year不能为空！");
            return  returnDetailDTO;
        }
        if(onLineMonitoringDTO.getYnBaseEquipmentLongId()!=null&&!onLineMonitoringDTO.getYnBaseEquipmentLongId().equals("")){
            if(onLineMonitoringDTO.getYnBaseEnterpriseSubstationLongId()==null||onLineMonitoringDTO.getYnBaseEnterpriseSubstationLongId().equals("")){
                returnDetailDTO.setStatus(false);
                returnDetailDTO.setErrorCode("-1");
                returnDetailDTO.setErrorMessage("如果是按照设备查询计量点的话，电房的整形id必填！");
                return  returnDetailDTO;
            }
        }
        try {
            returnDetailDTO=ynMonitoringInitService.selectOperationDateByEnterpriseOrSubstationOrEquipmentOrMonitoring(request,response,onLineMonitoringDTO, appLoginUser);
        }catch (YnTransactionalException e) {
            returnDetailDTO.setStatus(e.isState());
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage(e.getErrorMsg());
            return returnDetailDTO;
        }
        return returnDetailDTO;
    }

//查询根据维保公司查询该维保公司的所有计量企业以及中计量统计
    @RequestMapping(value="/selectCountMonitringAndEnterprise",method = RequestMethod.GET)
    public DetailDTO selectCountMonitringAndEnterprise(HttpServletRequest request,HttpServletResponse response, MonitoringObjectDTO monitoringObjectDTO,@RequestParam  String serviceAreas){
        DetailDTO returnDetailDTO= new DetailDTO(true);
        LoginAppDTO appLoginUser= new LoginAppDTO();//获取app登录用户信息
        String monitoringPassword=super.monitoringPassword;
        DesEncrypt desEncrypt=new DesEncrypt();
        if(monitoringObjectDTO.getMonitoringPassword()==null||monitoringObjectDTO.getMonitoringPassword().equals("")){
            returnDetailDTO.setStatus(false);
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage("monitoringPassword计量密码调用时候自带密码校验字符串不能为空！");
            return  returnDetailDTO;
        }
        String monitoringPasswordStr= null;
        try {
            monitoringPasswordStr = desEncrypt.decrypt(java.net.URLDecoder.decode(monitoringObjectDTO.getMonitoringPassword(),"utf-8") );
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
        if(monitoringObjectDTO.getYnBaseStaffCompanyId()==null||monitoringObjectDTO.getYnBaseStaffCompanyId().equals("")){
            returnDetailDTO.setStatus(false);
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage("ynBaseStaffCompanyId维保公司整形id不能为空！");
            return  returnDetailDTO;
        }
        try {
            returnDetailDTO=ynMonitoringInitService.selectCountMonitringAndEnterprise(monitoringObjectDTO, appLoginUser,toolClass.stringToList(serviceAreas));
        }catch (YnTransactionalException e) {
            returnDetailDTO.setStatus(e.isState());
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage(e.getErrorMsg());
            return returnDetailDTO;
        }
        return returnDetailDTO;
    }
    //查询根据用电企业id查询该用电企业下所有的电房以及计量点统计
    @RequestMapping(value="/selectCountMonitringAndSubstation",method = RequestMethod.GET)
    public DetailDTO selectCountMonitringAndSubstation(HttpServletRequest request,HttpServletResponse response, MonitoringObjectDTO monitoringObjectDTO){
        DetailDTO returnDetailDTO= new DetailDTO(true);
        LoginAppDTO appLoginUser= new LoginAppDTO();//获取app登录用户信息
        String monitoringPassword=super.monitoringPassword;
        DesEncrypt desEncrypt=new DesEncrypt();
        if(monitoringObjectDTO.getMonitoringPassword()==null||monitoringObjectDTO.getMonitoringPassword().equals("")){
            returnDetailDTO.setStatus(false);
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage("monitoringPassword计量密码调用时候自带密码校验字符串不能为空！");
            return  returnDetailDTO;
        }
        String monitoringPasswordStr= null;
        try {
            monitoringPasswordStr = desEncrypt.decrypt(java.net.URLDecoder.decode(monitoringObjectDTO.getMonitoringPassword(),"utf-8") );
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
        if(monitoringObjectDTO.getYnBaseCustomerEnterpriseLongId()==null||monitoringObjectDTO.getYnBaseCustomerEnterpriseLongId().equals("")){
            returnDetailDTO.setStatus(false);
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage("ynBaseCustomerEnterpriseLongId维保公司整形id不能为空！");
            return  returnDetailDTO;
        }
        try {
            returnDetailDTO=ynMonitoringInitService.selectCountMonitringAndSubstation(monitoringObjectDTO, appLoginUser);
        }catch (YnTransactionalException e) {
            returnDetailDTO.setStatus(e.isState());
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage(e.getErrorMsg());
            return returnDetailDTO;
        }
        return returnDetailDTO;
    }
    /**根据用電企業id统计用电企业的总用电量
     todayTotalSUM           今日总电量
     yestodayTotalSUM        昨日总电量
     dayGrowthRateSUM        日环比昨日（数值）
     dayGrowthRateStateSUM   今日环比昨日状态：1表示环比为整数，0表示环比为相等，-1表示环比为负数
     currentMonthTotalSUM    当前月总电量
     LastMonthTotalSUM       上月总电量
     monthGrowthRateSUM      当前月环比上月（数值）
     monthGrowthRateStateSUM 当前月环比上月状态：1表示环比为正数，0表示环比为相等，-1表示环比为负数
     */
    @RequestMapping(value="/selectElectricAnalysisByEnterpriseLongId",method = RequestMethod.GET)
    public DetailDTO selectElectricAnalysisByEnterpriseLongId(HttpServletRequest request,HttpServletResponse response, MonitoringObjectDTO monitoringObjectDTO){
        DetailDTO returnDetailDTO= new DetailDTO(true);
        LoginAppDTO appLoginUser= new LoginAppDTO();//获取app登录用户信息
        String monitoringPassword=super.monitoringPassword;
        DesEncrypt desEncrypt=new DesEncrypt();
        if(monitoringObjectDTO.getMonitoringPassword()==null||monitoringObjectDTO.getMonitoringPassword().equals("")){
            returnDetailDTO.setStatus(false);
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage("monitoringPassword计量密码调用时候自带密码校验字符串不能为空！");
            return  returnDetailDTO;
        }
        String monitoringPasswordStr= null;
        try {
            monitoringPasswordStr = desEncrypt.decrypt(java.net.URLDecoder.decode(monitoringObjectDTO.getMonitoringPassword(),"utf-8") );
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
        if(monitoringObjectDTO.getYnBaseCustomerEnterpriseLongId()==null||monitoringObjectDTO.getYnBaseCustomerEnterpriseLongId().equals("")){
            returnDetailDTO.setStatus(false);
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage("ynBaseCustomerEnterpriseLongId维保公司整形id不能为空！");
            return  returnDetailDTO;
        }
        try {
            returnDetailDTO=ynMonitoringInitService.selectElectricAnalysisByEnterpriseLongId(monitoringObjectDTO, appLoginUser);
        }catch (YnTransactionalException e) {
            returnDetailDTO.setStatus(e.isState());
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage(e.getErrorMsg());
            return returnDetailDTO;
        }
        return returnDetailDTO;
    }


    @RequestMapping(value="/selectSYPower",method = RequestMethod.GET)
    public DetailDTO selectSYPower(HttpServletRequest request,HttpServletResponse response, MonitoringObjectDTO monitoringObjectDTO,String userType){
        DetailDTO returnDetailDTO= new DetailDTO(true);
        LoginAppDTO appLoginUser= new LoginAppDTO();//获取app登录用户信息
        String monitoringPassword=super.monitoringPassword;
        DesEncrypt desEncrypt=new DesEncrypt();
        if(monitoringObjectDTO.getMonitoringPassword()==null||monitoringObjectDTO.getMonitoringPassword().equals("")){
            returnDetailDTO.setStatus(false);
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage("monitoringPassword计量密码调用时候自带密码校验字符串不能为空！");
            return  returnDetailDTO;
        }
        String monitoringPasswordStr= null;
        try {
            monitoringPasswordStr = desEncrypt.decrypt(java.net.URLDecoder.decode(monitoringObjectDTO.getMonitoringPassword(),"utf-8") );
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
        if(monitoringObjectDTO.getYnBaseCustomerEnterpriseLongId()==null||monitoringObjectDTO.getYnBaseCustomerEnterpriseLongId().equals("")){
            returnDetailDTO.setStatus(false);
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage("ynBaseCustomerEnterpriseLongId维保公司整形id不能为空！");
            return  returnDetailDTO;
        }
        try {
            returnDetailDTO=ynMonitoringInitService.selectSYPower(monitoringObjectDTO, appLoginUser,userType);
        }catch (YnTransactionalException e) {
            returnDetailDTO.setStatus(e.isState());
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage(e.getErrorMsg());
            return returnDetailDTO;
        }
        return returnDetailDTO;
    }

    //首页   终端数   总容量   报警折线图数据
    @RequestMapping(value="/selectSYCountAndRL",method = RequestMethod.GET)
    public DetailDTO selectSYCountAndRL(HttpServletRequest request,HttpServletResponse response, MonitoringObjectDTO monitoringObjectDTO,String userType,OnLineMonitoringDTO onLineMonitoringDTO){
        DetailDTO returnDetailDTO= new DetailDTO(true);
        LoginAppDTO appLoginUser= new LoginAppDTO();//获取app登录用户信息
        String monitoringPassword=super.monitoringPassword;
        DesEncrypt desEncrypt=new DesEncrypt();
        if(monitoringObjectDTO.getMonitoringPassword()==null||monitoringObjectDTO.getMonitoringPassword().equals("")){
            returnDetailDTO.setStatus(false);
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage("monitoringPassword计量密码调用时候自带密码校验字符串不能为空！");
            return  returnDetailDTO;
        }
        String monitoringPasswordStr= null;
        try {
            monitoringPasswordStr = desEncrypt.decrypt(java.net.URLDecoder.decode(monitoringObjectDTO.getMonitoringPassword(),"utf-8") );
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
        if(monitoringObjectDTO.getYnBaseCustomerEnterpriseLongId()==null||monitoringObjectDTO.getYnBaseCustomerEnterpriseLongId().equals("")){
            returnDetailDTO.setStatus(false);
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage("ynBaseCustomerEnterpriseLongId维保公司整形id不能为空！");
            return  returnDetailDTO;
        }
        try {
            returnDetailDTO=ynMonitoringInitService.selectSYCountAndRL(monitoringObjectDTO, appLoginUser,userType,onLineMonitoringDTO);
        }catch (YnTransactionalException e) {
            returnDetailDTO.setStatus(e.isState());
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage(e.getErrorMsg());
            return returnDetailDTO;
        }
        return returnDetailDTO;
    }






























}
