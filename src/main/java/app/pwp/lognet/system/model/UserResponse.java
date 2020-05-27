package app.pwp.lognet.system.model;

import java.io.Serializable;

public class UserResponse implements Serializable {
    private long uid;
    private String username;
    private String email;
    private Role role;

    public UserResponse(long uid, String username, String email, Role role) {
        this.uid = uid;
        this.username = username;
        this.email = email;
        this.role = role;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
