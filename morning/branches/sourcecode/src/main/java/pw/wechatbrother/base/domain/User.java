package pw.wechatbrother.base.domain;

import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>linpeizhan
 * <p>Version: 1.0
 */
public class User implements Serializable {

    private Integer id; //编号
    private Integer eid; //企业编号
    //@NotNull(message = "所属组织不能为空！")
    private Integer organizationId; //所属公司ID

    private String username; //用户名

    private String password = "e10adc3949ba59abbe56e057f20f883e"; //密码 说明e10adc3949ba59abbe56e057f20f883e这个是前台加密之后的123456
    private String salt; //加密密码的盐
    private List<Integer> roleIds; //拥有的角色列表
    private Boolean locked = Boolean.FALSE;
    private String type;//用户类型
    private String openid;//微信在公众号的唯一主键
    private String uuid;//本次调用唯一ID

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    /*
                    *HaiAng Add Strat

                     */
    private String organizationName;//所属组织名称
    private String roleNames;//拥有的角色列表名称
    private String roleIdList;//拥有的角色列表ID
    private String fullName;
    private String checkMsg;//验证码
    private String checkMsgTime;//验证码更新时间
    private String eName;//验证码更新时间

    public String geteName() {
        return eName;
    }

    public void seteName(String eName) {
        this.eName = eName;
    }

    public String getCheckMsgTime() {
        return checkMsgTime;
    }

    public void setCheckMsgTime(String checkMsgTime) {
        this.checkMsgTime = checkMsgTime;
    }

    public String getCheckMsg() {
        return checkMsg;
    }

    public void setCheckMsg(String checkMsg) {
        this.checkMsg = checkMsg;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }


    public String getRoleIdList() {
        return roleIdList;
    }

    public void setRoleIdList(String roleIdList) {
        this.roleIdList = roleIdList;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getRoleNames() {
        return roleNames;
    }

    public void setRoleNames(String roleNames) {
        this.roleNames = roleNames;
    }

    /*
            *HaiAng Add END
             */
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public User() {
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Integer organizationId) {
        this.organizationId = organizationId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getCredentialsSalt() {
        return username + salt;
    }

    public List<Integer> getRoleIds() {
        if (roleIds == null) {
            roleIds = new ArrayList<Integer>();
        }
        return roleIds;
    }

    public void setRoleIds(List<Integer> roleIds) {
        this.roleIds = roleIds;
    }


    public String getRoleIdsStr() {
        if (CollectionUtils.isEmpty(roleIds)) {
            return "";
        }
        StringBuilder s = new StringBuilder();
        for (Integer roleId : roleIds) {
            s.append(roleId);
            s.append(",");
        }
        return s.toString();
    }

    public void setRoleIdsStr(String roleIdsStr) {
        if (StringUtils.isEmpty(roleIdsStr)) {
            return;
        }
        String[] roleIdStrs = roleIdsStr.split(",");
        for (String roleIdStr : roleIdStrs) {
            if (StringUtils.isEmpty(roleIdStr)) {
                continue;
            }
            getRoleIds().add(Integer.valueOf(roleIdStr));
        }
    }

    public Boolean getLocked() {
        return locked;
    }

    public void setLocked(Boolean locked) {
        this.locked = locked;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (id != null ? !id.equals(user.id) : user.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", organizationId=" + organizationId +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", salt='" + salt + '\'' +
                ", roleIds=" + roleIds +
                ", locked=" + locked +
                ", organizationName='" + organizationName + '\'' +
                ", roleNames='" + roleNames + '\'' +
                '}';
    }

    public Integer getEid() {
        return eid;
    }

    public void setEid(Integer eid) {
        this.eid = eid;
    }
}
