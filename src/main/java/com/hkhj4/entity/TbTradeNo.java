package com.hkhj4.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TbTradeNo {
    Integer id;
    String tradeNo;
    String userEmail;
    Integer premiumId;
    String PayType;
    Integer payState;
    LocalDateTime createTime;
    LocalDateTime updateTime;
}
