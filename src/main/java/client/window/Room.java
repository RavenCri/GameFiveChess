package client.window;

import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.springframework.util.DigestUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import bean.User;
import client.tidings.HandleMsgThread;
import client.tidings.ReceiveMsgThread;
import client.ui.RoomPlane;
import pojo.GameRoom;
import util.GameRoomUtil;
/***
 * 
 * @author Raven
 * @date 下午5:57:58
 * @version
 * 	在线游戏列表
 */
public class Room extends JFrame {
	
	private static final long serialVersionUID = 1L;
	public static ImageIcon man = new ImageIcon("source/man.jpg");
	public static ImageIcon women = new ImageIcon("source/women.jpg");
	public static ImageIcon chessWhite = new ImageIcon("source/chessWhite.png");
	public static ImageIcon chessBlack = new ImageIcon("source/chessBlack.png");
	
	public static RoomPlane roomPlane = new RoomPlane();
	public BeginWindow priwid;
	
	public static LinkedBlockingQueue<JSONObject> msgQueue = new LinkedBlockingQueue<>();
	public static volatile JSONArray roomList;
	public static volatile boolean emptyRoom = true;
	public Room(BeginWindow priwid){
		setLayout(null);
		//大小不变
		setResizable(false);
		setTitle("在线游戏列表");
		man.setImage(man.getImage().getScaledInstance(40, 40,
				Image.SCALE_DEFAULT));
		women.setImage(women.getImage().getScaledInstance(40, 40,
				Image.SCALE_DEFAULT));
		chessWhite.setImage(chessWhite.getImage().getScaledInstance(40, 40,
				Image.SCALE_DEFAULT));
		chessBlack.setImage(chessBlack.getImage().getScaledInstance(40, 40,
				Image.SCALE_DEFAULT));
		
		
		this.priwid = priwid;
		
		setSize(800, 800);
		GameRoomUtil.CenterWindow(this);

		roomPlane.room = this;
		roomPlane.setSize(800,800);
		roomPlane.setLocation(0, 0);
		//开启消息接收线程。
		new ReceiveMsgThread().start();
		new HandleMsgThread().start();
		
				
		add(roomPlane);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
					GameRoomUtil.SendMsgToServer(priwid, "CloseLingoutGameRoom", null);
					priwid.setVisible(true);
					dispose();
			}
		});
		setVisible(true);		
	}

	
};

