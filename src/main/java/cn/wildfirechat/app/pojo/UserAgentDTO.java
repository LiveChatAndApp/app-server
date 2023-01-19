package cn.wildfirechat.app.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAgentDTO implements Serializable {
    private String browser;
    private String browserVersion;
    private String operatingSystem;
}
