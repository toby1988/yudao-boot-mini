package cn.iocoder.yudao.framework.mq.rocketmq.config;

import cn.iocoder.yudao.framework.mq.rocketmq.core.interceptor.RocketMQMessageInterceptor;
import cn.iocoder.yudao.framework.mq.rocketmq.core.transaction.RocketMQTransactionTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.spring.autoconfigure.RocketMQAutoConfiguration;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * RocketMQ 事务消息配置类
 * 
 * 注意：事务消息需要特殊的 Producer 配置
 * 
 * @author 芋道源码
 */
@Slf4j
@AutoConfiguration(after = {RocketMQAutoConfiguration.class, YudaoRocketMQAutoConfiguration.class})
@ConditionalOnClass(name = "org.apache.rocketmq.spring.core.RocketMQTemplate")
public class RocketMQTransactionAutoConfiguration {

    /**
     * 创建事务消息发送模板
     * 
     * 只有存在 TransactionListener 时才创建
     */
    @Bean
    @ConditionalOnBean(TransactionListener.class)
    @ConditionalOnMissingBean
    public RocketMQTransactionTemplate rocketMQTransactionTemplate(
            RocketMQTemplate rocketMQTemplate,
            List<RocketMQMessageInterceptor> interceptors) {
        
        RocketMQTransactionTemplate template = new RocketMQTransactionTemplate(rocketMQTemplate);
        
        // 注册拦截器
        interceptors.forEach(template::addInterceptor);
        
        log.info("[rocketMQTransactionTemplate][初始化 RocketMQTransactionTemplate 成功]");
        
        return template;
    }

    /**
     * 配置事务回查线程池
     * 
     * 用于事务回查的异步线程池
     */
    @Bean(name = "transactionCheckExecutorService")
    @ConditionalOnBean(TransactionListener.class)
    @ConditionalOnMissingBean(name = "transactionCheckExecutorService")
    public ExecutorService transactionCheckExecutorService() {
        // 创建事务回查线程池
        // 核心线程数建议根据业务并发量调整
        ExecutorService executorService = Executors.newFixedThreadPool(
                Runtime.getRuntime().availableProcessors(),
                r -> {
                    Thread thread = new Thread(r);
                    thread.setName("transaction-check-thread-" + thread.getId());
                    thread.setDaemon(true);
                    return thread;
                }
        );
        
        log.info("[transactionCheckExecutorService][创建事务回查线程池成功]");
        
        return executorService;
    }

}