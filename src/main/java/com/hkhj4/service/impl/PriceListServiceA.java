package com.hkhj4.service.impl;

import com.hkhj4.entity.PriceListItem;
import com.hkhj4.service.PriceListService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PriceListServiceA implements PriceListService {
    @Override
    public List<PriceListItem> get() {
        return List.of();
    }
}
