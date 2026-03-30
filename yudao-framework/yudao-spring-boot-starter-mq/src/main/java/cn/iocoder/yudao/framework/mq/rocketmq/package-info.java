/**
 * RocketMQ 消息队列封装
 * 
 * 提供了统一的消息发送和消费抽象，支持：
 * 1. 同步发送、异步发送、单向发送
 * 2. 延迟消息、顺序消息
 * 3. 消息拦截器机制
 * 4. 自动序列化/反序列化
 * 5. 集群消费和广播消费
 */
package cn.iocoder.yudao.framework.mq.rocketmq;