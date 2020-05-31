package app.pwp.lognet.system.form;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;

public class ChangePasswordForm {
    @NotEmpty(message = "请提交正确的参数")
    private String oldPassword;
    @NotEmpty
    @Length(min=6, message = "密码不得少于6个字符")
    private String newPassword;
    @NotEmpty(message = "请提交正确的参数")
    private String newConfirmPassword;

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
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
