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

    private List<AdventureDTO> adventures;

    public String hello() {
        return "Hello from session " + licenseId;
    }
}
