package cz.trailsthroughshadows.api.table.market.item;

import cz.trailsthroughshadows.algorithm.validation.ValidationService;
import cz.trailsthroughshadows.api.rest.exception.RestException;
import cz.trailsthroughshadows.api.rest.model.pagination.Pagination;
import cz.trailsthroughshadows.api.rest.model.pagination.RestPaginatedResult;
import cz.trailsthroughshadows.api.rest.model.response.MessageResponse;
import cz.trailsthroughshadows.api.table.market.item.model.Item;
import cz.trailsthroughshadows.api.table.market.item.model.ItemDTO;
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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController(value = "Item")
public class ItemController {

    private ValidationService validation;
    private ItemRepo itemRepo;

    @Operation(
            summary = "Get all items",
            description = """
                    # Get all items
                    This endpoint retrieves all item records with support for advanced query capabilities such as pagination, filtering, sorting, and selective field loading. By default, it employs lazy loading of items.

                    **Parameters**:
                    - `page` - Specifies the page number, starting from 0.
                    - `limit` - Number of items per page, default is 100.
                    - `filter` - Defines the conditions for filtering the items. Supported operations include eq, of, is, gt, gte, lt, lte, has, and bwn.
                    - `sort` - Defines the order of the results. Format example: &sort=name:asc,value:desc.
                    - `include` - Specifies which fields to load; if empty, all fields are considered.
                    - `lazy` - Determines if only specified fields should be loaded (true) or all fields (false).

                    These parameters allow for detailed customization of the returned data, accommodating various user needs for data retrieval and display.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All items retrieved successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = RestPaginatedResult.class))}),
            @ApiResponse(responseCode = "default", description = "Unexpected error",
                    content = @Content)
    })
    @GetMapping("/items")
    @Cacheable(value = "item", key = "T(java.util.Objects).hash(#page, #limit, #filter, #sort, #include, #lazy)")
    public ResponseEntity<RestPaginatedResult<Item>> findAllItems(
            @Parameter(description = "Page number, starts from 0. Helps in paginating the result set.", required = false)
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page. Determines the size of each page of results.", required = false)
            @RequestParam(defaultValue = "100") int limit,
            @Parameter(description = "Filter conditions in the format: &filter=type:eq:Weapon,rarity:has:Rare,... Supported operations include: eq, of, is, gt, gte, lt, lte, has, bwn (between, numbers are split by _).", required = false)
            @RequestParam(defaultValue = "") String filter,
            @Parameter(description = "Sorting parameters in the format: &sort=type:asc,value:desc,... Controls the order in which items are returned.", required = false)
            @RequestParam(defaultValue = "") String sort,
            @Parameter(description = "Specifies the fields to be loaded, which is case sensitive. If left empty, all fields are loaded.", required = false)
            @RequestParam(required = false, defaultValue = "") List<String> include,
            @Parameter(description = "Controls the loading of fields: **true** loads only specified fields in 'include', **false** loads all fields.", required = false)
            @RequestParam(required = false, defaultValue = "true") boolean lazy
    ) {
        // TODO: Re-Implement filtering, sorting and pagination @rcMarty
        // Issue: https://github.com/Trails-Through-Shadows/TTS-API/issues/31

        List<ItemDTO> entries = itemRepo.findAll().stream()
                .filter((entry) -> Filtering.match(entry, List.of(filter.split(","))))
                .sorted((a, b) -> Sorting.compareTo(a, b, List.of(sort.split(","))))
                .toList();

        List<ItemDTO> entriesPage = entries.stream()
                .skip((long) Math.max(page, 0) * limit)
                .limit(limit)
                .toList();

        if (lazy && !include.isEmpty()) {
            entriesPage.forEach(e -> Initialization.hibernateInitializeAll(e, include));
        } else if (!lazy) {
            entriesPage.forEach(Initialization::hibernateInitializeAll);
        }

        Pagination pagination = new Pagination(entriesPage.size(), (entries.size() > (Math.max(page, 0) + 1) * limit), entries.size(), page, limit);
        return new ResponseEntity<>(RestPaginatedResult.of(pagination, entriesPage.stream().map(Item::fromDTO).toList()), HttpStatus.OK);
    }

    @Operation(
            summary = "Get Item by ID",
            description = """
                    # Get Item by ID
                    Retrieves detailed information about a specific item using its unique identifier. This endpoint supports selective field loading through optional parameters, allowing for optimized data retrieval tailored to specific needs.

                    **Parameters**:
                    - `id` - The unique identifier of the item to be retrieved. This is required and cannot be empty.
                    - `include` - Optional. Specifies the case-sensitive fields to be loaded. If left empty, all fields are loaded.
                    - `lazy` - Optional. Controls the loading of fields: if set to **true**, only fields specified in 'include' are loaded; if **false** or omitted, all fields are loaded.

                    This method is designed to efficiently retrieve detailed data on individual items, providing flexibility in data retrieval and reducing overhead for systems and applications.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item successfully retrieved",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Item.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid ID supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Item not found",
                    content = @Content),
            @ApiResponse(responseCode = "default", description = "Unexpected error",
                    content = @Content)
    })
    @GetMapping("/items/{id}")
    @Cacheable(value = "item", key = "T(java.util.Objects).hash(#id, #include, #lazy)")
    public ResponseEntity<Item> findById(
            @Parameter(description = "The unique identifier of the item to be retrieved. Cannot be empty.", required = true)
            @PathVariable int id,
            @Parameter(description = "Specifies the case-sensitive fields to be loaded. Leave empty to load all fields.", required = false)
            @RequestParam(required = false, defaultValue = "") List<String> include,
            @Parameter(description = "Controls the loading of fields: **false** - All fields are loaded; **true** - Only specified fields in 'include' are loaded.", required = false)
            @RequestParam(required = false, defaultValue = "false") boolean lazy
    ) {
        ItemDTO entity = itemRepo
                .findById(id)
                .orElseThrow(() -> RestException.of(HttpStatus.NOT_FOUND, "Item with id '{}' not found! " + id));

        if (lazy && !include.isEmpty()) {
            Initialization.hibernateInitializeAll(entity, include);
        } else if (!lazy) {
            Initialization.hibernateInitializeAll(entity);
        }

        return new ResponseEntity<>(Item.fromDTO(entity), HttpStatus.OK);
    }

    @Operation(
            summary = "Create multiple items",
            description = """
                    # Create multiple items
                    This endpoint allows for the batch creation of multiple items at once. Clients must provide a list of item details in the request body.

                    **Parameters**:
                    - `entities` - List of item details; each entry must conform to the ItemDTO specification for successful creation.

                    This method is particularly useful for populating databases with item data or conducting bulk imports, offering an efficient way to manage multiple item records simultaneously.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All items created successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = MessageResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid data in request body",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Not authorized to perform this operation",
                    content = @Content),
            @ApiResponse(responseCode = "default", description = "Unexpected error",
                    content = @Content)
    })
    @PostMapping("/items")
    @CacheEvict(value = "item", allEntries = true)
    public ResponseEntity<MessageResponse> createEntity(
            @Parameter(description = "List of item data to be created. Each entry must conform to the ItemDTO structure and include all necessary details as required by the system.", required = true)
            @RequestBody List<ItemDTO> enTITIES
    ) {
        enTITIES.forEach(validation::validate);
        List<ItemDTO> saved = itemRepo.saveAll(enTITIES);
        String ids = saved.stream().map(ItemDTO::getId).map(String::valueOf).toList().toString();
        return new ResponseEntity<>(MessageResponse.of(HttpStatus.OK, "Actions with ids '%d' created!", ids),
                HttpStatus.OK);
    }

    @Operation(
            summary = "Update an existing item",
            description = """
                    # Update an existing item
                    Updates an item using its unique identifier with the provided item details. This operation requires:
                    - `id` - The unique identifier of the item to be updated. It must be provided as a path variable.
                    - `item` - The updated details of the item, provided within the request body.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item successfully updated",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = MessageResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid input or bad request",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Not authorized to perform this operation",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Item not found",
                    content = @Content),
            @ApiResponse(responseCode = "default", description = "Unexpected error",
                    content = @Content)
    })
    @PutMapping("/items/{id}")
    @CacheEvict(value = "item", allEntries = true)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<MessageResponse> update(
            @Parameter(description = "The unique identifier of the item to be updated. Cannot be empty.", required = true)
            @PathVariable int id,
            @Parameter(description = "The item data to be used for the update. Cannot be null or empty.", required = true)
            @RequestBody ItemDTO item
    ) {
        validation.validate(item);

        ItemDTO entityToUPdate = itemRepo
                .findById(id)
                .orElseThrow(() -> RestException.of(HttpStatus.NOT_FOUND, "Item with id '{}' not found!", id));

        entityToUPdate.setAction(item.getAction());
        entityToUPdate.setTitle(item.getTitle());
        entityToUPdate.setDescription(item.getDescription());
        entityToUPdate.setTag(item.getTag());
        entityToUPdate.setRequirements(item.getRequirements());
        entityToUPdate.setEffects(item.getEffects());
        entityToUPdate.setType(item.getType());

        itemRepo.save(entityToUPdate);

        return new ResponseEntity<>(MessageResponse.of(HttpStatus.OK, "Item with id '{}' updated!", id), HttpStatus.OK);
    }

    @Operation(
            summary = "Delete an item",
            description = """
                    # Delete an item
                    This endpoint facilitates the deletion of an item identified by its unique identifier. It checks if the item exists within the system and then deletes it, thereby permanently removing it from the database.

                    **Parameters**:
                    - `id` - The unique identifier of the item to be deleted. This ID must be provided in the path to ensure the correct item is targeted for deletion.

                    The deletion operation includes an authorization check to ensure that only users with the appropriate permissions can perform this action.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item deleted successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = MessageResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid ID or request parameters",
                    content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized - User lacks necessary permissions to delete the item",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Item not found",
                    content = @Content),
            @ApiResponse(responseCode = "default", description = "Unexpected error occurred",
                    content = @Content)
    })
    @DeleteMapping("/items/{id}")
    @CacheEvict(value = "item", allEntries = true)
    public ResponseEntity<MessageResponse> delete(
            @Parameter(description = "The unique identifier of the item to be deleted. Cannot be empty.", required = true)
            @PathVariable int id) {
        ItemDTO entityToDelete = itemRepo
                .findById(id)
                .orElseThrow(() -> RestException.of(HttpStatus.NOT_FOUND, "Item with id '{}' not found!", id));

        itemRepo.delete(entityToDelete);

        return new ResponseEntity<>(MessageResponse.of(HttpStatus.OK, "Item with id '{}' deleted!", id), HttpStatus.OK);
    }

    @Autowired
    public void setRepository(ItemRepo repository) {
        this.itemRepo = repository;
    }

    @Autowired
    public void setValidation(ValidationService validation) {
        this.validation = validation;
    }
}
