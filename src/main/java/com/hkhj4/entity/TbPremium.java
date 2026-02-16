package com.hkhj4.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TbPremium {
    Integer id;
    double price;
    double priceDiscount;
    String priceLabel;
    String priceDescription;
    short day;
}
