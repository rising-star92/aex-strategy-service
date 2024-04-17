package com.walmart.aex.strategy.producer;

import com.walmart.aex.strategy.dto.PlanStrategyResponse;
import com.walmart.aex.strategy.dto.kafka.Headers;
import com.walmart.aex.strategy.enums.EventType;
import com.walmart.aex.strategy.properties.KafkaProperties;
import io.strati.ccm.utils.client.annotation.ManagedConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaProducerException;
import org.springframework.kafka.core.KafkaSendCallback;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

import java.time.Instant;

@Slf4j
@Component
public class StrategyProducer {

    private static final String SOURCE = "AEX-StrategyService";
    @Autowired
    private KafkaTemplate<Object, Object> kafkaTemplate;

    @ManagedConfiguration
    private KafkaProperties kafkaProperties;

    public void postStrategyChangesToKafka(PlanStrategyResponse msg, Headers headers, EventType eventType, Long planId) {
        Message<PlanStrategyResponse> message = MessageBuilder.withPayload(msg)
                .setHeader("source", SOURCE)
                .setHeader("changeScope", headers.getChangeScope())
                .setHeader("type", eventType)
                .setHeader(KafkaHeaders.TIMESTAMP, Instant.now().getEpochSecond()).build();

        ListenableFuture<SendResult<Object, Object>> future = kafkaTemplate.send(kafkaProperties.getStrategyServiceTopic(),planId.toString(), message);
        future.addCallback(new KafkaSendCallback<Object, Object>() {
            @Override
            public void onSuccess(SendResult<Object, Object> result) {
                log.info("Strategy Changes payload sent to kafka is : {}", result.getProducerRecord().value());
                RecordMetadata metadata = result.getRecordMetadata();
                log.info(
                        "Strategy Changes event sent successfully with the following information : topic - {} , "
                                + "partition  - {} , offset - {}",
                        metadata.topic(), metadata.partition(), metadata.offset());
            }

            @Override
            public void onFailure(KafkaProducerException e) {
                log.error("Exception occurred when posting Strategy changes information to kafka ={}", e);
            }
        });
    }
}
