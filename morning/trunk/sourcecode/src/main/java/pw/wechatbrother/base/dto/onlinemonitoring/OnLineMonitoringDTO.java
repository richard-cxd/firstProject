package pw.wechatbrother.base.dto.onlinemonitoring;

import java.util.ArrayList;

/**
 * Created by zhengjingli on 2017/6/7.
 */
public class OnLineMonitoringDTO {
    private String monitoringPassword;//计量密码调用时候自带密码校验



    private String ynBaseCustomerEnterpriseId;
    private String ynBaseCustomerEnterpriseLongId;//用电企业整型id
    private String ynBaseEnterpriseSubstationLongId;//电房整型id
    private String ynBaseStaffCompanyId              ;//ynBaseStaffCompanyId                维保公司id
    private String monitoringPointAliasId;//计量点自定义主键
    private  String monitoringPointDate;//监测时间
    private String tag;//查询类型 电度,dl 电流,dy 电压,gj 告警,pl 频率,sd 湿度,sz 视在功率,wd 温度,wg 无功功率,yg 有功功率,ys 功率因数（1计量点,2用电企业，3维保公司，4电房）
    private String objectId;//对象id：计量点id，用电企业id，维保公司id，电房id
    //电房运行报告辅助字段
    private String startDate;//开始时间
    private String endDate;//结束时间
    private String year;//年
    private String month;//月
    private String day;//日
    private String dateString;//日期字符串，如果是年例如：2017，如果是月例如：201707，如果是日例如20170707

    private String tableNameString="0";//表名字段 动态表名字段
    private String tableType;//表类型，枚举有dl电流，dy电压等
    //用于运行报告输出
    private String divisor="1";//单位除数 表示的是功率需要除1000输出结果，其他不需要也就是除数变成了1

    //辅助告警信息查询分页
    private String limitStart;
    private String limitEnd;
    private String warningBackDay;//告警信息回溯多少天

    //辅助排序--liyidu
    private String oderCol;
    private String sortDir;

    //辅助查询--liyidu
    private String monitoringPointName;
    //为设备初始化查询的数据
    private String ynBaseEquipmentLongId;//设备整形id
    private String province;//省份
    private String city;//市
    private String area;//区

    public void setProvince(String province) {
        this.province = province;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getProvince() {
        return province;
    }

    public String getCity() {
        return city;
    }

    public String getArea() {
        return area;
    }

    private ArrayList<String> servicesList;

    private String servicesAreas;


    private String userTypes;

    public String getServicesAreas() {
        return servicesAreas;
    }

    public void setServicesAreas(String servicesAreas) {
        this.servicesAreas = servicesAreas;
    }
    public String getYnBaseCustomerEnterpriseId() {
        return ynBaseCustomerEnterpriseId;
    }

    public void setYnBaseCustomerEnterpriseId(String ynBaseCustomerEnterpriseId) {
        this.ynBaseCustomerEnterpriseId = ynBaseCustomerEnterpriseId;
    }

    public ArrayList<String> getServicesList() {
        return servicesList;
    }

    public void setServicesList(ArrayList<String> servicesList) {
        this.servicesList = servicesList;
    }

    public String getYnBaseStaffCompanyId() {
        return ynBaseStaffCompanyId;
    }

    public void setYnBaseStaffCompanyId(String ynBaseStaffCompanyId) {
        this.ynBaseStaffCompanyId = ynBaseStaffCompanyId;
    }

    public String getWarningBackDay() {
        return warningBackDay;
    }

    public void setWarningBackDay(String warningBackDay) {
        this.warningBackDay = warningBackDay;
    }

    public String getLimitStart() {
        return limitStart;
    }

    public void setLimitStart(String limitStart) {
        this.limitStart = limitStart;
    }

    public String getLimitEnd() {
        return limitEnd;
    }

    public void setLimitEnd(String limitEnd) {
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

    public String getMonitoringPointName() {
        return monitoringPointName;
    }

    public void setMonitoringPointName(String monitoringPointName) {
        this.monitoringPointName = monitoringPointName;
    }

    public String getDateString() {
        return dateString;
    }

    public void setDateString(String dateString) {
        this.dateString = dateString;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getTableNameString() {
        return tableNameString;
    }

    public void setTableNameString(String tableNameString) {
        this.tableNameString = tableNameString;
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

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getYnBaseEquipmentLongId() {
        return ynBaseEquipmentLongId;
    }

    public void setYnBaseEquipmentLongId(String ynBaseEquipmentLongId) {
        this.ynBaseEquipmentLongId = ynBaseEquipmentLongId;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
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

    public String getMonitoringPointDate() {
        return monitoringPointDate;
    }

    public void setMonitoringPointDate(String monitoringPointDate) {
        this.monitoringPointDate = monitoringPointDate;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getYnBaseCustomerEnterpriseLongId() {
        return ynBaseCustomerEnterpriseLongId;
    }

    public void setYnBaseCustomerEnterpriseLongId(String ynBaseCustomerEnterpriseLongId) {
        this.ynBaseCustomerEnterpriseLongId = ynBaseCustomerEnterpriseLongId;
    }

    public String getYnBaseEnterpriseSubstationLongId() {
        return ynBaseEnterpriseSubstationLongId;
    }

    public void setYnBaseEnterpriseSubstationLongId(String ynBaseEnterpriseSubstationLongId) {
        this.ynBaseEnterpriseSubstationLongId = ynBaseEnterpriseSubstationLongId;
    }
}
