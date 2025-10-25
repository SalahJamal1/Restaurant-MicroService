package com.payment.app;

import com.stripe.exception.StripeException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class PaymentApplication {
    public static void main(String[] args) throws StripeException {
        SpringApplication.run(PaymentApplication.class, args);

    }

}
