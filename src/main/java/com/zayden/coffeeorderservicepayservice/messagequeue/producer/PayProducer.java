package com.zayden.coffeeorderservicepayservice.messagequeue.producer;


import com.zayden.coffeeorderservicepayservice.dto.PointDto;
import com.zayden.coffeeorderservicepayservice.dto.kafka.Field;
import com.zayden.coffeeorderservicepayservice.dto.kafka.KafkaPayDto;
import com.zayden.coffeeorderservicepayservice.dto.kafka.PayloadPay;
import com.zayden.coffeeorderservicepayservice.dto.kafka.Schema;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class PayProducer {
    private KafkaTemplate<String, String> kafkaTemplate;


    List<Field> fields = Arrays.asList(
            new Field("string", true, "pay_id"),
            new Field("string", true, "user_id"),
            new Field("int32", true, "amount"),
            new Field("String", true, "status")
    );

    Schema schema = Schema.builder()
            .type("struct")
            .fields(fields)
            .optional(false)
            .name("pay")
            .build();

    @Autowired
    public PayProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public PointDto send(String topic, PointDto pointDto){
        PayloadPay payload = PayloadPay.builder()
                .pay_id(pointDto.getPayId())
                .user_id(pointDto.getUserId())
                .amount(pointDto.getAmount())
                .status(pointDto.getPayStatus())
                .build();
        KafkaPayDto kafkaPayDto = new KafkaPayDto(schema, payload);
        KafkaProducer kafkaProducer = new KafkaProducer(kafkaTemplate);
        kafkaProducer.send(topic, kafkaPayDto);
        log.info("Point Producer send data for pay from the User microservice"+kafkaPayDto);
        return pointDto;
    }



}
