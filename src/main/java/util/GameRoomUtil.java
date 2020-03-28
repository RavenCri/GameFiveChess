package util;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Toolkit;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.springframework.util.DigestUtils;

import com.alibaba.fastjson.JSONObject;

import client.GameClient;
import client.plane.GamePlane;
import client.window.BeginWindow;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
/**
* @Description:
* @Author: raven
* @Date: 2020/3/28
*/
public class GameRoomUtil {

	static Player p;
	static public Thread bGThread;

	/*** 
	* @Description:  包装要发送的消息对象
	* @Param: [msgType, msg]
	* 			消息类型，消息体
	* @return: com.alibaba.fastjson.JSONObject 
	* @Author: raven
	* @Date: 2020/3/28
	*/
	public static JSONObject getSendJSON(String msgType, String msg) {
	
		JSONObject sengJSON = new JSONObject();
		// 生成加密字符串 防止伪造信息
		String securityCheck = DigestUtils.md5DigestAsHex((msgType+msg).getBytes());
		sengJSON.put("securityCheck", securityCheck);
		sengJSON.put("msgType", msgType);
		// 如果消息是json字符，那么应该先包装成json对象 防止发送的时候把引号转义
		if(JSONObject.isValidObject(msg)) {
			sengJSON.put("msg",JSONObject.parse(msg));
		}else {
			sengJSON.put("msg",msg);
		}
		return sengJSON;
	}
	/***
	* @Description: 用来发送消息给后台服务器
	* @Param: [jFrame, msgtype, msg]
	* @return: void
	* @Author: raven
	* @Date: 2020/3/28
	*/
	public static void SendMsgToServer(JFrame jFrame,String msgtype,String msg){

		try {
			//包装要发送的消息
			JSONObject send = GameRoomUtil.getSendJSON(msgtype,msg);
			//发送消息
			BeginWindow.out.write(send+"\r\n");
			BeginWindow.out.flush();
			System.out.println("发送："+send.toJSONString());
		} catch (IOException e) {
			JOptionPane.showMessageDialog(jFrame, "您可能已经与服务器断开了连接。。");
			jFrame.setVisible(false);
			jFrame.dispose();
			GameClient.beginWindow.setVisible(true);
		}

	}

	/***
	* @Description: 用于居中窗口
	* @Param: [jFrame]
	* @return: void
	* @Author: raven
	* @Date: 2020/3/28
	*/
	public static void CenterWindow(JFrame jFrame){
		int ScreenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
		int ScreenHeight =Toolkit.getDefaultToolkit().getScreenSize().height;
		jFrame.setLocation((ScreenWidth-jFrame.getWidth())/2, (ScreenHeight-jFrame.getHeight())/2);
	}
	//i,j为左上角顶点,k,l为宽高
	//    -----------
	//	  |         |
	//    -----------
	public static void writeString(Point p ,Boolean mousedown,String string,Graphics g2, int i, int j, int k, int l) {


		if(p.getX()>=i-30&&p.getX()<=i-30+k&&p.getY()>=j-40&&p.getY()<=j-40+l) {
			if(mousedown) {
				g2.setColor(Color.RED);
			}else {
				g2.setColor(Color.cyan);
			}


			//画矩形  左上角的坐标 相对于字体来说 横坐标-30  纵坐标-40
			g2.drawRect(i-30,j-40,k,l);

			g2.drawString(string,i,j);

		}
		g2.setColor(Color.black);
	}
	public  void palyothermusic(String filename){

		new Thread(()->{
			try {
				InputStream in = getClass().getClassLoader().getResourceAsStream(filename);
				Player p = new Player(in);
				p.play();
			} catch (JavaLayerException e1) {
				e1.printStackTrace();
			}
		}).start();
	}
	public   void  playChessMovemusic( ) {
		new Thread(()->{
			BeginWindow.bofang = true;
			try {
				InputStream in = getClass().getClassLoader().getResourceAsStream("sound/move.mp3");
				Player play = new Player(in);
				play.play();
				BeginWindow.bofang = false;
			} catch (JavaLayerException e1) {
				e1.printStackTrace();
			}
		}).start();
	}
	public void playBackgroundMusic() {
		bGThread = new Thread() {
			@Override
			public void run() {
				while(true) {
					try {
						InputStream in = getClass().getClassLoader().getResourceAsStream("sound/bgmusic.mp3");
						p = new Player(in);
						p.play();
					} catch (JavaLayerException e1) {

						e1.printStackTrace();
					}
				}
			}
		};
		bGThread.start();
	}
	/***
	* @Description: 停止背景音乐
	* @Param: []
	* @return: void
	* @Author: raven
	* @Date: 2020/3/28
	*/
	public static void stopBackgroundMusic(){
		if(bGThread!=null)
		{
			bGThread.stop();
			bGThread = null;
		}
	}
	/*** 
	* @Description: 改变自己的棋子颜色
	* @Param: [] 
	* @return: void 
	* @Author: raven
	* @Date: 2020/3/28 
	*/
	public static void ChangeMyChessColor() {
		if(GamePlane.MyChessColor.equals("black")) {
			GamePlane.setMyChessColor("white");
			GamePlane.setMyChessColorINT(1);
		}else {
			GamePlane.setMyChessColor("black");
			GamePlane.setMyChessColorINT(2);
		}
	}
}
