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

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import common.pojo.User;
import server.pojo.GameRoom;
import server.pojo.UsersBuffer;
import server.pojo.OnlineManage;
import server.Server;
import server.service.UserServer;
import util.GameRoomUtil;
/***
* @Description: 用于服务端后台消息处理
* @Author: raven
* @Date: 2020/3/28
*/

@Component
@Scope("prototype")
public class HandleMsg implements Runnable{
	private Socket socket;
	@Autowired
	private UserServer userServer;
	private GameRoom gameRoom = new GameRoom(null);
	public HandleMsg(){
	}
	public  HandleMsg(Socket socket) {
		this.socket = socket;
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
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
						LingoutChessBordRoom();
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
							JSONObject msg  = new JSONObject();
							msg.put("NotifyType","login");
							msg.put("who",gameRoom.getUserBuffer1().getUser().getNickName());
							sendMsgToAllPlayers("systemNotify", msg.toJSONString());

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
								
					LingoutChessBordRoom();
					
					//游戏开始
				}else if (msgType.equals("GameBegin")) {
					sendMsgToPlayer(gameRoom.getUserBuffer2(), "GameBegin",null);
					//游戏准备
				}else if (msgType.equals("GameReady")) {
					sendMsgToPlayer(gameRoom.getUserBuffer2(), "GameReady",msgData);
					//游戏聊天
				}else if (msgType.equals("GameChat")) {
					sendMsgToPlayer(gameRoom.getUserBuffer2(), "GameChat",msgData);
				
				}else if ( msgType.equals("salaChat")) {
					sendMsgToAllPlayers("salaChat",msgData);
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
					LingoutChessBordRoom();
					break;
				}else if (msgType.equals("CloseLingoutGameRoom")) {
					Iterator<UsersBuffer> iterator = OnlineManage.onlineUsers.iterator();
					while (iterator.hasNext()) {
						UsersBuffer usbf = iterator.next();
						if(usbf == gameRoom.getUserBuffer1()) {
							iterator.remove();
						}
						
					}
					//通知大厅玩家 该玩家下线
					NotifyLogout();
					break;
				}else if (msgType.equals("ChessColor")) {
					sendMsgToPlayer(gameRoom.getUserBuffer2(), "ChessColor", msgData);
				}else if (msgType.equals("YouLose")) {
					sendMsgToPlayer(gameRoom.getUserBuffer2(), "YouLose", msgData);	
				}else if (msgType.equals("updateUserInfo")) {
					User user = Server.userServer.getUser(gameRoom.getUserBuffer1().getUser().getUserName());
					gameRoom.getUserBuffer1().setUser(user);
					if(gameRoom.getUserBuffer2() != null) {
						User user2 = Server.userServer.getUser(gameRoom.getUserBuffer2().getUser().getUserName());
						gameRoom.getUserBuffer2().setUser(user2);
					}
					
				}else if(msgType.equals("getAllUserInfo")) {
					sendAllUserInfo();
				}else if(msgType.equals("addFriend")){
					JSONObject data = JSON.parseObject(msgData);
					String fromUserName = data.getString("fromUserName");
					String toUserName = data.getString("toUserName");
					UsersBuffer uBuffer = OnlineManage.getUserBufferByUserName(toUserName);
					data.put("fromNickName",userServer.getUser(fromUserName).getNickName());
					sendMsgToPlayer(uBuffer,"hasUserAddYou",data.toJSONString());
				}else if(msgType.equals("addFriendCallBack")){
					JSONObject msgJSON = JSON.parseObject(msgData);
					String fromUserName = msgJSON.getString("fromUserName");
					String toUserName = msgJSON.getString("toUserName");
					System.out.println("添加回馈："+msgJSON);
					// 如果对方同意了
					if(msgJSON.getBoolean("status")){
						userServer.addFriend(fromUserName,toUserName);
						sendMsgToPlayer(OnlineManage.getUserBufferByUserName(fromUserName),"addFriendCallBack",msgJSON.getString("toNickName")+"已同意了你的好友添加请求。");
						List<User> toUserFriendList = userServer.getUserFriendByUserName(msgJSON.getString("toUserName"));
						List<User> fromUserFriendList = userServer.getUserFriendByUserName(msgJSON.getString("fromUserName"));

						sendMsgToPlayer(OnlineManage.getUserBufferByUserName(fromUserName),"friendUserList",JSON.toJSONString(fromUserFriendList));
						sendMsgToPlayer(OnlineManage.getUserBufferByUserName(toUserName),"friendUserList",JSON.toJSONString(toUserFriendList));

					}else{
						sendMsgToPlayer(OnlineManage.getUserBufferByUserName(fromUserName),"addFriendCallBack",msgJSON.getString("toNickName")+"拒绝了你的添加请求！");
					}
				}else if (msgType.equals("getSocialContactMsg")){

				// 邀请一起游戏
				}else if(msgType.equals("invitationGame")){
					JSONObject data = JSON.parseObject(msgData);
					String fromUserName = data.getString("fromUserName");
					String toUserName = data.getString("toUserName");
					data.put("fromNickName",userServer.getUser(fromUserName).getNickName());
					// 获取
					int num2 = OnlineManage.getRoomsByUserName(toUserName);

					if(num2 == 2){
						sendMsgToPlayer(gameRoom.getUserBuffer1(),"invitationGameCallBack",toUserName+"拒绝了您的游戏邀请！");
						return;
					}

					data.put("fromNickName",userServer.getUser(fromUserName).getNickName());
					//转发给邀请信息给被邀请的人
					sendMsgToPlayer(OnlineManage.getUserBufferByUserName(toUserName),"invitationGame",data.toJSONString());
				// 邀请回执状态
				}else if(msgType.equals("invitationGameCallBack")){
					JSONObject msgJSON = JSON.parseObject(msgData);
					String fromUserName = msgJSON.getString("fromUserName");
					System.out.println("邀请回馈："+msgJSON);

					// 通知 邀请者 邀请状态 如果同意一起游戏 邀请方会主动创建新房间
					sendMsgToPlayer(OnlineManage.getUserBufferByUserName(fromUserName),"invitationGameCallBack",msgData);
				// 创建好房间后 邀请 被邀请方进来
				}else if(msgType.equals("CreateRoomOK")){
					JSONObject msgJSON = JSON.parseObject(msgData);
					String toUserName = msgJSON.getString("toUserName");
					String fromUserName = msgJSON.getString("fromUserName");
					// 拿到对方的输出流 发给对方房间创建好了  让他加进来
					sendMsgToPlayer(OnlineManage.getUserBufferByUserName(toUserName),"CreateRoomOKAddRoom",fromUserName);

				// 被邀请方接收到已经创建好了的信息，然后通知客户端，让他主动去请求加入这个房间
				}else if(msgType.equals("CreateRoomOKAddRoom")){
					sendMsgToPlayer(gameRoom.getUserBuffer1(),"CreateRoomOKAddRoom",msgData);
				// 获取自己的好友列表
				}else if(msgType.equals("getFriendUserList")){
					List<User> friendList = userServer.getUserFriendByUserName(gameRoom.getUserBuffer1().getUser().getUserName());
					sendMsgToPlayer(gameRoom.getUserBuffer1(),"friendUserList",JSON.toJSONString(friendList));

				}
				
			} catch (IOException e) {	
				// 通知二号玩家 该玩家下线 ，让二号玩家更新房间状态
				IOEX();
				//通知大厅玩家 该玩家下线
				NotifyLogout();
				//更新关于他的房间状态
				LingoutChessBordRoom();
				break;
			} 
		}
		
		System.err.println("服务端一条循环线程终止");
	}
	/***
	* @Description: 发送所有在线用户信息
	* @Param: []
	* @return: void
	* @Author: raven
	* @Date: 2020/3/29
	*/
	private void sendAllUserInfo() {
		List<List> users = new ArrayList<>();
		List<JSONObject> user = new ArrayList<>();
		OnlineManage.onlineUsers.forEach(us->{
			JSONObject line = JSON.parseObject(JSONObject.toJSONString(us.getUser()));

			user.add(line);
			users.add(user);
		});
		OnlineManage.onlineUsers.forEach(u->{
			sendMsgToPlayer(u,"AllUserInfo", JSON.toJSONString(users));
		});

	}

	/***
	* @Description: 发送消息给所有玩家
	* @Param: [msgType, msgData]
	* @return: void
	* @Author: raven
	* @Date: 2020/3/28
	*/
	private void sendMsgToAllPlayers(String msgType, String msgData) {
		
		OnlineManage.onlineUsers.forEach((user)->{
			if(user != gameRoom.getUserBuffer1())
				sendMsgToPlayer(user,msgType,msgData);
		});
	}
	/***
	* @Description: 发送棋盘列表
	* @Param: [bWriter]
	* @return: void
	* @Author: raven
	* @Date: 2020/3/28
	*/
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
	/***
	* @Description: 离开游戏房间
	* @Param: []
	* @return: void
	* @Author: raven
	* @Date: 2020/3/28
	*/
	public  void LingoutChessBordRoom( ){
		Iterator<GameRoom> iterator = OnlineManage.Rooms.iterator();
		UsersBuffer curr = null;
		while (iterator.hasNext()) {
			
			GameRoom room = iterator.next();
			//如果房间是自己创建的。
			if(room.getUserBuffer1() == gameRoom.getUserBuffer1()) {
				
				//如果房间还有人，那么让他当房主。
				if(room.getUserBuffer2() != null) {
					//获取到 那个玩家的对象流
					curr = room.getUserBuffer2();
					//删除自己创建的
					iterator.remove();
					//发送给 那个玩家 房主退出房间了
					sendMsgToPlayer(room.getUserBuffer2(), "LeaveRoom",null);
					
					
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
		// 如果房间有人，那么需要创建新的房间 房主为 加入者
		if(curr != null)
			OnlineManage.Rooms.add(new GameRoom(curr));
		gameRoom.setUserBuffer2(null);
		
		sendToAllPlayersRoomList();
	
		
	
	}
	/***
	* @Description: 通知下线
	* @Param: []
	* @return: void
	* @Author: raven
	* @Date: 2020/3/28
	*/
	public void NotifyLogout() {
		OnlineManage.onlineUsers.remove(gameRoom.getUserBuffer1());
		JSONObject msg  = new JSONObject();
		msg.put("NotifyType","logout");
		msg.put("who",gameRoom.getUserBuffer1().getUser().getNickName());
		sendMsgToAllPlayers("systemNotify", msg.toJSONString());
		sendAllUserInfo();
	}
	private synchronized void IOEX() {
		if(socket.isConnected()) {
			if(gameRoom.getUserBuffer2() != null) {
				System.out.println("当前用户下线了。"+gameRoom.getUserBuffer1().getUser().getNickName());
				sendMsgToPlayer(gameRoom.getUserBuffer2(), "BreakGame", null);
				sendAllUserInfo();
			}
		}
		
	}
	/***
	* @Description: 创建房间
	* @Param: [username]
	* @return: void
	* @Author: raven
	* @Date: 2020/3/28
	*/
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
	/***
	* @Description: 用 用户流发送消息
	* @Param: [usersBuffer, msgType, msg]
	* @return: void
	* @Author: raven
	* @Date: 2020/3/28
	*/
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


	/***
	* @Description: 发送给所有用户在线棋盘列表
	* @Param: []
	* @return: void
	* @Author: raven
	* @Date: 2020/3/28
	*/
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
