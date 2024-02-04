package cz.trailsthroughshadows;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "validation")
public class ValidationConfig {

    private final HexGrid hexGrid;

    @Data
    public static class HexGrid {
        private int minHexes;
        private int maxHexes;
        private int maxWidth;
    }

}