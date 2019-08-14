package pw.wechatbrother.base.mapper;

import javax.annotation.Resource;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by YN on 2018/3/22.
 */
@Resource
public interface TeleMapper {

    //通过控制器回路id查询控制开关需要的数据
    List<LinkedHashMap> findKzdById(String controlId);

    //调用汤总存储过程控制开关
    List<LinkedHashMap> open(Map map);
    List<LinkedHashMap> off(Map map);

    //查询维保公司下的用电企业  yj_kzd
    List<LinkedHashMap> findEnterprise(String zoneid);

    //点进用电企业查看详情(开关数，控制开关)
    List<LinkedHashMap> enterpriseDetail(String enterpriseId);

    //查控制的状态
    List<LinkedHashMap> getControlStatus(String zoneid);

    List<LinkedHashMap> hangOut(String controlId);

    List<LinkedHashMap> cancelHangOut(String controlId);


/*    void test();

    Map selectId();*/
}
