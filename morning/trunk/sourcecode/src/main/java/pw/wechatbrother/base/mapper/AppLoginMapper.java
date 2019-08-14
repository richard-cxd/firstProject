package pw.wechatbrother.base.mapper;



import pw.wechatbrother.base.dto.LoginAppDTO;
import pw.wechatbrother.base.dto.onlinemonitoring.OnLineMonitoringDTO;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Resource
public interface AppLoginMapper {


   List<LoginAppDTO> selectUsersByUserName(Map paramsMap);

   int updateLockAndKeyByUserName(LoginAppDTO loginUser);

   List<LoginAppDTO> selectUserByUUID(Map paramsMap);
}