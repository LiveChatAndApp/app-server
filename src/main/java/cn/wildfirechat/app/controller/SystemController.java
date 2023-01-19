package cn.wildfirechat.app.controller;

import cn.wildfirechat.app.pojo.SystemVersionDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/app")
public class SystemController {

    public static final String VERSION = "/version";

    @Value("${system.app.version}")
    private String version;

    @GetMapping(value = VERSION)
    @ResponseBody
    public SystemVersionDto getVersion() {
        return SystemVersionDto.builder().projectCode("APP").projectVersion(version).build();
    }
}
