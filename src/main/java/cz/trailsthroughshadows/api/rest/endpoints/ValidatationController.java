package cz.trailsthroughshadows.api.rest.endpoints;

import cz.trailsthroughshadows.api.rest.exception.RestException;
import cz.trailsthroughshadows.api.rest.model.RestResponse;
import cz.trailsthroughshadows.api.rest.model.error.RestError;
import cz.trailsthroughshadows.api.rest.model.error.type.MessageError;
import cz.trailsthroughshadows.api.table.schematic.part.Part;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Component
@RestController(value = "/validation/")
public class ValidatationController {

    @PostMapping("/validate/part")
    public ResponseEntity<RestResponse> validatePart(@RequestBody Part part) {

        List<String> errors = new ArrayList<>();
        // TODO: Implement Part validation @Bačkorče
        if (part.getHexes().size() < 5) {
            errors.add("Part must have at least 5 hexes!");
        }

        if (errors.isEmpty()) {
            return new ResponseEntity<>(RestResponse.of("Part is valid."), HttpStatus.OK);
        }

        RestError error = new RestError(HttpStatus.NOT_ACCEPTABLE, "Part is not valid!");

        for (var e : errors) {
            error.addSubError(new MessageError("Part with id '%d' already exists!", e));
        }

        throw new RestException(error);

    }
}
