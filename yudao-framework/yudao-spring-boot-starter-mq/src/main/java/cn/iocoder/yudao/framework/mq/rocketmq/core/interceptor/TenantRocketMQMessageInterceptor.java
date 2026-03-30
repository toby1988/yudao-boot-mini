package cn.iocoder.yudao.framework.mq.rocketmq.core.interceptor;

import cn.iocoder.yudao.framework.mq.rocketmq.core.message.AbstractRocketMQMessage;
import org.apache.rocketmq.common.message.MessageExt;

/**
 * 多租户 RocketMQ 消息拦截器
 * 自动在消息中添加和提取租户信息
 *
 * 使用方式：
 * 1. 在 Spring 容器中注入此拦截器
 * 2. 发送消息时自动添加租户 ID
 * 3. 消费消息时自动提取租户 ID 并设置到上下文
 *
 * @author 芋道源码
 */
public class TenantRocketMQMessageInterceptor implements RocketMQMessageInterceptor {

    /**
     * 租户 ID Header Key
     */
    private static final String TENANT_ID_HEADER = "tenant-id";

    @Override
    public void sendMessageBefore(AbstractRocketMQMessage message) {
        // 从上下文中获取当前租户 ID
        Long tenantId = getTenantIdFromContext();
        if (tenantId != null) {
            message.addHeader(TENANT_ID_HEADER, String.valueOf(tenantId));
        }
    }

    @Override
    public void consumeMessageBefore(AbstractRocketMQMessage message, MessageExt messageExt) {
        // 从消息头中提取租户 ID
        String tenantIdStr = message.getHeader(TENANT_ID_HEADER);
        if (tenantIdStr != null) {
            try {
                Long tenantId = Long.parseLong(tenantIdStr);
                // 设置到当前上下文
                setTenantIdToContext(tenantId);
            } catch (NumberFormatException e) {
                // 忽略格式错误
            }
        }
    }

    @Override
    public void consumeMessageAfter(AbstractRocketMQMessage message, MessageExt messageExt) {
        // 清理租户上下文
        clearTenantContext();
    }

    /**
     * 从上下文中获取租户 ID
     * 
     * 实际项目中应该从 TenantContextHolder 或类似的上下文持有者中获取
     * 这里提供示例实现
     */
    private Long getTenantIdFromContext() {
        // TODO: 实际项目中应该从 TenantContextHolder 获取
        // return TenantContextHolder.getTenantId();
        return null;
    }

    /**
     * 设置租户 ID 到上下文
     * 
     * @param tenantId 租户 ID
     */
    private void setTenantIdToContext(Long tenantId) {
        // TODO: 实际项目中应该设置到 TenantContextHolder
        // TenantContextHolder.setTenantId(tenantId);
    }

    /**
     * 清理租户上下文
     */
    private void clearTenantContext() {
        // TODO: 实际项目中应该清理 TenantContextHolder
        // TenantContextHolder.clear();
    }

}