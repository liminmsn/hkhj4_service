package com.hkhj4.controller;

import com.hkhj4.mapper.FeedBackMapper;
import com.hkhj4.utily.Result;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Tag(name = "用户反馈")
public class FeedBack {
    @Resource
    FeedBackMapper feedBackMapper;

    @GetMapping("/api/feed_back/create")
    Result createFeedBack(@RequestParam String call, @RequestParam String info) {
        int i = feedBackMapper.submit(call, info);
        log.info("{}", i);
        return Result.success(200, "反馈提交成功!");
    }
}
