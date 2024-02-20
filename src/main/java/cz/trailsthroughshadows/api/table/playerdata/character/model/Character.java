package cz.trailsthroughshadows.api.table.playerdata.character.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import cz.trailsthroughshadows.api.util.ImageLoader;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Character extends CharacterDTO {

    private String url;

    public String getUrl() {
        return ImageLoader.getPath(new ArrayList<>(Arrays.asList(getRace().getTag(), getClazz().getTag())));
    }


    public static Character fromDTO(CharacterDTO dto) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(dto, Character.class);
    }
}
