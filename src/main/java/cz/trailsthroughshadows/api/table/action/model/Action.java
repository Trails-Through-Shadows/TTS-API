package cz.trailsthroughshadows.api.table.action.model;

import org.modelmapper.ModelMapper;

public class Action extends ActionDTO {

    public static Action fromDTO(ActionDTO dto) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(dto, Action.class);
    }
}
