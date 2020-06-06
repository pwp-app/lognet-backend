package app.pwp.lognet.app.model;

import app.pwp.lognet.base.model.BaseUUIDLog;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "lognet_errorlog")
// 通用的错误日志
public class ErrorLog extends BaseUUIDLog {
    // 对应的site
    private String siteId;
    // 日志内容（Long text）
    @Column(length = 16777215)
    private String content;

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
