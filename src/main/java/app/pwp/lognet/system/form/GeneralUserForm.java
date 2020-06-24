package app.pwp.lognet.system.form;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class GeneralUserForm {
    @NotNull(message = "请提交正确的参数")
    @Min(value = 1, message = "请提交正确的参数")
    private Long uid;

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }
}
