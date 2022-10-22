package com.zayden.coffeeorderservicepayservice.dto.kafka;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PayloadPay {
    private String pay_id;
    private String user_id;
    private int amount;
    private String status;
}
