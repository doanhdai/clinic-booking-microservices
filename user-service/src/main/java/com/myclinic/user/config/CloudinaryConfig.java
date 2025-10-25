package com.myclinic.user.config;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.github.cdimascio.dotenv.Dotenv;
@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary() {
        Dotenv dotenv = Dotenv.load();
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", dotenv.get("CLOUDINARY_CLOUD_NAME"),
                "api_key", dotenv.get("CLOUDINARY_API_KEY"),
                "api_secret", dotenv.get("CLOUDINARY_API_SECRET"),
                "secure", true
        ));
    }
}
