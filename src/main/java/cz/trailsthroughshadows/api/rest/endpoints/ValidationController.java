package cz.trailsthroughshadows.api.rest.endpoints;

import cz.trailsthroughshadows.algorithm.validation.Validable;
import cz.trailsthroughshadows.algorithm.validation.Validation;
import cz.trailsthroughshadows.algorithm.validation.ValidationResponse;
import cz.trailsthroughshadows.api.rest.exception.RestException;
import cz.trailsthroughshadows.api.rest.model.RestResponse;
import cz.trailsthroughshadows.api.rest.model.error.RestError;
import cz.trailsthroughshadows.api.rest.model.error.type.MessageError;
import cz.trailsthroughshadows.api.table.schematic.hex.model.Hex;
import cz.trailsthroughshadows.api.table.schematic.hex.model.HexDTO;
import cz.trailsthroughshadows.api.table.schematic.part.model.Part;
import cz.trailsthroughshadows.api.table.schematic.part.model.PartDTO;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController(value = "validation")
public class ValidationController {

    @PostMapping("/validate/part")
    public ResponseEntity<RestResponse> validatePart(@RequestBody PartDTO part) {
        return validate(Part.fromDTO(part));
    }
    @PostMapping("/validate/hex")
    public ResponseEntity<RestResponse> validateHex(@RequestBody HexDTO hex) {
        return validate(Hex.fromDTO(hex));
    }

    public ResponseEntity<RestResponse> validate(Validable validable) {
        ValidationResponse response = Validation.validate(validable);

        if (response.valid()) {
            return new ResponseEntity<>(new RestResponse(HttpStatus.OK, response.message()), HttpStatus.OK);
        }

        RestError error = new RestError(HttpStatus.NOT_ACCEPTABLE, response.message());
        for (var e : response.errors()) {
            error.addSubError(new MessageError(e));
        }

        throw new RestException(error);
    }
}
