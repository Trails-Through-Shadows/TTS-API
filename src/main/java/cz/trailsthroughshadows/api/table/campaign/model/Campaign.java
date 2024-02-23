package cz.trailsthroughshadows.api.table.campaign.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import cz.trailsthroughshadows.algorithm.util.Color;
import cz.trailsthroughshadows.algorithm.util.TextUtils;
import cz.trailsthroughshadows.api.table.schematic.location.model.dto.LocationDTO;
import cz.trailsthroughshadows.api.table.schematic.location.model.dto.LocationPathDTO;
import org.modelmapper.ModelMapper;

public class Campaign extends CampaignDTO {

    public static Campaign fromDTO(CampaignDTO dto) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(dto, Campaign.class);
    }

     public String getTree() {
        JsonArray nodes = new JsonArray();
        JsonArray links = new JsonArray();

        for (LocationDTO location : getMappedLocations()) {
            JsonObject node = new JsonObject();
            node.addProperty("key", location.getId());
            node.addProperty("text", location.getTitle());
            node.addProperty("fill", Color.getRandom());
            nodes.add(node);

            JsonObject size = new JsonObject();
            size.addProperty("width", TextUtils.getTextWidth(location.getTitle()) + 15);
            size.addProperty("height", 30);
            node.add("size", size);

            if (location.getPaths() != null) {
                for (LocationPathDTO path : location.getPaths()) {
                    JsonObject link = new JsonObject();
                    link.addProperty("from", location.getId());
                    link.addProperty("to", path.getIdEnd());
                    links.add(link);
                }
            }
        }

        JsonObject tree = new JsonObject();
        tree.add("nodes", nodes);
        tree.add("links", links);

        return tree.toString();
     }
}
