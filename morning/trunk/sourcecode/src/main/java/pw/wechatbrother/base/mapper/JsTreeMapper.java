package pw.wechatbrother.base.mapper;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * Created by YN on 2018/1/22.
 */
@Component
public interface JsTreeMapper {
    //查询维保公司下面的用电企业的电房下的监测点 jsTree
    List<Map> findMonitoringAndElectrical(Map maps);
    List<Map> findAllMonitoringAndElectrical(Map maps);
    List<Map> findHNMonitoringAndElectrical(Map maps);
    List<Map> findElectricalAndEnterprise(Map maps);
    List<Map> findAllElectricalAndEnterprise(Map maps);
    List<Map> findHNElectricalAndEnterprise(Map maps);
    List<Map> findEnterpriseNode(Map maps);
    List<Map> findAllEnterpriseNode(Map maps);
    List<Map> findHNEnterpriseNode(Map maps);


    //查询用电企业的电房下的监测点 jsTree
    List<Map> findMonitoringAndElectricalByEnterprise(@Param("enterpriseId")String enterpriseId);
    List<Map> findElectricalAndEnterpriseByEnterprise(@Param("enterpriseId")String enterpriseId);
    List<Map> findEnterpriseNodeByEnterprise(@Param("enterpriseId")String enterpriseId);





}
