package app.pwp.lognet.app.form;

import javax.validation.constraints.NotEmpty;

public class DeleteMissionLogForm {
    @NotEmpty(message = "请提交正确的参数")
    private String id;
    @NotEmpty(message = "请提交正确的参数")
    private String missionId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMissionId() {
        return missionId;
    }

    public void setMissionId(String missionId) {
        this.missionId = missionId;
    }
}
