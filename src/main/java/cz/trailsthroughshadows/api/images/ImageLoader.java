package cz.trailsthroughshadows.api.images;

import cz.trailsthroughshadows.api.configuration.ImageLoaderConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class ImageLoader {

    private static ImageLoaderConfig config;

    public static String getPath(List<String> tag) {
        log.debug("Mapping url, Tag: {}", tag);
        if (tag.get(0).isEmpty() || tag.get(1).isEmpty()) {
            return parsePath("*-unknown");
        }

        String race = tag.get(0).charAt(0) == 'r' ? tag.get(0) : tag.get(1);
        String clazz = tag.get(0).charAt(0) == 'c' ? tag.get(0) : tag.get(1);
        race = race.replaceFirst("^r", "w");

        String character = String.format("%s-%s", race, clazz.substring(2));

        return parsePath(character);
    }

    /**
     * @param tag
     * @return parsed url to image based on tag, if tag is unknown, returns
     *         unknown.png
     */
    public static String getPath(String tag) {
        return parsePath(tag);
    }

    private static String parsePath(String tag) {
        String folder = parseFolder(tag);
        if (folder.equals("unknown")) {
            return String.format("%s/%s/unknown.png", config.getUrl(), config.getPath());
        }
        tag = tag.substring(2);
        return String.format("%s/%s/%s/%s.png", config.getUrl(), config.getPath(), folder, tag);
    }

    private static String parseFolder(String tag) {

        if (tag.charAt(1) != '-') {
            return "unknown";
        }

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

    @Autowired
    public void setValidationConfig(ImageLoaderConfig validationConfig) {
        config = validationConfig;
    }

}
