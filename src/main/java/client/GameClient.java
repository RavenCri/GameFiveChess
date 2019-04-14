package client;



import javax.swing.UIManager;
import com.raven.main.BeginWindow;

/**
 * 
 * @author Raven
 * @date 下午5:53:46
 * @version
 *  	游戏启动类
 */
public class GameClient {
	public static BeginWindow beginWindow;
	public static String MSG ="";
	public static String onlineGameMSG;
	public static String namerepeatMSG;
	public static String roomFullOrRoomDistroyMSG;
	public static void main(String[] args) {
		/*
		 * 
		 *   	com.jtattoo.plaf.noire.NoireLookAndFeel 柔和黑
			    com.jtattoo.plaf.smart.SmartLookAndFeel 木质感+xp风格
			    com.jtattoo.plaf.mint.MintLookAndFeel 椭圆按钮+黄色按钮背景
			    com.jtattoo.plaf.mcwin.McWinLookAndFeel 椭圆按钮+绿色按钮背景
			    com.jtattoo.plaf.luna.LunaLookAndFeel 纯XP风格
			    com.jtattoo.plaf.hifi.HiFiLookAndFeel 黑色风格
			    com.jtattoo.plaf.fast.FastLookAndFeel 普通swing风格+蓝色边框
			    com.jtattoo.plaf.bernstein.BernsteinLookAndFeel 黄色风格
			    com.jtattoo.plaf.aluminium.AluminiumLookAndFeel 椭圆按钮+翠绿色按钮背景+金属质感（默认）
			    com.jtattoo.plaf.aero.AeroLookAndFeel xp清新风格
			    com.jtattoo.plafacryl.AcrylLookAndFeel 布质感+swing纯风格
		 */
	/*	 try {
	            ////////////////////////---------------------------------- select Look and Feel(下面就是要改变的地方了)
	            JFrame.setDefaultLookAndFeelDecorated(true);
	            UIManager.setLookAndFeel("com.jtattoo.plaf.mcwin.McWinLookAndFeel"); 
	            ////////////////////////---------------------------------- start application
	          
	        }
	        catch (Exception ex) {
	            ex.printStackTrace();
	        }*/
	     
		
		 try {
			
			UIManager.put("RootPane.setupButtonVisible", false);
			org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper.launchBeautyEyeLNF();
			
			beginWindow= new BeginWindow();
			
			
		
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	
		
	}
}
