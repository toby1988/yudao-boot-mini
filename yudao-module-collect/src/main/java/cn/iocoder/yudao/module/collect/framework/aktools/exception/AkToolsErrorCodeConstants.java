package cn.iocoder.yudao.module.collect.framework.aktools.exception;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;

/**
 * AkTools 错误码枚举
 *
 * @author ToBy.Qoder
 */
public interface AkToolsErrorCodeConstants {

    // ========== AkTools 接口调用相关错误码 (2000100000 - 2000199999) ==========
    
    /**
     * AkTools 接口调用失败
     */
    ErrorCode AKTOOLS_API_CALL_FAILED = new ErrorCode(2000100001, "AkTools 接口调用失败");
    
    /**
     * AkTools 接口返回业务错误
     */
    ErrorCode AKTOOLS_BUSINESS_ERROR = new ErrorCode(2000100002, "AkTools 接口业务错误");
    
    /**
     * AkTools 接口认证失败
     */
    ErrorCode AKTOOLS_AUTH_FAILED = new ErrorCode(2000100003, "AkTools 接口认证失败");
    
    /**
     * AkTools 接口请求参数错误
     */
    ErrorCode AKTOOLS_PARAM_ERROR = new ErrorCode(2000100004, "AkTools 接口请求参数错误");
    
    /**
     * AkTools 接口超时
     */
    ErrorCode AKTOOLS_TIMEOUT = new ErrorCode(2000100005, "AkTools 接口调用超时");
    
    /**
     * AkTools 接口网络连接异常
     */
    ErrorCode AKTOOLS_NETWORK_ERROR = new ErrorCode(2000100006, "AkTools 网络连接异常");
    
    /**
     * AkTools 接口响应数据解析失败
     */
    ErrorCode AKTOOLS_PARSE_ERROR = new ErrorCode(2000100007, "AkTools 响应数据解析失败");
    
    /**
     * AkTools 接口返回空数据
     */
    ErrorCode AKTOOLS_EMPTY_DATA = new ErrorCode(2000100008, "AkTools 接口返回空数据");
    
    /**
     * AkTools 接口频率限制
     */
    ErrorCode AKTOOLS_RATE_LIMIT = new ErrorCode(2000100009, "AkTools 接口调用频率超限");
    
    /**
     * AkTools 服务不可用
     */
    ErrorCode AKTOOLS_SERVICE_UNAVAILABLE = new ErrorCode(2000100010, "AkTools 服务暂时不可用");

}
