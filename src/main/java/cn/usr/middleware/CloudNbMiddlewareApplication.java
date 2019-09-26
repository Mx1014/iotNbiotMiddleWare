package cn.usr.middleware;


import cn.usr.middleware.mqtt.UsrCloudMqttClient;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Map;


/**
 * @author Administrator
 */
@SpringBootApplication
@ComponentScan({"cn.usr"})
@EnableScheduling
@MapperScan("cn.usr.middleware.dao")
@Slf4j
public class CloudNbMiddlewareApplication {


    @Value("${cn.device.offlineTime}")
    private Integer offlineTime;

    @Value("${cn.device.lastActionKey}")
    private String lastActionKey;

    /**
     * 定时时间
     */
    private final static int fixedRate = 1000 * 60;

//    @Autowired
//    private DataHandlerImpl dataHandler;


    public static ConfigurableApplicationContext applicationContext;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public static void main(String[] args) {

        applicationContext = SpringApplication.run(CloudNbMiddlewareApplication.class, args);

        UsrCloudMqttClient usrCloudMqttClient = applicationContext.getBeanFactory().getBean(UsrCloudMqttClient.class);
        try {
            usrCloudMqttClient.Connect();
        } catch (MqttException e) {
            e.printStackTrace();
            log.error("mqtt 客户端连接失败！！！{}", e);
        }
    }

    //        @Scheduled(fixedRate = fixedRate)
    private void checkMqttPushStatus() {
        Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries(lastActionKey);

        try {
            for (Map.Entry<Object, Object> entry : entries.entrySet()) {
                log.info("循环检测设备的在线时间比较，设备ID为：{},设备的时间为：{}", entry.getKey(), entry.getValue());
                // 如果当前时间减去存储的时间大于设定的时间那么就代表设备没有下线咯
                if (System.currentTimeMillis() - (Long) entry.getValue() > offlineTime) {

                    log.info("设备超过{}时间内没有动作，推送设备下线并删除设备的记录，设备ID为：{}", offlineTime, entry.getKey());
                    // 发送下线
//                    dataHandler.sendDeviceOfflineStateToCloud((String) entry.getKey());

                    // 删除记录
                    stringRedisTemplate.opsForHash().delete(lastActionKey, (String) entry.getKey());

                }
            }
        } catch (Exception e) {
            log.error("设备快速检测上下线操作中抛出异常:{}", e);
            e.printStackTrace();
        }

    }
}
