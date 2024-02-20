package cz.trailsthroughshadows.api.table.enemy;

import cz.trailsthroughshadows.algorithm.validation.ValidationService;
import cz.trailsthroughshadows.api.rest.exception.RestException;
import cz.trailsthroughshadows.api.rest.model.pagination.Pagination;
import cz.trailsthroughshadows.api.rest.model.pagination.RestPaginatedResult;
import cz.trailsthroughshadows.api.rest.model.response.MessageResponse;
import cz.trailsthroughshadows.api.table.effect.relation.forothers.EnemyEffectDTO;
import cz.trailsthroughshadows.api.table.enemy.model.Enemy;
import cz.trailsthroughshadows.api.table.enemy.model.dto.EnemyActionDTO;
import cz.trailsthroughshadows.api.table.enemy.model.dto.EnemyDTO;
import cz.trailsthroughshadows.api.util.Pair;
import cz.trailsthroughshadows.api.util.reflect.Filtering;
import cz.trailsthroughshadows.api.util.reflect.Initialization;
import cz.trailsthroughshadows.api.util.reflect.Sorting;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController(value = "Enemy")
public class EnemyController {

    private ValidationService validation;

    private EnemyRepo enemyRepo;

    @GetMapping("/enemies")
    @Cacheable(value = "enemy")
    public ResponseEntity<RestPaginatedResult<Enemy>> getEnemies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int limit,
            @RequestParam(defaultValue = "") String filter,
            @RequestParam(defaultValue = "") String sort
    ) {
        // TODO: Re-Implement filtering, sorting and pagination @rcMarty
        // Issue: https://github.com/Trails-Through-Shadows/TTS-API/issues/31

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
    //@Cacheable(value = "enemy", key = "#id")
    public ResponseEntity<Enemy> findById(
            @PathVariable int id,
            @RequestParam(required = false, defaultValue = "") List<String> include,
            @RequestParam(required = false, defaultValue = "false") boolean lazy
    ) {
        EnemyDTO entity = enemyRepo
                .findById(id)
                .orElseThrow(() -> RestException.of(HttpStatus.NOT_FOUND, "Action with id '%d' not found! " + id));

        if (!lazy) {
            Initialization.hibernateInitializeAll(entity);
        } else {
            Initialization.hibernateInitializeAll(entity, include);
        }

        return new ResponseEntity<>(Enemy.fromDTO(entity), HttpStatus.OK);
    }

    @DeleteMapping("/enemies/{id}")
    @CacheEvict(value = "enemy", key = "#id")
    public ResponseEntity<MessageResponse> deleteEnemy(@PathVariable int id) {
        EnemyDTO enemyDTO = enemyRepo
                .findById(id)
                .orElseThrow(() -> RestException.of(HttpStatus.NOT_FOUND, "Enemy with id %d not found", id));

        enemyRepo.delete(enemyDTO);
        return new ResponseEntity<>(MessageResponse.of(HttpStatus.OK, "Enemy with id '%d' deleted!", id), HttpStatus.OK);
    }

    @PutMapping("/enemies/{id}")
    @CacheEvict(value = "enemy", key = "#id")
    public ResponseEntity<MessageResponse> updateEnemyById(@PathVariable int id, @RequestBody EnemyDTO enemy) {
        EnemyDTO enemyToUpdate = enemyRepo
                .findById(id)
                .orElseThrow(() -> RestException.of(HttpStatus.NOT_FOUND, "Enemy with id %d not found", id));

        // Validate enemy
        validation.validate(enemy);

        enemyToUpdate.setTag(enemy.getTag());
        enemyToUpdate.setTitle(enemy.getTitle());
        enemyToUpdate.setDescription(enemy.getDescription());
        enemyToUpdate.setBaseDefence(enemy.getBaseDefence());
        enemyToUpdate.setBaseHealth(enemy.getBaseHealth());
        enemyToUpdate.setBaseInitiative(enemy.getBaseInitiative());

        enemyToUpdate.getEffects().retainAll(enemy.getEffects());
        enemyToUpdate.getActions().addAll(enemy.getActions());
        enemyToUpdate.getEffects().forEach(effect -> effect.setIdEnemy(enemyToUpdate.getId()));

        enemyToUpdate.getActions().retainAll(enemy.getActions());
        enemyToUpdate.getEffects().addAll(enemy.getEffects());
        enemyToUpdate.getActions().forEach(action -> action.getKey().setIdEnemy(enemyToUpdate.getId()));

        enemyRepo.save(enemyToUpdate);
        return new ResponseEntity<>(MessageResponse.of(HttpStatus.OK, "Enemy with id '%d' updated!", id), HttpStatus.OK);
    }

    @PostMapping("/enemies")
    @CacheEvict(value = "enemy", allEntries = true)
    public ResponseEntity<MessageResponse> createEnemies(@RequestBody List<EnemyDTO> enemies) {
        log.debug("Creating enemies: " + enemies);

        // Validate all enemies
        enemies.forEach(validation::validate);

        // Remove ids to always create new enemies
        enemies.forEach(e -> e.setId(null));

        // Remove relations and save them for later
        Map<String, Pair<List<EnemyEffectDTO>, List<EnemyActionDTO>>> enemyRelations = new HashMap<>();
        enemies.forEach(enemy -> {
            enemyRelations.put(enemy.getTag(), new Pair<>(new ArrayList<>(enemy.getEffects()), new ArrayList<>(enemy.getActions())));
            enemy.setEffects(null);
            enemy.setActions(null);
        });

        // Save enemies
        enemies = enemyRepo.saveAll(enemies);

        // Load relations
        enemies.forEach(enemy -> {
            Pair<List<EnemyEffectDTO>, List<EnemyActionDTO>> relations = enemyRelations.get(enemy.getTag());

            enemy.setEffects(new ArrayList<>(relations.first()));
            enemy.getEffects().forEach(effect -> effect.setIdEnemy(enemy.getId()));

            enemy.setActions(new ArrayList<>(relations.second()));
            enemy.getActions().forEach(action -> action.getKey().setIdEnemy(enemy.getId()));
        });

        // Save enemies relations
        enemies = enemyRepo.saveAll(enemies);

        String ids = enemies.stream().map((entry) -> String.valueOf(entry.getId())).toList().toString();
        return new ResponseEntity<>(MessageResponse.of(HttpStatus.OK, "Enemies with ids '%s' created!", ids), HttpStatus.OK);
    }

    @Autowired
    public void setRepository(EnemyRepo repository) {
        this.enemyRepo = repository;
    }

    @Autowired
    public void setValidation(ValidationService validation) {
        this.validation = validation;
    }
}