package cz.trailsthroughshadows.api.table.playerdata.adventure.achievement;

import cz.trailsthroughshadows.api.rest.model.pagination.Pagination;
import cz.trailsthroughshadows.api.rest.model.pagination.RestPaginatedResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/achievements")
public class AchievementController {

    @Autowired
    private AchievementRepo achievementRepo;

    @GetMapping("/{id}")
    @Cacheable(value = "achievement", key="T(java.util.Objects).hash(#id)")
    public AchievementDTO findById(@PathVariable int id) {
        return achievementRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid achievement Id:" + id));
    }

    @GetMapping("")
    @Cacheable(value = "achievement", key="T(java.util.Objects).hash(#page, #limit, #filter, #sort)")
    public ResponseEntity<RestPaginatedResult<AchievementDTO>> findAllEntities(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int limit,
            @RequestParam(defaultValue = "") String filter,
            @RequestParam(defaultValue = "") String sort) {
        var entities = achievementRepo.getAll();
        Pagination pagination = new Pagination(entities.size(), false, entities.size(), page, limit);
        return new ResponseEntity<>(RestPaginatedResult.of(pagination, entities), HttpStatus.OK);

    }
}