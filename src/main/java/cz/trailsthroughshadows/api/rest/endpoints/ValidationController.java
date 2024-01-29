package cz.trailsthroughshadows.api.rest.endpoints;

import cz.trailsthroughshadows.api.rest.exception.RestException;
import cz.trailsthroughshadows.api.rest.model.RestResponse;
import cz.trailsthroughshadows.api.rest.model.error.RestError;
import cz.trailsthroughshadows.api.rest.model.error.type.MessageError;
import cz.trailsthroughshadows.api.table.schematic.part.Part;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@Component
@RestController(value = "validation")
public class ValidationController {

    private ValidationService validationService;

    @PostMapping("/validate/part")
    public ResponseEntity<RestResponse> validatePart(@RequestBody Part part) {
        List<String> errors = validationService.validatePart(part);

        // TODO: Implement Part validation @Bačkorče
        // Validations?:
        // 1. Part must have at least 5 hexes
        // 2. Part must have at most 50 hexes
        // 3. Part is maximum 8 hexes wide and 8 hexes tall
        // 4. All hexes must be connected

        if (errors.isEmpty()) {
            return new ResponseEntity<>(new RestResponse(HttpStatus.OK, "Part is valid!"), HttpStatus.OK);
        }

        RestError error = new RestError(HttpStatus.NOT_ACCEPTABLE, "Part is not valid!");
        for (var e : errors) {
            error.addSubError(new MessageError(e));
        }

        throw new RestException(error);
    }

    @Autowired
    public void setValidationService(ValidationService validationService) {
        this.validationService = validationService;
    }
}
