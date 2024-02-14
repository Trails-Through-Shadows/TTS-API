package cz.trailsthroughshadows.api.table.market;

import cz.trailsthroughshadows.api.table.market.item.ItemRepo;
import cz.trailsthroughshadows.api.table.market.item.model.ItemDTO;
import cz.trailsthroughshadows.api.table.market.market.MarketRepo;
import cz.trailsthroughshadows.api.table.market.market.model.MarketDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("/markets")
public class MarketController {
    @Autowired
    private ItemRepo itemRepo;

    @Autowired
    private MarketRepo marketRepo;

    @GetMapping("/items")
    public Collection<ItemDTO> getAllItems() {
        return itemRepo.findAll();
    }

    @GetMapping("")
    public Collection<MarketDTO> getAllMarkets() {
        return marketRepo.findAll();
    }


}
