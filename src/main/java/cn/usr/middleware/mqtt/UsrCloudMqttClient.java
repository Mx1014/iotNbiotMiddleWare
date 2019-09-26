package cn.usr.middleware.mqtt;


import org.eclipse.paho.client.mqttv3.MqttException;

import java.io.UnsupportedEncodingException;

/**
 * 有人 透传云 客户端 接口类
 *
 * @author shizhiyuan
 */
public interface UsrCloudMqttClient {


    /**
     * 回调接口
     *
     * @param CloudMqttCallback
     */
    void setUsrCloudMqttCallback(UsrCloudMqttCallback CloudMqttCallback);

    /**
     * 连接
     *
     * @return
     * @throws MqttException
     */
    void Connect() throws MqttException;


    /**
     * 断开连接
     *
     * @return
     * @throws MqttException
     */
    boolean DisConnectUnCheck() throws MqttException;

    /**
     * 订阅主题
     *
     * @param devId
     * @throws MqttException
     */
    void SubscribeForDevId(String devId) throws MqttException;


    /**
     * 取消订阅
     *
     * @param devId
     * @throws MqttException
     */
    void DisSubscribeForDevId(String devId) throws MqttException;

    /**
     * 发布数据
     *
     * @param devId
     * @param data
     * @throws MqttException
     */
    void PublishDataForDevId(String devId, byte[] data) throws MqttException;


    /**
     * 字节数组转UTF8的字符串
     *
     * @param data
     * @return
     * @throws UnsupportedEncodingException
     */
    String USRToolBytesToUtf8str(byte[] data) throws UnsupportedEncodingException;

    /**
     * 字符串转utf
     *
     * @param data
     * @return
     * @throws UnsupportedEncodingException
     */
    byte[] USRToolUtf8strToBytes(String data) throws UnsupportedEncodingException;


}
