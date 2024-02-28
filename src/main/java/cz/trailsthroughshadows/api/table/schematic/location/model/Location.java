package cz.trailsthroughshadows.api.table.schematic.location.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import cz.trailsthroughshadows.api.images.ImageLoader;
import cz.trailsthroughshadows.api.table.campaign.CampaignRepo;
import cz.trailsthroughshadows.api.table.campaign.model.Story;
import cz.trailsthroughshadows.api.table.schematic.hex.model.Hex;
import cz.trailsthroughshadows.api.table.schematic.location.model.dto.LocationDTO;
import cz.trailsthroughshadows.api.table.schematic.location.model.dto.LocationPartDTO;
import cz.trailsthroughshadows.api.table.schematic.part.model.Part;
import cz.trailsthroughshadows.api.table.schematic.part.model.PartDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
@EqualsAndHashCode(callSuper = true)
public class Location extends LocationDTO {

    private String url;

    private List<Story> stories;

    public String getUrl() {
        if (url == null)
            url = ImageLoader.getPath(this.getTag());
        return ImageLoader.getPath(this.getTag());
    }

    public List<Story> findStories(CampaignRepo campaignRepo) {
        if (stories == null) {
            stories = campaignRepo.findAllStoriesByCampaignId(this.getId());
        }
        return stories;
    }

    @JsonIgnore
    public Part getStartPart() {

        ModelMapper modelMapper = new ModelMapper();
        List<Part> parts = getParts().stream()
                .map(LocationPartDTO::getPart)
                .map(partDTO -> modelMapper.map(partDTO, Part.class))
                .toList();

        PartDTO tmp = parts.stream()
                .filter(part -> Objects.equals(
                        part.getId(),
                        this.getStartHexes()
                                .stream()
                                .findFirst()
                                .orElseThrow(
                                        () -> new RuntimeException("No start part found in location " + this.getId()))
                                .getIdPart())) // hoping there will be only one starting part
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No start part found in location " + this.getId()));

        return Part.fromDTO(tmp);
    }

    /**
     * Get all mapped Hexes from LocationStartDTO
     * 
     * @return list of mapped hexes
     */
    @JsonIgnore
    public List<Hex> getStartingHexes() {
        List<Hex> hexes = startHexes.stream()
                .map(s -> Hex.fromDTO(s.getHex()))
                .toList();
        return hexes;
    }

    // TODO: Map locations only by specific campaign frrom database @rcMarty
    public static Location fromDTO(LocationDTO dto) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(dto, Location.class);
    }

    @JsonIgnore
    public List<Part> getMappedParts() {
        if (parts == null) {
            return new ArrayList<>();
        }

        return parts.stream()
                .map(locationPart -> Part.fromDTO(
                        locationPart.getPart(),
                        locationPart.getRotation(),
                        this.getEnemies(),
                        this.getObstacles()))
                .toList();
    }

    public enum Type {
        CITY, DUNGEON, MARKET, QUEST
    }
}
