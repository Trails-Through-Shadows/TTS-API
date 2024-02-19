package cz.trailsthroughshadows.algorithm.session.credentials;

import lombok.Data;

@Data
public class AuthRequest {
    private String key;
    private String password;
}
