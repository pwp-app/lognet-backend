package app.pwp.lognet.app.form;

import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import java.util.Date;

public class MissionUpdateForm {
    @NotEmpty(message = "请提交正确的参数")
    private String id;
    @NotEmpty(message = "请提交正确的参数")
    @Length(max = 30, message = "名称不能超过30个字符")
    private String name;
    @Length(max = 200, message = "描述不能超过200个字符")
    private String desc;
    private Date startTime;
    private Date endTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
}