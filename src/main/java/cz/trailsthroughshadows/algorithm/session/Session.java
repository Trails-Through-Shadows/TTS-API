package cz.trailsthroughshadows.algorithm.session;

import cz.trailsthroughshadows.api.table.playerdata.adventure.model.AdventureDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

@Data
@AllArgsConstructor
public class Session {

    private static final String key = "shadefa11en:ourPassword";
    private static final String masterKey = Base64.getEncoder().encodeToString(key.getBytes(StandardCharsets.UTF_8));
    public static Session ADMINISTRATOR_SESSION = new Session(masterKey, 0, List.of());

    private String token;
    private Integer licenseId;
    private List<AdventureDTO> adventures;

    public String hello() {
        return "Hello from session " + licenseId;
    }

    public boolean isAdmin() {
        return Objects.equals(token, ADMINISTRATOR_SESSION.token);
    }

    public boolean hasAccess(Integer licenseId) {
        return isAdmin() || this.licenseId.equals(licenseId);
    }
}
