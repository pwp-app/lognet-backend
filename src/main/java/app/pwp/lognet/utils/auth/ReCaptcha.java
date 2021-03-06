package app.pwp.lognet.utils.auth;

import app.pwp.lognet.utils.network.Http;
import app.pwp.lognet.utils.network.HttpResult;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

import java.math.BigDecimal;
import java.util.HashMap;

@Component
public class ReCaptcha {

    @Value(value = "${recaptcha.secret}")
    private String SECRET;
    private final String VERIFY_URL = "https://www.recaptcha.net/recaptcha/api/siteverify";
    private final double THRESHOLD = 0.6;

    @Async
    public ListenableFuture<Boolean> verify(String token) {
        if (token == null || token.length() < 1) {
            return new AsyncResult<>(false);
        }
        HashMap<String, Object> params = new HashMap<>();
        params.put("secret", SECRET);
        params.put("response", token);
        try {
            HttpResult res = Http.doPost(VERIFY_URL, params);
            if (res.getCode() != 200 || res.getBody() == null) {
                throw new RuntimeException("与 reCAPTCHA 服务器通讯失败");
            }
            JSONObject parsedObject = JSON.parseObject(res.getBody());
            if (!(boolean) parsedObject.get("success")) {
                throw new RuntimeException("reCAPTCHA 验证失败");
            }
            return new AsyncResult<>(((BigDecimal) parsedObject.get("score")).doubleValue() >= THRESHOLD);
        } catch (Exception e) {
            e.printStackTrace();
            return new AsyncResult<>(false);
        }
    }
}
