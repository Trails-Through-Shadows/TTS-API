package cz.trailsthroughshadows.api.table.equipment.market;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MarketRepo extends JpaRepository<Market, Market.MarketId> {
    
}
