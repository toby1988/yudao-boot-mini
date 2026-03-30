package cn.iocoder.yudao.framework.mq.rocketmq.core.transaction;

import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.framework.mq.rocketmq.core.interceptor.RocketMQMessageInterceptor;
import cn.iocoder.yudao.framework.mq.rocketmq.core.message.AbstractRocketMQTransactionMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * RocketMQ 事务消息发送模板
 * 
 * 事务消息流程：
 * 1. 发送半消息到 RocketMQ
 * 2. 执行本地事务
 * 3. 根据本地事务结果，提交或回滚消息
 * 4. 如果提交失败，进行事务回查
 *
 * @author 芋道源码
 */
@Slf4j
@RequiredArgsConstructor
public class RocketMQTransactionTemplate {

    private final RocketMQTemplate rocketMQTemplate;

    /**
     * 拦截器列表
     */
    @Getter
    private final List<RocketMQMessageInterceptor> interceptors = new ArrayList<>();

    /**
     * 发送事务消息
     * 
     * 流程：
     * 1. 发送半消息到 RocketMQ
     * 2. RocketMQ 回调 TransactionListener.executeLocalTransaction() 执行本地事务
     * 3. 根据本地事务结果，RocketMQ 决定提交或回滚消息
     *
     * @param message 事务消息
     * @param arg 本地事务参数（可选）
     * @return 事务发送结果
     */
    public TransactionSendResult sendTransactionMessage(AbstractRocketMQTransactionMessage message, Object arg) {
        try {
            // 发送前拦截
            interceptors.forEach(interceptor -> interceptor.sendMessageBefore(message));
            
            Message<String> msg = buildTransactionMessage(message);
            String destination = buildDestination(message);
            
            log.info("[sendTransactionMessage][发送事务消息，topic={}, keys={}, transactionId={}]", 
                    message.getTopic(), message.getKeys(), message.getTransactionId());
            
            TransactionSendResult result = rocketMQTemplate.sendMessageInTransaction(destination, msg, arg);
            
            log.info("[sendTransactionMessage][事务消息发送完成，topic={}, keys={}, result={}]", 
                    message.getTopic(), message.getKeys(), result.getSendStatus());
            
            return result;
        } finally {
            // 发送后拦截（倒序）
            for (int i = interceptors.size() - 1; i >= 0; i--) {
                interceptors.get(i).sendMessageAfter(message);
            }
        }
    }

    /**
     * 发送事务消息（无参数）
     */
    public TransactionSendResult sendTransactionMessage(AbstractRocketMQTransactionMessage message) {
        return sendTransactionMessage(message, null);
    }

    /**
     * 构建事务消息
     */
    private Message<String> buildTransactionMessage(AbstractRocketMQTransactionMessage message) {
        MessageBuilder<String> builder = MessageBuilder.withPayload(JsonUtils.toJsonString(message));
        
        if (message.getKeys() != null) {
            builder.setHeader(RocketMQHeaders.KEYS, message.getKeys());
        }
        if (message.getTags() != null) {
            builder.setHeader(RocketMQHeaders.TAGS, message.getTags());
        }
        if (message.getTransactionId() != null) {
            builder.setHeader(RocketMQHeaders.TRANSACTION_ID, message.getTransactionId());
        }
        
        // 添加自定义 Header
        message.getHeaders().forEach(builder::setHeader);
        
        return builder.build();
    }

    /**
     * 构建目的地（topic:tag 格式）
     */
    private String buildDestination(AbstractRocketMQTransactionMessage message) {
        if (message.getTags() != null && !message.getTags().isEmpty()) {
            return message.getTopic() + ":" + message.getTags();
        }
        return message.getTopic();
    }

    /**
     * 添加拦截器
     */
    public void addInterceptor(RocketMQMessageInterceptor interceptor) {
        interceptors.add(interceptor);
    }

}