package client.window;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import javax.sound.midi.MidiDevice.Info;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.apache.ibatis.annotations.Mapper;

import com.alibaba.fastjson.JSONObject;

import bean.User;
import client.tidings.ReceiveMsgThread;
import util.GameRoomUtil;
import util.HttpConnectUtil;


public class LoginFream extends JFrame{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JCheckBox realizePassWorld;
	boolean flag=false;
	private JLabel jl = new JLabel("账号：");
	private JLabel jl2 = new JLabel("密码：");
	private JTextField textfield=new JTextField("请输入您的账号....",30);
	private JPasswordField textfield2=new JPasswordField();
	private JButton loninButton ,regignButt;
	private JLabel bg;
	private JPanel panel=new JPanel();
	
	private Icon loginIcon ;
	public boolean exit;
	public LoginFream() {
		
	}
	public static Room room;
	public LoginFream(BeginWindow bWindow){
		super("登录界面");
		SiteInit(this,500,400);
		//设置不能最大化
		this.setResizable(false);
		panel.setLayout(null);
		jl.setFont(new Font("宋体", Font.PLAIN, 20));
		jl.setForeground(Color.blue);
		jl.setLayout(null);
		jl.setBounds(50, 60, 120, 30);
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		textfield.setFont(new Font("宋体", Font.PLAIN, 20));
		textfield.setLayout(null);
		//mFrame.add(textfield); 
		textfield.setBounds(120, 60, 170, 30);
		
		

		jl2.setFont(new Font("宋体", Font.PLAIN, 20));
		jl2.setLayout(null);
		jl2.setForeground(Color.blue);
		jl2.setBounds(50, 120, 120, 30);
		

		textfield2.setFont(new Font("宋体", Font.PLAIN, 20));
		textfield2.setLayout(null);
		//mFrame.add(textfield2); 
		textfield2.setBounds(120, 120, 170, 30);
		
		loninButton = new JButton("登录");
		loninButton.setFont(new Font("微软雅黑", Font.PLAIN, 20));
		loninButton.setLayout(null);
		loninButton.setForeground(Color.red);
		loninButton.setBounds(70, 170, 110, 50);
		loninButton.setOpaque(false);
		loginIcon =new ImageIcon("source/btn.png");
		loninButton.setIcon(loginIcon);
		
		regignButt = new JButton("注册");
		regignButt.setFont(new Font("微软雅黑", Font.PLAIN, 20));
		regignButt.setLayout(null);
		regignButt.setForeground(Color.red);
		regignButt.setBounds(250, 170, 110, 50);
		regignButt.setOpaque(false);
		regignButt.setIcon(loginIcon);
		panel.add(jl);
		panel.add(jl2);
		panel.add(textfield);
		panel.add(textfield2);
		panel.add(loninButton);
		panel.add(regignButt);
		realizePassWorld = new JCheckBox("记住密码");
		realizePassWorld.setBounds(300,125,140,20);
		realizePassWorld.setOpaque(false);//使其透明
		panel.add(realizePassWorld);
		bg= new JLabel(new ImageIcon("source/bg.jpg")); 
		bg.setLayout(null);
		bg.setBounds(0,0,500,400);
		panel.add(bg);
		this.add(panel);
		
		textfield.addMouseListener(new MouseAdapter() {

		@Override
		public void mouseClicked(MouseEvent e) {
			JTextField field =  (JTextField) e.getSource();
			if(field.getText().contains("请输入您的账号....")){
				field.setText("");
			}
		}
	});
		loninButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(textfield.getText().trim().equals("")||String.copyValueOf(textfield2.getPassword()).trim().equals("")) {
					JOptionPane.showMessageDialog(null, "请填写完整再提交~");
				}
				Map<String, String> msg = new HashMap<String, String>();
				msg.put("userName", textfield.getText().trim());
				msg.put("passWord", String.copyValueOf(textfield2.getPassword()).trim());
				new Thread() {
					public void run() {
						
						String reqStr = HttpConnectUtil.requestHTTP(BeginWindow.host+"/login",msg,"get");
						
						try {
							JSONObject res =   (JSONObject) JSONObject.parse(reqStr);
							System.out.println(res.get("info"));
							User loginUser = (User) JSONObject.toJavaObject(res.getJSONObject("info"), User.class);
							// 如果登录成功
							if("true".equals(res.get("status"))) {
								
								try {
									BeginWindow.out = new BufferedWriter(new OutputStreamWriter(BeginWindow.socket.getOutputStream(),"UTF-8"));
									BeginWindow.in = new BufferedReader(new InputStreamReader(BeginWindow.socket.getInputStream(),"UTF-8"));
									// 发送登录状态 同时获取了游戏房间列表
									GameRoomUtil.SendMsgToServer(bWindow, "LoginGame",res.getString("info"));	
									JSONObject resJSON =  ReceiveMsgThread.ReceiveMSg();
									String msgType = resJSON.getString("msgType");
									String msg = resJSON.getString("msg");
									if(msgType.equals("AlreadyLogined") && msg.equals("0") ) {
										
										BeginWindow.userPlayer = loginUser;
										room = new Room(bWindow);
										room.setVisible(true);
										bWindow.loginstaus =true;
										dispose();
									}else {
										JOptionPane.showMessageDialog(null, "您的账号真的已经登录过了。如果非你本人操作，请修改密码！");
									}
									
								
								} catch (UnsupportedEncodingException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								} catch (IOException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
							}else {
								
								JOptionPane.showMessageDialog(null, "账号密码错误，要不先注册个吧~");
							}
						} catch (Exception e) {
							JOptionPane.showMessageDialog(null, "登录信息异常。。");
							e.printStackTrace();
						}
						
						
						
					};
				}.start();
				
				
			}
		});
		regignButt.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
			

				Desktop desktop = Desktop.getDesktop(); 
				try {
					desktop.browse(new URI(BeginWindow.host+"/"));
				} catch (URISyntaxException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				
			}
		});
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
		
			
				dispose();
				bWindow.setVisible(true);
					
				
			}
		});
	
	}
	
	
	
	boolean getflag(){
		return flag;
	}
	
	static void SiteInit(JFrame MyWindow,int width,int height){
		//得到系统类的一个对象
		Toolkit toolkit=Toolkit.getDefaultToolkit();
		//获取屏幕分辨率
		Dimension a = toolkit.getScreenSize();
		int x = (int)a.getWidth();
		int y = (int )a.getHeight();
		MyWindow.setBounds((x-width)/2, (y-height)/2, width, height);
		MyWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
		//MyWindow.setVisible(true);
	}
}
