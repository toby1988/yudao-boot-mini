package cn.iocoder.yudao.framework.mq.rocketmq.core.example;

import cn.iocoder.yudao.framework.mq.rocketmq.core.YudaoRocketMQTemplate;
import cn.iocoder.yudao.framework.mq.rocketmq.core.transaction.RocketMQTransactionTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.springframework.stereotype.Component;

/**
 * 示例：RocketMQ 使用示例
 * 
 * @author 芋道源码
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RocketMQExample {

    private final YudaoRocketMQTemplate yudaoRocketMQTemplate;
    private final RocketMQTransactionTemplate transactionTemplate;

    /**
     * 示例1：同步发送普通消息
     */
    public void syncSendMessage() {
        OrderMessage message = OrderMessage.of(
                1L, 1001L, 2001L, 2, 9999
        );
        
        SendResult result = yudaoRocketMQTemplate.syncSend(message);
        
        log.info("[syncSendMessage][消息发送成功，msgId={}]", result.getMsgId());
    }

    /**
     * 示例2：发送延迟消息
     */
    public void sendDelayMessage() {
        OrderMessage message = OrderMessage.of(
                2L, 1002L, 2002L, 1, 8888
        );
        
        // 延迟等级 16 = 30分钟后消费
        SendResult result = yudaoRocketMQTemplate.syncSendDelayMessage(message, 16);
        
        log.info("[sendDelayMessage][延迟消息发送成功，msgId={}]", result.getMsgId());
    }

    /**
     * 示例3：发送顺序消息
     */
    public void sendOrderlyMessage() {
        OrderMessage message = OrderMessage.of(
                3L, 1003L, 2003L, 3, 7777
        );
        
        // 使用订单ID作为哈希键，保证同一订单的消息发送到同一队列
        SendResult result = yudaoRocketMQTemplate.syncSendOrderly(message, "ORDER_3");
        
        log.info("[sendOrderlyMessage][顺序消息发送成功，msgId={}]", result.getMsgId());
    }

    /**
     * 示例4：发送事务消息
     * 
     * 事务消息流程：
     * 1. 发送半消息到 RocketMQ
     * 2. RocketMQ 回调 OrderTransactionListener.executeLocalTransaction() 执行本地事务
     * 3. 本地事务成功 -> 提交消息 -> 库存服务消费消息 -> 扣减库存
     * 4. 本地事务失败 -> 回滚消息 -> 库存服务不会收到消息
     */
    public void sendTransactionMessage() {
        OrderTransactionMessage message = OrderTransactionMessage.of(
                4L, 1004L, 2004L, 1, 6666
        );
        
        // 发送事务消息
        TransactionSendResult result = transactionTemplate.sendTransactionMessage(message);
        
        // 检查本地事务结果
        if (LocalTransactionState.COMMIT_MESSAGE == result.getLocalTransactionState()) {
            log.info("[sendTransactionMessage][事务消息提交成功，orderId={}]", message.getOrderId());
        } else {
            log.warn("[sendTransactionMessage][事务消息回滚，orderId={}]", message.getOrderId());
        }
    }

}