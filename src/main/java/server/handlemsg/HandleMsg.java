package server.handlemsg;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.tomcat.util.security.MD5Encoder;
import org.springframework.util.DigestUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import bean.User;
import pojo.GameRoom;
import pojo.UsersBuffer;
import server.OnlineManage;
import server.Server;
import util.GameRoomUtil;

public class HandleMsg implements Runnable{
	private Socket socket;
	
	private GameRoom gameRoom = new GameRoom(null);
	public HandleMsg(){
	}
	public  HandleMsg(Socket socket) {
		this.socket = socket;
	}		
	@Override
	public void run() {
	
		
		try {
			BufferedReader bfReader = new BufferedReader(new InputStreamReader(socket
					.getInputStream(),"UTF-8"));
			BufferedWriter bfWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(),"UTF-8"));
			gameRoom.setUserBuffer1(new UsersBuffer(null, bfReader, bfWriter));
		
		} catch (IOException e1) {
			
			e1.printStackTrace();
		}
		
		while(true) {
			try {
				
				String resMsg = gameRoom.getUserBuffer1().getReaderPlayer().readLine();
				JSONObject msgJson;
				try {
					msgJson = JSONObject.parseObject(resMsg);
					System.out.println(msgJson);
					if(msgJson == null || msgJson.equals("{}")) {
						LingoutChessBordRoom(gameRoom.getUserBuffer1());
						break;
					}
				} catch (Exception e) {
						break;	
				}
				
				//消息类型
				String msgType = msgJson.getString("msgType");
				//消息数据
				String msgData =  msgJson.getString("msg");
				String securityCheck =  msgJson.getString("securityCheck");
				//消息验证
				if(!DigestUtils.md5DigestAsHex((msgType +msgData).getBytes()).equals(securityCheck)) {
					System.out.println("消息安全机制验证失败！");
					break;	
				}	
				if(!OnlineManage.onlineUsers.contains(gameRoom.getUserBuffer1())) {	
					boolean AlreadyLogined = false;	
					//新加入用户放入在线列表
					if(msgType.equals("LoginGame")) {
						
						User userPlayer = (User) JSONObject.toJavaObject(msgJson.getJSONObject("msg"), User.class);
						for (UsersBuffer userBuffer :OnlineManage.onlineUsers) {
							if(userBuffer.getUser().getUserName().equals(userPlayer.getUserName())) {
								sendMsgToPlayer(gameRoom.getUserBuffer1(), "AlreadyLogined","1");		
								System.out.println(userPlayer.getNickName()+"已经登录过了");
								AlreadyLogined = true;
								break;
							}
						}
						if(!AlreadyLogined) {
							gameRoom.getUserBuffer1().setUser(userPlayer);
							OnlineManage.onlineUsers.add(gameRoom.getUserBuffer1());
							System.out.println(userPlayer.getNickName()+"登录了服务器");
							sendMsgToPlayer(gameRoom.getUserBuffer1(), "AlreadyLogined","0");
							SendChessBoardlist(gameRoom.getUserBuffer1().getWriterPlayer());
						}							
					}
				}
						
				//创建一个房间
				if(msgType.equals("CreateGameRoom")) {
					
					OnlineManage.Rooms.add(gameRoom);
					sendMsgToPlayer(gameRoom.getUserBuffer1(), "CreateRoomSuccess",null);		
					sendToAllPlayersRoomList();
				}
				// 发送棋盘list
				else if(msgType.equals("GetOnlineGame")) {
					SendChessBoardlist(gameRoom.getUserBuffer1().getWriterPlayer());
				
				}else if(msgType.equals("AddGameHomeOwner")) {
					addRoom(msgData);
				
				}else if (msgType.equals("ChessBorldLocation")) {
					sendMsgToPlayer(gameRoom.getUserBuffer2(), "ChessBorldLocation",msgData);	
				}else if(msgType.equals("LeaveRoom")) {
								
					LingoutChessBordRoom(gameRoom.getUserBuffer1());
					
					//游戏开始
				}else if (msgType.equals("GameBegin")) {
					sendMsgToPlayer(gameRoom.getUserBuffer2(), "GameBegin",null);
					//游戏准备
				}else if (msgType.equals("GameReady")) {
					sendMsgToPlayer(gameRoom.getUserBuffer2(), "GameReady",msgData);
					//游戏聊天
				}else if (msgType.equals("GameChat")) {
					sendMsgToPlayer(gameRoom.getUserBuffer2(), "GameChat",msgData);
					//游戏中途退出游戏
				}else if(msgType.equals("BreakGame")) {
				
					sendMsgToPlayer(gameRoom.getUserBuffer2(), "BreakGame",null);
					
				}else if(msgType.equals("heqi")) {
				
					sendMsgToPlayer(gameRoom.getUserBuffer2(), "heqi",msgData);
					
				}else if(msgType.equals("huiqi")) {
				
					sendMsgToPlayer(gameRoom.getUserBuffer2(), "huiqi",msgData);
					
				}else if(msgType.equals("AdmitDefeat")) {
				
					sendMsgToPlayer(gameRoom.getUserBuffer2(), "AdmitDefeat",msgData);
					
				}else if(msgType.equals("CloseGameRoom")) {
				
					sendMsgToPlayer(gameRoom.getUserBuffer1(), "CloseGameRoom",null);
					LingoutChessBordRoom(gameRoom.getUserBuffer1());
					break;
				}else if (msgType.equals("CloseLingoutGameRoom")) {
					Iterator<UsersBuffer> iterator = OnlineManage.onlineUsers.iterator();
					while (iterator.hasNext()) {
						UsersBuffer usbf = iterator.next();
						if(usbf == gameRoom.getUserBuffer1()) {
							iterator.remove();
						}
						
					}
					break;
				}else if (msgType.equals("RoomMessage")) {
					sendMsgToPlayer(gameRoom.getUserBuffer2(), "RoomMessage", msgData);	
				}
				
			} catch (IOException e) {	
				IOEX();
				break;
			} 
		}
		System.err.println("服务端一条循环线程终止");
	}

	private void SendChessBoardlist(BufferedWriter bWriter) throws IOException {
		System.out.println("获取棋盘列表如下：");
		
		JSONObject Rooms = new JSONObject(true);
		JSONArray roomArray = new JSONArray();
		OnlineManage.Rooms.forEach((room)->{
			
						String nickNamePlayer1 = room.getUserBuffer1().getUser().getNickName();
						String userNamePlayer1 = room.getUserBuffer1().getUser().getUserName();
						String nickNamePlayer2;
						String userNamePlayer2;
						JSONArray roomJSON = new JSONArray();
						JSONObject user1JSON = new JSONObject();
						
						user1JSON.put("userName",userNamePlayer1);
						user1JSON.put("nickName",nickNamePlayer1);
						
						roomJSON.add(user1JSON);
						if(room.getUserBuffer2() != null) {
							
							JSONObject user2JSON = new JSONObject();
							nickNamePlayer2 = room.getUserBuffer2().getUser().getNickName();
							userNamePlayer2 = room.getUserBuffer2().getUser().getUserName();
							
							user2JSON.put("userName",userNamePlayer2);
							user2JSON.put("nickName",nickNamePlayer2);
							
							roomJSON.add(user2JSON);
						}
						
						roomArray.add(roomJSON);
						
						
		}); 
		
		Rooms.put("rooms", roomArray);
		System.out.println(Rooms);
		JSONObject sendJSON =  GameRoomUtil.getSendJSON("OnlineGameRooms", Rooms.toJSONString());
		bWriter.write(sendJSON.toJSONString()+"\r\n");
		bWriter.flush();	
	}
	public  void LingoutChessBordRoom(UsersBuffer usersBuffer){

		
		Iterator<GameRoom> iterator = OnlineManage.Rooms.iterator();
		while (iterator.hasNext()) {
			GameRoom room = iterator.next();
			//如果房间是自己创建的。
			if(room.getUserBuffer1() == gameRoom.getUserBuffer1()) {
				
				//如果房间还有人，那么让他当房主。
				if(room.getUserBuffer2() != null) {
					room.setUserBuffer1(room.getUserBuffer2());
					sendMsgToPlayer(room.getUserBuffer2(), "LeaveRoom",null);
					room.setUserBuffer2(null);
					
				}else {
					//没有人直接删除该房间。
					iterator.remove();
				}
			//如果自己是加入者	那么直接清空当前房间的第二个用户流
			}else if(room.getUserBuffer2() == gameRoom.getUserBuffer1()) {
				//给房主发消息
				sendMsgToPlayer(room.getUserBuffer1(), "LeaveRoom",null);
				room.setUserBuffer2(null);
			}
			
		}
		gameRoom.setUserBuffer2(null);
		// 发送玩家下线。
		sendToAllPlayersRoomList();
	
		
	
	}
	private synchronized void IOEX() {
		if(socket.isConnected()) {
			System.out.println("当前用户下线了。"+gameRoom.getUserBuffer1().getUser().getNickName());
			if(gameRoom.getUserBuffer2() != null) {
				sendMsgToPlayer(gameRoom.getUserBuffer2(), "BreakGame", null);
				
			}
			LingoutChessBordRoom(gameRoom.getUserBuffer1());
			
			OnlineManage.onlineUsers.remove(gameRoom.getUserBuffer1());
			
		}
		
	}
	private void addRoom(String username) {
	
		OnlineManage.Rooms.forEach((room)->{
			if(room.getUserBuffer1().getUser().getUserName().equals(username)) {
				if(room.getUserBuffer2() != null) {
					sendMsgToPlayer(gameRoom.getUserBuffer1(),"RoomFullOrRoomDistroy","1");
				}else {
					sendMsgToPlayer(gameRoom.getUserBuffer1(),"RoomFullOrRoomDistroy","0");
				}
				// 设置用户独有属性
				gameRoom.setUserBuffer2(room.getUserBuffer1());
				// 
				room.setUserBuffer2(gameRoom.getUserBuffer1());
			}
			
		});
		for(UsersBuffer userbuff:OnlineManage.onlineUsers) {
			if(userbuff.getUser().getUserName() .equals(username)) {
				JSONObject createRoomUser = (JSONObject) JSONObject.toJSON(userbuff.getUser());
				//发送创建玩家的信息。
				sendMsgToPlayer(gameRoom.getUserBuffer1(),"CreateRoomUserInfo",createRoomUser.toString());
				
				JSONObject addRoonUserStr = (JSONObject) JSONObject.toJSON(gameRoom.getUserBuffer1().getUser());
				sendMsgToPlayer(gameRoom.getUserBuffer2(),"AddRoomUserInfo",addRoonUserStr.toJSONString());
				break;
			}
		}
		
		
		sendToAllPlayersRoomList();
	
		
		
	}

	private void sendMsgToPlayer(UsersBuffer usersBuffer, String  msgType, String msg) {
		
		JSONObject sendJSON = GameRoomUtil.getSendJSON(msgType, msg);
		try {
			usersBuffer.getWriterPlayer().write(sendJSON.toJSONString()+"\r\n");
			usersBuffer.getWriterPlayer().flush();
		} catch (Exception e) {
			System.err.println("发送消息异常");
			e.printStackTrace();
		}
	}


	
	public void sendToAllPlayersRoomList() {
		OnlineManage.onlineUsers.forEach((userBuffer)->{
			try {
				SendChessBoardlist(userBuffer.getWriterPlayer());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
	}
}
