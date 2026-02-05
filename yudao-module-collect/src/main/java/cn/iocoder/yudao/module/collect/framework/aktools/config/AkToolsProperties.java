package cn.iocoder.yudao.module.collect.framework.aktools.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

/**
 * AkTools (AkShare HTTP 接口) 配置属性类
 * 用于配置 AkTools 服务的基础连接参数
 *
 * @author ToBy.Qoder
 */
@Component
@ConfigurationProperties(prefix = "aktools")
@Validated
@Data
public class AkToolsProperties {

    /**
     * AkTools 服务基础 URL
     * 例如: http://127.0.0.1:8080
     */
    @NotBlank(message = "AkTools 基础 URL 不能为空")
    private String baseUrl = "http://127.0.0.1:8080";

    /**
     * API 接口前缀路径
     * 例如: /api/public
     */
    @NotBlank(message = "API 前缀不能为空")
    private String apiPrefix = "/api/public";

    /**
     * HTTP 请求超时时间（毫秒）
     * 默认 30 秒
     */
    @Positive(message = "超时时间必须大于 0")
    @NotNull(message = "超时时间不能为空")
    private Integer timeout = 30000;

    /**
     * API 访问密钥（简单认证用）
     * 用于接口访问的身份验证
     */
    private String apiKey;

    /**
     * 连接池配置
     */
    private Pool pool = new Pool();

    /**
     * 连接池配置内部类
     */
    @Data
    public static class Pool {
        /**
         * 连接池最大连接数
         * 默认 200
         */
        @Positive(message = "最大连接数必须大于 0")
        private Integer maxTotal = 200;

        /**
         * 每个路由的默认最大连接数
         * 默认 50
         */
        @Positive(message = "每个路由最大连接数必须大于 0")
        private Integer defaultMaxPerRoute = 50;

        /**
         * 连接超时时间（毫秒）
         * 默认 5 秒
         */
        @Positive(message = "连接超时时间必须大于 0")
        private Integer connectionTimeout = 5000;

        /**
         * 从连接池获取连接的超时时间（毫秒）
         * 默认 2 秒
         */
        @Positive(message = "获取连接超时时间必须大于 0")
        private Integer connectionRequestTimeout = 2000;
    }

    /**
     * 获取完整的 API 基础路径
     *
     * @return 完整的 API 基础路径
     */
    public String getFullApiBaseUrl() {
        return baseUrl + apiPrefix;
    }

}
