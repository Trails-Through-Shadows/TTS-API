package cz.trailsthroughshadows.algorithm.validation;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "validation")
public class ValidationConfig {

    private final HexGrid hexGrid;

    private final Description description;

    private final Title title;

    private final Tag tag;

    @Data
    public static class HexGrid {
        private int minHexes;

        private int maxHexes;

        private int maxWidth;
    }

    @Data
    public static class Description {
        private int maxLen;

        private String allowedChars;
    }

    @Data
    public static class Title {
        private int minLen;

        private int maxLen;

        private String allowedChars;
    }

    @Data
    public static class Tag {
        private int minLen;

        private int maxLen;

        private String allowedChars;

        private String prefix;
    }


}