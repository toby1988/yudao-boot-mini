package cn.iocoder.yudao.framework.mq.rocketmq.core.example;

import cn.iocoder.yudao.framework.mq.rocketmq.core.message.AbstractRocketMQMessage;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 示例：订单消息（普通消息）
 * 
 * @author 芋道源码
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class OrderMessage extends AbstractRocketMQMessage {

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
        return "ORDER_TOPIC";
    }

    /**
     * 静态工厂方法
     */
    public static OrderMessage of(Long orderId, Long userId, 
            Long productId, Integer quantity, Integer amount) {
        OrderMessage message = new OrderMessage();
        message.setOrderId(orderId);
        message.setUserId(userId);
        message.setProductId(productId);
        message.setQuantity(quantity);
        message.setAmount(amount);
        // 设置消息 Key
        message.setKeys("ORDER_" + orderId);
        // 设置消息 Tag
        message.setTags("ORDER_CREATE");
        return message;
    }

}