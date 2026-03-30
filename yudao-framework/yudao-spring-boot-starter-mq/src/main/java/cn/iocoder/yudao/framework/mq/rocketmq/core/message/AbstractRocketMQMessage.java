package cn.iocoder.yudao.framework.mq.rocketmq.core.message;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * RocketMQ 消息抽象基类
 *
 * @author 芋道源码
 */
@Data
public abstract class AbstractRocketMQMessage {

    /**
     * 消息头信息
     */
    private Map<String, String> headers = new HashMap<>();

    /**
     * 消息 Key，用于消息查询和去重
     * 建议使用业务唯一标识
     */
    private String keys;

    /**
     * 消息标签，用于消息过滤
     */
    private String tags;

    public String getHeader(String key) {
        return headers.get(key);
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    /**
     * 获取 Topic 名称，默认使用类名
     *
     * @return Topic 名称
     */
    public String getTopic() {
        return getClass().getSimpleName();
    }

}