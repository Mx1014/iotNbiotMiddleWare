package cn.usr.middleware.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * redis处理类
 *
 * @author mayadong
 * @date 2018年12月12日
 */
@Component
public class RedisHandle {
    private static Logger log = LoggerFactory.getLogger(RedisHandle.class);

    private static StringRedisTemplate redisTemplate;

    @Resource
    @Qualifier("stringRedisTemplate")
    public void setStringRedisTemplate(StringRedisTemplate redisTemplate) {
        RedisHandle.redisTemplate = redisTemplate;
    }

    public static void setString(String key, String value) {
        log.debug("RedisUtil.setString Key:{}, Value:{}", key, value);
        redisTemplate.opsForValue().set(key, value);
    }

    public static void setStringWithMinutes(String key, String value, int minutes) {
        log.debug("RedisUtil.setStringWithMinutes Key:{}, Value:{}, minutes:{}", key, value, minutes);
        redisTemplate.opsForValue().set(key, value, minutes, TimeUnit.MINUTES);
    }

    public static void setStringWithSeconds(String key, String value, int seconds) {
        log.debug("RedisUtil.setStringWithMinutes Key:{}, Value:{}, seconds:{}", key, value, seconds);
        redisTemplate.opsForValue().set(key, value, seconds, TimeUnit.SECONDS);
    }

    public static void setObject(String key, Object value) {
        String valueJson = "";
        if (value != null) {
            valueJson = JSONObject.toJSONString(value);
        }
        log.debug("RedisUtil.setObject Key:{}, Value:{}", key, valueJson);
        redisTemplate.opsForValue().set(key, valueJson);
    }

    public static void setObjectWithMinutes(String key, Object value, int minutes) {
        String valueJson = "";
        if (value != null) {
            valueJson = JSONObject.toJSONString(value);

        }
        log.debug("RedisUtil.setObject Key:{}, Value:{}, minutes:{}", key, valueJson, minutes);
        redisTemplate.opsForValue().set(key, valueJson, minutes, TimeUnit.MINUTES);
    }

    public static void setListWithMinutes(String key, @SuppressWarnings("rawtypes") List value, int minutes) {
        String valueJson = "";
        if (value != null) {
            valueJson = JSONArray.toJSONString(value);
        }
        log.debug("RedisUtil.setObject Key:{}, Value:{}, minutes:{}", key, valueJson, minutes);
        redisTemplate.opsForValue().set(key, valueJson, minutes, TimeUnit.MINUTES);
    }

    public static void setObjectWithSeconds(String key, Object value, int seconds) {
        String valueJson = "";
        if (value != null) {
            valueJson = JSONObject.toJSONString(value);
        }
        log.debug("RedisUtil.setObject Key:{}, Value:{}, seconds:{}", key, valueJson, seconds);
        redisTemplate.opsForValue().set(key, valueJson, seconds, TimeUnit.SECONDS);
    }

    public static String getString(String key) {
        if (key == null || key.length() == 0) {
            return null;
        }
        String resultStr = redisTemplate.opsForValue().get(key);
        log.debug("RedisUtil.getString Key:{}, Value:{}", key, resultStr);
        return resultStr;
    }

    public static <T> T getObject(String key, Class<T> clazz) {
        if (key == null || key.length() == 0) {
            return null;
        }
        String stringValue = redisTemplate.opsForValue().get(key);
        log.debug("RedisUtil.getObject Key:{}, Value:{}", key, stringValue);
        JSONObject json = (JSONObject) JSONObject.parse(stringValue);
        return JSONObject.toJavaObject(json, clazz);
    }

    /**
     * @param key
     * @param clazz
     * @return
     */
    public static <T> List<T> getJsonArray(String key, Class<T> clazz) {
        if (key == null || key.length() == 0) {
            return null;
        }

        String stringValue = redisTemplate.opsForValue().get(key);
        log.debug("RedisUtil.getObject Key:{}, Value:{}", key, stringValue);
        return JSONArray.parseArray(stringValue, clazz);
    }

    public static void remove(String key) {
        log.debug("RedisUtil.remove Key:{}", key);
        redisTemplate.delete(key);
    }

    public static void expireBySeconds(String key, int i) {
        redisTemplate.expire(key, i, TimeUnit.SECONDS);
    }

    public static void expireByMinutes(String key, int i) {
        redisTemplate.expire(key, i, TimeUnit.MINUTES);
    }

    public static void leftPush(String key, String value) {
        redisTemplate.opsForList().leftPush(key, value);
    }

    public static String rightPop(String key) {
        return redisTemplate.opsForList().rightPop(key);
    }

    public static Long getExpireBySeconds(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    public static Long getExpireByMinutes(String key) {
        return redisTemplate.getExpire(key, TimeUnit.MINUTES);
    }

    public static Boolean isExists(String key) {
        Boolean flag = redisTemplate.hasKey(key);
        return flag == null ? false : flag.booleanValue();
    }

    public static Long incrBy(String key, long value) {
        return redisTemplate.boundValueOps(key).increment(value);
    }

    /**
     * @Title: incr @Description: 默认做自增长 +1操作 @author: XIESHIQING791 @param
     * key @return @return Long @throws
     */
    public static Long incr(String key) {
        return redisTemplate.boundValueOps(key).increment(1);
    }

    public static void hashSet(String key, Object hashKey, Object value) {
        if (value == null) {
            value = "";
        }
        if (!(value instanceof String)) {
            value = String.valueOf(value);
        }
        redisTemplate.opsForHash().put(key, hashKey, value);
    }

    public static Object hashGet(String key, Object hashKey) {
        return redisTemplate.opsForHash().get(key, hashKey);
    }

    public static Map<Object, Object> hashGetAll(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    public static void hashDel(String key, String hashKey) {
        redisTemplate.opsForHash().delete(key, hashKey);
    }

    public static boolean hashHasKey(String key, String hashKey) {
        return redisTemplate.opsForHash().hasKey(key, hashKey);
    }

    public static boolean zsetHasKey(String key, String did) {
        boolean res = false;
        Double num = redisTemplate.opsForZSet().score(key, did);
        if (num != null) {
            res = true;
        }

        return res;
    }

    public static void zsetRemove(String key, String did) {
        Double num = redisTemplate.opsForZSet().score(key, did);
        if (num != null) {
            redisTemplate.opsForZSet().remove(key, did);
        }
    }


}
