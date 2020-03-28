package server.pojo;

import common.pojo.User;

import java.io.BufferedReader;
import java.io.BufferedWriter;

/** 
 * @ClassName: UserBuffer
 * @description: 将用户信息 用户相关流封装成一个具体对象
 * @author:Raven
 * @Date: 2019年10月19日 下午12:38:28
 */

public class UsersBuffer {
	// 用户相关信息
	private User user;
	// 用户读取流
	private BufferedReader readerPlayer;
	// 用户写入流
	private BufferedWriter writerPlayer;


	public UsersBuffer(User user, BufferedReader readerPlayer, BufferedWriter writerPlayer) {
		super();
		this.user = user;
		this.readerPlayer = readerPlayer;
		this.writerPlayer = writerPlayer;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public BufferedReader getReaderPlayer() {
		return readerPlayer;
	}

	public void setReaderPlayer(BufferedReader readerPlayer) {
		this.readerPlayer = readerPlayer;
	}

	public BufferedWriter getWriterPlayer() {
		return writerPlayer;
	}

	public void setWriterPlayer(BufferedWriter writerPlayer) {
		this.writerPlayer = writerPlayer;
	}

	
	
}
 