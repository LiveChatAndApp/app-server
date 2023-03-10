package cn.wildfirechat.app.sms;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@Configuration
@ConfigurationProperties(prefix="alisms")
@PropertySources({
        @PropertySource(value = "classpath:aliyun_sms.properties", encoding = "UTF-8"),
        @PropertySource(value = "file:config/aliyun_sms.properties", encoding = "UTF-8", ignoreResourceNotFound = true)
})
public class AliyunSMSConfig {
    String accessKeyId;
    String accessSecret;
    String signName;
    String templateCode;

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }

    public String getAccessSecret() {
        return accessSecret;
    }

    public void setAccessSecret(String accessSecret) {
        this.accessSecret = accessSecret;
    }

    public String getSignName() {
        return signName;
    }

    public void setSignName(String signName) {
        this.signName = signName;
    }

    public String getTemplateCode() {
        return templateCode;
    }

    public void setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
    }
}
