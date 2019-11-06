package client.window;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.springframework.util.DigestUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import bean.User;
import client.start.GameClient;
import client.tidings.HandleMsgThread;
import client.tidings.ReceiveMsgThread;
import client.ui.GamePlane;
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
	public static ImageIcon man;
	public static ImageIcon women;
	public static ImageIcon chessWhite;
	public static ImageIcon chessBlack;
	
	public static RoomPlane roomPlane = new RoomPlane();
	public BeginWindow priwid;
	public static ChessBoard chessBoard;
	public static LinkedBlockingQueue<JSONObject> msgQueue = new LinkedBlockingQueue<>();
	public static volatile JSONArray roomList;
	public static volatile boolean emptyRoom = true;
	public JSplitPane chatPlane;
	public JScrollPane jscroll;
	public static JTextArea jt  = new JTextArea();
	public SimpleDateFormat dateFormat= new SimpleDateFormat("hh:mm:ss");
	public Room(BeginWindow priwid){
		setLayout(null);
		//大小不变
		setResizable(false);
		setTitle("在线游戏列表");
		man = new ImageIcon(getClass().getClassLoader().getResource("img/man.jpg"));
		women = new ImageIcon(getClass().getClassLoader().getResource("img/women.jpg"));
		chessWhite = new ImageIcon(getClass().getClassLoader().getResource("img/chessWhite.png"));
		chessBlack = new ImageIcon(getClass().getClassLoader().getResource("img/chessBlack.png"));
		man.setImage(man.getImage().getScaledInstance(40, 40,
				Image.SCALE_DEFAULT));
		women.setImage(women.getImage().getScaledInstance(40, 40,
				Image.SCALE_DEFAULT));
		chessWhite.setImage(chessWhite.getImage().getScaledInstance(40, 40,
				Image.SCALE_DEFAULT));
		chessBlack.setImage(chessBlack.getImage().getScaledInstance(40, 40,
				Image.SCALE_DEFAULT));
		
		
		this.priwid = priwid;
		
		setSize(1300, 800);
		GameRoomUtil.CenterWindow(this);

		roomPlane.room = this;
		roomPlane.setSize(800,800);
		roomPlane.setLocation(0, 0);
		//开启消息接收线程。
		new ReceiveMsgThread().start();
		ReceiveMsgThread.readFlag = true;
		new HandleMsgThread().start();
		
				
		add(roomPlane);
		chatPlane = new JSplitPane();
		chatPlane.setLayout(null);
		chatPlane.setSize(450, 800);
		chatPlane.setLocation(800, 0);
		
		jscroll = new JScrollPane(jt);
		jscroll.setViewportView(jt);
		jscroll.setLocation(0,0);
		jscroll.setSize(450,580);
		jt.setLineWrap(true);        //激活自动换行功能 
		jt.setWrapStyleWord(true); 
		jt.setEditable(false);
		jt.setLocation(0,0);
		jt.setSize(450,800);
		jt.setBackground(new Color((int)0xE6E6FA));
		JTextField sendtext = new JTextField();
		sendtext.setLocation(10,620);
		sendtext.setSize(250,40);
		
		sendtext.setBackground(new Color((int)0xE6E6FA));
		jt.setFont(new Font("楷体", Font.PLAIN, 25));
		sendtext.setFont(new Font("楷体", Font.PLAIN, 20));
		JLabel bq = new JLabel("发送消息:");
		bq.setForeground(Color.pink);
		bq.setSize(200,30);
		bq.setLocation(10,580);
		chatPlane.add(bq);
		JButton send = new JButton("Send");
		send.setLocation(300,610);
		send.setSize(60,60);
		send.setForeground(Color.pink);
		chatPlane.add(jscroll);
		//chatPlane.add(jt);
		chatPlane.add(send);
		chatPlane.add(sendtext);
		
		add(chatPlane);
		jt.setForeground(new Color(	0,250,154));
		
		jt.append("系统："+"\n"+"   欢迎加入游戏厅，希望来到这里能给你带来快乐，与室友一起组队开黑吧~~\n");	
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
					GameRoomUtil.SendMsgToServer(priwid, "CloseLingoutGameRoom", null);
					priwid.setVisible(true);
					ReceiveMsgThread.readFlag = false;
					dispose();
			}
		});
		setVisible(true);	
		send.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(sendtext.getText().equals("")) {
					JOptionPane.showMessageDialog(null, "不要发送空消息~~");
					return;
				}
				
			
				
				jt.append(BeginWindow.userPlayer.getNickName()+"："+dateFormat.format(new Date())+"\n"+"   "+sendtext.getText()+"\n");
				JSONObject msg = new JSONObject();
				msg.put("from", BeginWindow.userPlayer.getNickName());
				msg.put("msg", sendtext.getText());
				GameRoomUtil.SendMsgToServer(GamePlane.chessBoard, "salaChat",msg.toJSONString());
				sendtext.setText("");
				//最下方
				jt.setCaretPosition(jt.getDocument().getLength());
				//获取焦点
				sendtext.grabFocus();
		
				
			}
		});
		sendtext.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				
				 if(e.getKeyChar() == KeyEvent.VK_ENTER )  
	                {  
						send.doClick();
	                    
	                }  
			}
		});
	}


};

