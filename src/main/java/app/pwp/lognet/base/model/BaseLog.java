package app.pwp.lognet.base.model;

import org.springframework.data.annotation.CreatedDate;

import javax.persistence.MappedSuperclass;
import java.util.Date;

@MappedSuperclass
public abstract class BaseLog extends Base{
    @CreatedDate
    private Date createTime;
}
