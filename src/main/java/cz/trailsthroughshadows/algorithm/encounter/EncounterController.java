package cz.trailsthroughshadows.algorithm.encounter;

import cz.trailsthroughshadows.algorithm.encounter.model.Initiative;
import cz.trailsthroughshadows.api.rest.model.response.IdResponse;
import cz.trailsthroughshadows.api.rest.model.response.ObjectResponse;
import cz.trailsthroughshadows.api.rest.model.response.RestResponse;
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
}
