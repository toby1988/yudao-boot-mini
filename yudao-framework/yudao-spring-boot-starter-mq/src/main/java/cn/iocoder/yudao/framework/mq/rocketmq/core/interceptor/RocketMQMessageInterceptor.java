package cn.iocoder.yudao.framework.mq.rocketmq.core.interceptor;

import cn.iocoder.yudao.framework.mq.rocketmq.core.message.AbstractRocketMQMessage;
import org.apache.rocketmq.common.message.MessageExt;

/**
 * RocketMQ 消息拦截器
 * 通过拦截器机制实现扩展，例如：多租户、链路追踪、日志记录等
 *
 * 同时支持普通消息和事务消息
 *
 * @author 芋道源码
 */
public interface RocketMQMessageInterceptor {

    /**
     * 发送消息前拦截
     *
     * @param message 消息对象（普通消息或事务消息）
     */
    default void sendMessageBefore(AbstractRocketMQMessage message) {
    }

    /**
     * 发送消息后拦截
     *
     * @param message 消息对象（普通消息或事务消息）
     */
    default void sendMessageAfter(AbstractRocketMQMessage message) {
    }

    /**
     * 消费消息前拦截
     *
     * @param message 消息对象
     * @param messageExt RocketMQ 原始消息对象
     */
    default void consumeMessageBefore(AbstractRocketMQMessage message, MessageExt messageExt) {
    }

    /**
     * 消费消息后拦截
     *
     * @param message 消息对象
     * @param messageExt RocketMQ 原始消息对象
     */
    default void consumeMessageAfter(AbstractRocketMQMessage message, MessageExt messageExt) {
    }

}