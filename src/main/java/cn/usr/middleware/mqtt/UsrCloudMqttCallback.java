package cn.usr.middleware.mqtt;

/**
 * Usr cloud client CallBack interfaces
 *
 * @author shizhiyuan
 */
public interface UsrCloudMqttCallback {

    /**
     * 连接 回调函数
     *
     * @param returnCode
     */
    void onConnectAck(int returnCode, String description);

    /**
     * 订阅 回调函数
     *
     * @param messageId
     * @param clientId
     * @param topics
     * @param returnCode
     */
    void onSubscribeAck(int messageId, String clientId, String topics, int returnCode);


    /**
     * 取消订阅 回调函数
     *
     * @param messageId
     * @param clientId
     * @param topics
     * @param returnCode
     */
    void onDisSubscribeAck(int messageId, String clientId, String topics, int returnCode);


    /**
     * 接受到数据
     *
     * @param messageId
     * @param topic
     * @param data
     */
    void onReceiveEvent(int messageId, String topic, byte[] data);


    /**
     * 推送消息 回调函数
     *
     * @param messageId
     */
    void onPublishDataAck(int messageId, String topic, boolean isSuccess);


    /**
     * 本次推送消息结果回调
     *
     * @param messageId
     */
    void onPublishDataResult(int messageId, String topic);

}
