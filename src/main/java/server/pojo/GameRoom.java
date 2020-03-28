package server.pojo;


import server.pojo.UsersBuffer;

/**
 * @ClassName: GameRoom
 * @description: 游戏房间可以有两个玩家
 * @author:Raven
 * @Date: 2019年10月19日 下午12:56:22
 */

public class GameRoom {
	// 玩家1
	private UsersBuffer userBuffer1;
	// 玩家2
	private UsersBuffer userBuffer2;

	
	public GameRoom(UsersBuffer userBuffer1) {
		
		this.userBuffer1 = userBuffer1;
		
	}
	public UsersBuffer getUserBuffer1() {
		return userBuffer1;
	}
	public void setUserBuffer1(UsersBuffer userBuffer1) {
		this.userBuffer1 = userBuffer1;
	}
	public UsersBuffer getUserBuffer2() {
		return userBuffer2;
	}
	public void setUserBuffer2(UsersBuffer userBuffer2) {
		this.userBuffer2 = userBuffer2;
	}

	
}
 