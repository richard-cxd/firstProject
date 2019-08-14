package pw.wechatbrother.base.service.impl;


import org.springframework.stereotype.Service;
import pw.wechatbrother.base.domain.UserD;
import pw.wechatbrother.base.mapper.UserMapper;
import pw.wechatbrother.base.service.UserService;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    @Resource
    UserMapper userMapper;


    public List<UserD> select(String userId) {
        // System.out.print("bbbb===" + userId);
        List<UserD> user =userMapper.select(userId);

                return user;
    }

}
