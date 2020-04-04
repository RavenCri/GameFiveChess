

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import server.service.UserServer;


/**
 * @description: 用于测试客户端
 * @author: raven
 * @create: 2020-03-29 23:56
 **/
@SpringBootTest(classes = ClientTest.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ComponentScan("server")
public class ClientTest  {
    @Autowired
    public UserServer userServer;
    @Test
    public void startClient()  {

    }
}
