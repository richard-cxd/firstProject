package pw.wechatbrother.base.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import pw.wechatbrother.base.controller.BaseController;
import pw.wechatbrother.base.dto.DetailDTO;
import pw.wechatbrother.base.dto.LoginAppDTO;
import pw.wechatbrother.base.service.MonitoringPointRealTimeService;
import pw.wechatbrother.base.utils.exception.YnTransactionalException;
import pw.wechatbrother.base.utils.safety.DesEncrypt;

import java.io.UnsupportedEncodingException;

/**
 * Created by YN on 2018/3/30.
 */
//企业版拓扑监测点实时数据
@RestController
@RequestMapping("/monitoringPointRealTime")
public class MonitoringPointRealTimeController extends BaseController {
    @Autowired
    private MonitoringPointRealTimeService monitoringPointRealTimeService;

    //pc
    //调用汤总存储过程获取该维保公司的所有监测点信息
    @RequestMapping(value="/getMonitoringPointRealTime",method = RequestMethod.GET)
    public DetailDTO getMonitoringPointRealTime(String checkMonioringPassword,String zoneid){
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
            returnDetailDTO=monitoringPointRealTimeService.getMonitoringPointRealTime(zoneid);
        }catch (YnTransactionalException e) {
            returnDetailDTO.setStatus(e.isState());
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage(e.getErrorMsg());
            return returnDetailDTO;
        }
        return returnDetailDTO;
    }

    //跳到监测点的详情页面，先查询出  monitoringPointAliasId=&ynBaseEquipmentLongId=&ynBaseEnterpriseSubstationLongId=&tag=
    @RequestMapping(value="/findMonitoringDetails")
    public DetailDTO findMonitoringDetails(String checkMonioringPassword,String monitoringId){
        DetailDTO returnDetailDTO= new DetailDTO(true);
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
            returnDetailDTO=monitoringPointRealTimeService.findMonitoringDetails(monitoringId);
        }catch (YnTransactionalException e) {
            returnDetailDTO.setStatus(e.isState());
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage(e.getErrorMsg());
            return returnDetailDTO;
        }
        return returnDetailDTO;
    }
}
