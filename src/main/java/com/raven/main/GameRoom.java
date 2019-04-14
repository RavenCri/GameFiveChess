package com.raven.main;


import java.io.Serializable;
import java.net.Socket;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

public class GameRoom implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public GameRoom() {
	
	}
	Map<String,String> chessBoards = new LinkedHashMap <String,String>();
	public static Map<Socket,String> players = new ConcurrentHashMap<Socket,String>();

	public  Map<String, String> getChessBoards() {
		return chessBoards;
	}
	public void setChessBoards(Map<String, String> chessBoards) {
		this.chessBoards = chessBoards;
	}
	public static Map<Socket, String> getPlayers() {
		return players;
	}
	public static void setPlayers(Map<Socket, String> players) {
		GameRoom.players = players;
	}

	
	
}
