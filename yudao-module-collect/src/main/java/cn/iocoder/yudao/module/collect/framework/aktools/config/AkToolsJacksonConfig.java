package cn.iocoder.yudao.module.collect.framework.aktools.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * AkTools Jackson 配置类
 * 配置 Jackson 序列化特性，特别处理金融数据中的 NaN 和 Infinity
 *
 * @author ToBy.Qoder
 */
@Configuration
public class AkToolsJacksonConfig {
    
    private static final Logger log = LoggerFactory.getLogger(AkToolsJacksonConfig.class);

    /**
     * 创建处理 NaN 和 Infinity 的 Jackson 模块
     *
     * @return SimpleModule 实例
     */
    @Bean
    public SimpleModule akToolsJacksonModule() {
        SimpleModule module = new SimpleModule("AkToolsJacksonModule");
        
        // 注册 BigDecimal 反序列化器，处理 NaN 和 Infinity
        module.addDeserializer(BigDecimal.class, new BigDecimalDeserializer());
        
        // 注册 Double 反序列化器，处理 NaN 和 Infinity
        module.addDeserializer(Double.class, new DoubleDeserializer());
        module.addDeserializer(double.class, new DoubleDeserializer());
        
        // 注册 Float 反序列化器，处理 NaN 和 Infinity
        module.addDeserializer(Float.class, new FloatDeserializer());
        module.addDeserializer(float.class, new FloatDeserializer());
        
        log.info("[akToolsJacksonModule][AkTools Jackson 模块初始化完成]");
        return module;
    }

    /**
     * BigDecimal 反序列化器
     * 处理 "NaN"、"Infinity"、"-Infinity" 等特殊情况
     */
    public static class BigDecimalDeserializer extends JsonDeserializer<BigDecimal> {
        private static final Logger log = LoggerFactory.getLogger(BigDecimalDeserializer.class);
        
        @Override
        public BigDecimal deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            String value = p.getText();
            if (value == null || value.isEmpty()) {
                return null;
            }
            
            try {
                // 处理特殊情况
                if ("NaN".equalsIgnoreCase(value) || 
                    "Infinity".equalsIgnoreCase(value) || 
                    "-Infinity".equalsIgnoreCase(value)) {
                    log.debug("[BigDecimalDeserializer][检测到特殊数值] value: {}", value);
                    return BigDecimal.ZERO;
                }
                
                return new BigDecimal(value);
            } catch (NumberFormatException e) {
                log.warn("[BigDecimalDeserializer][数值格式转换失败] value: {}, 使用默认值 BigDecimal.ZERO", value);
                return BigDecimal.ZERO;
            }
        }
    }

    /**
     * Double 反序列化器
     * 处理 "NaN"、"Infinity"、"-Infinity" 等特殊情况
     */
    public static class DoubleDeserializer extends JsonDeserializer<Double> {
        @Override
        public Double deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            String value = p.getText();
            if (value == null || value.isEmpty()) {
                return null;
            }
            
            try {
                // 处理特殊情况
                if ("NaN".equalsIgnoreCase(value)) {
                    log.debug("[DoubleDeserializer][检测到 NaN 值] value: {}", value);
                    return 0.0;
                } else if ("Infinity".equalsIgnoreCase(value)) {
                    log.debug("[DoubleDeserializer][检测到正无穷大值] value: {}", value);
                    return Double.MAX_VALUE;
                } else if ("-Infinity".equalsIgnoreCase(value)) {
                    log.debug("[DoubleDeserializer][检测到负无穷大值] value: {}", value);
                    return -Double.MAX_VALUE;
                }
                
                return Double.parseDouble(value);
            } catch (NumberFormatException e) {
                log.warn("[DoubleDeserializer][数值格式转换失败] value: {}, 使用默认值 0.0", value);
                return 0.0;
            }
        }
    }

    /**
     * Float 反序列化器
     * 处理 "NaN"、"Infinity"、"-Infinity" 等特殊情况
     */
    public static class FloatDeserializer extends JsonDeserializer<Float> {
        @Override
        public Float deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            String value = p.getText();
            if (value == null || value.isEmpty()) {
                return null;
            }
            
            try {
                // 处理特殊情况
                if ("NaN".equalsIgnoreCase(value)) {
                    log.debug("[FloatDeserializer][检测到 NaN 值] value: {}", value);
                    return 0.0f;
                } else if ("Infinity".equalsIgnoreCase(value)) {
                    log.debug("[FloatDeserializer][检测到正无穷大值] value: {}", value);
                    return Float.MAX_VALUE;
                } else if ("-Infinity".equalsIgnoreCase(value)) {
                    log.debug("[FloatDeserializer][检测到负无穷大值] value: {}", value);
                    return -Float.MAX_VALUE;
                }
                
                return Float.parseFloat(value);
            } catch (NumberFormatException e) {
                log.warn("[FloatDeserializer][数值格式转换失败] value: {}, 使用默认值 0.0f", value);
                return 0.0f;
            }
        }
    }

}
