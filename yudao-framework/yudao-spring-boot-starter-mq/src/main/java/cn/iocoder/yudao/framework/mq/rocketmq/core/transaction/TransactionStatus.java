package cn.iocoder.yudao.framework.mq.rocketmq.core.transaction;

import org.apache.rocketmq.client.producer.LocalTransactionState;

/**
 * 事务状态枚举
 *
 * @author 芋道源码
 */
public enum TransactionStatus {

    /**
     * 提交事务，消息会被消费
     */
    COMMIT(LocalTransactionState.COMMIT_MESSAGE),

    /**
     * 回滚事务，消息会被删除
     */
    ROLLBACK(LocalTransactionState.ROLLBACK_MESSAGE),

    /**
     * 未知状态，需要事务回查
     */
    UNKNOWN(LocalTransactionState.UNKNOW);

    private final LocalTransactionState localTransactionState;

    TransactionStatus(LocalTransactionState localTransactionState) {
        this.localTransactionState = localTransactionState;
    }

    public LocalTransactionState getLocalTransactionState() {
        return localTransactionState;
    }

    /**
     * 从 RocketMQ LocalTransactionState 转换
     */
    public static TransactionStatus fromLocalTransactionState(LocalTransactionState state) {
        for (TransactionStatus status : values()) {
            if (status.localTransactionState == state) {
                return status;
            }
        }
        return UNKNOWN;
    }

}