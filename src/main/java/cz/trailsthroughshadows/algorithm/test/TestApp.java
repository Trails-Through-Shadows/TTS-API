package cz.trailsthroughshadows.algorithm.test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
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

    @GetMapping
    public Object test() {
        Location loc = locationRepo.findById(6).get();
        var x = loc.getNeighbors(loc.getLocationParts().get(0).getHexes().get(32 - 1), 2);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(x);
    }

    @Autowired
    public void setLocationRepo(LocationRepo locationRepo) {
        this.locationRepo = locationRepo;
    }
}
