package cz.trailsthroughshadows.api.table.campaign;

import cz.trailsthroughshadows.api.table.campaign.model.CampaignDTO;
import cz.trailsthroughshadows.api.table.campaign.model.Story;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CampaignRepo extends JpaRepository<CampaignDTO, Integer> {

    @Query("SELECT s from Story s where s.idCampaignLocation = ?1")
    List<Story> findAllStoriesByCampaignId(int idCampaign);
}
