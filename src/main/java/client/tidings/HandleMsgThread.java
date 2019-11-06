package client.tidings;

import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JOptionPane;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import bean.User;
import client.start.GameClient;
import client.ui.GamePlane;
import client.ui.MouseAdapterOfRoomPlane;
import client.ui.RoomPlane;
import client.window.BeginWindow;
import client.window.ChessBoard;
import client.window.LoginFream;
import client.window.Room;
import util.GameRoomUtil;

/** 
 * @ClassName: HandleMsg
 * @description: 
 * @author:Raven
 * @Date: 2019年10月20日 下午12:30:28
 */

public class HandleMsgThread extends Thread{

	@Override
		public void run() {
			while (true) {
				try {
					if(ReceiveMsgThread.readFlag) {
						if(!Room.msgQueue.isEmpty()  ) {
							JSONObject msgJSON = Room.msgQueue.poll();
							ResultMsg(msgJSON.getString("msgType"), msgJSON.getString("msg"));
						}
						
						Thread.sleep(200);
					}else {
						System.out.println("停止了处理消息线程");
						break;
					}
					
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	public static void ResultMsg(String msgType ,String msgData) {
		GameRoomUtil gameRoomUtil = new GameRoomUtil();
		if(msgType.equals("CloseGameRoom") ) {
				if(BeginWindow.out!=null) {
					try {
						BeginWindow.out.close();
						BeginWindow.socket.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}else if(msgType.equals("CreateRoomUserInfo")) {
			//棋盘窗口
			Room.chessBoard = new ChessBoard(LoginFream.room,"ADDRoom",BeginWindow.userPlayer);
			User CreateRoomUser = JSONObject.toJavaObject(JSONObject.parseObject(msgData), User.class);
			ChessBoard.gamepanel.setGameplayer2(CreateRoomUser); 
			ChessBoard.gamepanel.repaint();
			ChessBoard.jt.append("系统："+ChessBoard.gamepanel.dateFormat.format(new Date())+"\r\n"+"   你加入了"+ChessBoard.gamepanel.gameplayer2.getNickName()+"的房间\n");
			//然后设置当前窗口不可见
			LoginFream.room.setVisible(false);
		
		}else if(msgType.equals("AddRoomUserInfo")) {
			User addRoomUser = JSONObject.toJavaObject(JSONObject.parseObject(msgData), User.class);
		
			ChessBoard.gamepanel.setGameplayer2(addRoomUser) ; 
			
			ChessBoard.gamepanel.repaint();
			ChessBoard.jt.append("系统："+GamePlane.dateFormat.format(new Date())+"\r\n"+"   "+ChessBoard.gamepanel.gameplayer2.getNickName()+"加入了您的房间\n");
			
			GameRoomUtil.SendMsgToServer(LoginFream.room, "RoomMessage", GamePlane.MyChessColor);
		}else if (msgType.equals("CreateRoomSuccess")) {

			//然后设置当前窗口不可见
			LoginFream.room.setVisible(false);
			//棋盘窗口
			Room.chessBoard = new ChessBoard(LoginFream.room,"CreateRoom",BeginWindow.userPlayer);;
		}else if(msgType.equals("LeaveRoom")) {
			
			
			ChessBoard.jt.append("系统："+ChessBoard.gamepanel.dateFormat.format(new Date())+"\r\n"+"   "+ChessBoard.gamepanel.gameplayer2.getNickName()+"离开了房间\n");
			ChessBoard.gamepanel.gameplayer2 = null;
			GamePlane.chessBoard.RoomType = "CreateRoom";
			ChessBoard.gamepanel.repaint();
			
		}else if(msgType.equals("RoomMessage")){
			if(msgData.equals("black")) {
				GameRoomUtil.ChangeMyChessColor();
				ChessBoard.gamepanel.repaint();
			}
		}else if (msgType.equals("OnlineGameRooms")||msgType.equals("RoomFullOrRoomDistroy")) {
		
			if(msgType.equals("OnlineGameRooms") ) {
				if(msgData.equals("{\"rooms\":[]}")) {
					Room.emptyRoom = true;
				}else {
					Room.emptyRoom = false;
				}
				
				Room.roomList = JSONObject.parseObject(msgData).getJSONArray("rooms");
			}
			Room.roomPlane.removeMouseListener(RoomPlane.mous);
			//不要忘记这里的清空 不然鼠标监听器没效果
			RoomPlane.hasplayer.clear();
			Room.roomPlane.repaint();
			
			
			RoomPlane.mous = new MouseAdapterOfRoomPlane(Room.roomPlane);
			Room.roomPlane.addMouseListener(RoomPlane.mous);
		}else if(msgType.equals("GamePlayerLingout")){
			if(!GamePlane.chessBoard.RoomType.equals("CreateRoom")) {
				
				
				GamePlane.chessBoard.RoomType="CreateRoom";
			}
			ChessBoard.jt.append("系统："+ChessBoard.gamepanel.dateFormat.format(new Date())+"\n   "+ChessBoard.gamepanel.gameplayer2+"离开了房间\n");
			ChessBoard.gamepanel.gameplayer2 = null;
			
		}else if (msgType.equals("GameBegin")) {
			System.out.println("游戏开始");
			ChessBoard.gamepanel.kaishi = true;
			gameRoomUtil.palyothermusic("begin.mp3");
			
		}else if (msgType.equals("GameReady")) {
			if(msgData.equals("0")) {
				ChessBoard.gamepanel.zhunbei = false;
			}else {
				ChessBoard.gamepanel.zhunbei = true;
			}
		}else if(msgType.equals("GameChat")){
			if(msgData.contains("快点")) {
				gameRoomUtil.palyothermusic("flowerdie.mp3");
			}
			gameRoomUtil.palyothermusic("chating.mp3");
			ChessBoard.jt.append(ChessBoard.gamepanel.gameplayer2.getNickName()+"："+ChessBoard.gamepanel.dateFormat.format(new Date())+"\n   "+msgData.toString()+"\n");
			//设置总在最下方
			ChessBoard.jt.setCaretPosition(ChessBoard.jt.getDocument().getLength());
			
		}else if(msgType.equals("salaChat")){
			
			JSONObject msg = (JSONObject) JSONObject.parse(msgData);
			
			Room.jt.append(msg.getString("from")+"："+GamePlane.dateFormat.format(new Date())+"\n"+"   "+msg.getString("msg")+"\n");
			//设置总在最下方
			Room.jt.setCaretPosition(Room.jt.getDocument().getLength());
			
		}else if(msgType.equals("systemNotify")){
			
			JSONObject msgJSON = (JSONObject) JSONObject.parse(msgData);
			String notifyType= msgJSON.getString("NotifyType");
			String username = msgJSON.getString("who");
			String tip;
			if(notifyType.equals("logout")) {
				tip = "系统："+username  +"退出了游戏大厅\n";
			}else {
				tip = "系统："+username  +"加入了游戏大厅\n";
			}
			Room.jt.append(tip);
			//设置总在最下方
			Room.jt.setCaretPosition(Room.jt.getDocument().getLength());
			
		}else if(msgType.equals("BreakGame")) {
			JOptionPane.showMessageDialog(ChessBoard.gamepanel, "对方退出了房间，你赢的了这场比赛！");
			ChessBoard.gamepanel.gameplayer1.setWinBoutAddOne();
			
			ChessBoard.gamepanel.GameWinAfter(ChessBoard.gamepanel);
			
			gameRoomUtil.palyothermusic("winmusic.mp3");
		}else if (msgType.equals("YouLose")) {
			
			JOptionPane.showMessageDialog(ChessBoard.gamepanel, "你输了比赛哦~");
			ChessBoard.gamepanel.gameplayer2.setWinBoutAddOne();
			ChessBoard.gamepanel.GameWinAfter(ChessBoard.gamepanel);
			
		}else if(msgType.equals("ChessBorldLocation")) {
			String ChessBorldLocation[] = msgData.split(",");
			GamePlane.rx = Integer.parseInt(ChessBorldLocation[1]);
			GamePlane.ry = Integer.parseInt(ChessBorldLocation[2]);
			int x = GamePlane.rx;
			int y = GamePlane.ry;
			int xPoint = GamePlane.rx*45+430;
			int yPoint = GamePlane.ry *45+110;
			ChessBoard.gamepanel.chessPoint.add(ChessBorldLocation[0]+","+xPoint+","+yPoint);
			
			if(ChessBorldLocation[0].equals("white")) {
				ChessBoard.gamepanel.allChess[x][y] = 1;
			}
			else if(ChessBorldLocation[0].equals("black")){
				
				ChessBoard.gamepanel.allChess[x][y] = 2;
			}
			ChessBoard.gamepanel.isme = true;
		}else if (msgType.equals("AdmitDefeat")) {
			JOptionPane.showMessageDialog(ChessBoard.gamepanel, "对方认输了，你很棒哦");
			ChessBoard.gamepanel.gameplayer1.setWinBoutAddOne();
			gameRoomUtil.palyothermusic("winmusic.mp3");
			ChessBoard.gamepanel.GameWinAfter(ChessBoard.gamepanel);	
		}else if (msgType.equals("heqi")) {
			if(msgData != null) {
				if(msgData.equals("1")) {
					System.out.println("同意和棋");
					ChessBoard.gamepanel.gameplayer1.setWinBoutAddOne();
					ChessBoard.gamepanel.gameplayer2.setWinBoutAddOne();
					JOptionPane.showMessageDialog(ChessBoard.gamepanel, "对方同意了和棋");
					//游戏完成所做的事
					ChessBoard.gamepanel.GameWinAfter(ChessBoard.gamepanel);	

				}else {
					JOptionPane.showMessageDialog(ChessBoard.gamepanel, "对方不同意和棋");
				}
			}else {
				int i =JOptionPane.showConfirmDialog(ChessBoard.gamepanel, "对方请求和棋，你同意吗？","对方请求和棋",2);
				if(i==0) {
					GameRoomUtil.SendMsgToServer(GamePlane.chessBoard,"heqi","1");
					ChessBoard.gamepanel.gameplayer1.setWinBoutAddOne();
					ChessBoard.gamepanel.gameplayer2.setWinBoutAddOne();
					ChessBoard.gamepanel.GameWinAfter(ChessBoard.gamepanel);	
				}else {
					GameRoomUtil.SendMsgToServer(GamePlane.chessBoard,"heqi","0");
				}
			}
		}else if (msgType.equals("huiqi")) {
			if(msgData != null) {
				if(msgData.equals("1")) {
					System.out.println("同意悔棋");
					ChessBoard.gamepanel.allChess[GamePlane.rx][GamePlane.ry] =0;
					ChessBoard.gamepanel.chessPoint.remove(ChessBoard.gamepanel.chessPoint.size()-1);
					ChessBoard.gamepanel.isme = true;
				}else {
					JOptionPane.showMessageDialog(ChessBoard.gamepanel, "对方不同意悔棋");
				}		
			}else {
				int i =JOptionPane.showConfirmDialog(ChessBoard.gamepanel, "对方想要悔棋，你同意吗？","对方请求悔棋",2);
				if(i==0) {
					GameRoomUtil.SendMsgToServer(GamePlane.chessBoard,"huiqi","1");
					//同意之后禁用自己的状态
					ChessBoard.gamepanel.isme =false;
					ChessBoard.gamepanel.allChess[GamePlane.rx][GamePlane.ry] =0;
					ChessBoard.gamepanel.chessPoint.remove(ChessBoard.gamepanel.chessPoint.size()-1);
				
				}else {
					
					GameRoomUtil.SendMsgToServer(GamePlane.chessBoard,"huiqi","0");
				}
			}
		}
		ChessBoard.gamepanel.repaint();
}
}
 