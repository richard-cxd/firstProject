package pw.wechatbrother.base.service;

import pw.wechatbrother.base.dto.DetailDTO;

/**
 * Created by YN on 2018/3/30.
 */
public interface MonitoringPointRealTimeService {
    //企业版拓扑监测点实时数据
    public DetailDTO getMonitoringPointRealTime(String zoneid);
    public DetailDTO findMonitoringDetails(String monitoringId);

}
