package cn.usr.middleware.mqtt.impl;



import cn.usr.middleware.mqtt.UsrCloudMqttCallback;
import cn.usr.middleware.mqtt.UsrCloudMqttClient;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

import static org.eclipse.paho.client.mqttv3.MqttConnectOptions.*;
import static org.eclipse.paho.client.mqttv3.MqttException.REASON_CODE_CLIENT_NOT_CONNECTED;

/**
 * Usr cloud client Implements Class
 *
 * @author shizhiyuan
 */
@Service
@Slf4j
public class UsrCloudMqttClientImpl implements MqttCallbackExtended, UsrCloudMqttClient {


    public static final String DEVID = "<Id>";
    public static final String TOPIC_T_DOWN = "/t/<Id>/0/d";
    public static final String TOPIC_T_UP = "/t/<Id>/0/u";
    private final int Qos = 0;

    @Autowired
    private UsrCloudMqttCallback usrCloudMqttCallback;

    @Value("${mqtt.server_uri}")
    private String serverURI;
    @Value("${mqtt.user_name}")
    private String userName;
    @Value("${mqtt.password}")
    private String passWord;


    private volatile MqttClient mqttClient;

    public static final String CLIENTID_PREFIX = "Server:";

    public static final int SUCCESS = 0;//成功
    public static final int FAILE = 1;//失败
    public static final int CONNECT_COMPLETE = 2;//与服务器完成连接
    public static final int CONNECT_BREAK = 3;//与服务器断开

    @Override
    public void setUsrCloudMqttCallback(UsrCloudMqttCallback usrCloudMqttCallback) {
        this.usrCloudMqttCallback = usrCloudMqttCallback;
    }

    @Override
    public void Connect() throws MqttException {
        Connect1(userName, passWord);
    }

    private void Connect1(String userName, String passWord) throws MqttException {
        final String clientId = (CLIENTID_PREFIX + userName).trim();
        mqttClient = new MqttClient(serverURI, clientId, new MemoryPersistence());
        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(true);
        options.setUserName(userName);
        options.setPassword(passWord.toCharArray());
        options.setConnectionTimeout(CONNECTION_TIMEOUT_DEFAULT);
        options.setKeepAliveInterval(KEEP_ALIVE_INTERVAL_DEFAULT);
        options.setMaxInflight(10000);
        options.setAutomaticReconnect(true);
        options.setMqttVersion(MQTT_VERSION_3_1_1);
        mqttClient.setCallback(this);
        log.info("[MQTT 客户端连接] 用户名为：{}，密码为：{}，ClientId为：{}，属性信息为：MqttConnectOptions:{}", userName, passWord, clientId, options);
        mqttClient.connect(options);
    }


    @Override
    public boolean DisConnectUnCheck() throws MqttException {
        if (mqttClient == null && !mqttClient.isConnected()) {
            return false;
        }
        mqttClient.disconnect();
        return true;
    }


    /**
     * 订阅方法
     *
     * @param devId
     * @throws MqttException
     */
    public void SubscribeForDevId(String devId) throws MqttException {
        final String topic = TOPIC_T_DOWN.replaceAll(DEVID, devId);
        log.info("[MQTT 订阅] 主题为：{},QOS为：{}", topic, Qos);
        mqttClient.subscribe(topic, Qos);
    }


    @Override
    public void DisSubscribeForDevId(String devId) throws MqttException {
        if (mqttClient == null && !mqttClient.isConnected()) {
            throw new MqttException(REASON_CODE_CLIENT_NOT_CONNECTED);
        }
        final String topic = TOPIC_T_DOWN.replaceAll(DEVID, devId);
        UnSubscribe(topic);
    }

    /**
     * 私用取消订阅方法
     *
     * @param topic
     * @throws MqttException
     */
    private void UnSubscribe(String topic) throws MqttException {
        log.info("[MQTT 取消订阅]主题为：{}", topic);
        mqttClient.unsubscribe(topic);
    }


    @Override
    public String USRToolBytesToUtf8str(byte[] data) throws UnsupportedEncodingException {
        return new String(data, "utf-8");
    }

    @Override
    public byte[] USRToolUtf8strToBytes(String data) throws UnsupportedEncodingException {
        return data.getBytes("utf-8");
    }


    /**
     * 私有发布方法
     *
     * @param devId
     * @param data
     * @throws MqttException
     */
    public void PublishDataForDevId(String devId, byte[] data) throws MqttException {
        MqttMessage mqttMessage = new MqttMessage();
        mqttMessage.setQos(0);
        mqttMessage.setRetained(false);
        mqttMessage.setPayload(data);
        final String topic = TOPIC_T_UP.replaceAll(DEVID, devId);
        log.info("[MQTT 发布] 主题为：{} ，QOS为：{}", topic, Qos);
        mqttClient.publish(topic, mqttMessage);
    }


    /**
     * 当与服务器的连接丢失时调用此方法
     *
     * @param cause
     */
    @Override
    public void connectionLost(Throwable cause) {
        if (usrCloudMqttCallback != null) {
            usrCloudMqttCallback.onConnectAck(CONNECT_BREAK, cause.toString());
        }
    }

    /**
     * 在邮件的传送完成后调用，并且已收到所有确认
     *
     * @param token
     */
    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
    }

    /**
     * 当收到消息时，将调用此方法
     *
     * @param topic
     * @param message
     * @throws Exception
     */
    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        if (usrCloudMqttCallback != null) {
            usrCloudMqttCallback.onReceiveEvent(message.getId(), topic, message.getPayload());
        }
    }


    /**
     * 与服务器成功建立连接
     *
     * @param b
     * @param s
     */
    @Override
    public void connectComplete(boolean b, String s) {
        if (usrCloudMqttCallback != null) {
            usrCloudMqttCallback.onConnectAck(CONNECT_COMPLETE, "与服务器完成连接");
        }
    }
}
