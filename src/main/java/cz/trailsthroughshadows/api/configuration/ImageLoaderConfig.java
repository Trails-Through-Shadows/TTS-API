package cz.trailsthroughshadows.api.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Data
@ConfigurationProperties(prefix = "image-loader")
public class ImageLoaderConfig {

    private static String path;

    private static String url;


    public static String getPath(List<String> tag) {
        return "";
    }


    public static String getPath(String tag) {
        return parseTag(tag);
    }

    private static String parseTag(String tag) {
        String folder = parseFolder(tag);

        tag = tag.substring(2);
        return String.format("%s/%s/%s.png", url, folder, tag);
    }

    private static String parseFolder(String tag) {

        //get first character of tag
        char first = tag.charAt(0);

        return switch (first) {
            case 'p' -> "parts";
            case 'o' -> "obstacles";
            case 'l' -> "locations";
            case 'i' -> "items";
            case 'w' -> "characters";
            case 'e' -> "enemies";
            case 's' -> "summons";
            default -> "unknown";
        };

    }
}
