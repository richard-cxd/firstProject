package pw.wechatbrother.base.utils.safety;


import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import pw.wechatbrother.base.dto.DetailDTO;
import pw.wechatbrother.base.dto.LoginAppDTO;
import pw.wechatbrother.base.service.AppLoginService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zjl on 2016/11/27.
 */
public class AppTokenInteceptor implements HandlerInterceptor {

    @Autowired
    AppLoginService appLoginService;

    SimpleDateFormat formatterYMD = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat formatterYMDHms = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=utf-8");
        //正式app用
        String token = (String) request.getParameter("token");
//        String token = request.getHeader("token");
//        String tokenkey = request.getHeader("tokenKey");
//        String lock = request.getHeader("lock");
//        String lockkey = request.getHeader("lockKey");
//        String sourceType = request.getHeader("sourceType");//wechat 表示微信， app 表示移动端
        DetailDTO detailDTO = verifierToken(token);
        if (!detailDTO.getStatus()) {
            String outString =Utils.objectToJson(detailDTO);
            System.out.println("outString="+outString);
            response.getWriter().write(outString);
            // response.getWriter().print(detailDTO);
            return false;
        } else {
            LoginAppDTO loginAppDTO = (LoginAppDTO) detailDTO.getDetail();
            request.setAttribute("userName", loginAppDTO.getUserName());
            request.setAttribute("lock", loginAppDTO.getLock());
            request.setAttribute("appLoginUser", loginAppDTO);
            return true;
        }
    }


    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object o, ModelAndView modelAndView) throws Exception {

    }


    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object o, Exception e) throws Exception {

    }

    /**
     * @param token    通过tokentkey加密的token
     * @return
     */
    private DetailDTO verifierToken(String token) {
        DetailDTO detailDTO = new DetailDTO(true);
        LoginAppDTO returnUser = new LoginAppDTO();
        detailDTO.setErrorCode("1");
        if (StringUtils.isBlank(token) ) {
            detailDTO.setStatus(false);
            detailDTO.setErrorCode("-1");
            detailDTO.setErrorMessage("前台token不能为空！");
            return detailDTO;
        }
        Map paramsMap =new HashMap();
        String keyEncry=new DesEncrypt().encrypt(token);//加密之后的钥匙
        System.out.println(keyEncry);
        paramsMap.put("uuid",new DesEncrypt().encrypt(token));//暂时判定拿到的是key作为token
        List<LoginAppDTO> listUser=appLoginService.selectUserByUUID(paramsMap);
        if(listUser==null||listUser.size()!=1){//查询判断用户是否存在，如果不存在或者数据没有有且只有一条的话报错
            detailDTO.setStatus(false);
            detailDTO.setErrorCode("-1");
            detailDTO.setErrorMessage("用户异常请联系平台管理员！");
            return detailDTO;
        }else{
            LoginAppDTO loginAppDTO=listUser.get(0);
            try {
                long lastLoginTimeLong=formatterYMDHms.parse(loginAppDTO.getLastLoginTime()).getTime();//把最后一次时间转换为毫秒数
                long timeDiff = System.currentTimeMillis() - lastLoginTimeLong;
                if (Math.abs(timeDiff) > 7200000) { //两个小时内有效
                    detailDTO.setStatus(false);
                    detailDTO.setErrorCode("-1");
                    detailDTO.setErrorMessage("url已失效！");
                    return detailDTO;
                }
                loginAppDTO.setPassword(null);//如果校验成功把当前密码置为null
                detailDTO.setDetail(loginAppDTO);//如果校验成功把当前登录信息返回去

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return detailDTO;
    }

    private static String charAt(String username, int index) {
        return String.valueOf(username.charAt(index));
    }

}
