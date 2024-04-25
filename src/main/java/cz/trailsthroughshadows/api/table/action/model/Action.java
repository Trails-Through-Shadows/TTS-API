package cz.trailsthroughshadows.api.table.action.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import cz.trailsthroughshadows.api.table.action.features.attack.Attack;
import cz.trailsthroughshadows.api.table.action.features.movement.Movement;
import cz.trailsthroughshadows.api.table.action.features.skill.Skill;
import lombok.Getter;
import org.hibernate.Hibernate;
import org.modelmapper.ModelMapper;

@Getter
public class Action extends ActionDTO {

    @JsonProperty("skill")
    private Object mappedSkill;

    @JsonProperty("movement")
    private Object mappedMovement;

    @JsonProperty("attack")
    private Object mappedAttack;

    public static Action fromDTO(ActionDTO dto) {
        if (dto == null) {
            return null;
        }
        ModelMapper modelMapper = new ModelMapper();
        Action action = modelMapper.map(dto, Action.class);


        if (Hibernate.isInitialized(dto.getSkill())) {
            action.mappedSkill = Skill.fromDTO(dto.getSkill());
        } else {
            action.mappedSkill = dto.getSkill();
        }
        if (Hibernate.isInitialized(dto.getMovement())) {
            action.mappedMovement = Movement.fromDTO(dto.getMovement());
        } else {
            action.mappedMovement = dto.getMovement();
        }
        if (Hibernate.isInitialized(dto.getAttack())) {
            action.mappedAttack = Attack.fromDTO(dto.getAttack());
        } else {
            action.mappedAttack = dto.getAttack();
        }

        return action;
    }
}
