package personal.cyy.playground.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.filter.CommonsRequestLoggingFilter;
import personal.cyy.playground.common.filter.RequestLogPrintFilter;

/**
 * 日志自动配置
 *
 * @author: wcong
 * @date: 2022/11/25 15:17
 */
@EnableAsync
@Configuration
@ConditionalOnWebApplication
@EnableConfigurationProperties(OptLogProperties.class)
public class LogAutoConfiguration {

    @Bean
    @ConditionalOnProperty(prefix = OptLogProperties.PREFIX, name = "enable-http-log", havingValue = "true", matchIfMissing = true)
    public FilterRegistrationBean<CommonsRequestLoggingFilter> logFilterRegistration() {
        CommonsRequestLoggingFilter filter = new RequestLogPrintFilter();
        // 是否打印header中的内容，参数很多
        filter.setIncludeHeaders(false);
        // 是否打印查询字符串内容
        filter.setIncludeQueryString(true);
        // 是否打印 payLoad内容，内部使用的是ContentCachingRequestWrapper读取的request.getInputStream()，必须要先调用@RequstBody，才能取到值。
        filter.setIncludePayload(true);
        // 是否打印客户端信息（ip、session、remoteUser）
        filter.setIncludeClientInfo(true);
        // 1024字节（1kb），超出部分截取
        // 在UTF-8编码方案中，一个英文字符占用一个字节，一个汉字字符占用三个字节的空间。
        filter.setMaxPayloadLength(1024);
        // 设置 before request 日志前缀，默认为：Before request [
        filter.setBeforeMessagePrefix(RequestLogPrintFilter.REQUEST_START_PREFIX_FLAG);
        // 设置 before request 日志后缀，默认为：]
        filter.setBeforeMessageSuffix("]");
        // 设置 before request 日志前缀，默认为：After request [
        filter.setAfterMessagePrefix(RequestLogPrintFilter.REQUEST_END_PREFIX_FLAG);
        // 设置 after request 日志后缀，默认为：]
        filter.setAfterMessageSuffix("]");

        FilterRegistrationBean<CommonsRequestLoggingFilter> registration = new FilterRegistrationBean<>(filter);
        registration.addUrlPatterns("/*");
        registration.setOrder(0);
        registration.setName("commonsRequestLoggingFilter");
        return registration;
    }

}

