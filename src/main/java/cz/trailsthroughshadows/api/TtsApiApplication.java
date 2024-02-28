package cz.trailsthroughshadows.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

@Slf4j
@EnableCaching
@SpringBootApplication
@PropertySource("classpath:validation.properties")
@PropertySource("classpath:image.properties")
@ComponentScan(basePackages = "cz.trailsthroughshadows")
@ConfigurationPropertiesScan(basePackages = "cz.trailsthroughshadows")
public class TtsApiApplication {

    public static void main(String[] args) {

        // jdbc:mariadb://49.13.93.112:3306/tts_api

        SpringApplication app = new SpringApplication(TtsApiApplication.class);

        app.run(args);

        log.info("Docs running on http://localhost:8080/swagger-ui/index.html");
        log.info("API running on http://localhost:8080/");
    }

}
