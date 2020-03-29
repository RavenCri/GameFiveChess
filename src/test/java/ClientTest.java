

import client.window.BeginWindow;
import client.window.LoginFream;
import client.window.Room;
import com.alibaba.fastjson.JSONObject;
import com.sun.glass.ui.Application;
import common.pojo.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import server.service.UserServer;
import util.GameRoomUtil;

import java.io.*;
import java.net.Socket;

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
    public void startClient() throws IOException, InterruptedException {
        BeginWindow.socket = new Socket("127.0.0.1",6666);

        BeginWindow.out = new BufferedWriter(new OutputStreamWriter(BeginWindow.socket.getOutputStream(),"UTF-8"));
        BeginWindow.in = new BufferedReader(new InputStreamReader(BeginWindow.socket.getInputStream(),"UTF-8"));
        BeginWindow.userPlayer = userServer.getUser("1");
        Room room = new Room(new BeginWindow());
        GameRoomUtil.SendMsgToServer(null, "LoginGame", JSONObject.toJSONString( BeginWindow.userPlayer));
        room.setVisible(true);
        System.in.read();
    }
}
