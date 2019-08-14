package pw.wechatbrother.base.dto.onlinemonitoring;

/**
 * Created by zhengjingli on 2017/7/26.
 */
public class WarningConfigDTO {
    private String monitoringPassword;//计量密码调用时候自带密码校验
    private String warningType;//类型
    private String warningStatus;//状态1启用，0禁用
    private String warningValue;//上限值
    private String warningUnit;//单位
    private String ratedValue;//类型的额定值

    public String getRatedValue() {
        return ratedValue;
    }

    public void setRatedValue(String ratedValue) {
        this.ratedValue = ratedValue;
    }

    public String getMonitoringPassword() {
        return monitoringPassword;
    }

    public void setMonitoringPassword(String monitoringPassword) {
        this.monitoringPassword = monitoringPassword;
    }

    public String getWarningType() {
        return warningType;
    }

    public void setWarningType(String warningType) {
        this.warningType = warningType;
    }

    public String getWarningStatus() {
        return warningStatus;
    }

    public void setWarningStatus(String warningStatus) {
        this.warningStatus = warningStatus;
    }

    public String getWarningValue() {
        return warningValue;
    }

    public void setWarningValue(String warningValue) {
        this.warningValue = warningValue;
    }

    public String getWarningUnit() {
        return warningUnit;
    }

    public void setWarningUnit(String warningUnit) {
        this.warningUnit = warningUnit;
    }


}
