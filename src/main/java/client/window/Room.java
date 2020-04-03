package client.window;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.*;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import client.handle.HandleMsgThread;
import client.handle.ReceiveMsgThread;
import client.plane.GamePlane;
import client.plane.RoomPlane;
import common.pojo.User;
import util.GameRoomUtil;
/***
 * 
 * @author Raven
 * @date 下午5:57:58
 * @version
 * 	在线游戏列表
 */
public class Room extends JFrame {
	
	private static final long serialVersionUID = 1L;
	// 男人头像
	public static ImageIcon man;
	// 女人头像
	public static ImageIcon women;
	// 白棋子图标
	public static ImageIcon chessWhite;
	// 黑棋子图标
	public static ImageIcon chessBlack;
	// 在线游戏房间容器
	public static RoomPlane RoomsLeftPlane = new RoomPlane();
	public BeginWindow priwid;
	// 棋盘
	public static ChessBoard chessBoard;
	// 消息队列
	public static LinkedBlockingQueue<JSONObject> msgQueue = new LinkedBlockingQueue<>();
	// 消息列表
	public static volatile JSONArray roomList;
	// 空房间
	public static volatile boolean emptyRoom = true;
	// 聊天消息记录
	public JSplitPane chatRigthPlane;
	// 滚动条
	public JScrollPane jscroll;
	// 文字编辑框
	public static JTextArea jt  = new JTextArea();
	// 格式化时间
	public SimpleDateFormat dateFormat= new SimpleDateFormat("hh:mm:ss");
	public static  JTable gameUsersTable;
	public static DefaultTableModel tableModel;
	public static Object[][] cellData;
	public static String[] columnNames = {"账号","昵称", "积分", "胜率"};

	public static List<JSONObject> userfirends = new ArrayList<>();
	public Room(BeginWindow priwid) throws InterruptedException {
		setLayout(null);
		//大小不变
		setResizable(false);
		setTitle("在线游戏列表");
		man = new ImageIcon(getClass().getClassLoader().getResource("img/man.jpg"));
		women = new ImageIcon(getClass().getClassLoader().getResource("img/women.jpg"));
		chessWhite = new ImageIcon(getClass().getClassLoader().getResource("img/chessWhite.png"));
		chessBlack = new ImageIcon(getClass().getClassLoader().getResource("img/chessBlack.png"));
		man.setImage(man.getImage().getScaledInstance(40, 40,
				Image.SCALE_DEFAULT));
		women.setImage(women.getImage().getScaledInstance(40, 40,
				Image.SCALE_DEFAULT));
		chessWhite.setImage(chessWhite.getImage().getScaledInstance(40, 40,
				Image.SCALE_DEFAULT));
		chessBlack.setImage(chessBlack.getImage().getScaledInstance(40, 40,
				Image.SCALE_DEFAULT));
		

		this.priwid = priwid;
		// 设置窗体大小
		setSize(1300, 1000);
		// 居中窗体
		GameRoomUtil.CenterWindow(this);


		//开启消息接收线程。
		new ReceiveMsgThread().start();
		ReceiveMsgThread.readFlag = true;
		// 开启消息处理线程
		new HandleMsgThread().start();
		// 初始化左侧面板（房间列表）
		RoomsLeftPlane.room = this;
		RoomsLeftPlane.setSize(800,1000);
		RoomsLeftPlane.setLocation(0, 0);
		add(RoomsLeftPlane);
		// 右侧功能面板
		chatRigthPlane = new JSplitPane();
		chatRigthPlane.setLayout(null);
		chatRigthPlane.setSize(450, 1000);
		chatRigthPlane.setLocation(800, 0);
		// 初始化社交功能
		initSocialContact();
		//初始化大厅游戏人员
		initUsersTable();

		// 初始化聊天面板的滚动条
		initChatPanle();




	}

	private void initSocialContact() {
		JButton msgButtton = new JButton("消息中心");
		msgButtton.setLocation(50,210);
		msgButtton.setSize(100,40);
		msgButtton.setForeground(Color.black);
		JButton firiendList = new JButton("好友列表");
		firiendList.setLocation(300,210);
		firiendList.setSize(100,40);
		firiendList.setForeground(Color.black);
		chatRigthPlane.add(msgButtton);
		chatRigthPlane.add(firiendList);
		GameRoomUtil.SendMsgToServer(this, "getFriendUserList","");
	}

	/***
	* @Description:   初始化 聊天记录面板
	* @Param: []
	* @return: void
	* @Author: raven
	* @Date: 2020/3/29
	*/
	private void initChatPanle() {



		jscroll = new JScrollPane(jt);
		jscroll.setViewportView(jt);
		jscroll.setLocation(0,260);
		jscroll.setSize(450,530);
		jt.setLineWrap(true);        //激活自动换行功能
		jt.setWrapStyleWord(true);
		jt.setEditable(false);
		jt.setLocation(0,0);
		jt.setSize(450,750);
		jt.setBackground(new Color((int)0xE6E6FA));
		JTextField sendtext = new JTextField();
		sendtext.setLocation(10,850);
		sendtext.setSize(250,40);

		sendtext.setBackground(new Color((int)0xE6E6FA));
		jt.setFont(new Font("楷体", Font.PLAIN, 25));
		sendtext.setFont(new Font("楷体", Font.PLAIN, 20));
		JLabel bq = new JLabel("发送消息:");
		bq.setForeground(Color.pink);
		bq.setSize(200,30);
		bq.setLocation(10,800);
		chatRigthPlane.add(bq);
		JButton send = new JButton("Send");
		send.setLocation(300,835);
		send.setSize(60,60);
		send.setForeground(Color.pink);
		chatRigthPlane.add(jscroll);
		chatRigthPlane.add(send);
		chatRigthPlane.add(sendtext);

		add(chatRigthPlane);
		jt.setForeground(new Color(	0,250,154));
		jt.append("系统："+"\n"+"   欢迎加入游戏厅，希望来到这里能给你带来快乐，与室友一起组队开黑吧~~\n");
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
					GameRoomUtil.SendMsgToServer(priwid, "CloseLingoutGameRoom", null);
					priwid.setVisible(true);
					ReceiveMsgThread.readFlag = false;
					dispose();
			}
		});
		setVisible(true);
		send.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(sendtext.getText().equals("")) {
					JOptionPane.showMessageDialog(null, "不要发送空消息~~");
					return;
				}
				jt.append(BeginWindow.userPlayer.getNickName()+"："+dateFormat.format(new Date())+"\n"+"   "+sendtext.getText()+"\n");
				JSONObject msg = new JSONObject();
				msg.put("from", BeginWindow.userPlayer.getNickName());
				msg.put("msg", sendtext.getText());
				GameRoomUtil.SendMsgToServer(GamePlane.chessBoard, "salaChat",msg.toJSONString());
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
				 if(e.getKeyChar() == KeyEvent.VK_ENTER ) {
						send.doClick();
	                }
			}});
	}
	/***
	* @Description: 初始化游戏在线用户列表表格
	* @Param: []
	* @return: void
	* @Author: raven
	* @Date: 2020/3/29
	*/
	private void initUsersTable() throws InterruptedException {
		JSONObject send = new JSONObject();
		send.put("msgType","getAllUserInfo");
		GameRoomUtil.SendMsgToServer(this, "getAllUserInfo","");
		cellData = new Object[][]{};
		columnNames = new String[]{"账号", "昵称", "积分", "胜率"};
		tableModel = new DefaultTableModel(cellData, columnNames);

		gameUsersTable = new JTable(tableModel){
			//表格不允许被编辑
			public boolean isCellEditable(int row, int column) {
				return false;
			}

		};

		gameUsersTable.addMouseListener(
				new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						// 获取鼠标右键选中的行
						int row = gameUsersTable.rowAtPoint(e.getPoint());
						if (row == -1) {
							return;
						}
						// 获取已选中的行
						int[] rows = gameUsersTable.getSelectedRows();
						boolean inSelected = false;
						// 判断当前右键所在行是否已选中
						for (int r : rows) {
							if (row == r) {
								inSelected = true;
								break;
							}
						}
						// 当前鼠标右键点击所在行不被选中则高亮显示选中行
						if (!inSelected) {
							gameUsersTable.setRowSelectionInterval(row, row);
						}
						JPopupMenu popupMenu = new JPopupMenu();
						JMenuItem addUser = new JMenuItem("添加为好友");
						JMenuItem playgame = new JMenuItem("邀请游戏");
						class MenuItemListener extends MouseAdapter {
							private String type;
							public MenuItemListener(String type){
								this.type = type;
							}
							@Override
							public void mousePressed(MouseEvent e) {
								String username = (String) gameUsersTable.getValueAt(row, 0);
								String nickname = (String) gameUsersTable.getValueAt(row, 1);
								if (BeginWindow.userPlayer.getUserName().equals(username)) {
									JOptionPane.showMessageDialog(null, "不能对自己进行操作哦");
									return;
								}

								if(type.equals("添加")) {
									// forEach方法的return 相当于continue
									for (JSONObject u : userfirends) {

										if(u.getString("userName").equals(username)){
											JOptionPane.showMessageDialog(null, nickname+"已经是您的好友了！");
											return;
										}
									}

									JSONObject send = new JSONObject();
									send.put("fromUserName",BeginWindow.userPlayer.getUserName());
									send.put("toNickName",nickname);
									send.put("toUserName",username);
									GameRoomUtil.SendMsgToServer(null, "addFriend",send.toJSONString());
									//JOptionPane.showMessageDialog(null, "已向用户："+nickname+"发送添加请求！");
									System.out.println("已发送添加请求"+gameUsersTable.getValueAt(row,0));
								}else if(type.equals("邀请")){
									if(ChessBoard.gamepanel.kaishi){
										JOptionPane.showMessageDialog(LoginFream.bWindow, "游戏已经开始了,请先完成本局游戏才可以邀请对方哦~");
										return;
									}
									JSONObject send = new JSONObject();
									send.put("fromUserName",BeginWindow.userPlayer.getUserName());
									send.put("toNickName",nickname);
									send.put("toUserName",username);
									GameRoomUtil.SendMsgToServer(null, "invitationGame",send.toJSONString());
									//JOptionPane.showMessageDialog(null, "已邀请用户："+nickname+"，请稍后....");
									System.out.println("已邀请用户："+gameUsersTable.getValueAt(row,0)+"，请稍后....");
								}
							}
						}
						addUser.addMouseListener(new MenuItemListener("添加"));
						playgame.addMouseListener(new MenuItemListener("邀请"));
						popupMenu.add(addUser);
						popupMenu.add(playgame);
						popupMenu.show(e.getComponent(),e.getX(),e.getY());
					}
				});

		JScrollPane jScrollPane = new JScrollPane(gameUsersTable);
		jScrollPane.setSize(450,200);
		jScrollPane.setLocation(0,0);
		chatRigthPlane.add(jScrollPane);
	}


};

