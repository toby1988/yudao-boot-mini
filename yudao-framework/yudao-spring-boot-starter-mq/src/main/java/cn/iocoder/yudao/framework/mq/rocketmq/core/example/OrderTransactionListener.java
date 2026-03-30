package cn.iocoder.yudao.framework.mq.rocketmq.core.example;

import cn.iocoder.yudao.framework.mq.rocketmq.core.transaction.AbstractRocketMQTransactionListener;
import cn.iocoder.yudao.framework.mq.rocketmq.core.transaction.TransactionStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 示例：订单事务监听器
 * 
 * 实现订单创建的分布式事务：
 * 1. 本地事务：创建订单记录
 * 2. 消息消费：扣减库存（由库存服务消费）
 * 
 * @author 芋道源码
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderTransactionListener 
        extends AbstractRocketMQTransactionListener<OrderTransactionMessage> {

    // @Autowired
    // private OrderService orderService;

    /**
     * 执行本地事务：创建订单
     */
    @Override
    protected TransactionStatus executeLocalTransaction(OrderTransactionMessage message) {
        log.info("[executeLocalTransaction][开始执行本地事务，orderId={}]", message.getOrderId());
        
        try {
            // TODO: 实际业务代码
            // 1. 创建订单记录
            // Order order = new Order();
            // order.setId(message.getOrderId());
            // order.setUserId(message.getUserId());
            // order.setProductId(message.getProductId());
            // order.setQuantity(message.getQuantity());
            // order.setAmount(message.getAmount());
            // order.setStatus(OrderStatusEnum.CREATED.getStatus());
            // orderService.save(order);
            
            // 2. 模拟业务逻辑
            Thread.sleep(100);
            
            log.info("[executeLocalTransaction][本地事务执行成功，orderId={}]", message.getOrderId());
            
            // 本地事务成功，提交消息
            return TransactionStatus.COMMIT;
            
        } catch (Exception e) {
            log.error("[executeLocalTransaction][本地事务执行失败，orderId={}]", 
                    message.getOrderId(), e);
            
            // 本地事务失败，回滚消息
            return TransactionStatus.ROLLBACK;
        }
    }

    /**
     * 事务回查：查询订单是否创建成功
     * 
     * 场景：本地事务执行成功，但提交消息失败（网络问题等）
     * RocketMQ 会定期回调此方法查询事务状态
     */
    @Override
    protected TransactionStatus checkLocalTransaction(OrderTransactionMessage message) {
        log.info("[checkLocalTransaction][开始事务回查，orderId={}]", message.getOrderId());
        
        try {
            // TODO: 实际业务代码
            // 查询订单是否存在
            // Order order = orderService.getById(message.getOrderId());
            // if (order != null) {
            //     log.info("[checkLocalTransaction][订单存在，提交消息，orderId={}]", message.getOrderId());
            //     return TransactionStatus.COMMIT;
            // } else {
            //     log.info("[checkLocalTransaction][订单不存在，回滚消息，orderId={}]", message.getOrderId());
            //     return TransactionStatus.ROLLBACK;
            // }
            
            // 示例代码：模拟订单存在
            log.info("[checkLocalTransaction][事务回查完成，orderId={}，提交消息]", message.getOrderId());
            return TransactionStatus.COMMIT;
            
        } catch (Exception e) {
            log.error("[checkLocalTransaction][事务回查异常，orderId={}]", 
                    message.getOrderId(), e);
            
            // 异常情况返回 UNKNOWN，等待下次回查
            return TransactionStatus.UNKNOWN;
        }
    }

}