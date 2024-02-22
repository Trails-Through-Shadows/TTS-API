package cz.trailsthroughshadows.api.table.campaign;

import cz.trailsthroughshadows.api.table.campaign.model.CampaignDTO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CampaignRepo extends JpaRepository<CampaignDTO, Integer> {
}
