package com.computer;



import java.awt.Color;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import com.raven.main.BeginWindow;
import com.raven.main.Room;
import util.GameRoomUtil;


public class ComputerGame extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	int ChessBoardWidth = 1800;
	int ChessBoardHeight  =  1000;
	
	BeginWindow parFrame;
	GamePlane gamepanel;
	JSplitPane chatPlane;
	JTextArea jt  = new JTextArea();
	JScrollPane jscroll ;
	public ComputerGame() {
		
	}
	public ComputerGame(BeginWindow parFrame,String username,int AlgLeave) {
				setLayout(null);
				
				//设置标题
				this.setTitle("人机对战");
				//设置窗体大小
				this.setSize(ChessBoardWidth, ChessBoardHeight);
				//设置窗体出现位置
				GameRoomUtil.CenterWindow(this);
				this.setVisible(true);
				//窗口关闭事件
				addWindowListener(new WindowEvent(this));
							
				this.parFrame = parFrame;
				
				//播放背景音乐
				GameRoomUtil.playBgmusic();
				
				
				setResizable(false);
				
				
				gamepanel = new GamePlane(this,username,AlgLeave);
			
				gamepanel.setLocation(0, 0);
				gamepanel.setSize(ChessBoardWidth-500, ChessBoardHeight);
				
				
			
				
				
				
				chatPlane = new JSplitPane();
				chatPlane.setLayout(null);
				chatPlane.setSize(450, ChessBoardHeight);
				chatPlane.setLocation(1300, 0);
				
				jscroll = new JScrollPane(jt);
				jscroll.setViewportView(jt);
				jscroll.setLocation(0,0);
				jscroll.setSize(450,800);
				jt.setLineWrap(true);        //激活自动换行功能 
				jt.setWrapStyleWord(true); 
				jt.setEditable(false);
				jt.setLocation(0,0);
				jt.setSize(450,800);
				jt.setBackground(new Color((int)0xE6E6FA));
				JTextField sendtext = new JTextField();
				sendtext.setLocation(10,850);
				sendtext.setSize(350,40);
				
				sendtext.setBackground(new Color((int)0xE6E6FA));
				jt.setFont(new Font("楷体", Font.PLAIN, 25));
				sendtext.setFont(new Font("楷体", Font.PLAIN, 20));
				JLabel bq = new JLabel("发送消息:");
				bq.setForeground(Color.pink);
				bq.setSize(200,30);
				bq.setLocation(10,810);
				chatPlane.add(bq);
				JButton send = new JButton("Send");
				send.setLocation(370,840);
				send.setSize(60,60);
				send.setForeground(Color.pink);
				chatPlane.add(jscroll);
				//chatPlane.add(jt);
				chatPlane.add(send);
				chatPlane.add(sendtext);
				jt.setForeground(new Color(180, 143, 160));
				jt.append("系统："+gamepanel.dateFormat.format(new Date())+"\n"+"   欢迎加入游戏厅，希望来到这里能给你带来快乐，与室友一起组队开黑吧~~\n");	
				jt.append("系统："+gamepanel.dateFormat.format(new Date())+"\n"+"  下赢我我就叫你哥哥啦~~\n");	
				send.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						if(sendtext.getText().equals("")) {
							JOptionPane.showMessageDialog(parFrame, "不要发送空消息~~");
							return;
						}
						if(sendtext.getText().contains("快点")) {
							GameRoomUtil.palyothermusic("source/flowerdie.mp3");
						}
						
						if(sendtext.getText().contains("停止")) {
							GameRoomUtil.stopmusic();
							gamepanel.musicing = false;
						}
						if(sendtext.getText().contains("开始")) {
							GameRoomUtil.playBgmusic();
							gamepanel.musicing = true;
						}
						
						jt.append(gamepanel.gameplayer1+"："+gamepanel.dateFormat.format(new Date())+"\n"+"   "+sendtext.getText()+"\n");
						sendtext.setText("");
						//最下方
						jt.setCaretPosition(jt.getDocument().getLength());
						//获取焦点
						sendtext.grabFocus();
					
						
					}
				});
				sendtext.addKeyListener(new KeyAdapter() {
					@Override
					public void keyPressed(KeyEvent e) {
						
						 if(e.getKeyChar() == KeyEvent.VK_ENTER )  
			                {  
								send.doClick();
			                    
			                }  
					}
				});
				
				add(gamepanel);
				add(chatPlane);
			

				
					
				
	}

		




	
}
class WindowEvent implements WindowListener{
	ComputerGame chessBoard;
	public WindowEvent(){
		
	}
	public WindowEvent(ComputerGame chessBoard) {
		this.chessBoard = chessBoard;
	}

	@Override
	public void windowClosed(java.awt.event.WindowEvent e) {
		
		
	}

	@Override
	public void windowOpened(java.awt.event.WindowEvent e) {
		//System.out.println("打开");
		
		
	}

	@Override
	public void windowClosing(java.awt.event.WindowEvent e) {
	
			if(chessBoard.gamepanel.kaishi) {
				
				int i = JOptionPane.showConfirmDialog(chessBoard, "当前游戏已经开始，退出游戏你将输掉比赛，确认退出游戏？","确认退出？",2);
				if(i!=0) {
					return;
				}
				
			}
			
			chessBoard.dispose();
			GameRoomUtil.stopmusic();//停止播放音乐
			chessBoard.parFrame.setVisible(true);
		
	}

	@Override
	public void windowIconified(java.awt.event.WindowEvent e) {
		
	}

	@Override
	public void windowDeiconified(java.awt.event.WindowEvent e) {
		
		
	}

	@Override
	public void windowActivated(java.awt.event.WindowEvent e) {

		
	}

	@Override
	public void windowDeactivated(java.awt.event.WindowEvent e) {
		
		
	}
}


class xiaqiThread extends Thread{
	GamePlane gmplane;
	MouseEvent e;
	
	CompututerAlg compututerAlg = new CompututerAlg();
	public xiaqiThread(GamePlane chessBoard, MouseEvent e) {
		this.gmplane = chessBoard;
		this.e = e;
	}

	@Override
	public void run() {
		
		if(gmplane.isme) {
			
			Point p = e.getPoint();
			if(gmplane.MouseAtChess) {
				if(!gmplane.kaishi) {
					JOptionPane.showMessageDialog(gmplane, "游戏还未开始您不能落子");
					return;
				}
				if(gmplane.chessPoint.size()==0&&gmplane.MyChessColorINT==1) {
					JOptionPane.showMessageDialog(gmplane, "黑方先手~~");
					return;
					
				}
				double x = (p.getX()-430)/45;
				double y = (p.getY()-110)/45;
				gmplane.rx = (int)x;
				gmplane.ry =(int)y;
				String xstr = Double.toString(x);
				String ystr = Double.toString(y);
				char xd = xstr.charAt(xstr.indexOf(".")+1);
				char yd = ystr.charAt(ystr.indexOf(".")+1);
				if(xd-'0'>5) {
					gmplane.rx += 1;
				}
				if(yd-'0'>5) {
					gmplane.ry +=1;
				}
				//System.out.println(chessBoard.rx+"..."+chessBoard.ry);
				//如果点在棋盘上
				if(!(gmplane.rx>=0&&gmplane.rx<=14&&gmplane.ry>=0&&gmplane.ry<=14)) {
					return;
				}
				if(gmplane.allChess[gmplane.rx][gmplane.ry] !=0) {
					System.out.println("一定落子了");
					return;
				}
				// 白1黑2
				if(gmplane.MyChessColor.equals("white")) {
					gmplane.allChess[gmplane.rx][gmplane.ry] =1;
				}else {
					gmplane.allChess[gmplane.rx][gmplane.ry] =2;
				}
				GameRoomUtil.palyothermusic("source/mousedown.mp3");
				gmplane.chessPoint.add(gmplane.MyChessColor+","+(gmplane.rx*45+430)+","+(gmplane.ry*45+110));
				gmplane.isme = false;
				
				
				//System.err.println("我的颜色："+gmplane.MyChessColor);
				System.out.println(gmplane.rx+"..."+gmplane.ry);		
				//判断是否赢了比赛
				boolean iswin = gmplane.checkwin(gmplane.MyChessColorINT);
				if(iswin) {
					System.out.println("你赢了啊！！！");
					JOptionPane.showMessageDialog(null, "你赢得了比赛！！");
					gmplane.chessBoard.jt.append("系统："+gmplane.dateFormat.format(new Date())+"\n   你赢得了比赛！！\n");
					gmplane.MyplayGamewinnum++;
					gmplane.GameWinAfter(gmplane);
					GameRoomUtil.palyothermusic("source/winmusic.mp3");
				}else {
					ComputerXiaqi(gmplane);
				}
				if(gmplane.chessPoint.size()==255) {
					gmplane.chessBoard.jt.append("系统："+gmplane.dateFormat.format(new Date())+"\n   本局平手！！\n");
					gmplane.MyplayGamewinnum++;
					gmplane.ComputerGamewinnum++;
					gmplane.GameWinAfter(gmplane);
				}
				gmplane.repaint();
				
			}
			
			
				
		}
	}
	public void ComputerXiaqi(GamePlane gamePlane) {
		int [][]allChess = gamePlane.allChess;
		List<String> chessPoint = gamePlane.chessPoint;
		Point resPoint = null;
		//找到下棋子的点
		
		if(gamePlane.AlgLeave == 1) {
			resPoint = compututerAlg.countMaxLines_medium(allChess,gamePlane.MyChessColorINT);
		}else if (gamePlane.AlgLeave == 2) {
			resPoint = compututerAlg.countMaxLines_primary(allChess,gamePlane.MyChessColorINT);
		}
		
		gmplane.rx = (int) resPoint.getX();
		gmplane.ry = (int) resPoint.getY();
		// 目前随机下点 没时间完善
		//找到没有棋子的点
	/*	gmplane.rx = new Random().nextInt(15);
		gmplane.ry = new Random().nextInt(15);
		while (allChess[gmplane.rx][gmplane.ry]!=0) {
			gmplane.rx = new Random().nextInt(15);
			gmplane.ry = new Random().nextInt(15);
		}*/
		//如果当前位置没有棋子
		if(allChess[gmplane.rx][gmplane.ry]==0) {
			if(gmplane.ComputerChessColor.equals("white")) {
				gmplane.allChess[gmplane.rx][gmplane.ry] =1;
			}else {
				gmplane.allChess[gmplane.rx][gmplane.ry] =2;
			}
			boolean status = gmplane.checkwin(gmplane.ComputerChessColorINT);
			//System.err.println("电脑颜色："+gmplane.ComputerChessColor);
			gmplane.chessPoint.add(gmplane.ComputerChessColor+","+(gmplane.rx*45+430)+","+(gmplane.ry*45+110));
			gamePlane.isme = true;
			gamePlane.repaint();
			if(status) {
				JOptionPane.showMessageDialog(gmplane, "电脑获得了胜利！");
				gmplane.chessBoard.jt.append("系统："+gmplane.dateFormat.format(new Date())+"\n   电脑赢得了比赛！！\n");
				gmplane.ComputerGamewinnum++;
				gmplane.GameWinAfter(gmplane);
			}
		}
		
	}
	
}
class GamePlane extends JSplitPane implements MouseListener{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static ImageIcon stopImage  = new ImageIcon("source/stop.png");
	static ImageIcon bgImage  = new ImageIcon("source/bgImage.jpg");
	static ImageIcon chatImage = new ImageIcon("source/chat.png");
	static ImageIcon moveImage = new ImageIcon("source/move.png");
	static ImageIcon beginBG = new ImageIcon("source/bfmusic.png");
	static ImageIcon StopBG = new ImageIcon("source/stopmusic.png");
	BufferedImage qizi = null;
	BufferedImage bqizi = null;
	BufferedImage hqizi = null;
	String blackMessage = "无限制";
	String whiteMessage = "无限制";
	String gameplayer1 ="";
	
	int GameWidth = 1300;
	int GameHeight = 1000;
	ComputerGame chessBoard;
	Boolean MouseAtChess =false;
	Boolean mousedown = false;
	boolean musicing = true;
	//当前棋子的行列
	int rx ;
	int ry ;
	//算法等级
	int AlgLeave;
	//棋子的坐标
	List<String> chessPoint = new ArrayList<String>();
	//下过棋子的坐标
	Point mouseMove = new Point();
	//下过棋子的状态
	int [][]allChess = new int[15][15];
	Font gmFont = new Font("楷体",Font.BOLD,28);
	Color gameColor = new Color((int)0xB03060);
	//判断是自己下棋
	boolean isme = true;
	DecimalFormat df = new DecimalFormat("0.00");
	String gameplayer2 ="风萧萧兮易水寒";
	
	
	//白1黑2
	String MyChessColor ="black";
	int MyChessColorINT =2;
	String ComputerChessColor ="white";
	int ComputerChessColorINT = 1;
	
	
	
	BufferedImage Colorstaus;
	//胜率
	double MYWINLV = 0;
	double CopputerWINLV = 0;
	
	SimpleDateFormat dateFormat= new SimpleDateFormat("hh:mm:ss");
	int MyplayGamewinnum = 0;
	int ComputerGamewinnum = 0;
	// 总场数
	int PlayGamenum = 0;
	
	boolean kaishi = false;
	ImageIcon man = new ImageIcon("source/man.jpg");
	ImageIcon women = new ImageIcon("source/women.jpg");
	
	URL classUrl = this.getClass().getResource("");  
	//空的图片 即就是隐藏鼠标
	Image imageCursor = Toolkit.getDefaultToolkit().getImage(classUrl);  
	
	public GamePlane(ComputerGame chessBoard,String gameplayer1,int AlgLeave) {
		setLayout(null);
		this.AlgLeave = AlgLeave;
		this.gameplayer1 = gameplayer1;
		this.chessBoard = chessBoard;
		//透明
		//setOpaque(false); 
		//不要忘记添加 这个事件
		addMouseListener(this);	
	
		try {
			
			qizi = ImageIO.read(new File("source/qizi.png"));
			hqizi = qizi.getSubimage(10, 10, 85, 85);
			bqizi = qizi.getSubimage(5, 95, 85,85);
			
		} catch (IOException e1) {
			
			e1.printStackTrace();
		}	

		
		man.setImage(man.getImage().getScaledInstance(150, 150, Image.SCALE_DEFAULT));
		women.setImage(women.getImage().getScaledInstance(150, 150, Image.SCALE_DEFAULT));
		stopImage.setImage(stopImage.getImage().getScaledInstance(40, 40, Image.SCALE_DEFAULT));
		moveImage.setImage(moveImage.getImage().getScaledInstance(400, 400, Image.SCALE_DEFAULT));	
		addMouseMotionListener(new MouseMotionListener() {
		
			@Override
			public void mouseMoved(MouseEvent e) {
				mouseMove = e.getPoint();
				if(e.getX()>=430-10&&e.getX()<=1060+10&&e.getY()>=110&&e.getY()<=740+10) {

					
					if(!BeginWindow.bofang) {
						GameRoomUtil.playChessMovemusic("source/move.mp3");
						
					}
					MouseAtChess = true;
					setCursor(Toolkit.getDefaultToolkit().createCustomCursor(
		                    imageCursor,  new Point(0, 0), "cursor"));  
				}else {
					MouseAtChess = false;
					setCursor(null);
				}
				
				repaint();
				
			}
			
			@Override
			public void mouseDragged(MouseEvent e) {
				
				
			}
	
		});
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		BufferedImage bf = new BufferedImage(GameWidth, GameHeight, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = bf.createGraphics();
		
		//bf = g2.getDeviceConfiguration().createCompatibleImage(GameWidth, GameHeight, Transparency.TRANSLUCENT);
		//g2.dispose();
		//g2 = bf.createGraphics();
		
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);  
		g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,RenderingHints.VALUE_STROKE_DEFAULT); 
		g2.drawImage(GamePlane.bgImage.getImage(), 0, 0,GameWidth, GameHeight, GamePlane.bgImage.getImageObserver());
		g2.setColor(Color.BLACK);
		for(int i=0;i<15;i++) {
			g2.drawLine(430, 110+45*i, 1060, 110+45*i);
			g2.drawLine(430+45*i, 110, 430+45*i, 110+45*14);
		}
		
		
		
		
		g2.setFont(gmFont);
		g2.setColor(Color.black);

		g2.drawString("认输", 510, 820);
		GameRoomUtil.writeString(mouseMove, mousedown, "认输", g2, 510, 820, 180, 60);
		g2.drawString("悔棋", 710, 820);
		GameRoomUtil.writeString(mouseMove, mousedown, "悔棋", g2, 710, 820, 180, 60);
		g2.drawString("和棋", 910, 820);
		GameRoomUtil.writeString(mouseMove, mousedown, "和棋", g2, 910, 820, 180, 60);
		g2.drawImage(man.getImage(), 130,10,150,150,man.getImageObserver());
		g2.drawString(gameplayer1, 130,200);
		g2.drawString("胜率"+String.format("%.2f", MYWINLV*100)+"%", 130,240);
		
		g2.drawImage(women.getImage(), 130,470,150,150,women.getImageObserver());
		g2.drawString(gameplayer2, 130,660);
		g2.drawString("胜率"+String.format("%.2f", CopputerWINLV*100)+"%", 130,700);
		
		
		if(kaishi) {
			g2.drawString("正在游戏中", 650, 80);
			GameRoomUtil.writeString(mouseMove, mousedown, "正在游戏中", g2, 650, 80, 180, 60);
		}else {
			g2.drawString("开始游戏", 650, 80);
			GameRoomUtil.writeString(mouseMove, mousedown, "开始游戏", g2, 650, 80, 180, 60);
		}
		g2.setColor(Color.red);
			
			
		

//画头像下的棋子颜色
		if(MyChessColor.equals("white")) {
			g2.drawImage(Room.chessWhite.getImage(), 150,260,60,60,this);
			if(!gameplayer2.equals(""))
			g2.drawImage(Room.chessBlack.getImage(), 150,750,60,60,this);
		}else {
			g2.drawImage(Room.chessBlack.getImage(), 150,260,60,60,this);
			if(!gameplayer2.equals(""))
			g2.drawImage(Room.chessWhite.getImage(), 150,750,60,60,this);
		}
	
	// 画棋子
		chessPoint.forEach((e)->{
			String msg[] = e.split(",");
			if(msg[0].equals("black")) {
				g2.drawImage(hqizi, (int)Integer.parseInt(msg[1])-30,(int)Integer.parseInt(msg[2])-30,60,60,this);
			}else {
				g2.drawImage(bqizi, (int)Integer.parseInt(msg[1])-34,(int)Integer.parseInt(msg[2])-20,60,60,this);
			}
			
		});
		// 最后一个棋子标红点
		if(chessPoint.size()>=1) {
			int x = (int)Integer.parseInt(chessPoint.get(chessPoint.size()-1).split(",")[1]);
			int y = (int)Integer.parseInt(chessPoint.get(chessPoint.size()-1).split(",")[2]);
			g2.setColor(Color.red);
			g2.fillOval(x, y, 8, 8);
		}

		if(MouseAtChess) {
			if(!isme) {
				g2.drawImage(stopImage.getImage(), (int)mouseMove.getX()-25,(int)mouseMove.getY()-25,45,45,stopImage.getImageObserver());
				
			}else {
				if(MyChessColor.equals("white")) {
					g2.drawImage(bqizi, (int)mouseMove.getX()-40,(int)mouseMove.getY()-20,60,60,this);
					
					
				}else {
					g2.drawImage(hqizi, (int)mouseMove.getX()-30,(int)mouseMove.getY()-30,60,60,this);
				
				}
			}

		}
		if(musicing) {
			
			g2.drawImage(GamePlane.StopBG.getImage(), 1200, 30,50,50, this);
		}else {
			
			g2.drawImage(GamePlane.beginBG.getImage(), 1200, 30,50,50, this);
		}
		
		
		g.drawImage(bf,0,0,this);
	}
	//鼠标按下
	@Override
	public void mousePressed(MouseEvent e) {
		
		mousedown  = true;
		//gemePlane.repaint();
	}


	@Override
	public void mouseClicked(MouseEvent e) {
		new xiaqiThread(this,e).start();
		if (e.getX()>=510&&e.getX()<=630&&e.getY()>=820-40&&e.getY()<=880-40) {
			GameRoomUtil.playChessMovemusic("source/mousedown.mp3");
			if(!kaishi) {
				JOptionPane.showMessageDialog(this, "游戏还没有开始~你就认输了？？","false",2);
				return;
			}
			int i =JOptionPane.showConfirmDialog(this, "你确定要认输吗","认输",2);
			if(i==0) {	
				ComputerGamewinnum++;
				GameWinAfter(this);
				
			}
		
		}else if (e.getX()>=710&&e.getX()<=830&&e.getY()>=820-40&&e.getY()<=880-40) {
			GameRoomUtil.playChessMovemusic("source/mousedown.mp3");
			if(!kaishi) {
				JOptionPane.showMessageDialog(this, "游戏还没有开始~","false",2);
				return;
			}
			if(chessPoint.size()==0) {
				JOptionPane.showMessageDialog(this, "棋盘还没有棋子，不要乱点啊，再点我生气了");
				return;
			}
			//如果棋盘最新棋子不是我的颜色 那么删除两个棋子
			if(allChess[rx][ry]!=MyChessColorINT) {
				int x = (Integer.parseInt(chessPoint.get(chessPoint.size()-1).split(",")[1])-430)/45;
				int y = (Integer.parseInt(chessPoint.get(chessPoint.size()-1).split(",")[2])-110)/45;
				int x2 = (Integer.parseInt(chessPoint.get(chessPoint.size()-2).split(",")[1])-430)/45;
				int y2 = (Integer.parseInt(chessPoint.get(chessPoint.size()-2).split(",")[2])-110)/45;
				allChess[x][y] = 0;
				allChess[x2][y2] = 0;
				chessPoint.remove(chessPoint.size()-1);
				chessPoint.remove(chessPoint.size()-1);
			}else {
				int x = (Integer.parseInt(chessPoint.get(chessPoint.size()-1).split(",")[1])-430)/45;
				int y = (Integer.parseInt(chessPoint.get(chessPoint.size()-1).split(",")[2])-110)/45;
				allChess[x][y] = 0;
				chessPoint.remove(chessPoint.size()-1);
			}
		
		}else if (e.getX()>=910&&e.getX()<=1030&&e.getY()>=820-40&&e.getY()<=880-40) {
			GameRoomUtil.playChessMovemusic("source/mousedown.mp3");
			if(!kaishi) {
				JOptionPane.showMessageDialog(this, "游戏还没有开始~","false",2);
				return;
			}
			int i =JOptionPane.showConfirmDialog(this, "你确定要和棋吗","和棋",2);
			if(i==0) {
				GameWinAfter(this);
			}
			
		}else if (e.getX()>=650&&e.getX()<=830&&e.getY()>=80-40&&e.getY()<=140-40) {
			GameRoomUtil.playChessMovemusic("source/mousedown.mp3");
			kaishi = true;
			System.out.println("开始游戏了");
			GameRoomUtil.palyothermusic("source/begin.mp3");
			//如果电脑先手
			if(ComputerChessColorINT==2) {
				rx = new Random().nextInt(14);
				ry = new Random().nextInt(14);
				allChess[rx][ry] = ComputerChessColorINT;
				chessPoint.add(ComputerChessColor+","+(rx*45+430)+","+(ry*45+110));
			}
			
		}else if (e.getX()>=1200&&e.getX()<=1250&&e.getY()>=30&&e.getY()<=80) {
			GameRoomUtil.playChessMovemusic("source/mousedown.mp3");
			if(musicing) {
				musicing = false;
				
				GameRoomUtil.stopmusic();
			}else {
				musicing = true;
				GameRoomUtil.playBgmusic();
			}
			
		}
	
		repaint();
		
	}


	@Override
	public void mouseReleased(MouseEvent e) {
		
		mousedown  = false;
		this.repaint();
	}


	@Override
	public void mouseEntered(MouseEvent e) {
		
		
	}


	@Override
	public void mouseExited(MouseEvent e) {
		//System.out.println("离开");
		
	}
	public  void GameWinAfter(GamePlane gamepanel) {
		gamepanel.isme = true;
		gamepanel.PlayGamenum++;
		
		kaishi = false;
		isme = true;
		System.out.println("当前总局数："+PlayGamenum+"对方的局数："+ComputerGamewinnum);
		System.out.println("当前总局数："+PlayGamenum+"你赢的局数："+MyplayGamewinnum);
		MYWINLV = (double)MyplayGamewinnum/PlayGamenum;
		CopputerWINLV = (double)ComputerGamewinnum/PlayGamenum;
		
		chessPoint.clear();
		for (int i = 0; i < allChess.length; i++) {
			for (int j = 0; j < allChess[i].length; j++) {
				allChess[i][j]=0;
			}
		}
		if(gamepanel.ComputerChessColor.equals("white")) {
			gamepanel.ComputerChessColor = "black";
			gamepanel.ComputerChessColorINT = 2;
			
			gamepanel.MyChessColor = "white";
			gamepanel.MyChessColorINT = 1;
			
		}else {
			gamepanel.ComputerChessColor = "white";
			gamepanel.ComputerChessColorINT = 1;
			
			gamepanel.MyChessColor = "black";
			gamepanel.MyChessColorINT = 2;
			
		}
		
	
	}
	
	public boolean checkwin(int whoColorINT) {
		int num = 0,l=1;
		boolean ks = true;
		//   \这种情况
		for(int k =0;k<8;k++) {
		
				//上半部分
				if(ks) {
					//确保不越界 
					if(rx-l>=0&&ry-l>=0) {
						if(allChess[rx-l][ry-l]==whoColorINT) {
							num++;
							if (num==4) {
								return true;
							}
						}else {
							if(ks) {
								ks =false;
								l = 0;
							}
							
						}
						
					}else {
						if(ks) {
							ks =false;
							l = 0;
						}
					}
				}else {
					if(rx+l<=14&&ry+l<=14) {
						if(allChess[rx+l][ry+l]==whoColorINT) {
							num++;
							if (num==4) {
								return true;
							}
							
						}else {
							break;
						}
					}
				}
				l++;
		}
		num = 0;l=1;ks = true;
		//   |这种情况
		for(int k =0;k<8;k++) {
			//上半部分
			if(ks) {
				//确保不越界 
				if(ry-l>=0) {
					if(allChess[rx][ry-l]==whoColorINT) {
						num++;
						if (num==4) {
							return true;
						}
					}else {
						if(ks) {
							ks =false;
							l = 0;
						}		
					}	
				}else {
					if(ks) {
						ks =false;
						l = 0;
					}
				}	
			}else {
				if(ry+l<=14) {
					if(allChess[rx][ry+l]==whoColorINT) {
						num++;
						if (num==4) {
							return true;
						}				
					}else {
						break;
					}
				}
			}
			l++;
		}
			num = 0;l=1;ks = true;
			//   /这种情况
			for(int k =0;k<8;k++) {
				//上半部分
				if(ks) {
					//确保不越界 
					if(ry-l>=0&&rx+l<=14) {
						if(allChess[rx+l][ry-l]==whoColorINT) {
							num++;
							if (num==4) {
								return true;
							}
						}else {
							if(ks) {
								ks =false;
								l = 0;
							}
							
						}
						
					}else {
						if(ks) {
							ks =false;
							l = 0;
						}
					}	
				}else {
					if(rx-l>=0&&ry+l<=14) {
						if(allChess[rx-l][ry+l]==whoColorINT) {
							num++;
							if (num==4) {
								return true;
							}
							
						}else {
							break;
						}
				
					}
					
		
				}
				l++;
		}
		
			num = 0;l=1;ks = true;
			//   ------ 这种情况
			for(int k =0;k<8;k++) {
				//上半部分
				if(ks) {
					//确保不越界 
					if(rx-l>=0) {
						if(allChess[rx-l][ry]==whoColorINT) {
							num++;
							if (num==4) {
								return true;
							}
						}else {
							if(ks) {
								ks =false;
								l = 0;
							}					
						}				
					}else {
						if(ks) {
							ks =false;
							l = 0;
						}
					}	
				}else {
					if(rx+l<=14) {
						if(allChess[rx+l][ry]==whoColorINT) {
							num++;
							if (num==4) {
								return true;
							}				
						}else {
							break;
						}
					}
				}
			l++;
				
		}
			
				
		
		
		return false;
	}
	
}
