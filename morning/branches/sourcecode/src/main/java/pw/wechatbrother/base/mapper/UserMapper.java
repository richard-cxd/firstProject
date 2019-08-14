package pw.wechatbrother.base.mapper;



import pw.wechatbrother.base.domain.UserD;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Resource
public interface UserMapper {

   List<UserD>  select(String userId);
}