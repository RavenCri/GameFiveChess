package client.handle;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import common.pojo.User;
import client.plane.GamePlane;
import client.plane.MouseAdapterOfRoomPlane;
import client.plane.RoomPlane;
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
				// 如果可以读取消息
				if(ReceiveMsgThread.readFlag) {
					//如果消息队列不为空
					if(!Room.msgQueue.isEmpty()  ) {
						// 消息队列出队
						JSONObject msgJSON = Room.msgQueue.poll();
						// 处理消息
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
	/***
	* @Description: 用来处理消息数据
	* @Param: [msgType, msgData]
	* @return: void
	* @Author: raven
	* @Date: 2020/3/28
	*/
	public static void ResultMsg(String msgType ,String msgData) throws InterruptedException {
		GameRoomUtil gameRoomUtil = new GameRoomUtil();
		// 关闭游戏房间
		if(msgType.equals("CloseGameRoom") ) {
				if(BeginWindow.out!=null) {
					try {
						BeginWindow.out.close();
						BeginWindow.socket.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			// 创建游戏房间者信息
			}else if(msgType.equals("CreateRoomUserInfo")) {
			//棋盘窗口
			Room.chessBoard = new ChessBoard(LoginFream.room,"ADDRoom",BeginWindow.userPlayer);
			User CreateRoomUser = JSONObject.toJavaObject(JSONObject.parseObject(msgData), User.class);
			ChessBoard.gamepanel.setGameplayer2(CreateRoomUser); 
			ChessBoard.gamepanel.repaint();
			ChessBoard.jt.append("系统："+ChessBoard.gamepanel.dateFormat.format(new Date())+"\r\n"+"   你加入了"+ChessBoard.gamepanel.gameplayer2.getNickName()+"的房间\n");
			//然后设置当前窗口不可见
			//LoginFream.room.setVisible(false);
		// 加入游戏房间者信息
		}else if(msgType.equals("AddRoomUserInfo")) {
			User addRoomUser = JSONObject.toJavaObject(JSONObject.parseObject(msgData), User.class);
		
			ChessBoard.gamepanel.setGameplayer2(addRoomUser) ; 
			
			ChessBoard.gamepanel.repaint();
			ChessBoard.jt.append("系统："+GamePlane.dateFormat.format(new Date())+"\r\n"+"   "+ChessBoard.gamepanel.gameplayer2.getNickName()+"加入了您的房间\n");
			
			GameRoomUtil.SendMsgToServer(LoginFream.room, "ChessColor", GamePlane.MyChessColor);
		// 创建房间成功
		}else if (msgType.equals("CreateRoomSuccess")) {
			//然后设置当前窗口不可见
			//LoginFream.room.setVisible(false);
			//棋盘窗口
			Room.chessBoard = new ChessBoard(LoginFream.room,"CreateRoom",BeginWindow.userPlayer);
		// 离开房间
		}else if(msgType.equals("LeaveRoom")) {
			
			if(ChessBoard.gamepanel.gameplayer2 !=null){
				ChessBoard.jt.append("系统："+ChessBoard.gamepanel.dateFormat.format(new Date())+"\r\n"+"   "+ChessBoard.gamepanel.gameplayer2.getNickName()+"离开了房间\n");
				ChessBoard.gamepanel.gameplayer2 = null;
			}

			GamePlane.chessBoard.RoomType = "CreateRoom";
			ChessBoard.gamepanel.repaint();
		// 房间棋子颜色类型
		}else if(msgType.equals("ChessColor")){
			if(msgData.equals("black")) {
				GameRoomUtil.ChangeMyChessColor();
				ChessBoard.gamepanel.repaint();
			}
		// 获取游戏在线列表
		}else if (msgType.equals("OnlineGameRooms")||msgType.equals("RoomFullOrRoomDistroy")) {
		
			if(msgType.equals("OnlineGameRooms") ) {
				if(msgData.equals("{\"rooms\":[]}")) {
					Room.emptyRoom = true;
				}else {
					Room.emptyRoom = false;
				}
				
				Room.roomList = JSONObject.parseObject(msgData).getJSONArray("rooms");
			}
			Room.RoomsLeftPlane.removeMouseListener(RoomPlane.mous);
			//不要忘记这里的清空 不然鼠标监听器没效果
			RoomPlane.hasplayer.clear();
			Room.RoomsLeftPlane.repaint();
			
			
			RoomPlane.mous = new MouseAdapterOfRoomPlane(Room.RoomsLeftPlane);
			Room.RoomsLeftPlane.addMouseListener(RoomPlane.mous);
		// 游戏玩家离开
		}else if(msgType.equals("GamePlayerLingout")){
			if(!GamePlane.chessBoard.RoomType.equals("CreateRoom")) {
				
				
				GamePlane.chessBoard.RoomType="CreateRoom";
			}
			ChessBoard.jt.append("系统："+ChessBoard.gamepanel.dateFormat.format(new Date())+"\n   "+ChessBoard.gamepanel.gameplayer2+"离开了房间\n");
			ChessBoard.gamepanel.gameplayer2 = null;
		// 游戏开始
		}else if (msgType.equals("GameBegin")) {
			System.out.println("游戏开始");
			ChessBoard.gamepanel.kaishi = true;
			gameRoomUtil.palyothermusic("sound/begin.mp3");
		// 玩家准备
		}else if (msgType.equals("GameReady")) {
			if(msgData.equals("0")) {
				ChessBoard.gamepanel.zhunbei = false;
			}else {
				ChessBoard.gamepanel.zhunbei = true;
			}
		// 游戏聊天
		}else if(msgType.equals("GameChat")){
			if(msgData.contains("快点")) {
				gameRoomUtil.palyothermusic("sound/flowerdie.mp3");
			}
			gameRoomUtil.palyothermusic("sound/chating.mp3");
			ChessBoard.jt.append(ChessBoard.gamepanel.gameplayer2.getNickName()+"："+ChessBoard.gamepanel.dateFormat.format(new Date())+"\n   "+msgData.toString()+"\n");
			//设置总在最下方
			ChessBoard.jt.setCaretPosition(ChessBoard.jt.getDocument().getLength());
		//	游戏大厅玩家聊天消息
		}else if(msgType.equals("salaChat")){
			
			JSONObject msg = (JSONObject) JSONObject.parse(msgData);
			
			Room.jt.append(msg.getString("from")+"："+GamePlane.dateFormat.format(new Date())+"\n"+"   "+msg.getString("msg")+"\n");
			//设置总在最下方
			Room.jt.setCaretPosition(Room.jt.getDocument().getLength());
		// 系统通知消息
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
		//强行退出比赛
		}else if(msgType.equals("BreakGame")) {
			JOptionPane.showMessageDialog(ChessBoard.gamepanel, "对方退出了房间，你赢的了这场比赛！");
			ChessBoard.gamepanel.gameplayer1.setWinBoutAddOne();
			ChessBoard.gamepanel.gameplayer1.setIntegral(ChessBoard.gamepanel.gameplayer1.getIntegral()+20);
			ChessBoard.gamepanel.GameWinAfter(ChessBoard.gamepanel);
			
			gameRoomUtil.palyothermusic("sound/winmusic.mp3");
		// 输了比赛
		}else if (msgType.equals("YouLose")) {
			
			JOptionPane.showMessageDialog(ChessBoard.gamepanel, "你输了比赛哦~");
			ChessBoard.gamepanel.gameplayer2.setWinBoutAddOne();
			ChessBoard.gamepanel.gameplayer1.setIntegral(ChessBoard.gamepanel.gameplayer1.getIntegral()-20);
			ChessBoard.gamepanel.GameWinAfter(ChessBoard.gamepanel);
		//接收对方棋子位置
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
		// 认输
		}else if (msgType.equals("AdmitDefeat")) {
			JOptionPane.showMessageDialog(ChessBoard.gamepanel, "对方认输了，你很棒哦");
			ChessBoard.gamepanel.gameplayer1.setWinBoutAddOne();
			gameRoomUtil.palyothermusic("sound/winmusic.mp3");
			ChessBoard.gamepanel.gameplayer1.setIntegral(ChessBoard.gamepanel.gameplayer1.getIntegral()+20);
			ChessBoard.gamepanel.GameWinAfter(ChessBoard.gamepanel);	
		// 和棋
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
		// 悔棋
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
		}else if(msgType.equals("AllUserInfo")){

			List<List> users = (List<List>) JSONObject.parse(msgData);
			Room.cellData = new String[users.size()][4];

			//System.out.println(JSON.toJSON(users));

			JSONArray us = (JSONArray) users.get(0);
			for (int i = 0; i < us.size(); i++) {
				Room.cellData[i][0]=us.getJSONObject(i).getString("userName");
				Room.cellData[i][1]=us.getJSONObject(i).getString("nickName");
				Room.cellData[i][2]=us.getJSONObject(i).getString("integral");
				Room.cellData[i][3]=Double.parseDouble(us.getJSONObject(i).getString("winingProbability"))*100+"%";
			}
			/*for (int i = 0; i < Room.cellData.length; i++) {
				System.out.println(Room.cellData[i][0]);
				System.out.println(Room.cellData[i][1]);
				System.out.println(Room.cellData[i][2]);
				System.out.println(Room.cellData[i][3]);
			}*/
			Room.tableModel =  new DefaultTableModel(Room.cellData , Room.columnNames);

			Room.gameUsersTable.setModel(Room.tableModel);
			//隐藏账号列
			TableColumn columnOne = Room.gameUsersTable.getColumnModel().getColumn(0);
			columnOne.setWidth( 0 );
			columnOne.setPreferredWidth( 0 );
			columnOne.setMaxWidth( 0 );
			columnOne.setMinWidth( 0 );
			Room.gameUsersTable.getTableHeader().getColumnModel().getColumn( 0 ).
					setMaxWidth( 0 );
			Room.gameUsersTable.getTableHeader().getColumnModel().getColumn( 0 ).
					setMinWidth( 0 );
		}else if( msgType.equals("hasUserAddYou")){
			JSONObject data = JSON.parseObject(msgData);
			String nickname = data.getString("fromNickName");
			String username = data.getString("fromUserName");
			int i =JOptionPane.showConfirmDialog(Room.RoomsLeftPlane, String.format("%s(%s)请求加您为好友，是否同意？",nickname,username),"社交信息",2);

			if(i==0) {

				data.put("status",true);

				System.out.println("您同意了");
			}else {
				data.put("status",false);
				System.out.println("您不同意");
			}

			GameRoomUtil.SendMsgToServer(LoginFream.room,"addFriendCallBack",data.toJSONString());
		}else if( msgType.equals("addFriendCallBack")){

			JOptionPane.showMessageDialog(Room.RoomsLeftPlane, msgData);

		}else if(msgType.equals("invitationGame")){
			JSONObject data = JSON.parseObject(msgData);
			String nickname = data.getString("fromNickName");
			String username = data.getString("fromUserName");
			int i =JOptionPane.showConfirmDialog(Room.RoomsLeftPlane, String.format("%s(%s)邀请您一起游戏，是否前往？",nickname,username),"邀请游戏",2);


			if(i==0) {
				data.put("status",true);
				// 如果已经创建了游戏房间
				if(Room.chessBoard != null && Room.chessBoard.isVisible() ){

					GameRoomUtil.SendMsgToServer(LoginFream.room.chessBoard,"LeaveRoom",null);
					LoginFream.room.chessBoard.dispose();
					GameRoomUtil.stopBackgroundMusic();//停止播放音乐
					ChessBoard.jt.setText("");

					ChessBoard.gamepanel.zhunbei = false;
					ChessBoard.gamepanel.kaishi = false;
					ChessBoard.gamepanel.chessPoint.clear();
				}

				System.out.println("您同意了");
			}else {
				data.put("status",false);
				System.out.println("您不同意");
			}

			GameRoomUtil.SendMsgToServer(LoginFream.room,"invitationGameCallBack",data.toJSONString());
			//邀请状态
		}else if(msgType.equals("invitationGameCallBack")){

			if(JSONObject.isValidObject(msgData)){
				JSONObject data = JSON.parseObject(msgData);
				if(!data.getBoolean("status")){
					JOptionPane.showMessageDialog(Room.RoomsLeftPlane, data.getString("toNickName")+"拒绝了你的游戏邀请！");
				}else{
					// 先尝试离开自己的房间
					if(Room.chessBoard != null && Room.chessBoard.isVisible() ){
						GameRoomUtil.SendMsgToServer(LoginFream.room.chessBoard,"LeaveRoom",null);
						LoginFream.room.chessBoard.dispose();
						GameRoomUtil.stopBackgroundMusic();//停止播放音乐
						ChessBoard.jt.setText("");

						ChessBoard.gamepanel.zhunbei = false;
						ChessBoard.gamepanel.kaishi = false;
						ChessBoard.gamepanel.chessPoint.clear();
					}
					// 再创建新房间
					GameRoomUtil.SendMsgToServer(LoginFream.room,"CreateGameRoom",null);
					// 通知创建好了
					GameRoomUtil.SendMsgToServer(LoginFream.room,"CreateRoomOK",data.toJSONString());
				}
			}else{
				JOptionPane.showMessageDialog(Room.RoomsLeftPlane, msgData);
			}

		}else if(msgType.equals("CreateRoomOKAddRoom")){
			GameRoomUtil.SendMsgToServer(LoginFream.room,"AddGameHomeOwner",msgData);
		}else if(msgType.equals("friendUserList")){
			System.out.println("好友列表："+msgData);
			Room.userfirends = (List<JSONObject>) JSONObject.parse(msgData);
		}
		ChessBoard.gamepanel.repaint();
	}
}
 