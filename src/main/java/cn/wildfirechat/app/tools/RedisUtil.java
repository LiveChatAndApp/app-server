package cn.wildfirechat.app.tools;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Redis工具类
 * 参考：https://blog.csdn.net/justry_deng/article/details/104890620/
 */
@Slf4j
@Component
@SuppressWarnings("unchecked")
public class RedisUtil {
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 删除对应的value
     */
    public void delete(final String key) {
        if (exists(key)) {
            redisTemplate.delete(key);
        }
    }

    /**
     * 批量删除对应的value
     */
    public void delete(final String... keys) {
        for (String key : keys) {
            delete(key);
        }
    }

    /**
     * 批量删除key
     */
    public void deletePattern(final String pattern) {
        Set<Serializable> keys = redisTemplate.keys(pattern);
        if (keys != null && keys.size() > 0) {
            redisTemplate.delete(keys);
        }
    }

    /**
     * 判断缓存中是否有对应的value
     */
    public boolean exists(final String key) {
        Boolean hasKey = redisTemplate.hasKey(key);
        return hasKey != null && hasKey;
    }

    /**
     * 读取缓存
     */
    public Object get(final String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 写入缓存
     */
    public boolean set(final String key, Object value) {
        boolean result = false;
        try {
            redisTemplate.opsForValue().set(key, value);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 写入缓存（timeout单位：秒）
     */
    public boolean set(final String key, Object value, long timeout) {
        return set(key, value, timeout, TimeUnit.SECONDS);
    }

    /**
     * 写入缓存
     */
    public boolean set(final String key, Object value, long timeout, TimeUnit unit) {
        boolean result = false;
        try {
            redisTemplate.opsForValue().set(key, value);
            redisTemplate.expire(key, timeout, unit);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 写入缓存
     */
    public boolean set(final String key, Object value, Date date) {
        boolean result = false;
        try {
            redisTemplate.opsForValue().set(key, value);
            redisTemplate.expireAt(key, date);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 写入缓存
     */
    public boolean setExpireAt(final String key, Date date) {
        boolean result = false;
        try {
            redisTemplate.expireAt(key, date);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 删除Hash缓存
     */
    public void deleteHash(String key, String hashKey) {
        redisTemplate.opsForHash().delete(key, hashKey);
    }

    /**
     * 读取Hash缓存
     */
    public Object getHash(final String key, final String hashKey) {
        return redisTemplate.opsForHash().get(key, hashKey);
    }

    /**
     * 读取Hash缓存
     */
    public List<?> getHash(final String key) {
        Map<String, Object> entries = redisTemplate.opsForHash().entries(key);
        return new ArrayList<>(entries.values());
    }

    /**
     * 写入Hash缓存
     */
    public boolean setHash(final String key, final String hashKey, Object value) {
        boolean result = false;
        try {
            redisTemplate.opsForHash().put(key, hashKey, value);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 写入Hash缓存
     */
    public boolean setHash(final String key, Map<String, ?> map, Long timeout) {
        boolean result = false;
        try {
            redisTemplate.opsForHash().putAll(key, map);
            if (timeout != 0) {
                redisTemplate.expire(key, timeout, TimeUnit.SECONDS);
            }
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 自增计数器
     */
    public long increment(final String key, long delta) {
        Long val = redisTemplate.opsForValue().increment(key, delta);
        return Optional.ofNullable(val).orElse(0L);
    }

    /**
     * 自增计数器
     */
    public double increment(final String key, double delta) {
        Double val = redisTemplate.opsForValue().increment(key, delta);
        return Optional.ofNullable(val).orElse(0D);
    }

    public RedisTemplate getRedisTemplate() {
        return this.redisTemplate;
    }

    /**
     * 由数字排序需求使用
     */
    public boolean setZSet(String key, Object value, double score) {
        boolean result = false;
        try {
            redisTemplate.opsForZSet().add(key, value, score);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 由数字排序需求使用(由小到大)
     */
    public Set getZSet(String key, double minScore, double maxScore, long offset, long count) {
        boolean result = false;
        try {
            return redisTemplate.opsForZSet().rangeByScore(key, minScore, maxScore, offset, count);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 由数字排序需求使用(由大到小)
     */
    public Set getRevZSet(String key, double minScore, double maxScore, long offset, long count) {
        boolean result = false;
        try {
            return redisTemplate.opsForZSet().reverseRangeByScore(key, minScore, maxScore, offset, count);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获得指定元素的分数
     */
    public Double getZSetScore(String key, String object) {
        boolean result = false;
        try {
            return redisTemplate.opsForZSet().score(key, object);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Long deleteZSet(String key, Object... objects) {
        Long result = 0L;
        try {
            result = redisTemplate.opsForZSet().remove(key, objects);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
