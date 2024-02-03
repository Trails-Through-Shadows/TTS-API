package cz.trailsthroughshadows.api.table.enemy;

import cz.trailsthroughshadows.api.rest.exception.RestException;
import cz.trailsthroughshadows.api.rest.model.Pagination;
import cz.trailsthroughshadows.api.rest.model.RestPaginatedResult;
import cz.trailsthroughshadows.api.table.enemy.model.Enemy;
import cz.trailsthroughshadows.api.table.enemy.model.dto.EnemyDTO;
import cz.trailsthroughshadows.api.util.reflect.Filtering;
import cz.trailsthroughshadows.api.util.reflect.Sorting;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@Cacheable(value = "enemy")
@RestController(value = "Enemy")
public class EnemyController {
    private EnemyRepo enemyRepo;

    @GetMapping("/enemies")
    public ResponseEntity<RestPaginatedResult<Enemy>> getEnemies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int limit,
            @RequestParam(defaultValue = "") String filter, // TODO: Re-Implement filtering
            @RequestParam(defaultValue = "") String sort // TODO: Re-Implement sorting
    ) {
        List<Enemy> entries = enemyRepo.findAll().stream()
                .filter((entry) -> Filtering.match(entry, List.of(filter.split(","))))
                .sorted((a, b) -> Sorting.compareTo(a, b, List.of(sort.split(","))))
                .map(Enemy::fromDTO)
                .toList();

        List<Enemy> entriesPage = entries.stream()
                .skip((long) Math.max(page, 0) * limit)
                .limit(limit)
                .toList();

        Pagination pagination = new Pagination(entriesPage.size(), (entries.size() > (Math.max(page, 0) + 1) * limit), entries.size(), page, limit);
        return new ResponseEntity<>(RestPaginatedResult.of(pagination, entriesPage), HttpStatus.OK);
    }

    @GetMapping("/enemies/{id}")
    public ResponseEntity<Enemy> getEnemyById(@PathVariable int id) {
        EnemyDTO enemyDTO = enemyRepo
                .findById(id)
                .orElseThrow(() -> RestException.of(HttpStatus.NOT_FOUND, "Enemy with id %d not found", id));

        return new ResponseEntity<>(Enemy.fromDTO(enemyDTO), HttpStatus.OK);
    }

    @Autowired
    public void setRepository(EnemyRepo repository) {
        this.enemyRepo = repository;
    }
}