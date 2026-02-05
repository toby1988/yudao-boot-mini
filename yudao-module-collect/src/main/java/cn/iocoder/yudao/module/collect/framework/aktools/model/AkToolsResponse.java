package cn.iocoder.yudao.module.collect.framework.aktools.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * AkTools 接口响应通用包装类
 * 用于统一处理 AkTools 接口返回的数据格式
 *
 * @param <T> 数据类型
 * @author ToBy.Qoder
 */
@Data
@Schema(description = "AkTools 接口响应")
public class AkToolsResponse<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 响应状态
     * true: 成功, false: 失败
     */
    @Schema(description = "响应状态", example = "true")
    @JsonProperty("success")
    private Boolean success;

    /**
     * 业务数据
     * 当 success 为 true 时，包含实际的业务数据
     */
    @Schema(description = "业务数据")
    @JsonProperty("data")
    private T data;

    /**
     * 错误码
     * 当 success 为 false 时，包含错误码
     */
    @Schema(description = "错误码", example = "PARAM_ERROR")
    @JsonProperty("code")
    private String code;

    /**
     * 错误消息
     * 当 success 为 false 时，包含错误描述
     */
    @Schema(description = "错误消息", example = "参数格式错误")
    @JsonProperty("message")
    private String message;

    /**
     * 时间戳
     * 响应时间戳（毫秒）
     */
    @Schema(description = "响应时间戳", example = "1707136800000")
    @JsonProperty("timestamp")
    private Long timestamp;

    /**
     * 请求 ID
     * 用于追踪请求的唯一标识
     */
    @Schema(description = "请求 ID", example = "req_1234567890")
    @JsonProperty("requestId")
    private String requestId;

}
