package cz.trailsthroughshadows.api.table.equipment;

import cz.trailsthroughshadows.api.table.equipment.item.Item;
import cz.trailsthroughshadows.api.table.equipment.market.Market;
import cz.trailsthroughshadows.api.table.equipment.item.ItemRepo;
import cz.trailsthroughshadows.api.table.equipment.market.MarketRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("/api/equipment")
public class EquipmentController {
    @Autowired
    private ItemRepo itemRepo;
    @Autowired
    private MarketRepo marketRepo;

    @GetMapping("/items")
    public Collection<Item> getAllItems() {
        return itemRepo.findAll();
    }

    @GetMapping("/markets")
    public Collection<Market> getAllMarkets() {
        return marketRepo.findAll();
    }


}
