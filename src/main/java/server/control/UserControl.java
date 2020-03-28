package server.control;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;

import common.pojo.User;
import server.service.UserServer;

@Controller
public class UserControl {
	@Autowired
	UserServer userServer;
	@PostMapping("/adduser")
	@ResponseBody
	public String addUser(User user){
		System.out.println(user);
		if(user.getUserName()!=null&&!user.getUserName().equals("")
				&&user.getPassWord()!=null&&!user.getPassWord().equals("")
				&&user.getNickName()!=null&&!user.getNickName().equals("")) {
			boolean staus = userServer.addUser(user);
			if(staus) {
				return "注册成功！";
			}else {
				return "用户名已存在注册失败";
			}
		}else {
			return "信息填写不完整~注册失败";
		}
	
		
	}
	//springboot不支持使用post方式直接访问静态资源
		//@GetMapping 与 @PostMapping 不能同时使用
	@GetMapping({"/",""})
	public String index() {

		return "index.html";
	}
	@PostMapping({"","/"})
	public String index2() {

		return "index.html";
	}
	
	@RequestMapping("/login")
	@ResponseBody
	public Object login(User user) {
		
		User userInfo = userServer.userLogin(user);
		

		JSONObject res = new JSONObject();
		String status;
		if(userInfo!=null) {
			userInfo.setPassWord("");
			res.put("info",userInfo);
			status = "true";
		}else {
			status = "false";
		}
		res.put("status", status);
		
		return res;
	}
	@RequestMapping("/update")
	@ResponseBody
	public int update(User user) {
		return userServer.update(user);
	}
}
