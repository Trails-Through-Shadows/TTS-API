package cz.trailsthroughshadows.api.table.schematic.rest;

import cz.trailsthroughshadows.api.table.schematic.location.Location;
import cz.trailsthroughshadows.api.table.schematic.location.LocationRepo;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("/location")
@Log4j2
public class LocationController {

    @Autowired
    private LocationRepo repository;

    @GetMapping("/")
    public Collection<Location> findClass() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Location findById(@PathVariable int id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid class Id:" + id));
    }

}
