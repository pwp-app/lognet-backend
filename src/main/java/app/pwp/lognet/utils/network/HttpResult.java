package app.pwp.lognet.utils.network;

import java.io.Serializable;

public class HttpResult implements Serializable {

    // 响应的状态码
    private int code;

    // 响应的响应体
    private String body;

    public HttpResult(){
        // do nothing
    }

    public HttpResult(int code, String body){
        this.code = code;
        this.body = body;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
