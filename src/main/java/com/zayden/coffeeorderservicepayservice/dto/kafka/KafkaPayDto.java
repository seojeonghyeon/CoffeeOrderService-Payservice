package com.zayden.coffeeorderservicepayservice.dto.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class KafkaPayDto implements Serializable {
    private Schema schema;
    private PayloadPay payload;
}
