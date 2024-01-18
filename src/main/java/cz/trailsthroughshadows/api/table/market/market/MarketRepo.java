package cz.trailsthroughshadows.api.table.market.market;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MarketRepo extends JpaRepository<Market, Market.MarketId> {

}
