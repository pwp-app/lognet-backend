package app.pwp.lognet.config.shiro;

import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.cache.MemoryConstrainedCacheManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.servlet.Filter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

@Configuration
public class ShiroConfig {
    @Bean
    public Realm realm() {
        UserRealm realm = new UserRealm();
        realm.setCredentialsMatcher(hashedCredentialsMatcher());
        return realm;
    }

    @Bean
    public SimpleCookie rememberMeCookie() {
        SimpleCookie simpleCookie = new SimpleCookie("rememberMe");
        // 30天免登录
        simpleCookie.setMaxAge(2592000);
        return simpleCookie;
    }

    @Bean
    public CredentialsMatcher hashedCredentialsMatcher() {
        HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher();
        hashedCredentialsMatcher.setHashAlgorithmName("SHA-256");
        hashedCredentialsMatcher.setHashIterations(32);
        return hashedCredentialsMatcher;
    }

    @Bean
    public CookieRememberMeManager rememberMeManager() throws IOException {
        CookieRememberMeManager cookieRememberMeManager = new CookieRememberMeManager();
        cookieRememberMeManager.setCookie(rememberMeCookie());
        Properties properties = new Properties();
        properties.load(new ClassPathResource("properties/shiro.properties").getInputStream());
        cookieRememberMeManager.setCipherKey(properties.getProperty("key").getBytes());
        return cookieRememberMeManager;
    }

    @Bean
    public SessionManager sessionManager() {
        return new DefaultWebSessionManager();
    }

    @Bean({"securityManager"})
    public SecurityManager securityManager() throws IOException {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(realm());
        securityManager.setCacheManager(new MemoryConstrainedCacheManager());
        securityManager.setSessionManager(sessionManager());
        securityManager.setRememberMeManager(rememberMeManager());
        return securityManager;
    }

    public OptionsRequestUserFilter optionsRequestUserFilter(){
        return new OptionsRequestUserFilter();
    }

    public OptionsRequestRolesFilter optionsRequestRolesFilter() { return new OptionsRequestRolesFilter(); }

    @Bean({"shiroFilter"})
    public ShiroFilterFactoryBean shiroFilter(SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilter = new ShiroFilterFactoryBean();
        shiroFilter.setSecurityManager(securityManager);
        // 配置未授权状态的跳转
        shiroFilter.setLoginUrl("/error/auth");
        shiroFilter.setUnauthorizedUrl("/error/auth");
        // 自定义过滤器
        Map<String, Filter> filterMap = new LinkedHashMap<>();
        filterMap.put("user", optionsRequestUserFilter());
        filterMap.put("roles", optionsRequestRolesFilter());
        shiroFilter.setFilters(filterMap);
        // 配置不会被拦截的api
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
        // 允许无需权限访问的路径
        String[] anonPaths = {
                "/error/**",
                "/druid/**",
                "/ping/**",
                "/portal/**"
        };
        for (String path : anonPaths) {
            filterChainDefinitionMap.put(path, "anon");
        }
        // 对应的是user拦截器，支持rememberMe
        filterChainDefinitionMap.put("/**", "user");
        shiroFilter.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return shiroFilter;
    }

    @Bean({"lifecycleBeanPostProcessor"})
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    @Bean
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator proxyCreator = new DefaultAdvisorAutoProxyCreator();
        proxyCreator.setProxyTargetClass(true);
        return proxyCreator;
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
        advisor.setSecurityManager(securityManager);
        return advisor;
    }
}
