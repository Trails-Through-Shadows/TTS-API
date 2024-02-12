package cz.trailsthroughshadows.algorithm.validation;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "validation")
public class ValidationConfig {

    @Data
    public static class HexGrid {
        private int minHexes;

        private int maxHexes;

        private int maxWidth;
    }
    private final HexGrid hexGrid;

    @Data
    public static class Description {
        private int maxLen;

        private String allowedChars;
    }
    private final Description description;

    @Data
    public static class Title {
        private int minLen;

        private int maxLen;

        private String allowedChars;
    }
    private final Title title;

    @Data
    public static class Tag {
        private int minLen;

        private int maxLen;

        private String allowedChars;

        private String prefix;
    }
    private final Tag tag;

    @Data
    public static class Inventory {
        private int maxItems;
    }
    private final Inventory inventory;

    @Data
    public static class Adventure {
        private int maxPlayers;
        private int minReputation;
        private int maxReputation;
    }
    private final Adventure adventure;
}