package app.pwp.lognet.app.model;

import app.pwp.lognet.base.model.BaseUUIDEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "lognet_sites")
public class Site extends BaseUUIDEntity implements Serializable {
    // 所属用户
    private Long uid;
    // 域名
    private String domain;
    // 描述
    @Column(length = 100)
    private String description;
    // AppKey
    private String appKey;
    // 是否启用
    private boolean isEnabled;

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String desc) {
        this.description = desc;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }
}
