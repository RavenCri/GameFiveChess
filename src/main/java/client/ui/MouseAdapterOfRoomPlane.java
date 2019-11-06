package client.ui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JOptionPane;

import com.alibaba.fastjson.JSONObject;

import bean.User;
import client.start.GameClient;
import util.GameRoomUtil;

public class MouseAdapterOfRoomPlane extends MouseAdapter{
	RoomPlane roomPlane;
	String lastMsg = null;
	private GameRoomUtil gameRoomUtil = new GameRoomUtil();
	public MouseAdapterOfRoomPlane(){
		
	}
	public MouseAdapterOfRoomPlane(RoomPlane roomPlane){
		this.roomPlane = roomPlane;
	}
	public void mouseClicked(MouseEvent e) {

			//x -30  Y-40是矩形的左上角的点
			if(roomPlane.p.getX()>=100-30&&roomPlane.p.getX()<=100-30+roomPlane.rectwidth&&roomPlane.p.getY()>=roomPlane.lasty-40&&roomPlane.p.getY()<=roomPlane.lasty-40+roomPlane.rectheight) {
				gameRoomUtil.palyothermusic("mousedown.mp3");
				//给服务端发送创建房间消息
				GameRoomUtil.SendMsgToServer(roomPlane.room,"CreateGameRoom",null);
				
		
			
			}else if(roomPlane.p.getX()>=500-30&&roomPlane.p.getX()<=500-30+roomPlane.rectwidth&&roomPlane.p.getY()>=roomPlane.lasty-40&&roomPlane.p.getY()<=roomPlane.lasty-40+roomPlane.rectheight) {
				
				gameRoomUtil.palyothermusic("mousedown.mp3");
				//获取在线游戏房间列表	
				GameRoomUtil.SendMsgToServer(roomPlane.room,"GetOnlineGame",null);
				
				JOptionPane.showMessageDialog(roomPlane, "刷新成功！");
				//System.out.println("获取棋盘列表:\r\n"+GameClient.MSG);
				//每次刷新必须清空这个hasplayer
				RoomPlane.hasplayer.clear();
				//删除重复的监听器 只留下一个最终的
				//只有当房间更新的时候再添加新的监听器
			
				if(RoomPlane.mous!=null) {
					roomPlane.removeMouseListener(RoomPlane.mous);
				}
				RoomPlane.mous = new MouseAdapterOfRoomPlane(roomPlane);
				roomPlane.addMouseListener(RoomPlane.mous);
				
				
			
			//上一页	
			}else if (roomPlane.p.getX()>=100-30&&roomPlane.p.getX()<=100-30+roomPlane.rectwidth&&roomPlane.p.getY()>=700-40&&roomPlane.p.getY()<=700-40+roomPlane.rectheight) {
				gameRoomUtil.palyothermusic("mousedown.mp3");
				if(roomPlane.currpage==roomPlane.indexpage) {
					JOptionPane.showMessageDialog(roomPlane, "当前已经是第一页啦");
				}else {
					roomPlane.currpage -= 1;
				}
				
			//下一页		
			}else if (roomPlane.p.getX()>=500-30&&roomPlane.p.getX()<=500-30+roomPlane.rectwidth&&roomPlane.p.getY()>=700-40&&roomPlane.p.getY()<=700-40+roomPlane.rectheight) {
				gameRoomUtil.palyothermusic("mousedown.mp3");
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
							gameRoomUtil.palyothermusic("mousedown.mp3");
						
							//namemap存放的是当前页房主的所有唯一账号
							GameRoomUtil.SendMsgToServer(roomPlane.room,"AddGameHomeOwner",roomPlane.nameMap.get(""+i));
						}

					}
				}
			}
			roomPlane.repaint();
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
