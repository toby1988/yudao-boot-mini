package cn.iocoder.yudao.framework.mq.rocketmq.core;

import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.framework.mq.rocketmq.core.interceptor.RocketMQMessageInterceptor;
import cn.iocoder.yudao.framework.mq.rocketmq.core.message.AbstractRocketMQMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * RocketMQ 操作模板类
 * 封装了同步发送、异步发送、单向发送等多种消息发送方式
 *
 * @author 芋道源码
 */
@Slf4j
@RequiredArgsConstructor
public class YudaoRocketMQTemplate {

    private final RocketMQTemplate rocketMQTemplate;

    /**
     * 拦截器列表
     */
    @Getter
    private final List<RocketMQMessageInterceptor> interceptors = new ArrayList<>();

    /**
     * 同步发送消息
     *
     * @param message 消息对象
     * @return 发送结果
     */
    public SendResult syncSend(AbstractRocketMQMessage message) {
        try {
            sendMessageBefore(message);
            Message<String> msg = buildMessage(message);
            String destination = buildDestination(message);
            SendResult sendResult = rocketMQTemplate.syncSend(destination, msg);
            log.debug("[syncSend][发送消息成功，topic={}, keys={}, result={}]",
                    message.getTopic(), message.getKeys(), sendResult);
            return sendResult;
        } finally {
            sendMessageAfter(message);
        }
    }

    /**
     * 异步发送消息
     *
     * @param message 消息对象
     * @param callback 发送回调
     */
    public void asyncSend(AbstractRocketMQMessage message, SendCallback callback) {
        sendMessageBefore(message);
        Message<String> msg = buildMessage(message);
        String destination = buildDestination(message);
        rocketMQTemplate.asyncSend(destination, msg, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                try {
                    log.debug("[asyncSend][发送消息成功，topic={}, keys={}, result={}]",
                            message.getTopic(), message.getKeys(), sendResult);
                    callback.onSuccess(sendResult);
                } finally {
                    sendMessageAfter(message);
                }
            }

            @Override
            public void onException(Throwable e) {
                try {
                    log.error("[asyncSend][发送消息失败，topic={}, keys={}]",
                            message.getTopic(), message.getKeys(), e);
                    callback.onException(e);
                } finally {
                    sendMessageAfter(message);
                }
            }
        });
    }

    /**
     * 单向发送消息（不关心发送结果，适用于日志等场景）
     *
     * @param message 消息对象
     */
    public void sendOneWay(AbstractRocketMQMessage message) {
        try {
            sendMessageBefore(message);
            Message<String> msg = buildMessage(message);
            String destination = buildDestination(message);
            rocketMQTemplate.sendOneWay(destination, msg);
            log.debug("[sendOneWay][发送消息成功，topic={}, keys={}]",
                    message.getTopic(), message.getKeys());
        } finally {
            sendMessageAfter(message);
        }
    }

    /**
     * 同步发送延迟消息
     *
     * @param message 消息对象
     * @param delayLevel 延迟等级 1-18
     * @return 发送结果
     */
    public SendResult syncSendDelayMessage(AbstractRocketMQMessage message, int delayLevel) {
        try {
            sendMessageBefore(message);
            Message<String> msg = buildMessage(message);
            String destination = buildDestination(message);
            SendResult sendResult = rocketMQTemplate.syncSend(destination, msg, 3000, delayLevel);
            log.debug("[syncSendDelayMessage][发送延迟消息成功，topic={}, keys={}, delayLevel={}, result={}]",
                    message.getTopic(), message.getKeys(), delayLevel, sendResult);
            return sendResult;
        } finally {
            sendMessageAfter(message);
        }
    }

    /**
     * 同步发送顺序消息
     *
     * @param message 消息对象
     * @param hashKey 哈希键，用于选择队列
     * @return 发送结果
     */
    public SendResult syncSendOrderly(AbstractRocketMQMessage message, String hashKey) {
        try {
            sendMessageBefore(message);
            Message<String> msg = buildMessage(message);
            String destination = buildDestination(message);
            SendResult sendResult = rocketMQTemplate.syncSendOrderly(destination, msg, hashKey);
            log.debug("[syncSendOrderly][发送顺序消息成功，topic={}, keys={}, hashKey={}, result={}]",
                    message.getTopic(), message.getKeys(), hashKey, sendResult);
            return sendResult;
        } finally {
            sendMessageAfter(message);
        }
    }

    /**
     * 添加拦截器
     *
     * @param interceptor 拦截器
     */
    public void addInterceptor(RocketMQMessageInterceptor interceptor) {
        interceptors.add(interceptor);
    }

    /**
     * 构建消息对象
     */
    private Message<String> buildMessage(AbstractRocketMQMessage message) {
        MessageBuilder<String> builder = MessageBuilder.withPayload(JsonUtils.toJsonString(message));

        if (message.getKeys() != null) {
            builder.setHeader(RocketMQHeaders.KEYS, message.getKeys());
        }
        if (message.getTags() != null) {
            builder.setHeader(RocketMQHeaders.TAGS, message.getTags());
        }

        message.getHeaders().forEach(builder::setHeader);

        return builder.build();
    }

    /**
     * 构建目的地（topic:tag 格式）
     */
    private String buildDestination(AbstractRocketMQMessage message) {
        if (message.getTags() != null && !message.getTags().isEmpty()) {
            return message.getTopic() + ":" + message.getTags();
        }
        return message.getTopic();
    }

    private void sendMessageBefore(AbstractRocketMQMessage message) {
        interceptors.forEach(interceptor -> interceptor.sendMessageBefore(message));
    }

    private void sendMessageAfter(AbstractRocketMQMessage message) {
        for (int i = interceptors.size() - 1; i >= 0; i--) {
            interceptors.get(i).sendMessageAfter(message);
        }
    }

}