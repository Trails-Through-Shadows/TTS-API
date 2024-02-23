package cz.trailsthroughshadows.api.table.playerdata.character.model;

import cz.trailsthroughshadows.api.util.ImageLoader;
import lombok.Getter;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.Arrays;

@Getter
public class Character extends CharacterDTO {

    private String url;
    private Integer initiative;

    public String getUrl() {
        return ImageLoader.getPath(new ArrayList<>(Arrays.asList(getRace().getTag(), getClazz().getTag())));
    }


    public static Character fromDTO(CharacterDTO dto) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(dto, Character.class);
    }
}
