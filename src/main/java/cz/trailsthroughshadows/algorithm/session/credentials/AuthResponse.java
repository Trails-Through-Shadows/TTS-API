package cz.trailsthroughshadows.algorithm.session.credentials;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class AuthResponse {
    UUID token;
    Integer licenseId;
}
