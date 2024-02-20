package cz.trailsthroughshadows.api.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;


@Data
@ConfigurationProperties(prefix = "image")
public class ImageLoaderConfig {

    private final String path;

    private final String url;

}
