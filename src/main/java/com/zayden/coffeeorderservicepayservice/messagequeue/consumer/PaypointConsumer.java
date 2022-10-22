package com.zayden.coffeeorderservicepayservice.messagequeue.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.zayden.coffeeorderservicepayservice.dto.PointDto;
import com.zayden.coffeeorderservicepayservice.messagequeue.producer.PayProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
@Slf4j
public class PaypointConsumer {
    private Environment env;
    private PayProducer payProducer;

    @Autowired
    public PaypointConsumer(Environment env, PayProducer payProducer){
        this.env = env;
        this.payProducer = payProducer;
    }

    @KafkaListener(topics = "userservice-point-status")
    public void processUserPointPayment(String kafkaMessage){
        log.info("Before Pay Service : payment process");
        log.info("Kafka Message -> "+kafkaMessage);

        Map<Object, Object> map = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        try{
            map = mapper.readValue(kafkaMessage, new TypeReference<Map<Object, Object>>() {});
            PointDto pointDto = initPaypointDtoMapper(map);
            payProducer.send(env.getProperty("kafka.topics.pay-to-user-for-pay"), pointDto);
        }catch (JsonProcessingException ex){
            ex.printStackTrace();
        }

        log.info("After Pay Service : payment process");
    }
    private PointDto initPaypointDtoMapper(Map<Object, Object> map){
        LinkedHashMap<String, Object> payload = (LinkedHashMap<String, Object>)map.get("payload");

        PointDto pointDto = new PointDto();
        pointDto.setPayId((String)payload.get("pay_id"));
        pointDto.setUserId((String)payload.get("user_id"));
        pointDto.setAmount((Integer)payload.get("amount"));

        if(pointDto.getAmount() <= 0){
            pointDto.setPayStatus("CANCELED");
        }else if(pointDto.getAmount() > 50000){
            pointDto.setPayStatus("REJECTED");
        }else{
            pointDto.setPayStatus("CONFIRMED");
        }

        return pointDto;
    }

}
