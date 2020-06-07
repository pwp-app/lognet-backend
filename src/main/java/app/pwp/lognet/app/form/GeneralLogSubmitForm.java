package app.pwp.lognet.app.form;

import javax.validation.constraints.NotEmpty;

public class GeneralLogSubmitForm {
    @NotEmpty(message = "请提交正确的参数")
    private String appKey;
    @NotEmpty(message = "请提交正确的参数")
    private String content;

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
