package cz.trailsthroughshadows.api.table.schematic.hex.model;
import com.fasterxml.jackson.annotation.JsonProperty;
import cz.trailsthroughshadows.api.table.enemy.model.Enemy;
import cz.trailsthroughshadows.api.table.schematic.hex.model.dto.HexEnemyDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.Hibernate;
import org.modelmapper.ModelMapper;

@EqualsAndHashCode(callSuper = true)
@Data
public class HexEnemy extends HexEnemyDTO {

    @JsonProperty("enemy")
    private Object remappedEnemy;

    public static HexEnemy fromDTO(HexEnemyDTO dto) {
        ModelMapper modelMapper = new ModelMapper();
        HexEnemy hexEnemy = modelMapper.map(dto, HexEnemy.class);

        if (Hibernate.isInitialized(dto.getEnemy())) {
            hexEnemy.remappedEnemy = modelMapper.map(dto.getEnemy(), Enemy.class);
        } else {
            hexEnemy.remappedEnemy = dto.getEnemy();
        }

        hexEnemy.remappedEnemy = modelMapper.map(dto.getEnemy(), Enemy.class);
        return hexEnemy;
    }
}
