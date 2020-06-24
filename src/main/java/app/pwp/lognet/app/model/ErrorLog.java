package app.pwp.lognet.app.model;

import app.pwp.lognet.app.ro.HotPath;
import app.pwp.lognet.base.model.BaseUUIDLog;

import javax.persistence.*;

@Entity
@SqlResultSetMapping(name = "HotPath", classes = {
        @ConstructorResult(targetClass = HotPath.class,
                columns = {@ColumnResult(name = "domain"), @ColumnResult(name = "path"), @ColumnResult(name = "count", type=Long.class)})
})
@Table(name = "lognet_errorlog")
// 通用的错误日志
public class ErrorLog extends BaseUUIDLog {
    // 对应的site
    private String siteId;
    // 对应路径
    private String path;
    // 日志内容（Long text）
    @Column(length = 16777215)
    private String content;
    private int clientWidth;
    private int clientHeight;
    private int windowInnerWidth;
    private int windowInnerHeight;
    private int windowOuterWidth;
    private int windowOuterHeight;
    @Column(length = 1000)
    private String userAgent;

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public String getPath() {
        return path;
    }


    public void setPath(String path) {
        this.path = path;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getClientWidth() {
        return clientWidth;
    }

    public void setClientWidth(int clientWidth) {
        this.clientWidth = clientWidth;
    }

    public int getClientHeight() {
        return clientHeight;
    }

    public void setClientHeight(int clientHeight) {
        this.clientHeight = clientHeight;
    }

    public int getWindowInnerWidth() {
        return windowInnerWidth;
    }

    public void setWindowInnerWidth(int windowInnerWidth) {
        this.windowInnerWidth = windowInnerWidth;
    }

    public int getWindowInnerHeight() {
        return windowInnerHeight;
    }

    public void setWindowInnerHeight(int windowInnerHeight) {
        this.windowInnerHeight = windowInnerHeight;
    }

    public int getWindowOuterWidth() {
        return windowOuterWidth;
    }

    public void setWindowOuterWidth(int windowOuterWidth) {
        this.windowOuterWidth = windowOuterWidth;
    }

    public int getWindowOuterHeight() {
        return windowOuterHeight;
    }

    public void setWindowOuterHeight(int windowOuterHeight) {
        this.windowOuterHeight = windowOuterHeight;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }
}
