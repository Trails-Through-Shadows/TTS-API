package cz.trailsthroughshadows.api.table.playerdata.character;

import cz.trailsthroughshadows.algorithm.session.Session;
import cz.trailsthroughshadows.algorithm.session.SessionHandler;
import cz.trailsthroughshadows.api.rest.exception.RestException;
import cz.trailsthroughshadows.api.rest.model.error.RestError;
import cz.trailsthroughshadows.api.rest.model.pagination.Pagination;
import cz.trailsthroughshadows.api.rest.model.pagination.RestPaginatedResult;
import cz.trailsthroughshadows.api.rest.model.response.RestResponse;
import cz.trailsthroughshadows.api.table.playerdata.adventure.AdventureService;
import cz.trailsthroughshadows.api.table.playerdata.adventure.model.AdventureDTO;
import cz.trailsthroughshadows.api.table.playerdata.character.model.Character;
import cz.trailsthroughshadows.api.table.playerdata.character.model.CharacterDTO;
import cz.trailsthroughshadows.api.util.reflect.Filtering;
import cz.trailsthroughshadows.api.util.reflect.Initialization;
import cz.trailsthroughshadows.api.util.reflect.Sorting;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/characters")
public class CharacterController {

    @Autowired
    SessionHandler sessionHandler;

    @Autowired
    CharacterService characterService;

    @Autowired
    AdventureService adventureService;

    @Operation(
            summary = "Get Character by ID",
            description = """
                    # Get Character by ID
                    Retrieves detailed information about a specific character using its unique identifier. This endpoint supports selective field loading through optional parameters, allowing for optimized data retrieval tailored to specific needs.

                    **Parameters**:
                    - `id` - The unique identifier of the character to be retrieved. This is required and cannot be empty.
                    - `include` - Optional. Specifies the case-sensitive fields to be loaded. If left empty, all fields are loaded.
                    - `lazy` - Optional. Controls the loading of fields: if set to **true**, only fields specified in 'include' are loaded; if **false** or omitted, all fields are loaded.

                    This method is designed to efficiently retrieve detailed data on individual characters, providing flexibility in data retrieval and reducing overhead for systems and applications.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Character successfully retrieved",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Character.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid ID supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Character not found",
                    content = @Content),
            @ApiResponse(responseCode = "default", description = "Unexpected error",
                    content = @Content)
    })
    @GetMapping("/{id}")
    @Cacheable(value = "character", key = "T(java.util.Objects).hash(#id, #include, #lazy)")
    public ResponseEntity<Character> findById(
            @Parameter(description = "The unique identifier of the character to be retrieved. Cannot be empty.", required = true)
            @PathVariable int id,
            @Parameter(description = "Specifies the case-sensitive fields to be loaded. Leave empty to load all fields.", required = false)
            @RequestParam(required = false, defaultValue = "") List<String> include,
            @Parameter(description = "Controls the loading of fields: **false** - All fields are loaded; **true** - Only specified fields in 'include' are loaded.", required = false)
            @RequestParam(required = false, defaultValue = "false") boolean lazy,
            HttpServletRequest request
    ) {
        Session session = sessionHandler.getSessionFromRequest(request);
        CharacterDTO entity = characterService.findById(id);
        AdventureDTO adventure = adventureService.findById(entity.getIdAdventure());

        if (!session.hasAccess(adventure.getIdLicense())) {
            throw new RestException(RestError.of(HttpStatus.FORBIDDEN, "You are not authorized to access this resource!"));
        }

        if (!lazy) {
            Initialization.hibernateInitializeAll(entity);
        } else {
            Initialization.hibernateInitializeAll(entity, include);
        }

        return new ResponseEntity<>(Character.fromDTO(entity), HttpStatus.OK);
    }

    @Operation(
            summary = "Get all characters",
            description = """
                    # Get all characters
                    This endpoint retrieves all character records with support for advanced query capabilities such as pagination, filtering, sorting, and selective field loading. By default, it employs lazy loading of items.

                    **Parameters**:
                    - `page` - Specifies the page number, starting from 0.
                    - `limit` - Number of items per page, default is 100.
                    - `filter` - Defines the conditions for filtering the characters. Supported operations include eq, of, is, gt, gte, lt, lte, has, and bwn.
                    - `sort` - Defines the order of the results. Format example: &sort=name:asc,level:desc.
                    - `include` - Specifies which fields to load; if empty, all fields are considered.
                    - `lazy` - Determines if only specified fields should be loaded (true) or all fields (false).

                    These parameters allow for detailed customization of the returned data, accommodating various user needs for data retrieval and display.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All characters retrieved successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = RestPaginatedResult.class))}),
            @ApiResponse(responseCode = "default", description = "Unexpected error",
                    content = @Content)
    })
    @GetMapping("")
    @Cacheable(value = "character", key = "T(java.util.Objects).hash(#page, #limit, #filter, #sort, #include, #lazy)")
    public ResponseEntity<RestPaginatedResult<Character>> findAllEntities(
            @Parameter(description = "Page number, starts from 0. Helps in paginating the result set.", required = false)
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page. Determines the size of each page of results.", required = false)
            @RequestParam(defaultValue = "100") int limit,
            @Parameter(description = "Filter conditions in the format: &filter=name:eq:John,class:has:Warrior,... Supported operations include: eq, of, is, gt, gte, lt, lte, has, bwn (between, numbers are split by _).", required = false)
            @RequestParam(defaultValue = "") String filter,
            @Parameter(description = "Sorting parameters in the format: &sort=level:asc,name:desc,... Controls the order in which characters are returned.", required = false)
            @RequestParam(defaultValue = "") String sort,
            @Parameter(description = "Specifies the fields to be loaded, which is case sensitive. If left empty, all fields are loaded.", required = false)
            @RequestParam(required = false, defaultValue = "") List<String> include,
            @Parameter(description = "Controls the loading of fields: **true** loads only specified fields in 'include', **false** loads all fields.", required = false)
            @RequestParam(required = false, defaultValue = "true") boolean lazy,
            @Parameter(hidden = true) HttpServletRequest request
    ) {
        // TODO: Re-Implement filtering, sorting and pagination @rcMarty
        // Issue: https://github.com/Trails-Through-Shadows/TTS-API/issues/31

        Session session = sessionHandler.getSessionFromRequest(request);

        List<CharacterDTO> entries = characterService.findAll().stream()
                .filter((entry) -> Filtering.match(entry, List.of(filter.split(","))) &&
                        session.hasAccess(adventureService.findById(entry.getIdAdventure()).getIdLicense()))
                .sorted((a, b) -> Sorting.compareTo(a, b, List.of(sort.split(","))))
                .toList();

        List<CharacterDTO> entriesPage = entries.stream()
                .skip((long) Math.max(page, 0) * limit)
                .limit(limit)
                .toList();

        if (lazy && !include.isEmpty()) {
            entriesPage.forEach(e -> Initialization.hibernateInitializeAll(e, include));
        } else if (!lazy) {
            entriesPage.forEach(Initialization::hibernateInitializeAll);
        }
        List<Character> characters = entriesPage.stream().map(Character::fromDTO).toList();

        Pagination pagination = new Pagination(entriesPage.size(), false, entriesPage.size(), page, limit);
        return new ResponseEntity<>(RestPaginatedResult.of(pagination, characters), HttpStatus.OK);
    }

    @Operation(
            summary = "Update an existing character",
            description = """
                    # Update an existing character
                    Updates a character using its unique identifier with the provided character details. This operation requires:
                    - `id` - The unique identifier of the character to be updated. It must be provided as a path variable.
                    - `character` - The updated details of the character, provided within the request body.
                    Additionally, the session information is retrieved from the HTTP request to ensure that the operation is performed within the context of a valid session.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Character successfully updated",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = RestResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid input or bad request",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Not authorized to perform this operation",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Character not found",
                    content = @Content),
            @ApiResponse(responseCode = "default", description = "Unexpected error",
                    content = @Content)
    })
    @PutMapping("/{id}")
    @CacheEvict(value = "character", allEntries = true)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<RestResponse> updateCharacter(
            @Parameter(description = "The unique identifier of the character to be updated. Cannot be empty.", required = true)
            @PathVariable int id,
            @Parameter(description = "The character data to be used for the update. Cannot be null or empty.", required = true)
            @RequestBody CharacterDTO character,
            @Parameter(hidden = true) HttpServletRequest request
    ) {
        Session session = sessionHandler.getSessionFromRequest(request);
        return new ResponseEntity<>(characterService.update(id, character, session), HttpStatus.OK);
    }

    @Operation(
            summary = "Delete a character",
            description = """
                    # Delete a character
                    This endpoint allows for the deletion of a character specified by its unique identifier. It checks the existence of the character in the system and then deletes it, permanently removing it from the database.

                    **Parameters**:
                    - `id` - The unique identifier of the character to be deleted. This ID must be provided in the path to locate and ensure the correct character is targeted for deletion.

                    This operation requires user authorization to confirm that only users with appropriate permissions can delete characters, maintaining the security and integrity of the data.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Character deleted successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = RestResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid ID or request parameters",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized - User lacks necessary permissions to delete the character",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Character not found",
                    content = @Content),
            @ApiResponse(responseCode = "default", description = "Unexpected error occurred",
                    content = @Content)
    })
    @DeleteMapping("/{id}")
    @CacheEvict(value = "character", allEntries = true)
    public ResponseEntity<RestResponse> deleteCharacter(
            @Parameter(description = "The unique identifier of the character to be deleted. Cannot be empty.", required = true)
            @PathVariable int id,
            HttpServletRequest request) {

        Session session = sessionHandler.getSessionFromRequest(request);
        return new ResponseEntity<>(characterService.delete(id, session), HttpStatus.OK);
    }
}
