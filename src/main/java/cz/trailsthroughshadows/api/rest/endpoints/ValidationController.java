package cz.trailsthroughshadows.api.rest.endpoints;

import cz.trailsthroughshadows.algorithm.validation.PartValidation;
import cz.trailsthroughshadows.api.rest.exception.RestException;
import cz.trailsthroughshadows.api.rest.model.RestResponse;
import cz.trailsthroughshadows.api.rest.model.error.RestError;
import cz.trailsthroughshadows.api.rest.model.error.type.MessageError;
import cz.trailsthroughshadows.api.table.schematic.part.model.Part;
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
    public ResponseEntity<RestResponse> validatePart(@RequestBody Part part) {
        log.debug("Validating part " + part.getTag());

        List<String> errors = PartValidation.validate(part);

        if (errors.isEmpty()) {
            log.debug("Part is valid!");
            return new ResponseEntity<>(new RestResponse(HttpStatus.OK, "Part is valid!"), HttpStatus.OK);
        }

        log.debug("Part is not valid!");
        RestError error = new RestError(HttpStatus.NOT_ACCEPTABLE, "Part is not valid!");
        for (var e : errors) {
            log.debug(" > " + e);
            error.addSubError(new MessageError(e));
        }

        throw new RestException(error);
    }
}
