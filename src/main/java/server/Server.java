package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import com.raven.main.ConnectTomsg;
import com.raven.main.GameRoom;
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
	public static GameRoom gameRoom = new GameRoom();
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
				pool.execute(new ConnectTomsg(socket));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
}
