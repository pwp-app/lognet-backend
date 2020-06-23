package app.pwp.lognet.app.form;

import javax.validation.constraints.NotEmpty;

public class GeneralLogSubmitForm {
    @NotEmpty(message = "请提交正确的参数")
    private String appKey;
    @NotEmpty(message = "请提交正确的参数")
    private String path;
    @NotEmpty(message = "请提交正确的参数")
    private String content;
    private int clientWidth;
    private int clientHeight;
    private String userAgent;

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
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

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }
}
