package cz.trailsthroughshadows.algorithm.validation;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "validation")
public class ValidationConfig {

    private HexGrid hexGrid;

    @Data
    public static class HexGrid {
        private int maxHexes;
        private int minHexes;
        private int maxWidth;
    }
}
