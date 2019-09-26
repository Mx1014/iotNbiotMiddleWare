package cn.usr.middleware.mqtt.impl;


import cn.usr.middleware.mqtt.UsrCloudMqttCallback;
import cn.usr.middleware.service.impl.MessageListenerImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * @author Administrator
 */
@Service
@Slf4j
public class UsrCloudMqttCallbackImpl implements UsrCloudMqttCallback {


    @Autowired
    private MessageListenerImpl messageListener;


    public UsrCloudMqttCallbackImpl() {

    }

    @Override
    public void onConnectAck(int returnCode, String description) {
        log.info("[MQTT Client 与服务器连接交互结果]标示码：{},描述：{}", returnCode, description);
    }

    @Override
    public void onSubscribeAck(int messageId, String clientId, String topics, int returnCode) {

    }


    @Override
    public void onDisSubscribeAck(int messageId, String clientId, String topics, int returnCode) {

    }


    @Override
    public void onReceiveEvent(int messageId, String topic, byte[] data) {
        log.info("[收到Mqtt过来的信息，准备给设备下发]，主题为：{}", topic);

        String deviceId;
        try {
            // 截取topic里面的设备ID 主题样式为：/t/{devId}/{appTag}/d
            deviceId = StringUtils.split(topic, "/")[1];
        } catch (Exception e) {
            log.error("[截取设备ID异常，topic不规范或其他原因] topic为：{}", topic);
            return;
        }

        // 发送给设备，下面会有关于字节的处理
        messageListener.onMessageReceiveOfUCloud(deviceId,data);

    }


    @Override
    public void onPublishDataAck(int messageId, String topic, boolean isSuccess) {
        log.info("[onPublishDataAck]");
    }

    @Override
    public void onPublishDataResult(int messageId, String topic) {

    }


}
