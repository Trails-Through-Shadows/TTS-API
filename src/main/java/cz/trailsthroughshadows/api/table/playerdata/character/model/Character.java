package cz.trailsthroughshadows.api.table.playerdata.character.model;

import cz.trailsthroughshadows.api.images.ImageLoader;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.Arrays;


@Data
@EqualsAndHashCode(callSuper = true)
public class Character extends CharacterDTO {

    private String url;

    public static Character fromDTO(CharacterDTO dto) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(dto, Character.class);
    }

    public String getUrl() {
        if (url == null) {
            url = ImageLoader.getPath(new ArrayList<>(Arrays.asList(getRace().getTag(), getClazz().getTag())));
        }
        return url;
    }

    public Integer getDefence() {
        // todo add defence from items
        return getClazz().getBaseDefence();
    }

    public Integer getHealth() {
        // todo add health from items
        return getClazz().getBaseHealth();
    }

    public Integer getInitiative() {
        // todo add init from items
        return getClazz().getBaseInitiative() + getRace().getBaseInitiative();
    }

    @Override
    public String toString() {
        return "%s (%s %s): %s".formatted(getTitle(), getRace().getTitle(), getClazz().getTitle(), getPlayerName());
    }
}
