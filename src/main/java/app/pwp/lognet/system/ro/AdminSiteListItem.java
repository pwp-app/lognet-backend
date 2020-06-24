package app.pwp.lognet.system.ro;

import java.util.Date;

public class AdminSiteListItem {
    private String id;
    private Long uid;
    private Date createTime;
    private String username;
    private String domain;
    private Boolean enabled;

    public AdminSiteListItem(String id, Long uid, Date createTime, String username, String domain, Boolean enabled) {
        this.id = id;
        this.uid = uid;
        this.createTime = createTime;
        this.username = username;
        this.domain = domain;
        this.enabled = enabled;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
