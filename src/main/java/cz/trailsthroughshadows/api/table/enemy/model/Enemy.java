package cz.trailsthroughshadows.api.table.enemy.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import cz.trailsthroughshadows.api.table.enemy.model.dto.EnemyDTO;
import cz.trailsthroughshadows.api.table.schematic.hex.Hex;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.modelmapper.ModelMapper;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Enemy extends EnemyDTO {

    private Hex hex;

    public static Enemy fromDTO(EnemyDTO dto) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(dto, Enemy.class);
    }

    public static Enemy fromDTO(EnemyDTO dto, Hex hex) {
        Enemy enemy = fromDTO(dto);
        enemy.setHex(hex);

        return enemy;
    }
}
