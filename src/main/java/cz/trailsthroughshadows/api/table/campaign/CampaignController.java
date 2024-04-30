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

    @GetMapping("")
    @Cacheable(value = "campaign", key="T(java.util.Objects).hash(#page, #limit, #filter, #sort, #include, #lazy)")
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
    @Cacheable(value = "campaign", key="T(java.util.Objects).hash(#id, #include, #lazy)")
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
    @Cacheable(value = "campaign", key="T(java.util.Objects).hash(#id, #idLocation, #include, #lazy)")
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

    @Transactional
    @PutMapping("/{id}")
    @CacheEvict(value = "campaign", allEntries = true)
    public ResponseEntity<MessageResponse> update(@PathVariable int id, @RequestBody CampaignDTO campaign) {
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

                StringBuilder sb = new StringBuilder();
                sb.append("[");
                sb.append(cl.getConditions().stream().map(Object::toString).reduce((a, b) -> a + ", " + b).orElse(""));
                sb.append("]");
                cl.setConditionString(sb.toString());

                campaignToUpdate.getLocations().clear();
                campaignToUpdate.getLocations().addAll(locations);
            }
        }

        if (!achievements.isEmpty() || !locations.isEmpty()) {
            campaignToUpdate = campaignRepo.save(campaignToUpdate);
        }

        return new ResponseEntity<>(MessageResponse.of(HttpStatus.OK, "Campaign with id '{}' updated!", id), HttpStatus.OK);
    }

    @Transactional
    @DeleteMapping("/{id}")
    @CacheEvict(value = "campaign", allEntries = true)
    public ResponseEntity<MessageResponse> delete(@PathVariable int id) {
        CampaignDTO campaign = campaignRepo
                .findById(id)
                .orElseThrow(() -> RestException.of(HttpStatus.NOT_FOUND, "Campaign with id '{}' not found!", id));

        campaignRepo.delete(campaign);
        return new ResponseEntity<>(MessageResponse.of(HttpStatus.OK, "Campaign deleted successfully!"), HttpStatus.OK);
    }


    @Transactional
    @PostMapping("")
    @CacheEvict(value = "campaign", allEntries = true)
    public ResponseEntity<MessageResponse> create(@RequestBody List<CampaignDTO> campaigns) {
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

    @Cacheable(value = "campaignTree", key = "#id")
    @GetMapping(value = "/{id}/tree", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getTree(@PathVariable int id) {
        Campaign campaign = Campaign.fromDTO(campaignRepo
                .findById(id)
                .orElseThrow(() -> RestException.of(HttpStatus.NOT_FOUND, "Campaign with id '{}' not found!", id)));

        return new ResponseEntity<>(campaign.getTree(), HttpStatus.OK);
    }

}
