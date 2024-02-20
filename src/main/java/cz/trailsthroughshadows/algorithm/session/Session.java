package cz.trailsthroughshadows.algorithm.session;

import cz.trailsthroughshadows.api.table.playerdata.adventure.model.AdventureDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
public class Session {
    private UUID token;
    private Integer licenseId;

    public static Session ADMINISTRATOR_SESSION = new Session(UUID.fromString("00000000-0000-0000-0000-000000000000"), 0, List.of());

    private List<AdventureDTO> adventures;

    public String hello() {
        return "Hello from session " + licenseId;
    }

    public boolean isAdmin() {
        return licenseId == 0;
    }

    public boolean hasAccess(Integer licenseId) {
        return isAdmin() || this.licenseId.equals(licenseId);
    }
}
