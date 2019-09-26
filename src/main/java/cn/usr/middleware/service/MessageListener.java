package cn.usr.middleware.service;


import org.springframework.amqp.core.Message;

/**
 * @Package: cn.usr.middleware.service
 * @Description: TODO
 * @author: Rock 【shizhiyuan@usr.cn】
 * @Date: 2018-05-10 15:25
 */
public interface MessageListener {
    /**
     * 来自透传云的数据监听
     *
     * @param message
     */
    void onMessageReceive(Message message);

    /**
     * 来自有人的数据监听
     *
     * @param deviceId
     * @param data
     */
    void onMessageReceiveOfUCloud(String deviceId, byte[] data) throws Exception;
}
