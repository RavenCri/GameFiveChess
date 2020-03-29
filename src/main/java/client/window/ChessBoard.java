package client.window;



import java.awt.Color;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowListener;
import java.util.Date;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import common.pojo.User;
import client.plane.GamePlane;
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
	public JSplitPane chatPlane;
	public static JTextArea jt  = new JTextArea();
	public JScrollPane jscroll ;
	private GameRoomUtil gameRoomUtil = new GameRoomUtil();
	public ChessBoard() {
		
	}
	public ChessBoard(Room parFrame,String RoomType,User gamePlayer) {
				setLayout(null);
				gamepanel.musicing = true;
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
				new GameRoomUtil().playBackgroundMusic();
				
				
				setResizable(false);
				
				
				gamepanel.setChessBoard(this);
				gamepanel.setGameplayer1(gamePlayer);
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
				jt.setForeground(new Color(	0,250,154));
				
				jt.append("系统："+GamePlane.dateFormat.format(new Date())+"\n"+"   欢迎加入游戏厅，希望来到这里能给你带来快乐，与室友一起组队开黑吧~~\n");	
				
				send.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						if(sendtext.getText().equals("")) {
							JOptionPane.showMessageDialog(parFrame, "不要发送空消息~~");
							return;
						}
						if(gamepanel.gameplayer2 != null) {
							GameRoomUtil.SendMsgToServer(GamePlane.chessBoard, "GameChat",sendtext.getText());
						}
						if(sendtext.getText().contains("快点")) {
							gameRoomUtil.palyothermusic("sound/flowerdie.mp3");
						}
						if(sendtext.getText().contains("停止")) {
							GameRoomUtil.stopBackgroundMusic();
							gamepanel.musicing = false;
						}
						if(sendtext.getText().contains("开始")) {
							new GameRoomUtil().playBackgroundMusic();
							gamepanel.musicing = true;
						}
						jt.append(gamepanel.gameplayer1.getNickName()+"："+GamePlane.dateFormat.format(new Date())+"\n"+"   "+sendtext.getText()+"\n");
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
					 if(e.getKeyChar() == KeyEvent.VK_ENTER ) {
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
				
				GameRoomUtil.SendMsgToServer(chessBoard.parFrame,"BreakGame",null);
				ChessBoard.gamepanel.gameplayer2.setWinBoutAddOne();
				ChessBoard.gamepanel.gameplayer1.setIntegral(ChessBoard.gamepanel.gameplayer1.getIntegral()-20);
				ChessBoard.gamepanel.GameWinAfter(ChessBoard.gamepanel);
				
				ChessBoard.gamepanel.gameplayer2 = null;
				ChessBoard.gamepanel.zhunbei = false;
				ChessBoard.gamepanel.kaishi = false;
				ChessBoard.gamepanel.chessPoint.clear();
				
			}else {
				return;
			}
		}
		
		if(chessBoard.RoomType.equals("CreateRoom")||chessBoard.RoomType.equals("ADDRoom")) {
			
			GameRoomUtil.SendMsgToServer(chessBoard,"LeaveRoom",null);
			
			//chessBoard.setVisible(false);
			chessBoard.dispose();
			LoginFream.room.setVisible(true);
			GameRoomUtil.stopBackgroundMusic();//停止播放音乐
			ChessBoard.jt.setText("");
			ChessBoard.gamepanel.gameplayer2 = null;
			ChessBoard.gamepanel.zhunbei = false;
			ChessBoard.gamepanel.kaishi = false;
			ChessBoard.gamepanel.chessPoint.clear();
		

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

