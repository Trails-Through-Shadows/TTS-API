package cz.trailsthroughshadows.api.images;

import cz.trailsthroughshadows.api.configuration.ImageLoaderConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
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


    @Operation(
            summary = "Retrieve Customizable Image",
            description = """
                    # Retrieve Customizable Image
                    Fetches an image based on specified parameters allowing for dynamic customization. This endpoint caters to various needs by providing options to adjust the size, apply rounding to corners, and select specific image types or tokens.

                    **Parameters**:
                    - `type` - The category or collection to which the image belongs.
                    - `file` - The specific file name of the image to retrieve.
                    - `width` - Optional. The desired width to which the image should be resized.
                    - `height` - Optional. The desired height to which the image should be resized.
                    - `size` - Optional. A single value to resize the image to a square of specified dimensions.
                    - `radius` - Optional, defaults to 0. Applies a radius to round the corners of the image.
                    - `token` - Optional. A boolean that if set to true, fetches images from a token-specific path.

                    This endpoint is designed to be flexible, supporting various image retrieval scenarios from adjusting dimensions to accessing specific types of images like tokens. If the requested image is not found, a default placeholder image is returned, potentially resized and rounded according to specified parameters. Image is in png format.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Image successfully retrieved",
                    content = {@Content(mediaType = "image/png", // Assume PNG, adjust as necessary
                            schema = @Schema(type = "string", format = "binary"))}),
            @ApiResponse(responseCode = "404", description = "Image not found",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Error processing image request",
                    content = @Content),
            @ApiResponse(responseCode = "default", description = "Unexpected error",
                    content = @Content)
    })
    @GetMapping("/{type}/{file}")
    @Cacheable(value = "image", key = "T(java.util.Objects).hash(#type, #file, #width, #height, #size, #radius, #token)")
    public @ResponseBody byte[] getImage(
            @Parameter(description = "The category or collection to which the image belongs.", required = true)
            @PathVariable String type,
            @Parameter(description = "The specific file name of the image to retrieve.", required = true)
            @PathVariable String file,
            @Parameter(description = "The desired width to which the image should be resized. If not specified, the image will be returned in its original size.", required = false)
            @RequestParam(required = false) Integer width,
            @Parameter(description = "The desired height to which the image should be resized. If not specified, the image will be returned in its original size.", required = false)
            @RequestParam(required = false) Integer height,
            @Parameter(description = "A single value to resize the image to a square of specified dimensions. Overrides individual width/height settings if provided.", required = false)
            @RequestParam(required = false) Integer size,
            @Parameter(description = "Applies a radius to round the corners of the image, given in pixels. Defaults to 0, meaning no rounding.", required = false)
            @RequestParam(required = false, defaultValue = "0") Integer radius,
            @Parameter(description = "A boolean indicating if the image should be retrieved from a token-specific path. Useful for token images in games.", required = false)
            @RequestParam(required = false) boolean token
    ) throws IOException {

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

    @Operation(
            summary = "Retrieve SVG File",
            description = """
                    # Retrieve SVG File
                    Fetches a scalable vector graphics (SVG) file based on a specified type and file name. This endpoint is designed to serve SVG files, which are ideal for high-quality graphics that need to be scalable without loss of resolution.

                    **Parameters**:
                    - `type` - The category or collection to which the SVG file belongs.
                    - `file` - The specific file name of the SVG file to retrieve. The file name must include the '.svg' extension.

                    This method ensures that SVG files are easily accessible and can be retrieved dynamically based on their type and name. This is particularly useful for applications requiring high-quality graphic representations that are scalable.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SVG file successfully retrieved",
                    content = {@Content(mediaType = "image/svg+xml",
                            schema = @Schema(type = "string", format = "binary"))}),
            @ApiResponse(responseCode = "404", description = "SVG file not found",
                    content = @Content),
            @ApiResponse(responseCode = "default", description = "Unexpected error",
                    content = @Content)
    })
    @GetMapping(value = "/svg/{type}/{file:.+\\.svg}")
    @Cacheable(value = "svg", key = "T(java.util.Objects).hash(#type, #file)")
    public ResponseEntity<Resource> getSvg(
            @Parameter(description = "The category or collection to which the SVG file belongs.", required = true)
            @PathVariable String type,
            @Parameter(description = "The specific file name of the SVG file to retrieve, including the '.svg' extension.", required = true)
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
            log.warn("Serving file uri to string: {}", res.getURI());
            Path linux = new File(res.getURI()).toPath();
            log.warn("Serving file path : {}", linux);
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
