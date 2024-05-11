package cz.trailsthroughshadows.api.table.campaign;

import cz.trailsthroughshadows.algorithm.validation.ValidationService;
import cz.trailsthroughshadows.api.rest.exception.RestException;
import cz.trailsthroughshadows.api.rest.model.pagination.Pagination;
import cz.trailsthroughshadows.api.rest.model.pagination.RestPaginatedResult;
import cz.trailsthroughshadows.api.rest.model.response.MessageResponse;
import cz.trailsthroughshadows.api.table.campaign.model.Campaign;
import cz.trailsthroughshadows.api.table.campaign.model.CampaignAchievements;
import cz.trailsthroughshadows.api.table.campaign.model.CampaignDTO;
import cz.trailsthroughshadows.api.table.campaign.model.CampaignLocation;
import cz.trailsthroughshadows.api.util.Pair;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/campaigns")
public class CampaignController {
    private static final Logger log = LoggerFactory.getLogger(CampaignController.class);

    @Autowired
    private CampaignRepo campaignRepo;

    @Autowired
    private CampaignLocationRepo campaignLocationRepo;

    @Autowired
    private ValidationService validation;

    @Operation(
            summary = "Get all campaigns",
            description = """
                    # Get all campaigns
                    This endpoint retrieves all campaign records with support for advanced query capabilities such as pagination, filtering, sorting, and selective field loading. By default, it employs lazy loading of items.

                    **Parameters**:
                    - `page` - Specifies the page number, starting from 0.
                    - `limit` - Number of items per page, default is 100.
                    - `filter` - Defines the conditions for filtering the campaigns. Supported operations include eq, of, is, gt, gte, lt, lte, has, and bwn.
                    - `sort` - Defines the order of the results. Format example: &sort=name:asc,start_date:desc.
                    - `include` - Specifies which fields to load; if empty, all fields are considered.
                    - `lazy` - Determines if only specified fields should be loaded (true) or all fields (false).

                    These parameters allow for detailed customization of the returned data, accommodating various user needs for data retrieval and display.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All campaigns retrieved successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = RestPaginatedResult.class))}),
            @ApiResponse(responseCode = "default", description = "Unexpected error",
                    content = @Content)
    })
    @GetMapping("")
    @Cacheable(value = "campaign", key = "T(java.util.Objects).hash(#page, #limit, #filter, #sort, #include, #lazy)")
    public ResponseEntity<RestPaginatedResult<CampaignDTO>> findAllEntities(
            @Parameter(description = "Page number, starts from 0. Helps in paginating the result set.", required = false)
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page. Determines the size of each page of results.", required = false)
            @RequestParam(defaultValue = "100") int limit,
            @Parameter(description = "Filter conditions in the format: &filter=name:eq:Quest for the Holy Grail,start_date:gte:2020-01-01,... Supported operations include: eq, of, is, gt, gte, lt, lte, has, bwn (between, numbers are split by _).", required = false)
            @RequestParam(defaultValue = "") String filter,
            @Parameter(description = "Sorting parameters in the format: &sort=name:asc,start_date:desc,... Controls the order in which campaigns are returned.", required = false)
            @RequestParam(defaultValue = "") String sort,
            @Parameter(description = "Specifies the fields to be loaded, which is case sensitive. If left empty, all fields are loaded.", required = false)
            @RequestParam(required = false, defaultValue = "") List<String> include,
            @Parameter(description = "Controls the loading of fields: **true** loads only specified fields in 'include', **false** loads all fields.", required = false)
            @RequestParam(required = false, defaultValue = "true") boolean lazy
    ) {
        // TODO: Re-Implement filtering, sorting and pagination @rcMarty
        // Issue: https://github.com/Trails-Through-Shadows/TTS-API/issues/31

        List<CampaignDTO> entries = campaignRepo.findAll().stream()
                .filter((entry) -> Filtering.match(entry, List.of(filter.split(","))))
                .sorted((a, b) -> Sorting.compareTo(a, b, List.of(sort.split(","))))
                .toList();

        List<CampaignDTO> entriesPage = entries.stream()
                .skip((long) Math.max(page, 0) * limit)
                .limit(limit)
                .toList();

        if (lazy && !include.isEmpty()) {
            entriesPage.forEach(e -> Initialization.hibernateInitializeAll(e, include));
        } else if (!lazy) {
            entriesPage.forEach(Initialization::hibernateInitializeAll);
        }

        Pagination pagination = new Pagination(entriesPage.size(), false, entriesPage.size(), page, limit);
        return new ResponseEntity<>(RestPaginatedResult.of(pagination, entries), HttpStatus.OK);
    }

    @Operation(
            summary = "Get Campaign by ID",
            description = """
                    # Get Campaign by ID
                    Retrieves detailed information about a campaign using its unique identifier. This endpoint supports selective field loading through optional parameters, enabling optimized data retrieval based on specific needs.

                    **Parameters**:
                    - `id` - The unique identifier of the campaign to be retrieved. This is required and cannot be empty.
                    - `include` - Optional. Specifies the case-sensitive fields to be loaded. If left empty, all fields are loaded.
                    - `lazy` - Optional. Controls the loading of fields: if set to **true**, only fields specified in 'include' are loaded; if **false** or omitted, all fields are loaded.

                    This approach allows clients to fine-tune the response to fit specific use cases or to reduce network overhead.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Campaign successfully retrieved",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = CampaignDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid ID supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Campaign not found",
                    content = @Content),
            @ApiResponse(responseCode = "default", description = "Unexpected error",
                    content = @Content)
    })
    @GetMapping("/{id}")
    @Cacheable(value = "campaign", key = "T(java.util.Objects).hash(#id, #include, #lazy)")
    public CampaignDTO findById(
            @Parameter(description = "The unique identifier of the campaign to be retrieved. Cannot be empty.", required = true)
            @PathVariable int id,
            @Parameter(description = "Specifies the case-sensitive fields to be loaded. Leave empty to load all fields.", required = false)
            @RequestParam(required = false, defaultValue = "") List<String> include,
            @Parameter(description = "Controls the loading of fields: **false** - All fields are loaded; **true** - Only specified fields in 'include' are loaded.", required = false)
            @RequestParam(required = false, defaultValue = "false") boolean lazy
    ) {
        CampaignDTO entity = campaignRepo
                .findById(id)
                .orElseThrow(() -> RestException.of(HttpStatus.NOT_FOUND, "Action with id '{}' not found! " + id));

        if (!lazy)
            Initialization.hibernateInitializeAll(entity);
        else
            Initialization.hibernateInitializeAll(entity, include);

        return entity;

    }

    @Operation(
            summary = "Get Campaign Location by Campaign and Location IDs",
            description = """
                    # Get Campaign Location by Campaign and Location IDs
                    Retrieves detailed information about a specific location within a campaign using the campaign's unique identifier and the location's unique identifier. This endpoint supports selective field loading through optional parameters, allowing for optimized data retrieval.

                    **Parameters**:
                    - `id` - The unique identifier of the campaign.
                    - `idLocation` - The unique identifier of the location within the campaign.
                    - `include` - Optional. Specifies the case-sensitive fields to be loaded. If left empty, all fields are loaded.
                    - `lazy` - Optional. Controls the loading of fields: if set to **true**, only fields specified in 'include' are loaded; if **false** or omitted, all fields are loaded.

                    This method is ideal for retrieving specific location details within a broader campaign context, reducing the necessity to load full campaign data.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Campaign location successfully retrieved",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = CampaignLocation.class))}),
            @ApiResponse(responseCode = "404", description = "Campaign or location not found",
                    content = @Content),
            @ApiResponse(responseCode = "default", description = "Unexpected error",
                    content = @Content)
    })
    @GetMapping("/{id}/location/{idLocation}")
    @Cacheable(value = "campaign", key = "T(java.util.Objects).hash(#id, #idLocation, #include, #lazy)")
    public CampaignLocation findById2(
            @Parameter(description = "The unique identifier of the campaign. Cannot be empty.", required = true)
            @PathVariable int id,
            @Parameter(description = "The unique identifier of the location within the campaign. Cannot be empty.", required = true)
            @PathVariable int idLocation,
            @Parameter(description = "Specifies the case-sensitive fields to be loaded. Leave empty to load all fields.", required = false)
            @RequestParam(required = false, defaultValue = "") List<String> include,
            @Parameter(description = "Controls the loading of fields: **false** - All fields are loaded; **true** - Only specified fields in 'include' are loaded.", required = false)
            @RequestParam(required = false, defaultValue = "false") boolean lazy
    ) {
        CampaignDTO entity = campaignRepo
                .findById(id)
                .orElseThrow(() -> RestException.of(HttpStatus.NOT_FOUND, "Action with id '{}' not found! " + id));

        CampaignLocation location = entity.getLocations().stream()
                .filter(e -> e.getId() == idLocation)
                .findFirst()
                .orElseThrow(() -> RestException.of(HttpStatus.NOT_FOUND, "Location with id '{}' not found! " + idLocation));

        if (!lazy)
            Initialization.hibernateInitializeAll(location);
        else
            Initialization.hibernateInitializeAll(location, include);

        return location;
    }

    @Operation(
            summary = "Update an existing campaign",
            description = """
                    # Update an existing campaign
                    Updates a campaign using its unique identifier with the provided campaign details. This operation requires:
                    - `id` - The unique identifier of the campaign to be updated. It must be provided as a path variable.
                    - `campaign` - The updated details of the campaign, provided within the request body.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Campaign successfully updated",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = MessageResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid input or bad request",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Not authorized to perform this operation",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Campaign not found",
                    content = @Content),
            @ApiResponse(responseCode = "default", description = "Unexpected error",
                    content = @Content)
    })
    @PutMapping("/{id}")
    @CacheEvict(value = "campaign", allEntries = true)
    @Transactional
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<MessageResponse> update(
            @Parameter(description = "The unique identifier of the campaign to be updated. Cannot be empty.", required = true)
            @PathVariable int id,
            @Parameter(description = "The campaign data to be used for the update. Cannot be null or empty.", required = true)
            @RequestBody CampaignDTO campaign
    ) {
        log.debug("Updating campaign with id '{}': {}", id, campaign);

        // Validate campaign
        validation.validate(campaign);

        CampaignDTO campaignToUpdate = campaignRepo
                .findById(id)
                .orElseThrow(() -> RestException.of(HttpStatus.NOT_FOUND, "Campaign with id '{}' not found!", id));

        // Remove relations and save them for later
        List<CampaignAchievements> achievements = new ArrayList<>();
        if (campaign.getAchievements() != null) {
            achievements.addAll(campaign.getAchievements());
            campaign.getAchievements().clear();
        }

        List<CampaignLocation> locations = new ArrayList<>();
        if (campaign.getLocations() != null) {
            locations.addAll(campaign.getLocations());
            campaign.getLocations().clear();
        }

        // Update campaign
        campaignToUpdate.setTitle(campaign.getTitle());
        campaignToUpdate.setDescription(campaign.getDescription());
        campaignToUpdate = campaignRepo.save(campaignToUpdate);

        // Post load relations
        if (campaignToUpdate.getAchievements() != null) {
            campaignToUpdate.getAchievements().clear();
            campaignToUpdate.getAchievements().addAll(achievements);
        }

        if (campaignToUpdate.getLocations() != null) {
            for (CampaignLocation cl : locations) {
                cl.setIdCampaign(campaign.getId());

                if (cl.getStories() != null) {
                    cl.getStories().forEach(story -> {
                        if (story.getId() == 0) {
                            story.setId(null);
                        }

                        story.setIdCampaignLocation(cl.getId());
                    });
                }

                if (cl.getPaths() != null) {
                    cl.getPaths().forEach(path -> {
                        path.setIdCampaign(campaign.getId());
                    });
                }

//                StringBuilder sb = new StringBuilder();
//                sb.append("[");
//                sb.append(cl.getConditions().stream().map(Object::toString).reduce((a, b) -> a + ", " + b).orElse(""));
//                sb.append("]");
                cl.setConditionString(cl.getConditionString());

                campaignToUpdate.getLocations().clear();
                campaignToUpdate.getLocations().addAll(locations);
            }
        }

        if (!achievements.isEmpty() || !locations.isEmpty()) {
            campaignToUpdate = campaignRepo.save(campaignToUpdate);
        }

        return new ResponseEntity<>(MessageResponse.of(HttpStatus.OK, "Campaign with id '{}' updated!", id), HttpStatus.OK);
    }

    @Operation(
            summary = "Delete a campaign",
            description = """
                    # Delete a campaign
                    This endpoint facilitates the deletion of a campaign by its unique identifier. Once the campaign is identified, the system proceeds to delete it, thus permanently removing it from the database.

                    **Parameters**:
                    - `id` - The unique identifier of the campaign to be deleted. This identifier is required to locate the campaign in the system.

                    Only authorized users with the right privileges can perform this operation, ensuring the integrity and security of the data.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Campaign deleted successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = MessageResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "User not authorized to perform this operation",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Campaign not found",
                    content = @Content),
            @ApiResponse(responseCode = "default", description = "An unexpected error occurred",
                    content = @Content)
    })
    @Transactional
    @DeleteMapping("/{id}")
    @CacheEvict(value = "campaign", allEntries = true)
    public ResponseEntity<MessageResponse> delete(
            @Parameter(description = "The unique identifier of the campaign to be deleted. Cannot be empty.", required = true)
            @PathVariable int id) {
        CampaignDTO campaign = campaignRepo
                .findById(id)
                .orElseThrow(() -> RestException.of(HttpStatus.NOT_FOUND, "Campaign with id '{}' not found!", id));

        campaignRepo.delete(campaign);
        return new ResponseEntity<>(MessageResponse.of(HttpStatus.OK, "Campaign deleted successfully!"), HttpStatus.OK);
    }


    @Operation(
            summary = "Create multiple campaigns",
            description = """
                    # Create multiple campaigns
                    This endpoint allows for the batch creation of multiple campaigns at once. Clients must provide a list of campaign details in the request body.

                    **Parameters**:
                    - `campaigns` - List of campaign details; each entry must conform to the CampaignDTO specification for successful creation.

                    This method is particularly useful for initializing campaign data or conducting bulk imports, offering an efficient way to handle multiple campaign records simultaneously.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All campaigns created successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = MessageResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid data in request body",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Not authorized to perform this operation",
                    content = @Content),
            @ApiResponse(responseCode = "default", description = "Unexpected error",
                    content = @Content)
    })
    @PostMapping("")
    @CacheEvict(value = "campaign", allEntries = true)
    @Transactional
    public ResponseEntity<MessageResponse> create(
            @Parameter(description = "List of campaign data to be created. Each entry must conform to the CampaignDTO structure and include all necessary details as required by the system.", required = true)
            @RequestBody List<CampaignDTO> campaigns
    ) {
        log.info("Creating campaigns: {}", campaigns);

        // validate all and set ids to null
        campaigns.forEach(validation::validate);

        // Remove ids to prevent conflicts
        campaigns.forEach(e -> e.setId(null));

        // Remove relations and save them for later
        Map<String, Pair<List<CampaignAchievements>, List<CampaignLocation>>> achievementsAndLocations = new HashMap<>();

        campaigns.forEach(campaign -> {
            achievementsAndLocations.put(campaign.getTitle(), new Pair<>(new ArrayList<>(campaign.getAchievements()), new ArrayList<>(campaign.getLocations())));
            campaign.setAchievements(null);
            campaign.setLocations(null);
        });

        // Save all campaigns
        campaigns = campaignRepo.saveAll(campaigns);

        // Load relations
        campaigns.forEach(campaign -> {
            Pair<List<CampaignAchievements>, List<CampaignLocation>> pair = achievementsAndLocations.get(campaign.getTitle());
            campaign.setAchievements(new ArrayList<>(pair.first()));
            campaign.getAchievements().forEach(achievement -> achievement.getKey().setIdCampaign(campaign.getId()));

            List<CampaignLocation> campLoc = pair.second();
            for (CampaignLocation cl : campLoc) {
                cl.setId(null);
                cl.setIdCampaign(campaign.getId());
                cl.getStories().forEach(story -> {
                    story.setId(null);
                    story.setIdCampaignLocation(cl.getId());
                });
                cl.getPaths().forEach(path -> {
                    path.setIdCampaign(campaign.getId());
                });

                StringBuilder sb = new StringBuilder();
                sb.append("[");
                sb.append(cl.getConditions().stream().map(Object::toString).reduce((a, b) -> a + ", " + b).orElse(""));
                sb.append("]");
                cl.setConditionString(sb.toString());
            }

            // Save middle entities
            campLoc = campaignLocationRepo.saveAll(campLoc);
            campaign.setLocations(campLoc);
        });

        // Save all relations
        campaigns = campaignRepo.saveAll(campaigns);

        String ids = campaigns.stream().map(CampaignDTO::getId).map(String::valueOf).reduce((a, b) -> a + ", " + b).orElse("");
        return new ResponseEntity<>(MessageResponse.of(HttpStatus.OK, "Campaigns created: " + ids), HttpStatus.OK);
    }

    @Operation(
            summary = "Get Campaign Tree Structure",
            description = """
                    # Get Campaign Tree Structure
                    Retrieves the hierarchical tree structure of a campaign using its unique identifier. This endpoint is particularly useful for back-office applications where understanding the relational structure of campaign elements is necessary.

                    **Parameters**:
                    - `id` - The unique identifier of the campaign to retrieve the tree structure for. This identifier is required and must correspond to an existing campaign.

                    This method provides a visual or structured representation of the campaign elements, facilitating easier navigation and management in back-office systems.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Campaign tree structure successfully retrieved",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(type = "string"))}),  // Assuming the output is a JSON string.
            @ApiResponse(responseCode = "404", description = "Campaign not found",
                    content = @Content),
            @ApiResponse(responseCode = "default", description = "Unexpected error",
                    content = @Content)
    })
    @GetMapping(value = "/{id}/tree", produces = MediaType.APPLICATION_JSON_VALUE)
    @Cacheable(value = "campaignTree", key = "#id")
    public ResponseEntity<String> getTree(
            @Parameter(description = "The unique identifier of the campaign to retrieve its tree structure. Cannot be empty.", required = true)
            @PathVariable int id
    ) {
        Campaign campaign = Campaign.fromDTO(campaignRepo
                .findById(id)
                .orElseThrow(() -> RestException.of(HttpStatus.NOT_FOUND, "Campaign with id '{}' not found!", id)));

        return new ResponseEntity<>(campaign.getTree(), HttpStatus.OK);
    }

}
