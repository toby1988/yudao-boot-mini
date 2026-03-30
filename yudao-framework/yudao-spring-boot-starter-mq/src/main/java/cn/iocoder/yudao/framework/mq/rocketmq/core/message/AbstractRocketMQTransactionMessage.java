package cn.iocoder.yudao.framework.mq.rocketmq.core.message;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * RocketMQ 事务消息抽象基类
 * 
 * 事务消息适用于需要保证本地事务和消息发送一致性的场景
 * 例如：订单创建 + 库存扣减、支付成功 + 订单状态更新等
 *
 * @author 芋道源码
 */
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class AbstractRocketMQTransactionMessage extends AbstractRocketMQMessage {

    /**
     * 事务ID，用于事务回查
     * 建议使用业务唯一标识，如订单号
     */
    private String transactionId;

}