package pw.wechatbrother.base.service;


import pw.wechatbrother.base.dto.DetailDTO;
import pw.wechatbrother.base.dto.LoginAppDTO;
import pw.wechatbrother.base.dto.onlinemonitoring.OnLineMonitoringDTO;

import java.util.List;
import java.util.Map;


public interface AppLoginService {
    List<LoginAppDTO> selectUsersByUserName(Map paramsMap);

    int updateLockAndKeyByUserName(LoginAppDTO loginUser);

    List<LoginAppDTO> selectUserByUUID(Map paramsMap);
}
