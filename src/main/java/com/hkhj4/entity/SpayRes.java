package com.hkhj4.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpayRes {
    int code;
    String msg;
    String O_id;
    String trade_no;
    String payurl;
    String payurl2;
    String qrcode;
    String img;
}
