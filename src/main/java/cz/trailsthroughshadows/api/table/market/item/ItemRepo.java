package cz.trailsthroughshadows.api.table.market.item;

import cz.trailsthroughshadows.api.table.market.item.model.ItemDTO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepo extends JpaRepository<ItemDTO, Integer> {

}
