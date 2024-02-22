package cz.trailsthroughshadows.api.table.campaign.model;

import com.google.gson.JsonObject;
import org.modelmapper.ModelMapper;

public class Campaign extends CampaignDTO {

    public static Campaign fromDTO(CampaignDTO dto) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(dto, Campaign.class);
    }

     public JsonObject getTree() {
        // todo implement
        return new JsonObject();
     }
}
