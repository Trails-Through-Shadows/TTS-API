package cz.trailsthroughshadows.api.table.playerdata.adventure.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import cz.trailsthroughshadows.api.table.campaign.model.Campaign;
import cz.trailsthroughshadows.api.table.playerdata.character.model.Character;
import lombok.Getter;
import org.modelmapper.ModelMapper;

import java.util.Collection;

@Getter
public class Adventure extends AdventureDTO {

    //TODO here with lazy load is not serialized ID
    // if lazy only characters for example
    @JsonProperty("campaign")
    private Campaign campaign;

    @JsonProperty("characters")
    private Collection<Character> mappedcharacters;

    //private Collection<Location> adventureLocations;


    public static Adventure fromDTO(AdventureDTO dto) {
        ModelMapper mapper = new ModelMapper();
        Adventure adv = mapper.map(dto, Adventure.class);

        if (dto.getCharacters() != null)
            adv.mappedcharacters = dto.getCharacters().stream().map(Character::fromDTO).toList();
        if (dto.getCampaign() != null)
            adv.campaign = Campaign.fromDTO(dto.getCampaign());


        //TODO location to dao and other fields which has dao

        return adv;
    }
}
