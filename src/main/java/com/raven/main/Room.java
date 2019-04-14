package com.raven.main;

import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import util.GameRoomUtil;
/***
 * 
 * @author Raven
 * @date 下午5:57:58
 * @version
 * 	在线游戏列表
 */
public class Room extends JFrame {
	
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static ImageIcon man = new ImageIcon("source/man.jpg");
	static ImageIcon women = new ImageIcon("source/women.jpg");
	public static ImageIcon chessWhite = new ImageIcon("source/chessWhite.png");
	public static ImageIcon chessBlack = new ImageIcon("source/chessBlack.png");
	
	
	public static RoomPlane roomPlane = new RoomPlane();
	public BeginWindow priwid;

	
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
		
		GameRoomUtil.SendToServerMsg(this,"MSGTYPE:GetOnlineGame\r\n");
		GameRoomUtil.ResultMsg();
		roomPlane.room = this;
		roomPlane.setSize(800,800);
		roomPlane.setLocation(0, 0);
		GetServerMSG();
		add(roomPlane);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
			
				try {
					if(BeginWindow.out!=null) {
						BeginWindow.out.close();
						BeginWindow.socket.close();
					}
					
					dispose();
					priwid.setVisible(true);
					
				} catch (IOException e1) {
					
					e1.printStackTrace();
				}
			}
		});
		
		setVisible(true);
			
	}
	
	
	public void GetServerMSG(){
		new Thread() {
			public void run() {
				while (true) {
					int status = GameRoomUtil.ResultMsg();
					if(status==-1) {
						break;
					}
				}
				System.out.println("您退出了房间");
			};
		}.start();
	}
	
};

