package com.raven.main;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JOptionPane;

import client.GameClient;
import util.GameRoomUtil;

public class MouseThing extends MouseAdapter{
	RoomPlane roomPlane;
	String lastMsg = null;
	
	public MouseThing(){
		
	}
	public MouseThing(RoomPlane roomPlane){
		this.roomPlane = roomPlane;
	}
	public void mouseClicked(MouseEvent e) {
		//为什么点击一次会出现好多个点击一次？？
				//或许跟新增了多个监听器有关系！！！
		//System.out.println("点击一次");
		//如果击中为假  防止点击一次发送多次 
				//已经验证是 所以注释掉了 删除多余的监听器
	//	if(!roomPlane.hide) {
			//x -30  Y-40是矩形的左上角的点
			if(roomPlane.p.getX()>=100-30&&roomPlane.p.getX()<=100-30+roomPlane.rectwidth&&roomPlane.p.getY()>=roomPlane.lasty-40&&roomPlane.p.getY()<=roomPlane.lasty-40+roomPlane.rectheight) {
				GameRoomUtil.playChessMovemusic("source/mousedown.mp3");
				//给服务端发送创建房间消息
				GameRoomUtil.SendToServerMsg(roomPlane.room,"MSGTYPE:CreateGameRoom#"+BeginWindow.username+"%"+BeginWindow.nickName+"\r\n");
				
				//然后设置当前窗口不可见
				roomPlane.room.setVisible(false);
				//棋盘窗口
				new ChessBoard(roomPlane.room,"CreateRoom",BeginWindow.nickName);
		
			
			}else if(roomPlane.p.getX()>=500-30&&roomPlane.p.getX()<=500-30+roomPlane.rectwidth&&roomPlane.p.getY()>=roomPlane.lasty-40&&roomPlane.p.getY()<=roomPlane.lasty-40+roomPlane.rectheight) {
				
				GameRoomUtil.playChessMovemusic("source/mousedown.mp3");
				//获取在线游戏房间列表	
				GameRoomUtil.SendToServerMsg(roomPlane.room,"MSGTYPE:GetOnlineGame\r\n");
				//GameRoomUtil.ResultMsg();
				JOptionPane.showMessageDialog(roomPlane, "刷新成功！");
				//System.out.println("获取棋盘列表:\r\n"+GameClient.MSG);
				//每次刷新必须清空这个hasplayer
				RoomPlane.hasplayer.clear();
				//删除重复的监听器 只留下一个最终的
				//只有当房间更新的时候再添加新的监听器
				if(lastMsg!=null&&!lastMsg.equals(GameClient.MSG)) {
					if(RoomPlane.mous!=null) {
						roomPlane.removeMouseListener(RoomPlane.mous);
					}
					RoomPlane.mous = new MouseThing(roomPlane);
					roomPlane.addMouseListener(RoomPlane.mous);
				}
				
				lastMsg = GameClient.MSG;
			//上一页	
			}else if (roomPlane.p.getX()>=100-30&&roomPlane.p.getX()<=100-30+roomPlane.rectwidth&&roomPlane.p.getY()>=700-40&&roomPlane.p.getY()<=700-40+roomPlane.rectheight) {
				GameRoomUtil.playChessMovemusic("source/mousedown.mp3");
				if(roomPlane.currpage==roomPlane.indexpage) {
					JOptionPane.showMessageDialog(roomPlane, "当前已经是第一页啦");
				}else {
					roomPlane.currpage -= 1;
				}
				
			//下一页		
			}else if (roomPlane.p.getX()>=500-30&&roomPlane.p.getX()<=500-30+roomPlane.rectwidth&&roomPlane.p.getY()>=700-40&&roomPlane.p.getY()<=700-40+roomPlane.rectheight) {
				GameRoomUtil.playChessMovemusic("source/mousedown.mp3");
				if(roomPlane.currpage==roomPlane.lastpage-1) {
					JOptionPane.showMessageDialog(roomPlane, "当前已经是最后一页啦");
				}else {
					roomPlane.currpage += 1;
				}
				//加入对战
			}else {
				//Y/80 说明一共有这么多房间
				for(int i = 0;i<roomPlane.Y/80;i++) {
					if (roomPlane.p.getX()>=400-30&&roomPlane.p.getX()<=400-30+roomPlane.rectwidth&&roomPlane.p.getY()>=80*(i+1)+40-40&&roomPlane.p.getY()<=80*(i+1)+40-40+roomPlane.rectheight) {
						//System.out.println("要加入游戏了");
					
						if(i<RoomPlane.hasplayer.size())
						if(RoomPlane.hasplayer.get(i).equals("0")) {
							GameRoomUtil.playChessMovemusic("source/mousedown.mp3");
						
																	//namemap存放的是当前也的所有姓名
							GameRoomUtil.SendToServerMsg(roomPlane.room,"MSGTYPE:AddGamePlayerName#"+roomPlane.nameMap.get(""+i)+","+BeginWindow.winlv+"%"+BeginWindow.gamebout+"\r\n");
							//GameRoomUtil.ResultMsg();
							if("yes".equals(GameClient.MSG)) {
								//注意 如果该房间已经加入人了，那么就要在读一次，因为服务器会自动给客户端发送最新棋盘
								//GameRoomUtil.ResultMsg();
									/*
									 * 可以不用设置 因为repaint会重新赋值
									 * 我错了 这里需要设置一下 为什么呢 ？虽然repaint会重新赋值 但是前提是针对有房间的情况下
									 * 	就是说房间有人已经加入了房间  再次加入
									 *	加入就提示一次加入失败。然后再次点击就不会进来这个逻辑
									 *	然而 设置下重新赋值为1又是因为什么原因呢 是因为 如果房间为空了 那么paintjiu不会给hasplayer赋值
									 *	会导致没有房间也会提示房间状态已改变
									 */		
									RoomPlane.hasplayer.set(i, "1");
									JOptionPane.showMessageDialog(roomPlane, "房间状态已改变，加入失败,请刷新后重试！");
						
								
							}else {
								System.out.println("加入了"+roomPlane.nameMap.get(""+i)+"的房间");
								roomPlane.room.setVisible(false);
								new ChessBoard(roomPlane.room,"ADDRoom",BeginWindow.nickName);
								
							}
						}
							
							
					
					}
				
				}
			
			}
			roomPlane.repaint();
			//击中为真
			//roomPlane.hide = true;
			
	//	}
		
		
	}
	@Override
	public void mousePressed(MouseEvent e) {
		roomPlane.mousedown = true;
		//this.repaint();
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		// 松开则 击中为假
		//roomPlane.hide = false;
		roomPlane.mousedown = false;
		
	}
	@Override
	public void mouseEntered(MouseEvent e) {
		
		
	}
	@Override
	public void mouseExited(MouseEvent e) {
	
		
	}
}
