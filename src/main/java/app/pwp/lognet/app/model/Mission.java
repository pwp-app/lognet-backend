package app.pwp.lognet.app.model;

import app.pwp.lognet.base.model.BaseUUIDEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "lognet_mission")
public class Mission extends BaseUUIDEntity implements Serializable {
    // 绑定到的site的id
    private String siteId;
    // 任务名称
    private String name;
    // 描述
    @Column(length = 200)
    private String desc;
    // 任务可用期
    private Date startTime;
    private Date endTime;
    // 是否启用
    private boolean isEnabled;

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }
}
