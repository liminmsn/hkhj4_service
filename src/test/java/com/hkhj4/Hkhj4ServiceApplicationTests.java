package com.hkhj4;

import com.hkhj4.pay.Spay;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class Hkhj4ServiceApplicationTests {
    @Test
    void contextLoads() {
        var a = new Spay();
        a.getPayData();
    }
}
