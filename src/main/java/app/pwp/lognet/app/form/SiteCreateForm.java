package app.pwp.lognet.app.form;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

public class SiteCreateForm {
    @NotEmpty(message = "请提交正确的参数")
    @Pattern(regexp = "^((?!-)[A-Za-z0-9-]{1,63}(?<!-)\\.)+[A-Za-z]{2,6}$", message = "请提交正确的域名")
    private String domain;
    @Length(max = 100, message = "描述不得超过100个字符")
    private String desc;

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
