package cn.iocoder.yudao.framework.mq.rocketmq.config;

import cn.iocoder.yudao.framework.mq.rocketmq.core.YudaoRocketMQTemplate;
import cn.iocoder.yudao.framework.mq.rocketmq.core.interceptor.RocketMQMessageInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.autoconfigure.RocketMQAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

import java.util.List;

/**
 * RocketMQ 消息队列自动配置类
 *
 * @author 芋道源码
 */
@Slf4j
@AutoConfiguration(after = RocketMQAutoConfiguration.class)
@ConditionalOnClass(name = "org.apache.rocketmq.spring.core.RocketMQTemplate")
@ConditionalOnProperty(prefix = "yudao.rocketmq", name = "enable", havingValue = "true", matchIfMissing = true)
public class YudaoRocketMQAutoConfiguration {

    /**
     * 创建 YudaoRocketMQTemplate Bean
     *
     * @param rocketMQTemplate Spring RocketMQ 模板
     * @param interceptors 消息拦截器列表
     * @return YudaoRocketMQTemplate
     */
    @Bean
    public YudaoRocketMQTemplate yudaoRocketMQTemplate(
            org.apache.rocketmq.spring.core.RocketMQTemplate rocketMQTemplate,
            List<RocketMQMessageInterceptor> interceptors) {
        
        YudaoRocketMQTemplate template = new YudaoRocketMQTemplate(rocketMQTemplate);
        
        // 注册拦截器
        interceptors.forEach(template::addInterceptor);
        
        log.info("[yudaoRocketMQTemplate][初始化 YudaoRocketMQTemplate 成功，拦截器数量={}]", 
                interceptors.size());
        
        return template;
    }

}