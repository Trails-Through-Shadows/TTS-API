package cz.trailsthroughshadows.api.table.market.market.model;
import org.modelmapper.ModelMapper;

public class Market extends MarketDTO {


    public static Market fromDTO(MarketDTO dto) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(dto, Market.class);
    }
}
