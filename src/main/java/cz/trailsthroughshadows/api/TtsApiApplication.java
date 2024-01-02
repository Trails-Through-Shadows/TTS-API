package cz.trailsthroughshadows.api;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@Log4j2
@EnableCaching
@SpringBootApplication
public class TtsApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(TtsApiApplication.class, args);

        log.info("Docs running on http://localhost:8080/swagger-ui/index.html");
        log.info("API running on http://localhost:8080/");
    }

}
