package app.pwp.lognet.system.model;

import app.pwp.lognet.base.model.BaseEntity;
import app.pwp.lognet.system.ro.AdminUserListItem;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@SqlResultSetMapping(name = "AdminUserListItem", classes = {
        @ConstructorResult(targetClass = AdminUserListItem.class,
                columns = {@ColumnResult(name = "uid", type=Long.class), @ColumnResult(name = "createTime", type= Date.class), @ColumnResult(name = "username"), @ColumnResult(name = "role"), @ColumnResult(name="enabled", type=Boolean.class)})
})
@Table(name = "lognet_user")
public class User extends BaseEntity implements Serializable {
    @Column(nullable = false)
    private String username;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private String salt;
    private String email;
    @Column(nullable = false)
    private boolean enabled;
    private Long roleId;

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

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }
}
