package util;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.apache.tomcat.util.security.MD5Encoder;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServer;
import org.springframework.util.DigestUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import bean.User;
import client.start.GameClient;
import client.ui.GamePlane;
import client.ui.MouseAdapterOfRoomPlane;
import client.ui.RoomPlane;
import client.window.BeginWindow;
import client.window.ChessBoard;
import client.window.Room;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
public class GameRoomUtil {
	
	static Player p;
	static public Thread bGThread;
	
	public static JSONObject getSendJSON(String msgType, String msg) {
		JSONObject sengJSON = new JSONObject();
		
		String securityCheck = DigestUtils.md5DigestAsHex((msgType+msg).getBytes());
		sengJSON.put("securityCheck", securityCheck);
		sengJSON.put("msgType", msgType);
		
		if(JSONObject.isValidObject(msg)) {
			sengJSON.put("msg",JSONObject.parse(msg));
		}else {
			sengJSON.put("msg",msg);
		}
		
		return sengJSON;
	}
	public static void SendMsgToServer(JFrame jFrame,String msgtype,String msg){
		
		try {
			JSONObject send = GameRoomUtil.getSendJSON(msgtype,msg);
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
				InputStream in = getClass().getClassLoader().getResourceAsStream("sound/"+filename);
				
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
	public   void  playBgmusic() {
		
			bGThread= 	new Thread() {
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
	public static void stopmusic(){
		if(bGThread!=null)
		{
			
			bGThread.stop();
			
			bGThread = null;
		}	
		
	}
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
