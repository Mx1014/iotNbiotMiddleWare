package cn.usr.middleware.pojo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IotTaskQueueDTO {
    //自增ID
    private Integer id;

    //设备ID
    private String did;

    //数据点id
    private Integer dataid;

    //消息ID
    private String commandid;

    //数据
    private String data;

    //当前状态
    private int state;

    //创建时间
    private Integer createTime;

    //操作时间
    private Integer completTime;

    public IotTaskQueueDTO(String did, String commandid, String data, int state) {
        this.did = did;
        this.commandid = commandid;
        this.data = data;
        this.state = state;
    }


}