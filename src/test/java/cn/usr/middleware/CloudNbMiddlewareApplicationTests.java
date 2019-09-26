package cn.usr.middleware;

import cn.usr.middleware.util.ProtoStuffSerializerUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CloudNbMiddlewareApplicationTests {

    @Test
    public void contextLoads() {
    }

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


//    @Autowired
//    private RabbitTemplate rabbitTemplate;

    @Test
    public void test() throws Exception {
//
////         保存字符串
//        String china_telecom_token = stringRedisTemplate.opsForValue().get("china_telecom_token");
//        Set<String> online_dev = stringRedisTemplate.opsForZSet().rangeByScore("online_dev", Double.valueOf("00004183000000000083"), Double.valueOf("00004183000000000083"), Long.valueOf("4183"), Long.valueOf("4183"));
//
//        stringRedisTemplate.opsForValue().set("zhiyuan", "sooooss", 300, TimeUnit.SECONDS);
//        Long remove = stringRedisTemplate.opsForZSet().remove(ONLINE_DEV, "863703030579820");
//


//        this.rabbitTemplate.convertAndSend(
//                "exchange_chinatelcom_coap_center",
//                "to_center_data",
//                ProtoStuffSerializerUtil.serialize(null));
    }

    @Test
    public void test1() throws Exception {

//         保存字符串

//        for (int i = 0; i < 100; i++) {
//
//            stringRedisTemplate.opsForHash().put("ssss", (i + 1)+"2", "xxxx");
//        }
//        Set<String> online = stringRedisTemplate.opsForZSet().rangeByScore("online", 7774, 7774);
//        System.out.println(online.size());


        Double online_dev = stringRedisTemplate.opsForZSet().score("online_dev", "868221040020292");
        System.out.println(online_dev);


    }


}
