package cz.trailsthroughshadows.api.table.campaign;

import cz.trailsthroughshadows.api.table.campaign.model.CampaignLocation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CampaignLocationRepo  extends JpaRepository<CampaignLocation, Integer> {

}
