package server.dao;

import org.apache.ibatis.annotations.*;

import common.pojo.User;

import java.util.List;

@Mapper
public interface UserMapper {
	@Insert("insert into user(id,userName,passWord,winingProbability,gameBout,nickName,winBout,integral) values(0,#{userName},#{passWord}"
			+",#{winingProbability},#{gameBout},#{nickName},#{winBout},#{integral})")
	void addUser(User user);
	@Select("select *from user  where userName=#{userName} and passWord=#{passWord}")
	User userLogin(User user);
	
	@Update("update user set winingProbability=#{winingProbability},gameBout=#{gameBout},winBout=#{winBout},integral=#{integral} where userName=#{userName}")
	int update(User user);
	

	
	@Select("select *from user where userName=#{userName}")
	User getUserByUserName(String userName);

	@Select("select nickName,userName from user where id in(select friend_id from game_friend where user_id in (select id from user where username=#{userName}))")
	List<User> getUserFriendByUserName(String username);

	@Insert("insert into game_friend(id,user_id,friend_id) values(0,#{userId1},#{userId2})")

	void addFriend(@Param("userId1") int userId1, @Param("userId2")int userId2);
}
