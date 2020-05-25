package app.pwp.lognet.utils.geo;

import app.pwp.lognet.utils.network.Http;
import app.pwp.lognet.utils.network.HttpResult;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

@Component
public class Alimap {
    @Value(value = "${alimap.key}")
    private String ALIMAP_KEY;
    private final String API_BASE = "https://restapi.amap.com/v3/ip";
    @Async
    public ListenableFuture<AlimapLocation> getGeo(String ip) throws Exception {
        HttpResult res = Http.doGet(API_BASE + "?key=" + ALIMAP_KEY + "&ip=" + ip);
        if (res.getCode() != 200 || res.getBody() == null) {
            return new AsyncResult<>(null);
        }
        JSONObject parsedObject = JSON.parseObject(res.getBody());
        AlimapLocation location = new AlimapLocation();
        if (parsedObject.get("province") instanceof String) {
            location.setProvince(parsedObject.getString("province"));
            location.setCity(parsedObject.getString("city"));
        }
        return new AsyncResult<>(location);
    }
}
