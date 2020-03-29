package server.pojo;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class OnlineManage{
	

	public OnlineManage() {
	
	}
	public static List<UsersBuffer> onlineUsers = new ArrayList<UsersBuffer>();
	public static List<GameRoom>Rooms = new ArrayList<GameRoom>();

	public static UsersBuffer getUserBufferByUserName(String username){
		for (int i = 0; i < OnlineManage.onlineUsers.size(); i++) {
			if(OnlineManage.onlineUsers.get(i).getUser().getUserName().equals(username)){
				return OnlineManage.onlineUsers.get(i);
			}
		}
		return null;
	}
	public static int getRoomsByUserName(String username){
		AtomicInteger i  = new AtomicInteger();
		Rooms.forEach(room->{
			if(room.getUserBuffer1().getUser().getUserName().equals(username)){
				i.set(1);
				if(room.getUserBuffer2() != null){
					i.set(2);
				}
			}else if(room.getUserBuffer2()!=null && room.getUserBuffer2().getUser().getUserName().equals(username)){
				i.set(1);
				if(room.getUserBuffer1() != null){
					i.set(2);
				}
			}
		});
		return i.get();
	}

	
	
}
