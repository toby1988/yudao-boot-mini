package cn.iocoder.yudao.framework.mq.rocketmq.core.interceptor;

import cn.iocoder.yudao.framework.mq.rocketmq.core.message.AbstractRocketMQMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;

/**
 * RocketMQ 默认消息拦截器
 * 提供日志记录、链路追踪等基础功能
 *
 * @author 芋道源码
 */
@Slf4j
public class DefaultRocketMQMessageInterceptor implements RocketMQMessageInterceptor {

    @Override
    public void sendMessageBefore(AbstractRocketMQMessage message) {
        log.debug("[sendMessageBefore][准备发送消息，topic={}, keys={}, tags={}]", 
                message.getTopic(), message.getKeys(), message.getTags());
    }

    @Override
    public void sendMessageAfter(AbstractRocketMQMessage message) {
        log.debug("[sendMessageAfter][消息发送完成，topic={}, keys={}]", 
                message.getTopic(), message.getKeys());
    }

    @Override
    public void consumeMessageBefore(AbstractRocketMQMessage message, MessageExt messageExt) {
        log.debug("[consumeMessageBefore][准备消费消息，topic={}, msgId={}, keys={}, tags={}, retryTimes={}]", 
                messageExt.getTopic(), 
                messageExt.getMsgId(), 
                messageExt.getKeys(), 
                messageExt.getTags(),
                messageExt.getReconsumeTimes());
    }

    @Override
    public void consumeMessageAfter(AbstractRocketMQMessage message, MessageExt messageExt) {
        log.debug("[consumeMessageAfter][消息消费完成，topic={}, msgId={}, keys={}]", 
                messageExt.getTopic(), 
                messageExt.getMsgId(), 
                messageExt.getKeys());
    }

}