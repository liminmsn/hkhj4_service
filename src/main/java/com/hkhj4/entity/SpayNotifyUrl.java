package com.hkhj4.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpayNotifyUrl {
    String pid;
    String name;
    String money;
    String out_trade_no;
    String trade_no;
    String param;
    String trade_status;
    String type;
    String sign;
    String sign_type;
}
