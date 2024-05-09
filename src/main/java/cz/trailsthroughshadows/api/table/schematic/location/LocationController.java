package cz.trailsthroughshadows.api.table.schematic.location;

import cz.trailsthroughshadows.algorithm.validation.ValidationService;
import cz.trailsthroughshadows.api.rest.exception.RestException;
import cz.trailsthroughshadows.api.rest.model.pagination.Pagination;
import cz.trailsthroughshadows.api.rest.model.pagination.RestPaginatedResult;
import cz.trailsthroughshadows.api.rest.model.response.MessageResponse;
import cz.trailsthroughshadows.api.table.campaign.CampaignRepo;
import cz.trailsthroughshadows.api.table.schematic.hex.model.dto.HexEnemyDTO;
import cz.trailsthroughshadows.api.table.schematic.hex.model.dto.HexObstacleDTO;
import cz.trailsthroughshadows.api.table.schematic.location.model.Location;
import cz.trailsthroughshadows.api.table.schematic.location.model.dto.LocationDTO;
import cz.trailsthroughshadows.api.table.schematic.location.model.dto.LocationDoorDTO;
import cz.trailsthroughshadows.api.table.schematic.location.model.dto.LocationPartDTO;
import cz.trailsthroughshadows.api.table.schematic.location.model.dto.LocationStartDTO;
import cz.trailsthroughshadows.api.table.schematic.part.model.Part;
import cz.trailsthroughshadows.api.table.schematic.part.model.PartDTO;
import cz.trailsthroughshadows.api.util.reflect.Filtering;
import cz.trailsthroughshadows.api.util.reflect.Initialization;
import cz.trailsthroughshadows.api.util.reflect.Sorting;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController(value = "Location")
public class LocationController {

    private ValidationService validation;
    private LocationRepo locationRepo;
    private CampaignRepo campaignRepo;

    @Operation(
            summary = "Get all locations",
            description = """
                    # Get all locations
                    This endpoint retrieves all location records with support for advanced query capabilities such as pagination, filtering, sorting, and selective field loading. By default, it employs lazy loading of items.

                    **Parameters**:
                    - `page` - Specifies the page number, starting from 0.
                    - `limit` - Number of locations per page, default is 100.
                    - `filter` - Defines the conditions for filtering the locations. Supported operations include eq, of, is, gt, gte, lt, lte, has, and bwn.
                    - `sort` - Defines the order of the results. Format example: &sort=name:asc,importance:desc.
                    - `include` - Specifies which fields to load; if empty, all fields are considered.
                    - `lazy` - Determines if only specified fields should be loaded (true) or all fields (false).

                    These parameters allow for detailed customization of the returned data, accommodating various user needs for data retrieval and display.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All locations retrieved successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = RestPaginatedResult.class))}),
            @ApiResponse(responseCode = "default", description = "Unexpected error",
                    content = @Content)
    })
    @GetMapping("/locations")
    @Cacheable(value = "location", key = "T(java.util.Objects).hash(#page, #limit, #filter, #sort, #include, #lazy)")
    public ResponseEntity<RestPaginatedResult<Location>> getLocations(
            @Parameter(description = "Page number, starts from 0. Helps in paginating the result set.", required = false)
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of locations per page. Determines the size of each page of results.", required = false)
            @RequestParam(defaultValue = "100") int limit,
            @Parameter(description = "Filter conditions in the format: &filter=name:eq:Evergreen Forest,type:has:Natural,... Supported operations include: eq, of, is, gt, gte, lt, lte, has, bwn (between, numbers are split by _).", required = false)
            @RequestParam(defaultValue = "") String filter,
            @Parameter(description = "Sorting parameters in the format: &sort=name:asc,area:desc,... Controls the order in which locations are returned.", required = false)
            @RequestParam(defaultValue = "") String sort,
            @Parameter(description = "Specifies the fields to be loaded, which is case sensitive. If left empty, all fields are loaded.", required = false)
            @RequestParam(required = false, defaultValue = "") List<String> include,
            @Parameter(description = "Controls the loading of fields: **true** loads only specified fields in 'include', **false** loads all fields.", required = false)
            @RequestParam(required = false, defaultValue = "true") boolean lazy
    ) {
        // TODO: Re-Implement filtering, sorting and pagination
        // Issue: https://github.com/Trails-Through-Shadows/TTS-API/issues/31

        List<LocationDTO> entries = locationRepo.findAll().stream()
                .filter((entry) -> Filtering.match(entry, List.of(filter.split(","))))
                .sorted((a, b) -> Sorting.compareTo(a, b, List.of(sort.split(","))))
                .toList();

        List<LocationDTO> entriesPage = entries.stream()
                .skip((long) Math.max(page, 0) * limit)
                .limit(limit)
                .toList();

        Pagination pagination = new Pagination(entriesPage.size(), (entries.size() > (Math.max(page, 0) + 1) * limit), entries.size(), page, limit);

        if (lazy && !include.isEmpty()) {
            entriesPage.forEach(e -> Initialization.hibernateInitializeAll(e, include));
        } else if (!lazy) {
            entriesPage.forEach(Initialization::hibernateInitializeAll);
        }

        return new ResponseEntity<>(RestPaginatedResult.of(pagination, entriesPage.stream().map(Location::fromDTO).toList()), HttpStatus.OK);
    }

    @Operation(
            summary = "Get Location by ID",
            description = """
                    # Get Location by ID
                    Retrieves detailed information about a specific location using its unique identifier. This endpoint supports selective field loading through optional parameters, allowing for optimized data retrieval tailored to specific needs.

                    **Parameters**:
                    - `id` - The unique identifier of the location to be retrieved. This is required and cannot be empty.
                    - `include` - Optional. Specifies the case-sensitive fields to be loaded. If left empty, all fields are loaded.
                    - `lazy` - Optional. Controls the loading of fields: if set to **true**, only fields specified in 'include' are loaded; if **false** or omitted, all fields are loaded.

                    This method is designed to efficiently retrieve detailed data on individual locations, providing flexibility in data retrieval and reducing overhead for systems and applications.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Location successfully retrieved",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Location.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid ID supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Location not found",
                    content = @Content),
            @ApiResponse(responseCode = "default", description = "Unexpected error",
                    content = @Content)
    })
    @GetMapping("/locations/{id}")
    @Cacheable(value = "location", key = "T(java.util.Objects).hash(#id, #include, #lazy)")
    public ResponseEntity<Location> getLocationById(
            @Parameter(description = "The unique identifier of the location to be retrieved. Cannot be empty.", required = true)
            @PathVariable int id,
            @Parameter(description = "Specifies the case-sensitive fields to be loaded. Leave empty to load all fields.", required = false)
            @RequestParam(required = false, defaultValue = "") List<String> include,
            @Parameter(description = "Controls the loading of fields: **false** - All fields are loaded; **true** - Only specified fields in 'include' are loaded.", required = false)
            @RequestParam(required = false, defaultValue = "false") boolean lazy
    ) {
        LocationDTO entity = locationRepo
                .findById(id)
                .orElseThrow(() -> RestException.of(HttpStatus.NOT_FOUND, "Location with id '{}' not found! " + id));

        if (!lazy) {
            Initialization.hibernateInitializeAll(entity);
        } else {
            Initialization.hibernateInitializeAll(entity, include);
        }


        return new ResponseEntity<>(Location.fromDTO(entity), HttpStatus.OK);
    }

    @Operation(
            summary = "Get Part by Location ID",
            description = """
                    # Get Part by Location ID
                    Retrieves detailed information about a specific part within a given location using both the location's ID and the part's ID. This endpoint allows precise access to parts of locations, facilitating detailed queries about segments or components of larger geographical or structural areas.

                    **Parameters**:
                    - `idLocation` - The unique identifier of the location to which the part belongs. This identifier is required.
                    - `idPart` - The unique identifier of the part within the location that is to be retrieved. This identifier is also required.

                    This method is particularly useful for accessing detailed data about specific sections or components of a location, which may be crucial for applications dealing with geographic, urban, or architectural data.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Part successfully retrieved",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Part.class))}),
            @ApiResponse(responseCode = "404", description = "Location or part not found",
                    content = @Content),
            @ApiResponse(responseCode = "default", description = "Unexpected error",
                    content = @Content)
    })
    @GetMapping("/locations/{idLocation}/parts/{idPart}")
    @Cacheable(value = "location", key = "T(java.util.Objects).hash(#idLocation, #idPart)")
    public ResponseEntity<Part> getPartByLocationId(
            @Parameter(description = "The unique identifier of the location to which the part belongs. Cannot be empty.", required = true)
            @PathVariable Integer idLocation,
            @Parameter(description = "The unique identifier of the part within the location that is to be retrieved. Cannot be empty.", required = true)
            @PathVariable Integer idPart
    ) {

        LocationDTO locationDTO = locationRepo
                .findById(idLocation)
                .orElseThrow(() -> RestException.of(HttpStatus.NOT_FOUND, "Location with id '{}' not found!", idLocation));
        Location location = Location.fromDTO(locationDTO);

        PartDTO part = location.getMappedParts().stream()
                .filter(p -> p.getId() == idPart)
                .findFirst()
                .orElseThrow(() -> RestException.of(HttpStatus.NOT_FOUND, "Part with id '{}' not found!", idPart));
        // TODO zoze add doors here

        Part retPart = Part.fromDTO(part, location.getObstacles(), location.getDoors());

        if (retPart.getId() == location.getParts().get(0).getKey().getIdPart()) {
            retPart.setStartingHexes(location.getMappedStartHexes());
        }

        return new ResponseEntity<>(retPart, HttpStatus.OK);
    }

    @Operation(
            summary = "Create multiple locations",
            description = """
                    # Create multiple locations
                    This endpoint allows for the batch creation of multiple locations at once. Clients must provide a list of location details in the request body.

                    **Parameters**:
                    - `locations` - List of location details; each entry must conform to the LocationDTO specification for successful creation.

                    This method is particularly useful for setting up geographical or conceptual spaces within an application, facilitating bulk data management and initialization.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All locations created successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = MessageResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid data in request body",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Not authorized to perform this operation",
                    content = @Content),
            @ApiResponse(responseCode = "default", description = "Unexpected error",
                    content = @Content)
    })
    @PostMapping("/locations")
    @Transactional
    @CacheEvict(value = "location", allEntries = true)
    public ResponseEntity<MessageResponse> createLocation(
            @Parameter(description = "List of location data to be created. Each entry must conform to the LocationDTO structure and include all necessary details as required by the system.", required = true)
            @RequestBody List<LocationDTO> locations
    ) {
        log.debug("Creating locations: " + locations);

        // Validate all locations
        locations.forEach(validation::validate);
        locations.forEach(location -> location.setId(null));

        // Remove relations and save them for later
        Map<String, List<HexEnemyDTO>> hexEnemyRelations = new HashMap<>();
        Map<String, List<HexObstacleDTO>> hexObstacleRelations = new HashMap<>();
        Map<String, List<LocationPartDTO>> locationPartRelations = new HashMap<>();
        Map<String, List<LocationDoorDTO>> locationDoorRelations = new HashMap<>();
        Map<String, List<LocationStartDTO>> locationStartRelations = new HashMap<>();

        locations.forEach(location -> {
            hexEnemyRelations.put(location.getTag(), new ArrayList<>(location.getEnemies()));
            location.setEnemies(null);

            hexObstacleRelations.put(location.getTag(), new ArrayList<>(location.getObstacles()));
            location.setObstacles(null);

            locationPartRelations.put(location.getTag(), new ArrayList<>(location.getParts()));
            location.setParts(null);

            locationDoorRelations.put(location.getTag(), new ArrayList<>(location.getDoors()));
            location.setDoors(null);

            locationStartRelations.put(location.getTag(), new ArrayList<>(location.getStartHexes()));
            location.setStartHexes(null);
        });

        // Save locations
        locations.forEach(locationRepo::save);

        // Post load relations
        locations.forEach(location -> {
            List<HexEnemyDTO> hexEnemies = hexEnemyRelations.get(location.getTag());
            List<HexObstacleDTO> hexObstacles = hexObstacleRelations.get(location.getTag());
            List<LocationPartDTO> locationParts = locationPartRelations.get(location.getTag());
            List<LocationDoorDTO> locationDoors = locationDoorRelations.get(location.getTag());
            List<LocationStartDTO> locationStarts = locationStartRelations.get(location.getTag());

            location.setEnemies(hexEnemies);
            location.getEnemies().forEach(hexEnemy -> hexEnemy.getKey().setIdLocation(location.getId()));

            location.setObstacles(hexObstacles);
            location.getObstacles().forEach(hexObstacle -> hexObstacle.getKey().setIdLocation(location.getId()));

            location.setParts(locationParts);
            location.getParts().forEach(locationPart -> locationPart.setKey(
                    new LocationPartDTO.LocationPartId(location.getId(), locationPart.getPart().getId())));

            location.setDoors(locationDoors);
            location.getDoors().forEach(locationDoor -> locationDoor.getKey().setIdLocation(location.getId()));

            location.setStartHexes(locationStarts);
            location.getStartHexes().forEach(locationStart -> locationStart.setIdLocation(location.getId()));

            locationRepo.save(location);
        });

        String ids = locations.stream()
                .map(LocationDTO::getId)
                .map(String::valueOf)
                .reduce((a, b) -> a + ", " + b)
                .orElse("");
        return new ResponseEntity<>(MessageResponse.of(HttpStatus.OK, "Locations created: " + ids), HttpStatus.OK);
    }

    @Operation(
            summary = "Update an existing location",
            description = """
                    # Update an existing location
                    Updates a location using its unique identifier with the provided location details. This operation requires:
                    - `id` - The unique identifier of the location to be updated. It must be provided as a path variable.
                    - `location` - The updated details of the location, provided within the request body.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Location successfully updated",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = MessageResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid input or bad request",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Not authorized to perform this operation",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Location not found",
                    content = @Content),
            @ApiResponse(responseCode = "default", description = "Unexpected error",
                    content = @Content)
    })
    @PutMapping("/locations/{id}")
    @Transactional
    @CacheEvict(value = "location", allEntries = true)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<MessageResponse> updateLocationById(
            @Parameter(description = "The unique identifier of the location to be updated. Cannot be empty.", required = true)
            @PathVariable int id,
            @Parameter(description = "The location data to be used for the update. Cannot be null or empty.", required = true)
            @RequestBody LocationDTO location
    ) {
        LocationDTO locationToUpdate = locationRepo
                .findById(id)
                .orElseThrow(() -> RestException.of(HttpStatus.NOT_FOUND, "Location with id '{}' not found!", id));

        validation.validate(location);

        locationToUpdate.setTag(location.getTag());
        locationToUpdate.setTitle(location.getTitle());
        locationToUpdate.setDescription(location.getDescription());
        locationToUpdate.setEnemies(location.getEnemies());
        locationToUpdate.setObstacles(location.getObstacles());
        locationToUpdate.setDoors(location.getDoors());
        locationToUpdate.setParts(location.getParts());
        locationToUpdate.setStartHexes(location.getStartHexes());
        locationToUpdate.setType(location.getType());

        locationRepo.save(locationToUpdate);

        return new ResponseEntity<>(MessageResponse.of(HttpStatus.OK, "Location updated!"), HttpStatus.OK);
    }

    @Operation(
            summary = "Delete a location",
            description = """
                    # Delete a location
                    This endpoint allows for the deletion of a location identified by its unique identifier. It checks if the location exists within the system and proceeds to delete it, thereby permanently removing it from the database.

                    **Parameters**:
                    - `id` - The unique identifier of the location to be deleted. This ID must be provided in the path to ensure the correct location is targeted for deletion.

                    The deletion is performed as a transaction, ensuring that all data changes are consistent and any related data dependencies are handled correctly.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Location deleted successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = MessageResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid ID or request parameters",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized - User lacks necessary permissions to delete the location",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Location not found",
                    content = @Content),
            @ApiResponse(responseCode = "default", description = "Unexpected error occurred",
                    content = @Content)
    })
    @Transactional
    @DeleteMapping("/locations/{id}")
    @CacheEvict(value = "location", allEntries = true)
    public ResponseEntity<MessageResponse> deleteLocationById(
            @Parameter(description = "The unique identifier of the location to be deleted. Cannot be empty.", required = true)
            @PathVariable int id
    ) {

        LocationDTO location = locationRepo
                .findById(id)
                .orElseThrow(() -> RestException.of(HttpStatus.NOT_FOUND, "Location with id '{}' not found!", id));

        locationRepo.delete(location);
        return new ResponseEntity<>(MessageResponse.of(HttpStatus.OK, "Location deleted!"), HttpStatus.OK);
    }

    /**
     * ===============================================
     */

    @Autowired
    private void setLocationRepo(LocationRepo locationRepo) {
        this.locationRepo = locationRepo;
    }

    @Autowired
    public void setValidation(ValidationService validation) {
        this.validation = validation;
    }

    @Autowired
    public void setCampaignRepo(CampaignRepo campaignRepo) {
        this.campaignRepo = campaignRepo;
    }
}
