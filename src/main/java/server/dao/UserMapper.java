package server.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import bean.User;

@Mapper
public interface UserMapper {
	@Insert("insert into user(userName,passWord,winingProbability,gameBout,nickName) values(#{userName},#{passWord}"
			+",#{winingProbability},#{gameBout},#{nickName})")
	void addUser(User user);
	@Select("select *from user  where userName=#{userName} and passWord=#{passWord}")
	User userLogin(User user);
	
	@Update("update user set winingProbability=#{winingProbability},gameBout=#{gameBout} where userName=#{userName}")
	int update(User user);
	
	@Select("select *from user  where userName=#{userName}")
	User exituser(String userName);
}
