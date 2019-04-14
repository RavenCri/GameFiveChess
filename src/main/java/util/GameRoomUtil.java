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

import com.raven.main.BeginWindow;
import com.raven.main.ChessBoard;
import com.raven.main.GamePlane;
import com.raven.main.MouseThing;
import com.raven.main.Room;
import com.raven.main.RoomPlane;

import client.GameClient;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
public class GameRoomUtil {
	
	static Player p;
	static Thread bGThread;
	public static void SendToServerMsg(JFrame jFrame,String Msg){
		try {
			BeginWindow.out.write(Msg);
			BeginWindow.out.flush();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(jFrame, "您可能已经与服务器断开了连接。。");
			jFrame.setVisible(false);
			GameClient.beginWindow.setVisible(true);
			
		}
		
	}
	
/*	public static void SendToServerMsg(JFrame jFrame,String Msg){
		new Thread(()->{
			try {
				
				
				BeginWindow.out.write(Msg);
				BeginWindow.out.flush();
				String msg  = BeginWindow.in.readLine();
				// 切割消息头 消息体
				String msgsp[] = msg.split("#");
				if(msgsp[0].equals("MSGTYPE:OnlineGameRooms")) {
					if(msgsp.length==2) {
						Room.MSG = msgsp[1];
					}else {
						Room.MSG = "";
					}
				}else {
					Room.MSG = "";
				}
				System.out.println(Room.MSG);
				
			
			} catch (IOException e) {
				JOptionPane.showMessageDialog(jFrame, "刷新失败！您可能已经与服务器断开了连接。。");
				jFrame.setVisible(false);
				GameClient.beginWindow.setVisible(true);
				System.out.println("刷新失败！");
				
			}catch (NullPointerException e) {
				GameClient.beginWindow.setVisible(true);
				jFrame.setVisible(false);
				JOptionPane.showMessageDialog(jFrame, "Raven的服务器连接有问题，请启动本地Server服务");
			}
		});

		
		
		
	}*/
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
	public static void palyothermusic(String filename){
		
		new Thread(()->{
			
		File file = new File(filename);
		if(file.exists()) {
			try {
				InputStream in = new FileInputStream(file);
				
				Player p = new Player(in);
				p.play();
				
			} catch (FileNotFoundException e1) {
			
				e1.printStackTrace();
			}catch (JavaLayerException e1) {
				
				e1.printStackTrace();
			}	
		}
		
		}).start();
	}
	public static  void  playChessMovemusic(String filename) {
		new Thread(()->{
			BeginWindow.bofang = true;
		File file = new File(filename);
		try {
			InputStream in = new FileInputStream(file);
			
			Player play = new Player(in);
			play.play();
			BeginWindow.bofang = false;
		} catch (FileNotFoundException e1) {
			
			e1.printStackTrace();
		}catch (JavaLayerException e1) {
			
			e1.printStackTrace();
		}	
		}).start();
	}
	public static  void  playBgmusic() {
	bGThread= 	new Thread() {
			@Override
			public void run() {
				while(true) {
				
					File file = new File("source/bgmusic.mp3");
					try {
						InputStream in = new FileInputStream(file);
						p = new Player(in);
						p.play();
						
						
						
					} catch (FileNotFoundException e1) {
						
						e1.printStackTrace();
					}catch (JavaLayerException e1) {
						
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
	public static int ResultMsg() {
	
		try {
			String msg = BeginWindow.in.readLine();
			System.out.println("收到："+msg);
			String MsgHeadType[] = msg.split(":");
			//消息类型
			String MsgType = MsgHeadType[1].split("#")[0];
			//消息数据
			String MsgData = null;
			//等于2说明有消息数据
			if(MsgHeadType[1].split("#").length==2) {
				MsgData = MsgHeadType[1].split("#")[1];
			}
			
			
			if(!MsgHeadType[0].equals("MSGTYPE")) {
				
				return -1;
				// 
				//通知创建房间的玩家xx加入游戏
			}else if (MsgType.equals("namerepeat")||MsgType.equals("OnlineGameRooms")||MsgType.equals("RoomFullOrRoomDistroy")) {
				
				GameClient.MSG = MsgData;
				Room.roomPlane.removeMouseListener(RoomPlane.mous);
				Room.roomPlane.repaint();
				//不要忘记这里的清空 不然鼠标监听器没效果
				RoomPlane.hasplayer.clear();
				
				RoomPlane.mous = new MouseThing(Room.roomPlane);
				Room.roomPlane.addMouseListener(RoomPlane.mous);
				
				
				// 通知创建房间的玩家
			}else if(MsgType.equals("ADDYourRoom")) {
				// ADDYourRoom#1%raven,2%xiaotuzi,来跟你一起玩游戏啦！
				GameRoomUtil.SendToServerMsg(GamePlane.chessBoard,"MSGTYPE:NoticeGameState#"+MsgData.split(",")[1]+"%"+GamePlane.MYWINLV+"%"+GamePlane.MyPlayGamenum+"\r\n");
				ChessBoard.gamepanel.gameplayer2 = MsgData.split(",")[1].split("%")[1];
				GamePlane.HisPlayGamenum = Integer.parseInt(MsgData.split(",")[2].split("%")[1]);
				GamePlane.HisWINLV = Double.valueOf(MsgData.split(",")[2].split("%")[0]);
				ChessBoard.jt.append("系统："+ChessBoard.gamepanel.dateFormat.format(new Date())+"\r\n"+"   "+ChessBoard.gamepanel.gameplayer2+"加入了您的房间\n");
				//通知加入玩家 加入了谁的房间 获取 他的名字
			}else if (MsgType.equals("ADDSucc")) {
				String name = MsgData.split(",")[0];
				String mess = MsgData.split(",")[1];
				ChessBoard.gamepanel.gameplayer2 = name.split("&")[0].split("%")[1];
				GamePlane.HisWINLV = Double.valueOf(mess.split("%")[0]);
				GamePlane.HisPlayGamenum = Integer.parseInt(mess.split("%")[1]);
				
				ChessBoard.jt.append("系统："+ChessBoard.gamepanel.dateFormat.format(new Date())+"\r\n"+"   你加入了"+ChessBoard.gamepanel.gameplayer2+"的房间\n");
			}else if(MsgType.equals("ChessGameing")) {
				String cxy[] = MsgData.split(",");
				if(cxy.length==3) {//长度为3的话，说明是下棋的信息
					GamePlane.rx = Integer.parseInt(cxy[1]);
					GamePlane.ry = Integer.parseInt(cxy[2]);
					int x = GamePlane.rx*45+430;
					int y = GamePlane.ry *45+110;
					ChessBoard.gamepanel.chessPoint.add(cxy[0]+","+x+","+y);
					//设置我的状态为可用
					ChessBoard.gamepanel.isme = true;
					if(cxy[0].equals("white")) {
						ChessBoard.gamepanel.allChess[Integer.parseInt(cxy[1])][Integer.parseInt(cxy[2])] =1;
					}
					else if(cxy[0].equals("black")){
						
						ChessBoard.gamepanel.allChess[Integer.parseInt(cxy[1])][Integer.parseInt(cxy[2])] =2;
					}//长度是1的话说明 和棋和认输 和悔棋 
				}else if (cxy.length==1) {//如果是认输或者和棋
					GamePlane.MyplayGamewinnum = (int) (GamePlane.MyPlayGamenum*GamePlane.MYWINLV);
					GamePlane.HisplayGamewinnum = (int) (GamePlane.HisPlayGamenum*GamePlane.HisWINLV);
					if(cxy[0].equals("HeWON")||cxy[0].equals("YouWIN")||cxy[0].equals("heqi")) {
						if(cxy[0].equals("HeWON")) {
							JOptionPane.showMessageDialog(ChessBoard.gamepanel, "你输了！！！！");
							ChessBoard.jt.append("系统："+ChessBoard.gamepanel.dateFormat.format(new Date())+"\r\n   你输了！！！！\n");
							GamePlane.HisplayGamewinnum++;
						}else if(cxy[0].equals("YouWIN")){
							JOptionPane.showMessageDialog(ChessBoard.gamepanel, "对方认输了，你很棒哦");
							GamePlane.MyplayGamewinnum++;
							GameRoomUtil.palyothermusic("source/winmusic.mp3");
						}else if (cxy[0].equals("heqi")) {
							int i =JOptionPane.showConfirmDialog(ChessBoard.gamepanel, "对方请求和棋，你同意吗？","对方请求和棋",2);
							if(i==0) {
								GameRoomUtil.SendToServerMsg(GamePlane.chessBoard,"MSGTYPE:ChessGameing#heqi,0\r\n");
						
								GamePlane.HisplayGamewinnum++;
								GamePlane.MyplayGamewinnum++;
							}else {
								GameRoomUtil.SendToServerMsg(GamePlane.chessBoard,"MSGTYPE:ChessGameing#heqi,1\r\n");
								
								return 0;
							}
							
						} 
						//游戏完成所做的事
						ChessBoard.gamepanel.GameWinAfter(ChessBoard.gamepanel);
					}else if (cxy[0].equals("huiqi")) {//如果是悔棋
						
						int i =JOptionPane.showConfirmDialog(ChessBoard.gamepanel, "对方想要悔棋，你同意吗？","对方请求悔棋",2);
						if(i==0) {
							GameRoomUtil.SendToServerMsg(GamePlane.chessBoard,"MSGTYPE:ChessGameing#huiqi,0\r\n");
							//同意之后禁用自己的状态
							ChessBoard.gamepanel.isme =false;
							ChessBoard.gamepanel.allChess[GamePlane.rx][GamePlane.ry] =0;
							ChessBoard.gamepanel.chessPoint.remove(ChessBoard.gamepanel.chessPoint.size()-1);
						
						}else {
							
							GameRoomUtil.SendToServerMsg(GamePlane.chessBoard,"MSGTYPE:ChessGameing#huiqi,1\r\n");
						}
						
					}
				}else if (cxy.length==2) {
					if(cxy[0].equals("huiqi")) {
						if(cxy[1].equals("0")) {
							System.out.println("同意悔棋");
							ChessBoard.gamepanel.allChess[GamePlane.rx][GamePlane.ry] =0;
							ChessBoard.gamepanel.chessPoint.remove(ChessBoard.gamepanel.chessPoint.size()-1);
							ChessBoard.gamepanel.isme = true;
						}else {
							JOptionPane.showMessageDialog(ChessBoard.gamepanel, "对方不同意悔棋");
						}
					}else if (cxy[0].equals("heqi")) {
						if(cxy[1].equals("0")) {
							System.out.println("同意和棋");
							GamePlane.MyplayGamewinnum = (int) (GamePlane.MyPlayGamenum*GamePlane.MYWINLV);
							GamePlane.HisplayGamewinnum = (int) (GamePlane.HisPlayGamenum*GamePlane.HisWINLV);
							GamePlane.HisplayGamewinnum++;
							GamePlane.MyplayGamewinnum++;
							//游戏完成所做的事
							ChessBoard.gamepanel.GameWinAfter(ChessBoard.gamepanel);	

						}else {
							JOptionPane.showMessageDialog(ChessBoard.gamepanel, "对方不同意和棋");
						}
					}
						
					
				}

				
			}else if(MsgType.equals("youareblackoriswhite")) {
				GameRoomUtil.SendToServerMsg(GamePlane.chessBoard,"MSGTYPE:MyColor#"+GamePlane.MyChessColor+"%"+GamePlane.MYWINLV+"%"+GamePlane.MyPlayGamenum+"\r\n");
				
			}else if(MsgType.equals("SendHisColor")) {
				System.out.println("房主的信息："+MsgData);
				String[] split = MsgData.split("%");
					//设置我的颜色与防止的相反
					if(split[0].equals("white")) {
						GamePlane.MyChessColor = "black";
						GamePlane.MyChessColorINT = 2;
						
						
						System.out.println("设置我的颜色为black");
					}else {
						GamePlane.MyChessColor = "white";
						GamePlane.MyChessColorINT = 1;
						
						System.out.println("设置我的颜色为white");
					}
					
					GamePlane.HisPlayGamenum = Integer.parseInt(split[2]);
					GamePlane.HisWINLV = Double.valueOf(split[1]);
			}else if(MsgType.equals("GamePlayerLingout")){
				if(!GamePlane.chessBoard.RoomType.equals("CreateRoom")) {
					
					GamePlane.HisWINLV  = 0;
					GamePlane.chessBoard.RoomType="CreateRoom";
				}
				ChessBoard.jt.append("系统："+ChessBoard.gamepanel.dateFormat.format(new Date())+"\n   "+ChessBoard.gamepanel.gameplayer2+"离开了房间\n");
				ChessBoard.gamepanel.gameplayer2 ="";
				
			}else if (MsgType.equals("GAMEBEGIN")) {
				System.out.println("游戏开始");
				ChessBoard.gamepanel.kaishi = true;
				GameRoomUtil.palyothermusic("source/begin.mp3");
				
			}else if (MsgType.equals("GAMEREADY")) {
				if(MsgData.equals("0")) {
					ChessBoard.gamepanel.zhunbei = false;
				}else {
					ChessBoard.gamepanel.zhunbei = true;
				}
			}else if(MsgType.equals("GAMECHAT")){
				if(MsgData.contains("快点")) {
					GameRoomUtil.palyothermusic("source/flowerdie.mp3");
				}
				GameRoomUtil.palyothermusic("source/chating.mp3");
				ChessBoard.jt.append(ChessBoard.gamepanel.gameplayer2+"："+ChessBoard.gamepanel.dateFormat.format(new Date())+"\n   "+MsgData.toString()+"\n");
				//设置总在最下方
				ChessBoard.jt.setCaretPosition(ChessBoard.jt.getDocument().getLength());
				
			}else if(MsgType.equals("BreakGame")) {
				JOptionPane.showMessageDialog(ChessBoard.gamepanel, "对方退出了房间，你赢的了这场比赛！");
				GamePlane.MyplayGamewinnum = (int) (GamePlane.MyPlayGamenum*GamePlane.MYWINLV);
				GamePlane.HisplayGamewinnum = (int) (GamePlane.HisPlayGamenum*GamePlane.HisWINLV);
				GamePlane.MyplayGamewinnum++;
				GamePlane.HisWINLV  = 0;
				ChessBoard.gamepanel.GameWinAfter(ChessBoard.gamepanel);
				GameRoomUtil.palyothermusic("source/winmusic.mp3");
			}
			if(ChessBoard.gamepanel!=null)
			ChessBoard.gamepanel.repaint();
		} catch (IOException e) {
			
			return -1;
		}
		return 0;
		
	
	}
}
