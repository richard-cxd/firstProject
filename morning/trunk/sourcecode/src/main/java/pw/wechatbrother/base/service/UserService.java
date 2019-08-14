package pw.wechatbrother.base.service;


import pw.wechatbrother.base.domain.UserD;

import java.util.List;
import java.util.Map;


public interface UserService {

    public List<UserD>  select(String userId);

}
