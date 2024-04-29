package cz.trailsthroughshadows.api.table.campaign;

import cz.trailsthroughshadows.api.rest.exception.RestException;
import cz.trailsthroughshadows.api.rest.model.pagination.Pagination;
import cz.trailsthroughshadows.api.rest.model.pagination.RestPaginatedResult;
import cz.trailsthroughshadows.api.rest.model.response.MessageResponse;
import cz.trailsthroughshadows.api.table.campaign.model.Campaign;
import cz.trailsthroughshadows.api.table.campaign.model.CampaignDTO;
import cz.trailsthroughshadows.api.table.campaign.model.CampaignLocation;
import cz.trailsthroughshadows.api.util.reflect.Filtering;
import cz.trailsthroughshadows.api.util.reflect.Initialization;
import cz.trailsthroughshadows.api.util.reflect.Sorting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/campaigns")
public class CampaignController {
    @Autowired
    private CampaignRepo campaignRepo;

    @GetMapping("")
    public ResponseEntity<RestPaginatedResult<CampaignDTO>> findAllEntities(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int limit,
            @RequestParam(defaultValue = "") String filter,
            @RequestParam(defaultValue = "") String sort,
            @RequestParam(required = false, defaultValue = "") List<String> include,
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

    @GetMapping("/{id}")
    public CampaignDTO findById(
            @PathVariable int id,
            @RequestParam(required = false, defaultValue = "") List<String> include,
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

    @GetMapping("/{id}/location/{idLocation}")
    public CampaignLocation findById2(
            @PathVariable int id,
            @PathVariable int idLocation,
            @RequestParam(required = false, defaultValue = "") List<String> include,
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

    @PostMapping("")
    //@CacheEvict(value = "campaign", allEntries = true)
    public ResponseEntity<MessageResponse> create(@RequestBody List<CampaignDTO> campaigns) {

        //TODO validation of the input

        campaigns.forEach(e -> e.setId(null));


        return new ResponseEntity<>(MessageResponse.of(HttpStatus.OK, "Campaigns created successfully!"), HttpStatus.OK);
    }

    @GetMapping(value = "/{id}/tree", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getTree(@PathVariable int id) {
        Campaign campaign = Campaign.fromDTO(campaignRepo
                .findById(id)
                .orElseThrow(() -> RestException.of(HttpStatus.NOT_FOUND, "Campaign with id '{}' not found!", id)));

        return new ResponseEntity<>(campaign.getTree(), HttpStatus.OK);
    }

}
