package pw.wechatbrother.base.service.impl;


import org.springframework.stereotype.Service;
import pw.wechatbrother.base.dto.DetailDTO;
import pw.wechatbrother.base.dto.LoginAppDTO;
import pw.wechatbrother.base.dto.onlinemonitoring.OnLineMonitoringDTO;
import pw.wechatbrother.base.mapper.AppLoginMapper;
import pw.wechatbrother.base.mapper.YnBaseMapper;
import pw.wechatbrother.base.service.AppLoginService;
import pw.wechatbrother.base.service.YnBaseService;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AppLoginServiceImpl implements AppLoginService {
    @Resource
    AppLoginMapper appLoginMapper;

    public List<LoginAppDTO> selectUsersByUserName(Map paramsMap) {
        List<LoginAppDTO> userList=appLoginMapper.selectUsersByUserName(paramsMap);
        return userList;
    }

    public int updateLockAndKeyByUserName(LoginAppDTO loginUser) {
        return appLoginMapper.updateLockAndKeyByUserName(loginUser);
    }

    public List<LoginAppDTO> selectUserByUUID(Map paramsMap) {
        return appLoginMapper.selectUserByUUID(paramsMap);
    }
}
