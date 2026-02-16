package com.hkhj4.mapper;

import com.hkhj4.entity.TbPremium;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserMapper {
    @Select("select * from tb_premium")
    List<TbPremium> list();
}
