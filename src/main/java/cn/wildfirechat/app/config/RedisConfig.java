package cn.wildfirechat.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * redis 工具类
 */
@Configuration
public class RedisConfig {

//    public static final String ADD_MEMBER_COUNT_CURR_DATE_KEY = "addMemberCountCurrDate";
//    public static final String ADD_GROUP_COUNT_CURR_DATE_KEY = "addGroupCountCurrDate";
//    public static final String MESSAGE_COUNT_CURR_DATE_KEY = "messageCountCurrDate";
    public static final String ACTIVE_MEMBER_KEY = "activeMember"; //ZSet
    public static final String ACTIVE_GROUP_KEY = "activeGroup"; //ZSet

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
//        RedisTemplate<String, String> template = new RedisTemplate<>();
        RedisSerializer<String> redisSerializer = new StringRedisSerializer();

        template.setConnectionFactory(factory);
        // Add some specific configuration here. Key serializers, etc.
        //key序列化方式
        template.setKeySerializer(redisSerializer);
        //value序列化
        template.setValueSerializer(redisSerializer);
        //value hashmap序列化
        template.setHashValueSerializer(redisSerializer);
        //key haspmap序列化
        template.setHashKeySerializer(redisSerializer);

        return template;
    }

}