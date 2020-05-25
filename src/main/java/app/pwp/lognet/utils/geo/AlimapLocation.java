package app.pwp.lognet.utils.geo;

public class AlimapLocation {
    private String province;
    private String city;

    @Override
    public String toString() {
        if (city != null) {
            return province + " " + city;
        } else {
            return province;
        }
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
