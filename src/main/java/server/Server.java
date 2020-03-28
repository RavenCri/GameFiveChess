package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import server.handlemsg.HandleMsg;
import server.service.UserServer;
/**
 * 
 * @author Raven
 * @date 下午5:58:53
 * @version
 * 	游戏服务端
 */
@SpringBootApplication
public class Server implements Runnable{
	static ServerSocket serverSocket;
	static ExecutorService pool= Executors.newFixedThreadPool(50);
	@Autowired
	public static UserServer userServer;
	public static void main(String[] args) {
	
			SpringApplication.run(Server.class, args);
			new Thread(new Server()).start();
			
		
	}

	@Override
	public void run() {
		
			

		try {
			serverSocket = new ServerSocket(6666);
			System.out.println("服务已启动");
			//new Thread(new Server()).start();
			while(true) {
				Socket socket = serverSocket.accept();
				pool.execute(new HandleMsg(socket));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	public  UserServer getUserServer() {
		return userServer;
	}
	@Autowired
	public  void setUserServer(UserServer userServer) {
		Server.userServer = userServer;
	}
	
	
}
