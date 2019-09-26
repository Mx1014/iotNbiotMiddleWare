package cn.usr.middleware.entity;

import lombok.Data;

/**
 * Created by liu on 2018-01-04.
 */
@Data
public class Task {
    public static final int STATE_WATIT_SEND = 1;//待发送
    public static final int STATE_ARRIVED = 2;//已送达
    public static final int STATE_OPTION_SUCC = 3;//执行成功
    public static final int STATE_TIME_OUT = 4;//超时

    private long id;
    private String devId;
    private long dataId;
    private String data;
    private int state;
    private long createTime;
    private long updateTime;
    private long completeTime;

    public static Task createTask(String devId,long dataId,String data,long createTime){
        Task task = new Task();
        task.setDevId(devId);
        task.setDataId(dataId);
        task.setData(data);
        task.setCreateTime(createTime);
        task.setUpdateTime(createTime);
        task.setState(STATE_WATIT_SEND);
        return task;
    }
}
