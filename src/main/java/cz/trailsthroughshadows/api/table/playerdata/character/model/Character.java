package cz.trailsthroughshadows.api.table.playerdata.character.model;

import cz.trailsthroughshadows.api.util.ImageLoader;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.Arrays;


@Data
@EqualsAndHashCode(callSuper = true)
public class Character extends CharacterDTO {

    private String url;
    private Integer initiative;
    private Integer idPart;

    //TODO vcalculate initiative form class and race and as afterfetch or how it is called
    public Integer getInitiative() {
        return 50;
    }

    public String getUrl() {
        return ImageLoader.getPath(new ArrayList<>(Arrays.asList(getRace().getTag(), getClazz().getTag())));
    }


    public static Character fromDTO(CharacterDTO dto) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(dto, Character.class);
    }
}
