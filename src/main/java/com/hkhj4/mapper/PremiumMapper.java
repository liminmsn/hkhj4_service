package com.hkhj4.mapper;

import com.hkhj4.entity.TbMember;
import com.hkhj4.entity.TbPremium;
import com.hkhj4.entity.TbTradeNo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface PremiumMapper {
    @Select("select price,price_discount,price_label,price_descritpion,day from tb_premium")
    List<TbPremium> list();

    @Select("select price,price_discount,price_label,price_descritpion,day from tb_premium where #{id}")
    TbPremium getPremium(Integer id);

    @Select("select trade_no,user_email,premium_id,pay_type,pay_state,sign from tb_trade_no where trade_no=#{tradeNo} AND sign=#{sign}")
    TbTradeNo getTradeNo(String tradeNo, String sign);

    int createTradeNo(TbTradeNo tradeNo);

    @Select("select email,expire_time from tb_member where email=#{email}")
    TbMember getMembe(String email);
    @Update("update tb_member set expire_time=#{expire_time},update_time=NOW() where email=#{email}")
    int updateMember(String email, LocalDateTime expire_time);
    int createMember(TbMember tbMember);
}
