package cz.trailsthroughshadows.api.rest.endpoints;

import cz.trailsthroughshadows.api.rest.model.RestResponse;
import cz.trailsthroughshadows.api.table.schematic.part.Part;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ValidationService {

    public List<String> validatePart(Part part) {
        List<String> errors = new ArrayList<>();

        if (part.getHexes().size() < 5) {
            errors.add("Part must have at least 5 hexes!");
        }

        if (part.getHexes().size() > 50) {
            errors.add("Part must have at most 50 hexes!");
        }

        return errors;
    }
}
