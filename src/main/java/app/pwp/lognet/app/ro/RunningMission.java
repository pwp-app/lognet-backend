package app.pwp.lognet.app.ro;

import java.util.Date;

public class RunningMission {
    private String id;
    private String domain;
    private String name;
    private Date startTime;
    private Date endTime;

    public RunningMission(String id, String domain, String name, Date startTime, Date endTime) {
        this.id = id;
        this.domain = domain;
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
