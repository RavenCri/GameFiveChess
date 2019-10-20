package bean;

public class User {
	private String userName;
	private String passWord;
	private String nickName;
	private double winingProbability;
	private int gameBout;
	private int winBout;
	public User() {
		
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassWord() {
		return passWord;
	}
	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public double getWiningProbability() {
		return winingProbability;
	}
	public void setWiningProbability(double winingProbability) {
		this.winingProbability = winingProbability;
	}
	public int getGameBout() {
		return gameBout;
	}
	public void setGameBout(int gameBout) {
		this.gameBout = gameBout;
	}
	
	public int getWinBout() {
		return winBout;
	}
	public void setWinBout(int winBout) {
		this.winBout = winBout;
	}
	public User(String userName, String passWord, String nickName) {
		super();
		this.userName = userName;
		this.passWord = passWord;
		this.nickName = nickName;
	}
	public User(String userName, String passWord, String nickName, double winingProbability, int gameBout) {
		super();
		this.userName = userName;
		this.passWord = passWord;
		this.nickName = nickName;
		this.winingProbability = winingProbability;
		this.gameBout = gameBout;
	}
	public void setWinBoutAddOne() {
		this.winBout++;
	}
	public void setGameBoutAddOne() {
		this.gameBout++;
	}
}
