package cz.trailsthroughshadows.algorithm.session;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@AllArgsConstructor
@Data
public class Session {
    private Integer licenseId;
    private UUID uuid;

    public String hello() {
        return "Hello from session " + licenseId;
    }
}
