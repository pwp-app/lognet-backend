package app.pwp.lognet.app.form;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

public class SiteUpdateForm {
    @NotEmpty(message = "请提交正确的参数")
    private String id;
    @Length(max = 100, message = "描述不得超过100个字符")
    private String desc;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
