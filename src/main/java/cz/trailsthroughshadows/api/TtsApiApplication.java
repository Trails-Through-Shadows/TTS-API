package cz.trailsthroughshadows.api;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@EnableCaching
@SpringBootApplication
public class TtsApiApplication {

    public static void main(String[] args) {

//        ObjectMapper mapper = new ObjectMapper();
//        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        //.env file load
//        Dotenv dotenv = Dotenv.configure().directory(".config").load();
//
//        Map<String, Object> env = new HashMap<>();
//        env.put("spring.datasource.url", dotenv.get("DB_HOST") + ":" + dotenv.get("DB_PORT") + "/" + dotenv.get("DB"));
//        env.put("spring.datasource.driver-class-name", dotenv.get("DB_DRIVER"));
//        env.put("spring.datasource.username", dotenv.get("DB_USER"));
//        env.put("spring.datasource.password", dotenv.get("DB_PASSWORD"));
//        //jdbc:mariadb://49.13.93.112:3306/tts_api
//
//        SpringApplication app = new SpringApplication(TtsApiApplication.class);
//        app.setDefaultProperties(env);
//        app.run(args);
//        log.debug("Running with login: " + dotenv.get("DB_USER") + " -> " + dotenv.get("DB_PASSWORD"));


        SpringApplication.run(TtsApiApplication.class, args);
        log.info("Docs running on http://localhost:8080/swagger-ui/index.html");
        log.info("API running on http://localhost:8080/");
    }

}
