package cn.iocoder.yudao.framework.mq.rocketmq.core.transaction;

import cn.hutool.core.util.TypeUtil;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.framework.mq.rocketmq.core.message.AbstractRocketMQTransactionMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.common.message.MessageExt;

import java.lang.reflect.Type;

/**
 * RocketMQ 事务消息监听器抽象类
 * 
 * 开发者需要继承此类并实现两个方法：
 * 1. executeLocalTransaction：执行本地事务
 * 2. checkLocalTransaction：事务回查（可选，默认返回 UNKNOWN）
 *
 * 使用示例：
 * <pre>
 * {@code
 * @Component
 * public class OrderTransactionListener 
 *         extends AbstractRocketMQTransactionListener<OrderTransactionMessage> {
 *     
 *     @Override
 *     protected TransactionStatus executeLocalTransaction(OrderTransactionMessage message) {
 *         try {
 *             // 执行本地事务
 *             orderService.createOrder(message);
 *             return TransactionStatus.COMMIT;
 *         } catch (Exception e) {
 *             return TransactionStatus.ROLLBACK;
 *         }
 *     }
 *     
 *     @Override
 *     protected TransactionStatus checkLocalTransaction(OrderTransactionMessage message) {
 *         // 查询本地事务状态
 *         Order order = orderService.getByOrderId(message.getOrderId());
 *         return order != null ? TransactionStatus.COMMIT : TransactionStatus.ROLLBACK;
 *     }
 * }
 * }
 * </pre>
 *
 * @param <T> 事务消息类型
 * @author 芋道源码
 */
@Slf4j
public abstract class AbstractRocketMQTransactionListener<T extends AbstractRocketMQTransactionMessage> 
        implements TransactionListener {

    /**
     * 消息类型
     */
    private final Class<T> messageType;

    /**
     * 事务执行超时时间（毫秒）
     */
    protected long transactionTimeout = 60000L;

    /**
     * 事务回查最大次数
     */
    protected int checkMaxTimes = 5;

    public AbstractRocketMQTransactionListener() {
        this.messageType = getMessageClass();
    }

    /**
     * 执行本地事务
     * 
     * 当发送半消息成功后，RocketMQ 会回调此方法执行本地事务
     * 
     * @param message 事务消息
     * @return 事务状态
     */
    protected abstract TransactionStatus executeLocalTransaction(T message);

    /**
     * 事务回查
     * 
     * 当本地事务返回 UNKNOWN 或长时间未响应时，RocketMQ 会回调此方法进行事务回查
     * 默认实现返回 UNKNOWN，建议子类重写此方法
     * 
     * @param message 事务消息
     * @return 事务状态
     */
    protected TransactionStatus checkLocalTransaction(T message) {
        log.warn("[checkLocalTransaction][事务回查，默认返回 UNKNOWN，建议子类重写此方法，transactionId={}]", 
                message.getTransactionId());
        return TransactionStatus.UNKNOWN;
    }

    @Override
    public org.apache.rocketmq.client.producer.LocalTransactionState executeLocalTransaction(
            org.apache.rocketmq.common.message.Message msg, Object arg) {
        
        T message = null;
        try {
            // 反序列化消息
            String messageBody = new String(msg.getBody(), "UTF-8");
            message = JsonUtils.parseObject(messageBody, messageType);
            
            // 设置消息元数据
            if (message != null) {
                message.setKeys(msg.getKeys());
                message.setTags(msg.getTags());
                message.setTransactionId(msg.getTransactionId());
            }
            
            log.info("[executeLocalTransaction][开始执行本地事务，transactionId={}, keys={}]", 
                    msg.getTransactionId(), msg.getKeys());
            
            // 执行本地事务
            TransactionStatus status = executeLocalTransaction(message);
            
            log.info("[executeLocalTransaction][本地事务执行完成，transactionId={}, keys={}, status={}]", 
                    msg.getTransactionId(), msg.getKeys(), status);
            
            return status.getLocalTransactionState();
            
        } catch (Exception e) {
            log.error("[executeLocalTransaction][本地事务执行异常，transactionId={}, keys={}]", 
                    msg.getTransactionId(), msg.getKeys(), e);
            return org.apache.rocketmq.client.producer.LocalTransactionState.ROLLBACK_MESSAGE;
        }
    }

    @Override
    public org.apache.rocketmq.client.producer.LocalTransactionState checkLocalTransaction(
            MessageExt msg) {
        
        T message = null;
        try {
            // 反序列化消息
            String messageBody = new String(msg.getBody(), "UTF-8");
            message = JsonUtils.parseObject(messageBody, messageType);
            
            // 设置消息元数据
            if (message != null) {
                message.setKeys(msg.getKeys());
                message.setTags(msg.getTags());
                message.setTransactionId(msg.getTransactionId());
            }
            
            log.info("[checkLocalTransaction][开始事务回查，transactionId={}, keys={}, reconsumeTimes={}]", 
                    msg.getTransactionId(), msg.getKeys(), msg.getReconsumeTimes());
            
            // 执行事务回查
            TransactionStatus status = checkLocalTransaction(message);
            
            log.info("[checkLocalTransaction][事务回查完成，transactionId={}, keys={}, status={}]", 
                    msg.getTransactionId(), msg.getKeys(), status);
            
            return status.getLocalTransactionState();
            
        } catch (Exception e) {
            log.error("[checkLocalTransaction][事务回查异常，transactionId={}, keys={}]", 
                    msg.getTransactionId(), msg.getKeys(), e);
            return org.apache.rocketmq.client.producer.LocalTransactionState.UNKNOW;
        }
    }

    /**
     * 通过解析类上的泛型，获得消息类型
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

}