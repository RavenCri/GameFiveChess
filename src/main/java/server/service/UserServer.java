package server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import common.pojo.User;
import server.dao.UserMapper;

import java.util.List;

@Service

public class UserServer {
	@Autowired
	UserMapper userMapper ;
	public boolean addUser(User user) {
		User user2 = userMapper.getUserByUserName(user.getUserName());
		if(user2!=null) {
			return false;
		}else {
			// 初试积分1000
			user.setIntegral(1000);
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
	public User getUser(String userName) {
		User selectUser = userMapper.getUserByUserName(userName);
		selectUser.setPassWord("");
		return selectUser;
	}
	public void addFriend(String username1,String username2){
		int userId1 = getUser(username1).getId();
		int userId2 = getUser(username2).getId();
		userMapper.addFriend(userId1,userId2);

		userMapper.addFriend(userId2,userId1);
	}
	public List<User> getUserFriendByUserName(String username){
		return userMapper.getUserFriendByUserName(username);
	}
}
