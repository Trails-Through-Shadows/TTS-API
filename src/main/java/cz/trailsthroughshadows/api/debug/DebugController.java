package cz.trailsthroughshadows.api.debug;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@RestController
@RequestMapping(value = "/debug", produces = MediaType.TEXT_PLAIN_VALUE)
public class DebugController {
    @Autowired
    private ResourceLoader resourceLoader;

    @Operation(
            summary = "Get Latest Logs",
            description = """
                    # Get Latest Logs
                    Retrieves the latest entries from the system's log file up to a specified number of characters. This endpoint is useful for quickly accessing recent log data without needing to download or search through the entire log file.

                    **Parameters**:
                    - `characters` - Optional. Specifies the maximum number of characters to retrieve from the end of the log file. Defaults to 50000 characters.

                    This endpoint is particularly useful for developers and system administrators for monitoring and troubleshooting purposes.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Log entries successfully retrieved",
                    content = {@Content(mediaType = "text/plain")}),
            @ApiResponse(responseCode = "500", description = "Error reading log file",
                    content = @Content),
            @ApiResponse(responseCode = "default", description = "Unexpected error",
                    content = @Content)
    })
    @GetMapping("/logs/latest")
    public String getLatestLogs(
            @Parameter(description = "The maximum number of characters from the log file to retrieve. If not specified, defaults to 50000 characters.", required = false)
            @RequestParam(required = false, defaultValue = "50000") int characters
    ) throws IOException {
        String currDir = System.getProperty("user.dir");
        String path = currDir + "/logs/latest.log";
        Resource resource = resourceLoader.getResource("file:" + path);


        int start = Math.max(0, resource.getInputStream().available() - characters);
        log.debug("available: {}", resource.getInputStream().available());
        try (InputStream in = resource.getInputStream()) {
            in.skipNBytes(start);
            return new String(in.readNBytes(characters));
        }
    }

}
