package cz.trailsthroughshadows.api.table.action.features.summon.model;

import cz.trailsthroughshadows.api.images.ImageLoader;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.modelmapper.ModelMapper;

@Data
@EqualsAndHashCode(callSuper = true)
public class Summon extends SummonDTO {

    private String url;

    public static Summon fromDTO(SummonDTO dto) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(dto, Summon.class);
    }

    public String getUrl() {
        return ImageLoader.getPath(getTag());
    }

    @Override
    public String toString() {
        return getTitle();
    }
}
