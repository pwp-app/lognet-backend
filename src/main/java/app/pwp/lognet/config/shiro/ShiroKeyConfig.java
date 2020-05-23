package app.pwp.lognet.config.shiro;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Configuration
@ConfigurationProperties(prefix="shiro")
@PropertySource("classpath:shiro/key.properties")
@Component
public class ShiroKeyConfig {
    private String chpherkey;

    public String getChpherkey() {
        return chpherkey;
    }
}
