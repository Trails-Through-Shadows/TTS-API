package cz.trailsthroughshadows.algorithm.session.credentials;

import cz.trailsthroughshadows.algorithm.session.Session;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    String token;
    Integer licenseId;

    public AuthResponse(Session session) {
        this.token = session.getToken();
        this.licenseId = session.getLicenseId();
    }
}
