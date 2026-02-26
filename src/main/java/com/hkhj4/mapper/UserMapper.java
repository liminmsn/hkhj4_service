package com.hkhj4.mapper;

import com.hkhj4.entity.TbUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {
    /**
     * 登录
     */
    @Select("select email from tb_user where email=#{email} and password=#{password}")
    TbUser userLogin(String email,String password);
    /**
     * 获取用户信息
     */
    @Select("select username,email,gender,image,register_time from tb_user where email=#{email}")
    TbUser getUserInfo(String email);
    /**
     * 判断邮箱是否已经注册
     */
    @Select("select count(*) from tb_user where trim(email) = trim(#{email})")
    int countEmail(String email);

    /**
     * 创建用户
     */
    int createUser(TbUser tbUser);
}
