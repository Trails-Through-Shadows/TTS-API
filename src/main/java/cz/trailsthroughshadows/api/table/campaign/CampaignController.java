package cz.trailsthroughshadows.api.table.campaign;

import cz.trailsthroughshadows.algorithm.validation.ValidationService;
import cz.trailsthroughshadows.api.rest.exception.RestException;
import cz.trailsthroughshadows.api.rest.model.pagination.Pagination;
import cz.trailsthroughshadows.api.rest.model.pagination.RestPaginatedResult;
import cz.trailsthroughshadows.api.rest.model.response.MessageResponse;
import cz.trailsthroughshadows.api.table.campaign.model.*;
import cz.trailsthroughshadows.api.table.schematic.location.model.dto.LocationPathDTO;
import cz.trailsthroughshadows.api.util.reflect.Filtering;
import cz.trailsthroughshadows.api.util.reflect.Initialization;
import cz.trailsthroughshadows.api.util.reflect.Sorting;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
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
    private ValidationService validation;

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

    @PutMapping("/{id}")
    @Transactional(rollbackOn = Exception.class)
    @CacheEvict(value = "campaign", allEntries = true)
    public ResponseEntity<MessageResponse> update(@PathVariable int id, @RequestBody CampaignDTO campaign) {
        CampaignDTO campaignToUpdate = campaignRepo
                .findById(id)
                .orElseThrow(() -> RestException.of(HttpStatus.NOT_FOUND, "Campaign with id '{}' not found!", id));

        validation.validate(campaign);

        campaignToUpdate.setTitle(campaign.getTitle());
        campaignToUpdate.setDescription(campaign.getDescription());
        campaignToUpdate.setAchievements(campaign.getAchievements());
        campaignToUpdate.setLocations(campaign.getLocations());

        return new ResponseEntity<>(MessageResponse.of(HttpStatus.OK, "Campaign updated successfully!"), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Transactional(rollbackOn = Exception.class)
    @CacheEvict(value = "campaign", allEntries = true)
    public ResponseEntity<MessageResponse> delete(@PathVariable int id) {
        CampaignDTO campaign = campaignRepo
                .findById(id)
                .orElseThrow(() -> RestException.of(HttpStatus.NOT_FOUND, "Campaign with id '{}' not found!", id));

        campaignRepo.delete(campaign);
        return new ResponseEntity<>(MessageResponse.of(HttpStatus.OK, "Campaign deleted successfully!"), HttpStatus.OK);
    }


    @PostMapping("")
    //@CacheEvict(value = "campaign", allEntries = true)
    public ResponseEntity<MessageResponse> create(@RequestBody List<CampaignDTO> campaigns) {
        log.info("Creating campaigns: {}", campaigns);

        // validate all and set ids to null
        campaigns.forEach(validation::validate);
        campaigns.forEach(e -> e.setId(null));

        // save relations
        Map<String, List<CampaignAchievements>> achievementsRelations = new HashMap<>();
        Map<String, List<CampaignLocation>> locationsRelations = new HashMap<>();
        Map<String, List<LocationPathDTO>> pathsRelations = new HashMap<>();

        // remove relations
        campaigns.forEach(campaign -> {
            campaign.setAchievements(new ArrayList<>());
            campaign.setLocations(new ArrayList<>());

            // todo zoze glhf

            campaignRepo.save(campaign);
        });

        String ids = campaigns.stream().map(CampaignDTO::getId).map(String::valueOf).reduce((a, b) -> a + ", " + b).orElse("");

        return new ResponseEntity<>(MessageResponse.of(HttpStatus.OK, "Campaigns created: " + ids), HttpStatus.OK);
    }

    @GetMapping(value = "/{id}/tree", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getTree(@PathVariable int id) {
        Campaign campaign = Campaign.fromDTO(campaignRepo
                .findById(id)
                .orElseThrow(() -> RestException.of(HttpStatus.NOT_FOUND, "Campaign with id '{}' not found!", id)));

        return new ResponseEntity<>(campaign.getTree(), HttpStatus.OK);
    }

}
