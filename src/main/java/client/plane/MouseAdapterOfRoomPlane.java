package client.plane;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JOptionPane;

import client.window.BeginWindow;
import client.window.ChessBoard;
import client.window.LoginFream;
import client.window.Room;
import server.pojo.GameRoom;
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
				if(ChessBoard.gamepanel.kaishi){
					JOptionPane.showMessageDialog(roomPlane, "游戏已经开始了，无法创建新的房间");
					return;
				}else if(Room.chessBoard != null && Room.chessBoard.isVisible()){
					JOptionPane.showMessageDialog(roomPlane, "您已经加入一个游戏房间了，如果你要创建新房间请先退出房间。");
					return;
				}
				gameRoomUtil.palyothermusic("sound/mousedown.mp3");
				//给服务端发送创建房间消息
				GameRoomUtil.SendMsgToServer(roomPlane.room,"CreateGameRoom",null);
				
		
			
			}else if(roomPlane.p.getX()>=500-30&&roomPlane.p.getX()<=500-30+roomPlane.rectwidth&&roomPlane.p.getY()>=roomPlane.lasty-40&&roomPlane.p.getY()<=roomPlane.lasty-40+roomPlane.rectheight) {
				
				gameRoomUtil.palyothermusic("sound/mousedown.mp3");
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
				gameRoomUtil.palyothermusic("sound/mousedown.mp3");
				if(roomPlane.currpage==roomPlane.indexpage) {
					JOptionPane.showMessageDialog(roomPlane, "当前已经是第一页啦");
				}else {
					roomPlane.currpage -= 1;
				}
				
			//下一页		
			}else if (roomPlane.p.getX()>=500-30&&roomPlane.p.getX()<=500-30+roomPlane.rectwidth&&roomPlane.p.getY()>=700-40&&roomPlane.p.getY()<=700-40+roomPlane.rectheight) {
				gameRoomUtil.palyothermusic("sound/mousedown.mp3");
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
							gameRoomUtil.palyothermusic("sound/mousedown.mp3");
							if(roomPlane.nameMap.get(""+i).equals(roomPlane.room.priwid.userPlayer.getUserName())){
								JOptionPane.showMessageDialog(roomPlane, "你不能自己加入自己~");
								return;
							}
							if(ChessBoard.gamepanel.kaishi){
								JOptionPane.showMessageDialog(roomPlane, "当前游戏已经开始了，暂时无法加入其他对局哦");
								return;
							}
							if(roomPlane.room.chessBoard.gamepanel.gameplayer2!=null){
								GameRoomUtil.SendMsgToServer(LoginFream.room.chessBoard,"LeaveRoom",null);
								LoginFream.room.chessBoard.dispose();
								GameRoomUtil.stopBackgroundMusic();//停止播放音乐
								ChessBoard.jt.setText("");

								ChessBoard.gamepanel.zhunbei = false;
								ChessBoard.gamepanel.kaishi = false;
								ChessBoard.gamepanel.chessPoint.clear();
							}else if(roomPlane.room.chessBoard !=null && roomPlane.room.chessBoard.isVisible()){
								JOptionPane.showMessageDialog(roomPlane, "请先退出当前房间再加入房间哦");
								return;
							}
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
