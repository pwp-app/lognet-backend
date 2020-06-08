package app.pwp.lognet.app.form;

import javax.validation.constraints.NotEmpty;

public class GeneralDeleteForm {
    @NotEmpty(message = "请提交正确的参数")
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
