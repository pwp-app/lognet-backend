package app.pwp.lognet.utils.mail;

import app.pwp.lognet.LognetApplication;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MailTemplate {
    private static HashMap<String, String> templates = new HashMap<>();
    public static void init() throws IOException {
        // 读取邮件模板
        String[] names = { "validation", "validation_forget", "validation_bind" };
        for (String name : names) {
            templates.put(name, IOUtils.toString(new ClassPathResource("mail_templates/template_" + name + ".html").getInputStream(), "UTF-8"));
        }
    }
    // 构造邮件HTML
    public static String build(String template, HashMap<String, String> params) {
        if (!templates.containsKey(template)) {
            throw new RuntimeException("Unable to find a matching email template.");
        }
        String ret = templates.get(template);
        for (Map.Entry<String, String> entry: params.entrySet()) {
            String key = "${" + entry.getKey() + "}";
            while (ret.contains(key)) {
                ret = ret.replace(key, entry.getValue());
            }
        }
        return ret;
    }
}
