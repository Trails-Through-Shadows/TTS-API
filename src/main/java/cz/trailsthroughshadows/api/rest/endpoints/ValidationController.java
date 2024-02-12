package cz.trailsthroughshadows.api.rest.endpoints;

import cz.trailsthroughshadows.algorithm.validation.Validable;
import cz.trailsthroughshadows.algorithm.validation.ValidationService;
import cz.trailsthroughshadows.api.rest.model.RestResponse;
import cz.trailsthroughshadows.api.table.action.ActionRepo;
import cz.trailsthroughshadows.api.table.action.features.summon.Summon;
import cz.trailsthroughshadows.api.table.action.features.summon.SummonRepo;
import cz.trailsthroughshadows.api.table.action.model.ActionDTO;
import cz.trailsthroughshadows.api.table.effect.EffectRepo;
import cz.trailsthroughshadows.api.table.effect.model.EffectDTO;
import cz.trailsthroughshadows.api.table.enemy.EnemyRepo;
import cz.trailsthroughshadows.api.table.enemy.model.dto.EnemyDTO;
import cz.trailsthroughshadows.api.table.schematic.hex.model.dto.HexDTO;
import cz.trailsthroughshadows.api.table.schematic.obstacle.ObstacleRepo;
import cz.trailsthroughshadows.api.table.schematic.obstacle.model.Obstacle;
import cz.trailsthroughshadows.api.table.schematic.part.PartRepo;
import cz.trailsthroughshadows.api.table.schematic.part.model.PartDTO;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Log4j2
@Component
@RestController
@RequestMapping("/validate")
public class ValidationController {

    @Autowired
    ValidationService validation;

    //region Schema

    @PostMapping("/part")
    public ResponseEntity<RestResponse> validatePart(@RequestBody PartDTO part) {
        return validate(part);
    }

    @Autowired
    PartRepo partRepo;
    @PostMapping("/part/{id}")
    public ResponseEntity<RestResponse> validatePartById(@PathVariable int id) {
        return validate(partRepo.findById(id));
    }

    @PostMapping("/hex")
    public ResponseEntity<RestResponse> validateHex(@RequestBody HexDTO hex) {
        return validate(hex);
    }

    //endregion

    //region Encounter

    @PostMapping("/enemy")
    public ResponseEntity<RestResponse> validateEnemy(@RequestBody EnemyDTO enemy) {
        return validate(enemy);
    }

    @Autowired
    EnemyRepo enemyRepo;
    @PostMapping("/enemy/{id}")
    public ResponseEntity<RestResponse> validateEnemyById(@PathVariable int id) {
        return validate(enemyRepo.findById(id));
    }

    @PostMapping("/summon")
    public ResponseEntity<RestResponse> validateSummon(@RequestBody Summon summon) {
        return validate(summon);
    }

    @Autowired
    SummonRepo summonRepo;
    @PostMapping("/summon/{id}")
    public ResponseEntity<RestResponse> validateSummonById(@PathVariable int id) {
        return validate(summonRepo.findById(id));
    }

    @PostMapping("/obstacle")
    public ResponseEntity<RestResponse> validateObstacle(@RequestBody Obstacle obstacle) {
        return validate(obstacle);
    }

    @Autowired
    ObstacleRepo obstacleRepo;
    @PostMapping("/obstacle/{id}")
    public ResponseEntity<RestResponse> validateObstacleById(@PathVariable int id) {
        return validate(obstacleRepo.findById(id));
    }

    //endregion

    //region Combat
    @PostMapping("/action")
    public ResponseEntity<RestResponse> validateAction(@RequestBody ActionDTO action) {
        return validate(action);
    }

    @Autowired
    ActionRepo actionRepo;
    @PostMapping("/action/{id}")
    public ResponseEntity<RestResponse> validateActionById(@PathVariable int id) {
        return validate(actionRepo.findById(id));
    }

    @PostMapping("/effect")
    public ResponseEntity<RestResponse> validateEffect(@RequestBody EffectDTO effect) {
        return validate(effect);
    }

    @Autowired
    EffectRepo effectRepo;
    @PostMapping("/effect/{id}")
    public ResponseEntity<RestResponse> validateEffectById(@PathVariable int id) {
        return validate(effectRepo.findById(id));
    }

    //endregion

    private ResponseEntity<RestResponse> validate(Validable validable) {
        return validate(Optional.of(validable));
    }

    private ResponseEntity<RestResponse> validate(Optional<? extends Validable> validable) {
        return new ResponseEntity<>(new RestResponse(HttpStatus.OK, validation.validate(validable)), HttpStatus.OK);
    }
}
