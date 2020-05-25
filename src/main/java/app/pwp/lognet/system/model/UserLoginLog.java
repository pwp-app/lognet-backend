package app.pwp.lognet.system.model;

import app.pwp.lognet.base.model.BaseLog;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "lognet_log_login")
public class UserLoginLog extends BaseLog implements Serializable {
    private long uid;
    private String ip;
    private String geo;
    public UserLoginLog() {}
    public UserLoginLog(long uid) {
        this.uid = uid;
    }
    public UserLoginLog(long uid, String ip) {
        this.uid = uid;
        this.ip = ip;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getGeo() {
        return geo;
    }

    public void setGeo(String geo) {
        this.geo = geo;
    }
}
