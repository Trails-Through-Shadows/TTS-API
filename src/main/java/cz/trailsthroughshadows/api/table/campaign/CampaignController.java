package cz.trailsthroughshadows.api.table.campaign;

import com.google.gson.JsonObject;
import cz.trailsthroughshadows.api.rest.exception.RestException;
import cz.trailsthroughshadows.api.rest.model.pagination.Pagination;
import cz.trailsthroughshadows.api.rest.model.pagination.RestPaginatedResult;
import cz.trailsthroughshadows.api.rest.model.response.MessageResponse;
import cz.trailsthroughshadows.api.table.campaign.model.Campaign;
import cz.trailsthroughshadows.api.table.campaign.model.CampaignDTO;
import cz.trailsthroughshadows.api.util.reflect.Initialization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
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
        Collection<CampaignDTO> entities = campaignRepo.findAll();

        if (!lazy && !include.isEmpty()) {
            entities.forEach(e -> Initialization.hibernateInitializeAll(e, include));
        } else if (!lazy) {
            entities.forEach(Initialization::hibernateInitializeAll);
        }

        Pagination pagination = new Pagination(entities.size(), false, entities.size(), page, limit);
        return new ResponseEntity<>(RestPaginatedResult.of(pagination, entities), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public CampaignDTO findById(
            @PathVariable int id,
            @RequestParam(required = false, defaultValue = "") List<String> include,
            @RequestParam(required = false, defaultValue = "false") boolean lazy
    ) {
        CampaignDTO entity = campaignRepo
                .findById(id)
                .orElseThrow(() -> RestException.of(HttpStatus.NOT_FOUND, "Action with id '%d' not found! " + id));

        if (!lazy)
            Initialization.hibernateInitializeAll(entity);
        else
            Initialization.hibernateInitializeAll(entity, include);

        return entity;

    }

    @PostMapping("")
    @CacheEvict(value = "enemy", allEntries = true)
    public ResponseEntity<MessageResponse> create(@RequestBody List<CampaignDTO> campaigns) {

        //TODO validation of the input

        campaigns.forEach(e -> e.setId(null));


        return new ResponseEntity<>(MessageResponse.of(HttpStatus.OK, "Campaigns created successfully!"), HttpStatus.OK);
    }

    @GetMapping("/{id}/tree")
    public ResponseEntity<JsonObject> getTree(
            @PathVariable int id,
            @RequestParam(required = false, defaultValue = "") List<String> include,
            @RequestParam(required = false, defaultValue = "false") boolean lazy
    ) {
        Campaign campaign = Campaign.fromDTO(campaignRepo
                .findById(id)
                .orElseThrow(() -> RestException.of(HttpStatus.NOT_FOUND, "Campaign with id '%d' not found! " + id)));

        return new ResponseEntity<>(campaign.getTree(), HttpStatus.OK);
    }

}
