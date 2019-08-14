package pw.wechatbrother.base.dto.onlinemonitoring;

/**
 * 新增或者修改计量点是做的辅助对象
 * Created by zhengjingli on 2017/7/5.
 */
public class MonitoringObjectDTO {
    private String monitoringPassword;//计量密码调用时候自带密码校验
    private String monitoringPointAliasId          ;// monitoringPointAliasId           平台自定义id
    private String monitoringPointCoreId           ;// monitoringPointCoreId            监测点固件核心id
    private String monitoringPointName             ;// monitoringPointName              监测点名称
    private String monitoringPointType             ;// monitoringPointType              监测点类型 1单相，3三相，4四项，11温度，12湿度 （小于10就是基本电参量，大于10的就是环境参量）
    private String ynBaseEquipmentLongId             ;//ynBaseEquipmentLongId               设备整形id
    private String ynBaseEquipmentId                 ;//ynBaseEquipmentId                   设备id
    private String ynBaseEquipmentName               ;//ynBaseEquipmentName                 设备名称
    private String ynBaseEnterpriseSubstationLongId  ;//ynBaseEnterpriseSubstationLongId    电房整形id
    private String ynBaseEnterpriseSubstationId      ;//ynBaseEnterpriseSubstationId        电房id
    private String ynBaseEnterpriseSubstationName    ;//ynBaseEnterpriseSubstationName      电房名称
    private String ynBaseCustomerEnterpriseLongId    ;//ynBaseCustomerEnterpriseLongId      用电企业整形id
    private String ynBaseCustomerEnterpriseId        ;//ynBaseCustomerEnterpriseId          用电企业id
    private String ynBaseCustomerEnterpriseName      ;//ynBaseCustomerEnterpriseName        用电企业名称
    private String ynBaseStaffCompanyId              ;//ynBaseStaffCompanyId                维保公司id
    private String ynBaseStaffCompanyName            ;//ynBaseStaffCompanyName              维保公司名称
    private String monitoringPointLogicAddr         ;//monitoringPointLogicAddr            监测点逻辑地址
    private String monitoringSimCardNo               ;//monitoringSimCardNo                 监测点sim卡号码(也就是DTU镶嵌的手机卡手机号码)
    private String monitoringSimCardInnerNo          ;//monitoringSimCardInnerNo           监测点sim序列号(也就是DTU本身的机器编码)
    private String monitoringSimCardInnerName          ;//monitoringSimCardInnerName      监测点sim名称(也就是DTU本身的机名称)

    private String monitoringPt                        ;//监测点PT
    private String monitoringCt                        ;//监测点CT
    private String monitoringProtocolId               ;//通讯规约表主键
    private String dateString;
    //用于运行报告输出
    private String divisor="1";//单位除数 表示的是功率需要除1000输出结果，其他不需要也就是除数变成了1

    private String mStatus;//'状态，【0】未生效，【1】为已生效，【-1】为通信异常，【-2】为逻辑删除
    private String tableNameString="0";//表名字段 动态表名字段
    private String tableType;//表类型，枚举有dl电流，dy电压等

    //辅助告警信息查询分页--liyidu
    private int limitStart;
    private int limitEnd;

    private String ratedCapacity;//额定容量
    private String ratedVoltage;//额定电压
    private String ratedCurrent;//额定电流
    private String ratedTemperature;//额定温度
    private String ratedHumidity ;//额定湿度
    private String monitoringRl;


    public String getRatedCapacity() {
        return ratedCapacity;
    }

    public void setRatedCapacity(String ratedCapacity) {
        this.ratedCapacity = ratedCapacity;
    }

    public String getRatedVoltage() {
        return ratedVoltage;
    }

    public void setRatedVoltage(String ratedVoltage) {
        this.ratedVoltage = ratedVoltage;
    }

    public String getRatedCurrent() {
        return ratedCurrent;
    }

    public void setRatedCurrent(String ratedCurrent) {
        this.ratedCurrent = ratedCurrent;
    }

    public String getRatedTemperature() {
        return ratedTemperature;
    }

    public void setRatedTemperature(String ratedTemperature) {
        this.ratedTemperature = ratedTemperature;
    }

    public String getRatedHumidity() {
        return ratedHumidity;
    }

    public void setRatedHumidity(String ratedHumidity) {
        this.ratedHumidity = ratedHumidity;
    }

    //辅助排序--liyidu
    private String oderCol;
    private String sortDir;

    public String getDateString() {
        return dateString;
    }

    public void setDateString(String dateString) {
        this.dateString = dateString;
    }

    public String getTableType() {
        return tableType;
    }

    public void setTableType(String tableType) {
        this.tableType = tableType;
    }

    public String getDivisor() {
        return divisor;
    }

    public void setDivisor(String divisor) {
        this.divisor = divisor;
    }

    public String getMonitoringSimCardInnerName() {
        return monitoringSimCardInnerName;
    }

    public void setMonitoringSimCardInnerName(String monitoringSimCardInnerName) {
        this.monitoringSimCardInnerName = monitoringSimCardInnerName;
    }

    public String getmStatus() {
        return mStatus;
    }

    public void setmStatus(String mStatus) {
        this.mStatus = mStatus;
    }

    public String getMonitoringProtocolId() {
        return monitoringProtocolId;
    }

    public void setMonitoringProtocolId(String monitoringProtocolId) {
        this.monitoringProtocolId = monitoringProtocolId;
    }



    public String getTableNameString() {
        return tableNameString;
    }

    public void setTableNameString(String tableNameString) {
        this.tableNameString = tableNameString;
    }

    public String getMonitoringPt() {
        return monitoringPt;
    }

    public void setMonitoringPt(String monitoringPt) {
        this.monitoringPt = monitoringPt;
    }

    public String getMonitoringCt() {
        return monitoringCt;
    }

    public void setMonitoringCt(String monitoringCt) {
        this.monitoringCt = monitoringCt;
    }

    public String getMonitoringPointLogicAddr() {
        return monitoringPointLogicAddr;
    }

    public void setMonitoringPointLogicAddr(String monitoringPointLogicAddr) {
        this.monitoringPointLogicAddr = monitoringPointLogicAddr;
    }

    public String getMonitoringSimCardNo() {
        return monitoringSimCardNo;
    }

    public void setMonitoringSimCardNo(String monitoringSimCardNo) {
        this.monitoringSimCardNo = monitoringSimCardNo;
    }

    public String getMonitoringSimCardInnerNo() {
        return monitoringSimCardInnerNo;
    }

    public void setMonitoringSimCardInnerNo(String monitoringSimCardInnerNo) {
        this.monitoringSimCardInnerNo = monitoringSimCardInnerNo;
    }

    public String getMonitoringPassword() {
        return monitoringPassword;
    }

    public void setMonitoringPassword(String monitoringPassword) {
        this.monitoringPassword = monitoringPassword;
    }

    public String getMonitoringPointAliasId() {
        return monitoringPointAliasId;
    }

    public void setMonitoringPointAliasId(String monitoringPointAliasId) {
        this.monitoringPointAliasId = monitoringPointAliasId;
    }

    public String getMonitoringPointCoreId() {
        return monitoringPointCoreId;
    }

    public void setMonitoringPointCoreId(String monitoringPointCoreId) {
        this.monitoringPointCoreId = monitoringPointCoreId;
    }

    public String getMonitoringPointName() {
        return monitoringPointName;
    }

    public void setMonitoringPointName(String monitoringPointName) {
        this.monitoringPointName = monitoringPointName;
    }

    public String getMonitoringPointType() {
        return monitoringPointType;
    }

    public void setMonitoringPointType(String monitoringPointType) {
        this.monitoringPointType = monitoringPointType;
    }

    public String getYnBaseEquipmentLongId() {
        return ynBaseEquipmentLongId;
    }

    public void setYnBaseEquipmentLongId(String ynBaseEquipmentLongId) {
        this.ynBaseEquipmentLongId = ynBaseEquipmentLongId;
    }

    public String getYnBaseEquipmentId() {
        return ynBaseEquipmentId;
    }

    public void setYnBaseEquipmentId(String ynBaseEquipmentId) {
        this.ynBaseEquipmentId = ynBaseEquipmentId;
    }

    public String getYnBaseEquipmentName() {
        return ynBaseEquipmentName;
    }

    public void setYnBaseEquipmentName(String ynBaseEquipmentName) {
        this.ynBaseEquipmentName = ynBaseEquipmentName;
    }

    public String getYnBaseEnterpriseSubstationLongId() {
        return ynBaseEnterpriseSubstationLongId;
    }

    public void setYnBaseEnterpriseSubstationLongId(String ynBaseEnterpriseSubstationLongId) {
        this.ynBaseEnterpriseSubstationLongId = ynBaseEnterpriseSubstationLongId;
    }

    public String getYnBaseEnterpriseSubstationId() {
        return ynBaseEnterpriseSubstationId;
    }

    public void setYnBaseEnterpriseSubstationId(String ynBaseEnterpriseSubstationId) {
        this.ynBaseEnterpriseSubstationId = ynBaseEnterpriseSubstationId;
    }

    public String getYnBaseEnterpriseSubstationName() {
        return ynBaseEnterpriseSubstationName;
    }

    public void setYnBaseEnterpriseSubstationName(String ynBaseEnterpriseSubstationName) {
        this.ynBaseEnterpriseSubstationName = ynBaseEnterpriseSubstationName;
    }

    public String getYnBaseCustomerEnterpriseLongId() {
        return ynBaseCustomerEnterpriseLongId;
    }

    public void setYnBaseCustomerEnterpriseLongId(String ynBaseCustomerEnterpriseLongId) {
        this.ynBaseCustomerEnterpriseLongId = ynBaseCustomerEnterpriseLongId;
    }

    public String getYnBaseCustomerEnterpriseId() {
        return ynBaseCustomerEnterpriseId;
    }

    public void setYnBaseCustomerEnterpriseId(String ynBaseCustomerEnterpriseId) {
        this.ynBaseCustomerEnterpriseId = ynBaseCustomerEnterpriseId;
    }

    public String getYnBaseCustomerEnterpriseName() {
        return ynBaseCustomerEnterpriseName;
    }

    public void setYnBaseCustomerEnterpriseName(String ynBaseCustomerEnterpriseName) {
        this.ynBaseCustomerEnterpriseName = ynBaseCustomerEnterpriseName;
    }

    public String getYnBaseStaffCompanyId() {
        return ynBaseStaffCompanyId;
    }

    public void setYnBaseStaffCompanyId(String ynBaseStaffCompanyId) {
        this.ynBaseStaffCompanyId = ynBaseStaffCompanyId;
    }

    public String getYnBaseStaffCompanyName() {
        return ynBaseStaffCompanyName;
    }

    public void setYnBaseStaffCompanyName(String ynBaseStaffCompanyName) {
        this.ynBaseStaffCompanyName = ynBaseStaffCompanyName;
    }

    public int getLimitStart() {
        return limitStart;
    }

    public void setLimitStart(int limitStart) {
        this.limitStart = limitStart;
    }

    public int getLimitEnd() {
        return limitEnd;
    }

    public void setLimitEnd(int limitEnd) {
        this.limitEnd = limitEnd;
    }

    public String getOderCol() {
        return oderCol;
    }

    public void setOderCol(String oderCol) {
        this.oderCol = oderCol;
    }

    public String getSortDir() {
        return sortDir;
    }

    public void setSortDir(String sortDir) {
        this.sortDir = sortDir;
    }

    public String getMonitoringRl() {
        return monitoringRl;
    }

    public void setMonitoringRl(String monitoringRl) {
        this.monitoringRl = monitoringRl;
    }
}
