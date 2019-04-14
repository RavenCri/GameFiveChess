package com.raven.main;



import java.awt.Color;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import client.GameClient;
import util.GameRoomUtil;


public class ChessBoard extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	int ChessBoardWidth = 1800;
	int ChessBoardHeight  =  1000;
	
	public String RoomType;
	public Room parFrame;
	public static GamePlane gamepanel =new GamePlane();
	JSplitPane chatPlane;
	public static JTextArea jt  = new JTextArea();
	JScrollPane jscroll ;
	
	public ChessBoard() {
		
	}
	public ChessBoard(Room parFrame,String RoomType,String username) {
				setLayout(null);
				
				//设置标题
				this.setTitle("五子棋对战棋盘");
				//设置窗体大小
				this.setSize(ChessBoardWidth, ChessBoardHeight);
				//设置窗体出现位置
				GameRoomUtil.CenterWindow(this);
				this.setVisible(true);
				//窗口关闭事件
				addWindowListener(new WindowEvent(this));
							
				this.parFrame = parFrame;
				this.RoomType = RoomType;
				//播放背景音乐
				GameRoomUtil.playBgmusic();
				
				
				setResizable(false);
				
				
				gamepanel.setChessBoard(this);
				gamepanel.setGameplayer1(username);
				gamepanel.setMyPlayGamenum(BeginWindow.gamebout);
				GamePlane.setMYWINLV(BeginWindow.winlv);
				gamepanel.setMyplayGamewinnum((int)(BeginWindow.gamebout*BeginWindow.winlv));
				gamepanel.setLocation(0, 0);
				gamepanel.setSize(ChessBoardWidth-500, ChessBoardHeight);
				
				chatPlane = new JSplitPane();
				chatPlane.setLayout(null);
				chatPlane.setSize(450, ChessBoardHeight);
				chatPlane.setLocation(1300, 0);
				
				jscroll = new JScrollPane(jt);
				jscroll.setViewportView(jt);
				jscroll.setLocation(0,0);
				jscroll.setSize(450,800);
				jt.setLineWrap(true);        //激活自动换行功能 
				jt.setWrapStyleWord(true); 
				jt.setEditable(false);
				jt.setLocation(0,0);
				jt.setSize(450,800);
				jt.setBackground(new Color((int)0xE6E6FA));
				JTextField sendtext = new JTextField();
				sendtext.setLocation(10,850);
				sendtext.setSize(350,40);
				
				sendtext.setBackground(new Color((int)0xE6E6FA));
				jt.setFont(new Font("楷体", Font.PLAIN, 25));
				sendtext.setFont(new Font("楷体", Font.PLAIN, 20));
				JLabel bq = new JLabel("发送消息:");
				bq.setForeground(Color.pink);
				bq.setSize(200,30);
				bq.setLocation(10,810);
				chatPlane.add(bq);
				JButton send = new JButton("Send");
				send.setLocation(370,840);
				send.setSize(60,60);
				send.setForeground(Color.pink);
				chatPlane.add(jscroll);
				//chatPlane.add(jt);
				chatPlane.add(send);
				chatPlane.add(sendtext);
				jt.setForeground(new Color(180, 143, 160));
				jt.append("系统："+gamepanel.dateFormat.format(new Date())+"\n"+"   欢迎加入游戏厅，希望来到这里能给你带来快乐，与室友一起组队开黑吧~~\n");	
				
				send.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						if(sendtext.getText().equals("")) {
							JOptionPane.showMessageDialog(parFrame, "不要发送空消息~~");
							return;
						}
					
						GameRoomUtil.SendToServerMsg(gamepanel.chessBoard, "MSGTYPE:GAMECHAT#"+sendtext.getText()+"\r\n");
						
						if(sendtext.getText().contains("快点")) {
							GameRoomUtil.palyothermusic("source/flowerdie.mp3");
						}
						if(sendtext.getText().contains("停止")) {
							GameRoomUtil.stopmusic();
							gamepanel.musicing = false;
						}
						if(sendtext.getText().contains("开始")) {
							GameRoomUtil.playBgmusic();
							gamepanel.musicing = true;
						}
						
						jt.append(gamepanel.gameplayer1+"："+gamepanel.dateFormat.format(new Date())+"\n"+"   "+sendtext.getText()+"\n");
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
				
				add(gamepanel);
				add(chatPlane);

				
				
					
				
	}
	public String getRoomType() {
		return RoomType;
	}
	public void setRoomType(String roomType) {
		RoomType = roomType;
	}

		




	
}
class WindowEvent implements WindowListener{
	ChessBoard chessBoard;
	public WindowEvent(){
		
	}
	public WindowEvent(ChessBoard chessBoard) {
		this.chessBoard = chessBoard;
	}

	@Override
	public void windowClosed(java.awt.event.WindowEvent e) {
		
		
	}

	@Override
	public void windowOpened(java.awt.event.WindowEvent e) {
		//System.out.println("打开");
		
		
	}

	@Override
	public void windowClosing(java.awt.event.WindowEvent e) {
		System.out.println("退出");
		ChessBoard.jt.removeAll();
		if(ChessBoard.gamepanel.kaishi) {
			
			int i = JOptionPane.showConfirmDialog(chessBoard, "当前游戏已经开始，退出游戏你将输掉比赛，确认退出游戏？","确认退出？",2);
			if(i==0) {
				
				GameRoomUtil.SendToServerMsg(chessBoard.parFrame,"MSGTYPE:BreakGame\r\n");
				
			}else {
				return;
			}
		}
		
		if(chessBoard.RoomType.equals("CreateRoom")||chessBoard.RoomType.equals("ADDRoom")) {
			String msg = "";
			if(ChessBoard.gamepanel.gameplayer2.equals("")) {
				msg  = "MSGTYPE:LingoutGameNowPerson#0\r\n";
			}else {
				msg  = "MSGTYPE:LingoutGameNowPerson#1\r\n";
			}
			GameRoomUtil.SendToServerMsg(chessBoard,msg);
			chessBoard.parFrame.setVisible(true);
			chessBoard.dispose();
			GameRoomUtil.stopmusic();//停止播放音乐
			ChessBoard.jt.setText("");
			ChessBoard.gamepanel.gameplayer2 = "";
			ChessBoard.gamepanel.zhunbei = false;
			ChessBoard.gamepanel.kaishi = false;
			ChessBoard.gamepanel.chessPoint.clear();
			BeginWindow.winlv = GamePlane.MYWINLV;
			BeginWindow.gamebout = GamePlane.MyPlayGamenum;

		}

	
		
		
		
	}

	@Override
	public void windowIconified(java.awt.event.WindowEvent e) {
		
	}

	@Override
	public void windowDeiconified(java.awt.event.WindowEvent e) {
		
		
	}

	@Override
	public void windowActivated(java.awt.event.WindowEvent e) {

		
	}

	@Override
	public void windowDeactivated(java.awt.event.WindowEvent e) {
		
		
	}
}

class xiaqiThread extends Thread{
	GamePlane gmplane;
	MouseEvent e;
	public xiaqiThread(GamePlane chessBoard, MouseEvent e) {
		this.gmplane = chessBoard;
		this.e = e;
	}

	@Override
	public void run() {
		
		if(gmplane.isme) {
			
			Point p = e.getPoint();
			if(gmplane.MouseAtChess) {
				if(!gmplane.kaishi) {
					JOptionPane.showMessageDialog(gmplane, "游戏还未开始您不能落子");
					return;
				}
				if(gmplane.chessPoint.size()==0&&GamePlane.MyChessColorINT==1) {
					JOptionPane.showMessageDialog(gmplane, "黑方先手~~");
					return;
					
				}
				double x = (p.getX()-430)/45;
				double y = (p.getY()-110)/45;
				GamePlane.rx = (int)x;
				GamePlane.ry =(int)y;
				String xstr = Double.toString(x);
				String ystr = Double.toString(y);
				char xd = xstr.charAt(xstr.indexOf(".")+1);
				char yd = ystr.charAt(ystr.indexOf(".")+1);
				if(xd-'0'>5) {
					GamePlane.rx += 1;
				}
				if(yd-'0'>5) {
					GamePlane.ry +=1;
				}
				//System.out.println(chessBoard.rx+"..."+chessBoard.ry);
				//如果点在棋盘上
				if(!(GamePlane.rx>=0&&GamePlane.rx<=14&&GamePlane.ry>=0&&GamePlane.ry<=14)) {
					return;
				}
				if(gmplane.allChess[GamePlane.rx][GamePlane.ry] !=0) {
					System.out.println("一定落子了");
					return;
				}
				// 白1黑2
				if(GamePlane.MyChessColor.equals("white")) {
					gmplane.allChess[GamePlane.rx][GamePlane.ry] =1;
				}else {
					gmplane.allChess[GamePlane.rx][GamePlane.ry] =2;
				}
				GameRoomUtil.palyothermusic("source/mousedown.mp3");
				gmplane.chessPoint.add(GamePlane.MyChessColor+","+(GamePlane.rx*45+430)+","+(GamePlane.ry*45+110));
				gmplane.isme = false;
				
				
		
				System.out.println(GamePlane.rx+"..."+GamePlane.ry);
				GameRoomUtil.SendToServerMsg(GamePlane.chessBoard,"MSGTYPE:ChessGameing#"+GamePlane.MyChessColor+","+GamePlane.rx+","+gmplane.ry+"\r\n");
			
				
				//判断是否赢了比赛
				boolean iswin = gmplane.checkwin();
				if(iswin) {
					System.out.println("你赢了啊！！！");
					GameRoomUtil.SendToServerMsg(GamePlane.chessBoard,"MSGTYPE:ChessGameing#GameWIN"+"\r\n");
					JOptionPane.showMessageDialog(gmplane, "你赢得了比赛！！");
					ChessBoard.jt.append("系统："+gmplane.dateFormat.format(new Date())+"\n   你赢得了比赛！！\n");
					GamePlane.MyplayGamewinnum = (int) (GamePlane.MyPlayGamenum*GamePlane.MYWINLV);
					GamePlane.HisplayGamewinnum = (int) (GamePlane.HisPlayGamenum*GamePlane.HisWINLV);
					GamePlane.MyplayGamewinnum++;
					gmplane.GameWinAfter(gmplane);
					GameRoomUtil.palyothermusic("source/winmusic.mp3");
				}
				gmplane.repaint();
			
				
			}
			
			
				
		}
	}

}
