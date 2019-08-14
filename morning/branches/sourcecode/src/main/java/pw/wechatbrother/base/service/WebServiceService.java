package pw.wechatbrother.base.service;

import net.sf.json.JSONObject;
import org.apache.ibatis.annotations.Param;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by YN on 2018/1/22.
 */
public interface WebServiceService {
    public JSONObject findData(String companyId,String monitorId);
}
