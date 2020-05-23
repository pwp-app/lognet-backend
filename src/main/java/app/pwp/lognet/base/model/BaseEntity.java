package app.pwp.lognet.base.model;

import javax.persistence.*;
import java.util.Date;

@MappedSuperclass
public abstract class BaseEntity extends Base {
    private Date createTime;

    private Date lastUpdateTime;

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }
}
