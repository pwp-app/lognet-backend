package app.pwp.lognet.app.form;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

public class MissionLogSubmitForm {
    @NotEmpty(message = "请提交正确参数")
    private String appKey;
    @NotEmpty(message = "请提交正确参数")
    private String missionId;
    @NotEmpty(message = "请提交正确参数")
    private String path;
    @NotEmpty(message = "请提交正确参数")
    @Pattern(regexp = "^(debug)|(info)|(warn)|(error)$")
    private String type;
    @NotEmpty(message = "请提交正确参数")
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
