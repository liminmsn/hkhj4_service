package com.hkhj4.controller;

import com.hkhj4.entity.PriceListItem;
import com.hkhj4.service.impl.PriceListServiceA;
import com.hkhj4.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.awt.*;
import java.util.List;

@RestController
public class PriceListController {
    @Autowired
    private PriceListServiceA priceListServiceA;

    @RequestMapping("/priceList")
    Result priceList() {
        List<PriceListItem> list = priceListServiceA.get();
        return Result.success(list);
    }
}
