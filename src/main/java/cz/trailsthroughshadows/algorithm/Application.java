package cz.trailsthroughshadows.algorithm;

import cz.trailsthroughshadows.api.TtsApiApplication;
import cz.trailsthroughshadows.api.table.schematic.location.Location;
import cz.trailsthroughshadows.api.table.schematic.location.LocationRepo;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Log4j2
public class Application extends TtsApiApplication implements CommandLineRunner  {
    @Autowired
    LocationRepo locationRepo;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("Docs running on http://localhost:8080/swagger-ui/index.html");
        log.info("API running on http://localhost:8080/");

        Location loc = locationRepo.findById(1).get();

        log.info(loc);

    }
}
