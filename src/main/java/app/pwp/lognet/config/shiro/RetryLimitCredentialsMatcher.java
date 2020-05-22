package app.pwp.lognet.config.shiro;

import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class RetryLimitCredentialsMatcher extends HashedCredentialsMatcher {
    private static final Logger log = LoggerFactory.getLogger(RetryLimitCredentialsMatcher.class);

    private int maxRetryNum = 10;
    private EhCacheManager shiroEhcacheManager;

    public RetryLimitCredentialsMatcher(EhCacheManager shiroEhcacheManager) {
        this.shiroEhcacheManager = shiroEhcacheManager;
    }

    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
        Cache<String, AtomicInteger> passwordRetryCache = shiroEhcacheManager.getCache("passwordRetryCache");
        String username = (String) token.getPrincipal();
        //retry count + 1
        AtomicInteger retryCount = passwordRetryCache.get(username);
        if (null == retryCount) {
            retryCount = new AtomicInteger(0);
            passwordRetryCache.put(username, retryCount);
        }
        boolean matches = super.doCredentialsMatch(token, info);
        if (matches) {
            //clear retry data
            passwordRetryCache.remove(username);
        } else {
            if (retryCount.incrementAndGet() > maxRetryNum) {
                log.warn("用户[{}]在登录验证时失败验证超过{}次，帐号已被临时锁定", username, maxRetryNum);
                throw new ExcessiveAttemptsException("登录失败超过10次，帐号将被锁定30分钟");
            }
            passwordRetryCache.put(username, retryCount);
        }
        return matches;
    }
}
