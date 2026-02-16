package com.hkhj4;

import com.hkhj4.entity.TbPremium;
import com.hkhj4.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class Hkhj4ServiceApplicationTests {
    @Autowired
    private UserMapper userMapper;

    @Test
    void contextLoads() {
        List<TbPremium> list = userMapper.list();
        System.out.println(list);
    }

}
