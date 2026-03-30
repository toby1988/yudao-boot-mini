package cn.iocoder.yudao.framework.mq.rocketmq.core.listener;

import cn.hutool.core.util.TypeUtil;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.framework.mq.rocketmq.core.YudaoRocketMQTemplate;
import cn.iocoder.yudao.framework.mq.rocketmq.core.interceptor.RocketMQMessageInterceptor;
import cn.iocoder.yudao.framework.mq.rocketmq.core.message.AbstractRocketMQMessage;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

/**
 * RocketMQ 消息监听器抽象类
 * 继承此类并添加 @RocketMQMessageListener 注解即可实现消息消费
 *
 * 使用示例：
 * <pre>
 * {@code
 * @Component
 * @RocketMQMessageListener(
 *     topic = "ORDER_TOPIC",
 *     consumerGroup = "order-consumer-group",
 *     messageModel = MessageModel.CLUSTERING // 集群模式，默认值
 * )
 * public class OrderMessageListener extends AbstractRocketMQMessageListener<OrderMessage> {
 *     @Override
 *     public void onMessage(OrderMessage message) {
 *         // 处理消息
 *     }
 * }
 * }
 * </pre>
 *
 * @param <T> 消息类型
 * @author 芋道源码
 */
@Slf4j
public abstract class AbstractRocketMQMessageListener<T extends AbstractRocketMQMessage>
        implements RocketMQListener<MessageExt> {

    /**
     * 消息类型
     */
    private final Class<T> messageType;

    /**
     * YudaoRocketMQTemplate - 通过 Spring 自动注入
     */
    @Autowired(required = false)
    private YudaoRocketMQTemplate yudaoRocketMQTemplate;

    @SneakyThrows
    protected AbstractRocketMQMessageListener() {
        this.messageType = getMessageClass();
    }

    @Override
    public void onMessage(MessageExt messageExt) {
        T message = null;
        try {
            // 反序列化消息
            String messageBody = new String(messageExt.getBody(), "UTF-8");
            message = JsonUtils.parseObject(messageBody, messageType);
            
            // 设置消息元数据
            if (message != null) {
                message.setKeys(messageExt.getKeys());
                message.setTags(messageExt.getTags());
            }
            
            consumeMessageBefore(message, messageExt);
            
            // 消费消息
            onMessage(message);
            
            log.debug("[onMessage][消费消息成功，topic={}, msgId={}, keys={}]", 
                    messageExt.getTopic(), messageExt.getMsgId(), messageExt.getKeys());
        } catch (Exception e) {
            log.error("[onMessage][消费消息失败，topic={}, msgId={}, keys={}]", 
                    messageExt.getTopic(), messageExt.getMsgId(), messageExt.getKeys(), e);
            // 抛出异常，让 RocketMQ 进行重试
            throw new RuntimeException("消息消费失败", e);
        } finally {
            consumeMessageAfter(message, messageExt);
        }
    }

    /**
     * 处理消息（子类实现）
     *
     * @param message 消息对象
     */
    public abstract void onMessage(T message);

    /**
     * 通过解析类上的泛型，获得消息类型
     *
     * @return 消息类型
     */
    @SuppressWarnings("unchecked")
    private Class<T> getMessageClass() {
        Type type = TypeUtil.getTypeArgument(getClass(), 0);
        if (type == null) {
            throw new IllegalStateException(String.format(
                    "类型(%s) 需要设置消息类型泛型参数", getClass().getName()));
        }
        return (Class<T>) type;
    }

    private void consumeMessageBefore(AbstractRocketMQMessage message, MessageExt messageExt) {
        List<RocketMQMessageInterceptor> interceptors = getInterceptors();
        interceptors.forEach(interceptor -> interceptor.consumeMessageBefore(message, messageExt));
    }

    private void consumeMessageAfter(AbstractRocketMQMessage message, MessageExt messageExt) {
        List<RocketMQMessageInterceptor> interceptors = getInterceptors();
        for (int i = interceptors.size() - 1; i >= 0; i--) {
            interceptors.get(i).consumeMessageAfter(message, messageExt);
        }
    }

    /**
     * 获取拦截器列表
     * 如果 yudaoRocketMQTemplate 未注入，返回空列表
     */
    private List<RocketMQMessageInterceptor> getInterceptors() {
        if (yudaoRocketMQTemplate == null) {
            return Collections.emptyList();
        }
        return yudaoRocketMQTemplate.getInterceptors();
    }

}