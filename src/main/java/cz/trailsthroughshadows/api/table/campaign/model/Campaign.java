package cz.trailsthroughshadows.api.table.campaign.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import cz.trailsthroughshadows.api.table.schematic.location.model.dto.LocationDTO;
import cz.trailsthroughshadows.api.table.schematic.location.model.dto.LocationPathDTO;
import org.modelmapper.ModelMapper;

public class Campaign extends CampaignDTO {

    public static Campaign fromDTO(CampaignDTO dto) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(dto, Campaign.class);
    }

     public String getTree() {
        JsonArray tree = new JsonArray();

        for (LocationDTO location : getMappedLocations()) {
            JsonObject node = new JsonObject();
            node.addProperty("type", "node");
            node.addProperty("id", location.getId());
            node.addProperty("title", location.getTitle());

            tree.add(node);

            for (LocationPathDTO path : location.getPaths()) {
                JsonObject link = new JsonObject();
                link.addProperty("type", "link");
                link.addProperty("source", location.getId());
                link.addProperty("target", path.getIdEnd());

                tree.add(link);
            }
        }

        return tree.toString();
     }
}
