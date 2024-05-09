package cz.trailsthroughshadows.api.table.effect;

import cz.trailsthroughshadows.algorithm.validation.ValidationService;
import cz.trailsthroughshadows.api.rest.exception.RestException;
import cz.trailsthroughshadows.api.rest.model.pagination.Pagination;
import cz.trailsthroughshadows.api.rest.model.pagination.RestPaginatedResult;
import cz.trailsthroughshadows.api.rest.model.response.MessageResponse;
import cz.trailsthroughshadows.api.table.effect.model.Effect;
import cz.trailsthroughshadows.api.table.effect.model.EffectDTO;
import cz.trailsthroughshadows.api.table.effect.model.EffectEnum;
import cz.trailsthroughshadows.api.util.reflect.Filtering;
import cz.trailsthroughshadows.api.util.reflect.Initialization;
import cz.trailsthroughshadows.api.util.reflect.Sorting;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController(value = "Effect")
public class EffectController {

    private ValidationService validation;
    private EffectRepo effectRepo;

    @Operation(
            summary = "Get all effects",
            description = """
                    # Get all effects
                    This endpoint retrieves all effects with support for advanced query capabilities such as pagination, filtering, sorting, and selective field loading. By default, it employs lazy loading of items.

                    **Parameters**:
                    - `page` - Specifies the page number, starting from 0.
                    - `limit` - Number of items per page, default is 100.
                    - `filter` - Defines the conditions for filtering the effects. Supported operations include eq, of, is, gt, gte, lt, lte, has, and bwn.
                    - `sort` - Defines the order of the results. Format example: &sort=name:asc,duration:desc.
                    - `include` - Specifies which fields to load; if empty, all fields are considered.
                    - `lazy` - Determines if only specified fields should be loaded (true) or all fields (false).

                    These parameters allow for detailed customization of the returned data, accommodating various user needs for data retrieval and display.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All effects retrieved successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = RestPaginatedResult.class))}),
            @ApiResponse(responseCode = "default", description = "Unexpected error",
                    content = @Content)
    })
    @GetMapping("/effects")
    @Cacheable(value = "effect", key = "T(java.util.Objects).hash(#page, #limit, #filter, #sort, #include, #lazy)")
    public ResponseEntity<RestPaginatedResult<Effect>> getEffects(
            @Parameter(description = "Page number, starts from 0. Helps in paginating the result set.", required = false)
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page. Determines the size of each page of results.", required = false)
            @RequestParam(defaultValue = "100") int limit,
            @Parameter(description = "Filter conditions in the format: &filter=type:eq:Magical,name:has:fire,... Supported operations include: eq, of, is, gt, gte, lt, lte, has, bwn (between, numbers are split by _).", required = false)
            @RequestParam(defaultValue = "") String filter,
            @Parameter(description = "Sorting parameters in the format: &sort=name:asc,duration:desc,... Controls the order in which effects are returned.", required = false)
            @RequestParam(defaultValue = "") String sort,
            @Parameter(description = "Specifies the fields to be loaded, which is case sensitive. If left empty, all fields are loaded.", required = false)
            @RequestParam(required = false, defaultValue = "") List<String> include,
            @Parameter(description = "Controls the loading of fields: **true** loads only specified fields in 'include', **false** loads all fields.", required = false)
            @RequestParam(required = false, defaultValue = "true") boolean lazy
    ) {
        // TODO: Re-Implement filtering, sorting and pagination @rcMarty
        // Issue: https://github.com/Trails-Through-Shadows/TTS-API/issues/31

        List<EffectDTO> entries = effectRepo.findAll().stream()
                .filter((entry) -> Filtering.match(entry, List.of(filter.split(","))))
                .sorted((a, b) -> Sorting.compareTo(a, b, List.of(sort.split(","))))
                .toList();

        List<EffectDTO> entriesPage = entries.stream()
                .skip((long) Math.max(page, 0) * limit)
                .limit(limit)
                .toList();

        if (lazy && !include.isEmpty()) {
            entriesPage.forEach(e -> Initialization.hibernateInitializeAll(e, include));
        } else if (!lazy) {
            entriesPage.forEach(Initialization::hibernateInitializeAll);
        }

        Pagination pagination = new Pagination(entriesPage.size(), (entries.size() > (Math.max(page, 0) + 1) * limit),
                entries.size(), page, limit);
        return new ResponseEntity<>(
                RestPaginatedResult.of(pagination, entriesPage.stream().map(Effect::fromDTO).toList()), HttpStatus.OK);
    }

    @Operation(
            summary = "Get All Effect Types",
            description = """
                    # Get All Effect Types
                    Retrieves a list of all possible effect types defined in the system's enumeration. This endpoint is useful for understanding the various types of effects that can be applied within the system, such as in configurations or during operations that manipulate or rely on effect-based logic.

                    This endpoint returns an array of effect types, helping clients understand and select appropriate effects for different scenarios.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Effect types successfully retrieved",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = EffectEnum.class))}),
            @ApiResponse(responseCode = "default", description = "Unexpected error",
                    content = @Content)
    })
    @GetMapping("/enum/effectType")
    public ResponseEntity<List<EffectEnum>> getEffectType() {
        List<EffectEnum> obj = Arrays.stream(EffectDTO.EffectType.values())
                .map(EffectEnum::fromEnum).toList();

        return new ResponseEntity<>(obj, HttpStatus.OK);
    }

    @GetMapping("/enum/effectTarget")
    public ResponseEntity<List<EffectDTO.EffectTarget>> getEffectTarget() {
        return new ResponseEntity<>(Arrays.asList(EffectDTO.EffectTarget.values()), HttpStatus.OK);
    }

    @Operation(
            summary = "Get Effect by ID",
            description = """
                    # Get Effect by ID
                    Retrieves detailed information about a specific effect using its unique identifier. This endpoint supports selective field loading through optional parameters, allowing for optimized data retrieval tailored to specific requirements.

                    **Parameters**:
                    - `id` - The unique identifier of the effect to be retrieved. This is required and cannot be empty.
                    - `include` - Optional. Specifies the case-sensitive fields to be loaded. If left empty, all fields are loaded.
                    - `lazy` - Optional. Controls the loading of fields: if set to **true**, only fields specified in 'include' are loaded; if **false** or omitted, all fields are loaded.

                    This method is designed to efficiently retrieve detailed data on individual effects, providing flexibility in data retrieval and reducing overhead.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Effect successfully retrieved",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Effect.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid ID supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Effect not found",
                    content = @Content),
            @ApiResponse(responseCode = "default", description = "Unexpected error",
                    content = @Content)
    })
    @GetMapping("/effects/{id}")
    @Cacheable(value = "effect", key = "T(java.util.Objects).hash(#id, #include, #lazy)")
    public ResponseEntity<Effect> findById(
            @Parameter(description = "The unique identifier of the effect to be retrieved. Cannot be empty.", required = true)
            @PathVariable int id,
            @Parameter(description = "Specifies the case-sensitive fields to be loaded. Leave empty to load all fields.", required = false)
            @RequestParam(required = false, defaultValue = "") List<String> include,
            @Parameter(description = "Controls the loading of fields: **false** - All fields are loaded; **true** - Only specified fields in 'include' are loaded.", required = false)
            @RequestParam(required = false, defaultValue = "false") boolean lazy
    ) {
        EffectDTO entity = effectRepo
                .findById(id)
                .orElseThrow(() -> RestException.of(HttpStatus.NOT_FOUND, "Effect with id '{}' not found!", id));

        if (lazy && !include.isEmpty()) {
            Initialization.hibernateInitializeAll(entity, include);
        } else if (!lazy) {
            Initialization.hibernateInitializeAll(entity);
        }

        return new ResponseEntity<>(Effect.fromDTO(entity), HttpStatus.OK);
    }

    @Operation(
            summary = "Update an existing effect",
            description = """
                    # Update an existing effect
                    Updates an effect entity using its unique identifier with the provided effect details. This operation requires:
                    - `id` - The unique identifier of the effect to be updated. It must be provided as a path variable.
                    - `effect` - The updated details of the effect, provided within the request body.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Effect successfully updated",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = MessageResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid input or bad request",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Not authorized to perform this operation",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Effect not found",
                    content = @Content),
            @ApiResponse(responseCode = "default", description = "Unexpected error",
                    content = @Content)
    })
    @PutMapping("/effects/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<MessageResponse> updateEffect(
            @Parameter(description = "The unique identifier of the effect to be updated. Cannot be empty.", required = true)
            @PathVariable int id,
            @Parameter(description = "The effect data to be used for the update. Cannot be null or empty.", required = true)
            @RequestBody EffectDTO effect
    ) {
        validation.validate(effect);
        EffectDTO existing = effectRepo.findById(id)
                .orElseThrow(() -> RestException.of(HttpStatus.NOT_FOUND, "Effect with id '{}' not found!", id));

        existing.setDescription(effect.getDescription());
        existing.setDuration(effect.getDuration());
        existing.setType(effect.getType());
        existing.setTarget(effect.getTarget());
        existing.setDuration(effect.getDuration());
        existing.setDescription(effect.getDescription());

        effectRepo.save(effect);
        return new ResponseEntity<>(MessageResponse.of(HttpStatus.OK, "Effect updated successfully!"), HttpStatus.OK);

    }

    @Operation(
            summary = "Create multiple effects",
            description = """
                    # Create multiple effects
                    This endpoint allows for the batch creation of multiple effects at once. Clients must provide a list of effect details in the request body.

                    **Parameters**:
                    - `effect` - List of effect details; each entry must conform to the EffectDTO specification for successful creation.

                    This method is particularly useful for initializing effect data or conducting bulk imports, offering an efficient way to handle multiple effect records simultaneously.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All effects created successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = MessageResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid data in request body",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Not authorized to perform this operation",
                    content = @Content),
            @ApiResponse(responseCode = "default", description = "Unexpected error",
                    content = @Content)
    })
    @PostMapping("/effects")
    public ResponseEntity<MessageResponse> createEffect(
            @Parameter(description = "List of effect data to be created. Each entry must conform to the EffectDTO structure and include all necessary details as required by the system.", required = true)
            @RequestBody List<EffectDTO> effect
    ) {
        effect.forEach(validation::validate);
        effect.forEach(e -> e.setId(null));
        effectRepo.saveAll(effect);

        String ids = effect.stream().map(e -> String.valueOf(e.getId())).toList().toString();
        return new ResponseEntity<>(MessageResponse.of(HttpStatus.OK, "Effects with ids '%s'", ids), HttpStatus.OK);
    }

    @Operation(
            summary = "Delete an effect",
            description = """
                    # Delete an effect
                    This endpoint facilitates the deletion of an effect identified by its unique identifier. Upon identifying the effect, it proceeds to delete it from the system, thereby permanently removing it from the database.

                    **Parameters**:
                    - `id` - The unique identifier of the effect to be deleted. This ID must be specified in the path to ensure the correct effect is targeted for deletion.

                    Deleting an effect requires proper authorization checks to ensure that only users with sufficient permissions can perform this action.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Effect deleted successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = MessageResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid ID or request parameters",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized - User lacks necessary permissions to delete the effect",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Effect not found",
                    content = @Content),
            @ApiResponse(responseCode = "default", description = "Unexpected error occurred",
                    content = @Content)
    })
    @DeleteMapping("/effects/{id}")
    public ResponseEntity<MessageResponse> deleteEffect(
            @Parameter(description = "The unique identifier of the effect to be deleted. Cannot be empty.", required = true)
            @PathVariable int id) {
        EffectDTO effect = effectRepo
                .findById(id)
                .orElseThrow(() -> RestException.of(HttpStatus.NOT_FOUND, "Effect with id '{}' not found!", id));

        effectRepo.delete(effect);
        return new ResponseEntity<>(MessageResponse.of(HttpStatus.OK, "Effect with id '{}' deleted!", id),
                HttpStatus.OK);
    }

    @Autowired
    public void setRepository(EffectRepo repository) {
        this.effectRepo = repository;
    }

    @Autowired
    public void setValidation(ValidationService validation) {
        this.validation = validation;
    }
}
