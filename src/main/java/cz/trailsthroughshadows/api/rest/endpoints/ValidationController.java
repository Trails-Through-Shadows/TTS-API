package cz.trailsthroughshadows.api.rest.endpoints;

import cz.trailsthroughshadows.algorithm.validation.Validable;
import cz.trailsthroughshadows.algorithm.validation.Validation;
import cz.trailsthroughshadows.algorithm.validation.ValidationResponse;
import cz.trailsthroughshadows.api.rest.exception.RestException;
import cz.trailsthroughshadows.api.rest.model.RestResponse;
import cz.trailsthroughshadows.api.rest.model.error.RestError;
import cz.trailsthroughshadows.api.rest.model.error.type.MessageError;
import cz.trailsthroughshadows.api.table.schematic.part.model.Part;
import cz.trailsthroughshadows.api.table.schematic.part.model.PartDTO;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Log4j2
@Component
@RestController(value = "validation")
public class ValidationController {

    @PostMapping("/validate/part")
    public ResponseEntity<RestResponse> validatePart(@RequestBody PartDTO part) {
        return validate(Part.fromDTO(part));
    }

    public ResponseEntity<RestResponse> validate(Validable validable) {
        ValidationResponse response = Validation.validate(validable);

        if (response.isValid()) {
            return new ResponseEntity<>(new RestResponse(HttpStatus.OK, response.getMessage()), HttpStatus.OK);
        }

        RestError error = new RestError(HttpStatus.NOT_ACCEPTABLE, response.getMessage());
        for (var e : response.getErrors()) {
            log.debug(" > " + e);
            error.addSubError(new MessageError(e));
        }

        throw new RestException(error);
    }
}
