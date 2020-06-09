package app.pwp.lognet.app.ro;

public class HotPath {
    private String domain;
    private String path;
    private Long count;

    public HotPath(String domain, String path, Long count) {
        this.domain = domain;
        this.path = path;
        this.count = count;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}
