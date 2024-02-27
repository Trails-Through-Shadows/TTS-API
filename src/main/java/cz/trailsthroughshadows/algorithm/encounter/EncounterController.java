package cz.trailsthroughshadows.algorithm.encounter;

import cz.trailsthroughshadows.algorithm.encounter.model.EncounterEffect;
import cz.trailsthroughshadows.algorithm.encounter.model.Initiative;
import cz.trailsthroughshadows.algorithm.encounter.model.Interaction;
import cz.trailsthroughshadows.api.rest.model.response.IdResponse;
import cz.trailsthroughshadows.api.rest.model.response.ObjectResponse;
import cz.trailsthroughshadows.api.rest.model.response.RestResponse;
import cz.trailsthroughshadows.api.table.schematic.location.model.dto.LocationDoorDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
@RestController
@RequestMapping("/encounter")
public class EncounterController {

    @Autowired
    private EncounterHandler encounterHandler;

    @PostMapping("/{idAdventure}")
    public ResponseEntity<RestResponse> startEncounter(@RequestParam UUID token, @PathVariable Integer idAdventure, @RequestParam Integer idLocation) {
        return new ResponseEntity<>(IdResponse.of(HttpStatus.OK, encounterHandler.addEncounter(token, idAdventure, idLocation)), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestResponse> getEncounter(@RequestParam UUID token, @PathVariable Integer id) {
        return new ResponseEntity<>(ObjectResponse.of(HttpStatus.OK, encounterHandler.getEncounter(token, id)), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> endEncounter(@RequestParam UUID token, @PathVariable Integer id) {
        encounterHandler.removeEncounter(token, id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/{id}/initiative")
    public ResponseEntity<RestResponse> rollInitiative(@RequestParam UUID token, @PathVariable Integer id, @RequestBody List<Initiative> initiatives) {
        encounterHandler.getEncounter(token, id).rollInitiative(initiatives);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/{id}/initiative")
    public ResponseEntity<RestResponse> getInitiative(@RequestParam UUID token, @PathVariable Integer id) {
        return new ResponseEntity<>(ObjectResponse.of(HttpStatus.OK, encounterHandler.getEncounter(token, id).getInitiative()), HttpStatus.OK);
    }

    @PostMapping("/{id}/turn/character/{idCharacter}/start")
    public ResponseEntity<RestResponse> startCharacterTurn(@RequestParam UUID token, @PathVariable Integer id, @PathVariable Integer idCharacter) {
        return new ResponseEntity<>(ObjectResponse.of(HttpStatus.OK, encounterHandler.getEncounter(token, id).startCharacterTurn(idCharacter)), HttpStatus.OK);
    }

    @PostMapping("/{id}/turn/character/{idCharacter}/end")
    public ResponseEntity<RestResponse> endCharacterTurn(@RequestParam UUID token, @PathVariable Integer id, @PathVariable Integer idCharacter) {
        return new ResponseEntity<>(ObjectResponse.of(HttpStatus.OK, encounterHandler.getEncounter(token, id).endCharacterTurn(idCharacter)), HttpStatus.OK);
    }

    @PostMapping("/{id}/turn/enemy/{idEnemy}/start")
    public ResponseEntity<RestResponse> startEnemyTurn(@RequestParam UUID token, @PathVariable Integer id, @PathVariable Integer idEnemy) {
        return new ResponseEntity<>(ObjectResponse.of(HttpStatus.OK, encounterHandler.getEncounter(token, id).startEnemyTurn(idEnemy)), HttpStatus.OK);
    }

    @PostMapping("/{id}/turn/enemy/{idEnemy}/end")
    public ResponseEntity<RestResponse> endEnemyTurn(@RequestParam UUID token, @PathVariable Integer id, @PathVariable Integer idEnemy) {
        return new ResponseEntity<>(ObjectResponse.of(HttpStatus.OK, encounterHandler.getEncounter(token, id).endEnemyTurn(idEnemy)), HttpStatus.OK);
    }

    @PostMapping("/{id}/endRound")
    public ResponseEntity<RestResponse> endRound(@RequestParam UUID token, @PathVariable Integer id) {
        return new ResponseEntity<>(ObjectResponse.of(HttpStatus.OK, encounterHandler.getEncounter(token, id).endRound()), HttpStatus.OK);
    }

    @PostMapping("/{id}/interaction/character/{idCharacter}")
    public ResponseEntity<RestResponse> characterInteraction(@RequestParam UUID token, @PathVariable Integer id, @PathVariable Integer idCharacter, @RequestBody Interaction interaction) {
        return new ResponseEntity<>(ObjectResponse.of(HttpStatus.OK, encounterHandler.getEncounter(token, id).characterInteraction(idCharacter, interaction)), HttpStatus.OK);
    }

    @PostMapping("/{id}/interaction/enemy/{idEnemyGroup}/{idEnemy}")
    public ResponseEntity<RestResponse> enemyInteraction(@RequestParam UUID token, @PathVariable Integer id, @PathVariable Integer idEnemyGroup, @PathVariable Integer idEnemy, @RequestBody Interaction interaction) {
        return new ResponseEntity<>(ObjectResponse.of(HttpStatus.OK, encounterHandler.getEncounter(token, id).enemyInteraction(idEnemy, idEnemyGroup, interaction)), HttpStatus.OK);
    }

    @PostMapping("/{id}/interaction/summon/{idSummonGroup}/{idSummon}")
    public ResponseEntity<RestResponse> summonInteraction(@RequestParam UUID token, @PathVariable Integer id, @PathVariable Integer idSummonGroup, @PathVariable Integer idSummon, @RequestBody Interaction interaction) {
        return new ResponseEntity<>(ObjectResponse.of(HttpStatus.OK, encounterHandler.getEncounter(token, id).summonInteraction(idSummon, idSummonGroup, interaction)), HttpStatus.OK);
    }

    @PostMapping("/{id}/interaction/obstacle/{idObstacleGroup}/{idObstacle}")
    public ResponseEntity<RestResponse> obstacleInteraction(@RequestParam UUID token, @PathVariable Integer id, @PathVariable Integer idObstacleGroup, @PathVariable Integer idObstacle, @RequestBody Interaction interaction) {
        return new ResponseEntity<>(ObjectResponse.of(HttpStatus.OK, encounterHandler.getEncounter(token, id).obstacleInteraction(idObstacle, idObstacleGroup, interaction)), HttpStatus.OK);
    }

    @PostMapping("/{id}/openDoor")
    public ResponseEntity<RestResponse> openDoor(@RequestParam UUID token, @PathVariable Integer id, @RequestBody LocationDoorDTO door) {
        encounterHandler.getEncounter(token, id).openDoor(door);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
