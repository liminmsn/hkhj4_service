package com.hkhj4.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TbUser {
    Integer id;
    String username;
    String password;
    String email;
    String image;
    short gender;
    LocalDateTime registerTime;
    LocalDateTime createTime;
    LocalDateTime updateTime;
}
