package pw.wechatbrother.base.rest;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import pw.wechatbrother.base.controller.BaseController;
import pw.wechatbrother.base.dto.DetailDTO;
import pw.wechatbrother.base.dto.LoginAppDTO;
import pw.wechatbrother.base.dto.onlinemonitoring.OnLineMonitoringDTO;
import pw.wechatbrother.base.service.AppLoginService;
import pw.wechatbrother.base.service.YnBaseService;
import pw.wechatbrother.base.utils.JavaUUIDGenerator;
import pw.wechatbrother.base.utils.exception.YnTransactionalException;
import pw.wechatbrother.base.utils.safety.DesEncrypt;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** 公用的app总模块
 * Created by zhengjingli on 2017/6/12
 */
@RestController
@RequestMapping("/api/v1/appLogin")
public class AppLoginRestController extends BaseController {
    @Autowired
    private AppLoginService appLoginService;


    SimpleDateFormat formatterYMD = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat formatterYMDHms = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    //登录获取token
    @RequestMapping(value="/login",method = RequestMethod.POST)
    public DetailDTO login(HttpServletRequest request,HttpServletResponse response,@RequestBody LoginAppDTO loginAppDTO) throws UnsupportedEncodingException {
        DetailDTO returnDetailDTO= new DetailDTO(true);
        // LoginAppDTO appLoginUser= (LoginAppDTO) request.getAttribute("appLoginUser");//获取app登录用户信息
        if(loginAppDTO.getUserName()==null||loginAppDTO.getUserName().equals("")||loginAppDTO.getPassword()==null||loginAppDTO.getPassword().equals("")){
            returnDetailDTO.setStatus(false);
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage("请输入用户名或密码！");
            return returnDetailDTO;
        }
        Map paramsMap = new HashMap();
        paramsMap.put("userName",loginAppDTO.getUserName());
        List<LoginAppDTO> userList=appLoginService.selectUsersByUserName(paramsMap);
        if(userList==null||userList.size()!=1){
            returnDetailDTO.setStatus(false);
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage("用户名密码不正确，如有疑问请与平台管理员联系！");
            return returnDetailDTO;
        }else{
            LoginAppDTO loginUser=userList.get(0);
            if(loginUser.getPassword().equals(loginAppDTO.getPassword())){//用户名密码一致
                String lockStr= JavaUUIDGenerator.getUUID();//拿到锁的字符串
                String keyStr=JavaUUIDGenerator.getUUID();//拿到钥匙的字符串
                //String
                DesEncrypt desEncrypt= new DesEncrypt();
                String lockEncry=new DesEncrypt(keyStr).encrypt(lockStr);//加密之后的字符串
                String keyEncry=new DesEncrypt().encrypt(keyStr);//加密之后的钥匙
                loginUser.setLock(lockEncry);
                loginUser.setKey(keyEncry);
                loginUser.setLastLoginTime(formatterYMDHms.format(new Date()));
                int i = appLoginService.updateLockAndKeyByUserName(loginUser);
                Map returnMap = new HashMap();
                //returnMap.put("token",keyEncry);
                returnMap.put("token",keyStr);
                returnDetailDTO.setDetail(returnMap);
            }else{
                returnDetailDTO.setStatus(false);
                returnDetailDTO.setErrorCode("-1");
                returnDetailDTO.setErrorMessage("用户名密码不正确，如有疑问请与平台管理员联系！");
                return returnDetailDTO;
            }


        }
        /*try {
            returnDetailDTO=appLoginService.selectSubstationMsgByEnterpriseId(onLineMonitoringDTO, appLoginUser);
        }catch (YnTransactionalException e) {
            returnDetailDTO.setStatus(e.isState());
            returnDetailDTO.setErrorCode("-1");
            returnDetailDTO.setErrorMessage(e.getErrorMsg());
            return returnDetailDTO;
        }*/
        return returnDetailDTO;
    }




}
