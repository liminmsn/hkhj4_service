package com.hkhj4.mapper;

import com.hkhj4.entity.TbUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {
    /**
     * 获取用户信息
     */
    @Select("select * from tb_user where email=#{email} and password=#{password}")
    TbUser getUserInfo(String email,String password);
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
