package app.pwp.lognet.system.form;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

public class ChangeBindForm {
    @NotEmpty(message = "请提交正确的参数")
    private String password;
    @NotEmpty(message = "请提交正确的参数")
    @Email
    private String newMail;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNewMail() {
        return newMail;
    }

    public void setNewMail(String newMail) {
        this.newMail = newMail;
    }
}
