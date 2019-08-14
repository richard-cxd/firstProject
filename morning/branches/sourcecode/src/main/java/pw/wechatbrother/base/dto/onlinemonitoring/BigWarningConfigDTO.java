package pw.wechatbrother.base.dto.onlinemonitoring;

import java.util.List;

/**
 * Created by zhengjingli on 2017/7/26.
 */
public class BigWarningConfigDTO {
    private String monitoringPassword                ;//计量密码调用时候自带密码校验
    private String monitoringPointAliasId           ;// monitoringPointAliasId           平台自定义id
    private String monitoringPointCoreId            ;// monitoringPointCoreId            监测点固件核心id
 /*   private String ratedCurrent ;//额定电流
    private String ratedVoltage;//额定电压
    private String ratedCapacity;//额定容量
    private String ratedTemperature;//额定温度
    private String ratedHumidity;//额定湿度*/
    private List<WarningConfigDTO> warningConfigDTOList;

   /* public String getRatedCurrent() {
        return ratedCurrent;
    }

    public void setRatedCurrent(String ratedCurrent) {
        this.ratedCurrent = ratedCurrent;
    }

    public String getRatedVoltage() {
        return ratedVoltage;
    }

    public void setRatedVoltage(String ratedVoltage) {
        this.ratedVoltage = ratedVoltage;
    }

    public String getRatedCapacity() {
        return ratedCapacity;
    }

    public void setRatedCapacity(String ratedCapacity) {
        this.ratedCapacity = ratedCapacity;
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
    }*/

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

    public String getMonitoringPassword() {
        return monitoringPassword;
    }

    public void setMonitoringPassword(String monitoringPassword) {
        this.monitoringPassword = monitoringPassword;
    }

    public List<WarningConfigDTO> getWarningConfigDTOList() {
        return warningConfigDTOList;
    }

    public void setWarningConfigDTOList(List<WarningConfigDTO> warningConfigDTOList) {
        this.warningConfigDTOList = warningConfigDTOList;
    }
}
