package com.ecommerce.user.service.Impl;

import com.ecommerce.user.model.OtpMessage;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @KafkaListener(topics = "otp-topic", groupId = "otp-group")
    public void consumeOtp(OtpMessage otpMessage) throws MessagingException {

        String toEmail = otpMessage.getEmail();
        String otp = otpMessage.getOtp();

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(toEmail);
        helper.setSubject("Your OTP Code");

        String htmlContent = "<h2>Your OTP is: " + otp + "</h2>";

        helper.setText(htmlContent, true);

        mailSender.send(message);
    }
}