package cz.trailsthroughshadows.api;

import cz.trailsthroughshadows.algorithm.test.Application;
import cz.trailsthroughshadows.api.configuration.ImageLoaderConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@EnableCaching
@SpringBootApplication
@Configuration
@EnableScheduling
@PropertySource("classpath:validation.properties")
@PropertySource("classpath:image.properties")
@ComponentScan(basePackages = "cz.trailsthroughshadows")
@ConfigurationPropertiesScan(basePackages = "cz.trailsthroughshadows")
public class TtsApiApplication {


    private static ImageLoaderConfig imageLoaderConfig;

    public static void main(String[] args) {

        // jdbc:mariadb://49.13.93.112:3306/tts_api

        ConfigurableApplicationContext cx = SpringApplication.run(Application.class, args);

        String address = cx.getEnvironment().getProperty("server.address", "pičovina");
        Integer port = cx.getEnvironment().getProperty("server.port", Integer.class, 8080);

        if (address.equals("0.0.0.0")) {
            address = "localhost";
        }

        if (!address.startsWith("http://")) {
            address = "http://" + address;
        }

        log.info("Docs running on {}:{}/swagger-ui/index.html", address, port);
        log.info("API running on {}:{}", address, port);

//        imageLoaderConfig.setAddress(address);
//        imageLoaderConfig.setPort(port.toString());
    }

//    @Autowired
//    public void setImageLoaderConfig(ImageLoaderConfig imageLoaderConfig) {
//        TtsApiApplication.imageLoaderConfig = imageLoaderConfig;
//    }

}
