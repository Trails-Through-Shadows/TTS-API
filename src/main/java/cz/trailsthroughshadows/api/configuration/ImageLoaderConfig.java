package cz.trailsthroughshadows.api.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "image-loader")
public class ImageLoaderConfig {

    private static String path;

    private static String url;

}
