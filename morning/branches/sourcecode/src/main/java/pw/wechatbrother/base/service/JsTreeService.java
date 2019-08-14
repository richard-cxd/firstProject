package pw.wechatbrother.base.service;

import org.apache.ibatis.annotations.Param;
import pw.wechatbrother.base.dto.JsTree;

import java.util.List;
import java.util.Map;

/**
 * Created by YN on 2018/1/23.
 */
public interface JsTreeService {
    List<JsTree> getJsTree(String companyId);

    List<JsTree> findJstreeByEnterprise(String enterpriseId);
}
