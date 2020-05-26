package app.pwp.lognet.utils.auth;

public class ValidationUtils {
    public static int generateCode() {
        return (int)((Math.random()*9+1)*100000);
    }
}
