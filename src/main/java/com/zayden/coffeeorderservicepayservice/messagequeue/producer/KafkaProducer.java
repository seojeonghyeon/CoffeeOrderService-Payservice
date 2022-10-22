package com.zayden.coffeeorderservicepayservice.messagequeue.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Service
@Slf4j
public class KafkaProducer {
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    public KafkaProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(String topic, Object object){
        ObjectMapper mapper = new ObjectMapper();
        String jsonInString = "";
        try {
            jsonInString = mapper.writeValueAsString(object);
            executeProducerCallback(kafkaTemplate, topic, jsonInString);
        }catch (JsonProcessingException ex){
            ex.printStackTrace();
        }
    }

    public void executeProducerCallback(KafkaTemplate<String, String> kafkaTemplate, String topic, String jsonInString){
        ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, jsonInString);
        future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
            @Override
            public void onFailure(Throwable ex) {
                log.info("UNABLE TO SEND MESSAGE = [ TOPIC : " + topic + "  JSONSTRING : " + jsonInString
                        + " ] due to : "+ex.getMessage());
            }
            @Override
            public void onSuccess(SendResult<String, String> result) {
                log.info("SEND MESSAGE = [TOPIC : " + topic + "  JSONSTRING : " + jsonInString
                        + " ] with offset = [ "+ result.getRecordMetadata().offset() + " ]");
            }
        });
    }
}
