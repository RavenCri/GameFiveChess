package client.tidings;

import java.io.IOException;

import org.springframework.util.DigestUtils;

import com.alibaba.fastjson.JSONObject;

import client.window.BeginWindow;
import client.window.Room;

/** 
 * @ClassName: ReceiveHandleMsg
 * @description: 
 * @author:Raven
 * @Date: 2019年10月20日 下午12:19:18
 */

public class ReceiveMsgThread extends Thread{
	@Override
	public void run() {
		
		while (true) {
			JSONObject receive =  ReceiveMSg();
			
			try {
				Room.msgQueue.put(receive);
			
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
 