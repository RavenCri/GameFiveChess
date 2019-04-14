package client;

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

import com.raven.main.BeginWindow;
import com.raven.main.Room;

import util.GameRoomUtil;
import util.HttpConnectUtil;


public class LoginFream extends JFrame{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JCheckBox realizePassWorld;
	boolean flag=false;
	JLabel jl = new JLabel("账号：");
	JLabel jl2 = new JLabel("密码：");
	JTextField textfield=new JTextField("请输入您的账号....",30);
	JPasswordField textfield2=new JPasswordField();
	JButton loninButton ,regignButt;
	JLabel bg;
	JPanel panel=new JPanel();
	
	Icon loginIcon ;
	public boolean exit;
	public LoginFream() {
		
	}
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
		loginIcon =new ImageIcon("image\\login.png");
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
		bg= new JLabel(new ImageIcon("image\\aa.jpg")); 
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
						
						String staus[] = HttpConnectUtil.requestHTTP(BeginWindow.host+"/login",msg,"get").split(":");
						
						
						if(staus[0].contains("登陆成功")) {
							System.out.println(staus[0]+"--"+staus[1]+"--"+staus[2]+"--"+staus[3]+"--"+staus[4]);
							try {
								BeginWindow.out = new BufferedWriter(new OutputStreamWriter(BeginWindow.socket.getOutputStream(),"UTF-8"));
								BeginWindow.in = new BufferedReader(new InputStreamReader(BeginWindow.socket.getInputStream(),"UTF-8"));
								GameRoomUtil.SendToServerMsg(bWindow, "MSGTYPE:username#"+staus[2]+"%"+staus[1]+"\r\n");	
								GameRoomUtil.ResultMsg();
								
								BeginWindow.nickName = staus[1];
								BeginWindow.username = staus[2];
								BeginWindow.winlv = Double.valueOf(staus[3]);
								BeginWindow.gamebout = Integer.valueOf(staus[4].trim());
								Room room = new Room(bWindow);
								room.setVisible(true);
								bWindow.loginstaus =true;
								dispose();
							} catch (UnsupportedEncodingException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}else {
							System.out.println(staus[0]);
							JOptionPane.showMessageDialog(null, "账号密码错误，要不先注册个吧~");
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
