package cn.wildfirechat.app.util;

import cn.wildfirechat.app.pojo.UserAgentDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.bitwalker.useragentutils.UserAgent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserAgentUtils {
    public static UserAgentDTO getUserAgent(String userAgent) {
        UserAgent ua = UserAgent.parseUserAgentString(userAgent);
        UserAgentDTO dto = UserAgentDTO.builder()
                .browser(ua.getBrowser() != null ? ua.getBrowser().toString() : null)
                .browserVersion(ua.getBrowserVersion() != null ? ua.getBrowserVersion().toString() : null)
                .operatingSystem(ua.getOperatingSystem() != null ? ua.getOperatingSystem().toString() : null)
                .build();
        return dto;
    }

    public static void main(String[] args) throws Exception {
        String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/50.0.2661.0 Safari/537.36";
        UserAgentDTO dto = getUserAgent(userAgent);
        System.out.println(new ObjectMapper().writeValueAsString(dto));
    }
}
