package cn.iocoder.yudao.framework.mq.rocketmq.core.example;

import cn.iocoder.yudao.framework.mq.rocketmq.core.listener.AbstractRocketMQMessageListener;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.springframework.stereotype.Component;

/**
 * 示例：订单消息监听器（普通消息消费）
 * 
 * @author 芋道源码
 */
@Slf4j
@Component
@RocketMQMessageListener(
    topic = "ORDER_TOPIC",
    consumerGroup = "order-consumer-group"
)
public class OrderMessageListener extends AbstractRocketMQMessageListener<OrderMessage> {

    @Override
    public void onMessage(OrderMessage message) {
        log.info("[onMessage][收到订单消息，orderId={}]", message.getOrderId());
        
        // TODO: 处理业务逻辑
        // 例如：发送通知、记录日志等
        
        log.info("[onMessage][订单消息处理完成，orderId={}]", message.getOrderId());
    }

}

/**
 * 示例：库存消费者（事务消息消费）
 * 
 * 当事务消息提交后，库存服务会收到消息并扣减库存
 */
@Slf4j
@Component
@RocketMQMessageListener(
    topic = "ORDER_TRANSACTION_TOPIC",
    consumerGroup = "inventory-consumer-group"
)
class InventoryMessageListener extends AbstractRocketMQMessageListener<OrderTransactionMessage> {

    // @Autowired
    // private InventoryService inventoryService;

    @Override
    public void onMessage(OrderTransactionMessage message) {
        log.info("[onMessage][收到订单事务消息，开始扣减库存，orderId={}, productId={}, quantity={}]", 
                message.getOrderId(), message.getProductId(), message.getQuantity());
        
        // TODO: 扣减库存
        // inventoryService.deductStock(message.getProductId(), message.getQuantity());
        
        log.info("[onMessage][库存扣减成功，orderId={}]", message.getOrderId());
    }

}