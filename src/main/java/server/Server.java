package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.context.ConfigurableApplicationContext;

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
	final static ThreadFactory threadFactory = new ThreadFactoryBuilder()
			.setNameFormat("ClientThread-%d")
			.setDaemon(true)
			.build();
	static ExecutorService pool= Executors.newFixedThreadPool(50,threadFactory);
	private static DefaultListableBeanFactory beanFactory;
	@Autowired
	public static UserServer userServer;
	public static void main(String[] args) {

		ConfigurableApplicationContext run = SpringApplication.run(Server.class, args);
		new Thread(new Server()).start();
		beanFactory = (DefaultListableBeanFactory)run.getBeanFactory();

	}

	@Override
	public void run() {
		try {
			serverSocket = new ServerSocket(6666);
			System.out.println("服务已启动");
			//new Thread(new Server()).start();
			int i = 0;
			while(true) {
				Socket socket = serverSocket.accept();

				BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.rootBeanDefinition(HandleMsg.class);
				beanDefinitionBuilder.addPropertyValue("socket",socket);
				/**
				 * 注册到spring容器中
				 */
				beanFactory.registerBeanDefinition(HandleMsg.class.getName()+ ++i,beanDefinitionBuilder.getBeanDefinition());
				pool.execute(beanFactory.getBean(HandleMsg.class.getName()+ i,HandleMsg.class));
				for (String beanDefinitionName : beanFactory.getBeanDefinitionNames()) {
					System.out.println(beanDefinitionName);
				}
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
