package personal.cyy.playground.config;


import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashSet;
import java.util.Set;

import static personal.cyy.playground.config.OptLogProperties.PREFIX;


/**
 * 操作日志配置类
 *
 */
@Data
@NoArgsConstructor
@ConfigurationProperties(prefix = PREFIX)
public class OptLogProperties {
    public static final String PREFIX = "xxx.log";

    /**
     * 是否启用
     */
    private Boolean enabled = true;

    /**
     * 是否打印http接口请求日志
     */
    private Boolean enableHttpLog = true;

    /**
     * 不打印http接口请求日志的url
     */
    private Set<String> excludeHttpLogUrls = new HashSet<>();

}


