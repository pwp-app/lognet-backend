package app.pwp.lognet.utils.auth;

import org.springframework.stereotype.Component;

@Component
public class ValidationUtils {
    private int generateCode() {
        return (int)((Math.random()*9+1)*100000);
    }
}
