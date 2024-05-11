package cz.trailsthroughshadows.api.table.playerdata.adventure;

import cz.trailsthroughshadows.algorithm.session.Session;
import cz.trailsthroughshadows.algorithm.session.SessionHandler;
import cz.trailsthroughshadows.api.rest.exception.RestException;
import cz.trailsthroughshadows.api.rest.model.error.RestError;
import cz.trailsthroughshadows.api.rest.model.pagination.Pagination;
import cz.trailsthroughshadows.api.rest.model.pagination.RestPaginatedResult;
import cz.trailsthroughshadows.api.rest.model.response.RestResponse;
import cz.trailsthroughshadows.api.table.playerdata.adventure.model.Adventure;
import cz.trailsthroughshadows.api.table.playerdata.adventure.model.AdventureDTO;
import cz.trailsthroughshadows.api.table.playerdata.character.CharacterService;
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
@RequestMapping("/adventures")
public class AdventureController {

    @Autowired
    private SessionHandler sessionHandler;

    @Autowired
    private AdventureService adventureService;

    @Autowired
    private CharacterService characterService;

    @Operation(
            summary = "Get Adventure by ID",
            description = """
                    # Get Adventure by ID
                    Retrieves an adventure by its unique identifier. Optionally, specific fields can be included or excluded in the response based on the 'include' and 'lazy' parameters.

                    **Parameters**:
                    - `id` - The unique identifier of the adventure to be retrieved. This is required and cannot be empty.
                    - `include` - Optional. Specifies the case-sensitive fields to be loaded. If left empty, all fields are loaded.
                    - `lazy` - Optional. Controls the loading of fields: if set to **true**, only fields specified in 'include' are loaded; if **false** or omitted, all fields are loaded.

                    The method ensures that data retrieval is efficient and customized as needed by the consumer of the API.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Adventure successfully retrieved",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Adventure.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid ID supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Adventure not found",
                    content = @Content),
            @ApiResponse(responseCode = "default", description = "Unexpected error",
                    content = @Content)
    })
    @GetMapping("/{id}")
    @Cacheable(value = "adventure", key = "T(java.util.Objects).hash(#id, #include, #lazy)")
    public ResponseEntity<Adventure> findById(
            @Parameter(description = "The unique identifier of the adventure to be retrieved. Cannot be empty.", required = true)
            @PathVariable int id,
            @Parameter(description = "Specifies the case-sensitive fields to be loaded. Leave empty to load all fields.", required = false)
            @RequestParam(required = false, defaultValue = "") List<String> include,
            @Parameter(description = "Controls the loading of fields: **false** - All fields are loaded; **true** - Only specified fields in 'include' are loaded.", required = false)
            @RequestParam(required = false, defaultValue = "false") boolean lazy,
            HttpServletRequest request
    ) {
        Session session = sessionHandler.getSessionFromRequest(request);
        AdventureDTO entity = adventureService.findById(id);

        if (!session.hasAccess(entity.getIdLicense())) {
            throw new RestException(RestError.of(HttpStatus.FORBIDDEN, "You are not authorized to access this resource!"));
        }

        if (!lazy) {
            Initialization.hibernateInitializeAll(entity);
        } else {
            Initialization.hibernateInitializeAll(entity, include);
        }
        Adventure res = Adventure.fromDTO(entity);

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @Operation(
            summary = "Get all adventures",
            description = """
                    # Get all adventures
                    This endpoint retrieves all adventure records with support for advanced query capabilities such as pagination, filtering, sorting, and selective field loading. By default, it employs lazy loading of items.

                    **Parameters**:
                    - `page` - Specifies the page number, starting from 0.
                    - `limit` - Number of items per page, default is 100.
                    - `filter` - Defines the conditions for filtering the adventures. Supported operations include eq, of, is, gt, gte, lt, lte, has, and bwn.
                    - `sort` - Defines the order of the results. Format example: &sort=id:asc,title:desc.
                    - `include` - Specifies which fields to load; if empty, all fields are considered.
                    - `lazy` - Determines if only specified fields should be loaded (true) or all fields (false).

                    These parameters allow for detailed customization of the returned data, accommodating various user needs for data retrieval and display.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All adventures retrieved successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = RestPaginatedResult.class))}),
            @ApiResponse(responseCode = "default", description = "Unexpected error",
                    content = @Content)
    })
    @GetMapping("")
    @Cacheable(value = "adventure", key = "T(java.util.Objects).hash(#page, #limit, #filter, #sort, #include, #lazy)")
    public ResponseEntity<RestPaginatedResult<AdventureDTO>> findAllEntities(
            @Parameter(description = "Page number, starts from 0. Helps in paginating the result set.", required = false)
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page. Determines the size of each page of results.", required = false)
            @RequestParam(defaultValue = "100") int limit,
            @Parameter(description = "Filter conditions in the format: &filter=title:eq:fireball,id:bwn:1_20,type:is:false,... Supported operations include: eq, of, is, gt, gte, lt, lte, has, bwn (between, numbers are split by _).", required = false)
            @RequestParam(defaultValue = "") String filter,
            @Parameter(description = "Sorting parameters in the format: &sort=id:asc,title:desc,... Controls the order in which adventures are returned.", required = false)
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

        List<AdventureDTO> entries = adventureService.findAll().stream()
                .filter((entry) -> Filtering.match(entry, List.of(filter.split(","))) &&
                        session.hasAccess(entry.getIdLicense()))
                .sorted((a, b) -> Sorting.compareTo(a, b, List.of(sort.split(","))))
                .toList();

        List<AdventureDTO> entriesPage = entries.stream()
                .skip((long) Math.max(page, 0) * limit)
                .limit(limit)
                .toList();

        if (lazy && !include.isEmpty()) {
            entriesPage.forEach(e -> Initialization.hibernateInitializeAll(e, include));
        } else if (!lazy) {
            entriesPage.forEach(Initialization::hibernateInitializeAll);
        }

        Pagination pagination = new Pagination(entriesPage.size(), false, entriesPage.size(), page, limit);
        return new ResponseEntity<>(RestPaginatedResult.of(pagination, entriesPage), HttpStatus.OK);
    }

    @Operation(
            summary = "Add an adventure",
            description = """
                    # Add an adventure
                    This endpoint is for adding a new adventure associated with a specific license. The client must provide the adventure details in the request body.
                            
                    **Parameters**:
                    - `idLicense` - The ID of the license to which the adventure is to be added.
                    - `adventure` - The details of the adventure, conforming to the AdventureDTO specification.

                    This method is useful for dynamically adding adventures to a given license, facilitating easy expansion and management of available adventures.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Adventure added successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = RestResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid data in request body or incorrect license ID",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Not authorized to perform this operation",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "License not found",
                    content = @Content),
            @ApiResponse(responseCode = "default", description = "Unexpected error",
                    content = @Content)
    })
    @PostMapping("/{idLicense}")
    @CacheEvict(value = "adventure", allEntries = true)
    public ResponseEntity<RestResponse> addAdventure(
            @Parameter(description = "The ID of the license to which the adventure is being added.", required = true)
            @PathVariable int idLicense,

            @Parameter(description = "Adventure details to be added. Must conform to the AdventureDTO specification.", required = true)
            @RequestBody AdventureDTO adventure,

            HttpServletRequest request
    ) {
        Session session = sessionHandler.getSessionFromRequest(request);
        return new ResponseEntity<>(adventureService.add(adventure, idLicense, session), HttpStatus.OK);
    }

    @Operation(
            summary = "Update an existing adventure",
            description = """
                    # Update an existing adventure
                    Updates an adventure using its unique identifier with the provided adventure details. This operation requires:
                    - `id` - The unique identifier of the adventure to be updated. It must be provided as a path variable.
                    - `adventure` - The updated details of the adventure, provided within the request body.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Adventure successfully updated",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = RestResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid input or bad request",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Not authorized to perform this operation",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Adventure not found",
                    content = @Content),
            @ApiResponse(responseCode = "default", description = "Unexpected error",
                    content = @Content)
    })
    @PutMapping("/{id}")
    @CacheEvict(value = "adventure", allEntries = true)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<RestResponse> updateAdventure(
            @Parameter(description = "The unique identifier of the adventure to be updated. Cannot be empty.", required = true)
            @PathVariable int id,
            @Parameter(description = "The adventure data to be used for the update. Cannot be null or empty.", required = true)
            @RequestBody AdventureDTO adventure,
            @Parameter(hidden = true) HttpServletRequest request
    ) {
        Session session = sessionHandler.getSessionFromRequest(request);
        return new ResponseEntity<>(adventureService.update(id, adventure, session), HttpStatus.OK);
    }

    @Operation(
            summary = "Delete an adventure",
            description = """
                    # Delete an adventure
                    This endpoint allows for the deletion of an adventure specified by its unique identifier. It checks if the adventure exists and then proceeds to delete it, permanently removing it from the system.

                    **Parameters**:
                    - `id` - The unique identifier of the adventure to be deleted. It must be provided in the path to execute the deletion.

                    The operation requires checking user authorization to ensure that only eligible users can delete adventures.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Adventure deleted successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = RestResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid input or request parameters",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Not authorized to perform this operation",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Adventure not found",
                    content = @Content),
            @ApiResponse(responseCode = "default", description = "Unexpected error",
                    content = @Content)
    })
    @DeleteMapping("/{id}")
    @CacheEvict(value = "adventure", allEntries = true)
    public ResponseEntity<RestResponse> deleteAdventure(
            @Parameter(description = "The unique identifier of the adventure to be deleted. Cannot be empty.", required = true)
            @PathVariable int id,
            HttpServletRequest request) {
        Session session = sessionHandler.getSessionFromRequest(request);
        return new ResponseEntity<>(adventureService.delete(id, session), HttpStatus.OK);
    }

    @Operation(
            summary = "Get all characters in an adventure",
            description = """
                    # Get all characters in an adventure
                    Retrieves all character records associated with a specific adventure ID, supporting advanced query capabilities such as pagination, filtering, sorting, and selective field loading. By default, it employs lazy loading of items.

                    **Parameters**:
                    - `id` - The adventure ID to which the characters are related.
                    - `page` - Specifies the page number, starting from 0.
                    - `limit` - Number of items per page, default is 100.
                    - `filter` - Defines the conditions for filtering the characters. Supported operations include eq, of, is, gt, gte, lt, lte, has, and bwn.
                    - `sort` - Defines the order of the results. Format example: &sort=name:asc,age:desc.
                    - `include` - Specifies which fields to load; if empty, all fields are considered.
                    - `lazy` - Determines if only specified fields should be loaded (true) or all fields (false).

                    These parameters allow for detailed customization of the returned data, accommodating various user needs for data retrieval and display.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All characters retrieved successfully for the specified adventure",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = RestPaginatedResult.class))}),
            @ApiResponse(responseCode = "default", description = "Unexpected error",
                    content = @Content)
    })
    @GetMapping("/{id}/characters")
    @Cacheable(value = "adventure", key = "T(java.util.Objects).hash(#page, #limit, #filter, #sort, #include, #lazy)")
    public ResponseEntity<RestPaginatedResult<Character>> findAllEntities(
            @Parameter(description = "Adventure ID from which characters are retrieved.", required = true)
            @PathVariable int id,
            @Parameter(description = "Page number, starts from 0. Helps in paginating the result set.", required = false)
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page. Determines the size of each page of results.", required = false)
            @RequestParam(defaultValue = "100") int limit,
            @Parameter(description = "Filter conditions in the format: &filter=name:eq:John,age:gte:30,... Supported operations include: eq, of, is, gt, gte, lt, lte, has, bwn (between, numbers are split by _).", required = false)
            @RequestParam(defaultValue = "") String filter,
            @Parameter(description = "Sorting parameters in the format: &sort=name:asc,level:desc,... Controls the order in which characters are returned.", required = false)
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
                        session.hasAccess(adventureService.findById(entry.getIdAdventure()).getIdLicense())
                        && entry.getIdAdventure() == id)
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
            summary = "Add a character to an adventure",
            description = """
                    # Add a character to an adventure
                    This endpoint allows clients to add a new character to an existing adventure using the adventure's unique ID. The details of the character must be provided in the request body.
                            
                    **Parameters**:
                    - `id` - The ID of the adventure to which the character is being added.
                    - `character` - The details of the character, adhering to the CharacterDTO specification.

                    This method facilitates the expansion of an adventure's story by allowing the addition of new characters dynamically.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Character added successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = RestResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid data in request body or incorrect adventure ID",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Not authorized to perform this operation",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Adventure not found",
                    content = @Content),
            @ApiResponse(responseCode = "default", description = "Unexpected error",
                    content = @Content)
    })
    @PostMapping("/{id}/characters")
    @CacheEvict(value = "adventure", allEntries = true)
    public ResponseEntity<RestResponse> addCharacter(
            @Parameter(description = "The ID of the adventure to which the character is being added. This ID should match an existing adventure in the system.", required = true)
            @PathVariable int id,

            @Parameter(description = "Character details to be added to the adventure. The provided data must conform to the CharacterDTO specification.", required = true)
            @RequestBody CharacterDTO character,

            HttpServletRequest request
    ) {
        Session session = sessionHandler.getSessionFromRequest(request);
        return new ResponseEntity<>(characterService.add(character, id, session), HttpStatus.OK);
    }
}
