package cz.trailsthroughshadows.api.table.market.item.model;

import org.modelmapper.ModelMapper;

public class Item extends ItemDTO {

    public static Item fromDTO(ItemDTO dto) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(dto, Item.class);
    }
}
