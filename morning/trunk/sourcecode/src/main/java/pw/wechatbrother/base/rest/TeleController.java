package pw.wechatbrother.base.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import pw.wechatbrother.base.controller.BaseController;
import pw.wechatbrother.base.dto.DetailDTO;
import pw.wechatbrother.base.dto.LoginAppDTO;
import pw.wechatbrother.base.dto.onlinemonitoring.OnLineMonitoringDTO;
import pw.wechatbrother.base.service.TeleService;
import pw.wechatbrother.base.utils.exception.YnTransactionalException;
import pw.wechatbrother.base.utils.safety.DesEncrypt;

import java.io.UnsupportedEncodingException;

/**
 * Created by YN on 2018/3/22.
 */
@RestController
@RequestMapping("/api/v1/tele")
public class TeleController  extends BaseController {
        @Autowired
        private TeleService teleService;
    //调用汤总的存储过程，控制开关灯
//开
    @RequestMapping(value="/open",method = RequestMethod.GET)
    public DetailDTO open(String checkMonioringPassword,String controlId){
        DetailDTO returnDetailDTO= new DetailDTO(true);
        LoginAppDTO appLoginUser= new LoginAppDTO();
        DesEncrypt desEncrypt=new DesEncrypt();
        String monitoringPassword=super.monitoringPassword;
        String monitoringPasswordStr= null;
        try {
            monitoringPasswordStr = desEncrypt.decrypt(java.net.URLDecoder.decode(checkMonioringPassword,"utf-8") );
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String monitoringPasswordThis=monitoringPassword;
        if(checkMonioringPassword==null||checkMonioringPassword.equals("")){
            returnDetailDTO.setStatus(false);
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage("monitoringPassword计量密码调用时候自带密码校验字符串不能为空！");
            return  returnDetailDTO;
        }
        if(!monitoringPasswordThis.equals(monitoringPasswordStr)){
            returnDetailDTO.setStatus(false);
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage("调用权限调用失败！");
            return  returnDetailDTO;
        }
        try {
            returnDetailDTO=teleService.open(controlId);
        }catch (YnTransactionalException e) {
            returnDetailDTO.setStatus(e.isState());
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage(e.getErrorMsg());
            return returnDetailDTO;
        }
        return returnDetailDTO;
    }


//关
@RequestMapping(value="/off",method = RequestMethod.GET)
    public DetailDTO off(String checkMonioringPassword,String controlId){
        DetailDTO returnDetailDTO= new DetailDTO(true);
        LoginAppDTO appLoginUser= new LoginAppDTO();
        DesEncrypt desEncrypt=new DesEncrypt();
        String monitoringPassword=super.monitoringPassword;
        String monitoringPasswordStr= null;
        try {
            monitoringPasswordStr = desEncrypt.decrypt(java.net.URLDecoder.decode(checkMonioringPassword,"utf-8") );
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String monitoringPasswordThis=monitoringPassword;
        if(checkMonioringPassword==null||checkMonioringPassword.equals("")){
            returnDetailDTO.setStatus(false);
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage("monitoringPassword计量密码调用时候自带密码校验字符串不能为空！");
            return  returnDetailDTO;
        }
        if(!monitoringPasswordThis.equals(monitoringPasswordStr)){
            returnDetailDTO.setStatus(false);
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage("调用权限调用失败！");
            return  returnDetailDTO;
        }
        try {
            returnDetailDTO=teleService.off(controlId);
        }catch (YnTransactionalException e) {
            returnDetailDTO.setStatus(e.isState());
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage(e.getErrorMsg());
            return returnDetailDTO;
        }
        return returnDetailDTO;
    }


    //app 查询维保公司下的用电企业  yj_kzd
    @RequestMapping(value="/findEnterprise",method = RequestMethod.GET)
    public DetailDTO findEnterprise(String checkMonioringPassword,String zoneid){
        DetailDTO returnDetailDTO= new DetailDTO(true);
        LoginAppDTO appLoginUser= new LoginAppDTO();
        DesEncrypt desEncrypt=new DesEncrypt();
        String monitoringPassword=super.monitoringPassword;
        String monitoringPasswordStr= null;
        try {
            monitoringPasswordStr = desEncrypt.decrypt(java.net.URLDecoder.decode(checkMonioringPassword,"utf-8") );
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String monitoringPasswordThis=monitoringPassword;
        if(checkMonioringPassword==null||checkMonioringPassword.equals("")){
            returnDetailDTO.setStatus(false);
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage("monitoringPassword计量密码调用时候自带密码校验字符串不能为空！");
            return  returnDetailDTO;
        }
        if(!monitoringPasswordThis.equals(monitoringPasswordStr)){
            returnDetailDTO.setStatus(false);
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage("调用权限调用失败！");
            return  returnDetailDTO;
        }
        try {
            returnDetailDTO=teleService.findEnterprise(zoneid);
        }catch (YnTransactionalException e) {
            returnDetailDTO.setStatus(e.isState());
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage(e.getErrorMsg());
            return returnDetailDTO;
        }
        return returnDetailDTO;
    }



    //app 获取这个维保公司控制器开关状态
    @RequestMapping(value="/getControlStatus",method = RequestMethod.GET)
    public DetailDTO getControlStatus(String checkMonioringPassword,String zoneid){
        DetailDTO returnDetailDTO= new DetailDTO(true);
        LoginAppDTO appLoginUser= new LoginAppDTO();
        DesEncrypt desEncrypt=new DesEncrypt();
        String monitoringPassword=super.monitoringPassword;
        String monitoringPasswordStr= null;
        try {
            monitoringPasswordStr = desEncrypt.decrypt(java.net.URLDecoder.decode(checkMonioringPassword,"utf-8") );
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String monitoringPasswordThis=monitoringPassword;
        if(checkMonioringPassword==null||checkMonioringPassword.equals("")){
            returnDetailDTO.setStatus(false);
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage("monitoringPassword计量密码调用时候自带密码校验字符串不能为空！");
            return  returnDetailDTO;
        }
        if(!monitoringPasswordThis.equals(monitoringPasswordStr)){
            returnDetailDTO.setStatus(false);
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage("调用权限调用失败！");
            return  returnDetailDTO;
        }
        try {
            returnDetailDTO=teleService.getControlStatus(zoneid);
        }catch (YnTransactionalException e) {
            returnDetailDTO.setStatus(e.isState());
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage(e.getErrorMsg());
            return returnDetailDTO;
        }
        return returnDetailDTO;
    }



    @RequestMapping(value="/enterpriseDetail",method = RequestMethod.GET)
    public DetailDTO enterpriseDetail(String checkMonioringPassword,String enterpriseId){
        DetailDTO returnDetailDTO= new DetailDTO(true);
        LoginAppDTO appLoginUser= new LoginAppDTO();
        DesEncrypt desEncrypt=new DesEncrypt();
        String monitoringPassword=super.monitoringPassword;
        String monitoringPasswordStr= null;
        try {
            monitoringPasswordStr = desEncrypt.decrypt(java.net.URLDecoder.decode(checkMonioringPassword,"utf-8") );
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String monitoringPasswordThis=monitoringPassword;
        if(checkMonioringPassword==null||checkMonioringPassword.equals("")){
            returnDetailDTO.setStatus(false);
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage("monitoringPassword计量密码调用时候自带密码校验字符串不能为空！");
            return  returnDetailDTO;
        }
        if(!monitoringPasswordThis.equals(monitoringPasswordStr)){
            returnDetailDTO.setStatus(false);
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage("调用权限调用失败！");
            return  returnDetailDTO;
        }
        try {
            returnDetailDTO=teleService.enterpriseDetail(enterpriseId);
        }catch (YnTransactionalException e) {
            returnDetailDTO.setStatus(e.isState());
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage(e.getErrorMsg());
            return returnDetailDTO;
        }
        return returnDetailDTO;
    }


/*    @RequestMapping(value="/test",method = RequestMethod.GET)
    @ResponseBody
    public String test(){
        teleService.test();
        return "success";
    }*/

    //开关挂牌
    @RequestMapping(value="/hangOut",method = RequestMethod.GET)
    public DetailDTO hangOut(String checkMonioringPassword,String controlId){
        DetailDTO returnDetailDTO= new DetailDTO(true);
        LoginAppDTO appLoginUser= new LoginAppDTO();
        DesEncrypt desEncrypt=new DesEncrypt();
        String monitoringPassword=super.monitoringPassword;
        String monitoringPasswordStr= null;
        try {
            monitoringPasswordStr = desEncrypt.decrypt(java.net.URLDecoder.decode(checkMonioringPassword,"utf-8") );
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String monitoringPasswordThis=monitoringPassword;
        if(checkMonioringPassword==null||checkMonioringPassword.equals("")){
            returnDetailDTO.setStatus(false);
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage("monitoringPassword计量密码调用时候自带密码校验字符串不能为空！");
            return  returnDetailDTO;
        }
        if(!monitoringPasswordThis.equals(monitoringPasswordStr)){
            returnDetailDTO.setStatus(false);
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage("调用权限调用失败！");
            return  returnDetailDTO;
        }
        try {
            returnDetailDTO=teleService.hangOut(controlId);
        }catch (YnTransactionalException e) {
            returnDetailDTO.setStatus(e.isState());
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage(e.getErrorMsg());
            return returnDetailDTO;
        }
        return returnDetailDTO;
    }


    //开关取消挂牌
    @RequestMapping(value="/cancelHangOut",method = RequestMethod.GET)
    public DetailDTO cancelHangOut(String checkMonioringPassword,String controlId){
        DetailDTO returnDetailDTO= new DetailDTO(true);
        LoginAppDTO appLoginUser= new LoginAppDTO();
        DesEncrypt desEncrypt=new DesEncrypt();
        String monitoringPassword=super.monitoringPassword;
        String monitoringPasswordStr= null;
        try {
            monitoringPasswordStr = desEncrypt.decrypt(java.net.URLDecoder.decode(checkMonioringPassword,"utf-8") );
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String monitoringPasswordThis=monitoringPassword;
        if(checkMonioringPassword==null||checkMonioringPassword.equals("")){
            returnDetailDTO.setStatus(false);
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage("monitoringPassword计量密码调用时候自带密码校验字符串不能为空！");
            return  returnDetailDTO;
        }
        if(!monitoringPasswordThis.equals(monitoringPasswordStr)){
            returnDetailDTO.setStatus(false);
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage("调用权限调用失败！");
            return  returnDetailDTO;
        }
        try {
            returnDetailDTO=teleService.cancelHangOut(controlId);
        }catch (YnTransactionalException e) {
            returnDetailDTO.setStatus(e.isState());
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage(e.getErrorMsg());
            return returnDetailDTO;
        }
        return returnDetailDTO;
    }
}
