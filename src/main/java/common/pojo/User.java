package common.pojo;
/**
* @Description: 用户的实体类
* @Author: raven
* @Date: 2019/12/28
*/
public class User {
	private int id;
	//用户名
	private String userName;
	//用户密码
	private String passWord;
	//用户昵称
	private String nickName;
	//胜率
	private double winingProbability;
	//游戏总局数
	private int gameBout;
	//赢得总局数
	private int winBout;
	private int integral;
	public User() {
		
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getIntegral() {
		return integral;
	}

	public void setIntegral(int integral) {
		this.integral = integral;
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

	public User(String userName, String passWord, String nickName, double winingProbability, int gameBout, int winBout, int integral) {
		this.userName = userName;
		this.passWord = passWord;
		this.nickName = nickName;
		this.winingProbability = winingProbability;
		this.gameBout = gameBout;
		this.winBout = winBout;
		this.integral = integral;
	}

	public void setWinBoutAddOne() {
		this.winBout++;
	}
	public void setGameBoutAddOne() {
		this.gameBout++;
	}
}
