package server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import bean.User;
import server.dao.UserMapper;
@Service
public class UserServer {
	@Autowired
	UserMapper userMapper ;
	public boolean addUser(User user) {
		User user2 = userMapper.exituser(user.getUserName());
		if(user2!=null) {
			return false;
		}else {
			userMapper.addUser(user);
			return true;
		}
	
	}
	public User userLogin(User user) {
		
		
		return userMapper.userLogin(user);
		
	}
	public int update(User user) {
		return userMapper.update(user);
		
	}

}
