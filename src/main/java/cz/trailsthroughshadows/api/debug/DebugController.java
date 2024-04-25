package cz.trailsthroughshadows.api.debug;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping(value = "/debug", produces = MediaType.TEXT_PLAIN_VALUE)
public class DebugController {
    @Autowired
    private ResourceLoader resourceLoader;

    @GetMapping("/logs/latest")
    public String getLatestLogs() throws IOException {
        String currDir = System.getProperty("user.dir");
        String path = currDir + "/logs/latest.log";
        Resource resource = resourceLoader.getResource("file:" + path);
        return new String(resource.getInputStream().readAllBytes());
    }

}
