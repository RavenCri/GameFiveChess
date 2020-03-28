package client.handle;

import java.io.IOException;

import javax.swing.JOptionPane;

import org.springframework.util.DigestUtils;

import com.alibaba.fastjson.JSONObject;

import client.window.BeginWindow;
import client.window.LoginFream;
import client.window.Room;

/** 
 * @ClassName: ReceiveHandleMsg
 * @description: 
 * @author:Raven
 * @Date: 2019年10月20日 下午12:19:18
 */

public class ReceiveMsgThread extends Thread{
	public static boolean readFlag ;
	@Override
	public void run() {
		
		while (true) {
			JSONObject receive =  ReceiveMSg();
			
			try {
				if(receive != null && readFlag) {
					Room.msgQueue.put(receive);
				}else {
					readFlag = false;
					System.out.println("停止了读取消息线程");
					break;
				}
					
			
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public static JSONObject ReceiveMSg(){
		JSONObject msgJson;
		String resMsg;
		try {
			resMsg = BeginWindow.in.readLine();
			msgJson = JSONObject.parseObject(resMsg);
			System.out.println("收到："+msgJson);
			//消息类型
			String msgType = msgJson.getString("msgType");
			//消息数据
			String msgData =  msgJson.getString("msg");

			String securityCheck =  msgJson.getString("securityCheck");
			//消息验证
			
			if(DigestUtils.md5DigestAsHex((msgType +msgData).getBytes()).equals(securityCheck)) {
				
				return msgJson;
			}else {
				System.out.println("消息验证错误");
			}
		} catch (IOException e) {
			if(LoginFream.room !=null) {
				LoginFream.room.setVisible(false);
				LoginFream.room.dispose();
				if(LoginFream.room.priwid !=null) {
					LoginFream.room.priwid.setVisible(false);
					LoginFream.room.priwid.dispose();
				}
			}
			
			if(Room.chessBoard != null) {
				Room.chessBoard.setVisible(false);
				Room.chessBoard.dispose();
			}
			LoginFream.bWindow.setVisible(true);
			JOptionPane.showMessageDialog(LoginFream.bWindow, "与服务器连接异常，您已退出了游戏。");
			
			
			
		}
		return null;
	}
}
 