package app.pwp.lognet.system.form;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

public class ForgetPasswordForm {
    @NotEmpty(message = "请提交正确的参数")
    private String email;
    @Pattern(regexp = "^\\d{6}$", message = "请提交正确的参数")
    @NotEmpty(message = "请提交正确的参数")
    private String code;
    @NotEmpty
    @Length(min=6, message = "密码不得少于6个字符")
    private String newPassword;
    @NotEmpty(message = "请提交正确的参数")
    private String newConfirmPassword;

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

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getNewConfirmPassword() {
        return newConfirmPassword;
    }

    public void setNewConfirmPassword(String newConfirmPassword) {
        this.newConfirmPassword = newConfirmPassword;
    }
}
