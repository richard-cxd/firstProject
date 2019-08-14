package pw.wechatbrother.base.mapper;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by YN on 2018/1/22.
 */
@Resource
public interface WebServiceMapper {
    String findLastDataTag(@Param("idJCD")String idJCD);
    String findMaxDataData(@Param("companyId")String companyId,@Param("idJCD")String idJCD);
    List<HashMap> findData(@Param("AX") String AX,@Param("BX") String BX,@Param("CX") String CX,@Param("companyId")String companyId,@Param("idJCD")String idJCD);
}
