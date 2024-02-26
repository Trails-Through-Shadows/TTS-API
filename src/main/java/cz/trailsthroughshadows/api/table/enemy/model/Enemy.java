package cz.trailsthroughshadows.api.table.enemy.model;

import cz.trailsthroughshadows.api.images.ImageLoader;
import cz.trailsthroughshadows.api.table.enemy.model.dto.EnemyDTO;
import cz.trailsthroughshadows.api.table.schematic.hex.model.dto.HexDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.modelmapper.ModelMapper;

@Data
@EqualsAndHashCode(callSuper = true)
// @JsonInclude(JsonInclude.Include.NON_NULL)
public class Enemy extends EnemyDTO {

    private HexDTO hex;

    private String url;

    public String getUrl() {
        return ImageLoader.getPath(getTag());
    }

    public static Enemy fromDTO(EnemyDTO dto) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(dto, Enemy.class);
    }

    public static Enemy fromDTO(EnemyDTO dto, HexDTO hex) {
        Enemy enemy = fromDTO(dto);
        enemy.setHex(hex);

        return enemy;
    }

    @Override
    public String toString() {
        return getTitle();
    }
}
