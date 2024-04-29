package cz.trailsthroughshadows.api.table.campaign;

import cz.trailsthroughshadows.api.table.campaign.model.CampaignLocation;
import cz.trailsthroughshadows.api.table.schematic.location.model.dto.LocationPathDTO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationPath extends JpaRepository<LocationPathDTO, Integer> {

}
