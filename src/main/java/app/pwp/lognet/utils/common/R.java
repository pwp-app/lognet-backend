package app.pwp.lognet.utils.common;
import com.alibaba.fastjson.JSON;

public class R {
    private int code;
    private String message;
    private Object data;

    public R(String message) {
        this.message = message;
    }

    public R(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public R(int code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public String toString(){
        return JSON.toJSONString(this);
    }

    public static R create(int code, String message) {
        R r = new R(code, message);
        return r;
    }

    public static R ok() {
        R r = new R(200, "success");
        return r;
    }

    public static R ok(String message) {
        R r = new R(200, message);
        return r;
    }

    public static R ok(Object data) {
        R r = new R(200, "success", data);
        return r;
    }

    public static R ok(String message, Object data) {
        R r = new R(200, message, data);
        return r;
    }

    public static R error() {
        R r = new R(500, "error");
        return r;
    }

    public static R error(String message) {
        R r = new R(500, message);
        return r;
    }

    public static R badRequest(){
        return new R(400, "Bad Request");
    }

    public static R badRequest(String message){
        return new R(400, message);
    }

    public static R forbidden(){
        return new R(403, "Forbidden");
    }

    public static R forbidden(String message){
        return new R(403, message);
    }

    public static R noufound() { return new R(404, "API not found."); }

    public static R unauth(){
        return new R(401, "UnAuthorized");
    }

    public static R unauth(String message){
        return new R(401, message);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
