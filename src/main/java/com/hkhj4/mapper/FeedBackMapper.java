package com.hkhj4.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FeedBackMapper {
    int submit(String call,String info);
}
