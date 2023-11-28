package cz.trailsthroughshadows.api.table.inventory;

import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryRepo extends JpaRepository<Inventory,Integer> {

}
