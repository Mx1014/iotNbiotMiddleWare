package cn.usr.middleware.service;

import cn.usr.middleware.pojo.PlatformDeviceRelation;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class NBModuleServiceTest {

    @Autowired
    NBModuleService nbModuleService;



    @Test
    public void selectDevRelationByIMEI() {
        PlatformDeviceRelation platformDeviceRelation = nbModuleService.selectDevRelationByDeviceId("b0dd453f-e3c7-4a50-94d2-8d1943f6dd41");
        System.out.println(platformDeviceRelation);
    }

}