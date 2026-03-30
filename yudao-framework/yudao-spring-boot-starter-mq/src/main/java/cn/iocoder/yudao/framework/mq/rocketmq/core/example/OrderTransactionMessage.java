package cn.iocoder.yudao.framework.mq.rocketmq.core.example;

import cn.iocoder.yudao.framework.mq.rocketmq.core.message.AbstractRocketMQTransactionMessage;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 示例：订单事务消息
 * 
 * 用于实现订单创建 + 库存扣减的分布式事务
 * 
 * @author 芋道源码
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class OrderTransactionMessage extends AbstractRocketMQTransactionMessage {

    /**
     * 订单编号
     */
    private Long orderId;
    
    /**
     * 用户编号
     */
    private Long userId;
    
    /**
     * 商品编号
     */
    private Long productId;
    
    /**
     * 购买数量
     */
    private Integer quantity;
    
    /**
     * 订单金额
     */
    private Integer amount;

    /**
     * 重写 Topic
     */
    @Override
    public String getTopic() {
        return "ORDER_TRANSACTION_TOPIC";
    }

    /**
     * 静态工厂方法
     */
    public static OrderTransactionMessage of(Long orderId, Long userId, 
            Long productId, Integer quantity, Integer amount) {
        OrderTransactionMessage message = new OrderTransactionMessage();
        message.setOrderId(orderId);
        message.setUserId(userId);
        message.setProductId(productId);
        message.setQuantity(quantity);
        message.setAmount(amount);
        // 设置消息 Key
        message.setKeys("ORDER_TX_" + orderId);
        // 设置消息 Tag
        message.setTags("ORDER_CREATE");
        // 设置事务 ID（建议使用业务唯一标识）
        message.setTransactionId("TX_ORDER_" + orderId + "_" + System.currentTimeMillis());
        return message;
    }

}