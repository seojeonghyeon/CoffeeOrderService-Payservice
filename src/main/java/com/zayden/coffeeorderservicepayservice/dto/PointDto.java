package com.zayden.coffeeorderservicepayservice.dto;

import lombok.Data;

@Data
public class PointDto {
    private String payId;
    private String userId;
    private Integer amount;
    private String payStatus;
}
