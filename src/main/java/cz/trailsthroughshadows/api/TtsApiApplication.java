package cz.trailsthroughshadows.api;

import cz.trailsthroughshadows.api.table.clazz.Clazz;
import cz.trailsthroughshadows.api.table.clazz.ClazzRepo;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Log4j2
public class TtsApiApplication implements ApplicationRunner {

    @Autowired
    private ClazzRepo clazz;

    public static void main(String[] args) {
        SpringApplication.run(TtsApiApplication.class, args);
        log.info("http://localhost:8080/swagger-ui/index.html");

    }


    @Override
    public void run(ApplicationArguments args) throws Exception {
        Clazz claz = new Clazz(53,"reeeeee",10);
        log.info(claz);

        clazz.save(claz);
        claz.setName("aaaaaaaaa");
        clazz.save(claz);
        clazz.flush();
    }
}
