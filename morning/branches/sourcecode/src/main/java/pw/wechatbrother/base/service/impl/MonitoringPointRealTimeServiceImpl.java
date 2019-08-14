package pw.wechatbrother.base.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pw.wechatbrother.base.dto.DetailDTO;
import pw.wechatbrother.base.mapper.MonitoringPointRealTimeMapper;
import pw.wechatbrother.base.service.MonitoringPointRealTimeService;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by YN on 2018/3/30.
 */
@Service
public class MonitoringPointRealTimeServiceImpl implements MonitoringPointRealTimeService {
    @Autowired
    private MonitoringPointRealTimeMapper monitoringPointRealTimeMapper;

    //企业版拓扑监测点实时数据
    @Override
    public DetailDTO getMonitoringPointRealTime(String zoneid) {
        DetailDTO detailDTO=new DetailDTO(true);
        List<LinkedHashMap> list = monitoringPointRealTimeMapper.getRealTimeByZoneId(zoneid);
        detailDTO.setDetail(list);
        return detailDTO;
    }

    @Override
    public DetailDTO findMonitoringDetails(String monitoringId) {
        DetailDTO detailDTO=new DetailDTO(true);
        List<LinkedHashMap> list = monitoringPointRealTimeMapper.findMonitoringDetails(monitoringId);
        detailDTO.setDetail(list);
        return detailDTO;
    }
}
