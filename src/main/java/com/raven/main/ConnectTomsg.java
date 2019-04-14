package com.raven.main;

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

import server.Server;

public class ConnectTomsg implements Runnable{
	Socket socket;
	String myname;
	String othername;
	BufferedWriter otherBW;
	Socket otherSocket;
	BufferedReader reader = null;
	BufferedWriter writer = null;
	public static List<BufferedWriter> playersBuff = new ArrayList<BufferedWriter>();
	public ConnectTomsg(){
	}
	public  ConnectTomsg(Socket socket) {
		this.socket = socket;
	}		
	@Override
	public void run() {
		/**
		 * 消息体 组成 
		 * 	MSGTYPE:消息类型#消息内容。
		 *  	为了达到动态效果  创建房间 加入房间   退出房间 都需要给所有玩家发送房间状态
		 */
		
		try {
			
			reader = new BufferedReader(new InputStreamReader(socket
					.getInputStream(),"UTF-8"));
			writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(),"UTF-8"));
			playersBuff.add(writer);
		} catch (IOException e1) {
			
			e1.printStackTrace();
		}
		
		while(true) {
			try {
				
				String msg = reader.readLine();
				System.out.println(msg);
				
				//如果收到消息是null，说明对方关闭了io流 那么这个线程退出
				//如果消息类型头不是规定的 那么删掉这个socket连接 
				if(msg==null||!msg.split(":")[0].equals("MSGTYPE")) {
					GameRoom.players.remove(socket);
					IOEX();
					//删除自己
					playersBuff.remove(writer);
					break;
				}
				String MsgHeadType[] = msg.split(":");
				//消息类型
				String MsgType = MsgHeadType[1].split("#")[0];
				//消息数据
				StringBuilder MsgData = new StringBuilder();
				//等于2说明有消息数据
				if(msg.split("#").length==2) {
					MsgData.append(msg.split("#")[1]);
				}
				if(!GameRoom.players.containsKey(socket)) {
					
						
						//新加入用户放入在线列表
						if(MsgType.equals("username")) {
							
							writer.write("MSGTYPE:namerepeat#0\r\n");
							writer.flush();
							GameRoom.players.put(socket, MsgData.toString());
							myname = MsgData.toString();
							System.out.println(MsgData.toString()+",登录了服务器");
							
							
						}
						
					
				}else {
						
						
						//创建一个房间
						if(MsgType.equals("CreateGameRoom")) {
							Server.gameRoom.getChessBoards().put(MsgData.toString(), "");
							playersBuff.forEach((buff)->{
								try {
									SendChessBoardlist(buff);
								} catch (IOException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
							});
					

						}
						// 发送棋盘list
						else if(MsgType.equals("GetOnlineGame")) {
							SendChessBoardlist(writer);
						//加入者会给服务端发送他要加入房间创建者的姓名
						}else if(MsgType.equals("AddGamePlayerName")) {
							GameRoom.players.forEach((k,v)->{
								String name = MsgData.toString().split(",")[0];
								if(v.equals(name)) {
									//拿到创建房间玩家姓名
									othername = v;
									if(Server.gameRoom.getChessBoards().get(v)!=""||!Server.gameRoom.getChessBoards().containsKey(v)) {
										try {//加入的时候由于刷新导致房间状态改变 加入失败
											writer.write("MSGTYPE:RoomFullOrRoomDistroy#yes\r\n");
											writer.flush();
											//发送棋盘列表
											SendChessBoardlist(writer);
											return;
										} catch (IOException e) {
											
											e.printStackTrace();
										}
										
									}else {
										try {
											writer.write("MSGTYPE:RoomFullOrRoomDistroy#no\r\n");
											writer.flush();
										} catch (IOException e) {
											
											e.printStackTrace();
										}
										
									}
									//更新棋盘玩家与玩家
									Server.gameRoom.getChessBoards().put(othername, myname);
									
									//拿到创建房间玩家的BufferedWriter					
									try {
										otherSocket = k;
										otherBW = new BufferedWriter(new OutputStreamWriter(k.getOutputStream(),"UTF-8"));
										// 此条信息重点 发送了 加入者的名字和创建游戏者的名字。
										otherBW.write("MSGTYPE:ADDYourRoom#"+othername+","+myname+","+MsgData.toString().split(",")[1]+",来跟你一起玩游戏啦！\r\n");
										
										otherBW.flush();
										otherBW.write("MSGTYPE:youareblackoriswhite\r\n");
										otherBW.flush();
									} catch (IOException e1) {
										
										e1.printStackTrace();
									}	
								
							
								}
							});
							playersBuff.forEach((buff)->{
								try {
									SendChessBoardlist(buff);
								} catch (IOException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
							});
							
							//如果正在游戏中
						}else if (MsgType.equals("ChessGameing")) {
							String gamemsg  = msg.split("#")[1];
							//悔棋
							if(gamemsg.split(",").length==1) {//消息分割为1  那么就通知另一方 和棋 认输  悔棋 赢了游戏
								if (gamemsg.equals("huiqi")) {
									
									otherBW.write("MSGTYPE:ChessGameing#huiqi\r\n");
									otherBW.flush();
								}else if (gamemsg.equals("heqi")) {
									
									otherBW.write("MSGTYPE:ChessGameing#heqi\r\n");
									otherBW.flush();
								}else if (gamemsg.equals("renshu")){	
									
									otherBW.write("MSGTYPE:ChessGameing#YouWIN\r\n");
									otherBW.flush();
								}else if (gamemsg.equals("GameWIN")) {
									otherBW.write("MSGTYPE:ChessGameing#HeWON\r\n");
									otherBW.flush();
								}//如果是消息分割是 2和3的话，那就说明那个是发送 和棋，悔棋 对方响应的状态  （认输不需要对方同意哦）和下的棋子的坐标
							}else if (gamemsg.split(",").length==2||gamemsg.split(",").length==3) {
								otherBW.write("MSGTYPE:ChessGameing#"+gamemsg+"\r\n");
								otherBW.flush();
							}
							//创建者返回自己的名字回来，更新加入者线程的othereBW 对象 让加入者方便与其通信。
						}else if(MsgType.equals("NoticeGameState")) {
							GameRoom.players.forEach((k,v)->{
								String[] split = MsgData.toString().split(",")[0].split("%");
								String creatRoomPlays = split[0]+"%"+split[1];
								if(v.equals(creatRoomPlays)) {
									try {
										otherBW = new BufferedWriter(new OutputStreamWriter(k.getOutputStream(),"UTF-8"));
										othername = v;
										otherSocket = k;
										otherBW.write("MSGTYPE:ADDSucc#"+myname+"&"+creatRoomPlays+","+split[2]+"%"+split[3]+",我很开心你能加进来\r\n");
										otherBW.flush();
									} catch (IOException e) {
										
										e.printStackTrace();
									}
								}
							});
							
							//拿到自己的颜色发送给加入者
						}else if(MsgType.equals("MyColor")) {
							otherBW.write("MSGTYPE:SendHisColor#"+MsgData.toString()+"\r\n");
							otherBW.flush();
							//退出房间
						}else if(MsgType.equals("LingoutGameNowPerson")) {
							
							
							//如果当前局有人的话就通知对方自己下线了！！，没有的话就不通知了，直接删除服务器对象自己创建的房间
							if(MsgData.toString().equals("1")) {
								//这个方法比DeleteLingoutPlayer方法多个发送自己下线了
								IOEX();
							}else {
								DeleteLingoutPlayer();
							}
							playersBuff.forEach((buff)->{
								try {
									SendChessBoardlist(buff);
								} catch (IOException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
							});
							
							//游戏开始
						}else if (MsgType.equals("GAMEBEGIN")) {
							otherBW.write("MSGTYPE:GAMEBEGIN\r\n");
							otherBW.flush();
							//游戏准备
						}else if (MsgType.equals("GAMEREADY")) {
							otherBW.write(msg+"\r\n");
							otherBW.flush();
							//游戏聊天
						}else if (MsgType.equals("GAMECHAT")) {
							try {
								if(otherBW!=null) {
									otherBW.write(msg+"\r\n");
									otherBW.flush();
									
								}
							} catch (IOException e2) {
								otherBW.close();
								otherBW = null;
								otherSocket = null;
								System.err.println("获取到otherBW已经失效，现在关闭它");
							}
							
							//游戏中途退出游戏
						}else if(MsgType.equals("BreakGame")) {
							otherBW.write(msg+"\r\n");
							otherBW.flush();
							
							
						}
					
				}
			
				
			} catch (IOException e) {
				IOEX();
				try {
					if(otherBW!=null) {
						otherBW.close();
						otherBW = null;
					}
					if(otherSocket!=null) {
						otherSocket.close();
						otherSocket = null;
					}
					
					
				} catch (IOException e1) {
					
					e1.printStackTrace();
				}
				
				//删除自己
				playersBuff.remove(writer);
			
				break;
			} 
		}
		System.err.println("服务端一条循环线程终止");
	}
	private void SendChessBoardlist(BufferedWriter bWriter) throws IOException {
		System.out.println("获取棋盘列表如下：");
		String name ="";
		for(Map.Entry<String, String> entry:Server.gameRoom.getChessBoards().entrySet()) {
			// 昵称%账号,昵称%账号&昵称%账号,昵称%账号
			String play1 = entry.getKey();
			String play2 = entry.getValue();
			name +=(play1+","+play2+"&");
		}
		System.out.println("Room:"+name);
		bWriter.write("MSGTYPE:OnlineGameRooms#"+name+"\r\n");
		bWriter.flush();
	}
	public  void DeleteLingoutPlayer(){
		Iterator<String> iterator = Server.gameRoom.chessBoards.keySet().iterator();
		while (iterator.hasNext()) {
			String k = (String) iterator.next();
			String v = Server.gameRoom.chessBoards.get(k);
			//说明自己是创建者
			if(k.equals(myname)) {
				//一定是先删除再添加 ，不然也会抛异常的！
				iterator.remove();
				
				if(!v.equals("")) {
					
					Server.gameRoom.chessBoards.put(v, "");
					
				}
				//删除了元素新增元素后一定要break;
				//不加break 会Exception in thread "main" java.util.ConcurrentModificationException
				//如果是 ConcurrentHashMap 类型 那么就不需要break
				//	 原因：添加元素会改变map的迭代器里面的属性值。所以不能获取下一个元素了，map已经改变了
						/*
						 *  if (modCount != expectedModCount)
           						throw new ConcurrentModificationException();
						 * */
				break;
			
				
			}
			//说明自己是加入者
			if(v!=null&&v.equals(myname)) {
				Server.gameRoom.chessBoards.put(k, "");
				
			}
		}
	
		System.out.println("新的棋盘列表");
		Server.gameRoom.chessBoards.forEach((k,v)->{
			System.out.println(k+","+v);
		});
	}
	private synchronized void IOEX() {
		if(socket.isConnected()) {
			System.out.println("当前用户下线了。"+myname);
			
			DeleteLingoutPlayer();
			
		
			//Server.players.remove(socket);
			try {
				if(otherSocket!=null&&otherSocket.isConnected()==true&&otherBW!=null) {
					otherBW.write("MSGTYPE:GamePlayerLingout\r\n");
					otherBW.flush();
					//不能关otherBW缓冲，那么置NULL
					otherBW =null;
					otherSocket = null;
					
					
					
				}
				
			} catch (IOException e1) {
				
				e1.printStackTrace();
			}
		
			
		}
		
	}
	private void SendToOther(String myname, String string) {
		
		
	}



}
