package cn.iocoder.yudao.module.collect.framework.aktools.http;

import cn.iocoder.yudao.module.collect.framework.aktools.config.AkToolsProperties;
import lombok.RequiredArgsConstructor;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

/**
 * AkTools RestTemplate 配置类
 * 配置基于连接池的 HTTP 客户端
 *
 * @author ToBy.Qoder
 */
@Configuration
@RequiredArgsConstructor
public class AkToolsRestTemplateConfig {
    
    private static final Logger log = LoggerFactory.getLogger(AkToolsRestTemplateConfig.class);

    private final AkToolsProperties akToolsProperties;

    /**
     * 创建基于连接池的 RestTemplate Bean
     *
     * @return RestTemplate 实例
     */
    @Bean
    public RestTemplate akToolsRestTemplate() {
        try {
            CloseableHttpClient httpClient = createHttpClient();
            HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
            
            // 设置请求工厂超时配置
            factory.setConnectTimeout(akToolsProperties.getPool().getConnectionTimeout());
            factory.setConnectionRequestTimeout(akToolsProperties.getPool().getConnectionRequestTimeout());
            factory.setReadTimeout(akToolsProperties.getTimeout());
            
            RestTemplate restTemplate = new RestTemplate(factory);
            log.info("[akToolsRestTemplate][AkTools RestTemplate 初始化完成] " +
                            "maxTotal: {}, defaultMaxPerRoute: {}, connectTimeout: {}, readTimeout: {}", 
                    akToolsProperties.getPool().getMaxTotal(),
                    akToolsProperties.getPool().getDefaultMaxPerRoute(),
                    akToolsProperties.getPool().getConnectionTimeout(),
                    akToolsProperties.getTimeout());
            
            return restTemplate;
        } catch (Exception e) {
            log.error("[akToolsRestTemplate][RestTemplate 初始化失败]", e);
            throw new RuntimeException("AkTools RestTemplate 初始化失败", e);
        }
    }

    /**
     * 创建配置好的 HttpClient
     *
     * @return CloseableHttpClient 实例
     * @throws NoSuchAlgorithmException SSL算法异常
     * @throws KeyManagementException SSL密钥管理异常
     * @throws KeyStoreException 密钥库异常
     */
    private CloseableHttpClient createHttpClient() throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException {
        // 创建连接池管理器
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(akToolsProperties.getPool().getMaxTotal());
        connectionManager.setDefaultMaxPerRoute(akToolsProperties.getPool().getDefaultMaxPerRoute());
        
        // 配置请求参数
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(akToolsProperties.getPool().getConnectionTimeout())
                .setConnectionRequestTimeout(akToolsProperties.getPool().getConnectionRequestTimeout())
                .setSocketTimeout(akToolsProperties.getTimeout())
                .build();

        // 配置 SSL 上下文（信任所有证书，生产环境应使用正确的证书配置）
        SSLContext sslContext = new SSLContextBuilder()
                .loadTrustMaterial(null, (TrustStrategy) (chain, authType) -> true)
                .build();
        
        SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(
                sslContext, NoopHostnameVerifier.INSTANCE);

        // 构建 HttpClient
        return HttpClients.custom()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(requestConfig)
                .setSSLSocketFactory(socketFactory)
                .evictExpiredConnections()
                .evictIdleConnections(30, java.util.concurrent.TimeUnit.SECONDS)
                .build();
    }

}
