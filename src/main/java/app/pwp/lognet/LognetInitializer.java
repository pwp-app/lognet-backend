package app.pwp.lognet;

import app.pwp.lognet.system.service.RoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class LognetInitializer implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(LognetInitializer.class);
    @Resource
    private RoleService roleService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("Start check roles in database...");
        String[] roles = {"admin", "user"};
        int[] roles_level = {2, 1};
        for (int i = 0; i < roles.length; i++) {
            if (!roleService.exists(roles[i])) {
                log.info("Role ["+roles[i]+"] doesn't exist, adding to database...");
                boolean state = roleService.add(roles[i], roles_level[i]);
                if (!state) {
                    throw new RuntimeException("Adding role to database failed.");
                }
            }
        }
        log.info("Roles check finished.");
    }
}
