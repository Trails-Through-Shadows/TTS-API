package cz.trailsthroughshadows.api.images;

import cz.trailsthroughshadows.api.configuration.ImageLoaderConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

@Slf4j
@RestController
@RequestMapping(value = "/images", produces = MediaType.IMAGE_PNG_VALUE)
public class ImageController {

    @Autowired
    private ImageLoaderConfig config;

    @Autowired
    private ResourceLoader resourceLoader;

    @GetMapping("/{type}/{file}")
    public @ResponseBody byte[] getImage(
            @PathVariable String type,
            @PathVariable String file,
            @RequestParam(required = false) Integer width,
            @RequestParam(required = false) Integer height,
            @RequestParam(required = false) Integer size) throws IOException {

        String localpath = type + "/" + file;
        var physicalPath = config.getPath();
        String currDir = System.getProperty("user.dir");
        String path = currDir + "/" + physicalPath + "/" + localpath;

        File f = new File(path);

        width = width == null ? null : width > 2000 ? 2000 : width;
        height = height == null ? null : height > 2000 ? 2000 : height;
        size = size == null ? null : size > 2000 ? 2000 : size;
        width = width == null ? null : width < 10 ? 10 : width;
        height = height == null ? null : height < 10 ? 10 : height;
        size = size == null ? null : size < 10 ? 10 : size;

        if (!(f.exists() && !f.isDirectory())) {
            log.warn("File not found: {}", path);
            Resource resource = resourceLoader.getResource("classpath:/images/unknown.png");

            if (width != null && height != null) {
                return ImageScaler.scaleImage(resource, width, height).getInputStream().readAllBytes();
            } else if (size != null) {
                return ImageScaler.scaleImage(resource, size).getInputStream().readAllBytes();
            }

            return resource.getInputStream().readAllBytes();
        }
        Resource resource = resourceLoader.getResource("file:" + path);

        if (width != null && height != null) {
            return ImageScaler.scaleImage(resource, width, height).getInputStream().readAllBytes();
        } else if (size != null) {
            return ImageScaler.scaleImage(resource, size).getInputStream().readAllBytes();
        }

        return resource.getInputStream().readAllBytes();

    }

}
