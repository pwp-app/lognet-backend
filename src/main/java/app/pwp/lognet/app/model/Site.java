package app.pwp.lognet.app.model;

import app.pwp.lognet.app.ro.RunningMission;
import app.pwp.lognet.base.model.BaseUUIDEntity;
import app.pwp.lognet.system.ro.AdminSiteListItem;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@SqlResultSetMapping(name = "AdminSiteListItem", classes = {
        @ConstructorResult(targetClass = AdminSiteListItem.class,
                columns = {@ColumnResult(name = "id"), @ColumnResult(name = "uid", type=Long.class), @ColumnResult(name = "createTime", type=Date.class), @ColumnResult(name = "username"), @ColumnResult(name = "domain"), @ColumnResult(name="enabled", type=Boolean.class)})
})
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
