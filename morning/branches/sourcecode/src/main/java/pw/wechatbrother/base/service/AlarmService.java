package pw.wechatbrother.base.service;

import pw.wechatbrother.base.dto.DetailDTO;
import pw.wechatbrother.base.dto.LoginAppDTO;
import pw.wechatbrother.base.dto.onlinemonitoring.OnLineMonitoringDTO;

import java.util.List;
import java.util.Map;

public interface AlarmService {
    List<Map> getcompany(int companyId);

    List<Map> getSubstation(int companyId);

    List<Map> getmonitoringInfo(int substationId);

    List<Map> getmessgae(String aliasId );

    String alarmsetting(String data);

    DetailDTO getalarmMessage(OnLineMonitoringDTO onLineMonitoringDTO);

    List<Map> getGJdate(String aliasId);


}
