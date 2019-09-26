package cn.usr.middleware.dao;

import cn.usr.middleware.entity.Task;
import cn.usr.middleware.pojo.IotTaskQueueDTO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @author liu
 * @date 2018-01-04
 */
@Repository
public interface TaskDao {

    int deleteByDevIdAndDataId(@Param("devId") String devId, @Param("dataId") long dataId);

    int addTaskByIotTaskQueue(@Param("task") Task task);


    int updateState(@Param("devId") String devId, @Param("dataId") long dataId, @Param("time") long time);


    /**
     * 添加一条队列
     *
     * @param iotTaskQueueDTO
     * @return
     */
    int addTaskByIotTaskQueue(@Param("iotTaskQueue") IotTaskQueueDTO iotTaskQueueDTO);

    /**
     * 更改状态
     *
     * @param devId
     * @param commandId
     * @param state
     * @return
     */
    Integer updateTaskState(@Param("devId") String devId, @Param("commandId") String commandId, @Param("state") Integer state);

    Task getTaskByDeviceId(@Param("devId") String devId);

    int updateStateByIdOrDevId(Task task);

}
