package app.pwp.lognet.app.model;

import app.pwp.lognet.base.model.BaseUUIDLog;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "lognet_missionlog")
public class MissionLog extends BaseUUIDLog {
    // 对应的任务ID
    private String missionId;
    // 类型（debug、info、warn、error）
    private String type;
    // 对应页面
    private String path;
    // 日志内容（Long text）
    @Column(length = 16777215)
    private String content;

    public String getMissionId() {
        return missionId;
    }

    public void setMissionId(String missionId) {
        this.missionId = missionId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
