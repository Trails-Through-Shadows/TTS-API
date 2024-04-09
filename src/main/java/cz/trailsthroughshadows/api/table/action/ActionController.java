package cz.trailsthroughshadows.api.table.action;

import cz.trailsthroughshadows.algorithm.validation.ValidationService;
import cz.trailsthroughshadows.api.rest.exception.RestException;
import cz.trailsthroughshadows.api.rest.model.pagination.Pagination;
import cz.trailsthroughshadows.api.rest.model.pagination.RestPaginatedResult;
import cz.trailsthroughshadows.api.rest.model.response.MessageResponse;
import cz.trailsthroughshadows.api.table.action.model.Action;
import cz.trailsthroughshadows.api.table.action.model.ActionDTO;
import cz.trailsthroughshadows.api.table.effect.EffectRepo;
import cz.trailsthroughshadows.api.table.effect.model.EffectDTO;
import cz.trailsthroughshadows.api.table.effect.relation.foraction.AttackEffect;
import cz.trailsthroughshadows.api.table.effect.relation.foraction.MovementEffect;
import cz.trailsthroughshadows.api.table.effect.relation.foraction.SkillEffect;
import cz.trailsthroughshadows.api.util.reflect.Filtering;
import cz.trailsthroughshadows.api.util.reflect.Initialization;
import cz.trailsthroughshadows.api.util.reflect.Sorting;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController(value = "Action")
public class ActionController {

    private ValidationService validation;
    private ActionRepo actionRepo;
    private EffectRepo effectRepo;

    @GetMapping("/actions")
    //@Cacheable(value = "action")
    public ResponseEntity<RestPaginatedResult<Action>> findAllEntities(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int limit,
            @RequestParam(defaultValue = "") String filter,
            @RequestParam(defaultValue = "") String sort,
            @RequestParam(required = false, defaultValue = "") List<String> include,
            @RequestParam(required = false, defaultValue = "true") boolean lazy
    ) {
        // TODO: Re-Implement filtering, sorting and pagination @rcMarty
        // Issue: https://github.com/Trails-Through-Shadows/TTS-API/issues/31

        List<ActionDTO> entries = actionRepo.findAll().stream()
                .filter((entry) -> Filtering.match(entry, List.of(filter.split(","))))
                .sorted((a, b) -> Sorting.compareTo(a, b, List.of(sort.split(","))))
                .toList();

        List<ActionDTO> entriesPage = entries.stream()
                .skip((long) Math.max(page, 0) * limit)
                .limit(limit)
                .toList();

        if (lazy && !include.isEmpty()) {
            entriesPage.forEach(e -> Initialization.hibernateInitializeAll(e, include));
        } else if (!lazy) {
            entriesPage.forEach(Initialization::hibernateInitializeAll);
        }

        Pagination pagination = new Pagination(entriesPage.size(), (entries.size() > (Math.max(page, 0) + 1) * limit), entries.size(), page, limit);
        return new ResponseEntity<>(RestPaginatedResult.of(pagination, entriesPage.stream().map(Action::fromDTO).toList()), HttpStatus.OK);
    }

    @GetMapping("/actions/{id}")
    //@Cacheable(value = "action", key = "#id")
    public ResponseEntity<Action> findById(
            @PathVariable int id,
            @RequestParam(required = false, defaultValue = "") List<String> include,
            @RequestParam(required = false, defaultValue = "false") boolean lazy
    ) {
        ActionDTO entity = actionRepo
                .findById(id)
                .orElseThrow(() -> RestException.of(HttpStatus.NOT_FOUND, "Action with id '%d' not found!", id));

        if (!lazy) {
            Initialization.hibernateInitializeAll(entity);
        } else {
            Initialization.hibernateInitializeAll(entity, include);
        }

        return new ResponseEntity<>(Action.fromDTO(entity), HttpStatus.OK);
    }

    @PutMapping("/actions/{id}")
    public ResponseEntity<MessageResponse> update(@PathVariable int id, @RequestBody ActionDTO action) {
        validation.validate(action);

        ActionDTO entityToUpdate = actionRepo
                .findById(id)
                .orElseThrow(() -> RestException.of(HttpStatus.NOT_FOUND, "Action with id '%d' not found!", id));

        entityToUpdate.setTitle(action.getTitle());
        entityToUpdate.setDescription(action.getDescription());
        entityToUpdate.setDiscard(action.getDiscard());
        entityToUpdate.setLevelReq(action.getLevelReq());

        List<AttackEffect> attackEffects = new ArrayList<>();
        if (action.getAttack() != null && action.getAttack().getEffects() != null) {
            attackEffects.addAll(action.getAttack().getEffects());
            action.getAttack().getEffects().clear();
        }
        entityToUpdate.setAttack(action.getAttack());

        List<MovementEffect> movementEffects = new ArrayList<>();
        if (action.getMovement() != null && action.getMovement().getEffects() != null) {
            movementEffects.addAll(action.getMovement().getEffects());
            action.getMovement().getEffects().clear();
        }
        entityToUpdate.setMovement(action.getMovement());

        List<SkillEffect> skillEffects = new ArrayList<>();
        if (action.getSkill() != null && action.getSkill().getEffects() != null) {
            skillEffects.addAll(action.getSkill().getEffects());
            action.getSkill().getEffects().clear();
        }
        entityToUpdate.setSkill(action.getSkill());

        entityToUpdate.setRestoreCards(action.getRestoreCards());
        entityToUpdate.setSummonActions(null); // Ptfuj

        // Save the entity
        log.info(entityToUpdate.toString());
        entityToUpdate = actionRepo.saveAndFlush(entityToUpdate);

        // Movement effects
        if (entityToUpdate.getMovement() != null && entityToUpdate.getMovement().getEffects() != null) {
            for (MovementEffect movementEffect : movementEffects) {
                EffectDTO effect = processEffects(movementEffect.getEffect());

                movementEffect.setKey(new MovementEffect.MovementEffectId(entityToUpdate.getMovement().getId(), effect.getId()));
                movementEffect.setEffect(effect);
            }

            entityToUpdate.getMovement().getEffects().addAll(movementEffects);
        }

        // Skill effects
        if (entityToUpdate.getSkill() != null && entityToUpdate.getSkill().getEffects() != null) {
            for (SkillEffect skillEffect : skillEffects) {
                EffectDTO effect = processEffects(skillEffect.getEffect());

                skillEffect.setKey(new SkillEffect.SkillEffectId(entityToUpdate.getSkill().getId(), effect.getId()));
                skillEffect.setEffect(effect);
            }

            entityToUpdate.getSkill().getEffects().addAll(skillEffects);
        }

        // Attack effects
        if (entityToUpdate.getAttack() != null && entityToUpdate.getAttack().getEffects() != null) {
            for (AttackEffect attackEffect : attackEffects) {
                EffectDTO effect = processEffects(attackEffect.getEffect());

                attackEffect.setKey(new AttackEffect.AttackEffectId(entityToUpdate.getAttack().getId(), effect.getId()));
                attackEffect.setEffect(effect);
            }

            entityToUpdate.getAttack().getEffects().addAll(attackEffects);
        }

        ActionDTO lateSave = actionRepo.saveAndFlush(entityToUpdate);
        return new ResponseEntity<>(MessageResponse.of(HttpStatus.OK, "Action with id '%d' updated!", id), HttpStatus.OK);
    }

    private EffectDTO processEffects(EffectDTO inputEffect) {
        List<EffectDTO> effects = effectRepo.findUnique(
                inputEffect.getTarget(),
                inputEffect.getType(),
                inputEffect.getDuration(),
                inputEffect.getStrength()
        );

        EffectDTO effect = null;
        if (effects.isEmpty()) {
            log.info("Effect {} not found, creating new", inputEffect);
            effect = effectRepo.saveAndFlush(inputEffect);
        } else {
            log.info("Effect {} found", inputEffect);
            effect = effects.getFirst();
        }

        return effect;
    }

    @DeleteMapping("/actions/{id}")
    public ResponseEntity<MessageResponse> delete(@PathVariable int id) {
        ActionDTO entity = actionRepo
                .findById(id)
                .orElseThrow(() -> RestException.of(HttpStatus.NOT_FOUND, "Action with id '%d' not found!", id));

        actionRepo.delete(entity);
        return new ResponseEntity<>(MessageResponse.of(HttpStatus.OK, "Action with id '%d' deleted!", id),
                HttpStatus.OK);
    }

    @PostMapping("/actions")
    public ResponseEntity<MessageResponse> createList(@RequestBody List<ActionDTO> actions) {
        List<String> ids = new ArrayList<>();

        for (ActionDTO action : actions) {
            validation.validate(action);

            //copy action into entityToUpdate
            ActionDTO entityToUpdate = new ActionDTO(action);

            entityToUpdate.setAttack(null);
            entityToUpdate.setMovement(null);
            entityToUpdate.setSkill(null);
            entityToUpdate.setRestoreCards(null);
            entityToUpdate = actionRepo.saveAndFlush(entityToUpdate);

            List<AttackEffect> attackEffects = new ArrayList<>();
            if (action.getAttack() != null && action.getAttack().getEffects() != null) {
                attackEffects.addAll(action.getAttack().getEffects());
                action.getAttack().getEffects().clear();
            }
            entityToUpdate.setAttack(action.getAttack());

            List<MovementEffect> movementEffects = new ArrayList<>();
            if (action.getMovement() != null && action.getMovement().getEffects() != null) {
                movementEffects.addAll(action.getMovement().getEffects());
                action.getMovement().getEffects().clear();
            }
            entityToUpdate.setMovement(action.getMovement());

            List<SkillEffect> skillEffects = new ArrayList<>();
            if (action.getSkill() != null && action.getSkill().getEffects() != null) {
                skillEffects.addAll(action.getSkill().getEffects());
                action.getSkill().getEffects().clear();
            }
            entityToUpdate.setSkill(action.getSkill());

            entityToUpdate.setRestoreCards(action.getRestoreCards());
            entityToUpdate.setSummonActions(null); // Ptfuj

            // Save the entity
            log.info(entityToUpdate.toString());
            entityToUpdate = actionRepo.saveAndFlush(entityToUpdate);

            // Movement effects
            if (entityToUpdate.getMovement() != null && entityToUpdate.getMovement().getEffects() != null) {
                for (MovementEffect movementEffect : movementEffects) {
                    EffectDTO effect = processEffects(movementEffect.getEffect());

                    movementEffect.setKey(new MovementEffect.MovementEffectId(entityToUpdate.getMovement().getId(), effect.getId()));
                    movementEffect.setEffect(effect);
                }

                entityToUpdate.getMovement().getEffects().addAll(movementEffects);
            }

            // Skill effects
            if (entityToUpdate.getSkill() != null && entityToUpdate.getSkill().getEffects() != null) {
                for (SkillEffect skillEffect : skillEffects) {
                    EffectDTO effect = processEffects(skillEffect.getEffect());

                    skillEffect.setKey(new SkillEffect.SkillEffectId(entityToUpdate.getSkill().getId(), effect.getId()));
                    skillEffect.setEffect(effect);
                }

                entityToUpdate.getSkill().getEffects().addAll(skillEffects);
            }

            // Attack effects
            if (entityToUpdate.getAttack() != null && entityToUpdate.getAttack().getEffects() != null) {
                for (AttackEffect attackEffect : attackEffects) {
                    EffectDTO effect = processEffects(attackEffect.getEffect());

                    attackEffect.setKey(new AttackEffect.AttackEffectId(entityToUpdate.getAttack().getId(), effect.getId()));
                    attackEffect.setEffect(effect);
                }

                entityToUpdate.getAttack().getEffects().addAll(attackEffects);
            }

            ActionDTO lateSave = actionRepo.saveAndFlush(entityToUpdate);
            ids.add(String.valueOf(lateSave.getId()));
        }

        return new ResponseEntity<>(MessageResponse.of(HttpStatus.OK, "Actions %s created!", String.join(", ", ids)),
                HttpStatus.OK);
    }

    @Autowired
    public void setRepository(ActionRepo repository) {
        this.actionRepo = repository;
    }

    @Autowired
    public void setEffectRepo(EffectRepo effectRepo) {
        this.effectRepo = effectRepo;
    }

    @Autowired
    public void setValidation(ValidationService validation) {
        this.validation = validation;
    }
}
