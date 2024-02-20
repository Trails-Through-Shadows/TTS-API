package cz.trailsthroughshadows.api.util;

import cz.trailsthroughshadows.api.configuration.ImageLoaderConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ImageLoader {

    private static ImageLoaderConfig config;

    public static String getPath(List<String> tag) {
        String race = tag.get(0).charAt(0) == 'r' ? tag.get(0) : tag.get(1);
        String clazz = tag.get(0).charAt(0) == 'c' ? tag.get(0) : tag.get(1);

        String character = String.format("%s-%s", race.substring(2), clazz.substring(2));

        return parsePath(character);
    }


    public static String getPath(String tag) {
        return parsePath(tag);
    }

    private static String parsePath(String tag) {
        String folder = parseFolder(tag);

        tag = tag.substring(2);
        return String.format("%s%s/%s/%s.png", config.getUrl(), config.getPath(), folder, tag);
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


    @Autowired
    public void setValidationConfig(ImageLoaderConfig validationConfig) {
        config = validationConfig;
    }

}
