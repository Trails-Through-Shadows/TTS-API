package cz.trailsthroughshadows.api.images;

import cz.trailsthroughshadows.api.configuration.ImageLoaderConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

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
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false, defaultValue = "0") Integer radius,
            @RequestParam(required = false) boolean token) throws IOException {

        String localpath = type + "/" + ((token) ? "tokens/" : "") + file;
        var physicalPath = config.getPath();
        String currDir = System.getProperty("user.dir");
        String path = currDir + "/" + physicalPath + "/" + localpath;

        File f = new File(path);

        if (width != null) {
            width = Math.clamp(width, 10, 2000);
        }
        if (height != null) {
            height = Math.clamp(height, 10, 2000);
        }
        if (size != null) {
            size = Math.clamp(size, 10, 2000);
        }

        if (!(f.exists() && !f.isDirectory())) {
            log.warn("File not found: {}", path);
            Resource resource = resourceLoader.getResource("classpath:/images/unknown.png");

            if (width != null && height != null) {
                return ImageScaler.scaleImage(resource, width, height, radius).getInputStream().readAllBytes();
            } else if (size != null) {
                return ImageScaler.scaleImage(resource, size, size, radius).getInputStream().readAllBytes();
            }

            return resource.getInputStream().readAllBytes();
        }
        Resource resource = resourceLoader.getResource("file:" + path);

        if (width != null && height != null) {
            return ImageScaler.scaleImage(resource, width, height, radius).getInputStream().readAllBytes();
        } else if (size != null) {
            return ImageScaler.scaleImage(resource, size, size, radius).getInputStream().readAllBytes();
        }

        return resource.getInputStream().readAllBytes();

    }

    @GetMapping(value = "/svg/{type}/{file:.+\\.svg}")
    public ResponseEntity<Resource> getSwg(
            @PathVariable String type,
            @PathVariable String file
    ) throws IOException {

        String localpath = "svgs/" + type + "/" + file;
        var physicalPath = config.getPath();
        String currDir = System.getProperty("user.dir");
        String pathstr = currDir + "/" + physicalPath + "/" + localpath;

        Path path = new File(pathstr).toPath();
        log.info("Serving file: {}", pathstr);
        //check if file exists
        if (!Files.exists(path)) {
            log.warn("File not found: {}", pathstr);
            Resource res = resourceLoader.getResource("classpath:/images/linux.svg");
            log.warn("Serving file uri to string: {}", res.getURI().toString());
            Path linux = new File(res.getURI()).toPath();
            log.warn("Serving file path : {}", linux.toString());
            FileSystemResource resource = new FileSystemResource(linux);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(Files.probeContentType(linux)))
                    .body(resource);

        }

        FileSystemResource resource = new FileSystemResource(path);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(Files.probeContentType(path)))
                .body(resource);
    }

}
