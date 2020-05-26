package app.pwp.lognet.system.form;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;

public class MailValidateForm {
    @Email(message = "请提交正确的参数")
    private String email;
    @Pattern(regexp = "^\\d{6}$", message = "请提交正确的参数")
    private String code;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
