package com.ecommerce.user.service.Impl;

import com.ecommerce.user.model.OtpMessage;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class OtpProducer {

    private final KafkaTemplate<String, com.ecommerce.user.model.OtpMessage> kafkaTemplate;

    public OtpProducer(KafkaTemplate<String, OtpMessage> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendOtp(String email, String otp) {
        OtpMessage message = new OtpMessage(email, otp);
        kafkaTemplate.send("otp-topic", message);
    }
}