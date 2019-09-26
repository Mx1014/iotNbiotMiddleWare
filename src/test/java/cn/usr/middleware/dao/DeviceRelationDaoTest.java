package cn.usr.middleware.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DeviceRelationDaoTest {

    @Autowired
    DeviceRelationDao deviceRelationDao;

    @Test
    public void getDeviceRelationDao() {
        deviceRelationDao.selectDevRelationByDeviceId("b0dd453f-e3c7-4a50-94d2-8d1943f6dd41");
    }
}