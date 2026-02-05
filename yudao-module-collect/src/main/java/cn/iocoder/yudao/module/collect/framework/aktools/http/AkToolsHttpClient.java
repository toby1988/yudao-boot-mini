package cn.iocoder.yudao.module.collect.framework.aktools.http;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.module.collect.framework.aktools.config.AkToolsProperties;
import cn.iocoder.yudao.module.collect.framework.aktools.exception.AkToolsErrorCodeConstants;
import cn.iocoder.yudao.module.collect.framework.aktools.exception.AkToolsException;
import cn.iocoder.yudao.module.collect.framework.aktools.model.AkToolsResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * AkTools HTTP 客户端
 * 封装 AkTools 接口调用的通用方法
 *
 * @author ToBy.Qoder
 */
@Component
@RequiredArgsConstructor
public class AkToolsHttpClient {
    
    private static final Logger log = LoggerFactory.getLogger(AkToolsHttpClient.class);

    private final RestTemplate akToolsRestTemplate;
    private final AkToolsProperties akToolsProperties;

    /**
     * 执行 GET 请求
     *
     * @param apiPath API 路径（不包含基础 URL 和前缀）
     * @param queryParams 查询参数
     * @param responseType 响应类型
     * @param <T> 泛型类型
     * @return 响应数据
     */
    public <T> T getForObject(String apiPath, Map<String, Object> queryParams, Class<T> responseType) {
        return execute(HttpMethod.GET, apiPath, queryParams, null, responseType);
    }

    /**
     * 执行 POST 请求
     *
     * @param apiPath API 路径
     * @param requestBody 请求体
     * @param responseType 响应类型
     * @param <T> 泛型类型
     * @return 响应数据
     */
    public <T> T postForObject(String apiPath, Object requestBody, Class<T> responseType) {
        return execute(HttpMethod.POST, apiPath, null, requestBody, responseType);
    }

    /**
     * 执行 PUT 请求
     *
     * @param apiPath API 路径
     * @param requestBody 请求体
     * @param responseType 响应类型
     * @param <T> 泛型类型
     * @return 响应数据
     */
    public <T> T putForObject(String apiPath, Object requestBody, Class<T> responseType) {
        return execute(HttpMethod.PUT, apiPath, null, requestBody, responseType);
    }

    /**
     * 执行 DELETE 请求
     *
     * @param apiPath API 路径
     * @param queryParams 查询参数
     * @param responseType 响应类型
     * @param <T> 泛型类型
     * @return 响应数据
     */
    public <T> T deleteForObject(String apiPath, Map<String, Object> queryParams, Class<T> responseType) {
        return execute(HttpMethod.DELETE, apiPath, queryParams, null, responseType);
    }

    /**
     * 执行 HTTP 请求的核心方法
     *
     * @param method HTTP 方法
     * @param apiPath API 路径
     * @param queryParams 查询参数
     * @param requestBody 请求体
     * @param responseType 响应类型
     * @param <T> 泛型类型
     * @return 响应数据
     */
    private <T> T execute(HttpMethod method, String apiPath, Map<String, Object> queryParams,
                         Object requestBody, Class<T> responseType) {
        String url = buildUrl(apiPath, queryParams);
        HttpHeaders headers = buildHeaders();
        HttpEntity<Object> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            log.debug("[execute][准备调用 AkTools 接口] method: {}, url: {}, requestBody: {}", 
                    method, url, JsonUtils.toJsonString(requestBody));

            ResponseEntity<String> response = akToolsRestTemplate.exchange(url, method, requestEntity, String.class);
            
            log.debug("[execute][AkTools 接口调用完成] statusCode: {}, responseBody: {}", 
                    response.getStatusCode(), response.getBody());

            // 解析响应
            return parseResponse(response, responseType);
        } catch (RestClientException e) {
            log.error("[execute][AkTools 接口调用异常] method: {}, url: {}, error: {}", method, url, e.getMessage(), e);
            throw new AkToolsException(AkToolsErrorCodeConstants.AKTOOLS_NETWORK_ERROR, 
                    "网络连接异常", e, null);
        } catch (Exception e) {
            log.error("[execute][AkTools 接口处理异常] method: {}, url: {}, error: {}", method, url, e.getMessage(), e);
            throw new AkToolsException(AkToolsErrorCodeConstants.AKTOOLS_API_CALL_FAILED, 
                    "接口调用失败", e, null);
        }
    }

    /**
     * 构建完整 URL
     *
     * @param apiPath API 路径
     * @param queryParams 查询参数
     * @return 完整 URL
     */
    private String buildUrl(String apiPath, Map<String, Object> queryParams) {
        StringBuilder url = new StringBuilder(akToolsProperties.getFullApiBaseUrl());
        
        if (!apiPath.startsWith("/")) {
            url.append("/");
        }
        url.append(apiPath);

        // 添加查询参数
        if (queryParams != null && !queryParams.isEmpty()) {
            url.append("?");
            queryParams.forEach((key, value) -> {
                if (value != null) {
                    url.append(key).append("=").append(value).append("&");
                }
            });
            // 移除最后一个 &
            if (url.charAt(url.length() - 1) == '&') {
                url.deleteCharAt(url.length() - 1);
            }
        }

        return url.toString();
    }

    /**
     * 构建请求头
     *
     * @return HttpHeaders
     */
    private HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json;charset=UTF-8");
        headers.add("Accept", "application/json");
        
        // 添加 API Key 认证
        if (StrUtil.isNotBlank(akToolsProperties.getApiKey())) {
            headers.add("Authorization", "Bearer " + akToolsProperties.getApiKey());
        }
        
        return headers;
    }

    /**
     * 解析响应数据
     *
     * @param response HTTP 响应
     * @param responseType 响应类型
     * @param <T> 泛型类型
     * @return 解析后的数据
     */
    @SuppressWarnings("unchecked")
    private <T> T parseResponse(ResponseEntity<String> response, Class<T> responseType) {
        String responseBody = response.getBody();
        if (StrUtil.isBlank(responseBody)) {
            throw new AkToolsException(AkToolsErrorCodeConstants.AKTOOLS_EMPTY_DATA, 
                    "接口返回空数据", null, response.getStatusCodeValue());
        }

        try {
            // 如果期望返回字符串，直接返回
            if (responseType == String.class) {
                return (T) responseBody;
            }

            // 解析为 AkToolsResponse 格式
            AkToolsResponse<T> akResponse = JsonUtils.parseObject(responseBody, 
                    JsonUtils.getType(AkToolsResponse.class, responseType));

            // 检查业务状态
            if (!akResponse.isSuccess()) {
                throw new AkToolsException(AkToolsErrorCodeConstants.AKTOOLS_BUSINESS_ERROR,
                        akResponse.getCode(), akResponse.getMessage(), response.getStatusCodeValue());
            }

            return akResponse.getData();
        } catch (AkToolsException e) {
            throw e;
        } catch (Exception e) {
            log.error("[parseResponse][响应数据解析失败] responseBody: {}, error: {}", responseBody, e.getMessage(), e);
            throw new AkToolsException(AkToolsErrorCodeConstants.AKTOOLS_PARSE_ERROR,
                    "响应数据解析失败", e, response.getStatusCodeValue());
        }
    }

}
