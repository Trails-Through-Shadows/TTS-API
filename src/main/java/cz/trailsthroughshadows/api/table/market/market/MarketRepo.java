package cz.trailsthroughshadows.api.table.market.market;

import cz.trailsthroughshadows.api.table.market.market.model.MarketDTO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MarketRepo extends JpaRepository<MarketDTO, MarketDTO.MarketId> {

}
