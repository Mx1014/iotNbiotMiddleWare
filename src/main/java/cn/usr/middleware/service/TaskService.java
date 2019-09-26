package cn.usr.middleware.service;


import cn.usr.middleware.entity.Task;
import cn.usr.middleware.pojo.IotTaskQueueDTO;

/**
 * Created by liu on 2018-01-04.
 */
public interface TaskService {

    int addTask(Task task);


    int updateState(String devId, long dataId, long time);


    /**
     * 添加队列状态
     *
     * @param iotTaskQueueDTO
     * @return
     */
    Integer addTaskByIotTaskQueue(IotTaskQueueDTO iotTaskQueueDTO);

    /**
     * 修改队列状态
     *
     * @param devId
     * @param commandId
     * @param state
     * @return
     */
    Integer updateTaskState(String devId, String commandId, Integer state);


    Task getTaskByDeviceId(String devId);

    int updateStateByIdOrDevId(Task task);

}
