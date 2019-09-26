package cn.usr.middleware.config;

import cn.usr.middleware.service.impl.MessageListenerImpl;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.*;

/**
 * @Description: rabbitmq 配置文件类
 * @author: Rock 【shizhiyuan@usr.cn】
 * @Date: 2018-05-09 09:27
 */
//@Configuration
@Slf4j
public class AmqpConfig {

    @Value("${mq.address}")
    private String address;

    @Value("${mq.port}")
    private Integer port;

    @Value("${mq.userName}")
    private String userName;

    @Value("${mq.password}")
    private String password;

    @Value("${mq.exchange.chinatelcom}")
    private String exchangeChinatelcom;

    @Value("${mq.exchange.exchange_cache}")
    private String exchangeCache;

    @Value("${mq.queue.coap_cneter}")
    private String chinatelcomCoapToCenter;

    @Value("${mq.queue.center_coap}")
    private String centerToChinatelcomCoap;

    @Value("${mq.queue.nb_cache}")
    private String nb_cache;


    @Value("${mq.routingkey.to_center_data}")
    private String toCenterData;

    @Value("${mq.routingkey.to_device_data}")
    private String toDeviceData;


    @Autowired
    private MessageListenerImpl messageListener;


    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setAddresses(address);
        connectionFactory.setPort(port);
        connectionFactory.setUsername(userName);
        connectionFactory.setPassword(password);
        // 设置虚拟主机
        connectionFactory.setVirtualHost("/");
        // 设置发布确认
        connectionFactory.setPublisherConfirms(false);
        return connectionFactory;
    }


    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public RabbitTemplate rabbitTemplate() {
        RabbitTemplate template = new RabbitTemplate(connectionFactory());
        return template;
    }

    @Bean
    public FanoutExchange Exchange() {
        return new FanoutExchange(exchangeCache, false, false);
    }

    @Bean
    public DirectExchange defaultExchange() {
        return new DirectExchange(exchangeChinatelcom, false, false);
    }

    @Bean
    public Queue queue1() {
        return new Queue(centerToChinatelcomCoap, false, false, false, null);
    }

    @Bean
    public Queue queue3() {
        return new Queue(nb_cache, false, false, false, null);
    }

    @Bean
    public Binding binding2() {
        return BindingBuilder.bind(queue1()).to(defaultExchange()).with(toDeviceData);
    }


    @Bean
    public Binding bindingQueue() {
        return BindingBuilder.bind(queue3()).to(Exchange());
    }

    @Bean
    public SimpleMessageListenerContainer messageContainer() {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory());
        container.setQueues(queue1(), queue3());
        container.setExposeListenerChannel(true);
        container.setConsumerStartTimeout(5000);
        container.setMaxConcurrentConsumers(4);
        container.setConcurrentConsumers(4);
        container.setAcknowledgeMode(AcknowledgeMode.NONE);
        container.setMessageListener(new ChannelAwareMessageListener() {
            @Override
            public void onMessage(Message message, Channel channel) throws Exception {
                try {
                    messageListener.onMessageReceive(message);
                } catch (Exception e) {
                    log.error("接受来自有人云rabbitmq 异常，信息为：{}", e);
                }
            }
        });
        return container;
    }


}


