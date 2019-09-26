package cn.usr.middleware.service.impl;


import cn.usr.middleware.dao.TaskDao;
import cn.usr.middleware.entity.Task;
import cn.usr.middleware.pojo.IotTaskQueueDTO;
import cn.usr.middleware.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by liu on 2018-01-04.
 */
@Service
public class TaskServiceImpl implements TaskService {
    @Autowired
    private TaskDao taskDao;

    @Override
    public int addTask(Task task) {
        //先删除后插入
        taskDao.deleteByDevIdAndDataId(task.getDevId(), task.getDataId());
        return taskDao.addTaskByIotTaskQueue(task);
    }

    @Override
    public int updateState(String devId, long dataId, long time) {
        return taskDao.updateState(devId, dataId, time);
    }

    @Override
    public Integer addTaskByIotTaskQueue(IotTaskQueueDTO iotTaskQueueDTO) {
        return taskDao.addTaskByIotTaskQueue(iotTaskQueueDTO);
    }

    @Override
    public Integer updateTaskState(String devId, String commandId, Integer state) {
        return taskDao.updateTaskState(devId, commandId, state);
    }

    @Override
    public Task getTaskByDeviceId(String devId) {
        return taskDao.getTaskByDeviceId(devId);
    }

    @Override
    public int updateStateByIdOrDevId(Task task) {
        return taskDao.updateStateByIdOrDevId(task);
    }
}
