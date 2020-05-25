package app.pwp.lognet.system.model;

import app.pwp.lognet.base.model.Base;

import javax.management.relation.RoleInfo;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.io.Serializable;

@Entity
@Table(name="lognet_role", uniqueConstraints = @UniqueConstraint(columnNames = {"name"}))
public class Role extends Base implements Serializable  {
    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int level;

    public Role() {}

    public Role(String name) {
        this.name = name;
        this.level = 0;
    }

    public Role(String name, int level) {
        this.name = name;
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
