package personal.cyy.playground.common.filter;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.web.filter.CommonsRequestLoggingFilter;
import personal.cyy.playground.config.OptLogProperties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Set;

/**
 * 自定义的请求日志打印过滤器，打印所有接口请求参数、耗时等日志 <br/>
 *
 * 对比其他两种方式：<br/>
 * <ul>
 *     <li>1、aop的方式：必须要到controller层，才会打印, 有些接口在 过滤器、拦截器层被拦截</li>
 *     <li>2、拦截器的方式：request.getInputStream()只能调用一次，解决的方法为copy一份流，个人感觉在代码逻辑层面不太清晰</li>
 * </ul>
 *
 *  缺点：<br/>
 *  <ul>
 *      <li>1、无法获取返回值，适合一些不关心返回值的场景。</li>
 *      <li>2. 内部使用的是ContentCachingRequestWrapper读取的request.getInputStream()，必须要先调用@RequstBody，才能取到值。</li>
 *  </ul>
 *
 *  优点：<br/>
 *  <ul>
 *      <li>1、 实现简单，代码层次逻辑清晰。</li>
 *  </ul>
 *
 * @author: wcong
 * @date: 2022/11/25 15:17
 */
@Slf4j
public class RequestLogPrintFilter extends CommonsRequestLoggingFilter {

    /**
     * 接口请求 开始 日志前缀标识，目的是为了提高每次判断时的性能
     */
    public final static String REQUEST_START_PREFIX_FLAG = "0";
    /**
     * 接口请求 开始 日志前缀
     */
    private final static String REQUEST_START_PREFIX = "### request start[";
    /**
     * 接口请求 结束 日志前缀标识，目的是为了提高每次判断时的性能
     */
    public final static String REQUEST_END_PREFIX_FLAG = "1";
    /**
     * 接口请求 结束 日志前缀
     */
    private final static String REQUEST_END_PREFIX = "### request end[";

    /**
     * 是否为prod环境
     */
    private static final boolean IS_PROD_EVN;
    /**
     * 不打印接口请求日志的url集合
     */
    private static final Set<String> EXCLUDE_HTTP_LOG_URLS;
    static {
        final String activeProfile = SpringUtil.getActiveProfile();
        final OptLogProperties optLogProperties = SpringUtil.getBean(OptLogProperties.class);
        IS_PROD_EVN = "prod".equals(activeProfile);
        EXCLUDE_HTTP_LOG_URLS = optLogProperties.getExcludeHttpLogUrls();
    }

    /**
     * 重写父类方法：封装打印消息的格式
     */
    @Override
    protected String createMessage(HttpServletRequest request, String prefix, String suffix) {
        // 是否为不打印的url
        if (isExcludeLogUrl((request.getRequestURI()))) {
            return null;
        }

        final StringBuilder messageInfo = getMessageInfo(request, prefix, suffix);
        // 请求开始还是结束
        if (REQUEST_START_PREFIX_FLAG.equals(prefix)) {
            // 请求开始
            MDC.put("logStartTime", String.valueOf(System.currentTimeMillis()));
        } else {
            // 请求结束，记录耗时
            final Long logStartTime = Convert.toLong(MDC.get("logStartTime"), 0L);
            messageInfo.append("\r\n接口耗时: ").append(System.currentTimeMillis() - logStartTime).append("ms");
        }
        return messageInfo.toString();
    }

    /**
     * 重写父类方法：请求前调用逻辑
     */
    @Override
    protected void beforeRequest(HttpServletRequest request, String message) {
        // 是否为不打印的url
        if (isExcludeLogUrl((request.getRequestURI()))) {
            return;
        }
        doPrintLog(message);
    }

    /**
     * 重写父类方法：请求后调用逻辑
     */
    @Override
    protected void afterRequest(HttpServletRequest request, String message) {
        // 是否为不打印的url
        if (isExcludeLogUrl((request.getRequestURI()))) {
            return;
        }
        doPrintLog(message);
    }
    /**
     * 重写父类方法：是否打印日志
     */
    @Override
    protected boolean shouldLog(HttpServletRequest request) {
        // 父类中的逻辑是：logger.isDebugEnabled()
        return true;
    }

    /**
     * 统一封装打印的日志格式
     *
     * @param request   javax.servlet.http.HttpServletRequest
     * @param prefix    打印前缀
     * @param suffix    打印后缀
     * @return 封装好的日志格式
     */
    private StringBuilder getMessageInfo(HttpServletRequest request, String prefix, String suffix) {
        StringBuilder msg = new StringBuilder();

        // 判断是 请求开始 还是 请求结束
        if (REQUEST_START_PREFIX_FLAG.equals(prefix)) {
            msg.append(REQUEST_START_PREFIX);
        } else {
            msg.append(REQUEST_END_PREFIX);
        }

        msg.append(StrUtil.format("method={}; ", request.getMethod().toLowerCase()));
        msg.append("uri=").append(request.getRequestURI());

        // 是否有传递 查询字符串 信息
        if (isIncludeQueryString()) {
            String queryString = request.getQueryString();
            if (queryString != null) {
                msg.append('?').append(queryString);
            }
        }

        // 是否有传递 payload 信息
        if (isIncludePayload()) {
            String payload = getMessagePayload(request);
            if (payload != null) {
                msg.append("; payload=").append(payload);
            }
        }

        // 是否包含 客户端 信息
        if (isIncludeClientInfo()) {
            String client = request.getRemoteAddr();
            if (StrUtil.isNotBlank(client)) {
                msg.append("; client=").append(client);
            }
            HttpSession session = request.getSession(false);
            if (session != null) {
                msg.append("; session=").append(session.getId());
            }
            String user = request.getRemoteUser();
            if (user != null) {
                msg.append("; user=").append(user);
            }
        }

        msg.append(suffix);
        return msg;
    }

    /**
     * 具体打印的方法
     *
     * @param message   打印的消息
     */
    private void doPrintLog(String message) {
        // 生产环境打印debug级别
        if (IS_PROD_EVN) {
            log.debug(message);
        } else {
            log.info(message);
        }
        // log.info(message);
    }


    private Boolean isExcludeLogUrl(String url) {
        return EXCLUDE_HTTP_LOG_URLS.contains(url);
    }

}
