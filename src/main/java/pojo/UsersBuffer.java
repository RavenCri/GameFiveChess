package pojo;

import java.io.BufferedReader;
import java.io.BufferedWriter;

import bean.User;

/** 
 * @ClassName: UserBuffer
 * @description: 
 * @author:Raven
 * @Date: 2019年10月19日 下午12:38:28
 */

public class UsersBuffer {
	private User user;
	private BufferedReader readerPlayer;

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
 