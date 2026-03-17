package com.hkhj4.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TBFeedBack {
    int id;
    String call;
    String info;
    LocalDateTime createTime;
    LocalDateTime updateTime;
}
