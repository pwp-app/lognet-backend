package app.pwp.lognet.system.form;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class GeneralUserForm {
    @NotNull(message = "请提交正确的参数")
    @Min(value = 1, message = "请提交正确的参数")
    private long uid;

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }
}
