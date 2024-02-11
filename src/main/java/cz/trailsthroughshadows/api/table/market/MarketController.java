package cz.trailsthroughshadows.api.table.market;

import cz.trailsthroughshadows.api.table.market.item.Item;
import cz.trailsthroughshadows.api.table.market.item.ItemRepo;
import cz.trailsthroughshadows.api.table.market.market.Market;
import cz.trailsthroughshadows.api.table.market.market.MarketRepo;
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
    public Collection<Item> getAllItems() {
        return itemRepo.findAll();
    }

    @GetMapping("")
    public Collection<Market> getAllMarkets() {
        return marketRepo.findAll();
    }


}
