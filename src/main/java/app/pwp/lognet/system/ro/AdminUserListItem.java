package app.pwp.lognet.system.ro;

import java.util.Date;

public class AdminUserListItem {
    private Long uid;
    private Date createTime;
    private String username;
    private String role;
    private Boolean enabled;

    public AdminUserListItem(Long uid, Date createTime, String username, String role, Boolean enabled) {
        this.uid = uid;
        this.createTime = createTime;
        this.username = username;
        this.role = role;
        this.enabled = enabled;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
