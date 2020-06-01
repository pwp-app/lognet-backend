package app.pwp.lognet.app.model;

import app.pwp.lognet.base.model.BaseUUIDEntity;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "lognet_sites")
public class Site extends BaseUUIDEntity implements Serializable {
    // 域名
    private String domain;
    // 所属用户
    private Long uid;
}
