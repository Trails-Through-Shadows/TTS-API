package cz.trailsthroughshadows.api.debug;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
@RestController
@RequestMapping(value = "/debug", produces = MediaType.TEXT_PLAIN_VALUE)
public class DebugController {
    @Autowired
    private ResourceLoader resourceLoader;

    @GetMapping("/logs/latest")
    public String getLatestLogs(@RequestParam(required = false, defaultValue = "50000") int characters) throws IOException {
        String currDir = System.getProperty("user.dir");
        String path = currDir + "/logs/latest.log";
        Resource resource = resourceLoader.getResource("file:" + path);


        int start = Math.max(0, resource.getInputStream().available() - characters);
        try (InputStream in = resource.getInputStream()) {
            in.skipNBytes(start);
            return new String(in.readNBytes(characters));
        }
    }

}
