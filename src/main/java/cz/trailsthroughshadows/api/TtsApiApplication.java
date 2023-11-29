package cz.trailsthroughshadows.api;

import cz.trailsthroughshadows.api.table.character.clazz.Clazz;
import cz.trailsthroughshadows.api.table.character.clazz.ClazzRepo;
import cz.trailsthroughshadows.api.table.schematic.location.Location;
import cz.trailsthroughshadows.api.table.schematic.location.LocationRepo;
import cz.trailsthroughshadows.api.table.schematic.part.LocationPart;
import cz.trailsthroughshadows.api.table.schematic.part.Part;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication
@Log4j2
public class TtsApiApplication implements ApplicationRunner {

    @Autowired
    private ClazzRepo clazz;

    @Autowired
    private LocationRepo location;

    public static void main(String[] args) {
        SpringApplication.run(TtsApiApplication.class, args);
        log.info("http://localhost:8080/swagger-ui/index.html");

    }


    @Override
    public void run(ApplicationArguments args) throws Exception {
//        Clazz claz = new Clazz(53,"reeeeee",10);
//        log.info(claz);
//
//        clazz.save(claz);
//        claz.setName("aaaaaaaaa");
//        clazz.save(claz);
//        clazz.flush();

        Location loc = location.findById(2).orElse(null);
        List<LocationPart> parts = loc.getParts();
        log.info(parts);
    }
}
