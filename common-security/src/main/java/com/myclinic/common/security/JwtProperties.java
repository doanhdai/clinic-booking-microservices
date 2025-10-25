package com.myclinic.common.security;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component

@Slf4j
public class JwtProperties {

    private String secret;
    private long expiration;

    @PostConstruct
    public void init() {
        Dotenv dotenv = Dotenv.configure()
                .directory("./") // Tìm trong thư mục gốc project
                .ignoreIfMissing() // Không crash nếu không có .env
                .load();

        this.secret = dotenv.get("JWT_SECRET", "defaultSecretKeyForDevelopmentOnlyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyy");
        this.expiration = Long.parseLong(dotenv.get("JWT_EXPIRATION", "86400000"));

        logConfiguration();
    }

    private void logConfiguration() {
        if (secret.equals("defaultSecretKeyForDevelopmentOnly")) {
            log.warn("🚨 JWT_SECRET is not set in .env file. Using default secret (NOT SAFE FOR PRODUCTION)");
        } else {
            log.info("✅ JWT_SECRET loaded from .env file (length: {})", secret.length());
        }

        log.info("🕒 JWT Expiration: {} ms ({} hours)", expiration, expiration / (1000 * 60 * 60));
    }
}