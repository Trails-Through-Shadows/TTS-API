package cz.trailsthroughshadows.algorithm;

import cz.trailsthroughshadows.api.TtsApiApplication;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Log4j2
public class Application extends TtsApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);

        log.info("Docs running on http://localhost:8080/swagger-ui/index.html");
        log.info("API running on http://localhost:8080/");
    }

    public Application() {
        super();
    }
}
