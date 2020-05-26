package app.pwp.lognet.system.form;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

public class UserRegisterForm implements Serializable {
    @NotEmpty(message = "用户名不能为空")
    @Length(min=4, max=30, message = "用户名长度必须为4-30个字符")
    @Pattern(regexp = "^[A-Za-z0-9_]+$", message = "用户名只能为字母、数字、下划线")
    private String username;
    @NotEmpty(message = "密码不能为空")
    @Length(min=6, message = "密码不得少于6个字符")
    private String password;
    @NotEmpty(message = "确认密码不能为空")
    private String confirmPassword;
    @NotEmpty(message = "电子邮箱不能为空")
    @Email(message = "请输入正确的电子邮箱地址")
    private String email;
    @NotEmpty(message = "请提交完整参数")
    private String token;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
