package cz.trailsthroughshadows.api.table.market.item.model;

import cz.trailsthroughshadows.api.images.ImageLoader;
import org.modelmapper.ModelMapper;

public class Item extends ItemDTO {

    private String url;

    public static Item fromDTO(ItemDTO dto) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(dto, Item.class);
    }

    public String getUrl() {
        return ImageLoader.getPath(getTag());
    }
}
