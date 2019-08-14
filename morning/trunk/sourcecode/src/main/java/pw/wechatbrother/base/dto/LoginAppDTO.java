package pw.wechatbrother.base.dto;

/**辅助app登录工具
 * Created by zhengjingli on 2016/11/22.
 */
public class LoginAppDTO {
    private String userName; //loginName
    private String password;
    private String uuid;
    private String openid ;//微信的openid 在微信验证的时候和返回用户对象的时候使用
    private String lock;//系统登录时候带的锁，用于接口校验成功之后用户通过这个字段获取到当前用户lockstr锁的信息
    private String key;//解开锁的钥匙
    private String fullname;//中文名
    private String userid;//用户的id
    private String zoneid;//角色所属的区域ID，如果是如果人的部门所属的是集团层次的部门那就是集团的组织ID，否则则为公司组织ID
    private String zoneName;//角色所属的区域名称
    private String ynBaseCustomerEnterpriseId;//用户所属的企业ID，如果是外部用户（企业用户）的时候这个字段为必填
    private String lastLoginTime;//最后登录时间


    public String getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(String lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getZoneName() {
        return zoneName;
    }

    public void setZoneName(String zoneName) {
        this.zoneName = zoneName;
    }

    public String getZoneid() {
        return zoneid;
    }

    public void setZoneid(String zoneid) {
        this.zoneid = zoneid;
    }

    public String getYnBaseCustomerEnterpriseId() {
        return ynBaseCustomerEnterpriseId;
    }

    public void setYnBaseCustomerEnterpriseId(String ynBaseCustomerEnterpriseId) {
        this.ynBaseCustomerEnterpriseId = ynBaseCustomerEnterpriseId;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getLock() {
        return lock;
    }

    public void setLock(String lock) {
        this.lock = lock;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
