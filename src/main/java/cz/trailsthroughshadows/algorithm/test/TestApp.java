package cz.trailsthroughshadows.algorithm.test;

import cz.trailsthroughshadows.api.table.schematic.location.Location;
import cz.trailsthroughshadows.api.table.schematic.location.LocationRepo;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequestMapping("/api/test")
public class TestApp {

    LocationRepo locationRepo;

    @GetMapping("/")
    public String test() {
        Location loc = locationRepo.findById(1).get();
        log.info(loc.getNeighbors(loc.getLocationParts().get(0).getHexes().get(0)));
        return "test";
    }

    @Autowired
    public void setLocationRepo(LocationRepo locationRepo) {
        this.locationRepo = locationRepo;
    }
}
