package com.hkhj4.controller;

import com.hkhj4.utils.Result;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @RequestMapping("/hello")
    Result hello(){
        return  Result.success("hello world");
    }
}
