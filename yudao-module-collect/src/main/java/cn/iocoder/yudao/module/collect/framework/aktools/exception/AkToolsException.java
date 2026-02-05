package cn.iocoder.yudao.module.collect.framework.aktools.exception;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;
import cn.iocoder.yudao.framework.common.exception.ServiceException;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * AkTools 服务异常类
 * 继承自 yudao 的 ServiceException，用于处理 AkTools 接口调用过程中的各种异常情况
 *
 * @author ToBy.Qoder
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AkToolsException extends ServiceException {

    /**
     * AkTools 错误码
     */
    private String aktoolsCode;

    /**
     * AkTools 错误消息
     */
    private String aktoolsMessage;

    /**
     * HTTP 状态码
     */
    private Integer httpStatusCode;

    public AkToolsException(ErrorCode errorCode) {
        super(errorCode);
    }

    public AkToolsException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public AkToolsException(ErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }

    public AkToolsException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }

    /**
     * 构造函数 - 用于 AkTools 接口返回的业务异常
     *
     * @param errorCode 错误码
     * @param aktoolsCode AkTools 错误码
     * @param aktoolsMessage AkTools 错误消息
     * @param httpStatusCode HTTP 状态码
     */
    public AkToolsException(ErrorCode errorCode, String aktoolsCode, String aktoolsMessage, Integer httpStatusCode) {
        super(errorCode);
        this.aktoolsCode = aktoolsCode;
        this.aktoolsMessage = aktoolsMessage;
        this.httpStatusCode = httpStatusCode;
    }

    /**
     * 构造函数 - 用于网络连接异常
     *
     * @param errorCode 错误码
     * @param message 错误消息
     * @param cause 异常原因
     * @param httpStatusCode HTTP 状态码
     */
    public AkToolsException(ErrorCode errorCode, String message, Throwable cause, Integer httpStatusCode) {
        super(errorCode, message, cause);
        this.httpStatusCode = httpStatusCode;
    }

}
