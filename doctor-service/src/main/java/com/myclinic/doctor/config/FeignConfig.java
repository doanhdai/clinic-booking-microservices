package com.myclinic.doctor.config;

import com.myclinic.common.security.FeignJwtInterceptor;
import feign.Logger;
import feign.Request;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableFeignClients
public class FeignConfig {

    @Bean
    public FeignJwtInterceptor feignJwtInterceptor() {
        return new FeignJwtInterceptor();
    }
//    @Bean
//    public Logger.Level feignLoggerLevel() {
//        return Logger.Level.FULL;
//    }
//
//    @Bean
//    public Request.Options requestOptions() {
//        return new Request.Options(
//                5000, TimeUnit.MILLISECONDS,
//                5000, TimeUnit.MILLISECONDS,
//                true
//        );
//    }
}