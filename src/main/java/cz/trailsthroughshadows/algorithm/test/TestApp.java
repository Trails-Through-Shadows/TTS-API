package cz.trailsthroughshadows.algorithm.test;

import cz.trailsthroughshadows.api.table.action.ActionController;
import cz.trailsthroughshadows.api.table.action.ActionRepo;
import cz.trailsthroughshadows.api.table.action.features.summon.SummonRepo;
import cz.trailsthroughshadows.api.table.action.model.Action;
import cz.trailsthroughshadows.api.table.action.model.ActionDTO;
import cz.trailsthroughshadows.api.table.enemy.EnemyRepo;
import cz.trailsthroughshadows.api.table.playerdata.character.CharacterRepo;
import cz.trailsthroughshadows.api.table.schematic.location.LocationRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Slf4j
@RestController
@RequestMapping("/test")
public class TestApp {

    @Autowired
    LocationRepo locationRepo;

    @Autowired
    CharacterRepo characterRepo;

    @Autowired
    EnemyRepo enemyRepo;

    @Autowired
    SummonRepo summonRepo;

    @Autowired
    ActionRepo actionRepo;

    @GetMapping("")
    EntityModel<ActionDTO> test() {
        ActionDTO action = actionRepo.findById(1).orElse(new ActionDTO());
        return EntityModel.of(action,
                linkTo(methodOn(TestApp.class).test()).withSelfRel() //thats cool af
        );
    }

//    @GetMapping("/fasa")
//    ResponseEntity<?> testFasa() {
//
//        return ResponseEntity.ok("fasa").;
//
//
//    }

}
