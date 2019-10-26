package client.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;
import bean.User;
import client.window.BeginWindow;
import client.window.ChessBoard;
import client.window.Room;
import util.GameRoomUtil;
import util.HttpConnectUtil;

public class GamePlane extends JSplitPane implements MouseListener{
	
	/**
	 * 
	 */
	
	private static final long serialVersionUID = 1L;
	public static ImageIcon stopImage  = new ImageIcon("source/stop.png");
	public static ImageIcon bgImage  = new ImageIcon("source/bgImage.jpg");
	public static ImageIcon chatImage = new ImageIcon("source/chat.png");
	public static ImageIcon moveImage = new ImageIcon("source/move.png");
	public static ImageIcon beginBG = new ImageIcon("source/bfmusic.png");
	public static ImageIcon StopBG = new ImageIcon("source/stopmusic.png");
	public BufferedImage qizi = null;
	public BufferedImage bqizi = null;
	public BufferedImage hqizi = null;
	public String blackMessage = "无限制";
	public String whiteMessage = "无限制";
	
	public int GameWidth = 1300;
	public int GameHeight = 1000;
	public static ChessBoard chessBoard;
	public Boolean MouseAtChess =false;
	public Boolean mousedown = false;
	public boolean musicing = true;
	//当前棋子的行列
	public static int rx ;
	public static int ry ;
	//棋子的坐标
	public List<String> chessPoint = new ArrayList<String>();
	//下过棋子的坐标
	Point mouseMove = new Point();
	//下过棋子的状态
	public int [][]allChess = new int[15][15];
	Font gmFont = new Font("楷体",Font.BOLD,28);
	Color gameColor = new Color((int)0xB03060);
	//判断是自己下棋
	public boolean isme = true;
	DecimalFormat df = new DecimalFormat("0.00");

	public User gameplayer1;
	public User gameplayer2;
	ImageIcon man = new ImageIcon("source/man.jpg");
	ImageIcon women = new ImageIcon("source/women.jpg");
	
	public static String MyChessColor ="black";
	
	//白1黑2
	public static int MyChessColorINT =2;
	BufferedImage Colorstaus;
	
	
	public static SimpleDateFormat dateFormat= new SimpleDateFormat("hh:mm:ss");

	public boolean zhunbei = false;
	public boolean kaishi = false;
	
	URL classUrl = this.getClass().getResource("");  
	Image imageCursor = Toolkit.getDefaultToolkit().getImage(classUrl);  
	public GamePlane() {
		setLayout(null);
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
		g2.drawString(gameplayer1.getNickName(), 150,200);
		g2.drawString("胜率："+String.format("%.2f", gameplayer1.getWiningProbability()*100)+"%", 135,240);
		g2.drawString("总局数："+gameplayer1.getGameBout(), 135,280);
		if(gameplayer2 != null) {
			g2.drawImage(women.getImage(), 130,470,150,150,women.getImageObserver());
			g2.drawString(gameplayer2.getNickName(), 150,660);
			g2.drawString("胜率："+String.format("%.2f", gameplayer2.getWiningProbability()*100)+"%", 135,700);
			g2.drawString("总局数："+gameplayer2.getGameBout(), 135,740);
		}
		if(chessBoard.RoomType.equals("CreateRoom")) {
			if(kaishi) {
				g2.drawString("正在游戏中", 650, 80);
				GameRoomUtil.writeString(mouseMove, mousedown, "正在游戏中", g2, 650, 80, 180, 60);
			}else {
				g2.drawString("开始游戏", 650, 80);
				GameRoomUtil.writeString(mouseMove, mousedown, "开始游戏", g2, 650, 80, 180, 60);
				g2.setColor(Color.red);
				if(gameplayer2 != null) {
					if(zhunbei) {
						g2.drawString("已准备", 140,780);
					}else {
						g2.drawString("未准备", 140,780);
					}
				}
			}
			
			
		}else {
			if(kaishi) {//如果游戏开始了 那么就 显示正在游戏
				g2.drawString("正在游戏中", 650, 80);
				GameRoomUtil.writeString(mouseMove, mousedown, "正在游戏中", g2, 650, 80, 180, 60);
			}else {//否则的话就是准备与取消
				if(zhunbei) {
					g2.drawString("取消准备", 650, 80);
					GameRoomUtil.writeString(mouseMove, mousedown, "取消准备", g2, 650, 80, 180, 60);
				}else {
					g2.drawString("点击准备", 650, 80);
					GameRoomUtil.writeString(mouseMove, mousedown, "点击准备", g2, 650, 80, 180, 60);
				}
			}
			
			
		}
	
		//画头像下的棋子颜色
				if(MyChessColor.equals("white")) {
					g2.drawImage(Room.chessWhite.getImage(), 150,300,60,60,this);
					if(gameplayer2 != null)
					g2.drawImage(Room.chessBlack.getImage(), 150,790,60,60,this);
				}else {
					g2.drawImage(Room.chessBlack.getImage(), 150,300,60,60,this);
					if(gameplayer2 != null)
					g2.drawImage(Room.chessWhite.getImage(), 150,790,60,60,this);
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
						g2.drawImage(GamePlane.stopImage.getImage(), (int)mouseMove.getX()-25,(int)mouseMove.getY()-25,45,45,stopImage.getImageObserver());
						
					}else {
						if(GamePlane.MyChessColor.equals("white")) {
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
		
		g.drawImage(bf,0,0,this);
	}
	
	

	
	public void setChessBoard(ChessBoard chessBoard) {
		GamePlane.chessBoard = chessBoard;
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
				JOptionPane.showMessageDialog(this, "游戏还没有开始~不可以认输","false",2);
				return;
			}
			int i =JOptionPane.showConfirmDialog(this, "你确定要认输吗","认输",2);
			if(i==0) {
				GameRoomUtil.SendMsgToServer(chessBoard, "AdmitDefeat",null);
				gameplayer2.setWinBoutAddOne();
				GameWinAfter(this);		
			}
		
		}else if (e.getX()>=710&&e.getX()<=830&&e.getY()>=820-40&&e.getY()<=880-40) {
			GameRoomUtil.playChessMovemusic("source/mousedown.mp3");
			if(!kaishi) {
				JOptionPane.showMessageDialog(this, "游戏还没有开始~","false",2);
				return;
			}
			if(chessPoint.size()==0) {
				JOptionPane.showMessageDialog(this, "棋盘还没有棋子，不能悔棋。");
				return;
			}
			if(allChess[rx][ry]!=MyChessColorINT) {
				JOptionPane.showMessageDialog(this, "对方已经落子你不能悔棋");
				return;
			}

			int i =JOptionPane.showConfirmDialog(this, "你确定要悔棋吗","悔棋",2);
			if(i==0)GameRoomUtil.SendMsgToServer(chessBoard, "huiqi",null);
			
		
		}else if (e.getX()>=910&&e.getX()<=1030&&e.getY()>=820-40&&e.getY()<=880-40) {
			GameRoomUtil.playChessMovemusic("source/mousedown.mp3");
			if(!kaishi) {
				JOptionPane.showMessageDialog(this, "游戏还没有开始~","false",2);
				return;
			}
			int i =JOptionPane.showConfirmDialog(this, "你确定要和棋吗","和棋",2);
			if(i==0) {
				gameplayer1.setWinBoutAddOne();
				gameplayer2.setWinBoutAddOne();
				GameRoomUtil.SendMsgToServer(chessBoard, "heqi",null);
				
			}
			
		}else if (e.getX()>=650&&e.getX()<=830&&e.getY()>=80-40&&e.getY()<=140-40) {
			GameRoomUtil.playChessMovemusic("source/mousedown.mp3");
			if(!kaishi) {
				if(chessBoard.RoomType.equals("CreateRoom")) {
					//判断时候准备了
					
						if(zhunbei) {
							GameRoomUtil.SendMsgToServer(chessBoard, "GameBegin",null);
							kaishi = true;
							System.out.println("开始游戏了");
							GameRoomUtil.palyothermusic("source/begin.mp3");
						}else {
							if(gameplayer2 != null) {
								JOptionPane.showMessageDialog(chessBoard, gameplayer2.getNickName()+"还没有准备，无法开始游戏~");
								System.out.println("他还没有还没有准备");
								kaishi = false;
							}else {
								JOptionPane.showMessageDialog(chessBoard, "还没有玩家加入您的房间！无法开始游戏~");
								System.out.println("还没有玩家加入您的房间！无法开始游戏！");
							}
						
						}
						
					
					//加入玩家点击已准备发送消息
				}else {
					
					if(zhunbei) {
						zhunbei= false;	
						GameRoomUtil.SendMsgToServer(chessBoard,"GameReady","0");
						System.out.println("未准备！");
					}else {
						zhunbei = true;
						System.out.println("已准备！");
						GameRoomUtil.SendMsgToServer(chessBoard,"GameReady","1");
					}
					//给服务器发送消息  zhunbei的状态;
					
				}
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
		gameplayer1.setGameBoutAddOne();
		if(gameplayer2 != null) {
			gameplayer2.setGameBoutAddOne();
		}
		
		zhunbei = false;
		kaishi = false;
		isme = true;
		System.out.println("当前他的总局数："+gameplayer2.getGameBout()+"对方赢的局数："+gameplayer2.getWinBout());
		System.out.println("当前你的总局数："+gameplayer1.getGameBout()+"你赢的局数："+gameplayer1.getWinBout());
		gameplayer1.setWiningProbability((double)gameplayer1.getWinBout()/gameplayer1.getGameBout());
		gameplayer2.setWiningProbability((double)gameplayer2.getWinBout()/gameplayer2.getGameBout());
		
		chessPoint.clear();
		for (int i = 0; i < allChess.length; i++) {
			for (int j = 0; j < allChess[i].length; j++) {
				allChess[i][j]=0;
			}
		}
		if(GamePlane.MyChessColor.equals("white")) {
			GamePlane.MyChessColorINT = 2;
			GamePlane.MyChessColor = "black";
			
		}else {
			GamePlane.MyChessColorINT = 1;
			GamePlane.MyChessColor = "white";
			
		}
		new Thread() {
			public void run() {
				Map<String, String> map = new HashMap<String, String>();
				map.put("userName", gameplayer1.getUserName());
				map.put("winingProbability", gameplayer1.getWiningProbability()+"");
				map.put("winBout", gameplayer1.getWinBout()+"");
				map.put("gameBout", gameplayer1.getGameBout()+"");
				String stau= HttpConnectUtil.requestHTTP(BeginWindow.host+"/update", map, "get");
				System.out.println(stau);
				GameRoomUtil.SendMsgToServer(chessBoard, "updateUserInfo", null);
			};
		}.start();
	}
	


	public User getGameplayer1() {
		return gameplayer1;
	}

	public void setGameplayer1(User gameplayer1) {
		this.gameplayer1 = gameplayer1;
	}

	public User getGameplayer2() {
		return gameplayer2;
	}

	public void setGameplayer2(User gameplayer2) {
		this.gameplayer2 = gameplayer2;
	}

	public boolean checkwin() {
		int num = 0,l=1;
		boolean ks = true;
		//   \这种情况
		for(int k =0;k<8;k++) {
		
				//上半部分
				if(ks) {
					//确保不越界 
					if(rx-l>=0&&ry-l>=0) {
						if(allChess[rx-l][ry-l]==MyChessColorINT) {
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
						if(allChess[rx+l][ry+l]==MyChessColorINT) {
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
					if(allChess[rx][ry-l]==MyChessColorINT) {
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
					if(allChess[rx][ry+l]==MyChessColorINT) {
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
					if(rx+l<=14&&ry-l>=0) {
						if(allChess[rx+l][ry-l]==MyChessColorINT) {
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
						if(allChess[rx-l][ry+l]==MyChessColorINT) {
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
						if(allChess[rx-l][ry]==MyChessColorINT) {
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
						if(allChess[rx+l][ry]==MyChessColorINT) {
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


	public static String getMyChessColor() {
		return MyChessColor;
	}


	public static void setMyChessColor(String myChessColor) {
		MyChessColor = myChessColor;
	}


	public static int getMyChessColorINT() {
		return MyChessColorINT;
	}


	public static void setMyChessColorINT(int myChessColorINT) {
		MyChessColorINT = myChessColorINT;
	}
		
}
class xiaqiThread extends Thread{
	GamePlane gmplane;
	MouseEvent e;
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
				if(gmplane.chessPoint.size()==0&&GamePlane.MyChessColorINT==1) {
					JOptionPane.showMessageDialog(gmplane, "黑方先手~~");
					return;
					
				}
				double x = (p.getX()-430)/45;
				double y = (p.getY()-110)/45;
				GamePlane.rx = (int)x;
				GamePlane.ry =(int)y;
				String xstr = Double.toString(x);
				String ystr = Double.toString(y);
				char xd = xstr.charAt(xstr.indexOf(".")+1);
				char yd = ystr.charAt(ystr.indexOf(".")+1);
				if(xd-'0'>5) {
					GamePlane.rx += 1;
				}
				if(yd-'0'>5) {
					GamePlane.ry += 1;
				}
				//System.out.println(chessBoard.rx+"..."+chessBoard.ry);
				//如果点在棋盘上
				if(!(GamePlane.rx>=0&&GamePlane.rx<=14&&GamePlane.ry>=0&&GamePlane.ry<=14)) {
					return;
				}
				if(gmplane.allChess[GamePlane.rx][GamePlane.ry] !=0) {
					System.out.println("一定落子了");
					return;
				}
				// 白1黑2
				if(GamePlane.MyChessColor.equals("white")) {
					gmplane.allChess[GamePlane.rx][GamePlane.ry] =1;
				}else {
					gmplane.allChess[GamePlane.rx][GamePlane.ry] =2;
				}
				GameRoomUtil.palyothermusic("source/mousedown.mp3");
				gmplane.chessPoint.add(GamePlane.MyChessColor+","+(GamePlane.rx*45+430)+","+(GamePlane.ry*45+110));
				gmplane.isme = false;
				
				
		
				//System.out.println(GamePlane.rx+"..."+GamePlane.ry);
				GameRoomUtil.SendMsgToServer(GamePlane.chessBoard,"ChessBorldLocation",GamePlane.MyChessColor+","+GamePlane.rx+","+GamePlane.ry);
			
				
				//判断是否赢了比赛
				boolean iswin = gmplane.checkwin();
				if(iswin) {
					GameRoomUtil.SendMsgToServer(GamePlane.chessBoard, "YouLose", null);
					JOptionPane.showMessageDialog(gmplane, "你赢得了比赛！！");
					ChessBoard.jt.append("系统："+gmplane.dateFormat.format(new Date())+"\n   你赢得了比赛！！\n");
					gmplane.gameplayer1.setWinBoutAddOne();
					gmplane.GameWinAfter(gmplane);
					
					GameRoomUtil.palyothermusic("source/winmusic.mp3");
				}
				gmplane.repaint();
			
				
			}
			
			
				
		}
	}

}
