package server.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import common.pojo.User;

@Mapper
public interface UserMapper {
	@Insert("insert into user(userName,passWord,winingProbability,gameBout,nickName,winBout) values(#{userName},#{passWord}"
			+",#{winingProbability},#{gameBout},#{nickName},#{winBout})")
	void addUser(User user);
	@Select("select *from user  where userName=#{userName} and passWord=#{passWord}")
	User userLogin(User user);
	
	@Update("update user set winingProbability=#{winingProbability},gameBout=#{gameBout},winBout=#{winBout} where userName=#{userName}")
	int update(User user);
	
	@Select("select *from user  where userName=#{userName}")
	User exituser(String userName);
	
	@Select("select *from user where userName=#{userName}")
	User getUserByUserName(String userName);
}
