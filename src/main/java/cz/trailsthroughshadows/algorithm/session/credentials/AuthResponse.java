package cz.trailsthroughshadows.algorithm.session.credentials;

import cz.trailsthroughshadows.algorithm.session.Session;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class AuthResponse {
    UUID token;
    Integer licenseId;

    public AuthResponse(Session session) {
        this.token = session.getToken();
        this.licenseId = session.getLicenseId();
    }
}
