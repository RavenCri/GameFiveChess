package client.window;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import common.pojo.User;
import client.computer.ComputerGame;
import util.GameRoomUtil;
/**
 * 
 * @author Raven
 * @date 下午5:54:11
 * @version
 *  	该类是一个窗口，供玩家选择功能
 */
public class BeginWindow extends JFrame{

	private static final long serialVersionUID = 1L;
	// web主机地址
	public static String webHost = "http://127.0.0.1:8080";
	// 当前游戏窗口的输出流
	static public BufferedWriter out;
	// 当前游戏窗口的读取流
	static public BufferedReader in;
	// 判断当前是否播放音乐。
	public static boolean bofang;
	// 当前游戏窗口的用户玩家信息
	public static User userPlayer;
	// 当前游戏用户的socket对象
	static public Socket socket = null;
	// 登录对象
	public boolean loginstaus = false;
	public MyPlane myplane;
	//static public Room room;
	public BeginWindow(){
		setSize(530,700);
		setTitle("五子棋网络版  By Raven");
		setResizable(false);
		setLayout(null);
		GameRoomUtil.CenterWindow(this);
		setVisible(true);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
				
			}
		});
		// 添加面板容器
		myplane = new MyPlane(this);
		myplane.setSize(530,700);
		myplane.setLocation(0,0);
		add(myplane);
	}
	


	
	
	

}
// 窗口面板
class MyPlane extends JPanel implements MouseListener{

	private static final long serialVersionUID = 1L;
	BeginWindow beginWindow;
	int rectwidth = 180;
	int rectheight = 60;
	
	Font beginFont = new Font("黑体",Font.BOLD,60);
	Font gameFont = new Font("黑体",Font.BOLD,30);
	static ImageIcon bgImg;
	static Point p =new Point();
	
	//判断鼠标是否按下
	static Boolean mousedown = false;
	int i= 0,y =100,j=20;
	int modelint = 0;
	Timer timer;
	static String serverIp = "127.0.0.1";
	private GameRoomUtil gameRoomUtil = new GameRoomUtil();
	public MyPlane() {
	
	}
	
	public MyPlane(BeginWindow beginWindow) {
		bgImg = new ImageIcon(getClass().getClassLoader().getResource("img/bkImg.jpg"));
		this.beginWindow = beginWindow;
		//针对鼠标移动的接口
		this.addMouseMotionListener(new MouseMotionAdapter() {
			//鼠标移动触发器
			public void mouseMoved(MouseEvent e) {
				p = e.getPoint();
				// 如果不是个人介绍页面的话
				if(modelint!=2)
				repaint();
			}
			//按住鼠标移动才生效
			public void mouseDragged(MouseEvent e) {
				//System.out.println("鼠标按下不松拖动点的轨迹");

			}
		});
		addMouseListener(this);
		repaint();
	}
	@Override
	public void paint(Graphics g) {
		
		BufferedImage bi = new BufferedImage(1024, 1024,BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = bi.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);  
		g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,RenderingHints.VALUE_STROKE_DEFAULT); 
		//如果是模式0 的话
		if(modelint==0) {
			g2.drawImage(bgImg.getImage(), 0, 0,  530, 700,bgImg.getImageObserver());
			g2.setColor(Color.pink);
			g2.setFont(beginFont);
			g2.drawString("五子棋网络版", 40, 110);
			g2.setFont(gameFont);
			
			g2.drawString("网络对战", 150, 200);
			g2.drawString("人机对战", 150, 300);
			g2.drawString("游戏说明", 150, 400);
			g2.drawString("关于作者", 150, 500);
			
			GameRoomUtil.writeString(p,mousedown,"网络对战",g2,150, 200, rectwidth, rectheight);
			GameRoomUtil.writeString(p,mousedown,"人机对战",g2,150, 300, rectwidth, rectheight);
			GameRoomUtil.writeString(p,mousedown,"游戏说明",g2, 150, 400, rectwidth, rectheight);
			GameRoomUtil.writeString(p,mousedown,"关于作者", g2,150, 500, rectwidth, rectheight);
		}else if (modelint==1) {
			g2.drawImage(bgImg.getImage(), 0, 0,  530, 700, bgImg.getImageObserver());
			g2.setColor(Color.green);
			g2.setFont(gameFont);
			g2.drawString("这是一款网络对战的五子棋", 40, 100);
			g2.drawString("游戏，你可以和局域网中的", 40, 150);
			g2.drawString("好友一起对战游戏哦", 40, 200);
			g2.drawString("返回上个界面", 150, 500);
			GameRoomUtil.writeString(p, mousedown, "返回上个界面", g2, 150, 500, 250, 50);
		}else if (modelint==2) {
			super.paint(g);
			String msg = "你越觉得自己爱什么";
			String msg2= "那么你就越对什么爱的着迷~";
			String msg3 = "BY  Raven";
			if(timer!=null) {
				timer.cancel();
			}
			timer = new Timer();
			timer.schedule(new TimerTask() {
				
				@Override
				public void run() {
					i++;
					if(i>msg2.length()) {
						i=0;
					}
					if(i>10) {
						y+=50;
					}
					if(y>=400) {
						y=100;
					}
					repaint();
					
				}
			}, 250);
			g2.drawImage(bgImg.getImage(), 0, 0,  530, 700, bgImg.getImageObserver());
			g2.setColor(Color.RED);
			g2.setFont(gameFont);	
			if(i>msg.length()||i>msg3.length()) {
				
				g2.drawString(msg.substring(0,msg.length()), j, y);
				g2.drawString(msg3.substring(0,msg3.length()), j, y+100);
			}else {
				g2.drawString(msg.substring(0,i), j, y);
				g2.drawString(msg3.substring(0,i), j, y+100);
			}
			
			g2.drawString(msg2.substring(0,i), j, y+50);
			
			g2.drawString("返回上个界面", 150, 500);
			GameRoomUtil.writeString(p, mousedown, "返回上个界面", g2, 150, 500, 250, 50);
		}else if ( modelint == 3 ) {
			g2.drawImage(bgImg.getImage(), 0, 0,  530, 700,bgImg.getImageObserver());
			g2.setColor(Color.pink);
			g2.setFont(beginFont);
			g2.drawString("难度选择", 80, 110);
			g2.setFont(gameFont);
			
			g2.drawString("一般难度", 150, 200);
			g2.drawString("中级难度", 150, 300);
			
			GameRoomUtil.writeString(p,mousedown,"一般难度",g2,150, 200, rectwidth, rectheight);
			GameRoomUtil.writeString(p,mousedown,"中级难度",g2,150, 300, rectwidth, rectheight);
			g2.setColor(Color.pink);
			g2.drawString("返回上个界面", 150, 500);
			GameRoomUtil.writeString(p, mousedown, "返回上个界面", g2, 150, 500, 250, 50);
			
		}
		g2.dispose();
		g.drawImage(bi, 0, 0, this);
		
	}
	/***
	* @Description: 点击事件
	* @Param: [e]
	* @return: void
	* @Author: raven
	* @Date: 2020/3/28
	*/
	@Override
	public void mouseClicked(MouseEvent e) {
		//在线游戏或者 人机对战（位置重合了 需要在细化判断）
		if(p.getX()>=120&&p.getX()<=330-30&&p.getY()>=160&&p.getY()<=220&&(modelint==0||modelint==3)) {
			//在线游戏
			if(modelint==0) {
				OnlinePlaygame();
			//人机对战
			}else if (modelint==3) {
				ToComputerPlayGame(1);
				repaint();
			}
			gameRoomUtil.palyothermusic("sound/mousedown.mp3");
		//人机界面	
		}else if(p.getX()>=120&&p.getX()<=330-30&&p.getY()>=260&&p.getY()<=360-40&&(modelint==0||modelint==3)) {
			
			if(modelint==0) {
				modelint = 3;
			}else if (modelint==3) {
				ToComputerPlayGame(2);
				repaint();
			}
			gameRoomUtil.palyothermusic("sound/mousedown.mp3");
		// 游戏说明
		}else if(p.getX()>=120&&p.getX()<=330-30&&p.getY()>=360&&p.getY()<=460-40&&modelint==0) {
			
			modelint=1;
			repaint();
			//进入 游戏说明
			gameRoomUtil.palyothermusic("sound/mousedown.mp3");
		// 关于
		}else if(p.getX()>=120&&p.getX()<=330-30&&p.getY()>=460&&p.getY()<=560-40&&modelint==0) {
			
			modelint=2;
			repaint();
			// 进入关于作者
			gameRoomUtil.palyothermusic("sound/mousedown.mp3");
		//  y坐标注意要减字体大小的像素
		//点击 返回上一界面
		}else if (p.getX()>=150-30&&p.getX()<=400-30&&p.getY()>=500-40&&p.getY()<=550-40&&(modelint==1||modelint==2||modelint==3)) {

			modelint=0;
			repaint();
			gameRoomUtil.palyothermusic("sound/mousedown.mp3");
		}
	}
	/***
	* @Description: 创建人机对战窗口
	* @Param: [AlgLeave] 难度等级
	* @return: void
	* @Author: raven
	* @Date: 2020/3/28
	*/
	public void ToComputerPlayGame(int AlgLeave) {
		gameRoomUtil.palyothermusic("sound/mousedown.mp3");

		String username = null;
		//隐藏该窗口 创建人机窗口
		if(BeginWindow.userPlayer==null) {
			
			username = JOptionPane.showInputDialog("给您起一个个性的名称吧~");
			if(username == null || username.equals("")) {
				
				JOptionPane.showMessageDialog(beginWindow, "名字不能为空哦。");
				return;
			}
			BeginWindow.userPlayer = new User();
			BeginWindow.userPlayer.setUserName(username); 
		}
		new ComputerGame(beginWindow, BeginWindow.userPlayer.getUserName(),AlgLeave);
		beginWindow.setVisible(false);
		repaint();
	}
	/***
	* @Description: 点击了网络对战对应事件
	* @Param: []
	* @return: void
	* @Author: raven
	* @Date: 2020/3/28
	*/
	public void OnlinePlaygame() {
		gameRoomUtil.palyothermusic("sound/mousedown.mp3");
		// 登录游戏
		loginGame();
	}


	@Override
	public void mousePressed(MouseEvent e) {
		   //System.out.println("鼠标按下");  
		mousedown = true;
	
		this.repaint();
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		
		mousedown = false;
		//System.out.println("鼠标松开"); 
		
	}
	@Override
	public void mouseEntered(MouseEvent e) {
		//System.out.println("鼠标进入窗体");
	}
	@Override
	public void mouseExited(MouseEvent e) {
		//System.out.println("鼠标离开窗体");
	}
	
	private void loginGame() {
		int opotion = 0;
		try {
			
			opotion = JOptionPane.showConfirmDialog(this, "点击确定连接Raven的服务器，点击取消则连接本地服务器","连接方式",2);
			// 如果登录我提供的公网服务器
			if(opotion==0) {
				URL ip=new URL("http://raven520.top");
				System.out.println(ip.getHost());
				BeginWindow.socket = new Socket();
				BeginWindow.socket.connect(new InetSocketAddress(ip.getHost(), 6666),2000);
				BeginWindow.webHost = "http://fivechess.raven520.top:5500";
			// 用本地自己搭建的服务器
			}else {
				serverIp  = JOptionPane.showInputDialog(this, "请输入服务器IP地址(默认为本机->127.0.0.1)");
				if(serverIp==null)return;
				if(serverIp.equals("")) {
					serverIp ="127.0.0.1";
					BeginWindow.webHost = "http://127.0.0.1:8080";
				}
				BeginWindow.socket = new Socket();
				BeginWindow.socket.connect(new InetSocketAddress(serverIp, 6666),2000);
			}
		} catch (IOException e) {
			if(opotion == 0) {
				JOptionPane.showMessageDialog(this, "Raven的服务器连接超时了，请自行运行本地服务器哦~");
				return;
			}
			JOptionPane.showMessageDialog(this, "服务器连接失败！请检测目标ip主机是否开启本地服务/防火墙。");
			return;
		}
		// 登录窗口
		new LoginFream(beginWindow).setVisible(true);
		beginWindow.setVisible(false);
	}

}


