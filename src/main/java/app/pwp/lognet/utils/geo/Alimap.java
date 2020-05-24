package app.pwp.lognet.utils.geo;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.HashMap;

@Component
public class Alimap {
    @Async
    public ListenableFuture<HashMap<String, String>> getGeo(String ip) {

    }
}
