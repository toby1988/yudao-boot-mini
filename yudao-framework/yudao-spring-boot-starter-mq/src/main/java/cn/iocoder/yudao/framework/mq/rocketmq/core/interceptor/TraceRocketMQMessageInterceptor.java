package cn.iocoder.yudao.framework.mq.rocketmq.core.interceptor;

import cn.iocoder.yudao.framework.mq.rocketmq.core.message.AbstractRocketMQMessage;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.MDC;

/**
 * 链路追踪 RocketMQ 消息拦截器
 * 自动传递 TraceId，实现分布式链路追踪
 *
 * 使用方式：
 * 1. 在 Spring 容器中注入此拦截器
 * 2. 发送消息时自动添加 TraceId
 * 3. 消费消息时自动提取 TraceId 并设置到 MDC
 *
 * @author 芋道源码
 */
public class TraceRocketMQMessageInterceptor implements RocketMQMessageInterceptor {

    /**
     * TraceId Header Key
     */
    private static final String TRACE_ID_HEADER = "trace-id";

    /**
     * SpanId Header Key
     */
    private static final String SPAN_ID_HEADER = "span-id";

    @Override
    public void sendMessageBefore(AbstractRocketMQMessage message) {
        // 从 MDC 中获取 TraceId
        String traceId = MDC.get("traceId");
        if (traceId != null) {
            message.addHeader(TRACE_ID_HEADER, traceId);
        }

        // 从 MDC 中获取 SpanId
        String spanId = MDC.get("spanId");
        if (spanId != null) {
            message.addHeader(SPAN_ID_HEADER, spanId);
        }
    }

    @Override
    public void consumeMessageBefore(AbstractRocketMQMessage message, MessageExt messageExt) {
        // 从消息头中提取 TraceId 并设置到 MDC
        String traceId = message.getHeader(TRACE_ID_HEADER);
        if (traceId != null) {
            MDC.put("traceId", traceId);
        }

        // 从消息头中提取 SpanId 并设置到 MDC
        String spanId = message.getHeader(SPAN_ID_HEADER);
        if (spanId != null) {
            MDC.put("spanId", spanId);
        }

        // 如果没有 TraceId，生成一个
        if (traceId == null) {
            MDC.put("traceId", generateTraceId());
        }
    }

    @Override
    public void consumeMessageAfter(AbstractRocketMQMessage message, MessageExt messageExt) {
        // 清理 MDC
        MDC.remove("traceId");
        MDC.remove("spanId");
    }

    /**
     * 生成 TraceId
     * 
     * 实际项目中可以使用更复杂的算法，如 UUID、Snowflake 等
     */
    private String generateTraceId() {
        return String.valueOf(System.currentTimeMillis());
    }

}